package com.rork.kin.ui.state

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rork.kin.data.Album
import com.rork.kin.data.AuthRateLimiter
import com.rork.kin.data.AvatarColor
import com.rork.kin.data.Comment
import com.rork.kin.data.Family
import com.rork.kin.data.FamilyRepository
import com.rork.kin.data.LocalPhotoStore
import com.rork.kin.data.Member
import com.rork.kin.data.Notification
import com.rork.kin.data.Photo
import com.rork.kin.data.Role
import com.rork.kin.data.SecureStore
import com.rork.kin.data.StoredPhoto
import com.rork.kin.data.SupabaseAuth
import com.rork.kin.data.SupabaseFamily
import com.rork.kin.data.Validate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

data class AppState(
    val onboarded: Boolean = false,
    val authed: Boolean = false,
    val profileReady: Boolean = false,
    val inFamily: Boolean = false,
    val currentUser: Member? = null,
    val family: Family? = null,
    val members: List<Member> = emptyList(),
    val inviteCode: String = "",
    val photos: List<Photo> = emptyList(),
    val albums: List<Album> = emptyList(),
    val notifications: List<Notification> = emptyList(),
)

// Source of truth for avatar accent colours lives in [AvatarColor] so the
// remote-member mapper in SupabaseFamily can fall back to the same palette
// when `family_members.avatar_color` is its default (0L = transparent).
private val ACCENT_PALETTE: List<Long> get() = AvatarColor.PALETTE

class AppViewModel(app: Application) : AndroidViewModel(app) {

    private val _state = MutableStateFlow(AppState())
    val state: StateFlow<AppState> = _state.asStateFlow()

    private val storedPhotos = MutableStateFlow<List<StoredPhoto>>(emptyList())

    init {
        viewModelScope.launch {
            val ctx = getApplication<Application>()
            // Restore encrypted session, if any.
            val restored = SupabaseAuth.restore(ctx)
            val savedCode = SecureStore.get(ctx, SecureStore.KEY_INVITE_CODE).orEmpty()
            val savedFamilyName = SecureStore.get(ctx, SecureStore.KEY_FAMILY_NAME).orEmpty()
            val savedFamilyId = SecureStore.get(ctx, SecureStore.KEY_FAMILY_ID)
            if (restored != null) {
                _state.update {
                    it.copy(
                        authed = true,
                        inviteCode = savedCode,
                        inFamily = savedCode.isNotBlank(),
                        family = if (savedCode.isNotBlank()) Family(
                            id = savedFamilyId ?: "fam_local",
                            name = savedFamilyName.ifBlank { "Your family" },
                            inviteCode = savedCode,
                            createdAt = LocalDate.now(),
                        ) else null,
                    )
                }
            }
            val saved = LocalPhotoStore.loadAll(ctx)
            storedPhotos.value = saved
            if (saved.isNotEmpty()) {
                _state.update { it.copy(photos = saved.map(LocalPhotoStore::toPhoto)) }
            }
            // Pull the canonical member list from Supabase so other devices
            // appear in the UI — the previous build only ever showed the local
            // "You" because nothing read `family_members` back. Best-effort: a
            // network failure leaves the local fallback intact.
            if (restored != null && SupabaseAuth.isConfigured && savedFamilyId != null) {
                refreshMembers(savedFamilyId)
            }
        }
    }

    /**
     * Pull the family's current `family_members` rows from Supabase and merge
     * them into [_state]. The local "You" pill is kept at the head of the
     * list so it never disappears during a network blip; remote rows replace
     * the local placeholder for the same `user_id`.
     */
    private suspend fun refreshMembers(familyId: String) {
        val ctx = getApplication<Application>()
        when (val r = SupabaseFamily.fetchMembers(ctx, familyId)) {
            is SupabaseFamily.Result.Ok -> {
                val remote = r.value
                _state.update { s ->
                    val me = s.currentUser
                    val merged: List<Member> = if (me != null) {
                        // Prefer the remote row for the current user (it's
                        // the authoritative one) but if the server hasn't
                        // populated it yet, keep the local profile.
                        val remoteMe = remote.firstOrNull { it.id == me.id }
                        listOfNotNull(remoteMe ?: me) +
                            remote.filter { it.id != me.id }
                    } else {
                        remote
                    }
                    s.copy(members = merged)
                }
            }
            is SupabaseFamily.Result.Error -> {
                // Non-fatal: keep whatever members list we already had so the
                // UI doesn't suddenly empty out on a transient failure.
            }
        }
    }

