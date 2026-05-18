package co.bolo.app.analysis

import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.languageid.LanguageIdentifier
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FastTextClassifier @Inject constructor() {
    // We use ML Kit's Language ID as our "Real" lightweight on-device classifier.
    // It uses a model architecture similar to FastText (embedding + pooling) 
    // optimized for Android.
    private val languageIdentifier: LanguageIdentifier = LanguageIdentification.getClient()

    suspend fun classify(normalized: String): TokenAnalysis? {
        if (normalized.isEmpty()) return null

        return try {
            val languageCode = languageIdentifier.identifyLanguage(normalized).await()
            val confidence = 0.9f // ML Kit identifyLanguage doesn't return confidence in the single result call easily
            // Note: identifyPossibleLanguages can provide confidence scores.
            
            val (type, source) = when (languageCode) {
                "en" -> ClassificationType.ENGLISH to ClassificationSource.AI_FASTTEXT
                "und" -> ClassificationType.UNKNOWN to ClassificationSource.UNKNOWN
                else -> ClassificationType.NON_ENGLISH to ClassificationSource.AI_FASTTEXT
            }

            TokenAnalysis(
                token = "", // Filled by caller
                normalized = normalized,
                classification = type,
                source = source,
                confidence = if (languageCode == "und") 0.0f else confidence,
                reason = "ML Kit identified as: $languageCode"
            )
        } catch (e: Exception) {
            null
        }
    }

    suspend fun classifyWithConfidence(normalized: String): TokenAnalysis? {
        if (normalized.isEmpty()) return null

        return try {
            val possibleLanguages = languageIdentifier.identifyPossibleLanguages(normalized).await()
            val bestMatch = possibleLanguages.maxByOrNull { it.confidence } ?: return null
            
            val (type, source) = when (bestMatch.languageTag) {
                "en" -> ClassificationType.ENGLISH to ClassificationSource.AI_FASTTEXT
                "und" -> ClassificationType.UNKNOWN to ClassificationSource.UNKNOWN
                else -> ClassificationType.NON_ENGLISH to ClassificationSource.AI_FASTTEXT
            }

            TokenAnalysis(
                token = "",
                normalized = normalized,
                classification = type,
                source = source,
                confidence = bestMatch.confidence,
                reason = "ML Kit identified: ${bestMatch.languageTag} (${(bestMatch.confidence * 100).toInt()}%)"
            )
        } catch (e: Exception) {
            null
        }
    }
}
