package com.example.dkpace.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import com.example.dkpace.ui.theme.ArcadeColors

@Composable
fun SkullIcon(
    modifier: Modifier = Modifier.size(18.dp),
    tint: Color = ArcadeColors.PinkAccent,
    cutoutColor: Color = ArcadeColors.CardBackground
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Head dome
        drawArc(
            color = tint,
            startAngle = 140f,
            sweepAngle = 260f,
            useCenter = true,
            topLeft = Offset(w * 0.12f, h * 0.05f),
            size = Size(w * 0.76f, h * 0.65f)
        )
        // Jaw / Teeth box
        drawRect(
            color = tint,
            topLeft = Offset(w * 0.28f, h * 0.55f),
            size = Size(w * 0.44f, h * 0.35f)
        )

        // Left eye
        drawCircle(
            color = cutoutColor,
            radius = w * 0.12f,
            center = Offset(w * 0.35f, h * 0.38f)
        )
        // Right eye
        drawCircle(
            color = cutoutColor,
            radius = w * 0.12f,
            center = Offset(w * 0.65f, h * 0.38f)
        )

        // Nose triangle
        val nosePath = Path().apply {
            moveTo(w * 0.50f, h * 0.50f)
            lineTo(w * 0.44f, h * 0.60f)
            lineTo(w * 0.56f, h * 0.60f)
            close()
        }
        drawPath(path = nosePath, color = cutoutColor)

        // Teeth vertical slits
        drawLine(
            color = cutoutColor,
            start = Offset(w * 0.40f, h * 0.68f),
            end = Offset(w * 0.40f, h * 0.88f),
            strokeWidth = size.width * 0.06f
        )
        drawLine(
            color = cutoutColor,
            start = Offset(w * 0.50f, h * 0.68f),
            end = Offset(w * 0.50f, h * 0.88f),
            strokeWidth = size.width * 0.06f
        )
        drawLine(
            color = cutoutColor,
            start = Offset(w * 0.60f, h * 0.68f),
            end = Offset(w * 0.60f, h * 0.88f),
            strokeWidth = size.width * 0.06f
        )
    }
}
