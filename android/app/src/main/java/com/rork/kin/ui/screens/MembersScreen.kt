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
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rork.kin.ui.state.AppViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rork.kin.data.Role
import com.rork.kin.ui.components.Avatar
import com.rork.kin.ui.components.KinScreenHeader
import com.rork.kin.ui.components.KinTagline
import com.rork.kin.ui.theme.BlushPink
import com.rork.kin.ui.theme.CreamPaper
import com.rork.kin.ui.theme.InkBrown
import com.rork.kin.ui.theme.Mocha
import com.rork.kin.ui.theme.PolaroidWhite
import com.rork.kin.ui.theme.SageMist
import com.rork.kin.ui.theme.Terracotta
import com.rork.kin.ui.theme.WashiTan

@Composable
fun MembersScreen(
    appVm: AppViewModel,
    onBack: () -> Unit,
    onInvite: () -> Unit,
) {
    val state by appVm.state.collectAsStateWithLifecycle()
    val members = state.members
    val family = state.family
    val currentUserId = state.currentUser?.id
    val statusInsets = WindowInsets.statusBars.asPaddingValues()
    val navInsets = WindowInsets.navigationBars.asPaddingValues()

    Box(modifier = Modifier.fillMaxSize().background(CreamPaper)) {
        LazyColumn(
            contentPadding = PaddingValues(
                top = statusInsets.calculateTopPadding(),
                bottom = 24.dp + navInsets.calculateBottomPadding(),
            ),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item {
                KinScreenHeader(
                    title = family?.name ?: "Your family",
                    subtitle = when {
                        members.isEmpty() -> "No one has joined yet."
                        members.size == 1 -> "It's just you for now."
                        else -> "${members.size} members · since ${family?.createdAt?.year ?: ""}"
                    },
                    tagline = "the people who feel like home",
                    onBack = onBack,
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                )
            }
            item {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(PolaroidWhite)
                        .clickable(onClick = onInvite)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Terracotta.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(Icons.Filled.PersonAdd, null, tint = Terracotta)
                    }
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text(
                            "Invite a family member",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = InkBrown,
                        )
                        Text(
                            if (family?.inviteCode?.isNotBlank() == true) "Code: ${family.inviteCode}"
                            else "Share a code or QR with your family",
                            style = MaterialTheme.typography.bodySmall,
                            color = Mocha,
                        )
                    }
                }
            }
            if (members.size <= 1) {
                item {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 20.dp, vertical = 8.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(PolaroidWhite)
                            .padding(20.dp),
                    ) {
                        Text(
                            "It's just you for now.",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = InkBrown,
                        )
                        Spacer(Modifier.padding(top = 4.dp))
                        Text(
                            "Send the invite above so the rest of the family can show up here.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Mocha,
                        )
                    }
                }
            }
            items(members, key = { it.id }) { m ->
                Row(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(PolaroidWhite)
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Avatar(m, size = 48.dp)
                    Spacer(Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                m.name,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = InkBrown,
                            )
                            Spacer(Modifier.width(6.dp))
                            if (m.id == currentUserId) {
                                Text(
                                    "(you)",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Mocha,
                                )
                            }
                        }
                        Text(m.relationship, style = MaterialTheme.typography.bodySmall, color = Mocha)
                    }
                    RoleBadge(m.role)
                }
            }
        }
    }
}

@Composable
private fun RoleBadge(role: Role) {
    val (label, color) = when (role) {
        Role.Admin -> "Admin" to Terracotta
        Role.Member -> "Member" to WashiTan
        Role.Viewer -> "Kid" to SageMist
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(color.copy(alpha = 0.18f))
            .padding(horizontal = 10.dp, vertical = 6.dp),
    ) {
        Text(label, color = color, style = MaterialTheme.typography.labelSmall)
    }
}

// suppress unused warning
@Suppress("unused") private val _bp = BlushPink
@Suppress("unused") private val _c = Color.Black
