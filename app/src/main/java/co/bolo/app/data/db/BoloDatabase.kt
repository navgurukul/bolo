package co.bolo.app.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import co.bolo.app.data.model.Cohort
import co.bolo.app.data.model.Session
import co.bolo.app.data.model.SpeakerStat
import co.bolo.app.data.model.Student
import co.bolo.app.data.model.TranscriptChunk

@Database(
    entities = [Cohort::class, Student::class, Session::class, SpeakerStat::class, TranscriptChunk::class],
    version = 6,
    exportSchema = true
)
abstract class BoloDatabase : RoomDatabase() {
    abstract fun cohortDao(): CohortDao
    abstract fun studentDao(): StudentDao
    abstract fun sessionDao(): SessionDao
    abstract fun speakerStatDao(): SpeakerStatDao
    abstract fun transcriptChunkDao(): TranscriptChunkDao

    companion object {
        const val NAME = "bolo.db"

        // v4 -> v5 — no schema change. From v5 forward we preserve user
        // data across app updates. Every future schema bump MUST add a
        // real Migration object to ALL_MIGRATIONS below.
        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) { /* no-op */ }
        }

        // v5 -> v6 — drop the never-populated voiceEmbedding + enrolledAt
        // columns from students. SQLite versions shipped on older Android
        // builds can't DROP COLUMN, so we recreate the table.
        val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE students_new (
                        id TEXT NOT NULL PRIMARY KEY,
                        cohortId TEXT NOT NULL,
                        displayName TEXT NOT NULL,
                        FOREIGN KEY(cohortId) REFERENCES cohorts(id) ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    """
                    INSERT INTO students_new (id, cohortId, displayName)
                    SELECT id, cohortId, displayName FROM students
                    """.trimIndent()
                )
                db.execSQL("DROP TABLE students")
                db.execSQL("ALTER TABLE students_new RENAME TO students")
                db.execSQL("CREATE INDEX index_students_cohortId ON students(cohortId)")
            }
        }

        val ALL_MIGRATIONS: Array<Migration> = arrayOf(MIGRATION_4_5, MIGRATION_5_6)
    }
}
