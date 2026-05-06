package com.rork.kin.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.rork.kin.data.LocalPhotoStore
import com.rork.kin.ui.state.AppViewModel
import com.rork.kin.ui.theme.BlushPink
import com.rork.kin.ui.theme.CreamPaper
import com.rork.kin.ui.theme.InkBrown
import com.rork.kin.ui.theme.Mocha
import com.rork.kin.ui.theme.PolaroidWhite
import com.rork.kin.ui.theme.Terracotta
import java.io.File

private const val MAX_PHOTOS = 10

@Composable
fun UploadScreen(
    appVm: AppViewModel,
    onClose: () -> Unit,
    contentPadding: PaddingValues,
) {
    val ctx = LocalContext.current
    val state by appVm.state.collectAsStateWithLifecycle()

    var selectedUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var capturedFile by remember { mutableStateOf<File?>(null) }
    var caption by remember { mutableStateOf("") }
    var selectedAlbum by remember { mutableStateOf<String?>(null) }
    var uploading by remember { mutableStateOf(false) }

    var showCameraRationale by remember { mutableStateOf(false) }
    var showCameraDenied by remember { mutableStateOf(false) }

    // Multi-image picker. Photo Picker is permission-free on modern Android.
    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(MAX_PHOTOS),
    ) { uris ->
        if (uris.isNotEmpty()) {
            val combined = (selectedUris + uris).distinct().take(MAX_PHOTOS)
            selectedUris = combined
        }
    }

    // Camera capture writes into a FileProvider URI.
    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture(),
    ) { success ->
        val file = capturedFile
        if (success && file != null && file.exists() && file.length() > 0L) {
            // Treat the captured file as a "selected" item via its FileProvider URI.
            val uri = androidx.core.content.FileProvider.getUriForFile(
                ctx,
                "${ctx.packageName}.fileprovider",
                file,
            )
            selectedUris = (selectedUris + uri).take(MAX_PHOTOS)
        } else {
            file?.delete()
            capturedFile = null
        }
    }

    fun launchCamera() {
        val (file, uri) = LocalPhotoStore.newCaptureTarget(ctx)
        capturedFile = file
        cameraLauncher.launch(uri)
    }

    val cameraPermLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) launchCamera() else showCameraDenied = true
    }

    fun onCameraTap() {
        val granted = ContextCompat.checkSelfPermission(
            ctx, Manifest.permission.CAMERA,
        ) == PackageManager.PERMISSION_GRANTED
        if (granted) launchCamera() else showCameraRationale = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = contentPadding.calculateTopPadding())
            .verticalScroll(rememberScrollState())
            .padding(bottom = 100.dp + contentPadding.calculateBottomPadding()),
    ) {
        Spacer(Modifier.height(8.dp))
        Text(
            "Share a moment",
            style = MaterialTheme.typography.displayMedium,
            color = InkBrown,
            modifier = Modifier.padding(horizontal = 20.dp),
        )
        Text(
            "Pick from your phone or snap one now.",
            style = MaterialTheme.typography.bodyMedium,
            color = Mocha,
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        Spacer(Modifier.height(20.dp))

        // Big preview / empty state
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(20.dp))
                .background(PolaroidWhite),
        ) {
            if (selectedUris.isEmpty()) {
                EmptyPicker(
                    onGallery = {
                        galleryLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo),
                        )
                    },
                    onCamera = ::onCameraTap,
                )
            } else {
                AsyncImage(
                    model = selectedUris.first(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color(0x55000000)),
                                startY = 400f,
                            ),
                        ),
                )
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    ActionPill(Icons.Filled.AddAPhoto, "Camera", onClick = ::onCameraTap)
                    ActionPill(
                        Icons.Filled.PhotoLibrary,
                        "Add more",
                        onClick = {
                            galleryLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo),
                            )
                        },
                    )
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                        .clip(CircleShape)
                        .background(Color(0xCC000000))
                        .clickable { selectedUris = emptyList() }
                        .padding(8.dp),
                ) {
                    Icon(Icons.Filled.Close, "Clear", tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }
        }

        if (selectedUris.size > 1) {
            Spacer(Modifier.height(14.dp))
            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                itemsIndexed(selectedUris) { i, uri ->
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .border(1.dp, BlushPink, RoundedCornerShape(10.dp)),
                    ) {
                        AsyncImage(
                            model = uri,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(2.dp)
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(Color(0xCC000000))
                                .clickable {
                                    selectedUris = selectedUris.toMutableList().also { it.removeAt(i) }
                                },
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons.Filled.Close, null, tint = Color.White, modifier = Modifier.size(12.dp))
                        }
                    }
                }
            }
            Text(
                "${selectedUris.size} of $MAX_PHOTOS selected",
                style = MaterialTheme.typography.labelSmall,
                color = Mocha,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
            )
        }

        Spacer(Modifier.height(20.dp))

        SectionLabel("Caption")
        OutlinedTextField(
            value = caption,
            onValueChange = { caption = it },
            placeholder = { Text("Tell us what was happening…", color = Mocha.copy(alpha = 0.5f)) },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Terracotta,
                unfocusedBorderColor = BlushPink,
                focusedContainerColor = PolaroidWhite,
                unfocusedContainerColor = PolaroidWhite,
            ),
            minLines = 3,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
        )

        Spacer(Modifier.height(20.dp))
        SectionLabel("Add to album (optional)")

        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(state.albums) { album ->
                val selected = album.id == selectedAlbum
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(if (selected) Terracotta else PolaroidWhite)
                        .border(
                            1.dp,
                            if (selected) Terracotta else BlushPink,
                            RoundedCornerShape(50),
                        )
                        .clickable {
                            selectedAlbum = if (selected) null else album.id
                        }
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                ) {
                    if (selected) {
                        Icon(
                            Icons.Filled.Check, null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp),
                        )
                        Spacer(Modifier.width(6.dp))
                    }
                    Text(
                        album.title,
                        style = MaterialTheme.typography.labelLarge,
                        color = if (selected) Color.White else InkBrown,
                    )
                }
            }
        }

        Spacer(Modifier.height(28.dp))

        AnimatedVisibility(visible = uploading, enter = fadeIn(tween(220)), exit = fadeOut(tween(180))) {
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                // Indeterminate — the file copy doesn't expose byte-level progress,
                // so animating a fake fraction is misleading. The pulsing bar tells
                // the user "we're working" without lying about how far along we are.
                LinearProgressIndicator(
                    color = Terracotta,
                    trackColor = BlushPink.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "Tucking your memory into the album…",
                    style = MaterialTheme.typography.labelMedium,
                    color = Mocha,
                )
                Spacer(Modifier.height(16.dp))
            }
        }

        Button(
            onClick = {
                if (selectedUris.isEmpty() || uploading) return@Button
                uploading = true
                appVm.addFromUris(
                    uris = selectedUris,
                    caption = caption,
                    albumId = selectedAlbum,
                ) {
                    uploading = false
                    selectedUris = emptyList()
                    capturedFile = null
                    caption = ""
                    selectedAlbum = null
                    onClose()
                }
            },
            enabled = selectedUris.isNotEmpty() && !uploading,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .height(56.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Terracotta,
                disabledContainerColor = Terracotta.copy(alpha = 0.4f),
            ),
        ) {
            if (uploading) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(Modifier.width(10.dp))
                Text("Saving…", style = MaterialTheme.typography.titleMedium)
            } else {
                val label = when {
                    selectedUris.isEmpty() -> "Pick a photo first"
                    selectedUris.size == 1 -> "Share with family"
                    else -> "Share ${selectedUris.size} memories"
                }
                Text(label, style = MaterialTheme.typography.titleMedium)
            }
        }
    }

    if (showCameraRationale) {
        PrePermissionDialog(
            title = "Use your camera",
            body = "Snap a quick photo to share with your family. We never upload anything until you tap Share.",
            confirm = "Continue",
            onConfirm = {
                showCameraRationale = false
                cameraPermLauncher.launch(Manifest.permission.CAMERA)
            },
            onDismiss = { showCameraRationale = false },
        )
    }

    if (showCameraDenied) {
        AlertDialog(
            onDismissRequest = { showCameraDenied = false },
            containerColor = CreamPaper,
            title = { Text("Camera is off", color = InkBrown) },
            text = {
                Text(
                    "To take a photo, enable the camera permission for Kin in Settings.",
                    color = Mocha,
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showCameraDenied = false
                    openAppSettings(ctx)
                }) { Text("Open Settings", color = Terracotta) }
            },
            dismissButton = {
                TextButton(onClick = { showCameraDenied = false }) {
                    Text("Not now", color = Mocha)
                }
            },
        )
    }
}

