package co.bolo.app.ui.summary

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.bolo.app.analysis.ClassificationType
import co.bolo.app.analysis.TokenAnalysis
import co.bolo.app.analysis.TokenClassifier
import co.bolo.app.data.model.Session
import co.bolo.app.data.model.SpeakerStat
import co.bolo.app.data.model.Student
import co.bolo.app.data.model.TranscriptChunk
import co.bolo.app.data.repo.CohortRepo
import co.bolo.app.data.repo.SessionRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
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
    private val tokenClassifier: TokenClassifier,
    savedState: SavedStateHandle
) : ViewModel() {

    private val sessionId: String = savedState["sessionId"] ?: ""

    private val _debugData = MutableStateFlow<Pair<List<TokenAnalysis>, List<Pair<String, Int>>>>(emptyList<TokenAnalysis>() to emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<SummaryUiState> = sessionRepo.observeSession(sessionId)
        .flatMapLatest { session ->
            if (session == null) flowOf(SummaryUiState())
            else combine(
                cohortRepo.observeStudents(session.cohortId),
                sessionRepo.observeStats(sessionId),
                sessionRepo.observeChunks(sessionId),
                _debugData
            ) { students, stats, chunks, debug ->
                val byId = students.associateBy { it.id }
                val rows = stats.mapNotNull { stat ->
                    byId[stat.studentId]?.let { SummaryRow(it, stat) }
                }.sortedByDescending { it.stat.englishShare }
                
                // Trigger background classification for debug view if we haven't yet
                if (debug.first.isEmpty() && chunks.isNotEmpty()) {
                    generateDebugData(chunks)
                }

                SummaryUiState(
                    session = session,
                    rows = rows,
                    topSpeaker = rows.firstOrNull { it.stat.speechMs > 0L },
                    chunks = chunks,
                    tokenBreakdown = debug.first,
                    unknownWords = debug.second
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SummaryUiState())

    private fun generateDebugData(chunks: List<TranscriptChunk>) {
        viewModelScope.launch {
            val allRawTokens = chunks
                .flatMap { it.rawText.split(Regex("\\s+")) }
                .filter { it.isNotBlank() }
                .distinctBy { it.lowercase() }
            
            val analyses = allRawTokens.map { tokenClassifier.classify(it) }
            
            val samples = analyses.take(20)
            val unknown = analyses
                .filter { it.classification == ClassificationType.UNKNOWN }
                .groupBy { it.normalized }
                .mapValues { it.value.size }
                .toList()
                .sortedByDescending { it.second }
                .take(15)

            _debugData.value = samples to unknown
        }
    }
}
