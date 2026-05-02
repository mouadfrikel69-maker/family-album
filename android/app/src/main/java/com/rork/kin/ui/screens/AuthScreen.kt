package com.rork.kin.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Favorite
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.rork.kin.ui.components.PaperBackground
import com.rork.kin.ui.theme.BlushPink
import com.rork.kin.ui.theme.CaptionScript
import com.rork.kin.ui.theme.DustyRose
import com.rork.kin.ui.theme.InkBrown
import com.rork.kin.ui.theme.Mocha
import com.rork.kin.ui.theme.PolaroidWhite
import com.rork.kin.ui.theme.SageMist
import com.rork.kin.ui.theme.Terracotta
import com.rork.kin.ui.theme.WashiTan

@Composable
fun AuthScreen(onContinue: () -> Unit) {
    var email by remember { mutableStateOf("") }
    val statusInsets = WindowInsets.statusBars.asPaddingValues()
    val navInsets = WindowInsets.navigationBars.asPaddingValues()
    val scroll = rememberScrollState()

    PaperBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scroll)
                .padding(top = statusInsets.calculateTopPadding()),
        ) {
            // ---- HERO: stacked polaroids of family ----
            HeroPolaroidStack(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
            )

            Spacer(Modifier.height(8.dp))

            // ---- WORDMARK ----
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .shadow(4.dp, CircleShape)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(Terracotta, DustyRose),
                                ),
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            "k",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontStyle = FontStyle.Italic,
                            ),
                            color = Color.White,
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "kin",
                        style = MaterialTheme.typography.headlineLarge,
                        color = InkBrown,
                    )
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    "Welcome home.",
                    style = MaterialTheme.typography.displayLarge,
                    color = InkBrown,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "A quiet, private place for the people\nyou love most.",
                    style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 22.sp),
                    color = Mocha,
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(28.dp))

                // ---- EMAIL CARD ----
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(12.dp, RoundedCornerShape(24.dp))
                        .clip(RoundedCornerShape(24.dp))
                        .background(PolaroidWhite)
                        .border(1.dp, BlushPink.copy(alpha = 0.6f), RoundedCornerShape(24.dp))
                        .padding(horizontal = 18.dp, vertical = 20.dp),
                ) {
                    Text(
                        "your email",
                        style = MaterialTheme.typography.labelMedium,
                        color = Mocha.copy(alpha = 0.7f),
                        modifier = Modifier.padding(start = 4.dp, bottom = 6.dp),
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = {
                            Text(
                                "you@home.com",
                                color = Mocha.copy(alpha = 0.45f),
                            )
                        },
                        leadingIcon = {
                            Icon(Icons.Filled.AlternateEmail, null, tint = Terracotta)
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Terracotta.copy(alpha = 0.6f),
                            unfocusedBorderColor = BlushPink,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                        ),
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Spacer(Modifier.height(14.dp))

                    PrimaryContinueButton(onClick = onContinue)
                }

                Spacer(Modifier.height(22.dp))

                // ---- DIVIDER ----
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(BlushPink),
                    )
                    Text(
                        "  or continue with  ",
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

                Spacer(Modifier.height(16.dp))

                // ---- SOCIAL ROW ----
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    SocialChip(
                        label = "Apple",
                        bg = Color(0xFF1F1B16),
                        fg = Color.White,
                        modifier = Modifier.weight(1f),
                        onClick = onContinue,
                    )
                    SocialChip(
                        label = "Google",
                        bg = PolaroidWhite,
                        fg = InkBrown,
                        bordered = true,
                        modifier = Modifier.weight(1f),
                        onClick = onContinue,
                    )
                }

                Spacer(Modifier.height(28.dp))

                // ---- LITTLE PROMISE STRIP ----
                PromiseStrip()

                Spacer(Modifier.height(20.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(
                        Icons.Filled.Favorite,
                        null,
                        tint = Terracotta.copy(alpha = 0.8f),
                        modifier = Modifier.size(12.dp),
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "made for the people who feel like home",
                        style = CaptionScript.copy(fontSize = 13.sp),
                        color = Mocha,
                        textAlign = TextAlign.Center,
                    )
                }

                Spacer(
                    Modifier.height(
                        20.dp + navInsets.calculateBottomPadding(),
                    ),
                )
            }
        }
    }
}

