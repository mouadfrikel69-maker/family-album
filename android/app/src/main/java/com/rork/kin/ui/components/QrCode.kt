package com.rork.kin.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel

/**
 * Crisp, vector-drawn QR using ZXing for the bit matrix and Compose Canvas
 * for rendering. No bitmap allocation, scales perfectly with the box size.
 */
@Composable
fun QrCode(
    content: String,
    modifier: Modifier = Modifier,
    foreground: Color = Color(0xFF3B2A1F),
    background: Color = Color.Transparent,
) {
    val matrix = remember(content) {
        val hints = mapOf(
            EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.M,
            EncodeHintType.MARGIN to 0,
        )
        QRCodeWriter().encode(content.ifBlank { " " }, BarcodeFormat.QR_CODE, 64, 64, hints)
    }
    Canvas(modifier = modifier) {
        val cols = matrix.width
        val rows = matrix.height
        val cell = kotlin.math.min(size.width / cols, size.height / rows)
        val ox = (size.width - cell * cols) / 2f
        val oy = (size.height - cell * rows) / 2f
        if (background != Color.Transparent) {
            drawRect(background, topLeft = Offset(0f, 0f), size = Size(size.width, size.height))
        }
        for (y in 0 until rows) {
            for (x in 0 until cols) {
                if (matrix.get(x, y)) {
                    drawRect(
                        color = foreground,
                        topLeft = Offset(ox + x * cell, oy + y * cell),
                        size = Size(cell + 0.6f, cell + 0.6f),
                    )
                }
            }
        }
    }
}
