package com.rork.kin.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.util.concurrent.ConcurrentHashMap

/**
 * Encrypted on-device storage for Kin secrets.
 *
 * Backed by AndroidX Security `EncryptedSharedPreferences` which uses an
 * AES-256-GCM key wrapped by an Android Keystore master key (StrongBox when
 * available). Values are never written to disk in plaintext.
 *
 * What we store here:
 *   - access_token / refresh_token / user_id  (Supabase session)
 *   - invite_code  (the shared family secret — must not leak via ADB / backups)
 *   - profile_name / relationship  (mild PII)
 *
 * If keystore initialisation fails (very rare — corrupted keystore on a
 * jailbroken device, etc.) we fall back to an in-memory map so the app keeps
 * working but no secrets are persisted across launches. We never silently fall
 * back to plaintext disk storage.
 */
object SecureStore {

    private const val TAG = "SecureStore"
    private const val FILE = "kin_secure_prefs"

    const val KEY_ACCESS_TOKEN = "access_token"
    const val KEY_REFRESH_TOKEN = "refresh_token"
    const val KEY_USER_ID = "user_id"
    const val KEY_USER_EMAIL = "user_email"
    const val KEY_INVITE_CODE = "invite_code"
    const val KEY_FAMILY_ID = "family_id"
    const val KEY_FAMILY_NAME = "family_name"
    const val KEY_PROFILE_NAME = "profile_name"
    const val KEY_PROFILE_REL = "profile_relationship"
    const val KEY_PROFILE_INITIALS = "profile_initials"
    const val KEY_PROFILE_COLOR = "profile_color"

    @Volatile private var prefs: SharedPreferences? = null
    // Coroutines from Dispatchers.IO read/write SecureStore concurrently. The
    // previous mutableMapOf was not thread-safe, so the fallback could throw
    // ConcurrentModificationException or lose writes under load.
    private val memoryFallback = ConcurrentHashMap<String, String>()

    private fun ensure(ctx: Context): SharedPreferences? {
        prefs?.let { return it }
        return synchronized(this) {
            prefs ?: runCatching {
                val key = MasterKey.Builder(ctx.applicationContext)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build()
                EncryptedSharedPreferences.create(
                    ctx.applicationContext,
                    FILE,
                    key,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
                ).also { prefs = it }
            }.onFailure { Log.w(TAG, "EncryptedSharedPreferences unavailable, using memory-only fallback", it) }
                .getOrNull()
        }
    }

    fun put(ctx: Context, key: String, value: String?) {
        val p = ensure(ctx)
        if (p != null) {
            p.edit().apply {
                if (value == null) remove(key) else putString(key, value)
            }.apply()
        } else {
            if (value == null) memoryFallback.remove(key)
            else memoryFallback[key] = value
        }
    }

    fun get(ctx: Context, key: String): String? {
        val p = ensure(ctx)
        return p?.getString(key, null) ?: memoryFallback[key]
    }

    fun clear(ctx: Context) {
        ensure(ctx)?.edit()?.clear()?.apply()
        memoryFallback.clear()
    }
}
