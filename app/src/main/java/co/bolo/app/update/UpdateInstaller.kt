package co.bolo.app.update

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Downloads an APK from the URL given by [UpdateChecker], then hands it to
 * the system package installer. The user still has to confirm the install
 * in the OS dialog — Android does not allow silent updates for sideloaded
 * apps. If the device hasn't yet granted Bolo "Install unknown apps" the
 * installer dialog itself walks the user through the OS toggle.
 */
@Singleton
class UpdateInstaller @Inject constructor(
    @ApplicationContext private val context: Context
) {

    /**
     * Enqueues the APK download. Returns the DownloadManager id so the
     * caller (the ViewModel) can correlate completion notifications.
     */
    fun startDownload(apkUrl: String, versionName: String): Long {
        val dir = File(context.getExternalFilesDir(null), "updates").apply {
            mkdirs()
            // Wipe any stale APKs so we don't accumulate megabytes per release.
            listFiles()?.forEach { it.delete() }
        }
        val fileName = "bolo-$versionName.apk"
        val outFile = File(dir, fileName)

        val request = DownloadManager.Request(Uri.parse(apkUrl)).apply {
            setTitle("Bolo $versionName")
            setDescription("Downloading update")
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setDestinationUri(Uri.fromFile(outFile))
            setAllowedOverMetered(true)
            setAllowedOverRoaming(true)
        }

        val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val id = dm.enqueue(request)
        Log.d(TAG, "Enqueued update download id=$id to ${outFile.absolutePath}")
        return id
    }

    /**
     * Registers a one-shot receiver that fires the system installer once
     * the given download id completes. Caller is responsible for cleaning
     * up the receiver (we self-unregister inside).
     */
    fun installWhenComplete(downloadId: Long) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context, intent: Intent) {
                val completedId = intent.getLongExtra(
                    DownloadManager.EXTRA_DOWNLOAD_ID, -1L
                )
                if (completedId != downloadId) return
                try {
                    val uri = resolveDownloadedFileUri(downloadId) ?: return
                    launchInstaller(uri)
                } finally {
                    try { ctx.unregisterReceiver(this) } catch (_: Exception) {}
                }
            }
        }
        val filter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED)
        } else {
            @Suppress("UnspecifiedRegisterReceiverFlag")
            context.registerReceiver(receiver, filter)
        }
    }

    private fun resolveDownloadedFileUri(downloadId: Long): Uri? {
        val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val query = DownloadManager.Query().setFilterById(downloadId)
        dm.query(query).use { cursor ->
            if (cursor == null || !cursor.moveToFirst()) return null
            val statusIdx = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
            val status = cursor.getInt(statusIdx)
            if (status != DownloadManager.STATUS_SUCCESSFUL) {
                Log.w(TAG, "Download $downloadId finished with status=$status")
                return null
            }
            val localUriIdx = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
            val localUriStr = cursor.getString(localUriIdx) ?: return null
            val localUri = Uri.parse(localUriStr)
            val path = localUri.path ?: return null
            val file = File(path)
            return FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        }
    }

    private fun launchInstaller(contentUri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(contentUri, "application/vnd.android.package-archive")
            addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION or
                    Intent.FLAG_ACTIVITY_NEW_TASK
            )
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to launch installer", e)
        }
    }

    companion object {
        private const val TAG = "UpdateInstaller"
    }
}
