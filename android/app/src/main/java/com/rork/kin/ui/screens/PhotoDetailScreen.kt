package com.rork.kin.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import android.content.Intent
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Share
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.rork.kin.ui.components.Avatar
import com.rork.kin.ui.state.AppViewModel
import com.rork.kin.ui.theme.BlushPink
import com.rork.kin.ui.theme.CreamPaper
import com.rork.kin.ui.theme.InkBrown
import com.rork.kin.ui.theme.Mocha
import com.rork.kin.ui.theme.PolaroidWhite
import com.rork.kin.ui.theme.Terracotta
import java.time.format.DateTimeFormatter

@Composable
fun PhotoDetailScreen(
    appVm: AppViewModel,
    photoId: String,
    onBack: () -> Unit,
) {
    val state by appVm.state.collectAsStateWithLifecycle()
    val photo = state.photos.firstOrNull { it.id == photoId } ?: return
    val author = appVm.memberById(photo.authorId)
    val currentUserId = state.currentUser?.id
    val liked = currentUserId != null && currentUserId in photo.likedBy
    var commentText by remember { mutableStateOf("") }
    val statusInsets = WindowInsets.statusBars.asPaddingValues()
    val navInsets = WindowInsets.navigationBars.asPaddingValues()
    val context = LocalContext.current

    fun onShare() {
        val path = appVm.localPathFor(photo.id)
            ?: photo.url.removePrefix("file://").takeIf { it.startsWith("/") }
        val file = path?.let { File(it) }
        if (file == null || !file.exists()) {
            Toast.makeText(context, "This photo isn't available to share yet.", Toast.LENGTH_LONG).show()
            return
        }
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file,
        )
        val send = Intent(Intent.ACTION_SEND).apply {
            type = "image/jpeg"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, "A memory from Kin")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(send, "Share this memory"))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CreamPaper),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                top = statusInsets.calculateTopPadding(),
                bottom = 90.dp + navInsets.calculateBottomPadding(),
            ),
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = InkBrown)
                    }
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = { /* download */ }) {
                        Icon(Icons.Filled.Download, "Download", tint = InkBrown)
                    }
                    IconButton(onClick = { onShare() }) {
                        Icon(Icons.Filled.Share, "Share", tint = InkBrown)
                    }
                }
            }
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(PolaroidWhite)
                        .padding(10.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(0.85f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(BlushPink),
                    ) {
                        AsyncImage(
                            model = photo.url,
                            contentDescription = photo.caption,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }
            }
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 18.dp),
                ) {
                    Text(
                        photo.caption,
                        style = com.rork.kin.ui.theme.CaptionScript,
                        color = InkBrown,
                    )
                    Spacer(Modifier.height(14.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Avatar(author, size = 32.dp)
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(
                                author.name,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = InkBrown,
                            )
                            Text(
                                photo.takenOn.format(DateTimeFormatter.ofPattern("d MMMM yyyy")),
                                style = MaterialTheme.typography.bodySmall,
                                color = Mocha,
                            )
                        }
                    }
                    if (photo.location != null) {
                        Spacer(Modifier.height(10.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Place, null, tint = Mocha, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(photo.location, style = MaterialTheme.typography.bodySmall, color = Mocha)
                        }
                    }
                    Spacer(Modifier.height(14.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (liked) Terracotta.copy(alpha = 0.15f) else BlushPink)
                                .clickable { appVm.toggleLike(photo.id) }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    if (liked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                    null, tint = Terracotta,
                                    modifier = Modifier.size(18.dp),
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    "${photo.likedBy.size} hearts",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = Terracotta,
                                )
                            }
                        }
                        Spacer(Modifier.width(8.dp))
                        if (photo.likedBy.isNotEmpty()) {
                            Row {
                                photo.likedBy.take(4).forEach { id ->
                                    Avatar(
                                        appVm.memberById(id),
                                        size = 22.dp,
                                        modifier = Modifier.padding(start = 2.dp),
                                    )
                                }
                            }
                        }
                    }
                }
            }
            item {
                Text(
                    "Comments",
                    style = MaterialTheme.typography.titleMedium,
                    color = InkBrown,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                )
            }
            items(photo.comments, key = { it.id }) { c ->
                val a = appVm.memberById(c.authorId)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                ) {
                    Avatar(a, size = 32.dp)
                    Spacer(Modifier.width(10.dp))
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(PolaroidWhite)
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                    ) {
                        Text(
                            "${a.name} · ${a.relationship}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Mocha,
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            c.text,
                            style = MaterialTheme.typography.bodyMedium,
                            color = InkBrown,
                        )
                    }
                }
            }
            if (photo.comments.isEmpty()) {
                item {
                    Text(
                        "Be the first to say something kind.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Mocha,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(CreamPaper)
                .imePadding()
                .padding(
                    start = 12.dp, end = 12.dp,
                    top = 8.dp, bottom = 12.dp + navInsets.calculateBottomPadding(),
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = commentText,
                onValueChange = { commentText = it },
                placeholder = { Text("Add a comment…", color = Mocha.copy(alpha = 0.5f)) },
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BlushPink,
                    unfocusedBorderColor = BlushPink,
                    focusedContainerColor = PolaroidWhite,
                    unfocusedContainerColor = PolaroidWhite,
                ),
                singleLine = true,
                modifier = Modifier.weight(1f),
            )
            Spacer(Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (commentText.isBlank()) Mocha.copy(alpha = 0.2f) else Terracotta)
                    .clickable(enabled = commentText.isNotBlank()) {
                        appVm.addComment(photo.id, commentText)
                        commentText = ""
                    },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Send, "Send",
                    tint = Color.White,
                )
            }
        }
    }
}
