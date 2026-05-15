package co.bolo.app.ui.summary

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.bolo.app.data.model.Session
import co.bolo.app.data.model.SpeakerStat
import co.bolo.app.data.model.Student
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

data class SummaryRow(val student: Student, val stat: SpeakerStat)

data class SummaryUiState(
    val session: Session? = null,
    val rows: List<SummaryRow> = emptyList(),
    val topSpeaker: SummaryRow? = null
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
                sessionRepo.observeStats(sessionId)
            ) { students, stats ->
                val byId = students.associateBy { it.id }
                val rows = stats.mapNotNull { stat ->
                    byId[stat.studentId]?.let { SummaryRow(it, stat) }
                }.sortedByDescending { it.stat.englishShare }
                SummaryUiState(
                    session = session,
                    rows = rows,
                    topSpeaker = rows.firstOrNull { it.stat.speechMs > 0L }
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SummaryUiState())
}
