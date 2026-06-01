package co.bolo.app.ui.history

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.bolo.app.data.model.Session
import co.bolo.app.data.model.SpeakerStat
import co.bolo.app.data.model.Student
import co.bolo.app.data.model.TranscriptChunk
import co.bolo.app.data.repo.CohortRepo
import co.bolo.app.data.repo.SessionRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class StudentRowState(
    val student: Student,
    val sharePct: Int,
    val speechMs: Long
)

data class TimelineSegment(val englishShare: Float, val populated: Boolean)

data class SessionDetailUiState(
    val session: Session? = null,
    val rows: List<StudentRowState> = emptyList(),
    val timeline: List<TimelineSegment> = emptyList(),
    val chunkCount: Int = 0
)

@HiltViewModel
class SessionDetailViewModel @Inject constructor(
    private val cohortRepo: CohortRepo,
    private val sessionRepo: SessionRepo,
    savedState: SavedStateHandle
) : ViewModel() {

    private val sessionId: String = savedState["id"] ?: ""

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<SessionDetailUiState> = sessionRepo.observeSession(sessionId)
        .flatMapLatest { session ->
            if (session == null) flowOf(SessionDetailUiState())
            else combine(
                cohortRepo.observeStudents(session.cohortId),
                sessionRepo.observeStats(sessionId),
                sessionRepo.observeChunks(sessionId)
            ) { students, stats, chunks ->
                val byId = students.associateBy { it.id }
                val rows = stats.mapNotNull { stat ->
                    byId[stat.studentId]?.let {
                        StudentRowState(
                            student = it,
                            sharePct = (stat.englishShare * 100f).toInt(),
                            speechMs = stat.speechMs
                        )
                    }
                }.sortedByDescending { it.sharePct }

                SessionDetailUiState(
                    session = session,
                    rows = rows,
                    timeline = buildTimeline(session, chunks),
                    chunkCount = chunks.size
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SessionDetailUiState())

    private fun buildTimeline(session: Session, chunks: List<TranscriptChunk>): List<TimelineSegment> {
        val segments = 30
        val end = session.endedAt ?: chunks.maxOfOrNull { it.timestamp } ?: session.startedAt
        val span = (end - session.startedAt).coerceAtLeast(1L)
        val buckets = Array(segments) { mutableListOf<TranscriptChunk>() }
        chunks.forEach { c ->
            val offset = (c.timestamp - session.startedAt).coerceAtLeast(0L)
            val idx = ((offset * segments) / span).toInt().coerceIn(0, segments - 1)
            buckets[idx].add(c)
        }
        return buckets.map { bucket ->
            val tokens = bucket.sumOf { it.meaningfulCount }
            val english = bucket.sumOf { it.englishCount }
            TimelineSegment(
                englishShare = if (tokens > 0) english.toFloat() / tokens else 0f,
                populated = bucket.isNotEmpty()
            )
        }
    }
}
