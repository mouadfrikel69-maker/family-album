package com.rork.kin.data

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Serializable
data class StoredComment(
    val id: String,
    val authorId: String,
    val text: String,
    val createdAtIso: String,
)

@Serializable
data class StoredPhoto(
    val id: String,
    val path: String,
    val caption: String,
    val authorId: String,
    val createdAtIso: String,
    val takenOnIso: String,
    /** Legacy single-album field; superseded by [albumIds]. */
    val albumId: String? = null,
    val albumIds: List<String> = emptyList(),
    @SerialName("likedBy") val likedBy: List<String> = emptyList(),
    val comments: List<StoredComment> = emptyList(),
    val taggedMemberIds: List<String> = emptyList(),
)

object LocalPhotoStore {

    private const val TAG = "LocalPhotoStore"
    private const val META_FILE = "user_photos.json"
    private const val META_TMP_SUFFIX = ".tmp"
    private const val PHOTOS_DIR = "photos"
    private const val CAPTURE_DIR = "capture"

    private val json = Json { ignoreUnknownKeys = true; prettyPrint = false }

    private fun photosDir(ctx: Context): File =
        File(ctx.filesDir, PHOTOS_DIR).apply { if (!exists()) mkdirs() }

    private fun captureDir(ctx: Context): File =
        File(ctx.cacheDir, CAPTURE_DIR).apply { if (!exists()) mkdirs() }

    private fun metaFile(ctx: Context) = File(ctx.filesDir, META_FILE)

    /** Create an empty file in the cache + return a FileProvider URI for camera capture. */
    fun newCaptureTarget(ctx: Context): Pair<File, Uri> {
        val file = File(captureDir(ctx), "cap_${System.currentTimeMillis()}.jpg")
        file.createNewFile()
        val uri = FileProvider.getUriForFile(
            ctx,
            "${ctx.packageName}.fileprovider",
            file,
        )
        return file to uri
    }

    /** Copy a content URI into permanent app storage and strip identifying EXIF. */
    suspend fun importFromUri(ctx: Context, uri: Uri): String? = withContext(Dispatchers.IO) {
        runCatching {
            val out = File(photosDir(ctx), "img_${UUID.randomUUID()}.jpg")
            ctx.contentResolver.openInputStream(uri)?.use { input ->
                out.outputStream().use { output -> input.copyTo(output) }
            } ?: return@withContext null
            stripIdentifyingExif(out)
            out.absolutePath
        }.getOrNull()
    }

    /** Move a captured camera file into permanent storage and strip EXIF. */
    suspend fun importFromCapture(ctx: Context, captured: File): String? =
        withContext(Dispatchers.IO) {
            runCatching {
                if (!captured.exists() || captured.length() == 0L) return@withContext null
                val out = File(photosDir(ctx), "img_${UUID.randomUUID()}.jpg")
                captured.inputStream().use { input ->
                    out.outputStream().use { output -> input.copyTo(output) }
                }
                captured.delete()
                stripIdentifyingExif(out)
                out.absolutePath
            }.getOrNull()
        }

    /**
     * Best-effort scrub of EXIF tags that leak personally-identifying info
     * (GPS, camera maker / serial, original software name, etc.).
     *
     * Orientation is preserved so portrait shots keep their up-direction.
     * Non-fatal: if the file isn't a JPEG that ExifInterface can parse, we
     * just leave it alone — the photo still works in the album.
     */
    private fun stripIdentifyingExif(file: File) {
        runCatching {
            val exif = ExifInterface(file.absolutePath)
            // Tags we absolutely don't want surviving onto someone else's device.
            val toStrip = listOf(
                ExifInterface.TAG_GPS_LATITUDE,
                ExifInterface.TAG_GPS_LATITUDE_REF,
                ExifInterface.TAG_GPS_LONGITUDE,
                ExifInterface.TAG_GPS_LONGITUDE_REF,
                ExifInterface.TAG_GPS_ALTITUDE,
                ExifInterface.TAG_GPS_ALTITUDE_REF,
                ExifInterface.TAG_GPS_TIMESTAMP,
                ExifInterface.TAG_GPS_DATESTAMP,
                ExifInterface.TAG_GPS_PROCESSING_METHOD,
                ExifInterface.TAG_GPS_AREA_INFORMATION,
                ExifInterface.TAG_GPS_DEST_BEARING,
                ExifInterface.TAG_MAKE,
                ExifInterface.TAG_MODEL,
                ExifInterface.TAG_BODY_SERIAL_NUMBER,
                ExifInterface.TAG_LENS_SERIAL_NUMBER,
                ExifInterface.TAG_LENS_MAKE,
                ExifInterface.TAG_LENS_MODEL,
                ExifInterface.TAG_SOFTWARE,
                ExifInterface.TAG_ARTIST,
                ExifInterface.TAG_COPYRIGHT,
                ExifInterface.TAG_USER_COMMENT,
                ExifInterface.TAG_IMAGE_DESCRIPTION,
            )
            for (tag in toStrip) exif.setAttribute(tag, null)
            exif.saveAttributes()
        }.onFailure { Log.w(TAG, "stripIdentifyingExif failed for ${file.name}", it) }
    }

