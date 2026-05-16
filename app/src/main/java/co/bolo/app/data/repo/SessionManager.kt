package co.bolo.app.data.repo

import co.bolo.app.util.ChunkAnalysis
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

    private val _chunks = MutableStateFlow<List<ChunkAnalysis>>(emptyList())
    val chunks = _chunks.asStateFlow()

    fun addChunk(analysis: ChunkAnalysis) {
        val current = _chunks.value.toMutableList()
        current.add(analysis)
        _chunks.value = current
        
        val totalEnglish = current.sumOf { it.englishCount }
        val totalMeaningful = current.sumOf { it.meaningfulCount }
        
        _englishPercentage.value = if (totalMeaningful > 0) {
            totalEnglish.toFloat() / totalMeaningful.toFloat()
        } else 0f
    }

    fun setRecording(recording: Boolean) {
        _isRecording.value = recording
        if (!recording) {
            _englishPercentage.value = 0f
            _chunks.value = emptyList()
        }
    }
}
