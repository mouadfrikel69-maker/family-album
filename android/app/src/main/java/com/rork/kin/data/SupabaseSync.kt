package com.rork.kin.data

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.delete
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
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

/**
 * Lightweight Supabase REST client for Kin.
 *
 * No auth — every device joining a family with the same invite_code can read/write that
 * family's photos. RLS is left "open" via permissive policies (see SUPABASE_SETUP.md).
 *
 * Tables:
 *   family_photos(id, invite_code, local_id, author_name, author_initials, author_color,
 *                 caption, image_url, taken_on, created_at)
 *
 * Storage:
 *   bucket "kin-photos" (public). Object path: {invite_code}/{uuid}.jpg
 */
object SupabaseSync {

    private const val TAG = "SupabaseSync"
    private const val BUCKET = "kin-photos"
    private const val TABLE = "family_photos"

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

    /** Upload a local file to Supabase Storage. Returns the public URL or null on failure. */
    suspend fun uploadFile(inviteCode: String, file: File): String? = withContext(Dispatchers.IO) {
        if (!isConfigured) return@withContext null
        runCatching {
            val key = "${sanitize(inviteCode)}/${UUID.randomUUID()}.jpg"
            val res = client.post("$baseUrl/storage/v1/object/$BUCKET/$key") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $anonKey")
                    append("apikey", anonKey)
                    append("x-upsert", "true")
                }
                contentType(ContentType.Image.JPEG)
                setBody(file.readBytes())
            }
            if (!res.status.isSuccess()) {
                Log.w(TAG, "uploadFile failed: ${res.status} ${res.bodyAsText()}")
                return@runCatching null
            }
            "$baseUrl/storage/v1/object/public/$BUCKET/$key"
        }.onFailure { Log.w(TAG, "uploadFile error", it) }.getOrNull()
    }

    /** Insert a row in family_photos. Returns inserted row id or null. */
    suspend fun insertPhoto(row: RemotePhoto): String? = withContext(Dispatchers.IO) {
        if (!isConfigured) return@withContext null
        runCatching {
            val res = client.post("$baseUrl/rest/v1/$TABLE") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $anonKey")
                    append("apikey", anonKey)
                    append("Prefer", "return=representation")
                }
                contentType(ContentType.Application.Json)
                setBody(listOf(row))
            }
            if (!res.status.isSuccess()) {
                Log.w(TAG, "insertPhoto failed: ${res.status} ${res.bodyAsText()}")
                return@runCatching null
            }
            val arr = json.decodeFromString<List<RemotePhoto>>(res.bodyAsText())
            arr.firstOrNull()?.id
        }.onFailure { Log.w(TAG, "insertPhoto error", it) }.getOrNull()
    }

    /** List all photos for a family. */
    suspend fun listPhotos(inviteCode: String): List<RemotePhoto> = withContext(Dispatchers.IO) {
        if (!isConfigured) return@withContext emptyList()
        runCatching {
            val res = client.get("$baseUrl/rest/v1/$TABLE") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $anonKey")
                    append("apikey", anonKey)
                }
                parameter("invite_code", "eq.${sanitize(inviteCode)}")
                parameter("select", "*")
                parameter("order", "created_at.desc")
            }
            if (!res.status.isSuccess()) {
                Log.w(TAG, "listPhotos failed: ${res.status} ${res.bodyAsText()}")
                return@runCatching emptyList<RemotePhoto>()
            }
            json.decodeFromString<List<RemotePhoto>>(res.bodyAsText())
        }.onFailure { Log.w(TAG, "listPhotos error", it) }.getOrDefault(emptyList())
    }

    /** Convert a remote row into the in-memory Photo model. */
    fun toPhoto(r: RemotePhoto, currentUserId: String, fallbackId: String): Photo {
        val createdAt = runCatching {
            // Supabase returns ISO with offset; parse loosely
            LocalDateTime.parse(r.created_at?.substringBefore('+')?.substringBefore('Z') ?: "")
        }.getOrElse { LocalDateTime.now() }
        val takenOn = runCatching { LocalDate.parse(r.taken_on) }.getOrElse { LocalDate.now() }
        return Photo(
            id = r.id ?: fallbackId,
            url = r.image_url,
            authorId = currentUserId, // we don't have stable remote authors without auth
            caption = r.caption,
            createdAt = createdAt,
            takenOn = takenOn,
        )
    }

    private fun sanitize(s: String): String =
        s.trim().replace(Regex("[^A-Za-z0-9_-]"), "-").lowercase()
}
