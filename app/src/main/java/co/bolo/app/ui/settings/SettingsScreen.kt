package co.bolo.app.ui.settings

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.verticalScroll
import co.bolo.app.ui.components.BoloScreenTitle
import co.bolo.app.ui.components.BoloSectionLabel
import co.bolo.app.ui.components.BoloTopBar
import co.bolo.app.ui.components.Hairline
import co.bolo.app.ui.theme.BoloPalette
import co.bolo.app.ui.theme.MonoData

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onManageStudents: () -> Unit,
    onOpenSync: () -> Unit,
    onForgetVoice: () -> Unit
) {
    var ringOn by remember { mutableStateOf(true) }
    var vibrOn by remember { mutableStateOf(false) }
    var topicReminder by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BoloPalette.Bg)
            .verticalScroll(rememberScrollState())
            .padding(PaddingValues(horizontal = 24.dp, vertical = 28.dp))
    ) {
        BoloTopBar(label = "Settings", onBack = onBack)
        Spacer(Modifier.height(22.dp))
        BoloScreenTitle("Settings.")
        Spacer(Modifier.height(24.dp))

        BoloSectionLabel("Cohort")
        SettingRow(label = "Cohort name", value = "Pune · Batch 14")
        SettingRow(label = "Program code", value = "NGK-7T4P", mono = true)
        SettingRow(label = "Facilitator", value = "Anjali")
        SettingRow(
            label = "Manage students",
            value = "6 enrolled",
            chevron = true,
            onClick = onManageStudents
        )

        Spacer(Modifier.height(24.dp))
        BoloSectionLabel("During a session")
        ToggleRow(
            label = "Show soft colour ring",
            sub = "Green when English flows. Amber on drift.",
            on = ringOn,
            onChange = { ringOn = it }
        )
        ToggleRow(
            label = "Vibrate on long drift",
            sub = "One pulse after 30s in another language.",
            on = vibrOn,
            onChange = { vibrOn = it }
        )
        ToggleRow(
            label = "Show topic switch reminder",
            sub = "Suggest switching topic after 20 min.",
            on = topicReminder,
            onChange = { topicReminder = it }
        )

        Spacer(Modifier.height(24.dp))
        BoloSectionLabel("Sync")
        SettingRow(label = "Last sync", value = "2 minutes ago", chevron = true, onClick = onOpenSync)
        SettingRow(label = "Only sync on WiFi", value = "On")

        Spacer(Modifier.height(24.dp))
        BoloSectionLabel("Privacy")
        SettingRow(
            label = "Forget my voice",
            value = "",
            chevron = true,
            danger = true,
            onClick = onForgetVoice
        )
        SettingRow(label = "Export my data", value = "", chevron = true)
        SettingRow(label = "Privacy policy", value = "", chevron = true)

        Spacer(Modifier.height(24.dp))
        BoloSectionLabel("About")
        SettingRow(label = "Version", value = "0.1.4 · build 207", mono = true)
        SettingRow(label = "Acknowledgements", value = "", chevron = true)

        Spacer(Modifier.height(28.dp))
        Text(
            "Made with care for shared classrooms.",
            color = BoloPalette.InkFaint,
            fontFamily = MonoData,
            fontSize = 12.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(Modifier.height(40.dp))
    }
}

@Composable
fun SettingRow(
    label: String,
    value: String,
    mono: Boolean = false,
    chevron: Boolean = false,
    danger: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
                .padding(vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                label,
                color = if (danger) BoloPalette.MicRed else BoloPalette.Ink,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            if (value.isNotEmpty()) {
                Text(
                    value,
                    color = BoloPalette.InkFaint,
                    fontSize = 13.sp,
                    fontFamily = if (mono) MonoData else FontFamily.Default,
                    modifier = Modifier.padding(end = if (chevron) 8.dp else 0.dp)
                )
            }
            if (chevron) {
                Text("→", color = BoloPalette.InkFaint, fontSize = 14.sp)
            }
        }
        Hairline()
    }
}

@Composable
fun ToggleRow(
    label: String,
    sub: String,
    on: Boolean,
    onChange: (Boolean) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(label, color = BoloPalette.Ink, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(2.dp))
                Text(sub, color = BoloPalette.InkFaint, style = MaterialTheme.typography.bodySmall)
            }
            ToggleSwitch(on = on, onClick = { onChange(!on) })
        }
        Hairline()
    }
}

@Composable
private fun ToggleSwitch(on: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(width = 38.dp, height = 22.dp)
            .clip(RoundedCornerShape(11.dp))
            .background(if (on) BoloPalette.Sage else BoloPalette.SurfaceMuted)
            .clickable(onClick = onClick),
        contentAlignment = if (on) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 2.dp)
                .size(18.dp)
                .clip(CircleShape)
                .background(Color.White)
        )
    }
}
