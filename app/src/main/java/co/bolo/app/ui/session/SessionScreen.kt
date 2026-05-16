package co.bolo.app.ui.session

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.bolo.app.ui.components.BreathingRedDot
import co.bolo.app.ui.components.EnglishRing
import co.bolo.app.ui.components.TopicPill
import co.bolo.app.ui.theme.BoloPalette
import co.bolo.app.util.Format
import kotlinx.coroutines.launch

@Composable
fun SessionScreen(
    onEnd: (sessionId: String) -> Unit,
    onCancel: () -> Unit,
    vm: SessionViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val permissions = mutableListOf(Manifest.permission.RECORD_AUDIO)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        permissions.add(Manifest.permission.POST_NOTIFICATIONS)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        vm.onPermissionResult(results.values.all { it })
    }

    LaunchedEffect(Unit) {
        val allGranted = permissions.all {
            ContextCompat.checkSelfPermission(context, it) == PermissionChecker.PERMISSION_GRANTED
        }
        vm.onPermissionResult(allGranted)
    }

    when (state.phase) {
        SessionUiState.Phase.PickingTopic -> TopicPicker(state, vm, onCancel) {
            launcher.launch(permissions.toTypedArray())
        }
        SessionUiState.Phase.Running, SessionUiState.Phase.Ending -> Recording(state, vm, onEnd)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TopicPicker(
    state: SessionUiState,
    vm: SessionViewModel,
    onCancel: () -> Unit,
    onRequestPermission: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BoloPalette.Bg)
            .padding(PaddingValues(horizontal = 22.dp, vertical = 28.dp))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Cancel",
                modifier = Modifier.clickable(onClick = onCancel),
                style = MaterialTheme.typography.labelLarge,
                color = BoloPalette.InkMuted
            )
            BreathingRedDot(active = false, size = 9.dp)
        }
        Spacer(Modifier.height(28.dp))
        Text("Today we'll talk about…", style = MaterialTheme.typography.headlineMedium, color = BoloPalette.Ink)
        Spacer(Modifier.height(6.dp))
        Text(
            "Pick a topic, or write your own. You can change it next session.",
            style = MaterialTheme.typography.bodyMedium,
            color = BoloPalette.InkMuted
        )
        Spacer(Modifier.height(20.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            SUGGESTED_TOPICS.forEach { t ->
                TopicPill(label = t, selected = state.topic == t, onClick = { vm.setTopic(t) })
            }
        }
        Spacer(Modifier.height(20.dp))
        Text("OR YOUR OWN", style = MaterialTheme.typography.labelSmall, color = BoloPalette.InkFaint)
        Spacer(Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(BoloPalette.Surface)
                .border(1.dp, BoloPalette.Hairline, RoundedCornerShape(14.dp))
                .padding(horizontal = 14.dp, vertical = 14.dp)
        ) {
            if (state.customTopic.isEmpty() && state.topic.isEmpty()) {
                Text(
                    "Travel stories, family memories, anything…",
                    style = MaterialTheme.typography.bodyMedium,
                    color = BoloPalette.InkFaint
                )
            }
            BasicTextField(
                value = state.customTopic,
                onValueChange = vm::setCustomTopic,
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = BoloPalette.Ink),
                cursorBrush = SolidColor(BoloPalette.Ink),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(Modifier.height(40.dp))
        val canStart = vm.resolvedTopic().isNotBlank() && state.students.isNotEmpty()
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(if (canStart) BoloPalette.Ink else BoloPalette.SurfaceMuted)
                .clickable(enabled = canStart) {
                    if (state.isPermissionGranted) {
                        vm.start()
                    } else {
                        onRequestPermission()
                    }
                }
                .padding(vertical = 18.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                if (state.isPermissionGranted) "Start listening" else "Grant Mic Permission",
                color = if (canStart) BoloPalette.Bg else BoloPalette.InkFaint,
                style = MaterialTheme.typography.titleMedium
            )
        }
        Spacer(Modifier.height(12.dp))
        Text(
            "No audio leaves this phone. The mic light stays on whenever we're listening.",
            style = MaterialTheme.typography.bodySmall,
            color = BoloPalette.InkFaint,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun Recording(state: SessionUiState, vm: SessionViewModel, onEnd: (String) -> Unit) {
    val scope = rememberCoroutineScope()
    // For MVP without diarization, we don't display "Now Speaking" reliably
    val currentSpeaker = if (state.students.size > 1) "Group Session" else state.students.firstOrNull()?.displayName ?: "—"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BoloPalette.Bg)
            .padding(PaddingValues(horizontal = 22.dp, vertical = 28.dp)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(state.topic, style = MaterialTheme.typography.titleMedium, color = BoloPalette.Ink)
            Row(verticalAlignment = Alignment.CenterVertically) {
                BreathingRedDot(active = true, size = 10.dp)
                Spacer(Modifier.padding(start = 8.dp))
                Text("REC", style = MaterialTheme.typography.labelSmall, color = BoloPalette.MicRed)
            }
        }
        Spacer(Modifier.height(40.dp))

        EnglishRing(share = state.englishShareRolling, drift = state.drifting, diameter = 240.dp) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    Format.clockMs(state.elapsedMs),
                    style = MaterialTheme.typography.displayMedium,
                    color = BoloPalette.Ink
                )
                Text(
                    "${(state.englishShareRolling * 100).toInt()}% English usage",
                    style = MaterialTheme.typography.labelMedium,
                    color = BoloPalette.SageDeep
                )
            }
        }

        Spacer(Modifier.height(32.dp))
        Text("PARTICIPANTS", style = MaterialTheme.typography.labelSmall, color = BoloPalette.InkFaint)
        Spacer(Modifier.height(6.dp))
        Text(currentSpeaker, style = MaterialTheme.typography.headlineSmall, color = BoloPalette.Ink)

        Spacer(Modifier.weight(1f))
        
        // Show a snippet of the transcript internally if needed for debugging or transparency
        // For MVP, we'll keep it hidden as per requirements, but the state is there.

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(BoloPalette.SurfaceMuted)
                .clickable {
                    scope.launch {
                        val id = vm.end()
                        onEnd(id)
                    }
                }
                .padding(vertical = 18.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("End session", style = MaterialTheme.typography.titleMedium, color = BoloPalette.Ink)
        }
        Spacer(Modifier.height(12.dp))
        Text(
            "Only the English percentage is saved.",
            style = MaterialTheme.typography.bodySmall,
            color = BoloPalette.InkFaint
        )
    }
}
