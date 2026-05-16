package co.bolo.app.data.seed

import co.bolo.app.data.db.BoloDatabase
import co.bolo.app.data.model.Cohort
import co.bolo.app.data.model.Student

/**
 * Minimal seed for MVP startup.
 * Provides one default cohort so the user has a place to start.
 */
object Seed {
    const val DEFAULT_COHORT_ID = "cohort-default"

    suspend fun apply(db: BoloDatabase) {
        if (db.cohortDao().byId(DEFAULT_COHORT_ID) != null) return

        val now = System.currentTimeMillis()
        val cohort = Cohort(
            id = DEFAULT_COHORT_ID,
            name = "My First Cohort",
            createdAt = now
        )
        db.cohortDao().upsert(cohort)

        // Seed a couple of students as examples if needed, 
        // but the user can also add their own in the future.
        // For MVP, we'll keep it very minimal.
    }
}
