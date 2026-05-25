package co.bolo.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.bolo.app.data.model.Cohort
import co.bolo.app.data.model.Session
import co.bolo.app.data.model.Student
import co.bolo.app.data.repo.CohortRepo
import co.bolo.app.data.repo.SessionRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class HomeUiState(
    val cohorts: List<Cohort> = emptyList(),
    val selectedCohortId: String? = null,
    val students: List<Student> = emptyList(),
    val recentSessions: List<Session> = emptyList(),
    val cohortStudentCounts: Map<String, Int> = emptyMap()
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val cohortRepo: CohortRepo,
    private val sessionRepo: SessionRepo
) : ViewModel() {

    private val selectedCohortId = MutableStateFlow<String?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<HomeUiState> = combine(
        cohortRepo.observeCohorts(),
        selectedCohortId
    ) { cohorts, sel ->
        // Prefer an explicit selection; otherwise fall back to first cohort.
        val effectiveId = sel?.takeIf { id -> cohorts.any { it.id == id } }
            ?: cohorts.firstOrNull()?.id
        cohorts to effectiveId
    }.flatMapLatest { (cohorts, sel) ->
        if (cohorts.isEmpty()) flowOf(HomeUiState())
        else if (sel == null) flowOf(HomeUiState(cohorts = cohorts))
        else combine(
            cohortRepo.observeStudents(sel),
            sessionRepo.observeForCohort(sel)
        ) { students, sessions ->
            // For headline counts on each cohort row we lean on the
            // currently-loaded students list for the selected cohort; for
            // others we ship 0 and let the cohort screen show real counts.
            val counts = cohorts.associate { c ->
                c.id to if (c.id == sel) students.size else 0
            }
            HomeUiState(
                cohorts = cohorts,
                selectedCohortId = sel,
                students = students,
                recentSessions = sessions.take(5),
                cohortStudentCounts = counts
            )
        }
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), HomeUiState()
    )

    fun selectCohort(id: String) { selectedCohortId.value = id }
}
