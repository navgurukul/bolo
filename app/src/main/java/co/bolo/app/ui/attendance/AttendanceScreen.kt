package co.bolo.app.ui.attendance

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.bolo.app.data.model.Student
import co.bolo.app.ui.components.BoloAvatar
import co.bolo.app.ui.components.BoloCaption
import co.bolo.app.ui.components.BoloScreenTitle
import co.bolo.app.ui.components.BoloSolidButton
import co.bolo.app.ui.components.BoloTopBar
import co.bolo.app.ui.components.Hairline
import co.bolo.app.ui.theme.BoloPalette
import co.bolo.app.ui.theme.MonoData
import co.bolo.app.ui.theme.SansUI

/**
 * "Who's here today?" — the entry-point to a session.
 *
 * Lists every student persisted in the chosen cohort, lets the facilitator
 * tap to toggle attendance, and offers an inline add-student field so a
 * new face can be added without leaving the flow. All names persist in
 * Room; the next session will already have them.
 */
@Composable
fun AttendanceScreen(
    onBack: () -> Unit,
    onAddStudent: () -> Unit,
    onStart: (presentIds: List<String>) -> Unit,
    vm: AttendanceViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsStateWithLifecycle()
    var draftName by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BoloPalette.Bg),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 28.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        item {
            BoloTopBar(label = "Who's here today?", onBack = onBack)
            Spacer(Modifier.height(22.dp))
            BoloScreenTitle("Tap each student\nwho's in the room.")
            Spacer(Modifier.height(8.dp))
            Text(
                "Only people you tap get credited during the session. Untapped time is counted as group time — not against anyone.",
                color = BoloPalette.InkMuted,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(22.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BoloCaption("Cohort · ${state.totalCount}")
                Text(
                    "${state.presentCount} selected",
                    color = BoloPalette.SageDeep,
                    fontFamily = MonoData,
                    fontSize = 12.sp
                )
            }
            Spacer(Modifier.height(8.dp))
        }

        if (state.students.isEmpty()) {
            item {
                Spacer(Modifier.height(28.dp))
                Text(
                    "No one in this cohort yet.",
                    color = BoloPalette.InkMuted,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    "Add the first student below — Bolo will remember them across sessions.",
                    color = BoloPalette.InkFaint,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(Modifier.height(20.dp))
            }
        } else {
            items(state.students, key = { it.id }) { s ->
                StudentToggleRow(
                    student = s,
                    on = s.id in state.presentIds,
                    onClick = { vm.toggle(s.id) }
                )
            }
        }

        item {
            Spacer(Modifier.height(18.dp))
            BoloCaption("Add someone new")
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(BoloPalette.SageSoft),
                    contentAlignment = Alignment.Center
                ) {
                    Text("+", color = BoloPalette.SageDeep, fontSize = 22.sp)
                }
                Spacer(Modifier.width(14.dp))
                BasicTextField(
                    value = draftName,
                    onValueChange = { draftName = it },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    textStyle = TextStyle(
                        color = BoloPalette.Ink,
                        fontFamily = SansUI,
                        fontSize = 16.sp
                    ),
                    cursorBrush = SolidColor(BoloPalette.Ink),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Done
                    ),
                    decorationBox = { inner ->
                        if (draftName.isEmpty()) {
                            Text(
                                "First name…",
                                color = BoloPalette.InkFaint,
                                fontFamily = SansUI,
                                fontSize = 16.sp
                            )
                        }
                        inner()
                    }
                )
                val enabled = draftName.trim().isNotEmpty()
                Text(
                    "Add",
                    color = if (enabled) BoloPalette.SageDeep else BoloPalette.InkFaint,
                    fontWeight = FontWeight.Medium,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(if (enabled) BoloPalette.SageSoft else BoloPalette.Bg)
                        .clickable(enabled = enabled) {
                            vm.addStudent(draftName) {}
                            draftName = ""
                        }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                )
            }
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(BoloPalette.Hairline)
            )

            Spacer(Modifier.height(28.dp))
            BoloSolidButton(
                label = if (state.presentCount == 0) "Tap someone to start"
                        else "Start with ${state.presentCount} ${if (state.presentCount == 1) "student" else "students"} →",
                onClick = { onStart(state.presentIds.toList()) },
                enabled = state.presentCount > 0
            )
            Spacer(Modifier.height(14.dp))
            Text(
                "Anyone you've added once stays in this cohort forever. Bring more faces next time without re-typing.",
                color = BoloPalette.InkFaint,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun StudentToggleRow(
    student: Student,
    on: Boolean,
    onClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BoloAvatar(name = student.displayName, size = 36.dp, active = on)
            Spacer(Modifier.width(14.dp))
            Text(
                student.displayName,
                modifier = Modifier.weight(1f),
                color = if (on) BoloPalette.Ink else BoloPalette.InkFaint,
                style = MaterialTheme.typography.bodyLarge
            )
            ToggleCircle(on = on)
        }
        Hairline()
    }
}

@Composable
private fun ToggleCircle(on: Boolean) {
    Box(
        modifier = Modifier
            .size(22.dp)
            .clip(CircleShape)
            .background(if (on) BoloPalette.Sage else BoloPalette.Bg)
            .border(
                if (on) 0.dp else 1.5.dp,
                if (on) BoloPalette.Sage else BoloPalette.Ink.copy(alpha = 0.22f),
                CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        if (on) {
            Text("✓", color = BoloPalette.Surface, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}
