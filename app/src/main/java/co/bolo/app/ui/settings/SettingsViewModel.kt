package co.bolo.app.ui.settings

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.bolo.app.data.model.Cohort
import co.bolo.app.data.repo.CohortRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class SettingsUiState(
    val cohort: Cohort? = null,
    val studentCount: Int = 0
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val cohortRepo: CohortRepo,
    savedState: SavedStateHandle
) : ViewModel() {

    private val cohortArg: String? = savedState["cohortId"]

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<SettingsUiState> = cohortRepo.observeCohorts()
        .flatMapLatest { cohorts ->
            val active = cohorts.firstOrNull { it.id == cohortArg } ?: cohorts.firstOrNull()
            if (active == null) flowOf(SettingsUiState())
            else cohortRepo.observeStudents(active.id)
                .let { studentsFlow ->
                    combine(flowOf(active), studentsFlow) { c, students ->
                        SettingsUiState(cohort = c, studentCount = students.size)
                    }
                }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SettingsUiState())
}
