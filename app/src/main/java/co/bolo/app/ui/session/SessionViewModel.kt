package co.bolo.app.ui.session

import android.content.Context
import android.content.Intent
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.bolo.app.analysis.ChunkAnalysis
import co.bolo.app.data.model.Session
import co.bolo.app.data.model.SpeakerStat
import co.bolo.app.data.model.Student
import co.bolo.app.data.model.TranscriptChunk
import co.bolo.app.data.repo.CohortRepo
import co.bolo.app.data.repo.RecognizerStatus
import co.bolo.app.data.repo.SessionManager
import co.bolo.app.data.repo.SessionRepo
import co.bolo.app.service.SessionService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class SessionUiState(
    val cohortId: String = "",
    val students: List<Student> = emptyList(),
    val phase: Phase = Phase.PickingTopic,
    val topic: String = "",
    val customTopic: String = "",
    val elapsedMs: Long = 0L,
    val englishShareRolling: Float = 0f,
    val totalSpeechMs: Long = 0L,
    val totalEnglishMs: Long = 0L,
    val isPermissionGranted: Boolean = false,
    val chunks: List<ChunkAnalysis> = emptyList(),
    val activeSpeakerId: String? = null,
    val studentSpeechMs: Map<String, Long> = emptyMap(),
    val paused: Boolean = false,
    val recognizerStatus: RecognizerStatus = RecognizerStatus.Ok
) {
    enum class Phase { PickingTopic, Running, Ending }
}

