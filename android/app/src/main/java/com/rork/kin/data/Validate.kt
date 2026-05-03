package com.rork.kin.data

import java.security.SecureRandom

/**
 * Centralised input validation + safe normalisation.
 *
 * Goals:
 *  - Reject obviously malicious or oversized input client-side (defence in depth;
 *    Supabase RLS + column constraints are the real authority).
 *  - Strip control characters, zero-width unicode, and bidi overrides that can
 *    be used for spoofing attacks.
 *  - Cap lengths so a malicious user can't push huge rows into the family.
 */
object Validate {

    // Email — RFC 5322-lite. Good enough for client-side gating; the auth server
    // is the source of truth.
    private val EMAIL = Regex("^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}$")

    // Strict alphanumeric+dash for invite codes / paths.
    private val INVITE = Regex("^[A-Z0-9-]{6,40}$")

    // Unsafe unicode: control chars, zero-width, bidi overrides, BOM.
    private val UNSAFE = Regex(
        "[\\u0000-\\u001F\\u007F\\u200B-\\u200F\\u202A-\\u202E\\u2066-\\u2069\\uFEFF]"
    )

    const val MAX_NAME = 60
    const val MAX_RELATIONSHIP = 30
    const val MAX_CAPTION = 500
    const val MAX_FAMILY_NAME = 60
    const val MAX_COMMENT = 1000
    const val MAX_PASSWORD = 128
    const val MIN_PASSWORD = 8
    const val MAX_EMAIL = 254

    fun email(raw: String): String? {
        val v = raw.trim()
        if (v.length > MAX_EMAIL) return null
        return v.takeIf { EMAIL.matches(it) }?.lowercase()
    }

    fun password(raw: String): String? {
        if (raw.length !in MIN_PASSWORD..MAX_PASSWORD) return null
        // Reject control chars; allow any printable unicode otherwise.
        if (UNSAFE.containsMatchIn(raw)) return null
        return raw
    }

    fun name(raw: String, max: Int = MAX_NAME): String =
        raw.replace(UNSAFE, "").trim().take(max)

    fun caption(raw: String): String =
        raw.replace(UNSAFE, "").trim().take(MAX_CAPTION)

    fun comment(raw: String): String =
        raw.replace(UNSAFE, "").trim().take(MAX_COMMENT)

    fun inviteCode(raw: String): String? {
        val v = raw.trim().uppercase().replace(" ", "")
        return v.takeIf { INVITE.matches(it) }
    }

    /**
     * Cryptographically strong, human-readable invite code.
     * 16 chars from a Crockford-ish alphabet (no 0/O/1/I) split with dashes:
     *   e.g. "K7HX-9PRD-3VNQ-W8XT"
     *
     * 32^16 ≈ 1.2 × 10^24 possibilities — not bruteforceable.
     */
    fun newInviteCode(): String {
        val alphabet = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ"
        val rng = SecureRandom()
        val sb = StringBuilder()
        repeat(16) { i ->
            if (i > 0 && i % 4 == 0) sb.append('-')
            sb.append(alphabet[rng.nextInt(alphabet.length)])
        }
        return sb.toString()
    }
}
