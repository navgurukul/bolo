package co.bolo.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import co.bolo.app.ui.theme.BoloPalette

/**
 * Stripped-down line chart. Y axis = English share (0..1). X axis = ordinal session index.
 * No external charting lib — keeps APK small and the look on-brand.
 */
@Composable
fun TrendChart(
    points: List<Float>,
    modifier: Modifier = Modifier
) {
    if (points.isEmpty()) {
        Box(
            modifier = modifier.fillMaxWidth().height(180.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "No sessions yet — your trend will appear here after your first one.",
                style = MaterialTheme.typography.bodySmall,
                color = BoloPalette.InkFaint
            )
        }
        return
    }

    Canvas(modifier = modifier.fillMaxWidth().height(180.dp)) {
        val padX = 16.dp.toPx()
        val padY = 14.dp.toPx()
        val w = size.width - padX * 2
        val h = size.height - padY * 2

        // Hairline gridlines at 0, 25, 50, 75, 100%
        for (i in 0..4) {
            val y = padY + h - (h * i / 4f)
            drawLine(
                color = BoloPalette.Hairline,
                start = Offset(padX, y),
                end = Offset(padX + w, y),
                strokeWidth = 1f
            )
        }

        if (points.size == 1) {
            val x = padX + w / 2f
            val y = padY + h - (h * points[0].coerceIn(0f, 1f))
            drawCircle(BoloPalette.Sage, radius = 6.dp.toPx(), center = Offset(x, y))
            return@Canvas
        }

        val stepX = w / (points.size - 1).coerceAtLeast(1)
        val path = Path()
        points.forEachIndexed { idx, raw ->
            val v = raw.coerceIn(0f, 1f)
            val x = padX + stepX * idx
            val y = padY + h - (h * v)
            if (idx == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        drawPath(
            path = path,
            color = BoloPalette.SageDeep,
            style = Stroke(width = 2.5.dp.toPx())
        )
        // Dots
        points.forEachIndexed { idx, raw ->
            val v = raw.coerceIn(0f, 1f)
            val x = padX + stepX * idx
            val y = padY + h - (h * v)
            drawCircle(BoloPalette.Sage, radius = 4.dp.toPx(), center = Offset(x, y))
            drawCircle(BoloPalette.Surface, radius = 2.dp.toPx(), center = Offset(x, y))
        }
    }
}
