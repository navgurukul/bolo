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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.bolo.app.data.model.Session
import co.bolo.app.data.model.Student
import co.bolo.app.ui.components.BreathingRedDot
import co.bolo.app.ui.components.Hairline
import co.bolo.app.ui.components.Wordmark
import co.bolo.app.ui.theme.BoloPalette
import co.bolo.app.util.Format

@Composable
fun HomeScreen(
    onStartSession: (cohortId: String) -> Unit,
    onOpenDashboard: (studentId: String) -> Unit,
    onEnroll: (cohortId: String, studentId: String) -> Unit,
    onOpenSession: (sessionId: String) -> Unit,
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
                BreathingRedDot(active = false, size = 9.dp)
            }
        }

        if (state.recentSessions.isEmpty() && state.cohorts.isNotEmpty() && state.students.isEmpty()) {
            item {
                EmptyState(onStart = { state.cohorts.firstOrNull()?.let { onStartSession(it.id) } })
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
                Column {
                    Text("COHORT", style = MaterialTheme.typography.labelSmall, color = BoloPalette.InkFaint)
                    Spacer(Modifier.height(8.dp))
                    state.cohorts.forEach { c ->
                        val selected = c.id == cohortId
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
                                    "Created ${Format.relativeDay(c.createdAt)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = BoloPalette.InkFaint
                                )
                            }
                            Text(
                                "${state.students.size} students",
                                style = MaterialTheme.typography.labelMedium,
                                color = BoloPalette.SageDeep
                            )
                        }
                    }
                }
            }

            item {
                // Big Start button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(BoloPalette.Ink)
                        .clickable(enabled = cohortId != null) { cohortId?.let(onStartSession) }
                        .padding(vertical = 22.dp, horizontal = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(BoloPalette.MicRed.copy(alpha = 0.16f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(BoloPalette.MicRed)
                            )
                        }
                        Spacer(Modifier.size(14.dp))
                        Text(
                            "Start a session",
                            color = BoloPalette.Bg,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
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
                        onOpenDashboard = onOpenDashboard,
                        onEnroll = { onEnroll(stu.cohortId, stu.id) }
                    )
                }
            }

            if (state.recentSessions.isNotEmpty()) {
                item {
                    Spacer(Modifier.height(8.dp))
                    Hairline()
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "RECENT SESSIONS",
                        style = MaterialTheme.typography.labelSmall,
                        color = BoloPalette.InkFaint
                    )
                }

                items(state.recentSessions, key = { it.id }) { ses ->
                    RecentSessionRow(ses, onOpen = { onOpenSession(ses.id) })
                }
            }
        }

        item { Spacer(Modifier.height(40.dp)) }
    }
}

@Composable
private fun EmptyState(onStart: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Start your first session",
            style = MaterialTheme.typography.headlineMedium,
            color = BoloPalette.Ink,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(12.dp))
        Text(
            "Begin a live session to analyze English usage in your group.",
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
            Text("Begin Setup", color = BoloPalette.Bg, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
private fun StudentRow(
    student: Student,
    onOpenDashboard: (String) -> Unit,
    onEnroll: () -> Unit
) {
    val enrolled = student.enrolledAt != null
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
                if (enrolled) "Voice enrolled · tap for trend"
                else "Tap to enroll voice",
                style = MaterialTheme.typography.labelSmall,
                color = if (enrolled) BoloPalette.SageDeep else BoloPalette.InkFaint
            )
        }
        if (!enrolled) {
            Text(
                "Enroll",
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(BoloPalette.SageSoft)
                    .clickable(onClick = onEnroll)
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                color = BoloPalette.SageDeep,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
private fun RecentSessionRow(session: Session, onOpen: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(BoloPalette.Surface)
            .border(1.dp, BoloPalette.Hairline, RoundedCornerShape(12.dp))
            .clickable(onClick = onOpen)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(session.topic, style = MaterialTheme.typography.titleMedium, color = BoloPalette.Ink)
            Text(
                Format.relativeDay(session.startedAt),
                style = MaterialTheme.typography.labelSmall,
                color = BoloPalette.InkFaint
            )
        }
        Text(
            Format.percent(session.englishShare) + " English",
            style = MaterialTheme.typography.titleMedium,
            color = BoloPalette.SageDeep
        )
    }
}
