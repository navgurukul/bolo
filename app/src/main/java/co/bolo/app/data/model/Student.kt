package co.bolo.app.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "students",
    foreignKeys = [
        ForeignKey(
            entity = Cohort::class,
            parentColumns = ["id"],
            childColumns = ["cohortId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("cohortId")]
)
data class Student(
    @PrimaryKey val id: String,
    val cohortId: String,
    val displayName: String
)
