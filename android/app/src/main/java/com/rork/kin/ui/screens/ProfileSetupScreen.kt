package com.rork.kin.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
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

private val SUGGESTED_RELATIONSHIPS = listOf(
    "Mom", "Dad", "Grandma", "Grandpa", "Aunt", "Uncle",
    "Sister", "Brother", "Daughter", "Son", "Cousin", "Family",
)

@Composable
fun ProfileSetupScreen(
    onContinue: (name: String, relationship: String, avatarUri: String?) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var relationship by remember { mutableStateOf("") }
    var avatarUri by remember { mutableStateOf<Uri?>(null) }
    val statusInsets = WindowInsets.statusBars.asPaddingValues()
    val navInsets = WindowInsets.navigationBars.asPaddingValues()

    val pickAvatar = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia(),
    ) { uri -> if (uri != null) avatarUri = uri }

    PaperBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = statusInsets.calculateTopPadding())
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp + navInsets.calculateBottomPadding()),
        ) {
            Spacer(Modifier.height(24.dp))
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) { KinWordmark(showLabel = true, badgeSize = 32.dp) }
            Spacer(Modifier.height(20.dp))
            Text(
                "Tell us who you are.",
                style = MaterialTheme.typography.displayMedium,
                color = InkBrown,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "Just so your family knows who's sharing.",
                style = MaterialTheme.typography.bodyLarge,
                color = Mocha,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(8.dp))
            KinTagline("a small page on the family album")

            Spacer(Modifier.height(28.dp))

            // Avatar picker
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(112.dp)
                        .clip(CircleShape)
                        .background(BlushPink)
                        .border(2.dp, Terracotta.copy(alpha = 0.4f), CircleShape)
                        .clickable {
                            pickAvatar.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                            )
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    if (avatarUri != null) {
                        AsyncImage(
                            model = avatarUri,
                            contentDescription = "Your avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                        )
                    } else {
                        Icon(
                            Icons.Filled.AddAPhoto,
                            "Pick avatar",
                            tint = Terracotta,
                            modifier = Modifier.size(36.dp),
                        )
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Text(
                if (avatarUri == null) "Tap to add a photo (optional)" else "Tap to change photo",
                style = MaterialTheme.typography.labelMedium,
                color = Mocha,
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )

            Spacer(Modifier.height(28.dp))

            Text("Your name", style = MaterialTheme.typography.titleSmall, color = Mocha)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = { Text("e.g. Sarah", color = Mocha.copy(alpha = 0.5f)) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Terracotta,
                    unfocusedBorderColor = BlushPink,
                    focusedContainerColor = PolaroidWhite,
                    unfocusedContainerColor = PolaroidWhite,
                ),
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(20.dp))

            Text("Relationship", style = MaterialTheme.typography.titleSmall, color = Mocha)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = relationship,
                onValueChange = { relationship = it },
                placeholder = { Text("Mom, Dad, Grandma…", color = Mocha.copy(alpha = 0.5f)) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Terracotta,
                    unfocusedBorderColor = BlushPink,
                    focusedContainerColor = PolaroidWhite,
                    unfocusedContainerColor = PolaroidWhite,
                ),
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(12.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(SUGGESTED_RELATIONSHIPS) { tag ->
                    val selected = relationship.equals(tag, ignoreCase = true)
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(if (selected) Terracotta else PolaroidWhite)
                            .border(
                                1.dp,
                                if (selected) Terracotta else BlushPink,
                                RoundedCornerShape(50),
                            )
                            .clickable { relationship = tag }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            tag,
                            style = MaterialTheme.typography.labelLarge,
                            color = if (selected) Color.White else InkBrown,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }

            Spacer(Modifier.height(40.dp))

            KinPrimaryButton(
                label = "Continue",
                onClick = { onContinue(name, relationship, avatarUri?.toString()) },
                enabled = name.isNotBlank() && relationship.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}
