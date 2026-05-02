package com.rork.kin.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rork.kin.data.Member
import com.rork.kin.data.Photo
import com.rork.kin.ui.components.Avatar
import com.rork.kin.ui.components.KinPrimaryButton
import com.rork.kin.ui.components.KinTagline
import com.rork.kin.ui.components.KinWordmark
import com.rork.kin.ui.components.Polaroid
import com.rork.kin.ui.state.AppViewModel
import com.rork.kin.ui.theme.BlushPink
import com.rork.kin.ui.theme.InkBrown
import com.rork.kin.ui.theme.Mocha
import com.rork.kin.ui.theme.PolaroidWhite
import com.rork.kin.ui.theme.Terracotta
import com.rork.kin.ui.theme.WashiTan
import java.time.Duration
import java.time.LocalDateTime

@Composable
fun HomeScreen(
    appVm: AppViewModel,
    onOpenPhoto: (String) -> Unit,
    onOpenMenu: () -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenMembers: () -> Unit,
    contentPadding: PaddingValues,
) {
    val state by appVm.state.collectAsStateWithLifecycle()
    val photos = state.photos.sortedByDescending { it.createdAt }
    val familyName = state.family?.name ?: "Your family"

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = 8.dp + contentPadding.calculateTopPadding(),
            bottom = 100.dp + contentPadding.calculateBottomPadding(),
        ),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        item { TopBar(familyName, onOpenMenu, onOpenNotifications) }
        if (state.members.isNotEmpty()) {
            item {
                FamilyStrip(
                    members = state.members,
                    currentUserId = state.currentUser?.id,
                    onMemberClick = { onOpenMembers() },
                )
            }
        }
        if (photos.isEmpty()) {
            item { EmptyFeed(onInvite = onOpenMembers) }
        } else {
            item { GreetingHeader(photos.size) }
            item {
                KinTagline(
                    "made for the people who feel like home",
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
                )
            }
            items(items = photos, key = { it.id }) { photo ->
                val author = appVm.memberById(photo.authorId)
                FeedPolaroid(
                    photo = photo,
                    author = author,
                    currentUserId = state.currentUser?.id,
                    resolveMember = { appVm.memberById(it) },
                    onLike = { appVm.toggleLike(photo.id) },
                    onClick = { onOpenPhoto(photo.id) },
                )
            }
        }
    }
}

@Composable
private fun EmptyFeed(onInvite: () -> Unit) {
    com.rork.kin.ui.components.KinCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        contentPadding = 24.dp,
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(BlushPink),
            contentAlignment = Alignment.Center,
        ) {
            Text("\uD83C\uDF3F", style = MaterialTheme.typography.displaySmall)
        }
        Spacer(Modifier.height(14.dp))
        Text(
            "A blank page, waiting.",
            style = MaterialTheme.typography.headlineSmall,
            color = InkBrown,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "Tap the + below to add your first photo, or invite your family so they can share too.",
            style = MaterialTheme.typography.bodyMedium,
            color = Mocha,
        )
        Spacer(Modifier.height(16.dp))
        KinPrimaryButton(
            label = "Invite family",
            onClick = onInvite,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(14.dp))
        com.rork.kin.ui.components.KinPromiseStrip()
    }
}

@Composable
private fun TopBar(familyName: String, onMenu: () -> Unit, onNotifs: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onMenu) {
            Icon(Icons.Outlined.Menu, "Menu", tint = InkBrown)
        }
        Spacer(Modifier.width(2.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                KinWordmark(showLabel = false, badgeSize = 22.dp)
                Spacer(Modifier.width(8.dp))
                Text(familyName.lowercase(), style = MaterialTheme.typography.labelMedium, color = Mocha)
            }
            Text(
                "Today",
                style = MaterialTheme.typography.headlineMedium,
                color = InkBrown,
            )
        }
        IconButton(onClick = onNotifs) {
            Box {
                Icon(Icons.Filled.Notifications, "Notifications", tint = InkBrown)
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Terracotta),
                )
            }
        }
    }
}

@Composable
private fun GreetingHeader(count: Int) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text(
            when (count) {
                1 -> "One little moment."
                else -> "$count little moments."
            },
            style = MaterialTheme.typography.displaySmall,
            color = InkBrown,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            "Tap a polaroid to read the story behind it.",
            style = MaterialTheme.typography.bodyMedium,
            color = Mocha,
        )
    }
}

