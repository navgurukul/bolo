package co.bolo.app.ui.cohort

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.bolo.app.data.repo.CohortRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewCohortViewModel @Inject constructor(
    private val cohortRepo: CohortRepo
) : ViewModel() {

    fun create(name: String, onCreated: (cohortId: String) -> Unit) {
        val trimmed = name.trim().ifBlank { return }
        viewModelScope.launch {
            val id = cohortRepo.createCohort(trimmed)
            onCreated(id)
        }
    }
}