    fun completeOnboarding() = _state.update { it.copy(onboarded = true) }

    /**
     * Real sign-in — email + password. Returns null on success, an inline error
     * message otherwise. The previous no-arg `signIn()` backdoor is gone.
     *
     * In local-only mode (Supabase not configured) the credentials are still
     * normalised + rate-limited; the only thing skipped is the network round-trip.
     */
    suspend fun signInWithPassword(email: String, password: String): String? {
        val ctx = getApplication<Application>()
        val cleanEmail = Validate.email(email) ?: return "Enter a valid email"
        val cleanPass = Validate.password(password)
            ?: return Validate.passwordError(password) ?: "Password is invalid"
        // Throttle credential stuffing per-account.
        val lockoutMs = AuthRateLimiter.lockoutRemainingMs(ctx, cleanEmail)
        if (lockoutMs > 0) {
            return "Too many attempts. Try again in ${AuthRateLimiter.formatRemaining(lockoutMs)}."
        }
        if (!SupabaseAuth.isConfigured) {
            // Local-only mode — the AuthScreen surfaces a visible "DEV bypass" banner
            // so this can't be mistaken for a real authenticated session.
            _state.update { it.copy(authed = true) }
            return null
        }
        return when (val r = SupabaseAuth.signIn(ctx, cleanEmail, cleanPass)) {
            is SupabaseAuth.AuthResult.Ok -> {
                AuthRateLimiter.recordSuccess(ctx, cleanEmail)
                _state.update { it.copy(authed = true) }
                null
            }
            is SupabaseAuth.AuthResult.Error -> {
                AuthRateLimiter.recordFailure(ctx, cleanEmail)
                r.message
            }
        }
    }

    suspend fun signUpWithPassword(email: String, password: String): String? {
        val ctx = getApplication<Application>()
        val cleanEmail = Validate.email(email) ?: return "Enter a valid email"
        val cleanPass = Validate.password(password)
            ?: return Validate.passwordError(password) ?: "Password must be 8+ characters with a letter and a number"
        if (!SupabaseAuth.isConfigured) {
            _state.update { it.copy(authed = true) }
            return null
        }
        return when (val r = SupabaseAuth.signUp(ctx, cleanEmail, cleanPass)) {
            is SupabaseAuth.AuthResult.Ok -> {
                AuthRateLimiter.recordSuccess(ctx, cleanEmail)
                _state.update { it.copy(authed = true) }
                null
            }
            is SupabaseAuth.AuthResult.Error -> r.message
        }
    }

