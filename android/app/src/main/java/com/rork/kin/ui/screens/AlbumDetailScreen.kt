package com.rork.kin.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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

@Composable
fun AlbumDetailScreen(
    appVm: AppViewModel,
    albumId: String,
    onBack: () -> Unit,
    onOpenPhoto: (String) -> Unit,
) {
    val state by appVm.state.collectAsStateWithLifecycle()
    val album = state.albums.firstOrNull { it.id == albumId } ?: return
    val members = state.members
    val photos = album.photoIds.mapNotNull { pid -> state.photos.firstOrNull { it.id == pid } }
    val cover = photos.firstOrNull { it.id == album.coverPhotoId } ?: photos.firstOrNull()
    val statusInsets = WindowInsets.statusBars.asPaddingValues()
    val navInsets = WindowInsets.navigationBars.asPaddingValues()

    Box(modifier = Modifier.fillMaxSize().background(CreamPaper)) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = statusInsets.calculateTopPadding(),
                bottom = 24.dp + navInsets.calculateBottomPadding(),
                start = 12.dp, end = 12.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item(span = { GridItemSpan(3) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(album.accentColor)),
                ) {
                    if (cover != null) {
                        AsyncImage(
                            model = cover.url,
                            contentDescription = album.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0x33000000), Color.Transparent, Color(0xCC000000)),
                                ),
                            ),
                    )
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, "Back",
                            tint = Color.White,
                        )
                    }
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(20.dp),
                    ) {
                        Text(
                            album.title,
                            style = MaterialTheme.typography.displaySmall,
                            color = Color.White,
                        )
                        Text(
                            "${album.dateRangeLabel} · ${photos.size} photos",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.85f),
                        )
                        Spacer(Modifier.height(10.dp))
                        Row {
                            members.take(4).forEach { m ->
                                Avatar(m, size = 26.dp, modifier = Modifier.padding(end = (-6).dp))
                            }
                        }
                    }
                }
            }
            item(span = { GridItemSpan(3) }) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        "Photos",
                        style = MaterialTheme.typography.titleMedium,
                        color = InkBrown,
                    )
                    Spacer(Modifier.weight(1f))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(PolaroidWhite)
                            .clickable { /* add */ }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                    ) {
                        Icon(Icons.Filled.Add, null, tint = InkBrown, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Add", style = MaterialTheme.typography.labelLarge, color = InkBrown)
                    }
                }
            }
            items(photos, key = { it.id }) { p ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(BlushPink)
                        .clickable { onOpenPhoto(p.id) },
                ) {
                    AsyncImage(
                        model = p.url,
                        contentDescription = p.caption,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
            if (photos.isEmpty()) {
                item(span = { GridItemSpan(3) }) {
                    Text(
                        "Empty album. Add the first photo.",
                        color = Mocha,
                        modifier = Modifier.padding(20.dp),
                    )
                }
            }
        }
    }
}
