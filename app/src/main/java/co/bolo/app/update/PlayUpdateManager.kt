package co.bolo.app.update

import android.app.Activity
import android.util.Log
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability

/**
 * Manages official Google Play In-App Updates for Play Store releases.
 * 
 * Safe for Play Store compliance: Uses Google's official Play Core SDK,
 * requiring zero custom APK downloading or dangerous permissions.
 */
object PlayUpdateManager {

    private const val TAG = "PlayUpdateManager"
    const val REQUEST_CODE_UPDATE = 9001

    /**
     * Checks Google Play Store for an update and prompts the user if available.
     */
    fun checkForUpdates(activity: Activity) {
        val appUpdateManager = AppUpdateManagerFactory.create(activity)
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                try {
                    val updateOptions = AppUpdateOptions.defaultOptions(AppUpdateType.IMMEDIATE)
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        activity,
                        updateOptions,
                        REQUEST_CODE_UPDATE
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to start Google Play update flow", e)
                }
            }
        }.addOnFailureListener { e ->
            // Normal for local debug builds or sideloaded APKs not downloaded from Play Store.
            Log.d(TAG, "Google Play update check skipped or unavailable: ${e.message}")
        }
    }

    /**
     * Resume an in-progress immediate update if the user returns to the app.
     */
    fun resumeUpdateIfInProgress(activity: Activity) {
        val appUpdateManager = AppUpdateManagerFactory.create(activity)
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                try {
                    val updateOptions = AppUpdateOptions.defaultOptions(AppUpdateType.IMMEDIATE)
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        activity,
                        updateOptions,
                        REQUEST_CODE_UPDATE
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to resume Play update", e)
                }
            }
        }
    }
}

