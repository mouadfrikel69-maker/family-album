package com.rork.kin.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddHome
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rork.kin.ui.components.KinPrimaryButton
import com.rork.kin.ui.components.KinTagline
import com.rork.kin.ui.components.KinWordmark
import com.rork.kin.ui.components.PaperBackground
import com.rork.kin.ui.theme.BlushPink
import com.rork.kin.ui.theme.DustyRose
import com.rork.kin.ui.theme.InkBrown
import com.rork.kin.ui.theme.Mocha
import com.rork.kin.ui.theme.PolaroidWhite
import com.rork.kin.ui.theme.Terracotta
import com.rork.kin.ui.theme.WashiTan

@Composable
fun CreateJoinFamilyScreen(
    onCreate: (familyName: String) -> Unit,
    onJoin: (inviteCode: String) -> Unit,
) {
    var mode by remember { mutableStateOf<Mode?>(null) }
    var familyName by remember { mutableStateOf("") }
    var inviteCode by remember { mutableStateOf("") }
    val statusInsets = WindowInsets.statusBars.asPaddingValues()
    val navInsets = WindowInsets.navigationBars.asPaddingValues()

    PaperBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = statusInsets.calculateTopPadding())
                .padding(horizontal = 24.dp),
        ) {
            Spacer(Modifier.height(28.dp))
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) { KinWordmark(showLabel = true, badgeSize = 32.dp) }
            Spacer(Modifier.height(24.dp))
            Text(
                "One last step.",
                style = MaterialTheme.typography.displayMedium,
                color = InkBrown,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "Start your family circle, or join one.",
                style = MaterialTheme.typography.bodyLarge,
                color = Mocha,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(10.dp))
            KinTagline("the people who feel like home")
            Spacer(Modifier.height(28.dp))

            ChoiceCard(
                icon = Icons.Filled.AddHome,
                title = "Start a new family",
                subtitle = "Create the album. Invite the rest later.",
                accent = Terracotta,
                selected = mode == Mode.Create,
                onClick = { mode = Mode.Create },
            )
            Spacer(Modifier.height(14.dp))
            ChoiceCard(
                icon = Icons.Filled.MailOutline,
                title = "I have an invite code",
                subtitle = "Join the family album you were invited to.",
                accent = WashiTan,
                selected = mode == Mode.Join,
                onClick = { mode = Mode.Join },
            )

            Spacer(Modifier.height(28.dp))

            when (mode) {
                Mode.Create -> {
                    Text("Family name", style = MaterialTheme.typography.titleSmall, color = Mocha)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = familyName,
                        onValueChange = { familyName = it },
                        placeholder = { Text("e.g. The Hayes Family", color = Mocha.copy(alpha = 0.5f)) },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = textFieldColors(),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                Mode.Join -> {
                    Text("Invite code", style = MaterialTheme.typography.titleSmall, color = Mocha)
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = inviteCode,
                        onValueChange = { inviteCode = it.uppercase() },
                        placeholder = { Text("HAYES-2026", color = Mocha.copy(alpha = 0.5f)) },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = textFieldColors(),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                null -> Unit
            }

            Spacer(Modifier.weight(1f))

            val canSubmit = when (mode) {
                Mode.Create -> familyName.isNotBlank()
                Mode.Join -> inviteCode.isNotBlank()
                null -> false
            }
            KinPrimaryButton(
                label = when (mode) {
                    Mode.Join -> "Join family"
                    Mode.Create -> "Create family"
                    null -> "Continue"
                },
                onClick = {
                    when (mode) {
                        Mode.Create -> onCreate(familyName)
                        Mode.Join -> onJoin(inviteCode)
                        null -> Unit
                    }
                },
                enabled = canSubmit,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(20.dp + navInsets.calculateBottomPadding()))
        }
    }
}

private enum class Mode { Create, Join }

@Composable
private fun ChoiceCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    accent: Color,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(PolaroidWhite)
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = if (selected) accent else BlushPink,
                shape = RoundedCornerShape(20.dp),
            )
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(accent.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, null, tint = accent)
        }
        Spacer(Modifier.size(14.dp))
        Column(verticalArrangement = Arrangement.Center) {
            Text(title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold), color = InkBrown)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = Mocha)
        }
    }
}

@Composable
private fun textFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = Terracotta,
    unfocusedBorderColor = BlushPink,
    focusedContainerColor = PolaroidWhite,
    unfocusedContainerColor = PolaroidWhite,
)