    /** Save the user's profile from the setup screen. Always Admin to start. */
    fun setProfile(name: String, relationship: String, avatarUri: String? = null) {
        val cleanName = Validate.name(name).ifBlank { "You" }
        val cleanRel = Validate.name(relationship, max = Validate.MAX_RELATIONSHIP)
        val initials = cleanName
            .split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .joinToString("") { it.first().uppercase() }
            .ifBlank { "Y" }
        val color = ACCENT_PALETTE[(cleanName.hashCode() and 0x7fffffff) % ACCENT_PALETTE.size]
        // The local Member.id MUST match the Supabase auth UID so it lines up
        // with the `family_members.user_id` column on the server. Without this
        // the merge logic in [refreshMembers] couldn't tell that the current
        // user's local row and the one fetched from PostgREST were the same
        // person, which produced a duplicate "you" pill in MembersScreen.
        // Local-only mode (no Supabase) keeps the synthetic id as a fallback.
        val authUid = SupabaseAuth.session?.user?.id
        val memberId = authUid ?: "me_${UUID.randomUUID()}"
        val me = Member(
            id = memberId,
            name = cleanName,
            relationship = cleanRel,
            avatarColor = color,
            initials = initials,
            role = Role.Admin,
        )
        FamilyRepository.currentUser = me
        val ctx = getApplication<Application>()
        SecureStore.put(ctx, SecureStore.KEY_PROFILE_NAME, cleanName)
        SecureStore.put(ctx, SecureStore.KEY_PROFILE_REL, cleanRel)
        SecureStore.put(ctx, SecureStore.KEY_PROFILE_INITIALS, initials)
        SecureStore.put(ctx, SecureStore.KEY_PROFILE_COLOR, color.toString())
        _state.update {
            it.copy(
                profileReady = true,
                currentUser = me,
                members = if (it.members.any { m -> m.id == me.id }) it.members else listOf(me) + it.members.filter { m -> m.id != FamilyRepository.currentUserId },
            )
        }
        // Avatar URI is accepted for future use; not persisted yet.
        @Suppress("UNUSED_EXPRESSION") avatarUri
    }

    fun updateProfile(name: String, relationship: String) = setProfile(name, relationship)

    /**
     * Create a new family. Returns null on success, error message on failure.
     *
     * When Supabase is configured the row is inserted into the `families` table
     * (RLS enforces `auth.uid() = created_by`). In local-only mode the family is
     * synthesised on-device so the rest of the UI keeps working without a backend.
     */
    suspend fun createFamily(name: String): String? {
        val cleanName = Validate.name(name, max = Validate.MAX_FAMILY_NAME).ifBlank { "Our family" }
        val code = Validate.newInviteCode()
        val ctx = getApplication<Application>()

        val fam: Family = if (SupabaseAuth.isConfigured) {
            when (val r = SupabaseFamily.createFamily(ctx, cleanName, code)) {
                is SupabaseFamily.Result.Ok -> Family(
                    id = r.value.id,
                    name = r.value.name,
                    inviteCode = r.value.inviteCode,
                    createdAt = LocalDate.now(),
                )
                is SupabaseFamily.Result.Error -> return r.message
            }
        } else {
            Family(
                id = "fam_${UUID.randomUUID()}",
                name = cleanName,
                inviteCode = code,
                createdAt = LocalDate.now(),
            )
        }

        FamilyRepository.family = fam
        SecureStore.put(ctx, SecureStore.KEY_INVITE_CODE, fam.inviteCode)
        SecureStore.put(ctx, SecureStore.KEY_FAMILY_ID, fam.id)
        SecureStore.put(ctx, SecureStore.KEY_FAMILY_NAME, fam.name)
        val me = _state.value.currentUser
        _state.update {
            it.copy(
                inFamily = true,
                family = fam,
                inviteCode = fam.inviteCode,
                members = listOfNotNull(me),
            )
        }
        // The DB trigger inserts the creator into `family_members` server-side;
        // pull that row back so the UI matches what other devices will see.
        if (SupabaseAuth.isConfigured) refreshMembers(fam.id)
        return null
    }

