package com.rork.kin.data

import android.content.Context
import java.security.MessageDigest

/**
 * Brute-force defence for the sign-in screen.
 *
 * Tracks failed attempts **per-email** in EncryptedSharedPreferences and enforces
 * an exponentially growing lockout window:
 *   3 fails  →  30s
 *   5 fails  →  2m
 *   8 fails  →  10m
 *   12+ fails → 1h
 *
 * State is encrypted at rest, scoped per-account, and cleared on successful sign-in.
 * This is **client-side** defence in depth — Supabase Auth also rate-limits at the
 * server. The combination makes credential stuffing impractical even if a single
 * device is stolen.
 */
object AuthRateLimiter {

    private const val PREFIX_FAILS = "auth_fails_"
    private const val PREFIX_UNTIL = "auth_until_"

    private fun keyHash(email: String): String {
        // Don't store the raw email as a key (mild PII). The previous polynomial
        // hash had a 32-bit codomain, so two unrelated emails could trivially
        // collide and share a lockout counter. SHA-256 truncated to 16 hex chars
        // (64 bits) makes that ~impossible while staying short enough for a key.
        val bytes = MessageDigest.getInstance("SHA-256")
            .digest(email.lowercase().trim().toByteArray(Charsets.UTF_8))
        val sb = StringBuilder(16)
        for (i in 0 until 8) {
            sb.append(((bytes[i].toInt() ushr 4) and 0xF).toString(16))
            sb.append((bytes[i].toInt() and 0xF).toString(16))
        }
        return sb.toString()
    }

    /** Milliseconds remaining before [email] may try again, or 0 if not locked. */
    fun lockoutRemainingMs(ctx: Context, email: String): Long {
        val k = keyHash(email)
        val until = SecureStore.get(ctx, PREFIX_UNTIL + k)?.toLongOrNull() ?: 0L
        val remaining = until - System.currentTimeMillis()
        return if (remaining > 0) remaining else 0L
    }

    /** Record a failed attempt and bump the lockout window. */
    fun recordFailure(ctx: Context, email: String) {
        val k = keyHash(email)
        val fails = (SecureStore.get(ctx, PREFIX_FAILS + k)?.toIntOrNull() ?: 0) + 1
        SecureStore.put(ctx, PREFIX_FAILS + k, fails.toString())
        val penaltyMs = penaltyFor(fails)
        if (penaltyMs > 0) {
            val until = System.currentTimeMillis() + penaltyMs
            SecureStore.put(ctx, PREFIX_UNTIL + k, until.toString())
        }
    }

    /** Reset on successful sign-in. */
    fun recordSuccess(ctx: Context, email: String) {
        val k = keyHash(email)
        SecureStore.put(ctx, PREFIX_FAILS + k, null)
        SecureStore.put(ctx, PREFIX_UNTIL + k, null)
    }

    private fun penaltyFor(fails: Int): Long = when {
        fails < 3 -> 0L
        fails < 5 -> 30_000L            // 30s
        fails < 8 -> 2 * 60_000L        // 2m
        fails < 12 -> 10 * 60_000L      // 10m
        // The previous formula was `min(60L, fails) * 60_000L`, which only hit
        // the documented "1h cap" once `fails >= 60` — i.e. it took 48 extra
        // failures past the threshold to actually reach 1 hour. Now 12+ fails
        // immediately maps to a 1h lockout per the spec.
        else -> 60L * 60_000L           // 1h
    }

    fun formatRemaining(ms: Long): String {
        val s = (ms / 1000).coerceAtLeast(1)
        if (s < 60) return "${s}s"
        val m = s / 60
        if (m < 60) return "${m}m"
        return "${m / 60}h"
    }
}
