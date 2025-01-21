package com.samyak2403.emf_magneticfielddetector.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.math.min
import kotlin.math.PI

@Composable
fun SpeedView(
    modifier: Modifier = Modifier,
    speed: Float,
    maxSpeed: Float = 100f
) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val radius = min(width, height) / 2
        val center = Offset(width / 2, height / 2)
        val strokeWidth = radius * 0.1f

        // Draw background circle
        drawCircle(
            color = Color.LightGray.copy(alpha = 0.3f),
            radius = radius,
            center = center,
            style = Stroke(width = strokeWidth)
        )

        // Draw progress arc
        val sweepAngle = (speed / maxSpeed) * 270f
        drawArc(
            color = when {
                speed < maxSpeed * 0.3f -> Color.Green
                speed < maxSpeed * 0.7f -> Color.Yellow
                else -> Color.Red
            },
            startAngle = 135f,
            sweepAngle = sweepAngle,
            useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // Draw needle
        rotate(135f + sweepAngle, center) {
            drawLine(
                color = Color.Red,
                start = center,
                end = Offset(center.x + radius * 0.7f, center.y),
                strokeWidth = strokeWidth * 0.5f,
                cap = StrokeCap.Round
            )
        }

        // Draw center circle
        drawCircle(
            color = Color.DarkGray,
            radius = radius * 0.1f,
            center = center
        )
    }
}
