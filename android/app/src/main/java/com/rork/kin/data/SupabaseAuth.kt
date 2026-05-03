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
 * Real Supabase Auth (GoTrue) client.
 *
 * Why we need it:
 *   The previous "open invite_code" model meant anyone with a leaked code
 *   had full read/write access to a family's photos forever. With auth:
 *     - Each user has a unique uid backed by an email + password.
 *     - RLS policies pin every row to `auth.uid()` and a `family_members` table.
 *     - Storage uploads / downloads use signed URLs, not public links.
 *     - The session JWT lives in EncryptedSharedPreferences, refreshed on demand.
 *
 * Tokens are never logged, never written to plaintext disk, never sent
 * over cleartext (network security config enforces TLS-only).
 */
object SupabaseAuth {

    private const val TAG = "SupabaseAuth"

    private val baseUrl: String get() = SupabaseConfig.SUPABASE_URL.trimEnd('/')
    private val anonKey: String get() = SupabaseConfig.SUPABASE_ANON_KEY

    val isConfigured: Boolean
        get() = SupabaseConfig.SUPABASE_URL.isNotBlank() &&
                SupabaseConfig.SUPABASE_ANON_KEY.isNotBlank()

    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = false }

    private val client by lazy {
        HttpClient(Android) {
            install(ContentNegotiation) { json(this@SupabaseAuth.json) }
        }
    }

    @Volatile var session: Session? = null
        private set

    @Serializable
    data class Session(
        @SerialName("access_token") val accessToken: String,
        @SerialName("refresh_token") val refreshToken: String,
        @SerialName("user") val user: AuthUser,
        @SerialName("expires_at") val expiresAt: Long? = null,
    )

    @Serializable
    data class AuthUser(
        val id: String,
        val email: String? = null,
    )

    @Serializable
    private data class CredsBody(val email: String, val password: String)

    @Serializable
    private data class RefreshBody(@SerialName("refresh_token") val refreshToken: String)

    sealed interface AuthResult {
        data class Ok(val session: Session) : AuthResult
        data class Error(val message: String) : AuthResult
    }

    /** Restore the last session from EncryptedSharedPreferences. */
    suspend fun restore(ctx: Context): Session? = withContext(Dispatchers.IO) {
        val access = SecureStore.get(ctx, SecureStore.KEY_ACCESS_TOKEN) ?: return@withContext null
        val refresh = SecureStore.get(ctx, SecureStore.KEY_REFRESH_TOKEN) ?: return@withContext null
        val uid = SecureStore.get(ctx, SecureStore.KEY_USER_ID) ?: return@withContext null
        val email = SecureStore.get(ctx, SecureStore.KEY_USER_EMAIL)
        val s = Session(access, refresh, AuthUser(uid, email))
        session = s
        s
    }

    suspend fun signUp(ctx: Context, email: String, password: String): AuthResult =
        post(ctx, "/auth/v1/signup", email, password)

    suspend fun signIn(ctx: Context, email: String, password: String): AuthResult =
        post(ctx, "/auth/v1/token?grant_type=password", email, password)

    private suspend fun post(
        ctx: Context,
        path: String,
        email: String,
        password: String,
    ): AuthResult = withContext(Dispatchers.IO) {
        if (!isConfigured) return@withContext AuthResult.Error("Auth not configured")
        runCatching {
            val res = client.post("$baseUrl$path") {
                headers {
                    append("apikey", anonKey)
                }
                contentType(ContentType.Application.Json)
                setBody(CredsBody(email, password))
            }
            if (!res.status.isSuccess()) {
                return@runCatching AuthResult.Error("Sign-in failed (${res.status.value})")
            }
            val s = json.decodeFromString<Session>(res.bodyAsText())
            persist(ctx, s)
            session = s
            AuthResult.Ok(s)
        }.onFailure { Log.w(TAG, "auth post error", it) }
            .getOrElse { AuthResult.Error("Network error") }
    }

    /** Exchange the refresh token for a fresh access token. Returns null on failure. */
    suspend fun refresh(ctx: Context): Session? = withContext(Dispatchers.IO) {
        if (!isConfigured) return@withContext null
        val current = session ?: restore(ctx) ?: return@withContext null
        runCatching {
            val res = client.post("$baseUrl/auth/v1/token?grant_type=refresh_token") {
                headers { append("apikey", anonKey) }
                contentType(ContentType.Application.Json)
                setBody(RefreshBody(current.refreshToken))
            }
            if (!res.status.isSuccess()) return@runCatching null
            val s = json.decodeFromString<Session>(res.bodyAsText())
            persist(ctx, s)
            session = s
            s
        }.getOrNull()
    }

    suspend fun signOut(ctx: Context) {
        val token = session?.accessToken
        session = null
        SecureStore.put(ctx, SecureStore.KEY_ACCESS_TOKEN, null)
        SecureStore.put(ctx, SecureStore.KEY_REFRESH_TOKEN, null)
        SecureStore.put(ctx, SecureStore.KEY_USER_ID, null)
        SecureStore.put(ctx, SecureStore.KEY_USER_EMAIL, null)
        if (!isConfigured || token.isNullOrBlank()) return
        runCatching {
            client.post("$baseUrl/auth/v1/logout") {
                headers {
                    append("apikey", anonKey)
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
            }
        }
    }

    private fun persist(ctx: Context, s: Session) {
        SecureStore.put(ctx, SecureStore.KEY_ACCESS_TOKEN, s.accessToken)
        SecureStore.put(ctx, SecureStore.KEY_REFRESH_TOKEN, s.refreshToken)
        SecureStore.put(ctx, SecureStore.KEY_USER_ID, s.user.id)
        SecureStore.put(ctx, SecureStore.KEY_USER_EMAIL, s.user.email)
    }

    /** Bearer token to use for PostgREST/Storage requests, falling back to anon. */
    fun bearerToken(): String = session?.accessToken ?: anonKey
}
