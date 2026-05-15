package co.bolo.app.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.bolo.app.ui.components.Hairline
import co.bolo.app.ui.components.TopicPill
import co.bolo.app.ui.components.TrendChart
import co.bolo.app.ui.theme.BoloPalette
import co.bolo.app.util.Format

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DashboardScreen(
    studentId: String,
    onBack: () -> Unit,
    vm: DashboardViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val name = state.student?.displayName ?: "Student"
    val pts = state.points.map { it.englishShare }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(BoloPalette.Bg),
        contentPadding = PaddingValues(horizontal = 22.dp, vertical = 28.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Back",
                    modifier = Modifier.clickable(onClick = onBack),
                    style = MaterialTheme.typography.labelLarge,
                    color = BoloPalette.InkMuted
                )
                Text("YOUR TREND", style = MaterialTheme.typography.labelSmall, color = BoloPalette.InkFaint)
            }
        }
        item {
            Text(name, style = MaterialTheme.typography.headlineLarge, color = BoloPalette.Ink)
            Spacer(Modifier.height(4.dp))
            Text(
                "Across ${state.points.size} session${if (state.points.size == 1) "" else "s"}" +
                    state.selectedTopic?.let { " · $it" }.orEmpty(),
                style = MaterialTheme.typography.bodyMedium,
                color = BoloPalette.InkMuted
            )
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(BoloPalette.Surface)
                    .border(1.dp, BoloPalette.Hairline, RoundedCornerShape(20.dp))
                    .padding(horizontal = 16.dp, vertical = 18.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text("AVERAGE", style = MaterialTheme.typography.labelSmall, color = BoloPalette.InkFaint)
                            Text(
                                Format.percent(state.averageShare),
                                style = MaterialTheme.typography.displayMedium,
                                color = BoloPalette.Ink
                            )
                            Text(
                                "English, by speaking time",
                                style = MaterialTheme.typography.labelSmall,
                                color = BoloPalette.InkFaint
                            )
                        }
                    }
                    Spacer(Modifier.height(20.dp))
                    TrendChart(points = pts)
                }
            }
        }

        if (state.topics.isNotEmpty()) {
            item {
                Text("FILTER BY TOPIC", style = MaterialTheme.typography.labelSmall, color = BoloPalette.InkFaint)
                Spacer(Modifier.height(8.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    TopicPill(
                        label = "All",
                        selected = state.selectedTopic == null,
                        onClick = { vm.selectTopic(null) }
                    )
                    state.topics.forEach { t ->
                        TopicPill(
                            label = t,
                            selected = state.selectedTopic == t,
                            onClick = { vm.selectTopic(t) }
                        )
                    }
                }
            }
        }

        item {
            Hairline()
            Spacer(Modifier.height(4.dp))
            Text("SESSIONS", style = MaterialTheme.typography.labelSmall, color = BoloPalette.InkFaint)
        }

        items(state.points.reversed(), key = { it.sessionId }) { p ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(BoloPalette.Surface)
                    .border(1.dp, BoloPalette.Hairline, RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(p.topic, style = MaterialTheme.typography.titleMedium, color = BoloPalette.Ink)
                    Text(
                        "${Format.relativeDay(p.startedAt)} · ${Format.minutes(p.speechMs)} spoken",
                        style = MaterialTheme.typography.labelSmall,
                        color = BoloPalette.InkFaint
                    )
                }
                Text(
                    Format.percent(p.englishShare),
                    style = MaterialTheme.typography.titleMedium,
                    color = BoloPalette.SageDeep
                )
            }
        }

        item {
            Spacer(Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(BoloPalette.Surface)
                    .border(1.dp, BoloPalette.Hairline, RoundedCornerShape(14.dp))
                    .clickable { vm.forgetVoice() }
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Column {
                    Text("Forget my voice", style = MaterialTheme.typography.titleMedium, color = BoloPalette.MicRed)
                    Text(
                        "Deletes ${name}'s voice fingerprint from this phone instantly.",
                        style = MaterialTheme.typography.bodySmall,
                        color = BoloPalette.InkFaint
                    )
                }
            }
        }
        item { Spacer(Modifier.height(40.dp)) }
    }
}
