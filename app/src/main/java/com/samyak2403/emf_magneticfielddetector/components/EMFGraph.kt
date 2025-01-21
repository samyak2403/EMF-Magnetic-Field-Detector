package com.samyak2403.emf_magneticfielddetector.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun EMFGraph(
    values: List<Float>,
    modifier: Modifier = Modifier,
    maxValue: Float = 200f
) {
    if (values.isEmpty()) return
    
    val graphColor = MaterialTheme.colorScheme.primary
    
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
    ) {
        val path = Path()
        val strokeWidth = 2.dp.toPx()

        if (values.size > 1) {
            val xStep = size.width / (values.size - 1)
            val yStep = size.height / maxValue

            // Start path from the first point
            path.moveTo(0f, size.height - (values.first() * yStep))

            // Draw lines connecting all points
            values.forEachIndexed { index, value ->
                if (index == 0) return@forEachIndexed
                val x = index * xStep
                val y = size.height - (value * yStep)
                path.lineTo(x, y)
            }

            // Draw the path
            drawPath(
                path = path,
                color = graphColor,
                style = Stroke(width = strokeWidth)
            )

            // Draw points
            values.forEachIndexed { index, value ->
                val x = index * xStep
                val y = size.height - (value * yStep)
                drawCircle(
                    color = graphColor,
                    radius = strokeWidth,
                    center = Offset(x, y)
                )
            }
        }
    }
}
