package com.rork.kin.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val KinLightColors = lightColorScheme(
    primary = Terracotta,
    onPrimary = WarmWhite,
    primaryContainer = BlushPink,
    onPrimaryContainer = TerracottaDeep,
    secondary = WashiTan,
    onSecondary = InkBrown,
    secondaryContainer = DustyRose,
    onSecondaryContainer = InkBrown,
    tertiary = SageMist,
    onTertiary = InkBrown,
    background = CreamPaper,
    onBackground = InkBrown,
    surface = WarmWhite,
    onSurface = InkBrown,
    surfaceVariant = BlushPink,
    onSurfaceVariant = Mocha,
    outline = FadedInk,
    outlineVariant = BlushPink,
)

@Composable
fun AppTheme(
    content: @Composable () -> Unit,
) {
    // Force the cozy paper palette regardless of system theme — the brand IS the warmth.
    MaterialTheme(
        colorScheme = KinLightColors,
        typography = KinTypography,
        content = content,
    )
}
