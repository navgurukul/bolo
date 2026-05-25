package co.bolo.app.ui.cohort

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.bolo.app.data.repo.CohortRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class CohortPickRowState(
    val id: String,
    val name: String,
    val studentCount: Int
)

data class CohortPickerUiState(
    val cohorts: List<CohortPickRowState> = emptyList()
)

@HiltViewModel
class CohortPickerViewModel @Inject constructor(
    private val cohortRepo: CohortRepo
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<CohortPickerUiState> = cohortRepo.observeCohorts()
        .flatMapLatest { cohorts ->
            if (cohorts.isEmpty()) {
                flowOf(CohortPickerUiState())
            } else {
                combine(
                    cohorts.map { c -> cohortRepo.observeStudents(c.id).map { it.size } }
                ) { counts ->
                    CohortPickerUiState(
                        cohorts = cohorts.mapIndexed { i, c ->
                            CohortPickRowState(
                                id = c.id,
                                name = c.name,
                                studentCount = counts[i]
                            )
                        }
                    )
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CohortPickerUiState())
}
