package co.bolo.app.data.repo

import co.bolo.app.analysis.ChunkAnalysis
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Status of the cloud speech recognizer. Surfaced to the UI so a session
 * doesn't silently fail (no internet, Google quota exhausted, etc.).
 */
enum class RecognizerStatus {
    Ok,
    NoInternet,
    Overloaded,
    ServerError
}

@Singleton
class SessionManager @Inject constructor() {
    private val _isRecording = MutableStateFlow(false)
    val isRecording = _isRecording.asStateFlow()

    private val _englishPercentage = MutableStateFlow(0f)
    val englishPercentage = _englishPercentage.asStateFlow()

    private val _chunks = MutableStateFlow<List<ChunkAnalysis>>(emptyList())
    val chunks = _chunks.asStateFlow()

    private val _activeSpeakerId = MutableStateFlow<String?>(null)
    val activeSpeakerId = _activeSpeakerId.asStateFlow()

    private val _paused = MutableStateFlow(false)
    val paused = _paused.asStateFlow()

    private val _recognizerStatus = MutableStateFlow(RecognizerStatus.Ok)
    val recognizerStatus = _recognizerStatus.asStateFlow()

    fun setActiveSpeaker(studentId: String?) {
        _activeSpeakerId.value = studentId
    }

    fun setPaused(value: Boolean) {
        _paused.value = value
    }

    fun setRecognizerStatus(status: RecognizerStatus) {
        _recognizerStatus.value = status
    }

    fun addChunk(analysis: ChunkAnalysis) {
        val analysisWithSpeaker = analysis.copy(speakerId = _activeSpeakerId.value)
        val current = _chunks.value.toMutableList()
        current.add(analysisWithSpeaker)
        _chunks.value = current

        val totalEnglish = current.sumOf { it.metrics.englishCount }
        val totalMeaningful = current.sumOf { it.metrics.meaningfulCount }

        _englishPercentage.value = if (totalMeaningful > 0) {
            totalEnglish.toFloat() / totalMeaningful.toFloat()
        } else 0f
    }

    fun setRecording(recording: Boolean) {
        _isRecording.value = recording
        if (!recording) {
            _englishPercentage.value = 0f
            _chunks.value = emptyList()
            _activeSpeakerId.value = null
            _paused.value = false
            _recognizerStatus.value = RecognizerStatus.Ok
        }
    }
}
