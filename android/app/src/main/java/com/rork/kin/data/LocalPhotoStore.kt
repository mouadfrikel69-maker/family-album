package com.rork.kin.data

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

@Serializable
data class StoredPhoto(
    val id: String,
    val path: String,
    val caption: String,
    val authorId: String,
    val createdAtIso: String,
    val takenOnIso: String,
    val albumId: String? = null,
)

object LocalPhotoStore {

    private const val META_FILE = "user_photos.json"
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

    /** Copy a content URI into permanent app storage. Returns the absolute file path. */
    suspend fun importFromUri(ctx: Context, uri: Uri): String? = withContext(Dispatchers.IO) {
        runCatching {
            val out = File(photosDir(ctx), "img_${UUID.randomUUID()}.jpg")
            ctx.contentResolver.openInputStream(uri)?.use { input ->
                out.outputStream().use { output -> input.copyTo(output) }
            } ?: return@withContext null
            out.absolutePath
        }.getOrNull()
    }

    /** Move a captured camera file into permanent storage. Returns absolute path. */
    suspend fun importFromCapture(ctx: Context, captured: File): String? =
        withContext(Dispatchers.IO) {
            runCatching {
                if (!captured.exists() || captured.length() == 0L) return@withContext null
                val out = File(photosDir(ctx), "img_${UUID.randomUUID()}.jpg")
                captured.inputStream().use { input ->
                    out.outputStream().use { output -> input.copyTo(output) }
                }
                captured.delete()
                out.absolutePath
            }.getOrNull()
        }

    suspend fun loadAll(ctx: Context): List<StoredPhoto> = withContext(Dispatchers.IO) {
        val f = metaFile(ctx)
        if (!f.exists()) return@withContext emptyList()
        runCatching { json.decodeFromString<List<StoredPhoto>>(f.readText()) }
            .getOrDefault(emptyList())
    }

    suspend fun saveAll(ctx: Context, photos: List<StoredPhoto>) = withContext(Dispatchers.IO) {
        runCatching { metaFile(ctx).writeText(json.encodeToString(photos)) }
        Unit
    }

    suspend fun delete(ctx: Context, photo: StoredPhoto) = withContext(Dispatchers.IO) {
        runCatching { File(photo.path).delete() }
        Unit
    }

    fun toPhoto(s: StoredPhoto): Photo = Photo(
        id = s.id,
        url = "file://${s.path}",
        authorId = s.authorId,
        caption = s.caption,
        createdAt = LocalDateTime.parse(s.createdAtIso),
        takenOn = LocalDate.parse(s.takenOnIso),
        albumIds = listOfNotNull(s.albumId),
    )
}
