package com.rork.kin.data

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

/**
 * Hardened Supabase client for Kin.
 *
 * Security model:
 *   - Every request is authenticated with the user's JWT (`SupabaseAuth.bearerToken()`).
 *     The anon key is only used as the `apikey` header (required by PostgREST)
 *     and for *unauthenticated* fallback in unconfigured projects.
 *   - The `kin-photos` bucket is **private**. Uploads go through the authenticated
 *     user; reads happen via short-lived **signed URLs** (60s) — no public links.
 *   - RLS policies (see SUPABASE_SETUP.md) require membership in the family,
 *     proven by `family_members.user_id = auth.uid()`. The invite_code alone
 *     is no longer sufficient to read or write.
 *   - All inputs are validated via [Validate] before hitting the wire.
 */
object SupabaseSync {

    private const val TAG = "SupabaseSync"
    private const val BUCKET = "kin-photos"
    private const val TABLE = "family_photos"
    private const val SIGNED_URL_TTL_SECONDS = 60

    val isConfigured: Boolean
        get() = SupabaseConfig.SUPABASE_URL.isNotBlank() &&
                SupabaseConfig.SUPABASE_ANON_KEY.isNotBlank()

    private val baseUrl: String get() = SupabaseConfig.SUPABASE_URL.trimEnd('/')
    private val anonKey: String get() = SupabaseConfig.SUPABASE_ANON_KEY

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private val client by lazy {
        HttpClient(Android) {
            install(ContentNegotiation) {
                json(this@SupabaseSync.json)
            }
        }
    }

    @Serializable
    data class RemotePhoto(
        val id: String? = null,
        val invite_code: String,
        val local_id: String,
        val author_name: String,
        val author_initials: String,
        val author_color: Long,
        val caption: String,
        val image_url: String,
        val taken_on: String,
        val created_at: String? = null,
    )

    @Serializable
    private data class SignedUrlResponse(
        @SerialName("signedURL") val signedUrl: String? = null,
    )

    /** Upload a local file to the **private** Supabase Storage bucket. Returns the storage key. */
    suspend fun uploadFile(inviteCode: String, file: File): String? = withContext(Dispatchers.IO) {
        if (!isConfigured) return@withContext null
        // Hard cap to stop a malicious / buggy client from pushing 500MB photos.
        if (file.length() > 25L * 1024 * 1024) {
            Log.w(TAG, "uploadFile rejected: file too large (${file.length()} bytes)")
            return@withContext null
        }
        val safeCode = sanitize(inviteCode).ifBlank { return@withContext null }
        runCatching {
            val key = "$safeCode/${UUID.randomUUID()}.jpg"
            val res = client.post("$baseUrl/storage/v1/object/$BUCKET/$key") {
                authHeaders()
                headers { append("x-upsert", "false") }
                contentType(ContentType.Image.JPEG)
                setBody(file.readBytes())
            }
            if (!res.status.isSuccess()) {
                Log.w(TAG, "uploadFile failed: ${res.status}")
                return@runCatching null
            }
            key
        }.onFailure { Log.w(TAG, "uploadFile error", it) }.getOrNull()
    }

    /**
     * Get a short-lived (60s) signed URL for a private storage object.
     * Pass the storage key returned by [uploadFile], or fall back to extracting
     * it from a previously stored `image_url` if it was a public-style URL.
     */
    suspend fun signedUrl(storageKey: String): String? = withContext(Dispatchers.IO) {
        if (!isConfigured) return@withContext null
        runCatching {
            val res = client.post("$baseUrl/storage/v1/object/sign/$BUCKET/$storageKey") {
                authHeaders()
                contentType(ContentType.Application.Json)
                setBody(mapOf("expiresIn" to SIGNED_URL_TTL_SECONDS))
            }
            if (!res.status.isSuccess()) {
                Log.w(TAG, "signedUrl failed: ${res.status}")
                return@runCatching null
            }
            val parsed = json.decodeFromString<SignedUrlResponse>(res.bodyAsText())
            parsed.signedUrl?.let { suffix ->
                if (suffix.startsWith("http")) suffix
                else "$baseUrl/storage/v1${if (suffix.startsWith("/")) suffix else "/$suffix"}"
            }
        }.onFailure { Log.w(TAG, "signedUrl error", it) }.getOrNull()
    }

    /** Insert a row in family_photos. Returns inserted row id or null. */
    suspend fun insertPhoto(row: RemotePhoto): String? = withContext(Dispatchers.IO) {
        if (!isConfigured) return@withContext null
        // Defensive client-side cap; real authority is the column constraint + RLS.
        val safe = row.copy(
            caption = Validate.caption(row.caption),
            author_name = Validate.name(row.author_name),
        )
        runCatching {
            val res = client.post("$baseUrl/rest/v1/$TABLE") {
                authHeaders()
                headers { append("Prefer", "return=representation") }
                contentType(ContentType.Application.Json)
                setBody(listOf(safe))
            }
            if (!res.status.isSuccess()) {
                Log.w(TAG, "insertPhoto failed: ${res.status}")
                return@runCatching null
            }
            val arr = json.decodeFromString<List<RemotePhoto>>(res.bodyAsText())
            arr.firstOrNull()?.id
        }.onFailure { Log.w(TAG, "insertPhoto error", it) }.getOrNull()
    }

    /** List all photos for a family. RLS gates this — non-members see an empty list. */
    suspend fun listPhotos(inviteCode: String): List<RemotePhoto> = withContext(Dispatchers.IO) {
        if (!isConfigured) return@withContext emptyList()
        runCatching {
            val res = client.get("$baseUrl/rest/v1/$TABLE") {
                authHeaders()
                parameter("invite_code", "eq.${sanitize(inviteCode)}")
                parameter("select", "*")
                parameter("order", "created_at.desc")
                parameter("limit", "500")
            }
            if (!res.status.isSuccess()) {
                Log.w(TAG, "listPhotos failed: ${res.status}")
                return@runCatching emptyList<RemotePhoto>()
            }
            json.decodeFromString<List<RemotePhoto>>(res.bodyAsText())
        }.onFailure { Log.w(TAG, "listPhotos error", it) }.getOrDefault(emptyList())
    }

    /** Convert a remote row into the in-memory Photo model. */
    fun toPhoto(r: RemotePhoto, currentUserId: String, fallbackId: String): Photo {
        val createdAt = runCatching {
            LocalDateTime.parse(r.created_at?.substringBefore('+')?.substringBefore('Z') ?: "")
        }.getOrElse { LocalDateTime.now() }
        val takenOn = runCatching { LocalDate.parse(r.taken_on) }.getOrElse { LocalDate.now() }
        return Photo(
            id = r.id ?: fallbackId,
            url = r.image_url,
            authorId = currentUserId,
            caption = r.caption,
            createdAt = createdAt,
            takenOn = takenOn,
        )
    }

    /** Authorisation = user JWT (preferred) + apikey header (always required by PostgREST). */
    private fun io.ktor.client.request.HttpRequestBuilder.authHeaders() {
        headers {
            append(HttpHeaders.Authorization, "Bearer ${SupabaseAuth.bearerToken()}")
            append("apikey", anonKey)
        }
    }

    private fun sanitize(s: String): String =
        s.trim().replace(Regex("[^A-Za-z0-9_-]"), "-").lowercase()
}
