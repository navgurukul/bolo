package co.bolo.app.ui.firstrun

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.bolo.app.ui.components.BoloGhostButton
import co.bolo.app.ui.components.BoloQuietButton
import co.bolo.app.ui.components.BoloSolidButton
import co.bolo.app.ui.theme.BoloPalette
import co.bolo.app.ui.theme.SansUI
import co.bolo.app.ui.theme.SerifAccent

@Composable
fun MicPermScreen(
    onAllow: () -> Unit,
    onOnce: () -> Unit = {},
    onDeny: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BoloPalette.Bg)
    ) {
        // Hint of home behind dim scrim
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Text(
                "Bolo",
                color = BoloPalette.Ink.copy(alpha = 0.5f),
                fontFamily = SerifAccent,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Normal,
                fontSize = 30.sp,
                letterSpacing = (-0.6).sp
            )
        }
        // Dim scrim
        Box(
            Modifier
                .fillMaxSize()
                .background(Color(0x73141812))
        )

        // Bottom sheet
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .background(BoloPalette.Surface)
                .padding(PaddingValues(horizontal = 28.dp, vertical = 24.dp))
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(BoloPalette.SageSoft),
                contentAlignment = Alignment.Center
            ) {
                Text("🎤", color = BoloPalette.SageDeep, fontSize = 28.sp)
            }
            Spacer(Modifier.height(18.dp))
            Text(
                "Allow Bolo to use the microphone?",
                color = BoloPalette.Ink,
                fontFamily = SansUI,
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = (-0.3).sp
            )
            Spacer(Modifier.height(10.dp))
            Text(
                "Bolo needs the mic so it can send speech to Google's speech service for transcription. Bolo keeps only the resulting text — no audio is stored on this phone.",
                color = BoloPalette.InkMuted,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(22.dp))
            BoloSolidButton("While using the app", onClick = onAllow)
            Spacer(Modifier.height(8.dp))
            BoloGhostButton("Only this time", onClick = onOnce)
            Spacer(Modifier.height(8.dp))
            BoloQuietButton("Don't allow", onClick = onDeny)
        }
    }
}
