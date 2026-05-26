package co.bolo.app.data.repo

import co.bolo.app.data.db.CohortDao
import co.bolo.app.data.db.StudentDao
import co.bolo.app.data.model.Cohort
import co.bolo.app.data.model.Student
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CohortRepo @Inject constructor(
    private val cohortDao: CohortDao,
    private val studentDao: StudentDao
) {
    fun observeCohorts(): Flow<List<Cohort>> = cohortDao.observeAll()
    suspend fun cohort(id: String): Cohort? = cohortDao.byId(id)
    suspend fun studentCount(cohortId: String): Int = cohortDao.studentCount(cohortId)

    /** Persist a new cohort. Returns the new id. */
    suspend fun createCohort(name: String): String {
        val id = "cohort-${UUID.randomUUID()}"
        cohortDao.upsert(
            Cohort(id = id, name = name.trim(), createdAt = System.currentTimeMillis())
        )
        return id
    }

    suspend fun renameCohort(id: String, name: String) {
        val existing = cohortDao.byId(id) ?: return
        cohortDao.upsert(existing.copy(name = name.trim()))
    }

    suspend fun deleteCohort(id: String) = cohortDao.delete(id)

    fun observeStudents(cohortId: String): Flow<List<Student>> = studentDao.observeByCohort(cohortId)
    suspend fun studentsOf(cohortId: String): List<Student> = studentDao.byCohort(cohortId)
    suspend fun student(id: String): Student? = studentDao.byId(id)
    suspend fun upsertStudent(student: Student) = studentDao.upsert(student)
    suspend fun deleteStudent(id: String) = studentDao.delete(id)

    /** One-shot insert from a free-form name. Returns the new student id. */
    suspend fun addStudent(cohortId: String, name: String): String {
        val id = "student-${UUID.randomUUID()}"
        studentDao.upsert(
            Student(
                id = id,
                cohortId = cohortId,
                displayName = name.trim()
            )
        )
        return id
    }
}
