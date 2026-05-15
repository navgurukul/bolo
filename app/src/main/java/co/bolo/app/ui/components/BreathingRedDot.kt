package co.bolo.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import co.bolo.app.ui.theme.BoloPalette

/**
 * The single most important UI rule: when audio capture is live, this dot is visible.
 * Phase 0 has no audio yet, so callers pass `active = true` whenever the session is
 * "running" in the prototype. The breathing animation matches the design spec exactly
 * (1.5 s cycle, opacity 1 → 0.45 → 1, scale 1 → 0.85 → 1).
 */
@Composable
fun BreathingRedDot(
    active: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 10.dp,
    speedMultiplier: Float = 1f
) {
    val transition = rememberInfiniteTransition(label = "bolo-breathe")
    val cycleMs = (1500 / speedMultiplier).toInt().coerceAtLeast(300)
    val opacity by transition.animateFloat(
        initialValue = 1f,
        targetValue = 0.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(cycleMs, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "opacity"
    )
    val scale by transition.animateFloat(
        initialValue = 1f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(cycleMs, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    val effectiveOpacity = if (active) opacity else 0.18f
    val effectiveScale = if (active) scale else 1f

    Canvas(modifier = modifier.size(size)) {
        val r = (this.size.minDimension / 2f) * effectiveScale
        drawCircle(
            color = BoloPalette.MicRed.copy(alpha = effectiveOpacity),
            radius = r,
            center = Offset(this.size.width / 2f, this.size.height / 2f)
        )
    }
}

@Composable
fun BreathingRedDotWithLabel(
    active: Boolean,
    label: String = if (active) "Listening" else "Mic off",
    modifier: Modifier = Modifier,
    labelColor: Color = BoloPalette.InkMuted
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        BreathingRedDot(active = active, size = 9.dp)
        Spacer(Modifier.width(8.dp))
        Text(text = label, color = labelColor, style = androidx.compose.material3.MaterialTheme.typography.labelSmall)
    }
}
