package co.bolo.app.ui.summary

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.bolo.app.ui.components.Hairline
import co.bolo.app.ui.components.StaticEnglishHalo
import co.bolo.app.ui.theme.AccentItalic
import co.bolo.app.ui.theme.BoloPalette
import co.bolo.app.util.Format

private const val REFLECTION =
    "Which moment did you switch — and what word was missing?"

@Composable
fun SummaryScreen(
    sessionId: String,
    onDone: () -> Unit,
    onOpenStudent: (String) -> Unit,
    vm: SummaryViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val session = state.session

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(BoloPalette.Bg),
        contentPadding = PaddingValues(horizontal = 22.dp, vertical = 28.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("SESSION SUMMARY", style = MaterialTheme.typography.labelSmall, color = BoloPalette.InkFaint)
                Text(
                    session?.let { Format.relativeDay(it.startedAt) } ?: "",
                    style = MaterialTheme.typography.labelSmall,
                    color = BoloPalette.InkFaint
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                session?.topic.orEmpty(),
                style = MaterialTheme.typography.headlineLarge,
                color = BoloPalette.Ink
            )
        }

        item {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                StaticEnglishHalo(share = session?.englishShare ?: 0f, diameter = 220.dp)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        Format.percent(session?.englishShare ?: 0f),
                        style = MaterialTheme.typography.displayLarge,
                        color = BoloPalette.Ink
                    )
                    Text(
                        "English Usage",
                        style = MaterialTheme.typography.labelMedium,
                        color = BoloPalette.InkFaint
                    )
                }
            }
            Spacer(Modifier.height(10.dp))
            val total = session?.totalSpeechMs ?: 0L
            Text(
                "Analyzed ${session?.chunksProcessed ?: 0} speech chunks over ${Format.minutes(total)}",
                style = MaterialTheme.typography.bodyMedium,
                color = BoloPalette.InkMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            Hairline()
            Spacer(Modifier.height(4.dp))
            Text("PARTICIPANTS", style = MaterialTheme.typography.labelSmall, color = BoloPalette.InkFaint)
            Spacer(Modifier.height(8.dp))
            // Show list of names even if per-student stats are 0 for MVP
            val names = state.rows.joinToString(", ") { it.student.displayName }
            Text(
                names.ifBlank { "Group Session" },
                style = MaterialTheme.typography.bodyLarge,
                color = BoloPalette.Ink
            )
        }

        item {
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(BoloPalette.Surface)
                    .border(1.dp, BoloPalette.Hairline, RoundedCornerShape(18.dp))
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Column {
                    Text("30-SECOND REFLECTION", style = MaterialTheme.typography.labelSmall, color = BoloPalette.InkFaint)
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "“$REFLECTION”",
                        style = AccentItalic,
                        color = BoloPalette.Ink,
                        textAlign = TextAlign.Start
                    )
                }
            }
        }

        item {
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(BoloPalette.Ink)
                    .clickable(onClick = onDone)
                    .padding(vertical = 18.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Done", color = BoloPalette.Bg, style = MaterialTheme.typography.titleMedium)
            }
            Spacer(Modifier.height(12.dp))
            Text(
                "The conversation transcript has been saved locally for internal analysis.",
                style = MaterialTheme.typography.bodySmall,
                color = BoloPalette.InkFaint,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
