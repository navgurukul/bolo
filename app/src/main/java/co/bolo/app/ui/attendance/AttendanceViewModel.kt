package co.bolo.app.ui.attendance

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.bolo.app.data.model.Cohort
import co.bolo.app.data.model.Student
import co.bolo.app.data.repo.CohortRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AttendanceUiState(
    val cohort: Cohort? = null,
    val students: List<Student> = emptyList(),
    val presentIds: Set<String> = emptySet()
) {
    val presentCount: Int get() = presentIds.size
    val totalCount: Int get() = students.size
}

/**
 * Backs the "Who's here today?" screen. Pulls real students for the given
 * cohort from Room, lets the facilitator toggle attendance, and exposes
 * inline add-student via [addStudent]. Selection state lives in memory
 * only — a session is the moment of truth, not "marked present" rows.
 */
@HiltViewModel
class AttendanceViewModel @Inject constructor(
    private val cohortRepo: CohortRepo,
    savedState: SavedStateHandle
) : ViewModel() {

    private val cohortId: String = savedState["cohortId"] ?: ""
    private val present = MutableStateFlow<Set<String>>(emptySet())

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<AttendanceUiState> =
        cohortRepo.observeCohorts()
            .flatMapLatest { cohorts ->
                val cohort = cohorts.firstOrNull { it.id == cohortId }
                if (cohort == null) flowOf(AttendanceUiState())
                else combine(
                    cohortRepo.observeStudents(cohortId),
                    present
                ) { students, sel ->
                    // Default: everyone present the first time the screen loads.
                    val effective = if (sel.isEmpty() && students.isNotEmpty())
                        students.map { it.id }.toSet()
                    else sel.intersect(students.map { it.id }.toSet())
                    AttendanceUiState(cohort = cohort, students = students, presentIds = effective)
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AttendanceUiState())

    fun toggle(studentId: String) {
        val cur = present.value.ifEmpty { state.value.students.map { it.id }.toSet() }
        present.value = if (studentId in cur) cur - studentId else cur + studentId
    }

    fun clear() { present.value = emptySet() }

    fun addStudent(name: String, onAdded: (String) -> Unit = {}) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return
        viewModelScope.launch {
            val id = cohortRepo.addStudent(cohortId, trimmed)
            // Auto-mark as present.
            val cur = present.value.ifEmpty { state.value.students.map { it.id }.toSet() }
            present.value = cur + id
            onAdded(id)
        }
    }

    fun removeStudent(studentId: String) {
        viewModelScope.launch { cohortRepo.deleteStudent(studentId) }
    }
}
