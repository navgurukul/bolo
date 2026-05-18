package co.bolo.app.ui.firstrun

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.bolo.app.ui.components.BoloCaption
import co.bolo.app.ui.components.BoloQuietButton
import co.bolo.app.ui.components.BoloScreenTitle
import co.bolo.app.ui.components.BoloSolidButton
import co.bolo.app.ui.components.Wordmark
import co.bolo.app.ui.theme.BoloPalette
import co.bolo.app.ui.theme.MonoData
import kotlinx.coroutines.delay

private val PAIR_CODE = listOf("N", "G", "K", "7", "T", "4")

@Composable
fun PairScreen(
    onContinue: () -> Unit,
    onNoCode: () -> Unit = {}
) {
    var showing by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        while (showing < 6) {
            delay(220)
            showing += 1
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BoloPalette.Bg)
            .verticalScroll(rememberScrollState())
            .padding(PaddingValues(horizontal = 24.dp, vertical = 28.dp))
    ) {
        Spacer(Modifier.height(24.dp))
        Wordmark()
        Spacer(Modifier.height(36.dp))
        BoloCaption("Step 1 of 4 · Pair this phone")
        Spacer(Modifier.height(8.dp))
        BoloScreenTitle("Enter your\nprogram code.")
        Spacer(Modifier.height(16.dp))
        Text(
            "Your program coordinator gave you a six-character code. It binds this device to your cohort group.",
            color = BoloPalette.InkMuted,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(28.dp))

        // Code slots
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
        ) {
            PAIR_CODE.forEachIndexed { i, ch ->
                CodeSlot(
                    text = if (i < showing) ch else "",
                    isCurrent = i == showing,
                    filled = i < showing
                )
            }
        }

        Spacer(Modifier.height(14.dp))
        Text(
            if (showing < 6) "Listening…" else "✓ Code recognised · NavGurukul, Pune",
            color = BoloPalette.InkFaint,
            fontFamily = MonoData,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(28.dp))
        BoloSolidButton("Continue", onClick = onContinue)
        Spacer(Modifier.height(10.dp))
        BoloQuietButton("I don't have a code yet", onClick = onNoCode)
    }
}

@Composable
private fun CodeSlot(text: String, isCurrent: Boolean, filled: Boolean) {
    val borderColor = if (isCurrent) BoloPalette.Sage else BoloPalette.Hairline
    Box(
        modifier = Modifier
            .size(width = 44.dp, height = 56.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (filled) BoloPalette.Surface else BoloPalette.Bg)
            .border(if (isCurrent) 2.dp else 1.dp, borderColor, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text,
            color = BoloPalette.Ink,
            fontFamily = MonoData,
            fontSize = 24.sp
        )
    }
}