@Composable
private fun PrePermissionDialog(
    title: String,
    body: String,
    confirm: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CreamPaper,
        title = { Text(title, color = InkBrown) },
        text = { Text(body, color = Mocha) },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text(confirm, color = Terracotta) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Not now", color = Mocha) }
        },
    )
}

@Composable
private fun EmptyPicker(onGallery: () -> Unit, onCamera: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(BlushPink.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Filled.PhotoLibrary, null, tint = Terracotta, modifier = Modifier.size(32.dp))
        }
        Spacer(Modifier.height(12.dp))
        Text(
            "Pick a memory",
            style = MaterialTheme.typography.titleMedium,
            color = InkBrown,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            "Up to $MAX_PHOTOS at a time. Stays on this device.",
            style = MaterialTheme.typography.bodySmall,
            color = Mocha,
        )
        Spacer(Modifier.height(18.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ActionPill(Icons.Filled.PhotoLibrary, "Gallery", onClick = onGallery)
            ActionPill(Icons.Filled.AddAPhoto, "Camera", onClick = onCamera)
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
        color = Mocha,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
    )
}

@Composable
private fun ActionPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(CircleShape)
            .background(Color(0xCCFFFBF5))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
    ) {
        Icon(icon, null, tint = InkBrown, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(6.dp))
        Text(label, style = MaterialTheme.typography.labelLarge, color = InkBrown)
    }
}

private fun openAppSettings(ctx: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", ctx.packageName, null)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    ctx.startActivity(intent)
}
