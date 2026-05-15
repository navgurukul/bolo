package co.bolo.app.data.seed

import co.bolo.app.data.db.BoloDatabase
import co.bolo.app.data.model.Cohort
import co.bolo.app.data.model.Session
import co.bolo.app.data.model.SpeakerStat
import co.bolo.app.data.model.Student
import java.util.UUID
import kotlin.math.roundToLong
import kotlin.random.Random

/**
 * Phase 0 seed: one cohort, 6 students, 4 past sessions with deterministic-but-believable
 * per-speaker stats. Re-run safe — keyed UUIDs derived from a fixed seed.
 */
object Seed {
    private const val COHORT_ID = "cohort-ngk-batch7"
    private val STUDENT_NAMES = listOf("Anita", "Rahul", "Priya", "Vikram", "Meena", "Arjun")
    private val TOPICS = listOf("Daily life", "Mock interview", "News chat", "Tech")

    suspend fun apply(db: BoloDatabase) {
        if (db.cohortDao().byId(COHORT_ID) != null) return

        val now = System.currentTimeMillis()
        val cohort = Cohort(
            id = COHORT_ID,
            name = "NavGurukul · Batch 7",
            createdAt = now - 30L * 24 * 3_600_000
        )
        db.cohortDao().upsert(cohort)

        val students = STUDENT_NAMES.mapIndexed { idx, name ->
            Student(
                id = "stu-$idx-${name.lowercase()}",
                cohortId = COHORT_ID,
                displayName = name,
                voiceEmbedding = null,
                enrolledAt = now - (29L - idx) * 24 * 3_600_000
            )
        }
        students.forEach { db.studentDao().upsert(it) }

        val rng = Random(seed = 42)
        val sessionsBack = listOf(14, 9, 5, 1)
        sessionsBack.forEachIndexed { sIdx, daysAgo ->
            val started = now - daysAgo.toLong() * 24 * 3_600_000
            val durationMin = 35 + rng.nextInt(15)
            val totalSpeechMs = (durationMin * 60_000L * (0.55 + rng.nextDouble() * 0.2)).roundToLong()
            val baselineEnglish = 0.32 + sIdx * 0.06 + rng.nextDouble() * 0.05
            val englishSpeechMs = (totalSpeechMs * baselineEnglish).roundToLong()

            val session = Session(
                id = "ses-$sIdx-${UUID(0L, sIdx.toLong()).toString().takeLast(8)}",
                cohortId = COHORT_ID,
                topic = TOPICS[sIdx % TOPICS.size],
                startedAt = started,
                endedAt = started + durationMin * 60_000L,
                totalSpeechMs = totalSpeechMs,
                englishSpeechMs = englishSpeechMs
            )
            db.sessionDao().upsert(session)

            val perStudentShare = FloatArray(students.size) { 1f / students.size + (rng.nextFloat() - 0.5f) * 0.06f }
            val shareSum = perStudentShare.sum()
            val normalized = perStudentShare.map { it / shareSum }

            val stats = students.mapIndexed { idx, student ->
                val speechMs = (totalSpeechMs * normalized[idx]).roundToLong()
                // Each student's English share drifts around the session baseline.
                val personalEnglish = (baselineEnglish + (rng.nextDouble() - 0.5) * 0.2).coerceIn(0.05, 0.95)
                val englishMs = (speechMs * personalEnglish).roundToLong()
                SpeakerStat(
                    id = "stat-${session.id}-${student.id}",
                    sessionId = session.id,
                    studentId = student.id,
                    speechMs = speechMs,
                    englishMs = englishMs
                )
            }
            db.speakerStatDao().upsertAll(stats)
        }
    }
}
