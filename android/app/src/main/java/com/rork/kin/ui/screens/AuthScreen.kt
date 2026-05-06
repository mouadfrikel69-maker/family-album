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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import kotlinx.coroutines.launch
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

/**
 * Sign-in / create-account screen.
 *
 * The screen owns email + password input and the sign-in / sign-up toggle.
 * `submit` is the only way out of this screen — it returns null on success
 * (in which case `onAuthed` is fired) or an error message that is shown
 * inline. The previous "tap continue, skip auth" backdoor is gone.
 */
@Composable
fun AuthScreen(
    submit: suspend (email: String, password: String, isSignUp: Boolean) -> String?,
    onAuthed: () -> Unit,
    isDevAuthBypass: Boolean = false,
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var isSignUp by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val statusInsets = WindowInsets.statusBars.asPaddingValues()
    val navInsets = WindowInsets.navigationBars.asPaddingValues()
    val scroll = rememberScrollState()

    val canSubmit = !loading && email.isNotBlank() && password.isNotBlank()
    val onSubmit = {
        if (canSubmit) {
            error = null
            loading = true
            scope.launch {
                val msg = submit(email.trim(), password, isSignUp)
                loading = false
                if (msg == null) onAuthed() else error = msg
            }
        }
        Unit
    }

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

                if (isDevAuthBypass) {
                    Spacer(Modifier.height(16.dp))
                    DevBypassBanner()
                }

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
                        onValueChange = { email = it; error = null },
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
                        enabled = !loading,
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

                    Spacer(Modifier.height(10.dp))

                    Text(
                        "your password",
                        style = MaterialTheme.typography.labelMedium,
                        color = Mocha.copy(alpha = 0.7f),
                        modifier = Modifier.padding(start = 4.dp, bottom = 6.dp),
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; error = null },
                        placeholder = {
                            Text(
                                if (isSignUp) "at least 8 characters" else "your password",
                                color = Mocha.copy(alpha = 0.45f),
                            )
                        },
                        leadingIcon = {
                            Icon(Icons.Filled.Lock, null, tint = Terracotta)
                        },
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    if (showPassword) Icons.Filled.VisibilityOff
                                    else Icons.Filled.Visibility,
                                    contentDescription = if (showPassword) "Hide password" else "Show password",
                                    tint = Mocha.copy(alpha = 0.7f),
                                )
                            }
                        },
                        visualTransformation = if (showPassword) VisualTransformation.None
                        else PasswordVisualTransformation(),
                        singleLine = true,
                        enabled = !loading,
                        shape = RoundedCornerShape(16.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Terracotta.copy(alpha = 0.6f),
                            unfocusedBorderColor = BlushPink,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                        ),
                        modifier = Modifier.fillMaxWidth(),
                    )

                    if (error != null) {
                        Spacer(Modifier.height(10.dp))
                        Text(
                            error!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFB35A3D),
                            textAlign = TextAlign.Start,
                            modifier = Modifier.padding(horizontal = 4.dp),
                        )
                    }

                    Spacer(Modifier.height(14.dp))

                    PrimaryContinueButton(
                        label = if (isSignUp) "Create account" else "Sign in",
                        loading = loading,
                        enabled = canSubmit,
                        onClick = onSubmit,
                    )

                    Spacer(Modifier.height(10.dp))

                    SignUpToggle(
                        isSignUp = isSignUp,
                        onToggle = {
                            isSignUp = !isSignUp
                            error = null
                        },
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

    Box(
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
private fun PrimaryContinueButton(
    label: String = "Continue",
    loading: Boolean = false,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed && enabled) 0.97f else 1f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = 380f),
        label = "btnScale",
    )
    LaunchedEffect(loading) { if (!loading) pressed = false }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                alpha = if (enabled) 1f else 0.55f
            }
            .shadow(10.dp, RoundedCornerShape(18.dp))
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(Color(0xFFD17A5C), Terracotta, Color(0xFFB35A3D)),
                ),
            )
            .clickable(
                enabled = enabled,
                interactionSource = interaction,
                indication = null,
            ) {
                pressed = true
                onClick()
            },
        contentAlignment = Alignment.Center,
    ) {
        if (loading) {
            CircularProgressIndicator(
                color = Color.White,
                strokeWidth = 2.5.dp,
                modifier = Modifier.size(22.dp),
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    label,
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
}

@Composable
private fun SignUpToggle(isSignUp: Boolean, onToggle: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable { onToggle() }
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = if (isSignUp) "Already have an account?" else "New here?",
            style = MaterialTheme.typography.bodySmall,
            color = Mocha.copy(alpha = 0.8f),
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = if (isSignUp) "Sign in" else "Create one",
            style = MaterialTheme.typography.bodySmall.copy(
                fontStyle = FontStyle.Italic,
            ),
            color = Terracotta,
        )
    }
}

@Composable
private fun DevBypassBanner() {
    Row(
        modifier = Modifier
            .padding(horizontal = 12.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(WashiTan.copy(alpha = 0.35f))
            .border(
                1.dp,
                WashiTan.copy(alpha = 0.7f),
                RoundedCornerShape(12.dp),
            )
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(Color(0xFFB35A3D)),
        )
        Spacer(Modifier.width(10.dp))
        Text(
            "DEV — auth bypassed (Supabase not configured)",
            style = MaterialTheme.typography.labelSmall,
            color = InkBrown,
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
