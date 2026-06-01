package co.bolo.app.analysis

object TokenNormalizer {
    private val NOISE_REGEX = Regex("^[^a-z0-9']+$")

    fun normalize(raw: String): String {
        return raw.lowercase().replace(Regex("[^a-z0-9']"), "").trim()
    }

    fun isNoise(normalized: String): Boolean {
        return normalized.isEmpty() || NOISE_REGEX.matches(normalized)
    }

    fun isNumber(normalized: String): Boolean {
        return normalized.isNotEmpty() && normalized.all { it.isDigit() }
    }
}