    suspend fun loadAll(ctx: Context): List<StoredPhoto> = withContext(Dispatchers.IO) {
        val f = metaFile(ctx)
        if (!f.exists()) return@withContext emptyList()
        runCatching { json.decodeFromString<List<StoredPhoto>>(f.readText()) }
            .onFailure { Log.w(TAG, "loadAll: meta unreadable, starting from empty", it) }
            .getOrDefault(emptyList())
    }

    /**
     * Persist the photo list **atomically**: write to a sibling `.tmp` file
     * first, then rename onto the real meta file. A crash partway through
     * leaves the previous `user_photos.json` intact instead of producing a
     * zero-byte / partial JSON.
     */
    suspend fun saveAll(ctx: Context, photos: List<StoredPhoto>) = withContext(Dispatchers.IO) {
        runCatching {
            val target = metaFile(ctx)
            val tmp = File(target.parentFile, target.name + META_TMP_SUFFIX)
            tmp.writeText(json.encodeToString(photos))
            // renameTo isn't guaranteed atomic on every fs, but it's close enough
            // for app-private storage. If it fails, fall back to a copy + delete.
            if (!tmp.renameTo(target)) {
                tmp.copyTo(target, overwrite = true)
                tmp.delete()
            }
        }.onFailure { Log.w(TAG, "saveAll failed", it) }
        Unit
    }

    /**
     * Remove the photo's bytes **and** its metadata row, then persist. Replaces
     * the previous bytes-only delete which left a dangling JSON entry.
     */
    suspend fun delete(ctx: Context, photo: StoredPhoto) = withContext(Dispatchers.IO) {
        runCatching { File(photo.path).delete() }
        val remaining = loadAll(ctx).filterNot { it.id == photo.id }
        saveAll(ctx, remaining)
    }

    fun toPhoto(s: StoredPhoto): Photo {
        val albums = if (s.albumIds.isNotEmpty()) s.albumIds
        else listOfNotNull(s.albumId)
        return Photo(
            id = s.id,
            url = "file://${s.path}",
            authorId = s.authorId,
            caption = s.caption,
            createdAt = LocalDateTime.parse(s.createdAtIso),
            takenOn = LocalDate.parse(s.takenOnIso),
            albumIds = albums,
            likedBy = s.likedBy.toSet(),
            comments = s.comments.map { c ->
                Comment(
                    id = c.id,
                    authorId = c.authorId,
                    text = c.text,
                    createdAt = LocalDateTime.parse(c.createdAtIso),
                )
            },
            taggedMemberIds = s.taggedMemberIds,
        )
    }

    fun fromPhoto(p: Photo): StoredPhoto {
        val path = p.url.removePrefix("file://")
        return StoredPhoto(
            id = p.id,
            path = path,
            caption = p.caption,
            authorId = p.authorId,
            createdAtIso = p.createdAt.toString(),
            takenOnIso = p.takenOn.toString(),
            albumIds = p.albumIds,
            likedBy = p.likedBy.toList(),
            comments = p.comments.map { c ->
                StoredComment(
                    id = c.id,
                    authorId = c.authorId,
                    text = c.text,
                    createdAtIso = c.createdAt.toString(),
                )
            },
            taggedMemberIds = p.taggedMemberIds,
        )
    }
}
