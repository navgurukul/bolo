package co.bolo.app.ui.firstrun

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.bolo.app.ui.components.BoloCaption
import co.bolo.app.ui.components.BoloScreenTitle
import co.bolo.app.ui.components.BoloSolidButton
import co.bolo.app.ui.components.Hairline
import co.bolo.app.ui.theme.BoloPalette

@Composable
fun ConsentScreen(
    onContinue: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BoloPalette.Bg)
            .verticalScroll(rememberScrollState())
            .padding(PaddingValues(horizontal = 24.dp, vertical = 28.dp))
    ) {
        Spacer(Modifier.height(24.dp))
        BoloCaption("Step 2 of 4 · How Bolo listens")
        Spacer(Modifier.height(10.dp))
        BoloScreenTitle("We listen.\nWe keep the words.")
        Spacer(Modifier.height(24.dp))

        PromiseRow(
            icon = "👂",
            title = "Bolo listens during a session.",
            sub = "It detects whether each speaker is using English or another language. That's it."
        )
        PromiseRow(
            icon = "☁️",
            title = "Speech goes to Google for transcription.",
            sub = "Android's built-in speech service turns what's spoken into text. Google's privacy policy applies to that step."
        )
        PromiseRow(
            icon = "👻",
            title = "Bolo never saves the audio.",
            sub = "Only the transcript and the English share are stored on this phone. No recordings, no uploads from Bolo to anywhere else."
        )
        PromiseRow(
            icon = "●",
            title = "A red dot stays visible when the mic is on.",
            sub = "Always. Even if the screen dims. No surprises."
        )

        Spacer(Modifier.height(28.dp))
        BoloSolidButton("I understand — continue", onClick = onContinue)
    }
}

@Composable
private fun PromiseRow(icon: String, title: String, sub: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(BoloPalette.SageSoft),
            contentAlignment = Alignment.Center
        ) {
            Text(icon, color = BoloPalette.SageDeep, fontSize = 16.sp)
        }
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                title,
                color = BoloPalette.Ink,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(4.dp))
            Text(
                sub,
                color = BoloPalette.InkFaint,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
    Hairline()
}
