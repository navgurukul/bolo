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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.bolo.app.ui.components.BoloAvatar
import co.bolo.app.ui.components.BoloCaption
import co.bolo.app.ui.components.BoloItalicAccent
import co.bolo.app.ui.components.BoloQuietButton
import co.bolo.app.ui.components.BoloSolidButton
import co.bolo.app.ui.components.BreathingRedDot
import co.bolo.app.ui.components.EnglishRing
import co.bolo.app.ui.components.TopicPill
import co.bolo.app.ui.theme.BoloPalette
import co.bolo.app.ui.theme.MonoData
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
        BoloSolidButton(
            label = if (state.isPermissionGranted) "Start listening" else "Grant mic permission",
            enabled = canStart,
            onClick = {
                if (state.isPermissionGranted) vm.start() else onRequestPermission()
            }
        )
        Spacer(Modifier.height(12.dp))
        Text(
            "Speech is sent to Google for transcription. Bolo keeps only the text. The mic light stays on whenever we're listening.",
            style = MaterialTheme.typography.bodySmall,
            color = BoloPalette.InkFaint,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Recording(state: SessionUiState, vm: SessionViewModel, onEnd: (String) -> Unit) {
    val scope = rememberCoroutineScope()
    var paused by remember { mutableStateOf(false) }
    var topicOpen by remember { mutableStateOf(false) }
    var liveTopic by remember(state.topic) { mutableStateOf(state.topic.ifBlank { "Free talk" }) }
    val currentSpeaker = if (state.students.size > 1) "Group Session" else state.students.firstOrNull()?.displayName ?: "—"

    Box(modifier = Modifier.fillMaxSize().background(BoloPalette.Bg)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(PaddingValues(horizontal = 22.dp, vertical = 28.dp)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top row: breathing dot + MIC ON · topic (chevron) on left; pause + elapsed on right
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { topicOpen = true }
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (paused) BoloPalette.InkFaint else BoloPalette.MicRed)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        "${if (paused) "PAUSED" else "MIC ON"} · ${liveTopic.uppercase()}",
                        color = BoloPalette.InkMuted,
                        fontFamily = MonoData,
                        fontSize = 11.sp,
                        letterSpacing = 1.5.sp
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "▾",
                        color = BoloPalette.InkMuted,
                        fontSize = 11.sp
                    )
                }
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .border(1.dp, BoloPalette.Hairline, RoundedCornerShape(999.dp))
                        .clickable { paused = !paused }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        if (paused) "▶" else "II",
                        color = BoloPalette.Ink,
                        fontFamily = MonoData,
                        fontSize = 12.sp
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        Format.clockMs(state.elapsedMs),
                        color = BoloPalette.Ink,
                        fontFamily = MonoData,
                        fontSize = 12.sp
                    )
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

            Spacer(Modifier.height(28.dp))
            Text(
                "TAP ACTIVE SPEAKER TO TRACK TIME", 
                style = MaterialTheme.typography.labelSmall, 
                color = BoloPalette.InkFaint,
                fontFamily = MonoData,
                letterSpacing = 1.2.sp
            )
            Spacer(Modifier.height(12.dp))
            
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // General Group option
                val isGeneralActive = state.activeSpeakerId == null
                val generalBg = if (isGeneralActive) BoloPalette.SageSoft else BoloPalette.Surface
                val generalBorder = if (isGeneralActive) BoloPalette.Sage else BoloPalette.Hairline
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(generalBg)
                        .border(1.dp, generalBorder, RoundedCornerShape(999.dp))
                        .clickable { vm.selectActiveSpeaker(null) }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(BoloPalette.SurfaceMuted)
                            .border(1.dp, if (isGeneralActive) BoloPalette.Sage else BoloPalette.Hairline, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "👥",
                            fontSize = 11.sp
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Group",
                        color = BoloPalette.Ink,
                        fontWeight = if (isGeneralActive) FontWeight.SemiBold else FontWeight.Normal,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // Student options
                state.students.forEach { student ->
                    val isActive = state.activeSpeakerId == student.id
                    val bg = if (isActive) BoloPalette.SageSoft else BoloPalette.Surface
                    val border = if (isActive) BoloPalette.Sage else BoloPalette.Hairline
                    val speakingMs = state.studentSpeechMs[student.id] ?: 0L
                    
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(bg)
                            .border(1.dp, border, RoundedCornerShape(999.dp))
                            .clickable { vm.selectActiveSpeaker(student.id) }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BoloAvatar(
                            name = student.displayName,
                            size = 24.dp,
                            active = isActive
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            student.displayName,
                            color = BoloPalette.Ink,
                            fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        if (speakingMs > 0L) {
                            Spacer(Modifier.width(6.dp))
                            Text(
                                "(${Format.clockMs(speakingMs)})",
                                color = if (isActive) BoloPalette.SageDeep else BoloPalette.InkFaint,
                                fontFamily = MonoData,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            BoloSolidButton(
                label = "End session",
                danger = true,
                onClick = {
                    scope.launch {
                        val id = vm.end()
                        onEnd(id)
                    }
                }
            )
            Spacer(Modifier.height(12.dp))
            Text(
                "Only the English percentage is saved.",
                style = MaterialTheme.typography.bodySmall,
                color = BoloPalette.InkFaint
            )
        }

        if (paused) {
            PauseOverlay(
                onResume = { paused = false },
                onEnd = {
                    scope.launch {
                        val id = vm.end()
                        onEnd(id)
                    }
                }
            )
        }

        if (topicOpen) {
            TopicSwitchSheet(
                current = liveTopic,
                onPick = { picked ->
                    liveTopic = picked
                    topicOpen = false
                },
                onClose = { topicOpen = false }
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────
// PauseOverlay — dimmed scrim + centred card with italic "Paused."
// ─────────────────────────────────────────────────────────────
@Composable
fun PauseOverlay(onResume: () -> Unit, onEnd: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BoloPalette.Ink.copy(alpha = 0.55f))
            .clickable(enabled = false) { },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .width(280.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(BoloPalette.Surface)
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BoloItalicAccent("Paused.", fontSize = 26)
            Spacer(Modifier.height(8.dp))
            Text(
                "The mic is muted. Nothing is being counted right now.",
                color = BoloPalette.InkMuted,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(22.dp))
            BoloSolidButton("Resume listening", onClick = onResume)
            Spacer(Modifier.height(8.dp))
            BoloQuietButton("End the session", onClick = onEnd)
        }
    }
}

// ─────────────────────────────────────────────────────────────
// TopicSwitchSheet — bottom sheet for swapping topic mid-session
// ─────────────────────────────────────────────────────────────
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TopicSwitchSheet(
    current: String,
    onPick: (String) -> Unit,
    onClose: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BoloPalette.Ink.copy(alpha = 0.45f))
            .clickable(onClick = onClose),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .background(BoloPalette.Surface)
                .clickable(enabled = false) { }
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 18.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(40.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(BoloPalette.Ink.copy(alpha = 0.22f))
                )
            }
            BoloCaption("Switch topic mid-session")
            Spacer(Modifier.height(4.dp))
            BoloItalicAccent("What are we talking about now?", fontSize = 22)
            Spacer(Modifier.height(16.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                co.bolo.app.ui.components.BOLO_TOPICS.forEach { tp ->
                    TopicPill(
                        label = tp,
                        selected = tp == current,
                        onClick = { onPick(tp) }
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(
                "The session continues — we'll just tag what's said from here on with the new topic.",
                color = BoloPalette.InkFaint,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(Modifier.height(8.dp))
        }
    }
}
