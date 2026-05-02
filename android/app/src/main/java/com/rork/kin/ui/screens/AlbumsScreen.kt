package com.rork.kin.ui.screens

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.rork.kin.data.Album
import com.rork.kin.ui.components.KinCard
import com.rork.kin.ui.components.KinPrimaryButton
import com.rork.kin.ui.components.KinTagline
import com.rork.kin.ui.components.KinWordmark
import com.rork.kin.ui.state.AppViewModel
import com.rork.kin.ui.theme.BlushPink
import com.rork.kin.ui.theme.InkBrown
import com.rork.kin.ui.theme.Mocha
import com.rork.kin.ui.theme.PolaroidWhite
import com.rork.kin.ui.theme.Terracotta

@Composable
fun AlbumsScreen(
    appVm: AppViewModel,
    onOpenAlbum: (String) -> Unit,
    onNewAlbum: () -> Unit,
    contentPadding: PaddingValues,
) {
    val state by appVm.state.collectAsStateWithLifecycle()

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp, end = 16.dp,
            top = 16.dp + contentPadding.calculateTopPadding(),
            bottom = 100.dp + contentPadding.calculateBottomPadding(),
        ),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    KinWordmark(showLabel = false, badgeSize = 26.dp)
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "the family album",
                        style = MaterialTheme.typography.labelMedium,
                        color = Mocha,
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text("Albums", style = MaterialTheme.typography.displayMedium, color = InkBrown)
                Spacer(Modifier.height(6.dp))
                Text(
                    "Bound by hand. Filled together.",
                    style = MaterialTheme.typography.bodyLarge, color = Mocha,
                )
                Spacer(Modifier.height(8.dp))
                KinTagline("a chapter for every season")
                Spacer(Modifier.height(16.dp))
            }
        }

        item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
            NewAlbumCard(onClick = onNewAlbum)
        }

        if (state.albums.isEmpty()) {
            item(span = { androidx.compose.foundation.lazy.grid.GridItemSpan(2) }) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(PolaroidWhite)
                        .padding(24.dp),
                ) {
                    Text(
                        "No albums yet.",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = InkBrown,
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Create your first album to start collecting memories together — a trip, a season, a milestone.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Mocha,
                    )
                }
            }
        } else {
            items(items = state.albums, key = { it.id }) { album ->
                val cover = state.photos.firstOrNull { it.id == album.coverPhotoId }
                AlbumCard(album, cover?.url, onClick = { onOpenAlbum(album.id) })
            }
        }
    }
}

@Composable
private fun NewAlbumCard(onClick: () -> Unit) {
    KinCard(modifier = Modifier.fillMaxWidth(), contentPadding = 18.dp) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .height(48.dp)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Terracotta.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.Add, null, tint = Terracotta)
            }
            Spacer(Modifier.padding(start = 14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Start a new album",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = InkBrown,
                )
                Text(
                    "Anyone in the family can add to it.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Mocha,
                )
            }
        }
        Spacer(Modifier.height(14.dp))
        KinPrimaryButton(
            label = "Begin an album",
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun AlbumCard(album: Album, coverUrl: String?, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(3.dp, RoundedCornerShape(18.dp), clip = false)
            .clip(RoundedCornerShape(18.dp))
            .background(PolaroidWhite)
            .clickable(onClick = onClick),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .background(BlushPink),
        ) {
            if (coverUrl != null) {
                AsyncImage(
                    model = coverUrl,
                    contentDescription = album.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
        ) {
            Text(
                album.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                ),
                color = InkBrown,
                maxLines = 1,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                album.dateRangeLabel,
                style = MaterialTheme.typography.bodySmall,
                color = Mocha,
                maxLines = 1,
            )
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color(album.accentColor)),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "${album.photoIds.size} photos",
                    style = MaterialTheme.typography.labelMedium,
                    color = Mocha,
                )
            }
        }
    }
}
