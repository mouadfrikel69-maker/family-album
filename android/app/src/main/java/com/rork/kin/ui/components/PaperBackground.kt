package com.rork.kin.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.rork.kin.ui.theme.CreamPaper
import kotlin.random.Random

/** Subtle paper-like cream background with very soft tonal speckle. */
@Composable
fun PaperBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val speckles = remember {
        val rng = Random(42)
        List(80) { Triple(rng.nextFloat(), rng.nextFloat(), rng.nextFloat() * 0.4f + 0.2f) }
    }
    Box(modifier = modifier.fillMaxSize().background(CreamPaper)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // soft warm radial glow at top
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x33E8B4A0), Color.Transparent),
                    center = Offset(size.width * 0.5f, size.height * -0.1f),
                    radius = size.width * 0.95f,
                )
            )
            speckles.forEach { (x, y, a) ->
                drawCircle(
                    color = Color(0xFF8B7560).copy(alpha = a * 0.06f),
                    radius = 1.2f,
                    center = Offset(x * size.width, y * size.height),
                )
            }
        }
        content()
    }
}
