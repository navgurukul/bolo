package co.bolo.app.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transcript_chunks",
    foreignKeys = [
        ForeignKey(
            entity = Session::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("sessionId")]
)
data class TranscriptChunk(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: String,
    val sequence: Int,
    val rawText: String,
    val cleanedText: String,
    val englishCount: Int,
    val meaningfulCount: Int,
    val fillerCount: Int,
    val studentId: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
