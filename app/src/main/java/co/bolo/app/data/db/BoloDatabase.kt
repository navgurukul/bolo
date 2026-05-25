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
    version = 5,
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

        val ALL_MIGRATIONS: Array<Migration> = arrayOf(MIGRATION_4_5)
    }
}
