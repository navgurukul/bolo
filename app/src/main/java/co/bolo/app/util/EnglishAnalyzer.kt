package co.bolo.app.util

import android.content.Context
import java.util.*

enum class ClassificationReason(val description: String) {
    EMPTY_OR_NOISE("Noise"),
    FILLER("Filler"),
    NUMBER("Number"),
    ALLOWED_SHORT("Common short word"),
    DICTIONARY_MATCH("Dictionary match"),
    STEM_MATCH("Stem match"),
    PROPER_NOUN("Proper noun"),
    UNKNOWN("Not in dictionary")
}

data class TokenAnalysis(
    val text: String,
    val normalized: String,
    val stem: String? = null,
    val isEnglish: Boolean,
    val isFiller: Boolean,
    val isIgnored: Boolean,
    val reason: ClassificationReason
)

data class ChunkAnalysis(
    val raw: String,
    val cleaned: String,
    val tokens: List<TokenAnalysis>,
    val englishCount: Int,
    val meaningfulCount: Int,
    val fillerCount: Int
)

object EnglishAnalyzer {
    private val dictionary = HashSet<String>(20000)
    private var isInitialized = false

    private val FILLERS = setOf(
        "um", "uh", "err", "hmm", "ah", "oh", "uhh", "umm", "huh", "like", "actually", "basically"
    )
    
    private val ALLOWED_SHORT = setOf(
        "i", "a", "am", "an", "is", "to", "in", "it", "at", "on", "if", "up", "so", "by", "do", "be", "me", "my", "we", "he", "no", "us", "as", "of", "or"
    )

    private val NOISE_REGEX = Regex("^[^a-z0-9']+$")

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
            // Minimal fallback if asset loading fails
            dictionary.addAll(listOf("the", "be", "to", "of", "and", "a", "in", "that", "have", "i", "it", "for", "not", "on", "with", "he", "as", "you", "do", "at", "this", "but", "his", "by", "from", "they", "we", "say", "her", "she", "or", "an", "will", "my", "one", "all", "would", "there", "their", "what", "so", "up", "out", "if", "about", "who", "get", "which", "go", "me", "when", "make", "can", "like", "time", "no", "just", "him", "know", "take", "people", "into", "year", "your", "good", "some", "could", "them", "see", "other", "than", "then", "now", "look", "only", "come", "its", "over", "think", "also", "back", "after", "use", "two", "how", "our", "work", "first", "well", "way", "even", "new", "want", "because", "any", "these", "give", "day", "most", "us", "is", "am", "are", "was", "were", "been", "has", "had", "did", "does", "done", "yes", "no", "okay", "ok", "right", "sure", "great", "fine", "thanks", "thank", "please", "sorry", "excuse", "hello", "hi", "hey", "name", "talk", "start", "quite", "very", "more", "last", "next", "here", "each"))
        }
    }

    fun analyzeChunk(text: String): ChunkAnalysis {
        val rawTokens = text.split(Regex("\\s+")).filter { it.isNotBlank() }
        val tokenAnalyses = rawTokens.map { analyzeToken(it) }
        
        val englishCount = tokenAnalyses.count { it.isEnglish }
        val meaningfulCount = tokenAnalyses.count { !it.isFiller && !it.isIgnored }
        val fillerCount = tokenAnalyses.count { it.isFiller }
        
        val cleaned = tokenAnalyses
            .filter { !it.isFiller && !it.isIgnored }
            .joinToString(" ") { it.normalized }

        return ChunkAnalysis(
            raw = text,
            cleaned = cleaned,
            tokens = tokenAnalyses,
            englishCount = englishCount,
            meaningfulCount = meaningfulCount,
            fillerCount = fillerCount
        )
    }

    private fun analyzeToken(rawText: String): TokenAnalysis {
        val normalized = rawText.lowercase().replace(Regex("[^a-z0-9']"), "").trim()
        
        if (normalized.isEmpty() || NOISE_REGEX.matches(normalized)) {
            return TokenAnalysis(rawText, normalized, null, false, false, true, ClassificationReason.EMPTY_OR_NOISE)
        }

        // 1. Numbers (92, 100, etc)
        if (normalized.all { it.isDigit() }) {
            return TokenAnalysis(rawText, normalized, null, true, false, false, ClassificationReason.NUMBER)
        }
        
        // 2. Fillers
        if (FILLERS.contains(normalized)) {
            return TokenAnalysis(rawText, normalized, null, false, true, false, ClassificationReason.FILLER)
        }
        
        // 3. Allowed short words (I, a, am...)
        if (ALLOWED_SHORT.contains(normalized)) {
            return TokenAnalysis(rawText, normalized, null, true, false, false, ClassificationReason.ALLOWED_SHORT)
        }
        
        // 4. Exact dictionary match
        if (dictionary.contains(normalized)) {
            return TokenAnalysis(rawText, normalized, null, true, false, false, ClassificationReason.DICTIONARY_MATCH)
        }
        
        // 5. Stem match
        val stem = getStem(normalized)
        if (stem != normalized && (dictionary.contains(stem) || dictionary.contains(stem + "e"))) {
            return TokenAnalysis(rawText, normalized, stem, true, false, false, ClassificationReason.STEM_MATCH)
        }
        
        // 6. Proper Noun heuristic (Capitalized in raw text, not in dictionary)
        if (rawText.isNotEmpty() && rawText[0].isUpperCase() && normalized.length > 2) {
             return TokenAnalysis(rawText, normalized, stem, false, false, false, ClassificationReason.PROPER_NOUN)
        }

        // 7. Unknown
        return TokenAnalysis(rawText, normalized, stem, false, false, false, ClassificationReason.UNKNOWN)
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

    // Helpers for backward compatibility
    fun cleanAndTokenize(text: String): List<String> = analyzeChunk(text).tokens.filter { !it.isFiller && !it.isIgnored }.map { it.normalized }
    fun isEnglish(word: String): Boolean = analyzeToken(word).isEnglish
}
