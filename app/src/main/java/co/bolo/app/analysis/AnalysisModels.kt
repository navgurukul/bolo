package co.bolo.app.analysis

enum class ClassificationSource {
    DICTIONARY,
    HEURISTIC,
    AI_FASTTEXT,
    NOISE_OR_FILLER,
    UNKNOWN
}

data class TokenAnalysis(
    val token: String,
    val normalized: String,
    val classification: ClassificationType,
    val source: ClassificationSource,
    val confidence: Float = 1.0f,
    val reason: String? = null
)

data class ChunkAnalysis(
    val rawText: String,
    val tokens: List<TokenAnalysis>,
    val englishScore: Float, // 0.0 to 1.0
    val metrics: ChunkMetrics
)

data class ChunkMetrics(
    val englishCount: Int,
    val meaningfulCount: Int,
    val fillerCount: Int,
    val totalWords: Int
)
