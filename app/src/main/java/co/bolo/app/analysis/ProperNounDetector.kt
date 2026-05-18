package co.bolo.app.analysis

object ProperNounDetector {
    /**
     * Heuristic for proper nouns:
     * - Starts with uppercase
     * - Contains only latin letters/numbers
     * - Minimum length of 3 (to avoid "I", "At", etc. which are in dictionary anyway)
     */
    fun isProperNoun(rawText: String, normalized: String): Boolean {
        if (rawText.isEmpty()) return false
        val firstChar = rawText[0]
        
        // Basic check: Is the first letter capitalized?
        if (!firstChar.isUpperCase()) return false
        
        // Ensure it's not a common short word or just noise
        if (normalized.length < 3) return false
        
        // Ensure it's mostly alphabetical
        return normalized.all { it.isLetter() || it.isDigit() }
    }
}
