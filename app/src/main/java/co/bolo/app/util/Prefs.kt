package co.bolo.app.util

import android.content.Context
import android.content.SharedPreferences

/**
 * Tiny SharedPreferences wrapper for first-run persistence.
 * Phase 0: only "paired" matters — the rest of state lives in Room or in-memory.
 */
object Prefs {
    private const val FILE = "bolo_prefs"
    private const val KEY_PAIRED = "paired"

    private fun prefs(context: Context): SharedPreferences =
        context.applicationContext.getSharedPreferences(FILE, Context.MODE_PRIVATE)

    fun isPaired(context: Context): Boolean =
        prefs(context).getBoolean(KEY_PAIRED, false)

    fun setPaired(context: Context, value: Boolean) {
        prefs(context).edit().putBoolean(KEY_PAIRED, value).apply()
    }
}
