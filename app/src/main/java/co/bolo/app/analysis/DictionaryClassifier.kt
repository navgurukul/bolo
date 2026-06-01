package co.bolo.app.analysis

import android.content.Context
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DictionaryClassifier @Inject constructor() {
    private val dictionary = HashSet<String>(20000)
    private val fillers = setOf(
        "um", "uh", "err", "hmm", "ah", "oh", "uhh", "umm", "huh", "like", "actually", "basically"
    )
    private val allowedShort = setOf(
        "i", "a", "am", "an", "is", "to", "in", "it", "at", "on", "if", "up", "so", "by", "do", "be", "me", "my", "we", "he", "no", "us", "as", "of", "or"
    )

    private var isInitialized = false

    fun initialize(context: Context) {
        if (isInitialized) return
        try {
            context.assets.open("dictionaries/english_words.txt").bufferedReader().useLines { lines ->
                lines.forEach { line ->
                    val word = line.trim().lowercase()
                    if (word.isNotEmpty()) {
                        dictionary.add(word)
                    }
                }
            }
            isInitialized = true
        } catch (e: Exception) {
            // Minimal fallback
            dictionary.addAll(listOf("the", "be", "to", "of", "and", "a", "in", "that", "have", "i", "it", "for", "not", "on", "with", "he", "as", "you", "do", "at", "this", "but", "his", "by", "from", "they", "we", "say", "her", "she", "or", "an", "will", "my", "one", "all", "would", "there", "their", "what", "so", "up", "out", "if", "about", "who", "get", "which", "go", "me", "when", "make", "can", "like", "time", "no", "just", "him", "know", "take", "people", "into", "year", "your", "good", "some", "could", "them", "see", "other", "than", "then", "now", "look", "only", "come", "its", "over", "think", "also", "back", "after", "use", "two", "how", "our", "work", "first", "well", "way", "even", "new", "want", "because", "any", "these", "give", "day", "most", "us", "is", "am", "are", "was", "were", "been", "has", "had", "did", "does", "done", "yes", "no", "okay", "ok", "right", "sure", "great", "fine", "thanks", "thank", "please", "sorry", "excuse", "hello", "hi", "hey"))
            isInitialized = true
        }
    }

    fun classify(normalized: String): TokenAnalysis? {
        if (fillers.contains(normalized)) {
            return TokenAnalysis(
                token = "", // Will be filled by caller
                normalized = normalized,
                classification = ClassificationType.FILLER,
                source = ClassificationSource.NOISE_OR_FILLER,
                reason = "Common filler word"
            )
        }

        if (allowedShort.contains(normalized)) {
            return TokenAnalysis(
                token = "",
                normalized = normalized,
                classification = ClassificationType.ENGLISH,
                source = ClassificationSource.DICTIONARY,
                reason = "Common short word"
            )
        }

        if (dictionary.contains(normalized)) {
            return TokenAnalysis(
                token = "",
                normalized = normalized,
                classification = ClassificationType.ENGLISH,
                source = ClassificationSource.DICTIONARY,
                reason = "Found in dictionary"
            )
        }

        // Stemming logic
        val stem = getStem(normalized)
        if (stem != normalized && (dictionary.contains(stem) || dictionary.contains(stem + "e"))) {
            return TokenAnalysis(
                token = "",
                normalized = normalized,
                classification = ClassificationType.ENGLISH,
                source = ClassificationSource.DICTIONARY,
                reason = "Stem '$stem' found in dictionary"
            )
        }

        return null
    }

    private fun getStem(word: String): String {
        if (word.length <= 3) return word
        return when {
            word.endsWith("ing") -> word.removeSuffix("ing")
            word.endsWith("ed") -> word.removeSuffix("ed")
            word.endsWith("s") && !word.endsWith("ss") -> word.removeSuffix("s")
            word.endsWith("ly") -> word.removeSuffix("ly")
            word.endsWith("tion") -> word.removeSuffix("tion")
            else -> word
        }
    }
}
