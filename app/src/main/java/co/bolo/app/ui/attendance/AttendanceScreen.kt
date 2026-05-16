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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.bolo.app.ui.components.BOLO_COHORT
import co.bolo.app.ui.components.BoloAvatar
import co.bolo.app.ui.components.BoloCaption
import co.bolo.app.ui.components.BoloScreenTitle
import co.bolo.app.ui.components.BoloSolidButton
import co.bolo.app.ui.components.BoloTopBar
import co.bolo.app.ui.components.Hairline
import co.bolo.app.ui.components.MockStudent
import co.bolo.app.ui.theme.BoloPalette
import co.bolo.app.ui.theme.MonoData

@Composable
fun AttendanceScreen(
    onBack: () -> Unit,
    onAddStudent: () -> Unit,
    onStart: (count: Int) -> Unit
) {
    var present by remember {
        mutableStateOf(BOLO_COHORT.map { it.id }.toSet())
    }

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
                "Only people you tap will be counted. The phone will still hear everyone — it just won't credit unknown speakers.",
                color = BoloPalette.InkMuted,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(22.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                BoloCaption("Enrolled · ${BOLO_COHORT.size}")
                Text(
                    "${present.size} selected",
                    color = BoloPalette.SageDeep,
                    fontFamily = MonoData,
                    fontSize = 12.sp
                )
            }
            Spacer(Modifier.height(8.dp))
        }
        items(BOLO_COHORT, key = { it.id }) { s ->
            StudentToggleRow(
                student = s,
                on = present.contains(s.id),
                onClick = {
                    present = if (present.contains(s.id)) present - s.id else present + s.id
                }
            )
        }
        item {
            Spacer(Modifier.height(14.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onAddStudent)
                    .padding(vertical = 14.dp),
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
                Spacer(Modifier.width(12.dp))
                Text(
                    "Add a new student to the cohort",
                    color = BoloPalette.SageDeep,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(Modifier.height(24.dp))
            BoloSolidButton(
                "Start with ${present.size} ${if (present.size == 1) "student" else "students"} →",
                onClick = { onStart(present.size) },
                enabled = present.isNotEmpty()
            )
            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun StudentToggleRow(
    student: MockStudent,
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
            BoloAvatar(name = student.name, size = 36.dp, active = on)
            Spacer(Modifier.width(14.dp))
            Text(
                student.name,
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
