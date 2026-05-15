package co.bolo.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import co.bolo.app.ui.theme.BoloPalette

/**
 * Ambient English-share ring. Sage = English flowing, amber = drift (≥30s of non-English).
 * Off by default per PRD — caller decides whether to render.
 *
 * @param share 0f..1f rolling English share over the last ~60s
 * @param drift true if the group has been in non-English for 30s+
 */
@Composable
fun EnglishRing(
    share: Float,
    drift: Boolean,
    diameter: Dp = 220.dp,
    strokeWidth: Dp = 10.dp,
    content: @Composable () -> Unit = {}
) {
    val target = if (drift) BoloPalette.Amber else BoloPalette.Sage
    val color by animateColorAsState(targetValue = target, animationSpec = tween(900), label = "ring-color")

    val transition = rememberInfiniteTransition(label = "ring-breath")
    val pulse by transition.animateFloat(
        initialValue = 0.55f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(modifier = Modifier.size(diameter), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(diameter)) {
            val stroke = Stroke(width = strokeWidth.toPx())
            // Track
            drawCircle(
                color = BoloPalette.Hairline,
                radius = this.size.minDimension / 2f - stroke.width / 2f,
                center = Offset(this.size.width / 2f, this.size.height / 2f),
                style = stroke
            )
            // Share arc — sweep proportional to share
            val sweep = (share.coerceIn(0f, 1f)) * 360f
            drawArc(
                color = color.copy(alpha = pulse),
                startAngle = -90f,
                sweepAngle = sweep,
                useCenter = false,
                style = stroke
            )
        }
        content()
    }
}

@Composable
fun StaticEnglishHalo(
    share: Float,
    diameter: Dp = 160.dp,
    color: Color = BoloPalette.Sage
) {
    Canvas(modifier = Modifier.size(diameter)) {
        val stroke = Stroke(width = 6.dp.toPx())
        drawCircle(
            color = BoloPalette.Hairline,
            radius = this.size.minDimension / 2f - stroke.width / 2f,
            center = Offset(this.size.width / 2f, this.size.height / 2f),
            style = stroke
        )
        val sweep = (share.coerceIn(0f, 1f)) * 360f
        drawArc(
            color = color,
            startAngle = -90f,
            sweepAngle = sweep,
            useCenter = false,
            style = stroke
        )
    }
}
