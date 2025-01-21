package com.samyak2403.emf_magneticfielddetector.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun EMFSpeedometer(
    value: Float,
    maxValue: Float,
    modifier: Modifier = Modifier,
    lowValueColor: Color = Color(0xFF00C853),
    mediumValueColor: Color = Color(0xFFFFD600),
    highValueColor: Color = Color(0xFFD50000),
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    textColor: Color = MaterialTheme.colorScheme.onSurface
) {
    val sweepAngle by animateFloatAsState(
        targetValue = (value / maxValue) * 240f,
        label = "sweepAngle"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            val width = size.width
            val height = size.height
            val radius = minOf(width, height) / 2
            val center = Offset(width / 2, height / 2)
            val strokeWidth = radius * 0.1f

            // Draw background arc
            drawArc(
                color = backgroundColor.copy(alpha = 0.2f),
                startAngle = 150f,
                sweepAngle = 240f,
                useCenter = false,
                topLeft = Offset(strokeWidth, strokeWidth),
                size = Size(width - strokeWidth * 2, height - strokeWidth * 2),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Draw value arc
            val arcColor = when {
                value < maxValue * 0.3f -> lowValueColor
                value < maxValue * 0.7f -> mediumValueColor
                else -> highValueColor
            }

            drawArc(
                color = arcColor,
                startAngle = 150f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(strokeWidth, strokeWidth),
                size = Size(width - strokeWidth * 2, height - strokeWidth * 2),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Draw tick marks
            for (i in 0..12) {
                val angle = 150f + i * 20
                val angleInRadians = angle * PI / 180
                val startRadius = radius * 0.6f
                val endRadius = radius * 0.7f
                val startX = center.x + cos(angleInRadians).toFloat() * startRadius
                val startY = center.y + sin(angleInRadians).toFloat() * startRadius
                val endX = center.x + cos(angleInRadians).toFloat() * endRadius
                val endY = center.y + sin(angleInRadians).toFloat() * endRadius

                drawLine(
                    color = textColor.copy(alpha = 0.5f),
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = 2.dp.toPx()
                )
            }
        }

        // Draw value text
        Text(
            text = "%.1f µT".format(value),
            color = textColor,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
    }
}
