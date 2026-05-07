package com.rork.kin.data

/**
 * Shared accent-colour palette for member avatars. Both the local profile
 * setup ([com.rork.kin.ui.state.AppViewModel.setProfile]) and the remote
 * member mapper ([SupabaseFamily.fetchMembers]) use [colorForSeed] to derive
 * a deterministic colour when the server-side `family_members.avatar_color`
 * column is still its default (`0`, which renders as fully-transparent in
 * Compose's `Color(0L)`).
 *
 * Kept on the warm side intentionally — the app's UI lives on a CreamPaper
 * background and saturated/cool tones look out of place against it.
 */
internal object AvatarColor {

    val PALETTE: List<Long> = listOf(
        0xFFC76B4A, 0xFFE8B4A0, 0xFFD4A574, 0xFF8B7560,
        0xFFB8C4A8, 0xFFA04E33, 0xFFE2A878, 0xFF7C8C6E,
    )

    /**
     * Pick a stable colour from [PALETTE] based on [seed]. The same seed
     * always produces the same colour, so the user keeps the same avatar
     * tint across sessions / devices regardless of which side computed it.
     */
    fun colorForSeed(seed: String): Long {
        if (seed.isEmpty()) return PALETTE[0]
        val h = seed.hashCode() and 0x7fffffff
        return PALETTE[h % PALETTE.size]
    }
}
