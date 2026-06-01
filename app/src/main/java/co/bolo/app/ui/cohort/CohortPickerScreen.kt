package co.bolo.app.ui.cohort

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.bolo.app.ui.components.BoloScreenTitle
import co.bolo.app.ui.components.BoloSolidButton
import co.bolo.app.ui.components.BoloTopBar
import co.bolo.app.ui.theme.BoloPalette

@Composable
fun CohortPickerScreen(
    onBack: () -> Unit,
    onPick: (cohortId: String) -> Unit,
    onNewCohort: () -> Unit,
    vm: CohortPickerViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BoloPalette.Bg)
            .padding(PaddingValues(horizontal = 24.dp, vertical = 28.dp))
    ) {
        BoloTopBar(label = "Manage students", onBack = onBack)
        Spacer(Modifier.height(22.dp))
        BoloScreenTitle("Which cohort?")
        Spacer(Modifier.height(8.dp))
        Text(
            "Pick a group to add, rename, or remove students.",
            color = BoloPalette.InkMuted,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(20.dp))

        if (state.cohorts.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "No cohorts yet.",
                    color = BoloPalette.InkFaint,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(20.dp))
                BoloSolidButton(label = "Create a cohort  →", onClick = onNewCohort)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(state.cohorts, key = { it.id }) { row ->
                    CohortPickRow(
                        name = row.name,
                        count = row.studentCount,
                        onClick = { onPick(row.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CohortPickRow(name: String, count: Int, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(BoloPalette.Surface)
            .border(1.dp, BoloPalette.Hairline, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.padding(end = 12.dp)) {
            Text(name, style = MaterialTheme.typography.titleMedium, color = BoloPalette.Ink)
            Text(
                "$count ${if (count == 1) "student" else "students"}",
                style = MaterialTheme.typography.labelSmall,
                color = BoloPalette.InkFaint
            )
        }
        Text("→", color = BoloPalette.InkFaint, fontSize = 16.sp)
    }
}
