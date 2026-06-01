package co.bolo.app.analysis

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TranscriptAnalyzer @Inject constructor(
    private val tokenClassifier: TokenClassifier
) {
    suspend fun analyze(text: String): ChunkAnalysis {
        val rawTokens = text.split(Regex("\\s+")).filter { it.isNotBlank() }
        val tokenAnalyses = rawTokens.map { tokenClassifier.classify(it) }

        val meaningfulTokens = tokenAnalyses.filter { it.classification.isMeaningful() }
        val englishCount = tokenAnalyses.count { it.classification.isEnglishCounting() }
        val fillerCount = tokenAnalyses.count { it.classification == ClassificationType.FILLER }

        val englishScore = if (meaningfulTokens.isNotEmpty()) {
            englishCount.toFloat() / meaningfulTokens.size.toFloat()
        } else {
            0f
        }

        return ChunkAnalysis(
            rawText = text,
            tokens = tokenAnalyses,
            englishScore = englishScore,
            metrics = ChunkMetrics(
                englishCount = englishCount,
                meaningfulCount = meaningfulTokens.size,
                fillerCount = fillerCount,
                totalWords = rawTokens.size
            )
        )
    }
}
