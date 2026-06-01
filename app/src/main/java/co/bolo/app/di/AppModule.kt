package co.bolo.app.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import co.bolo.app.data.db.BoloDatabase
import co.bolo.app.data.db.CohortDao
import co.bolo.app.data.db.SessionDao
import co.bolo.app.data.db.SpeakerStatDao
import co.bolo.app.data.db.StudentDao
import co.bolo.app.data.db.TranscriptChunkDao
import co.bolo.app.data.repo.SessionManager
import co.bolo.app.data.seed.Seed
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): BoloDatabase {
        val seedScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        lateinit var db: BoloDatabase
        db = Room.databaseBuilder(ctx, BoloDatabase::class.java, BoloDatabase.NAME)
            // From v5 onward we preserve user data across app updates.
            // Older v1..v3 installs (pre-public, dev devices only) are
            // allowed to wipe cleanly — they predate any real cohort data.
            .addMigrations(*BoloDatabase.ALL_MIGRATIONS)
            .fallbackToDestructiveMigrationFrom(1, 2, 3)
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db2: androidx.sqlite.db.SupportSQLiteDatabase) {
                    super.onCreate(db2)
                    // Seed the default cohort exactly once, on first install.
                    seedScope.launch { Seed.apply(db) }
                }
            })
            .build()
        return db
    }

    @Provides fun cohortDao(db: BoloDatabase): CohortDao = db.cohortDao()
    @Provides fun studentDao(db: BoloDatabase): StudentDao = db.studentDao()
    @Provides fun sessionDao(db: BoloDatabase): SessionDao = db.sessionDao()
    @Provides fun statDao(db: BoloDatabase): SpeakerStatDao = db.speakerStatDao()
    @Provides fun transcriptChunkDao(db: BoloDatabase): TranscriptChunkDao = db.transcriptChunkDao()

    @Provides
    @Singleton
    fun provideSessionManager(): SessionManager = SessionManager()
}
