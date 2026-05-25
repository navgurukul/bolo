package co.bolo.app.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.bolo.app.data.model.Session
import co.bolo.app.ui.components.BoloCaption
import co.bolo.app.ui.components.BoloCard
import co.bolo.app.ui.components.BoloScreenTitle
import co.bolo.app.ui.components.BoloTopBar
import co.bolo.app.ui.components.Hairline
import co.bolo.app.ui.theme.BoloPalette
import co.bolo.app.ui.theme.MonoData
import co.bolo.app.ui.theme.SansUI
import co.bolo.app.util.Format

@Composable
fun HistoryScreen(
    onBack: () -> Unit,
    onOpenSession: (id: String) -> Unit,
    vm: HistoryViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BoloPalette.Bg),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 28.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        item {
            BoloTopBar(label = "Session history", onBack = onBack)
            Spacer(Modifier.height(18.dp))
            BoloScreenTitle(
                if (state.sessions.isEmpty()) "No sessions yet."
                else "${state.sessions.size} session" +
                    if (state.sessions.size == 1) "." else "s."
            )
            Spacer(Modifier.height(22.dp))
            if (state.sessions.isNotEmpty()) {
                BoloCard {
                    Column {
                        BoloCaption("Median English share")
                        Spacer(Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                "${state.medianSharePct}",
                                color = BoloPalette.Ink,
                                fontFamily = SansUI,
                                fontWeight = FontWeight.Medium,
                                fontSize = 56.sp,
                                letterSpacing = (-1.8).sp
                            )
                            Text(
                                "%",
                                color = BoloPalette.InkFaint,
                                fontFamily = SansUI,
                                fontSize = 22.sp,
                                modifier = Modifier.padding(start = 2.dp, bottom = 8.dp)
                            )
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "across ${state.sessions.size} session" +
                                if (state.sessions.size == 1) "" else "s",
                            color = BoloPalette.InkFaint,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }
        items(state.sessions, key = { it.id }) { s ->
            HistoryRow(s, onClick = { onOpenSession(s.id) })
        }
        item {
            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun HistoryRow(s: Session, onClick: () -> Unit) {
    val durationMs = (s.endedAt ?: s.startedAt) - s.startedAt
    val pct = (s.englishShare * 100f).toInt()
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    Format.relativeDay(s.startedAt).uppercase(),
                    color = BoloPalette.InkFaint,
                    fontFamily = MonoData,
                    fontSize = 10.5.sp,
                    letterSpacing = 1.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(s.topic, color = BoloPalette.Ink, style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(2.dp))
                Text(
                    Format.minutes(durationMs),
                    color = BoloPalette.InkFaint,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    "$pct",
                    color = BoloPalette.Ink,
                    fontFamily = SansUI,
                    fontWeight = FontWeight.Normal,
                    fontSize = 28.sp,
                    letterSpacing = (-0.6).sp
                )
                Text(
                    "%",
                    color = BoloPalette.InkFaint,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
        }
        Hairline()
    }
}
