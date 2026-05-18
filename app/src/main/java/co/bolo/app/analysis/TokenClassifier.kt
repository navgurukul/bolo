package co.bolo.app.analysis

import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenClassifier @Inject constructor(
    private val dictionaryClassifier: DictionaryClassifier,
    private val fastTextClassifier: FastTextClassifier
) {
    private val cache = ConcurrentHashMap<String, TokenAnalysis>()

    suspend fun classify(rawToken: String): TokenAnalysis {
        val normalized = TokenNormalizer.normalize(rawToken)
        
        // 0. Cache lookup
        cache[normalized]?.let { 
            return it.copy(token = rawToken) 
        }

        // 1. Noise/Filler/Numbers (Deterministic)
        if (TokenNormalizer.isNoise(normalized)) {
            return TokenAnalysis(
                token = rawToken,
                normalized = normalized,
                classification = ClassificationType.UNKNOWN,
                source = ClassificationSource.NOISE_OR_FILLER,
                reason = "Noise or punctuation"
            )
        }

        if (TokenNormalizer.isNumber(normalized)) {
            return TokenAnalysis(
                token = rawToken,
                normalized = normalized,
                classification = ClassificationType.ENGLISH,
                source = ClassificationSource.HEURISTIC,
                reason = "Numerical token"
            )
        }

        // 2. Dictionary lookup
        val dictResult = dictionaryClassifier.classify(normalized)
        if (dictResult != null) {
            val result = dictResult.copy(token = rawToken)
            cache[normalized] = result
            return result
        }

        // 3. Proper Noun detection
        // Decision: Proper nouns count positively toward English usage.
        if (ProperNounDetector.isProperNoun(rawToken, normalized)) {
            val result = TokenAnalysis(
                token = rawToken,
                normalized = normalized,
                classification = ClassificationType.PROPER_NOUN,
                source = ClassificationSource.HEURISTIC,
                reason = "Proper noun (capitalized)"
            )
            cache[normalized] = result
            return result
        }

        // 4. REAL FastText (ML Kit) fallback for unknown words
        val aiResult = fastTextClassifier.classifyWithConfidence(normalized)
        if (aiResult != null) {
            val result = aiResult.copy(token = rawToken)
            cache[normalized] = result
            return result
        }

        // 5. Final fallback
        val finalResult = TokenAnalysis(
            token = rawToken,
            normalized = normalized,
            classification = ClassificationType.UNKNOWN,
            source = ClassificationSource.UNKNOWN,
            reason = "Could not classify"
        )
        cache[normalized] = finalResult
        return finalResult
    }

    fun clearCache() {
        cache.clear()
    }
}
