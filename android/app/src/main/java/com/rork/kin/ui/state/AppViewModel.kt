package com.rork.kin.ui.state

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rork.kin.data.Album
import com.rork.kin.data.AuthRateLimiter
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

private val ACCENT_PALETTE = listOf(
    0xFFC76B4A, 0xFFE8B4A0, 0xFFD4A574, 0xFF8B7560,
    0xFFB8C4A8, 0xFFA04E33, 0xFFE2A878, 0xFF7C8C6E,
)

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
            if (restored != null) {
                _state.update {
                    it.copy(
                        authed = true,
                        inviteCode = savedCode,
                        inFamily = savedCode.isNotBlank(),
                        family = if (savedCode.isNotBlank()) Family(
                            id = SecureStore.get(ctx, SecureStore.KEY_FAMILY_ID) ?: "fam_local",
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
        }
    }

    fun completeOnboarding() = _state.update { it.copy(onboarded = true) }

    /** Local-only sign-in flag (kept for screens not yet wired to real auth). */
    fun signIn() = _state.update { it.copy(authed = true) }

    /** Real Supabase auth — email + password. Returns null on success, error message otherwise. */
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
            // Local-only mode — keep app usable without a Supabase project.
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
        val me = Member(
            id = "me_${UUID.randomUUID()}",
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

    fun createFamily(name: String) {
        val cleanName = Validate.name(name, max = Validate.MAX_FAMILY_NAME).ifBlank { "Our family" }
        val code = Validate.newInviteCode()
        val fam = Family(
            id = "fam_${UUID.randomUUID()}",
            name = cleanName,
            inviteCode = code,
            createdAt = LocalDate.now(),
        )
        FamilyRepository.family = fam
        val ctx = getApplication<Application>()
        SecureStore.put(ctx, SecureStore.KEY_INVITE_CODE, code)
        SecureStore.put(ctx, SecureStore.KEY_FAMILY_ID, fam.id)
        SecureStore.put(ctx, SecureStore.KEY_FAMILY_NAME, cleanName)
        val me = _state.value.currentUser
        _state.update {
            it.copy(
                inFamily = true,
                family = fam,
                inviteCode = code,
                members = listOfNotNull(me),
            )
        }
    }

    fun joinFamily(code: String? = null) {
        // Accept lightly-formatted codes; Validate enforces the strict shape.
        val cleaned = code?.let { Validate.inviteCode(it) }
            ?: code?.trim()?.uppercase()?.replace(" ", "")
            ?: ""
        val fam = Family(
            id = "fam_join_${UUID.randomUUID()}",
            name = "Your family",
            inviteCode = cleaned,
            createdAt = LocalDate.now(),
        )
        FamilyRepository.family = fam
        val ctx = getApplication<Application>()
        SecureStore.put(ctx, SecureStore.KEY_INVITE_CODE, cleaned)
        SecureStore.put(ctx, SecureStore.KEY_FAMILY_ID, fam.id)
        SecureStore.put(ctx, SecureStore.KEY_FAMILY_NAME, fam.name)
        val me = _state.value.currentUser
        _state.update {
            it.copy(
                inFamily = true,
                family = fam,
                inviteCode = cleaned,
                members = listOfNotNull(me),
            )
        }
    }

    fun signOut() {
        viewModelScope.launch {
            val ctx = getApplication<Application>()
            SupabaseAuth.signOut(ctx)
            SecureStore.clear(ctx)
            _state.update { AppState() }
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
                albumId = albumId,
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
        val direct = _state.value.photos.filter { it.albumIds.contains(albumId) }
        val seeded = album.photoIds.mapNotNull { pid -> _state.value.photos.firstOrNull { it.id == pid } }
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
