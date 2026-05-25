package co.bolo.app.ui.settings

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.bolo.app.data.db.BoloDatabase
import co.bolo.app.data.model.Cohort
import co.bolo.app.data.repo.CohortRepo
import co.bolo.app.util.Prefs
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class SettingsUiState(
    val cohort: Cohort? = null,
    val studentCount: Int = 0
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val cohortRepo: CohortRepo,
    private val db: BoloDatabase,
    @ApplicationContext private val context: Context,
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

    /**
     * Wipes every cohort, student, session, stat and transcript chunk from
     * Room, then flips the paired flag back to false. Caller is expected to
     * navigate to Splash on the returned Job's completion so the user runs
     * through the first-run flow again — that's how consent is re-collected.
     */
    fun clearAllData(onCleared: () -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                db.clearAllTables()
            }
            Prefs.setPaired(context, false)
            onCleared()
        }
    }
}