@Composable
private fun FamilyStrip(
    members: List<Member>,
    currentUserId: String?,
    onMemberClick: () -> Unit,
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(items = members) { m ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onMemberClick() }
                    .padding(6.dp),
            ) {
                Box {
                    Avatar(member = m, size = 56.dp)
                    if (m.id == currentUserId) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(WashiTan),
                        )
                    }
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    if (m.id == currentUserId) "You" else m.relationship.ifBlank { m.name },
                    style = MaterialTheme.typography.labelMedium,
                    color = Mocha,
                )
            }
        }
    }
}

@Composable
private fun FeedPolaroid(
    photo: Photo,
    author: Member,
    currentUserId: String?,
    resolveMember: (String) -> Member,
    onLike: () -> Unit,
    onClick: () -> Unit,
) {
    val rotation = remember(photo.id) {
        ((photo.id.hashCode() % 7) - 3).toFloat()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Avatar(member = author, size = 36.dp)
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        author.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = InkBrown,
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "· ${author.relationship}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Mocha,
                    )
                }
                Text(
                    text = relativeTime(photo.createdAt) +
                            (photo.location?.let { " · $it" } ?: ""),
                    style = MaterialTheme.typography.bodySmall,
                    color = Mocha,
                )
            }
        }
        Spacer(Modifier.height(14.dp))
        Polaroid(
            imageUrl = photo.url,
            caption = photo.caption,
            rotation = rotation,
            showTape = (photo.id.hashCode() % 3) == 0,
            onClick = onClick,
            modifier = Modifier.padding(horizontal = 6.dp),
        )
        Spacer(Modifier.height(12.dp))
        ReactionRow(
            photo = photo,
            currentUserId = currentUserId,
            resolveMember = resolveMember,
            onLike = onLike,
            onComment = onClick,
        )
    }
}

@Composable
private fun ReactionRow(
    photo: Photo,
    currentUserId: String?,
    resolveMember: (String) -> Member,
    onLike: () -> Unit,
    onComment: () -> Unit,
) {
    val liked = currentUserId != null && currentUserId in photo.likedBy
    val scale by animateFloatAsState(
        targetValue = if (liked) 1.15f else 1f,
        animationSpec = spring(dampingRatio = 0.45f, stiffness = 700f),
        label = "heart",
    )
    Row(
        modifier = Modifier.fillMaxWidth().padding(start = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .clickable(onClick = onLike)
                .padding(horizontal = 10.dp, vertical = 6.dp),
        ) {
            Icon(
                imageVector = if (liked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                contentDescription = "Heart",
                tint = if (liked) Terracotta else Mocha,
                modifier = Modifier.graphicsLayer { scaleX = scale; scaleY = scale },
            )
            Spacer(Modifier.width(6.dp))
            Text(
                photo.likedBy.size.toString(),
                color = if (liked) Terracotta else Mocha,
                style = MaterialTheme.typography.labelLarge,
            )
        }
        Spacer(Modifier.width(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .clickable(onClick = onComment)
                .padding(horizontal = 10.dp, vertical = 6.dp),
        ) {
            Icon(Icons.Filled.ChatBubbleOutline, "Comments", tint = Mocha)
            Spacer(Modifier.width(6.dp))
            Text(photo.comments.size.toString(), color = Mocha, style = MaterialTheme.typography.labelLarge)
        }
        Spacer(Modifier.weight(1f))
        if (photo.taggedMemberIds.isNotEmpty()) {
            Row {
                photo.taggedMemberIds.take(2).forEach { id ->
                    Avatar(
                        resolveMember(id),
                        size = 24.dp,
                        modifier = Modifier.padding(start = 2.dp),
                    )
                }
            }
        }
    }
    if (photo.comments.isNotEmpty()) {
        val latest = photo.comments.last()
        val author = resolveMember(latest.authorId)
        Row(
            modifier = Modifier
                .padding(top = 8.dp, start = 4.dp, end = 4.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(PolaroidWhite)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "${author.name}: ",
                style = MaterialTheme.typography.titleSmall,
                color = InkBrown,
            )
            Text(
                latest.text,
                style = MaterialTheme.typography.bodyMedium,
                color = Mocha,
                maxLines = 1,
            )
        }
    }
}

private fun relativeTime(t: LocalDateTime): String {
    val d = Duration.between(t, LocalDateTime.now())
    return when {
        d.toMinutes() < 1 -> "just now"
        d.toMinutes() < 60 -> "${d.toMinutes()}m"
        d.toHours() < 24 -> "${d.toHours()}h"
        d.toDays() < 7 -> "${d.toDays()}d"
        else -> "${d.toDays() / 7}w"
    }
}
