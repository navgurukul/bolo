package co.bolo.app.data.model

import androidx.room.ColumnInfo
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
    val displayName: String,
    // Phase 0: null. Phase 2: AES-wrapped 256-D vector, 256 * 4 = 1024 B serialized.
    @ColumnInfo(name = "voiceEmbedding") val voiceEmbedding: ByteArray? = null,
    val enrolledAt: Long? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Student) return false
        return id == other.id && cohortId == other.cohortId && displayName == other.displayName &&
            enrolledAt == other.enrolledAt &&
            voiceEmbedding.contentEqualsOrBothNull(other.voiceEmbedding)
    }
    override fun hashCode(): Int {
        var h = id.hashCode()
        h = 31 * h + cohortId.hashCode()
        h = 31 * h + displayName.hashCode()
        h = 31 * h + (enrolledAt?.hashCode() ?: 0)
        h = 31 * h + (voiceEmbedding?.contentHashCode() ?: 0)
        return h
    }
}

private fun ByteArray?.contentEqualsOrBothNull(other: ByteArray?): Boolean =
    if (this == null && other == null) true
    else if (this != null && other != null) this.contentEquals(other)
    else false
