package com.rork.kin.data

import android.content.Context
import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.headers
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

/**
 * Supabase family-management client.
 *
 * Two operations talk to the cloud:
 *  - [createFamily]  — INSERT a row into `families`, returning the generated UUID + invite_code.
 *  - [joinFamily]    — invoke the `join_family(code, name, rel)` security-definer RPC.
 *
 * Both are no-ops if Supabase is not configured (local-only mode), in which case
 * the caller falls back to a synthetic local id and the user only ever sees their
 * own device.
 *
 * Errors are surfaced as a typed [Result] so the UI can show them inline.
 */
object SupabaseFamily {

    private const val TAG = "SupabaseFamily"

    private val baseUrl: String get() = SupabaseConfig.SUPABASE_URL.trimEnd('/')
    private val anonKey: String get() = SupabaseConfig.SUPABASE_ANON_KEY

    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = false }

    private val client by lazy {
        HttpClient(Android) {
            install(ContentNegotiation) { json(this@SupabaseFamily.json) }
        }
    }

    sealed interface Result<out T> {
        data class Ok<T>(val value: T) : Result<T>
        data class Error(val message: String) : Result<Nothing>
    }

    @Serializable
    data class CreatedFamily(
        val id: String,
        val name: String,
        @SerialName("invite_code") val inviteCode: String,
    )

    @Serializable
    private data class CreateFamilyBody(
        val name: String,
        @SerialName("invite_code") val inviteCode: String,
        @SerialName("created_by") val createdBy: String,
    )

    @Serializable
    private data class JoinFamilyBody(
        val code: String,
        val name: String,
        val rel: String,
    )

    /**
     * Insert a new row into `families`. The active session must belong to the
     * caller (the RLS `families.create` policy enforces `auth.uid() = created_by`).
     */
    suspend fun createFamily(
        ctx: Context,
        name: String,
        inviteCode: String,
    ): Result<CreatedFamily> = withContext(Dispatchers.IO) {
        val session = SupabaseAuth.session
            ?: SupabaseAuth.restore(ctx)
            ?: return@withContext Result.Error("Not signed in")
        if (SupabaseConfig.SUPABASE_URL.isBlank()) {
            return@withContext Result.Error("Supabase not configured")
        }
        runCatching {
            val res = client.post("$baseUrl/rest/v1/families") {
                headers {
                    append("apikey", anonKey)
                    append(HttpHeaders.Authorization, "Bearer ${session.accessToken}")
                    append("Prefer", "return=representation")
                }
                contentType(ContentType.Application.Json)
                setBody(
                    CreateFamilyBody(
                        name = name,
                        inviteCode = inviteCode,
                        createdBy = session.user.id,
                    ),
                )
            }
            if (!res.status.isSuccess()) {
                return@runCatching Result.Error(
                    "Could not create family (${res.status.value})",
                )
            }
            val rows: List<CreatedFamily> = json.decodeFromString(res.bodyAsText())
            val first = rows.firstOrNull()
                ?: return@runCatching Result.Error("Empty response from Supabase")
            Result.Ok(first)
        }.onFailure { Log.w(TAG, "createFamily error", it) }
            .getOrElse { Result.Error("Network error: ${it::class.simpleName}") }
    }

    /**
     * Join an existing family by invite code. The server-side `join_family`
     * RPC enforces auth and writes the membership row in a single atomic step,
     * which is why we route joining through it instead of a direct INSERT.
     */
    suspend fun joinFamily(
        ctx: Context,
        code: String,
        displayName: String,
        relationship: String,
    ): Result<String> = withContext(Dispatchers.IO) {
        val session = SupabaseAuth.session
            ?: SupabaseAuth.restore(ctx)
            ?: return@withContext Result.Error("Not signed in")
        if (SupabaseConfig.SUPABASE_URL.isBlank()) {
            return@withContext Result.Error("Supabase not configured")
        }
        runCatching {
            val res = client.post("$baseUrl/rest/v1/rpc/join_family") {
                headers {
                    append("apikey", anonKey)
                    append(HttpHeaders.Authorization, "Bearer ${session.accessToken}")
                }
                contentType(ContentType.Application.Json)
                setBody(JoinFamilyBody(code = code, name = displayName, rel = relationship))
            }
            if (!res.status.isSuccess()) {
                val body = res.bodyAsText()
                // PostgREST returns the raised exception message for security-definer
                // RPCs — surface "invalid invite code" verbatim when the server says so.
                val msg = when {
                    "invalid invite code" in body -> "That invite code doesn't match a family"
                    "not authenticated" in body -> "Sign in first"
                    else -> "Could not join family (${res.status.value})"
                }
                return@runCatching Result.Error(msg)
            }
            // The RPC returns a JSON-quoted UUID string, e.g. `"f1c2…"`.
            val raw = res.bodyAsText().trim().trim('"')
            if (raw.isBlank()) {
                Result.Error("Empty response from Supabase")
            } else {
                Result.Ok(raw)
            }
        }.onFailure { Log.w(TAG, "joinFamily error", it) }
            .getOrElse { Result.Error("Network error: ${it::class.simpleName}") }
    }
}
