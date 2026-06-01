package co.bolo.app.analysis

enum class ClassificationType {
    ENGLISH,
    LIKELY_ENGLISH,
    PROPER_NOUN,
    NON_ENGLISH,
    FILLER,
    UNKNOWN;

    fun isEnglishCounting(): Boolean = this == ENGLISH || this == LIKELY_ENGLISH || this == PROPER_NOUN
    fun isMeaningful(): Boolean = this != FILLER && this != UNKNOWN
}