    /**
     * Join a family by invite code. Returns null on success, error message on
     * failure. In local-only mode the code is just stored on-device.
     */
    suspend fun joinFamily(code: String?): String? {
        // Accept lightly-formatted codes; Validate enforces the strict shape.
        val cleaned = code?.let { Validate.inviteCode(it) }
            ?: return "Enter a valid invite code"
        val ctx = getApplication<Application>()

        val fam: Family = if (SupabaseAuth.isConfigured) {
            val me = _state.value.currentUser
            val displayName = me?.name ?: ""
            val rel = me?.relationship ?: ""
            when (val r = SupabaseFamily.joinFamily(ctx, cleaned, displayName, rel)) {
                is SupabaseFamily.Result.Ok -> Family(
                    id = r.value,
                    // The RPC only returns the family id; the name surfaces from the
                    // "members read" SELECT once it's fetched. Use a soft placeholder
                    // until then.
                    name = "Your family",
                    inviteCode = cleaned,
                    createdAt = LocalDate.now(),
                )
                is SupabaseFamily.Result.Error -> return r.message
            }
        } else {
            Family(
                id = "fam_join_${UUID.randomUUID()}",
                name = "Your family",
                inviteCode = cleaned,
                createdAt = LocalDate.now(),
            )
        }

        FamilyRepository.family = fam
        SecureStore.put(ctx, SecureStore.KEY_INVITE_CODE, fam.inviteCode)
        SecureStore.put(ctx, SecureStore.KEY_FAMILY_ID, fam.id)
        SecureStore.put(ctx, SecureStore.KEY_FAMILY_NAME, fam.name)
        val me = _state.value.currentUser
        _state.update {
            it.copy(
                inFamily = true,
                family = fam,
                inviteCode = fam.inviteCode,
                members = listOfNotNull(me),
            )
        }
        // After joining, fetch the rest of the family so the new user sees
        // who's already in it (rather than the previous local-only "just me").
        if (SupabaseAuth.isConfigured) refreshMembers(fam.id)
        return null
    }

    fun signOut() {
        viewModelScope.launch {
            val ctx = getApplication<Application>()
            SupabaseAuth.signOut(ctx)
            SecureStore.clear(ctx)
            // Clear the global mutable singleton too — otherwise the previous
            // user's name / family flickers in the UI before profile-setup
            // overwrites it on the next sign-in.
            FamilyRepository.reset()
            storedPhotos.value = emptyList()
            _state.update { AppState() }
        }
    }

    /**
     * Re-derive [storedPhotos] from the current visible photo list and write
     * the JSON file. Called after any mutation that should outlive the process
     * (likes, comments, album-membership changes, additions, deletions).
     *
     * Only photos that came in via [LocalPhotoStore] (i.e. exist in
     * `storedPhotos.value`) are persisted; demo seed data stays in memory.
     */
    private fun persistPhotos() {
        val knownIds = storedPhotos.value.map { it.id }.toSet()
        val updatedStored = _state.value.photos
            .filter { it.id in knownIds }
            .map(LocalPhotoStore::fromPhoto)
        storedPhotos.value = updatedStored
        viewModelScope.launch {
            LocalPhotoStore.saveAll(getApplication(), updatedStored)
        }
    }

    fun toggleLike(photoId: String) {
        val me = FamilyRepository.currentUserId
        _state.update { s ->
            s.copy(photos = s.photos.map { p ->
                if (p.id != photoId) p
                else p.copy(
                    likedBy = if (me in p.likedBy) p.likedBy - me else p.likedBy + me
                )
            })
        }
        persistPhotos()
    }

    fun addComment(photoId: String, text: String) {
        val safe = Validate.comment(text)
        if (safe.isBlank()) return
        val newComment = Comment(
            id = "c_${System.currentTimeMillis()}",
            authorId = FamilyRepository.currentUserId,
            text = safe,
            createdAt = LocalDateTime.now(),
        )
        _state.update { s ->
            s.copy(photos = s.photos.map { p ->
                if (p.id != photoId) p else p.copy(comments = p.comments + newComment)
            })
        }
        persistPhotos()
    }

    /**
     * Remove a photo from the album, its bytes from disk, and its metadata row.
     *
     * The metadata write goes through the same [persistPhotos] path as likes /
     * comments / additions — that's the single authoritative writer of
     * `user_photos.json`. If we did the metadata delete inside
     * [LocalPhotoStore] (load-modify-save the file independently), it would
     * race a concurrent like / comment save and one side could resurrect or
     * drop the other's mutation.
     */
    fun deletePhoto(photoId: String) {
        val target = storedPhotos.value.firstOrNull { it.id == photoId }
        _state.update { s -> s.copy(photos = s.photos.filterNot { it.id == photoId }) }
        persistPhotos()
        if (target != null) {
            viewModelScope.launch {
                LocalPhotoStore.deleteFile(target)
            }
        }
    }

