package co.bolo.app.ui.history

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.bolo.app.ui.components.BOLO_HISTORY
import co.bolo.app.ui.components.BoloCaption
import co.bolo.app.ui.components.BoloCard
import co.bolo.app.ui.components.BoloDelta
import co.bolo.app.ui.components.BoloGhostButton
import co.bolo.app.ui.components.BoloScreenTitle
import co.bolo.app.ui.components.BoloTopBar
import co.bolo.app.ui.components.Hairline
import co.bolo.app.ui.components.MockHistorySession
import co.bolo.app.ui.theme.BoloPalette
import co.bolo.app.ui.theme.MonoData
import co.bolo.app.ui.theme.SansUI

@Composable
fun HistoryScreen(
    onBack: () -> Unit,
    onOpenSession: (id: String) -> Unit
) {
    val median = BOLO_HISTORY.sumOf { it.pct } / BOLO_HISTORY.size

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
            BoloScreenTitle("Last 6 sessions.")
            Spacer(Modifier.height(22.dp))
            BoloCard {
                Column {
                    BoloCaption("This week vs. last")
                    Spacer(Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            "$median",
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
                        Spacer(Modifier.padding(start = 14.dp))
                        BoloDelta(value = 7)
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "median English share across the cohort",
                        color = BoloPalette.InkFaint,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Spacer(Modifier.height(24.dp))
        }
        items(BOLO_HISTORY, key = { it.id }) { s ->
            HistoryRow(s, onClick = { onOpenSession(s.id) })
        }
        item {
            Spacer(Modifier.height(22.dp))
            BoloGhostButton("Export this month as CSV", onClick = { })
            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun HistoryRow(s: MockHistorySession, onClick: () -> Unit) {
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
                    s.date.uppercase(),
                    color = BoloPalette.InkFaint,
                    fontFamily = MonoData,
                    fontSize = 10.5.sp,
                    letterSpacing = 1.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(s.topic, color = BoloPalette.Ink, style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(2.dp))
                Text(
                    "${s.duration} min · ${s.students} students · top: ${s.top}",
                    color = BoloPalette.InkFaint,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    "${s.pct}",
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
