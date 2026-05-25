package co.bolo.app.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import co.bolo.app.data.model.Cohort
import co.bolo.app.data.model.Session
import co.bolo.app.data.model.SpeakerStat
import co.bolo.app.data.model.Student
import co.bolo.app.data.model.TranscriptChunk
import kotlinx.coroutines.flow.Flow

@Dao
interface CohortDao {
    @Query("SELECT * FROM cohorts ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<Cohort>>

    @Query("SELECT * FROM cohorts WHERE id = :id")
    suspend fun byId(id: String): Cohort?

    @Query("SELECT COUNT(*) FROM students WHERE cohortId = :cohortId")
    suspend fun studentCount(cohortId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(cohort: Cohort)

    @Query("DELETE FROM cohorts WHERE id = :id")
    suspend fun delete(id: String)
}

@Dao
interface StudentDao {
    @Query("SELECT * FROM students WHERE cohortId = :cohortId ORDER BY displayName")
    fun observeByCohort(cohortId: String): Flow<List<Student>>

    @Query("SELECT * FROM students WHERE cohortId = :cohortId ORDER BY displayName")
    suspend fun byCohort(cohortId: String): List<Student>

    @Query("SELECT * FROM students WHERE id = :id")
    suspend fun byId(id: String): Student?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(student: Student)

    @Update
    suspend fun update(student: Student)

    @Query("DELETE FROM students WHERE id = :id")
    suspend fun delete(id: String)
}

@Dao
interface SessionDao {
    @Query("SELECT * FROM sessions WHERE cohortId = :cohortId ORDER BY startedAt DESC")
    fun observeByCohort(cohortId: String): Flow<List<Session>>

    @Query("SELECT * FROM sessions WHERE id = :id")
    suspend fun byId(id: String): Session?

    @Query("SELECT * FROM sessions WHERE id = :id")
    fun observeById(id: String): Flow<Session?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(session: Session)
}

@Dao
interface SpeakerStatDao {
    @Query("SELECT * FROM speaker_stats WHERE sessionId = :sessionId")
    fun observeBySession(sessionId: String): Flow<List<SpeakerStat>>

    @Query("SELECT * FROM speaker_stats WHERE studentId = :studentId")
    fun observeByStudent(studentId: String): Flow<List<SpeakerStat>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(stat: SpeakerStat)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(stats: List<SpeakerStat>)
}

@Dao
interface TranscriptChunkDao {
    @Query("SELECT * FROM transcript_chunks WHERE sessionId = :sessionId ORDER BY sequence ASC")
    fun observeBySession(sessionId: String): Flow<List<TranscriptChunk>>

    @Query("SELECT * FROM transcript_chunks WHERE sessionId = :sessionId ORDER BY sequence ASC")
    suspend fun bySession(sessionId: String): List<TranscriptChunk>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(chunks: List<TranscriptChunk>)
}
