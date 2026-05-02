package com.rork.kin.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rork.kin.ui.theme.BlushPink
import com.rork.kin.ui.theme.CaptionScript
import com.rork.kin.ui.theme.DustyRose
import com.rork.kin.ui.theme.InkBrown
import com.rork.kin.ui.theme.Mocha
import com.rork.kin.ui.theme.PolaroidWhite
import com.rork.kin.ui.theme.SageMist
import com.rork.kin.ui.theme.Terracotta
import com.rork.kin.ui.theme.WashiTan

/** The little "k + kin" wordmark from the auth screen. */
@Composable
fun KinWordmark(
    modifier: Modifier = Modifier,
    showLabel: Boolean = true,
    badgeSize: Dp = 32.dp,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(badgeSize)
                .shadow(4.dp, CircleShape)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(listOf(Terracotta, DustyRose)),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                "k",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontStyle = FontStyle.Italic,
                ),
                color = Color.White,
            )
        }
        if (showLabel) {
            Spacer(Modifier.width(8.dp))
            Text(
                "kin",
                style = MaterialTheme.typography.headlineMedium,
                color = InkBrown,
            )
        }
    }
}

/**
 * Soft polaroid-paper card matching the auth email card.
 * `bordered = true` adds the BlushPink hairline outline.
 */
@Composable
fun KinCard(
    modifier: Modifier = Modifier,
    bordered: Boolean = true,
    elevation: Dp = 8.dp,
    cornerRadius: Dp = 22.dp,
    contentPadding: Dp = 18.dp,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .shadow(elevation, RoundedCornerShape(cornerRadius))
            .clip(RoundedCornerShape(cornerRadius))
            .background(PolaroidWhite)
            .then(
                if (bordered) Modifier.border(
                    1.dp,
                    BlushPink.copy(alpha = 0.6f),
                    RoundedCornerShape(cornerRadius),
                ) else Modifier,
            )
            .padding(contentPadding),
        content = content,
    )
}

/** The terracotta-gradient primary button with press scaling. */
@Composable
fun KinPrimaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = Icons.Filled.ArrowForward,
) {
    val interaction = remember { MutableInteractionSource() }
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = 380f),
        label = "kinBtnScale",
    )
    val brush = if (enabled) {
        Brush.horizontalGradient(
            listOf(Color(0xFFD17A5C), Terracotta, Color(0xFFB35A3D)),
        )
    } else {
        Brush.horizontalGradient(
            listOf(DustyRose.copy(alpha = 0.5f), DustyRose.copy(alpha = 0.5f)),
        )
    }
    Box(
        modifier = modifier
            .height(56.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .shadow(if (enabled) 10.dp else 0.dp, RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp))
            .background(brush)
            .clickable(
                interactionSource = interaction,
                indication = null,
                enabled = enabled,
            ) {
                pressed = true
                onClick()
                pressed = false
            },
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                label,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp),
                color = Color.White,
            )
            if (icon != null) {
                Spacer(Modifier.width(8.dp))
                Icon(icon, null, tint = Color.White, modifier = Modifier.size(18.dp))
            }
        }
    }
}

/** Soft secondary chip-style button. */
@Composable
fun KinSecondaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
) {
    Row(
        modifier = modifier
            .height(52.dp)
            .shadow(0.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(PolaroidWhite)
            .border(1.dp, BlushPink, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        if (icon != null) {
            Icon(icon, null, tint = Terracotta, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(label, color = InkBrown, style = MaterialTheme.typography.titleMedium)
    }
}

/**
 * Page header with optional back arrow, big serif title, italic-script tagline and centered wordmark.
 */
@Composable
fun KinScreenHeader(
    title: String,
    subtitle: String? = null,
    tagline: String? = null,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    centered: Boolean = true,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = if (centered) Alignment.CenterHorizontally else Alignment.Start,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (onBack != null) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(PolaroidWhite)
                        .border(1.dp, BlushPink, CircleShape)
                        .clickable(onClick = onBack),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        "Back",
                        tint = InkBrown,
                        modifier = Modifier.size(18.dp),
                    )
                }
            } else {
                Spacer(Modifier.size(38.dp))
            }
            Spacer(Modifier.weight(1f))
            KinWordmark(showLabel = true, badgeSize = 30.dp)
            Spacer(Modifier.weight(1f))
            Spacer(Modifier.size(38.dp))
        }
        Spacer(Modifier.height(18.dp))
        Text(
            title,
            style = MaterialTheme.typography.displayMedium,
            color = InkBrown,
            textAlign = if (centered) TextAlign.Center else TextAlign.Start,
            modifier = Modifier.padding(horizontal = 20.dp),
        )
        if (subtitle != null) {
            Spacer(Modifier.height(6.dp))
            Text(
                subtitle,
                style = MaterialTheme.typography.bodyLarge,
                color = Mocha,
                textAlign = if (centered) TextAlign.Center else TextAlign.Start,
                modifier = Modifier.padding(horizontal = 24.dp),
            )
        }
        if (tagline != null) {
            Spacer(Modifier.height(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 24.dp),
            ) {
                Icon(
                    Icons.Filled.Favorite,
                    null,
                    tint = Terracotta.copy(alpha = 0.8f),
                    modifier = Modifier.size(11.dp),
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    tagline,
                    style = CaptionScript.copy(fontSize = 13.sp),
                    color = Mocha,
                )
            }
        }
    }
}

/** "—  label  —" thin-line divider used on the auth screen. */
@Composable
fun KinThinDivider(label: String? = null, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth(),
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(BlushPink),
        )
        if (label != null) {
            Text(
                "  $label  ",
                color = Mocha.copy(alpha = 0.7f),
                style = MaterialTheme.typography.labelSmall,
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(1.dp)
                    .background(BlushPink),
            )
        }
    }
}

/** Rounded promise strip with three colored dots — the "private · ad-free · just family" style. */
@Composable
fun KinPromiseStrip(
    items: List<Pair<String, Color>> = listOf(
        "private" to SageMist,
        "ad-free" to DustyRose,
        "just family" to WashiTan,
    ),
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(BlushPink.copy(alpha = 0.4f))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items.forEach { (label, dot) ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(dot),
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    label,
                    style = MaterialTheme.typography.labelSmall,
                    color = Mocha,
                )
            }
        }
    }
}

/** Italic script tagline with a heart icon — "made for the people who feel like home". */
@Composable
fun KinTagline(text: String, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxWidth(),
    ) {
        Icon(
            Icons.Filled.Favorite,
            null,
            tint = Terracotta.copy(alpha = 0.8f),
            modifier = Modifier.size(12.dp),
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text,
            style = CaptionScript.copy(fontSize = 13.sp),
            color = Mocha,
            textAlign = TextAlign.Center,
        )
    }
}
