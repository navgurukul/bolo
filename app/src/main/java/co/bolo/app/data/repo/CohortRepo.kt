package co.bolo.app.data.repo

import co.bolo.app.data.db.CohortDao
import co.bolo.app.data.db.StudentDao
import co.bolo.app.data.model.Cohort
import co.bolo.app.data.model.Student
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CohortRepo @Inject constructor(
    private val cohortDao: CohortDao,
    private val studentDao: StudentDao
) {
    fun observeCohorts(): Flow<List<Cohort>> = cohortDao.observeAll()
    suspend fun cohort(id: String): Cohort? = cohortDao.byId(id)
    fun observeStudents(cohortId: String): Flow<List<Student>> = studentDao.observeByCohort(cohortId)
    suspend fun student(id: String): Student? = studentDao.byId(id)
    suspend fun upsertStudent(student: Student) = studentDao.upsert(student)
}
