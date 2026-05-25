package co.bolo.app.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.bolo.app.ui.components.BoloCaption
import co.bolo.app.ui.components.BoloCard
import co.bolo.app.ui.components.BoloScreenTitle
import co.bolo.app.ui.components.BoloSectionLabel
import co.bolo.app.ui.components.BoloTopBar
import co.bolo.app.ui.theme.BoloPalette
import co.bolo.app.ui.theme.MonoData
import co.bolo.app.util.Format

@Composable
fun SessionDetailScreen(
    sessionId: String,
    onBack: () -> Unit,
    vm: SessionDetailViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val session = state.session

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BoloPalette.Bg),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 28.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        item {
            BoloTopBar(
                label = session?.let { Format.relativeDay(it.startedAt) } ?: "Session",
                onBack = onBack
            )
            Spacer(Modifier.height(18.dp))
        }
        if (session == null) {
            item {
                Text(
                    "Session not found.",
                    color = BoloPalette.InkFaint,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            return@LazyColumn
        }
        val durationMs = (session.endedAt ?: session.startedAt) - session.startedAt
        val pct = (session.englishShare * 100f).toInt()
        item {
            BoloCaption("${session.topic} · ${Format.minutes(durationMs)} · ${state.rows.size} tracked")
            Spacer(Modifier.height(4.dp))
            BoloScreenTitle("$pct% English.")
            Spacer(Modifier.height(24.dp))
            BoloCard {
                Column {
                    BoloCaption("English share over the session")
                    Spacer(Modifier.height(12.dp))
                    Timeline(state.timeline)
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("0:00", color = BoloPalette.InkFaint, fontFamily = MonoData, fontSize = 10.sp)
                        Text(
                            Format.clockMs(durationMs / 2),
                            color = BoloPalette.InkFaint,
                            fontFamily = MonoData,
                            fontSize = 10.sp
                        )
                        Text(
                            Format.clockMs(durationMs),
                            color = BoloPalette.InkFaint,
                            fontFamily = MonoData,
                            fontSize = 10.sp
                        )
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
            if (state.rows.isNotEmpty()) {
                BoloSectionLabel("Per student")
                Spacer(Modifier.height(8.dp))
            }
        }
        items(state.rows, key = { it.student.id }) { row ->
            PerStudentRow(row, rank = state.rows.indexOf(row) + 1)
        }
        item {
            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun Timeline(segments: List<TimelineSegment>) {
    if (segments.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "No speech captured.",
                color = BoloPalette.InkFaint,
                style = MaterialTheme.typography.bodySmall
            )
        }
        return
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        segments.forEach { seg ->
            val isEnglish = seg.populated && seg.englishShare >= 0.5f
            val fraction = when {
                !seg.populated -> 0.12f
                else -> (0.18f + seg.englishShare * 0.82f).coerceIn(0.12f, 1f)
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(fraction)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        when {
                            !seg.populated -> BoloPalette.SurfaceMuted
                            isEnglish -> BoloPalette.Sage
                            else -> BoloPalette.SurfaceMuted
                        }
                    )
            )
        }
    }
}

@Composable
private fun PerStudentRow(row: StudentRowState, rank: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            String.format("%02d", rank),
            color = BoloPalette.InkFaint,
            fontFamily = MonoData,
            fontSize = 11.sp,
            modifier = Modifier.padding(end = 12.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(row.student.displayName, color = BoloPalette.Ink, style = MaterialTheme.typography.bodyLarge)
            if (row.speechMs > 0L) {
                Text(
                    Format.minutes(row.speechMs),
                    color = BoloPalette.InkFaint,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        Box(
            modifier = Modifier
                .padding(end = 12.dp)
                .width(110.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(BoloPalette.SurfaceMuted)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(row.sharePct / 100f)
                    .height(4.dp)
                    .background(BoloPalette.Sage)
            )
        }
        Text(
            "${row.sharePct}%",
            color = BoloPalette.Ink,
            fontFamily = MonoData,
            fontSize = 13.sp
        )
    }
    Spacer(
        Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(BoloPalette.Hairline)
    )
}
