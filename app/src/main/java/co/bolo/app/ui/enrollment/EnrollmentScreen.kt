package co.bolo.app.ui.enrollment

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.bolo.app.ui.components.BreathingRedDot
import co.bolo.app.ui.theme.AccentItalic
import co.bolo.app.ui.theme.BoloPalette
import co.bolo.app.ui.theme.SerifAccent

private const val READ_ALOUD =
    "Today is a good day to speak — even when the words come slowly."

@Composable
fun EnrollmentScreen(
    cohortId: String,
    studentId: String,
    onDone: () -> Unit,
    onCancel: () -> Unit,
    vm: EnrollmentViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BoloPalette.Bg)
            .padding(PaddingValues(horizontal = 22.dp, vertical = 28.dp)),
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Cancel",
                modifier = Modifier.clickable(onClick = onCancel),
                style = MaterialTheme.typography.labelLarge,
                color = BoloPalette.InkMuted
            )
            BreathingRedDot(active = state.phase == EnrollPhase.Listening, size = 9.dp)
        }

        Spacer(Modifier.height(36.dp))
        Text(
            "Enrolling".uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = BoloPalette.InkFaint
        )
        Text(
            state.studentName,
            style = MaterialTheme.typography.headlineLarge,
            color = BoloPalette.Ink
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "We'll listen for 10 seconds and store only a tiny fingerprint of your voice — never the recording itself.",
            style = MaterialTheme.typography.bodyMedium,
            color = BoloPalette.InkMuted
        )

        Spacer(Modifier.height(40.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(BoloPalette.Surface)
                .padding(horizontal = 22.dp, vertical = 28.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text("READ ALOUD", style = MaterialTheme.typography.labelSmall, color = BoloPalette.InkFaint)
                Spacer(Modifier.height(14.dp))
                Text(
                    text = "“$READ_ALOUD”",
                    style = AccentItalic,
                    color = BoloPalette.Ink,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(Modifier.height(40.dp))

        when (state.phase) {
            EnrollPhase.Intro -> {
                PrimaryButton(label = "I'm ready") { vm.begin() }
            }
            EnrollPhase.Countdown -> {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(
                        text = state.countdown.coerceAtLeast(1).toString(),
                        color = BoloPalette.Ink,
                        fontFamily = SerifAccent,
                        fontStyle = FontStyle.Italic,
                        fontSize = 96.sp
                    )
                }
            }
            EnrollPhase.Listening -> {
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Listening · ${state.secondsRemaining}s",
                        style = MaterialTheme.typography.titleLarge,
                        color = BoloPalette.SageDeep
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Audio stays in memory. Nothing is being saved.",
                        style = MaterialTheme.typography.bodySmall,
                        color = BoloPalette.InkFaint
                    )
                }
            }
            EnrollPhase.Done -> {
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("All set.", style = MaterialTheme.typography.headlineMedium, color = BoloPalette.Ink)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "${state.studentName}'s voice is enrolled. Nothing was saved except the fingerprint.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = BoloPalette.InkMuted,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(24.dp))
                    PrimaryButton(label = "Done", onClick = onDone)
                }
            }
        }
    }
}

@Composable
private fun PrimaryButton(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(BoloPalette.Ink)
            .clickable(onClick = onClick)
            .padding(vertical = 18.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = BoloPalette.Bg, style = MaterialTheme.typography.titleMedium)
    }
}
