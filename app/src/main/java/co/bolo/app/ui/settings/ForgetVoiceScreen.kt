package co.bolo.app.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.bolo.app.ui.components.BoloCaption
import co.bolo.app.ui.components.BoloCard
import co.bolo.app.ui.components.BoloQuietButton
import co.bolo.app.ui.components.BoloScreenTitle
import co.bolo.app.ui.components.BoloSolidButton
import co.bolo.app.ui.components.BoloTopBar
import co.bolo.app.ui.theme.BoloPalette
import kotlinx.coroutines.delay

@Composable
fun ForgetVoiceScreen(
    onBack: () -> Unit,
    onDone: () -> Unit
) {
    var holding by remember { mutableStateOf(false) }
    var pct by remember { mutableIntStateOf(0) }
    var done by remember { mutableStateOf(false) }

    LaunchedEffect(holding, done) {
        if (!holding && !done) pct = 0
        while (holding && !done) {
            delay(60)
            pct = (pct + 4).coerceAtMost(100)
            if (pct >= 100) {
                done = true
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BoloPalette.Bg)
            .verticalScroll(rememberScrollState())
            .padding(PaddingValues(horizontal = 24.dp, vertical = 28.dp))
    ) {
        BoloTopBar(label = "Forget my voice", onBack = onBack)
        Spacer(Modifier.height(22.dp))

        if (!done) {
            BoloScreenTitle("This is permanent.")
            Spacer(Modifier.height(16.dp))
            Text(
                "We will erase your voice fingerprint from this phone. Past session summaries stay (they're already anonymous), but new sessions won't be able to recognise you until you enroll again.",
                color = BoloPalette.InkMuted,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(32.dp))
            BoloCard {
                Column {
                    BoloCaption("To confirm")
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Hold the button below for two seconds.",
                        color = BoloPalette.Ink,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            Spacer(Modifier.height(28.dp))
            HoldButton(
                holding = holding,
                pct = pct,
                onHoldChange = { holding = it }
            )
            Spacer(Modifier.height(14.dp))
            BoloQuietButton("Never mind", onClick = onBack)
        } else {
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(BoloPalette.SurfaceMuted),
                    contentAlignment = Alignment.Center
                ) {
                    Text("✕", color = BoloPalette.InkFaint, fontSize = 28.sp)
                }
                BoloScreenTitle("Forgotten.", textAlign = TextAlign.Center)
                Text(
                    "Your voice fingerprint is gone. You can enroll again any time.",
                    color = BoloPalette.InkMuted,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                BoloSolidButton("Back to home", onClick = onDone)
            }
        }
        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun HoldButton(
    holding: Boolean,
    pct: Int,
    onHoldChange: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(BoloPalette.MicRed)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        onHoldChange(true)
                        try {
                            awaitRelease()
                        } finally {
                            onHoldChange(false)
                        }
                    }
                )
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(pct / 100f)
                .height(56.dp)
                .background(Color.Black.copy(alpha = 0.25f))
        )
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                if (holding) "Keep holding…" else "Hold to forget",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
