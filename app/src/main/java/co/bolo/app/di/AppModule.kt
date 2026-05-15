package co.bolo.app.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import co.bolo.app.data.db.BoloDatabase
import co.bolo.app.data.db.CohortDao
import co.bolo.app.data.db.SessionDao
import co.bolo.app.data.db.SpeakerStatDao
import co.bolo.app.data.db.StudentDao
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
            .fallbackToDestructiveMigration()
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db2: androidx.sqlite.db.SupportSQLiteDatabase) {
                    super.onCreate(db2)
                    seedScope.launch { Seed.apply(db) }
                }
            })
            .build()
        // Belt-and-braces: also seed on first open in case the DB exists but is empty
        // (Phase 0 only — Phase 4 will move seeding behind a build flag).
        seedScope.launch { Seed.apply(db) }
        return db
    }

    @Provides fun cohortDao(db: BoloDatabase): CohortDao = db.cohortDao()
    @Provides fun studentDao(db: BoloDatabase): StudentDao = db.studentDao()
    @Provides fun sessionDao(db: BoloDatabase): SessionDao = db.sessionDao()
    @Provides fun statDao(db: BoloDatabase): SpeakerStatDao = db.speakerStatDao()
}
