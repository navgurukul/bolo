package co.bolo.app.ui.components

/**
 * Mock data mirroring the JSX design seeds (BOLO_COHORT, BOLO_TOPICS, BOLO_HISTORY).
 * New screens are presentational only — they pull from this rather than Room.
 */

data class MockStudent(
    val id: String,
    val name: String,
    val pct: Int,
    val weekly: List<Int>
)

data class MockHistorySession(
    val id: String,
    val date: String,
    val topic: String,
    val duration: Int,
    val pct: Int,
    val students: Int,
    val top: String
)

val BOLO_COHORT: List<MockStudent> = listOf(
    MockStudent("priya",   "Priya",   84, listOf(56, 62, 68, 64, 71, 75, 78, 84)),
    MockStudent("karthik", "Karthik", 78, listOf(44, 51, 55, 58, 63, 68, 72, 78)),
    MockStudent("rohit",   "Rohit",   71, listOf(38, 42, 49, 52, 58, 63, 67, 71)),
    MockStudent("meena",   "Meena",   68, listOf(40, 44, 46, 53, 55, 60, 64, 68)),
    MockStudent("daniel",  "Daniel",  64, listOf(35, 38, 41, 46, 52, 55, 60, 64)),
    MockStudent("asha",    "Asha",    58, listOf(29, 33, 36, 41, 44, 48, 53, 58))
)

val BOLO_TOPICS: List<String> = listOf(
    "Daily life",
    "Mock interview",
    "News chat",
    "Tech",
    "Free talk",
    "Family"
)

val BOLO_HISTORY: List<MockHistorySession> = listOf(
    MockHistorySession("s1", "Today · 9:30",     "Daily life",     28, 71, 6, "Priya"),
    MockHistorySession("s2", "Yesterday · 4:10", "Mock interview", 42, 78, 5, "Karthik"),
    MockHistorySession("s3", "Mon · 9:30",       "News chat",      35, 64, 6, "Priya"),
    MockHistorySession("s4", "Sat · 11:00",      "Free talk",      20, 58, 4, "Meena"),
    MockHistorySession("s5", "Fri · 9:30",       "Tech",           31, 66, 6, "Rohit"),
    MockHistorySession("s6", "Thu · 4:00",       "Family",         24, 53, 5, "Asha")
)
