package co.bolo.app.ui.enrollment

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.bolo.app.data.repo.CohortRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class EnrollPhase { Intro, Countdown, Listening, Done }

data class EnrollUiState(
    val studentName: String = "",
    val phase: EnrollPhase = EnrollPhase.Intro,
    val countdown: Int = 3,
    val secondsRemaining: Int = 10
)

@HiltViewModel
class EnrollmentViewModel @Inject constructor(
    private val cohortRepo: CohortRepo,
    savedState: SavedStateHandle
) : ViewModel() {

    private val studentId: String = savedState["studentId"] ?: ""
    private val _state = MutableStateFlow(EnrollUiState())
    val state: StateFlow<EnrollUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val s = cohortRepo.student(studentId)
            _state.value = _state.value.copy(studentName = s?.displayName ?: "Student")
        }
    }

    fun begin() {
        viewModelScope.launch {
            _state.value = _state.value.copy(phase = EnrollPhase.Countdown, countdown = 3)
            repeat(3) {
                delay(900)
                _state.value = _state.value.copy(countdown = (_state.value.countdown - 1).coerceAtLeast(0))
            }
            _state.value = _state.value.copy(phase = EnrollPhase.Listening, secondsRemaining = 10)
            while (isActive && _state.value.secondsRemaining > 0) {
                delay(1000)
                _state.value = _state.value.copy(secondsRemaining = _state.value.secondsRemaining - 1)
            }
            _state.value = _state.value.copy(phase = EnrollPhase.Done)
            // Phase 2 will write the encrypted embedding here.
        }
    }
}
