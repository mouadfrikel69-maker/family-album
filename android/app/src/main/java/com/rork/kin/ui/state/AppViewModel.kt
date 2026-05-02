package com.rork.kin.ui.state

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rork.kin.data.Album
import com.rork.kin.data.Comment
import com.rork.kin.data.Family
import com.rork.kin.data.FamilyRepository
import com.rork.kin.data.LocalPhotoStore
import com.rork.kin.data.Member
import com.rork.kin.data.Notification
import com.rork.kin.data.Photo
import com.rork.kin.data.Role
import com.rork.kin.data.StoredPhoto
import com.rork.kin.data.SupabaseSync
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
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
    val syncing: Boolean = false,
    val photos: List<Photo> = emptyList(),
    val albums: List<Album> = emptyList(),
    val notifications: List<Notification> = emptyList(),
    val shareUrls: Map<String, String> = emptyMap(),
)

private val ACCENT_PALETTE = listOf(
    0xFFC76B4A, 0xFFE8B4A0, 0xFFD4A574, 0xFF8B7560,
    0xFFB8C4A8, 0xFFA04E33, 0xFFE2A878, 0xFF7C8C6E,
)

class AppViewModel(app: Application) : AndroidViewModel(app) {

    private val _state = MutableStateFlow(AppState())
    val state: StateFlow<AppState> = _state.asStateFlow()

    private val storedPhotos = MutableStateFlow<List<StoredPhoto>>(emptyList())

    val isCloudConfigured: Boolean get() = SupabaseSync.isConfigured

    init {
        viewModelScope.launch {
            val saved = LocalPhotoStore.loadAll(getApplication())
            storedPhotos.value = saved
            if (saved.isNotEmpty()) {
                _state.update { it.copy(photos = saved.map(LocalPhotoStore::toPhoto)) }
            }
        }
    }

    fun completeOnboarding() = _state.update { it.copy(onboarded = true) }
    fun signIn() = _state.update { it.copy(authed = true) }

    /** Save the user's profile from the setup screen. Always Admin to start. */
    fun setProfile(name: String, relationship: String, avatarUri: String? = null) {
        val cleanName = name.trim().ifBlank { "You" }
        val cleanRel = relationship.trim()
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
        val cleanName = name.trim().ifBlank { "Our family" }
        val code = generateInviteCode(cleanName)
        val fam = Family(
            id = "fam_${UUID.randomUUID()}",
            name = cleanName,
            inviteCode = code,
            createdAt = LocalDate.now(),
        )
        FamilyRepository.family = fam
        val me = _state.value.currentUser
        _state.update {
            it.copy(
                inFamily = true,
                family = fam,
                inviteCode = code,
                members = listOfNotNull(me),
            )
        }
        syncFromRemote()
    }

    fun joinFamily(code: String? = null) {
        val cleaned = code?.trim()?.uppercase()?.takeIf { it.isNotBlank() } ?: ""
        val fam = Family(
            id = "fam_join_${UUID.randomUUID()}",
            name = "Your family",
            inviteCode = cleaned,
            createdAt = LocalDate.now(),
        )
        FamilyRepository.family = fam
        val me = _state.value.currentUser
        _state.update {
            it.copy(
                inFamily = true,
                family = fam,
                inviteCode = cleaned,
                members = listOfNotNull(me),
            )
        }
        syncFromRemote()
    }

    private fun generateInviteCode(familyName: String): String {
        val prefix = familyName
            .uppercase()
            .filter { it.isLetter() }
            .take(5)
            .ifBlank { "FAM" }
        val year = LocalDate.now().year
        return "$prefix-$year"
    }

    fun signOut() {
        _state.update { AppState() }
    }

