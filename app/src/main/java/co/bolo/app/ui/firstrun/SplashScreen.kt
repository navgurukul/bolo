package co.bolo.app.ui.firstrun

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.bolo.app.ui.theme.BoloPalette
import co.bolo.app.ui.theme.MonoData
import co.bolo.app.ui.theme.SerifAccent
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onContinue: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1800)
        onContinue()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BoloPalette.Bg)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    "Bolo",
                    color = BoloPalette.Ink,
                    fontFamily = SerifAccent,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Normal,
                    fontSize = 88.sp,
                    letterSpacing = (-2).sp
                )
                Text(
                    ".",
                    color = BoloPalette.Sage,
                    fontFamily = SerifAccent,
                    fontWeight = FontWeight.Normal,
                    fontSize = 88.sp
                )
            }
            Spacer(Modifier.height(24.dp))
            Text(
                "SPEAK · LISTEN · GROW",
                color = BoloPalette.InkFaint,
                fontFamily = MonoData,
                fontSize = 11.sp,
                letterSpacing = 2.sp
            )
        }
        BreathingDots(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
        )
    }
}

@Composable
private fun BreathingDots(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "splash-dots")
    val phase0 by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "p0"
    )
    val phase1 by transition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, delayMillis = 180, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "p1"
    )
    val phase2 by transition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, delayMillis = 360, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "p2"
    )
    Box(modifier = modifier.height(60.dp), contentAlignment = Alignment.Center) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Dot(phase0)
            Dot(phase1)
            Dot(phase2)
        }
    }
}

@Composable
private fun Dot(alpha: Float) {
    Box(
        Modifier
            .size(6.dp)
            .clip(CircleShape)
            .background(BoloPalette.Sage)
            .alpha(alpha)
    )
}
