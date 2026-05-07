package com.rork.kin.data

import java.time.LocalDate

/**
 * Holds the signed-in user's profile and the family they belong to.
 *
 * No seeded demo data: everything starts empty. The current user is set from the
 * profile-setup screen after sign-in. Members, photos, albums and notifications
 * are populated by Supabase as real activity happens.
 */
object FamilyRepository {

    private const val GUEST_ID = "me"

    /** Filled in once the user completes profile setup. */
    @Volatile
    var currentUser: Member = Member(
        id = GUEST_ID,
        name = "You",
        relationship = "",
        avatarColor = 0xFFC76B4A,
        initials = "·",
        role = Role.Admin,
    )

    val currentUserId: String get() = currentUser.id

    /** Family the user has created or joined. Set when joining/creating a family. */
    @Volatile
    var family: Family = Family(
        id = "fam_pending",
        name = "Your family",
        inviteCode = "",
        createdAt = LocalDate.now(),
    )

    /** Wipe both singletons back to their default placeholders. Called on sign-out. */
    fun reset() {
        currentUser = Member(
            id = GUEST_ID,
            name = "You",
            relationship = "",
            avatarColor = 0xFFC76B4A,
            initials = "·",
            role = Role.Admin,
        )
        family = Family(
            id = "fam_pending",
            name = "Your family",
            inviteCode = "",
            createdAt = LocalDate.now(),
        )
    }

    /**
     * Best-effort lookup. Returns a soft placeholder so the UI never crashes
     * when a member id (e.g. from a remote photo) isn't in the local list yet.
     */
    fun memberById(id: String, members: List<Member> = emptyList()): Member {
        if (id == currentUser.id) return currentUser
        members.firstOrNull { it.id == id }?.let { return it }
        return Member(
            id = id,
            name = "Family member",
            relationship = "",
            avatarColor = 0xFFB8C4A8,
            initials = "·",
            role = Role.Member,
        )
    }
}
