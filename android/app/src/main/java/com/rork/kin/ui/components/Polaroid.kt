package com.rork.kin.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.rork.kin.ui.theme.CaptionScript
import com.rork.kin.ui.theme.PolaroidWhite
import com.rork.kin.ui.theme.WashiTan

@Composable
fun Polaroid(
    imageUrl: String,
    caption: String? = null,
    rotation: Float = 0f,
    aspectRatio: Float = 0.85f,
    showTape: Boolean = false,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val interaction = remember { MutableInteractionSource() }
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "polaroidScale",
    )

    Box(
        modifier = modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .rotate(rotation),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(4.dp), clip = false)
                .background(PolaroidWhite, RoundedCornerShape(4.dp))
                .padding(10.dp)
                .then(
                    if (onClick != null) Modifier.clickable(
                        interactionSource = interaction,
                        indication = null,
                    ) {
                        pressed = true
                        onClick()
                        pressed = false
                    } else Modifier,
                ),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(aspectRatio)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFFEDE5DA)),
            ) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = caption,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            }
            if (caption != null) {
                Text(
                    text = caption,
                    style = CaptionScript,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .padding(top = 14.dp, bottom = 6.dp, start = 4.dp, end = 4.dp),
                )
            }
        }
        if (showTape) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .size(width = 64.dp, height = 18.dp)
                    .rotate(-3f)
                    .background(WashiTan.copy(alpha = 0.78f), RoundedCornerShape(1.dp)),
            )
        }
    }
}