    fun addLocalPhotos(paths: List<String>, caption: String, albumId: String?) {
        if (paths.isEmpty()) return
        val safeCaption = Validate.caption(caption)
        val now = LocalDateTime.now()
        val today = LocalDate.now()
        val newStored = paths.map { path ->
            StoredPhoto(
                id = "p_${UUID.randomUUID()}",
                path = path,
                caption = safeCaption.ifBlank { "A new little memory." },
                authorId = FamilyRepository.currentUserId,
                createdAtIso = now.toString(),
                takenOnIso = today.toString(),
                albumIds = listOfNotNull(albumId),
            )
        }
        val newPhotos = newStored.map(LocalPhotoStore::toPhoto)

        storedPhotos.update { it + newStored }
        _state.update { s -> s.copy(photos = newPhotos + s.photos) }

        viewModelScope.launch {
            LocalPhotoStore.saveAll(getApplication(), storedPhotos.value)
        }
    }

    fun addFromUris(uris: List<Uri>, caption: String, albumId: String?, onDone: () -> Unit = {}) {
        if (uris.isEmpty()) { onDone(); return }
        viewModelScope.launch {
            val ctx = getApplication<Application>()
            val paths = uris.mapNotNull { LocalPhotoStore.importFromUri(ctx, it) }
            addLocalPhotos(paths, caption, albumId)
            onDone()
        }
    }

    fun createAlbum(title: String): String {
        val cleanTitle = Validate.name(title, max = Validate.MAX_FAMILY_NAME).ifBlank { "Untitled album" }
        val color = ACCENT_PALETTE[(cleanTitle.hashCode() and 0x7fffffff) % ACCENT_PALETTE.size]
        val today = LocalDate.now()
        val month = today.month.name.lowercase().replaceFirstChar { it.uppercase() }
        val album = Album(
            id = "a_${UUID.randomUUID()}",
            title = cleanTitle,
            coverPhotoId = "",
            photoIds = emptyList(),
            dateRangeLabel = "$month ${today.year}",
            accentColor = color,
        )
        _state.update { it.copy(albums = listOf(album) + it.albums) }
        return album.id
    }

    fun addFromCapture(file: java.io.File, caption: String, albumId: String?, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            val ctx = getApplication<Application>()
            val path = LocalPhotoStore.importFromCapture(ctx, file)
            if (path != null) addLocalPhotos(listOf(path), caption, albumId)
            onDone()
        }
    }

    /** Resolve the on-device file path for a photo, if it's a locally stored one. */
    fun localPathFor(photoId: String): String? =
        storedPhotos.value.firstOrNull { it.id == photoId }?.path

    /** Resolve a member by id, falling back to a soft placeholder. */
    fun memberById(id: String): Member =
        FamilyRepository.memberById(id, _state.value.members)

    fun photoById(id: String): Photo? = _state.value.photos.firstOrNull { it.id == id }
    fun albumById(id: String): Album? = _state.value.albums.firstOrNull { it.id == id }

    fun photosByAlbum(albumId: String): List<Photo> {
        val album = albumById(albumId) ?: return emptyList()
        val all = _state.value.photos
        // Build an id-keyed index once so the `seeded` lookup is O(m) instead
        // of O(n*m). At 5 K photos / 50-photo albums the previous nested
        // firstOrNull was 250 K comparisons per album-detail render.
        val byId = all.associateBy { it.id }
        val direct = all.filter { it.albumIds.contains(albumId) }
        val seeded = album.photoIds.mapNotNull(byId::get)
        return (direct + seeded).distinctBy { it.id }
    }

    fun memories(today: LocalDate = LocalDate.now()): List<Photo> {
        return _state.value.photos
            .filter { it.takenOn.year < today.year &&
                    kotlin.math.abs(it.takenOn.dayOfYear - today.dayOfYear) <= 3 }
            .sortedByDescending { it.takenOn }
    }

    fun groupedByMonth(): List<Pair<String, List<Photo>>> {
        return _state.value.photos
            .sortedByDescending { it.takenOn }
            .groupBy { p -> "${p.takenOn.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${p.takenOn.year}" }
            .toList()
    }
}
