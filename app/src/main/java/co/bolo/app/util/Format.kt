package co.bolo.app.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

object Format {
    private val dayFmt = SimpleDateFormat("d MMM", Locale.getDefault())
    private val dayYearFmt = SimpleDateFormat("d MMM yyyy", Locale.getDefault())

    fun percent(share: Float): String = "${(share * 100f).roundToInt()}%"

    fun minutes(ms: Long): String {
        val mins = TimeUnit.MILLISECONDS.toMinutes(ms)
        return when {
            mins <= 0L -> "<1 min"
            mins == 1L -> "1 min"
            else -> "$mins min"
        }
    }

    fun clockMs(ms: Long): String {
        val total = ms / 1000L
        val m = total / 60
        val s = total % 60
        return "%02d:%02d".format(m, s)
    }

    fun relativeDay(epochMs: Long, now: Long = System.currentTimeMillis()): String {
        val days = TimeUnit.MILLISECONDS.toDays(now - epochMs)
        return when {
            days <= 0L -> "Today"
            days == 1L -> "Yesterday"
            days < 7L -> "$days days ago"
            days < 365L -> dayFmt.format(Date(epochMs))
            else -> dayYearFmt.format(Date(epochMs))
        }
    }
}
