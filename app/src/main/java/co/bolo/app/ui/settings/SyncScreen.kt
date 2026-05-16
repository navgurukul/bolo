package co.bolo.app.ui.settings

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.bolo.app.ui.components.BoloCaption
import co.bolo.app.ui.components.BoloCard
import co.bolo.app.ui.components.BoloGhostButton
import co.bolo.app.ui.components.BoloPrivacyNote
import co.bolo.app.ui.components.BoloScreenTitle
import co.bolo.app.ui.components.BoloSectionLabel
import co.bolo.app.ui.components.BoloTopBar
import co.bolo.app.ui.components.Hairline
import co.bolo.app.ui.theme.BoloPalette
import co.bolo.app.ui.theme.MonoData
import co.bolo.app.ui.theme.SansUI

@Composable
fun SyncScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BoloPalette.Bg)
            .verticalScroll(rememberScrollState())
            .padding(PaddingValues(horizontal = 24.dp, vertical = 28.dp))
    ) {
        BoloTopBar(label = "Sync", onBack = onBack)
        Spacer(Modifier.height(22.dp))
        BoloScreenTitle("All caught up.")
        Spacer(Modifier.height(14.dp))
        Text(
            "Bolo only syncs session summaries — JSON files about two kilobytes each. Audio is never uploaded. Voice fingerprints never leave the phone.",
            color = BoloPalette.InkMuted,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(28.dp))
        BoloCard {
            Column {
                BoloCaption("Last sync")
                Spacer(Modifier.height(6.dp))
                Text(
                    "2 minutes ago",
                    color = BoloPalette.Ink,
                    fontFamily = SansUI,
                    fontWeight = FontWeight.Medium,
                    fontSize = 28.sp,
                    letterSpacing = (-0.5).sp
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    "12 sessions uploaded · 24.3 KB total",
                    color = BoloPalette.InkFaint,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        Spacer(Modifier.height(22.dp))
        BoloSectionLabel("Queue")
        Spacer(Modifier.height(14.dp))
        SyncQueueRow("Today · 9:30 · Daily life", "2.1 KB · synced")
        SyncQueueRow("Yesterday · 4:10 · Mock interview", "2.3 KB · synced")
        SyncQueueRow("Mon · 9:30 · News chat", "2.0 KB · synced")

        Spacer(Modifier.height(28.dp))
        BoloGhostButton("Sync now", onClick = { })
        Spacer(Modifier.height(10.dp))
        BoloPrivacyNote("Sync runs in the background on WiFi only. You can keep using Bolo offline.")
        Spacer(Modifier.height(40.dp))
    }
}

@Composable
private fun SyncQueueRow(label: String, detail: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(BoloPalette.Sage)
            )
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(label, color = BoloPalette.Ink, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(2.dp))
                Text(
                    detail,
                    color = BoloPalette.InkFaint,
                    fontFamily = MonoData,
                    fontSize = 12.sp
                )
            }
        }
        Hairline()
    }
}
