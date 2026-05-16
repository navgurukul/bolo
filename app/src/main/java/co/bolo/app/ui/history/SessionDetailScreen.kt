package co.bolo.app.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.bolo.app.ui.components.BOLO_COHORT
import co.bolo.app.ui.components.BOLO_HISTORY
import co.bolo.app.ui.components.BoloCaption
import co.bolo.app.ui.components.BoloCard
import co.bolo.app.ui.components.BoloItalicAccent
import co.bolo.app.ui.components.BoloScreenTitle
import co.bolo.app.ui.components.BoloSecondaryMetric
import co.bolo.app.ui.components.BoloSectionLabel
import co.bolo.app.ui.components.BoloTopBar
import co.bolo.app.ui.components.MockStudent
import co.bolo.app.ui.theme.BoloPalette
import co.bolo.app.ui.theme.MonoData
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SessionDetailScreen(
    sessionId: String,
    onBack: () -> Unit
) {
    val s = BOLO_HISTORY.firstOrNull { it.id == sessionId } ?: BOLO_HISTORY.first()
    val sorted = BOLO_COHORT.sortedByDescending { it.pct }.take(s.students)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BoloPalette.Bg),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 28.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        item {
            BoloTopBar(label = s.date, onBack = onBack)
            Spacer(Modifier.height(18.dp))
            BoloCaption("${s.topic} · ${s.duration} min · ${s.students} students")
            Spacer(Modifier.height(4.dp))
            BoloScreenTitle("${s.pct}% English.")
            Spacer(Modifier.height(24.dp))
            BoloCard {
                Column {
                    BoloCaption("English share over the session")
                    Spacer(Modifier.height(12.dp))
                    Timeline()
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("0:00", color = BoloPalette.InkFaint, fontFamily = MonoData, fontSize = 10.sp)
                        Text("${s.duration / 2}:00", color = BoloPalette.InkFaint, fontFamily = MonoData, fontSize = 10.sp)
                        Text("${s.duration}:00", color = BoloPalette.InkFaint, fontFamily = MonoData, fontSize = 10.sp)
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
            BoloSectionLabel("Per student")
            Spacer(Modifier.height(8.dp))
        }
        items(sorted, key = { it.id }) { stu ->
            PerStudentRow(stu, rank = sorted.indexOf(stu) + 1)
        }
        item {
            Spacer(Modifier.height(24.dp))
            BoloCard(accent = true) {
                Column {
                    BoloCaption("What the room said back")
                    Spacer(Modifier.height(8.dp))
                    BoloItalicAccent(
                        text = "“We switched when nobody knew the word for ‘interest rate’.”",
                        fontSize = 18
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "From the post-session reflection",
                        color = BoloPalette.InkFaint,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Spacer(Modifier.height(20.dp))
            BoloSecondaryMetric("Sustained English (≥ 60s)", "6 stretches")
            BoloSecondaryMetric("Soft drift events", "3")
            BoloSecondaryMetric("Words in English", "${s.pct - 3}%")
            BoloSecondaryMetric("Synced to cloud", "✓ 2 min ago")
            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun Timeline() {
    val segments = 40
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        for (i in 0 until segments) {
            val noise = sin(i * 1.3) * 0.5 + cos(i * 0.7) * 0.5
            val isEn = noise > -0.1
            val frac = if (isEn) {
                (0.5f + (abs(sin(i * 0.9)).toFloat() * 0.3f)).coerceIn(0.1f, 1f)
            } else {
                (0.2f + (abs(cos(i.toDouble())).toFloat() * 0.25f)).coerceIn(0.1f, 1f)
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(frac)
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (isEn) BoloPalette.Sage else BoloPalette.SurfaceMuted)
            )
        }
    }
}

@Composable
private fun PerStudentRow(stu: MockStudent, rank: Int) {
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
        Text(
            stu.name,
            color = BoloPalette.Ink,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
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
                    .fillMaxWidth(stu.pct / 100f)
                    .height(4.dp)
                    .background(BoloPalette.Sage)
            )
        }
        Text(
            "${stu.pct}%",
            color = BoloPalette.Ink,
            fontFamily = MonoData,
            fontSize = 13.sp
        )
    }
    androidx.compose.foundation.layout.Spacer(
        Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(BoloPalette.Hairline)
    )
}
