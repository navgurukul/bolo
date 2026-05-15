package co.bolo.app.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import co.bolo.app.data.model.Cohort
import co.bolo.app.data.model.Session
import co.bolo.app.data.model.SpeakerStat
import co.bolo.app.data.model.Student

@Database(
    entities = [Cohort::class, Student::class, Session::class, SpeakerStat::class],
    version = 1,
    exportSchema = true
)
abstract class BoloDatabase : RoomDatabase() {
    abstract fun cohortDao(): CohortDao
    abstract fun studentDao(): StudentDao
    abstract fun sessionDao(): SessionDao
    abstract fun speakerStatDao(): SpeakerStatDao

    companion object {
        const val NAME = "bolo.db"
    }
}
