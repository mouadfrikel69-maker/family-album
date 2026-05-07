package com.rork.kin.data

import android.content.Context
import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
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
 * Three operations talk to the cloud:
 *  - [createFamily]  — INSERT a row into `families`, returning the generated UUID + invite_code.
 *  - [joinFamily]    — invoke the `join_family(code, name, rel)` security-definer RPC.
 *  - [fetchMembers]  — SELECT every row of `family_members` for the active family,
 *                     so each device shows the *real* member list instead of a
 *                     locally-fabricated "just me" stub.
 *
 * Every authed call goes through [SupabaseAuth.ensureValidSession] (proactive
 * refresh near expiry) and retries once on 401 (the cached access token may
 * have been revoked or rotated server-side). The previous code path used the
 * cached `session.accessToken` directly which 401'd silently after ~1 h.
 *
 * Operations no-op in local-only mode (Supabase not configured); callers fall
 * back to a synthetic local id and the user only ever sees their own device.
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
            // Without explicit timeouts a single dead socket on a flaky network
            // hangs the calling coroutine indefinitely. 30 s is a comfortable
            // upper bound for any PostgREST round-trip.
            install(HttpTimeout) {
                connectTimeoutMillis = 10_000
                requestTimeoutMillis = 30_000
                socketTimeoutMillis = 30_000
            }
        }
    }

    /**
     * Run [request] against PostgREST with the active session's bearer token,
     * proactively refreshing first if the token is near expiry, and retrying
     * exactly once on a 401 (the server may have revoked / rotated the token
     * since we last refreshed). Returns null if no session is restorable.
     */
    private suspend fun authedRequest(
        ctx: Context,
        request: suspend (token: String) -> HttpResponse,
    ): HttpResponse? {
        val session = SupabaseAuth.ensureValidSession(ctx) ?: return null
        val first = request(session.accessToken)
        if (first.status.value != 401) return first
        // Stale or revoked token — try one refresh + retry. If refresh fails,
        // surface the original 401 so the UI can prompt re-sign-in.
        val refreshed = SupabaseAuth.refresh(ctx) ?: return first
        return request(refreshed.accessToken)
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
     * Wire shape of a `family_members` row as returned by PostgREST. Mapped
     * to the local [Member] model in [fetchMembers] below.
     */
    @Serializable
    private data class MemberRow(
        @SerialName("user_id") val userId: String,
        @SerialName("display_name") val displayName: String = "",
        val role: String = "member",
        val relationship: String = "",
        val initials: String = "",
        @SerialName("avatar_color") val avatarColor: Long = 0L,
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
        if (SupabaseConfig.SUPABASE_URL.isBlank()) {
            return@withContext Result.Error("Supabase not configured")
        }
        val createdBy = SupabaseAuth.session?.user?.id
            ?: SupabaseAuth.restore(ctx)?.user?.id
            ?: return@withContext Result.Error("Not signed in")
        runCatching {
            val res = authedRequest(ctx) { token ->
                client.post("$baseUrl/rest/v1/families") {
                    headers {
                        append("apikey", anonKey)
                        append(HttpHeaders.Authorization, "Bearer $token")
                        append("Prefer", "return=representation")
                    }
                    contentType(ContentType.Application.Json)
                    setBody(
                        CreateFamilyBody(
                            name = name,
                            inviteCode = inviteCode,
                            createdBy = createdBy,
                        ),
                    )
                }
            } ?: return@runCatching Result.Error("Not signed in")
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
        if (SupabaseConfig.SUPABASE_URL.isBlank()) {
            return@withContext Result.Error("Supabase not configured")
        }
        runCatching {
            val res = authedRequest(ctx) { token ->
                client.post("$baseUrl/rest/v1/rpc/join_family") {
                    headers {
                        append("apikey", anonKey)
                        append(HttpHeaders.Authorization, "Bearer $token")
                    }
                    contentType(ContentType.Application.Json)
                    setBody(JoinFamilyBody(code = code, name = displayName, rel = relationship))
                }
            } ?: return@runCatching Result.Error("Not signed in")
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

    /**
     * Fetch every member of the given family. Returns each row already mapped
     * to the local [Member] model. Callers wire the result into
     * `AppState.members` so other devices show up in the UI.
     *
     * The current user is *not* filtered out here — callers decide whether to
     * keep them as the head of the list (which is what `AppViewModel` does so
     * the on-device "You" pill never disappears during a network blip).
     */
    suspend fun fetchMembers(
        ctx: Context,
        familyId: String,
    ): Result<List<Member>> = withContext(Dispatchers.IO) {
        if (SupabaseConfig.SUPABASE_URL.isBlank()) {
            return@withContext Result.Error("Supabase not configured")
        }
        runCatching {
            // PostgREST: GET /rest/v1/family_members?family_id=eq.<id>&select=...
            // RLS guarantees the result is empty unless the caller is a member.
            val select = "user_id,display_name,role,relationship,initials,avatar_color"
            val url = "$baseUrl/rest/v1/family_members?family_id=eq.$familyId&select=$select"
            val res = authedRequest(ctx) { token ->
                client.get(url) {
                    headers {
                        append("apikey", anonKey)
                        append(HttpHeaders.Authorization, "Bearer $token")
                    }
                }
            } ?: return@runCatching Result.Error("Not signed in")
            if (!res.status.isSuccess()) {
                return@runCatching Result.Error(
                    "Could not load family members (${res.status.value})",
                )
            }
            val rows: List<MemberRow> = json.decodeFromString(res.bodyAsText())
            Result.Ok(rows.map { it.toMember() })
        }.onFailure { Log.w(TAG, "fetchMembers error", it) }
            .getOrElse { Result.Error("Network error: ${it::class.simpleName}") }
    }

    private fun MemberRow.toMember(): Member {
        val resolvedName = displayName.ifBlank { "Family member" }
        // The DB defaults `family_members.avatar_color` to 0 and neither the
        // creator-trigger nor the join_family RPC sets it, so every fetched
        // row currently arrives with avatarColor=0L. Compose's Color(0L) is
        // 0x00000000 — fully transparent — which renders an invisible avatar
        // circle plus invisible white initials on the cream background.
        // Fall back to a deterministic palette pick so each member gets a
        // stable, distinct tint until proper colour persistence lands.
        val resolvedColor = if (avatarColor != 0L) {
            avatarColor
        } else {
            // Seed off the raw `displayName` (still possibly blank) so an
            // empty/null name on the server falls through to `userId`,
            // giving each user without a display name a *distinct* tint
            // instead of every blank-name member sharing the same colour.
            AvatarColor.colorForSeed(displayName.ifBlank { userId })
        }
        return Member(
            id = userId,
            name = resolvedName,
            relationship = relationship,
            avatarColor = resolvedColor,
            initials = initials.ifBlank {
                displayName
                    .split(" ")
                    .filter { it.isNotBlank() }
                    .take(2)
                    .joinToString("") { it.first().uppercase() }
                    .ifBlank { "·" }
            },
            role = when (role.lowercase()) {
                "admin" -> Role.Admin
                "viewer" -> Role.Viewer
                else -> Role.Member
            },
        )
    }
}