    /** Pull remote photos for the current family and merge into the feed. */
    fun syncFromRemote() {
        if (!SupabaseSync.isConfigured) return
        val code = _state.value.inviteCode.ifBlank { return }
        viewModelScope.launch {
            _state.update { it.copy(syncing = true) }
            val remote = SupabaseSync.listPhotos(code)
            val me = FamilyRepository.currentUserId
            val knownLocalIds = storedPhotos.value.map { it.id }.toSet()
            val incoming = remote
                .filter { it.local_id !in knownLocalIds }
                .map { SupabaseSync.toPhoto(it, me, fallbackId = "r_" + it.local_id) }
            if (incoming.isNotEmpty()) {
                _state.update { s ->
                    val existingIds = s.photos.map { it.id }.toSet()
                    val deduped = incoming.filter { it.id !in existingIds }
                    s.copy(photos = deduped + s.photos)
                }
            }
            _state.update { it.copy(syncing = false) }
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
        if (text.isBlank()) return
        val newComment = Comment(
            id = "c_${System.currentTimeMillis()}",
            authorId = FamilyRepository.currentUserId,
            text = text.trim(),
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
        val now = LocalDateTime.now()
        val today = LocalDate.now()
        val newStored = paths.map { path ->
            StoredPhoto(
                id = "p_${UUID.randomUUID()}",
                path = path,
                caption = caption.ifBlank { "A new little memory." },
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
        // Push to Supabase so other family members see them.
        if (SupabaseSync.isConfigured && _state.value.inviteCode.isNotBlank()) {
            newStored.forEach { pushStoredToRemote(it) }
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
        val cleanTitle = title.trim().ifBlank { "Untitled album" }
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

    fun addFromCapture(file: File, caption: String, albumId: String?, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            val ctx = getApplication<Application>()
            val path = LocalPhotoStore.importFromCapture(ctx, file)
            if (path != null) addLocalPhotos(listOf(path), caption, albumId)
            onDone()
        }
    }

    private fun pushStoredToRemote(sp: StoredPhoto) {
        viewModelScope.launch {
            val file = File(sp.path)
            if (!file.exists()) return@launch
            val code = _state.value.inviteCode.ifBlank { return@launch }
            val publicUrl = SupabaseSync.uploadFile(code, file) ?: return@launch
            val me = _state.value.currentUser ?: FamilyRepository.currentUser
            SupabaseSync.insertPhoto(
                SupabaseSync.RemotePhoto(
                    invite_code = code,
                    local_id = sp.id,
                    author_name = me.name,
                    author_initials = me.initials,
                    author_color = me.avatarColor,
                    caption = sp.caption,
                    image_url = publicUrl,
                    taken_on = sp.takenOnIso,
                )
            )
            _state.update { s -> s.copy(shareUrls = s.shareUrls + (sp.id to publicUrl)) }
        }
    }

    fun getShareUrl(photoId: String, onResult: (String?) -> Unit) {
        val photo = photoById(photoId)
        if (photo != null && (photo.url.startsWith("https://") || photo.url.startsWith("http://"))) {
            onResult(photo.url); return
        }
        _state.value.shareUrls[photoId]?.let { onResult(it); return }
        if (!SupabaseSync.isConfigured) { onResult(null); return }
        val sp = storedPhotos.value.firstOrNull { it.id == photoId }
        if (sp == null) { onResult(null); return }
        viewModelScope.launch {
            val file = File(sp.path)
            if (!file.exists()) { onResult(null); return@launch }
            val code = _state.value.inviteCode.ifBlank { onResult(null); return@launch }
            val publicUrl = SupabaseSync.uploadFile(code, file)
            if (publicUrl == null) { onResult(null); return@launch }
            val me = _state.value.currentUser ?: FamilyRepository.currentUser
            SupabaseSync.insertPhoto(
                SupabaseSync.RemotePhoto(
                    invite_code = code,
                    local_id = sp.id,
                    author_name = me.name,
                    author_initials = me.initials,
                    author_color = me.avatarColor,
                    caption = sp.caption,
                    image_url = publicUrl,
                    taken_on = sp.takenOnIso,
                )
            )
            _state.update { s -> s.copy(shareUrls = s.shareUrls + (sp.id to publicUrl)) }
            onResult(publicUrl)
        }
    }

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
