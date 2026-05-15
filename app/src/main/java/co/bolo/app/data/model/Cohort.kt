package co.bolo.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cohorts")
data class Cohort(
    @PrimaryKey val id: String,
    val name: String,
    val createdAt: Long
)
