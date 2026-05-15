package co.bolo.app.ui.session

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.bolo.app.data.model.Session
import co.bolo.app.data.model.SpeakerStat
import co.bolo.app.data.model.Student
import co.bolo.app.data.repo.CohortRepo
import co.bolo.app.data.repo.SessionRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import kotlin.math.roundToLong
import kotlin.random.Random

data class SessionUiState(
    val cohortId: String = "",
    val students: List<Student> = emptyList(),
    val phase: Phase = Phase.PickingTopic,
    val topic: String = "",
    val customTopic: String = "",
    val elapsedMs: Long = 0L,
    val currentSpeakerIdx: Int = 0,
    val englishShareRolling: Float = 0.55f,
    val drifting: Boolean = false,
    val perStudentSpeechMs: Map<String, Long> = emptyMap(),
    val perStudentEnglishMs: Map<String, Long> = emptyMap(),
    val totalSpeechMs: Long = 0L,
    val totalEnglishMs: Long = 0L
) {
    enum class Phase { PickingTopic, Running, Ending }
}

val SUGGESTED_TOPICS = listOf("Daily life", "Mock interview", "News chat", "Tech", "Free talk")

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val cohortRepo: CohortRepo,
    private val sessionRepo: SessionRepo,
    savedState: SavedStateHandle
) : ViewModel() {

    private val cohortId: String = savedState["cohortId"] ?: ""
    private val _state = MutableStateFlow(SessionUiState(cohortId = cohortId))
    val state: StateFlow<SessionUiState> = _state.asStateFlow()
    private var sessionId: String? = null
    private var startedAt: Long = 0L
    private var loop: Job? = null
    private val rng = Random(System.currentTimeMillis())

    init {
        viewModelScope.launch {
            cohortRepo.observeStudents(cohortId).collect { list ->
                _state.value = _state.value.copy(students = list)
            }
        }
    }

    fun setTopic(t: String) { _state.value = _state.value.copy(topic = t, customTopic = "") }
    fun setCustomTopic(t: String) { _state.value = _state.value.copy(customTopic = t, topic = "") }
    fun resolvedTopic(): String =
        _state.value.topic.ifBlank { _state.value.customTopic.trim() }

    fun start() {
        val topic = resolvedTopic().ifBlank { "Free talk" }
        sessionId = UUID.randomUUID().toString()
        startedAt = System.currentTimeMillis()
        _state.value = _state.value.copy(
            phase = SessionUiState.Phase.Running,
            topic = topic,
            elapsedMs = 0L
        )
        loop = viewModelScope.launch { tick() }
    }

    private suspend fun tick() {
        val tickMs = 250L
        while (isActive && _state.value.phase == SessionUiState.Phase.Running) {
            delay(tickMs)
            val s = _state.value
            // Phase 0: simulate. Phase 1+ replaces this loop with real VAD/classifier output.
            val students = s.students
            if (students.isEmpty()) continue

            val speakerIdx = if (rng.nextFloat() < 0.04f) {
                (s.currentSpeakerIdx + 1 + rng.nextInt(students.size)) % students.size
            } else s.currentSpeakerIdx

            val englishProb = (s.englishShareRolling + (rng.nextFloat() - 0.5f) * 0.12f).coerceIn(0.05f, 0.95f)
            val drift = englishProb < 0.35f && rng.nextFloat() < 0.3f

            val speechDelta = tickMs
            val englishDelta = (speechDelta * englishProb).roundToLong()
            val sid = students[speakerIdx].id

            _state.value = s.copy(
                currentSpeakerIdx = speakerIdx,
                englishShareRolling = englishProb,
                drifting = drift,
                elapsedMs = s.elapsedMs + tickMs,
                totalSpeechMs = s.totalSpeechMs + speechDelta,
                totalEnglishMs = s.totalEnglishMs + englishDelta,
                perStudentSpeechMs = s.perStudentSpeechMs + (sid to ((s.perStudentSpeechMs[sid] ?: 0L) + speechDelta)),
                perStudentEnglishMs = s.perStudentEnglishMs + (sid to ((s.perStudentEnglishMs[sid] ?: 0L) + englishDelta))
            )
        }
    }

    /** Returns the persisted session id so navigation can route to summary. */
    suspend fun end(): String {
        loop?.cancel()
        _state.value = _state.value.copy(phase = SessionUiState.Phase.Ending)
        val s = _state.value
        val id = sessionId ?: UUID.randomUUID().toString()
        val now = System.currentTimeMillis()
        sessionRepo.upsertSession(
            Session(
                id = id,
                cohortId = cohortId,
                topic = s.topic.ifBlank { "Free talk" },
                startedAt = startedAt.takeIf { it > 0 } ?: now,
                endedAt = now,
                totalSpeechMs = s.totalSpeechMs,
                englishSpeechMs = s.totalEnglishMs
            )
        )
        val stats = s.students.map { stu ->
            SpeakerStat(
                id = "stat-$id-${stu.id}",
                sessionId = id,
                studentId = stu.id,
                speechMs = s.perStudentSpeechMs[stu.id] ?: 0L,
                englishMs = s.perStudentEnglishMs[stu.id] ?: 0L
            )
        }
        sessionRepo.upsertStats(stats)
        return id
    }
}