// ---------- HERO ----------

@Composable
private fun HeroPolaroidStack(modifier: Modifier = Modifier) {
    val infinite = rememberInfiniteTransition(label = "heroFloat")
    val drift by infinite.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(4200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "drift",
    )

    BoxWithConstraints(
        modifier = modifier.height(300.dp),
        contentAlignment = Alignment.Center,
    ) {
        // back-left polaroid
        HeroPolaroid(
            url = "https://images.unsplash.com/photo-1602030638412-bb8dcc0bc8b0?w=600&q=80",
            caption = "us, last summer",
            rotation = -10f + drift * 0.6f,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = (-78).dp, y = 8.dp)
                .width(150.dp),
            tapeColor = SageMist,
        )
        // back-right polaroid
        HeroPolaroid(
            url = "https://images.unsplash.com/photo-1519689680058-324335c77eba?w=600&q=80",
            caption = "little hands",
            rotation = 9f - drift * 0.5f,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(x = 80.dp, y = 14.dp)
                .width(148.dp),
            tapeColor = DustyRose,
        )
        // front-center polaroid
        HeroPolaroid(
            url = "https://images.unsplash.com/photo-1544126592-807ade215a0b?w=700&q=80",
            caption = "home",
            rotation = -2f + drift * 0.3f,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-2).dp)
                .width(170.dp),
            tapeColor = WashiTan,
            elevated = true,
        )
    }
}

@Composable
private fun HeroPolaroid(
    url: String,
    caption: String,
    rotation: Float,
    modifier: Modifier = Modifier,
    tapeColor: Color = WashiTan,
    elevated: Boolean = false,
) {
    Box(modifier = modifier.rotate(rotation)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = if (elevated) 18.dp else 10.dp,
                    shape = RoundedCornerShape(4.dp),
                    clip = false,
                )
                .background(PolaroidWhite, RoundedCornerShape(4.dp))
                .padding(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFFEDE5DA)),
            ) {
                AsyncImage(
                    model = url,
                    contentDescription = caption,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
            }
            Text(
                caption,
                style = CaptionScript.copy(fontSize = 13.sp),
                color = InkBrown,
                modifier = Modifier.padding(top = 8.dp, start = 4.dp, bottom = 2.dp),
            )
        }
        // tape
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-6).dp)
                .size(width = 52.dp, height = 16.dp)
                .rotate(-4f)
                .background(tapeColor.copy(alpha = 0.78f), RoundedCornerShape(1.dp)),
        )
    }
}

// ---------- BUTTONS ----------

@Composable
private fun PrimaryContinueButton(onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.97f else 1f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = 380f),
        label = "btnScale",
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .shadow(10.dp, RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFFD17A5C), Terracotta, Color(0xFFB35A3D)),
                ),
            )
            .clickable(
                interactionSource = interaction,
                indication = null,
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
                "Continue",
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 16.sp),
                color = Color.White,
            )
            Spacer(Modifier.width(8.dp))
            Icon(
                Icons.Filled.ArrowForward,
                null,
                tint = Color.White,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

@Composable
private fun SocialChip(
    label: String,
    bg: Color,
    fg: Color,
    onClick: () -> Unit,
    bordered: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val interaction = remember { MutableInteractionSource() }
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 420f),
        label = "chipScale",
    )

    Box(
        modifier = modifier
            .height(52.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .shadow(if (bordered) 0.dp else 4.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(bg)
            .then(
                if (bordered) Modifier.border(
                    1.dp,
                    BlushPink,
                    RoundedCornerShape(16.dp),
                ) else Modifier,
            )
            .clickable(
                interactionSource = interaction,
                indication = null,
            ) {
                pressed = true
                onClick()
                pressed = false
            },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            label,
            color = fg,
            style = MaterialTheme.typography.titleMedium,
        )
    }
}

// ---------- PROMISE STRIP ----------

@Composable
private fun PromiseStrip() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(BlushPink.copy(alpha = 0.4f))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        PromiseDot("private", SageMist)
        PromiseDot("ad-free", DustyRose)
        PromiseDot("just family", WashiTan)
    }
}

@Composable
private fun PromiseDot(label: String, dot: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(end = 4.dp),
    ) {
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
