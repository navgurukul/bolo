package co.bolo.app.ui.dashboard

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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TrendPoint(
    val sessionId: String,
    val startedAt: Long,
    val topic: String,
    val englishShare: Float,
    val speechMs: Long
)

data class DashboardUiState(
    val student: Student? = null,
    val topics: List<String> = emptyList(),
    val selectedTopic: String? = null,
    val points: List<TrendPoint> = emptyList(),
    val averageShare: Float = 0f
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val cohortRepo: CohortRepo,
    private val sessionRepo: SessionRepo,
    savedState: SavedStateHandle
) : ViewModel() {

    private val studentId: String = savedState["studentId"] ?: ""
    private val selectedTopic = MutableStateFlow<String?>(null)
    private val student = MutableStateFlow<Student?>(null)

    init {
        viewModelScope.launch {
            student.value = cohortRepo.student(studentId)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: StateFlow<DashboardUiState> = student
        .flatMapLatest { stu ->
            if (stu == null) flowOf(DashboardUiState())
            else combine(
                sessionRepo.observeForCohort(stu.cohortId),
                sessionRepo.observeStudentTrend(stu.id),
                selectedTopic
            ) { sessions, stats, topic ->
                val byId = sessions.associateBy { it.id }
                val all = stats.mapNotNull { st ->
                    val ses = byId[st.sessionId] ?: return@mapNotNull null
                    TrendPoint(
                        sessionId = ses.id,
                        startedAt = ses.startedAt,
                        topic = ses.topic,
                        englishShare = st.englishShare,
                        speechMs = st.speechMs
                    )
                }.sortedBy { it.startedAt }

                val topics = all.map { it.topic }.distinct()
                val filtered = if (topic == null) all else all.filter { it.topic == topic }
                val avg = if (filtered.isEmpty()) 0f
                          else filtered.sumOf { it.englishShare.toDouble() }.toFloat() / filtered.size

                DashboardUiState(
                    student = stu,
                    topics = topics,
                    selectedTopic = topic,
                    points = filtered,
                    averageShare = avg
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DashboardUiState())

    fun selectTopic(topic: String?) { selectedTopic.value = topic }
}
