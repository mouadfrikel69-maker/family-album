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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rork.kin.ui.components.Avatar
import com.rork.kin.ui.components.KinCard
import com.rork.kin.ui.components.KinPrimaryButton
import com.rork.kin.ui.components.KinTagline
import com.rork.kin.ui.components.KinWordmark
import com.rork.kin.ui.state.AppViewModel
import com.rork.kin.ui.theme.BlushPink
import com.rork.kin.ui.theme.InkBrown
import com.rork.kin.ui.theme.Mocha
import com.rork.kin.ui.theme.PolaroidWhite
import com.rork.kin.ui.theme.SageMist
import com.rork.kin.ui.theme.Terracotta
import com.rork.kin.ui.theme.WashiTan

@Composable
fun ProfileScreen(
    appVm: AppViewModel,
    onSignOut: () -> Unit,
    onOpenInvite: () -> Unit,
    onOpenSettings: () -> Unit,
    contentPadding: PaddingValues,
) {
    val state by appVm.state.collectAsStateWithLifecycle()
    val me = state.currentUser ?: return
    val mine = state.photos.count { it.authorId == me.id }
    val total = state.photos.size
    val familyName = state.family?.name ?: "Your family"

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            top = 8.dp + contentPadding.calculateTopPadding(),
            bottom = 100.dp + contentPadding.calculateBottomPadding(),
        ),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
            ) {
                KinWordmark(showLabel = true, badgeSize = 28.dp)
                Spacer(Modifier.height(18.dp))
                Box(
                    modifier = Modifier
                        .size(112.dp)
                        .clip(CircleShape)
                        .background(BlushPink),
                    contentAlignment = Alignment.Center,
                ) {
                    Avatar(member = me, size = 104.dp)
                }
                Spacer(Modifier.height(14.dp))
                Text(me.name, style = MaterialTheme.typography.displaySmall, color = InkBrown)
                Text(
                    if (me.relationship.isBlank()) familyName
                    else "${me.relationship} · $familyName",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Mocha,
                )
                Spacer(Modifier.height(8.dp))
                KinTagline("the people who feel like home")
            }
        }
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                StatCard("Your photos", mine.toString(), Terracotta, modifier = Modifier.weight(1f))
                StatCard("Family total", total.toString(), WashiTan, modifier = Modifier.weight(1f))
                StatCard("Memories", "${appVm.memories().size}", SageMist, modifier = Modifier.weight(1f))
            }
        }
        item {
            KinCard(
                modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth(),
                contentPadding = 0.dp,
            ) {
                MenuRow(Icons.Filled.PersonAdd, "Invite family", "Send a code or share a link", onOpenInvite)
                Divider()
                MenuRow(Icons.Filled.Notifications, "Notifications", "Choose what to be told about", onOpenSettings)
                Divider()
                MenuRow(Icons.Filled.Shield, "Privacy", "Only your family. Always.", onOpenSettings)
                Divider()
                MenuRow(Icons.Filled.Storage, "Storage", "240 of 5,000 photos used", onOpenSettings)
                Divider()
                MenuRow(Icons.Filled.Settings, "Settings", "Theme, account, language", onOpenSettings)
            }
        }
        item {
            KinPrimaryButton(
                label = "Sign out",
                onClick = onSignOut,
                icon = Icons.Filled.Logout,
                modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth(),
            )
        }
        item {
            Text(
                "Made with love.\nMemories live longer when shared.",
                color = Mocha,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, accent: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .shadow(6.dp, RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp))
            .background(PolaroidWhite)
            .border(1.dp, BlushPink.copy(alpha = 0.6f), RoundedCornerShape(18.dp))
            .padding(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(accent),
        )
        Spacer(Modifier.height(6.dp))
        Text(
            value,
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.SemiBold),
            color = InkBrown,
        )
        Text(label, style = MaterialTheme.typography.labelSmall, color = Mocha)
    }
}

@Composable
private fun MenuRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(BlushPink),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, null, tint = Terracotta)
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = InkBrown)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Mocha)
        }
        Icon(
            Icons.AutoMirrored.Filled.ArrowForwardIos, null,
            tint = Mocha, modifier = Modifier.size(14.dp),
        )
    }
}

@Composable
private fun Divider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(1.dp)
            .background(BlushPink),
    )
}
