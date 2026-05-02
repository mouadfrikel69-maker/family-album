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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.unit.dp
import com.rork.kin.ui.components.KinScreenHeader
import com.rork.kin.ui.theme.BlushPink
import com.rork.kin.ui.theme.CreamPaper
import com.rork.kin.ui.theme.InkBrown
import com.rork.kin.ui.theme.Mocha
import com.rork.kin.ui.theme.PolaroidWhite
import com.rork.kin.ui.theme.Terracotta
import com.rork.kin.ui.theme.WashiTan

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val statusInsets = WindowInsets.statusBars.asPaddingValues()
    val navInsets = WindowInsets.navigationBars.asPaddingValues()
    var pushNew by remember { mutableStateOf(true) }
    var pushComments by remember { mutableStateOf(true) }
    var pushMemories by remember { mutableStateOf(true) }
    var largeText by remember { mutableStateOf(false) }
    var offlineCache by remember { mutableStateOf(true) }

    Box(Modifier.fillMaxSize().background(CreamPaper)) {
        LazyColumn(
            contentPadding = PaddingValues(
                top = statusInsets.calculateTopPadding(),
                bottom = 24.dp + navInsets.calculateBottomPadding(),
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                KinScreenHeader(
                    title = "Settings",
                    tagline = "the little dials that shape the album",
                    onBack = onBack,
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                )
            }

            item { SectionHeader("Notifications") }
            item { ToggleRow("New photos", "When the family adds something new.", pushNew) { pushNew = it } }
            item { ToggleRow("Comments & hearts", "Replies on your photos.", pushComments) { pushComments = it } }
            item { ToggleRow("On this day", "Memories from previous years.", pushMemories) { pushMemories = it } }

            item { SectionHeader("Reading") }
            item { ToggleRow("Larger text", "Easier on the eyes for grandparents.", largeText) { largeText = it } }

            item { SectionHeader("Offline") }
            item { ToggleRow("Cache recent photos", "Browse the album with no signal.", offlineCache) { offlineCache = it } }

            item { SectionHeader("Storage") }
            item {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(PolaroidWhite)
                        .padding(16.dp),
                ) {
                    Text("240 / 5,000 photos", style = MaterialTheme.typography.titleMedium, color = InkBrown)
                    Spacer(Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(50))
                            .background(BlushPink),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.048f)
                                .fillMaxSize()
                                .clip(RoundedCornerShape(50))
                                .background(Terracotta),
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Plenty of room for a few thousand more memories.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Mocha,
                    )
                }
            }
            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = WashiTan,
        modifier = Modifier.padding(start = 28.dp, top = 8.dp),
    )
}

@Composable
private fun ToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(PolaroidWhite)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = InkBrown)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Mocha)
        }
        Spacer(Modifier.width(12.dp))
        Switch(
            checked = checked,
            onCheckedChange = onChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Terracotta,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = BlushPink,
                uncheckedBorderColor = BlushPink,
            ),
        )
    }
}

@Suppress("unused") private val _x: Int = 0
