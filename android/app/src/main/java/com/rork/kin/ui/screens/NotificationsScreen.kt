package com.rork.kin.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HistoryEdu
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
import com.rork.kin.data.NotifKind
import com.rork.kin.ui.components.KinScreenHeader
import com.rork.kin.ui.theme.BlushPink
import com.rork.kin.ui.theme.CreamPaper
import com.rork.kin.ui.theme.InkBrown
import com.rork.kin.ui.theme.Mocha
import com.rork.kin.ui.theme.PolaroidWhite
import com.rork.kin.ui.theme.SageMist
import com.rork.kin.ui.theme.Terracotta
import com.rork.kin.ui.theme.WashiTan
import java.time.Duration
import java.time.LocalDateTime

@Composable
fun NotificationsScreen(appVm: AppViewModel, onBack: () -> Unit) {
    val state by appVm.state.collectAsStateWithLifecycle()
    val notifications = state.notifications
    val statusInsets = WindowInsets.statusBars.asPaddingValues()
    val navInsets = WindowInsets.navigationBars.asPaddingValues()

    Box(Modifier.fillMaxSize().background(CreamPaper)) {
        LazyColumn(
            contentPadding = PaddingValues(
                top = statusInsets.calculateTopPadding(),
                bottom = 24.dp + navInsets.calculateBottomPadding(),
            ),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item {
                KinScreenHeader(
                    title = "Quiet little updates",
                    tagline = "only the family, only what matters",
                    onBack = onBack,
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                )
            }
            if (notifications.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 20.dp, vertical = 12.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(PolaroidWhite)
                            .padding(24.dp),
                    ) {
                        Text(
                            "All quiet.",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = InkBrown,
                        )
                        Spacer(Modifier.size(6.dp))
                        Text(
                            "We'll let you know when family shares something, comments, or sends a heart.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Mocha,
                        )
                    }
                }
            }
            items(notifications, key = { it.id }) { n ->
                val actor = appVm.memberById(n.actorId)
                Row(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (!n.read) PolaroidWhite else BlushPink.copy(alpha = 0.5f))
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val (icon, color) = when (n.icon) {
                        NotifKind.NewPhoto -> Icons.Filled.AddAPhoto to Terracotta
                        NotifKind.Comment -> Icons.Filled.ChatBubble to WashiTan
                        NotifKind.Like -> Icons.Filled.Favorite to Terracotta
                        NotifKind.NewMember -> Icons.Filled.PersonAdd to SageMist
                        NotifKind.Memory -> Icons.Filled.HistoryEdu to WashiTan
                    }
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(color.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(icon, null, tint = color)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        val text = "${actor.name} ${n.text}"
                        Text(
                            text,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (!n.read) FontWeight.SemiBold else FontWeight.Normal,
                            ),
                            color = InkBrown,
                        )
                        Text(
                            relativeTime(n.createdAt),
                            style = MaterialTheme.typography.labelSmall,
                            color = Mocha,
                        )
                    }
                    if (!n.read) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Terracotta),
                        )
                    }
                }
            }
        }
    }
}

private fun relativeTime(t: LocalDateTime): String {
    val d = Duration.between(t, LocalDateTime.now())
    return when {
        d.toMinutes() < 60 -> "${d.toMinutes()}m ago"
        d.toHours() < 24 -> "${d.toHours()}h ago"
        d.toDays() < 7 -> "${d.toDays()}d ago"
        else -> "${d.toDays() / 7}w ago"
    }
}

@Suppress("unused") private val _c = Color.Black
