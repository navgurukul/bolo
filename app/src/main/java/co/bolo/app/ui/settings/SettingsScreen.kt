package co.bolo.app.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.verticalScroll
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.bolo.app.BuildConfig
import co.bolo.app.ui.components.BoloGhostButton
import co.bolo.app.ui.components.BoloScreenTitle
import co.bolo.app.ui.components.BoloSectionLabel
import co.bolo.app.ui.components.BoloSolidButton
import co.bolo.app.ui.components.BoloTopBar
import co.bolo.app.ui.components.Hairline
import co.bolo.app.ui.theme.BoloPalette
import co.bolo.app.ui.theme.MonoData

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onManageStudents: () -> Unit,
    onDataCleared: () -> Unit,
    vm: SettingsViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsStateWithLifecycle()
    var clearDialogOpen by remember { mutableStateOf(false) }

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
        SettingRow(label = "Cohort name", value = state.cohort?.name ?: "No cohort yet")
        SettingRow(
            label = "Manage students",
            value = "${state.studentCount} enrolled",
            chevron = true,
            onClick = onManageStudents
        )

        Spacer(Modifier.height(24.dp))
        BoloSectionLabel("Privacy")
        SettingRow(
            label = "Clear all data & restart setup",
            value = "",
            chevron = true,
            danger = true,
            onClick = { clearDialogOpen = true }
        )

        Spacer(Modifier.height(24.dp))
        BoloSectionLabel("About")
        SettingRow(
            label = "Version",
            value = "${BuildConfig.VERSION_NAME} · build ${BuildConfig.VERSION_CODE}",
            mono = true
        )

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

    if (clearDialogOpen) {
        ClearAllDataDialog(
            onConfirm = {
                clearDialogOpen = false
                vm.clearAllData(onDataCleared)
            },
            onDismiss = { clearDialogOpen = false }
        )
    }
}

@Composable
private fun ClearAllDataDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BoloPalette.Ink.copy(alpha = 0.55f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 28.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(BoloPalette.Surface)
                .clickable(enabled = false) { }
                .padding(26.dp)
        ) {
            Text(
                "Clear everything?",
                color = BoloPalette.Ink,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(10.dp))
            Text(
                "Cohorts, students, sessions, transcripts and your consent will all be deleted from this phone. " +
                    "You'll be taken back to first-run setup. This can't be undone.",
                color = BoloPalette.InkMuted,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(22.dp))
            BoloSolidButton("Yes, clear everything", danger = true, onClick = onConfirm)
            Spacer(Modifier.height(8.dp))
            BoloGhostButton("Cancel", onClick = onDismiss)
        }
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

