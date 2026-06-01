package co.bolo.app.ui.history

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.bolo.app.data.model.Session
import co.bolo.app.data.repo.CohortRepo
import co.bolo.app.data.repo.SessionRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class HistoryUiState(
    val sessions: List<Session> = emptyList(),
    val medianSharePct: Int = 0
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val cohortRepo: CohortRepo,
    private val sessionRepo: SessionRepo,
    savedState: SavedStateHandle
) : ViewModel() {

    private val cohortArg: String? = savedState["cohortId"]

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<HistoryUiState> = cohortRepo.observeCohorts()
        .flatMapLatest { cohorts ->
            val active = cohorts.firstOrNull { it.id == cohortArg } ?: cohorts.firstOrNull()
            if (active == null) flowOf(HistoryUiState())
            else sessionRepo.observeForCohort(active.id).map { sessions ->
                val pcts = sessions.map { (it.englishShare * 100f).toInt() }.sorted()
                val median = when {
                    pcts.isEmpty() -> 0
                    pcts.size % 2 == 1 -> pcts[pcts.size / 2]
                    else -> ((pcts[pcts.size / 2 - 1] + pcts[pcts.size / 2]) / 2.0).toInt()
                }
                HistoryUiState(sessions = sessions, medianSharePct = median)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), HistoryUiState())
}
