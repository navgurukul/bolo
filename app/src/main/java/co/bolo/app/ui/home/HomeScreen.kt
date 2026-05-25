package co.bolo.app.ui.home

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
import androidx.compose.foundation.layout.size
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
import co.bolo.app.data.model.Student
import co.bolo.app.ui.components.BoloPrivacyNote
import co.bolo.app.ui.components.BoloSyncedPill
import co.bolo.app.ui.components.Hairline
import co.bolo.app.ui.components.Wordmark
import co.bolo.app.ui.theme.BoloPalette
import co.bolo.app.ui.theme.MonoData

@Composable
fun HomeScreen(
    onStartSession: (cohortId: String) -> Unit,
    onOpenDashboard: (studentId: String) -> Unit,
    onOpenSession: (sessionId: String) -> Unit,
    onOpenHistory: (cohortId: String?) -> Unit,
    onOpenSettings: (cohortId: String?) -> Unit,
    onNewCohort: () -> Unit = {},
    vm: HomeViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val cohortId = state.selectedCohortId

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(BoloPalette.Bg),
        contentPadding = PaddingValues(horizontal = 22.dp, vertical = 28.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Wordmark()
                BoloSyncedPill()
            }
            Spacer(Modifier.height(6.dp))
            val selectedCohortName = state.cohorts.firstOrNull { it.id == cohortId }?.name
            val tagline = if (selectedCohortName != null)
                "$selectedCohortName · ${state.students.size} ${if (state.students.size == 1) "student" else "students"}"
            else "No cohorts yet"
            Text(
                tagline,
                color = BoloPalette.InkFaint,
                fontFamily = MonoData,
                fontSize = 11.sp,
                letterSpacing = 0.5.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (state.cohorts.isEmpty()) {
            item {
                EmptyState(label = "Create your first cohort", onStart = onNewCohort)
            }
        } else {
            item {
                Text(
                    "Good to see you.",
                    style = MaterialTheme.typography.headlineMedium,
                    color = BoloPalette.Ink
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Pick a cohort and start when the room is ready.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = BoloPalette.InkMuted
                )
            }

            item {
                // Cohort row
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "COHORTS",
                            style = MaterialTheme.typography.labelSmall,
                            color = BoloPalette.InkFaint
                        )
                        Text(
                            "+ New",
                            modifier = Modifier
                                .clickable(onClick = onNewCohort)
                                .padding(vertical = 2.dp, horizontal = 6.dp),
                            color = BoloPalette.SageDeep,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                    state.cohorts.forEach { c ->
                        val selected = c.id == cohortId
                        val count = state.cohortStudentCounts[c.id] ?: 0
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (selected) BoloPalette.SageSoft else BoloPalette.Surface)
                                .border(
                                    1.dp,
                                    if (selected) BoloPalette.Sage else BoloPalette.Hairline,
                                    RoundedCornerShape(14.dp)
                                )
                                .clickable { vm.selectCohort(c.id) }
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(c.name, style = MaterialTheme.typography.titleMedium, color = BoloPalette.Ink)
                                Text(
                                    "Saved ${co.bolo.app.util.Format.relativeDay(c.createdAt)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = BoloPalette.InkFaint
                                )
                            }
                            Text(
                                "$count ${if (count == 1) "student" else "students"}",
                                style = MaterialTheme.typography.labelMedium,
                                color = BoloPalette.SageDeep
                            )
                        }
                    }
                }
            }

            item {
                // Primary CTA — sage solid button per design system.
                co.bolo.app.ui.components.BoloSolidButton(
                    label = "Start a session  →",
                    onClick = { cohortId?.let(onStartSession) },
                    enabled = cohortId != null
                )
            }

            if (state.students.isNotEmpty()) {
                item {
                    Hairline()
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "STUDENTS",
                        style = MaterialTheme.typography.labelSmall,
                        color = BoloPalette.InkFaint
                    )
                }

                items(state.students, key = { it.id }) { stu ->
                    StudentRow(
                        student = stu,
                        onOpenDashboard = onOpenDashboard
                    )
                }
            }

            item {
                Spacer(Modifier.height(8.dp))
                Hairline()
                HomeLink(label = "See your progress", onClick = {
                    state.students.firstOrNull()?.let { onOpenDashboard(it.id) }
                })
                Hairline()
                HomeLink(label = "Past sessions", onClick = { onOpenHistory(cohortId) })
                Hairline()
                HomeLink(label = "Settings", onClick = { onOpenSettings(cohortId) })
                Hairline()
            }

            item {
                Spacer(Modifier.height(16.dp))
                BoloPrivacyNote(
                    "Speech is transcribed by Google's speech service. Bolo keeps the transcript and the English share — never the audio."
                )
            }
        }

        item { Spacer(Modifier.height(40.dp)) }
    }
}

@Composable
private fun HomeLink(label: String, onClick: () -> Unit, hint: String? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            color = BoloPalette.Ink,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        if (hint != null) {
            Text(
                hint,
                color = BoloPalette.InkFaint,
                fontFamily = MonoData,
                fontSize = 12.sp,
                modifier = Modifier.padding(end = 10.dp)
            )
        }
        Text("→", color = BoloPalette.InkFaint, fontSize = 16.sp)
    }
}

@Composable
private fun EmptyState(label: String, onStart: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Welcome to Bolo",
            style = MaterialTheme.typography.headlineMedium,
            color = BoloPalette.Ink,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(12.dp))
        Text(
            "Make a cohort once, add students, and every session after will pick up where this one leaves off.",
            style = MaterialTheme.typography.bodyMedium,
            color = BoloPalette.InkMuted,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(32.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(BoloPalette.Ink)
                .clickable(onClick = onStart)
                .padding(vertical = 18.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(label, color = BoloPalette.Bg, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
private fun StudentRow(
    student: Student,
    onOpenDashboard: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(BoloPalette.Surface)
            .border(1.dp, BoloPalette.Hairline, RoundedCornerShape(12.dp))
            .clickable { onOpenDashboard(student.id) }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.padding(end = 12.dp)) {
            Text(student.displayName, style = MaterialTheme.typography.titleMedium, color = BoloPalette.Ink)
            Text(
                "Tap for trend",
                style = MaterialTheme.typography.labelSmall,
                color = BoloPalette.InkFaint
            )
        }
    }
}
