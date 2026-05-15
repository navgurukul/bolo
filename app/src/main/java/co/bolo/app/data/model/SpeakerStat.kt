package co.bolo.app.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "speaker_stats",
    foreignKeys = [
        ForeignKey(
            entity = Session::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Student::class,
            parentColumns = ["id"],
            childColumns = ["studentId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("sessionId"), Index("studentId")]
)
data class SpeakerStat(
    @PrimaryKey val id: String,
    val sessionId: String,
    val studentId: String,
    val speechMs: Long,
    val englishMs: Long
) {
    val englishShare: Float
        get() = if (speechMs <= 0L) 0f else englishMs.toFloat() / speechMs.toFloat()
}
