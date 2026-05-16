package co.bolo.app.util

object EnglishAnalyzer {
    // Extended list of common English words for a more robust MVP.
    private val DICTIONARY = setOf(
        "the", "be", "to", "of", "and", "a", "in", "that", "have", "i", "it", "for", "not", "on", "with", "he", "as", "you", "do", "at",
        "this", "but", "his", "by", "from", "they", "we", "say", "her", "she", "or", "an", "will", "my", "one", "all", "would", "there", "their", "what",
        "so", "up", "out", "if", "about", "who", "get", "which", "go", "me", "when", "make", "can", "like", "time", "no", "just", "him", "know", "take",
        "people", "into", "year", "your", "good", "some", "could", "them", "see", "other", "than", "then", "now", "look", "only", "come", "its", "over", "think", "also",
        "back", "after", "use", "two", "how", "our", "work", "first", "well", "way", "even", "new", "want", "because", "any", "these", "give", "day", "most", "us",
        "is", "am", "are", "was", "were", "been", "has", "had", "did", "does", "done", "will", "shall", "should", "would", "may", "might", "must", "can", "could",
        "yes", "no", "okay", "ok", "right", "sure", "good", "great", "fine", "thanks", "thank", "please", "sorry", "excuse", "hello", "hi", "hey",
        "what", "where", "when", "why", "how", "who", "which", "whose", "whom",
        "i", "you", "he", "she", "it", "we", "they", "me", "him", "her", "us", "them", "my", "your", "his", "hers", "its", "our", "their", "mine", "yours", "ours", "theirs"
    )

    // Common filler words to ignore in percentage calculation
    private val FILLERS = setOf("um", "uh", "err", "hmm", "ah", "oh", "like", "you know", "actually", "basically")

    fun cleanAndTokenize(text: String): List<String> {
        // Remove noise, punctuation, and repeated filler characters (simplified)
        return text.lowercase()
            .replace(Regex("[^a-z\\s]"), "") // Remove punctuation
            .split(Regex("\\s+"))
            .map { it.trim() }
            .filter { it.length > 1 } // Ignore single character noise
            .filter { !FILLERS.contains(it) } // Remove obvious fillers
    }

    fun isEnglish(word: String): Boolean {
        // Basic MVP match: in dictionary or matches English phonotactics (heuristic)
        // For SOSC students, technical English words are common.
        if (DICTIONARY.contains(word)) return true
        
        // Technical word heuristic: ends in common English suffixes
        val suffixes = listOf("ing", "tion", "able", "ment", "ness", "ity", "ized", "ally")
        if (suffixes.any { word.endsWith(it) }) return true
        
        // Length heuristic: longer non-dictionary words are often technical/English
        return word.length >= 5
    }

    fun calculateEnglishPercentage(tokens: List<String>): Float {
        if (tokens.isEmpty()) return 0f
        val englishCount = tokens.count { isEnglish(it) }
        return englishCount.toFloat() / tokens.size
    }
}
