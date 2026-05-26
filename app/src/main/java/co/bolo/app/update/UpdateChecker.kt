package co.bolo.app.update

import android.util.Log
import co.bolo.app.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

/**
 * One-shot result of a GitHub /releases/latest check.
 */
sealed class UpdateState {
    object Idle : UpdateState()
    object Checking : UpdateState()
    object UpToDate : UpdateState()
    data class Available(
        val versionName: String,
        val versionCode: Int,
        val apkUrl: String,
        val notes: String
    ) : UpdateState()
    data class Error(val message: String) : UpdateState()
}

/**
 * Checks GitHub Releases for a newer build of Bolo. Convention:
 *
 *  - Each GitHub release tag is for one build.
 *  - The release body **must** start with a line `versionCode: N`
 *    where N is the `BuildConfig.VERSION_CODE` of that build. The
 *    checker compares N to the installed `BuildConfig.VERSION_CODE`
 *    to decide whether to prompt the user.
 *  - The release **must** attach the signed `app-release.apk` as an
 *    asset; the checker picks the first asset whose name ends in
 *    `.apk` and hands its `browser_download_url` to the downloader.
 *
 * Network errors / missing fields / a missing versionCode line are
 * treated as "no update right now" rather than surfaced loudly — the
 * facilitator opens Bolo to run a class, not to debug the updater.
 */
@Singleton
class UpdateChecker @Inject constructor() {

    suspend fun checkForUpdate(): UpdateState = withContext(Dispatchers.IO) {
        val url = URL("https://api.github.com/repos/$OWNER/$REPO/releases/latest")
        val conn = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 10_000
            readTimeout = 10_000
            setRequestProperty("Accept", "application/vnd.github+json")
            setRequestProperty("User-Agent", "Bolo-Android/${BuildConfig.VERSION_NAME}")
        }
        try {
            val code = conn.responseCode
            if (code != HttpURLConnection.HTTP_OK) {
                Log.w(TAG, "GitHub API returned $code")
                return@withContext UpdateState.Error("HTTP $code")
            }
            val body = conn.inputStream.bufferedReader().use { it.readText() }
            val json = JSONObject(body)

            val tagName = json.optString("tag_name").ifBlank {
                return@withContext UpdateState.Error("No tag_name in response")
            }
            val releaseBody = json.optString("body", "")
            val latestVersionCode = parseVersionCode(releaseBody) ?: run {
                Log.w(TAG, "Latest release $tagName has no `versionCode: N` line — skipping check")
                return@withContext UpdateState.Idle
            }

            if (latestVersionCode <= BuildConfig.VERSION_CODE) {
                return@withContext UpdateState.UpToDate
            }

            val apkUrl = findApkAssetUrl(json) ?: run {
                Log.w(TAG, "Release $tagName has no APK asset")
                return@withContext UpdateState.Error("No APK asset on latest release")
            }

            UpdateState.Available(
                versionName = tagName.removePrefix("v"),
                versionCode = latestVersionCode,
                apkUrl = apkUrl,
                notes = releaseBody.lineSequence()
                    .dropWhile { it.startsWith("versionCode:") || it.isBlank() }
                    .joinToString("\n")
                    .trim()
            )
        } catch (e: Exception) {
            Log.w(TAG, "Update check failed", e)
            UpdateState.Error(e.message ?: "unknown")
        } finally {
            conn.disconnect()
        }
    }

    /** Pulls the first `versionCode: N` line out of the release body. */
    private fun parseVersionCode(body: String): Int? {
        val regex = Regex("""(?im)^\s*versionCode\s*[:=]\s*(\d+)""")
        return regex.find(body)?.groupValues?.get(1)?.toIntOrNull()
    }

    private fun findApkAssetUrl(release: JSONObject): String? {
        val assets = release.optJSONArray("assets") ?: return null
        for (i in 0 until assets.length()) {
            val asset = assets.optJSONObject(i) ?: continue
            val name = asset.optString("name")
            if (name.endsWith(".apk", ignoreCase = true)) {
                return asset.optString("browser_download_url").ifBlank { null }
            }
        }
        return null
    }

    companion object {
        private const val TAG = "UpdateChecker"
        // Update channel — pinned to NavGurukul's public Bolo repo.
        private const val OWNER = "navgurukul"
        private const val REPO = "bolo"
    }
}
