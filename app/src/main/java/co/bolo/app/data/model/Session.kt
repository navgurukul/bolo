package co.bolo.app.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "sessions",
    foreignKeys = [
        ForeignKey(
            entity = Cohort::class,
            parentColumns = ["id"],
            childColumns = ["cohortId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("cohortId"), Index("startedAt")]
)
data class Session(
    @PrimaryKey val id: String,
    val cohortId: String,
    val topic: String,
    val startedAt: Long,
    val endedAt: Long?,
    val totalSpeechMs: Long,
    val englishSpeechMs: Long
) {
    val englishShare: Float
        get() = if (totalSpeechMs <= 0L) 0f else englishSpeechMs.toFloat() / totalSpeechMs.toFloat()
}
