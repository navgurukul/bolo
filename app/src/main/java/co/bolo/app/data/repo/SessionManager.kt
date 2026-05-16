package co.bolo.app.data.repo

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor() {
    private val _isRecording = MutableStateFlow(false)
    val isRecording = _isRecording.asStateFlow()

    private val _englishPercentage = MutableStateFlow(0f)
    val englishPercentage = _englishPercentage.asStateFlow()

    private val _transcript = MutableStateFlow("")
    val transcript = _transcript.asStateFlow()

    private val _chunksProcessed = MutableStateFlow(0)
    val chunksProcessed = _chunksProcessed.asStateFlow()

    fun updateMetrics(percentage: Float, fullTranscript: String, chunks: Int) {
        _englishPercentage.value = percentage
        _transcript.value = fullTranscript
        _chunksProcessed.value = chunks
    }

    fun setRecording(recording: Boolean) {
        _isRecording.value = recording
        if (!recording) {
            _englishPercentage.value = 0f
            _transcript.value = ""
            _chunksProcessed.value = 0
        }
    }
}
