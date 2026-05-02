package com.rork.kin.data

/**
 * Supabase project credentials for Kin.
 *
 * Paste your values here from Supabase dashboard → Project Settings → API:
 *   - SUPABASE_URL: Project URL (e.g. https://xxxxxxxx.supabase.co)
 *   - SUPABASE_ANON_KEY: Project API keys → anon public
 *
 * If left blank, the app falls back to local-only mode (no sync, no share links).
 */
object SupabaseConfig {
    const val SUPABASE_URL: String = ""
    const val SUPABASE_ANON_KEY: String = ""
}
