package co.bolo.app.ui.summary

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.bolo.app.data.model.Session
import co.bolo.app.data.model.SpeakerStat
import co.bolo.app.data.model.Student
import co.bolo.app.data.model.TranscriptChunk
import co.bolo.app.data.repo.CohortRepo
import co.bolo.app.data.repo.SessionRepo
import co.bolo.app.util.ClassificationReason
import co.bolo.app.util.EnglishAnalyzer
import co.bolo.app.util.TokenAnalysis
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class SummaryRow(val student: Student, val stat: SpeakerStat)

data class SummaryUiState(
    val session: Session? = null,
    val rows: List<SummaryRow> = emptyList(),
    val topSpeaker: SummaryRow? = null,
    val chunks: List<TranscriptChunk> = emptyList(),
    val tokenBreakdown: List<TokenAnalysis> = emptyList(),
    val unknownWords: List<Pair<String, Int>> = emptyList()
)

@HiltViewModel
class SummaryViewModel @Inject constructor(
    private val cohortRepo: CohortRepo,
    private val sessionRepo: SessionRepo,
    savedState: SavedStateHandle
) : ViewModel() {

    private val sessionId: String = savedState["sessionId"] ?: ""

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<SummaryUiState> = sessionRepo.observeSession(sessionId)
        .flatMapLatest { session ->
            if (session == null) flowOf(SummaryUiState())
            else combine(
                cohortRepo.observeStudents(session.cohortId),
                sessionRepo.observeStats(sessionId),
                sessionRepo.observeChunks(sessionId)
            ) { students, stats, chunks ->
                val byId = students.associateBy { it.id }
                val rows = stats.mapNotNull { stat ->
                    byId[stat.studentId]?.let { SummaryRow(it, stat) }
                }.sortedByDescending { it.stat.englishShare }
                
                val allAnalyzedTokens = chunks.flatMap { chunk ->
                    EnglishAnalyzer.analyzeChunk(chunk.rawText).tokens
                }

                // For sample classification
                val samples = allAnalyzedTokens
                    .distinctBy { it.text.lowercase() }
                    .take(15)

                // Track unknown words for dictionary improvement
                val unknown = allAnalyzedTokens
                    .filter { it.reason == ClassificationReason.UNKNOWN }
                    .groupBy { it.normalized }
                    .mapValues { it.value.size }
                    .toList()
                    .sortedByDescending { it.second }
                    .take(15)

                SummaryUiState(
                    session = session,
                    rows = rows,
                    topSpeaker = rows.firstOrNull { it.stat.speechMs > 0L },
                    chunks = chunks,
                    tokenBreakdown = samples,
                    unknownWords = unknown
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SummaryUiState())
}
