package co.bolo.app.data.repo

import co.bolo.app.data.db.SessionDao
import co.bolo.app.data.db.SpeakerStatDao
import co.bolo.app.data.db.TranscriptChunkDao
import co.bolo.app.data.model.Session
import co.bolo.app.data.model.SpeakerStat
import co.bolo.app.data.model.TranscriptChunk
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionRepo @Inject constructor(
    private val sessionDao: SessionDao,
    private val statDao: SpeakerStatDao,
    private val chunkDao: TranscriptChunkDao
) {
    fun observeForCohort(cohortId: String): Flow<List<Session>> = sessionDao.observeByCohort(cohortId)
    fun observeStats(sessionId: String): Flow<List<SpeakerStat>> = statDao.observeBySession(sessionId)
    fun observeStudentTrend(studentId: String): Flow<List<SpeakerStat>> = statDao.observeByStudent(studentId)
    fun observeSession(id: String): Flow<Session?> = sessionDao.observeById(id)
    fun observeChunks(sessionId: String): Flow<List<TranscriptChunk>> = chunkDao.observeBySession(sessionId)
    
    suspend fun session(id: String): Session? = sessionDao.byId(id)
    suspend fun upsertSession(s: Session) = sessionDao.upsert(s)
    suspend fun upsertStats(stats: List<SpeakerStat>) = statDao.upsertAll(stats)
    suspend fun insertChunks(chunks: List<TranscriptChunk>) = chunkDao.insertAll(chunks)
}
