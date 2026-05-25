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
    val drifting: Boolean = false,
    val totalSpeechMs: Long = 0L,
    val totalEnglishMs: Long = 0L,
    val participantCount: Int = 0,
    val setupNames: List<String> = emptyList(),
    val isPermissionGranted: Boolean = false,
    val chunks: List<ChunkAnalysis> = emptyList(),
    val showDebugTranscript: Boolean = false,
    val activeSpeakerId: String? = null,
    val studentSpeechMs: Map<String, Long> = emptyMap()
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
    private val _state = MutableStateFlow(SessionUiState(cohortId = cohortId))
    val state: StateFlow<SessionUiState> = _state.asStateFlow()
    private var sessionId: String? = null
    private var startedAt: Long = 0L
    private var timerJob: Job? = null
    private var isManualSetup = false
    private val studentSpeechMsMap = mutableMapOf<String, Long>()

    init {
        viewModelScope.launch {
            cohortRepo.observeStudents(cohortId).collect { list ->
                if (!isManualSetup && list.isNotEmpty()) {
                    _state.value = _state.value.copy(students = list)
                }
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
    }

    fun onPermissionResult(granted: Boolean) {
        _state.value = _state.value.copy(isPermissionGranted = granted)
    }

    fun toggleDebugTranscript() {
        _state.value = _state.value.copy(showDebugTranscript = !_state.value.showDebugTranscript)
    }

    fun setParticipantCount(count: Int) {
        isManualSetup = true
        _state.value = _state.value.copy(
            participantCount = count,
            setupNames = List(count) { "" }
        )
    }

    fun updateSetupName(index: Int, name: String) {
        val names = _state.value.setupNames.toMutableList()
        if (index in names.indices) {
            names[index] = name
            _state.value = _state.value.copy(setupNames = names)
        }
    }

    fun finalizeParticipants() {
        val names = _state.value.setupNames
        if (names.isNotEmpty()) {
            _state.value = _state.value.copy(
                students = names.mapIndexed { i, name ->
                    val displayName = name.ifBlank { "Participant ${i + 1}" }
                    Student(
                        id = "student-${UUID.randomUUID()}", 
                        cohortId = cohortId, 
                        displayName = displayName
                    )
                }
            )
        }
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

        // 1. Save any new students created during manual setup
        s.students.forEach { student ->
            cohortRepo.upsertStudent(student)
        }
        
        val totalEnglishWords = s.chunks.sumOf { it.metrics.englishCount }
        val totalMeaningfulTokens = s.chunks.sumOf { it.metrics.meaningfulCount }

        // 2. Persist the session with summary data
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
        
        // 3. Link students to session
        val stats = s.students.map { stu ->
            val studentChunks = s.chunks.filter { it.speakerId == stu.id }
            val studentEnglishWords = studentChunks.sumOf { it.metrics.englishCount }
            val studentMeaningfulTokens = studentChunks.sumOf { it.metrics.meaningfulCount }
            
            val speechMs = studentSpeechMsMap[stu.id] ?: 0L
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

        // 4. Save detailed transcript chunks for debug/analysis
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