val SUGGESTED_TOPICS = listOf("Daily life", "Mock interview", "News chat", "Tech", "Free talk")

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val cohortRepo: CohortRepo,
    private val sessionRepo: SessionRepo,
    private val sessionManager: SessionManager,
    @ApplicationContext private val context: Context,
    savedState: SavedStateHandle
) : ViewModel() {

    private val cohortId: String = savedState["cohortId"] ?: ""
    /**
     * Comma-joined list of student ids selected on the attendance screen.
     * Empty / null means "everyone in the cohort". The viewmodel filters
     * the observed roster down to this set so untapped students do not
     * appear as taggable tiles during the session.
     */
    private val presentArg: String? = savedState["present"]
    private val presentIds: Set<String> =
        presentArg?.split(',')?.map { it.trim() }?.filter { it.isNotEmpty() }?.toSet().orEmpty()

    private val _state = MutableStateFlow(SessionUiState(cohortId = cohortId))
    val state: StateFlow<SessionUiState> = _state.asStateFlow()
    private var sessionId: String? = null
    private var startedAt: Long = 0L
    private var timerJob: Job? = null
    private val studentSpeechMsMap = mutableMapOf<String, Long>()

    init {
        viewModelScope.launch {
            cohortRepo.observeStudents(cohortId).collect { list ->
                val filtered = if (presentIds.isEmpty()) list
                else list.filter { it.id in presentIds }
                _state.value = _state.value.copy(students = filtered)
            }
        }

        viewModelScope.launch {
            sessionManager.englishPercentage.collect { percentage ->
                _state.value = _state.value.copy(
                    englishShareRolling = percentage,
                    totalEnglishMs = (_state.value.totalSpeechMs * percentage).toLong()
                )
            }
        }

        viewModelScope.launch {
            sessionManager.chunks.collect { list ->
                _state.value = _state.value.copy(chunks = list)
            }
        }

        viewModelScope.launch {
            sessionManager.activeSpeakerId.collect { activeId ->
                _state.value = _state.value.copy(activeSpeakerId = activeId)
            }
        }

        viewModelScope.launch {
            sessionManager.paused.collect { paused ->
                _state.value = _state.value.copy(paused = paused)
            }
        }

        viewModelScope.launch {
            sessionManager.recognizerStatus.collect { status ->
                _state.value = _state.value.copy(recognizerStatus = status)
            }
        }
    }

    fun togglePause() {
        sessionManager.setPaused(!sessionManager.paused.value)
    }

    fun onPermissionResult(granted: Boolean) {
        _state.value = _state.value.copy(isPermissionGranted = granted)
    }

    fun setTopic(t: String) { _state.value = _state.value.copy(topic = t, customTopic = "") }
    fun setCustomTopic(t: String) { _state.value = _state.value.copy(customTopic = t, topic = "") }
    fun resolvedTopic(): String =
        _state.value.topic.ifBlank { _state.value.customTopic.trim() }

    fun selectActiveSpeaker(studentId: String?) {
        sessionManager.setActiveSpeaker(studentId)
    }

    fun start() {
        val topic = resolvedTopic().ifBlank { "Free talk" }
        sessionId = UUID.randomUUID().toString()
        startedAt = System.currentTimeMillis()

        studentSpeechMsMap.clear()
        _state.value.students.forEach {
            studentSpeechMsMap[it.id] = 0L
        }

        _state.value = _state.value.copy(
            phase = SessionUiState.Phase.Running,
            topic = topic,
            elapsedMs = 0L,
            studentSpeechMs = studentSpeechMsMap.toMap(),
            activeSpeakerId = null
        )

        val intent = Intent(context, SessionService::class.java)
        context.startForegroundService(intent)

        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000)
                // Freeze the clock + per-speaker counters while paused — pause
                // means the recognizer is cancelled and no chunks are flowing,
                // so it would be a lie to keep incrementing speech time.
                if (sessionManager.paused.value) continue

                val currentActiveSpeakerId = sessionManager.activeSpeakerId.value
                if (currentActiveSpeakerId != null && studentSpeechMsMap.containsKey(currentActiveSpeakerId)) {
                    val prevTime = studentSpeechMsMap[currentActiveSpeakerId] ?: 0L
                    studentSpeechMsMap[currentActiveSpeakerId] = prevTime + 1000L
                }

                _state.value = _state.value.copy(
                    elapsedMs = _state.value.elapsedMs + 1000,
                    totalSpeechMs = _state.value.totalSpeechMs + 1000,
                    studentSpeechMs = studentSpeechMsMap.toMap()
                )
            }
        }
    }

    suspend fun end(): String {
        timerJob?.cancel()

        val intent = Intent(context, SessionService::class.java)
        context.stopService(intent)

        _state.value = _state.value.copy(phase = SessionUiState.Phase.Ending)
        val s = _state.value
        val id = sessionId ?: UUID.randomUUID().toString()
        val now = System.currentTimeMillis()

        val totalEnglishWords = s.chunks.sumOf { it.metrics.englishCount }
        val totalMeaningfulTokens = s.chunks.sumOf { it.metrics.meaningfulCount }

        sessionRepo.upsertSession(
            Session(
                id = id,
                cohortId = cohortId,
                topic = s.topic.ifBlank { "Free talk" },
                startedAt = startedAt.takeIf { it > 0 } ?: now,
                endedAt = now,
                totalSpeechMs = s.totalSpeechMs,
                englishSpeechMs = s.totalEnglishMs,
                transcript = s.chunks.joinToString("\n") { it.rawText },
                chunksProcessed = s.chunks.size,
                englishWordCount = totalEnglishWords,
                meaningfulTokenCount = totalMeaningfulTokens
            )
        )

        // Only persist stats for students who actually spoke. Rows with
        // speechMs == 0 used to pollute the History median and the Dashboard
        // trend line for anyone who was on the attendance list but quiet.
        val stats = s.students.mapNotNull { stu ->
            val speechMs = studentSpeechMsMap[stu.id] ?: 0L
            if (speechMs <= 0L) return@mapNotNull null

            val studentChunks = s.chunks.filter { it.speakerId == stu.id }
            val studentEnglishWords = studentChunks.sumOf { it.metrics.englishCount }
            val studentMeaningfulTokens = studentChunks.sumOf { it.metrics.meaningfulCount }

            val englishShare = if (studentMeaningfulTokens > 0) {
                studentEnglishWords.toFloat() / studentMeaningfulTokens.toFloat()
            } else 0f
            val englishMs = (speechMs * englishShare).toLong()

            SpeakerStat(
                id = "stat-$id-${stu.id}",
                sessionId = id,
                studentId = stu.id,
                speechMs = speechMs,
                englishMs = englishMs
            )
        }
        sessionRepo.upsertStats(stats)

        val transcriptChunks = s.chunks.mapIndexed { index, analysis ->
            TranscriptChunk(
                sessionId = id,
                sequence = index,
                rawText = analysis.rawText,
                cleanedText = analysis.tokens.filter { it.classification.isMeaningful() }.joinToString(" ") { it.normalized },
                englishCount = analysis.metrics.englishCount,
                meaningfulCount = analysis.metrics.meaningfulCount,
                fillerCount = analysis.metrics.fillerCount,
                studentId = analysis.speakerId
            )
        }
        sessionRepo.insertChunks(transcriptChunks)

        return id
    }
}
