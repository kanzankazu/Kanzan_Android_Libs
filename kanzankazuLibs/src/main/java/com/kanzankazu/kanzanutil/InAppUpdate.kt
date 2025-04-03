package com.kanzankazu.kanzanutil

import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.ActivityResult
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.kanzankazu.R
import com.kanzankazu.kanzanutil.kanzanextension.type.debugMessageDebug
import java.lang.ref.WeakReference

/**
 * A class that manages in-app updates for an Android application.
 * It uses `AppUpdateManager` to handle both immediate and flexible update types and provides
 * utility methods to validate, execute, and complete updates.
 *
 * @constructor Creates an instance of `InAppUpdate` with the specified `Activity` and optional `AppUpdateManager`.
 *
 * @param activity The `Activity` used to display UI components and manage the update lifecycle.
 * @param appUpdateManager The `AppUpdateManager` instance for managing the in-app update process. By default, a new instance is created using `AppUpdate
 * ManagerFactory`.
 */
class InAppUpdate(
    private val activity: Activity,
    private val appUpdateManager: AppUpdateManager = AppUpdateManagerFactory.create(activity)
) {

    var appUpdateType: Int = AppUpdateType.FLEXIBLE
    private var installStateUpdatedListener: InstallStateUpdatedListener? = null
    private val updateTimeoutMs: Long = 10_000L // Timeout untuk update selesai

    init {
        setupInAppUpdate()
    }

    /**
     * Configures and initiates an in-app update process using the specified update type.
     * Registers a listener to handle installation state updates and checks for update availability.
     * If an update is available and allowed for the specified type, it begins the update process.
     * On failure to check update info, logs the error.
     *
     * @param appUpdateType The type of in-app update to perform, defaulting to `AppUpdateType.FLEXIBLE`.
     *                      Use `AppUpdateType.IMMEDIATE` for immediate updates or `AppUpdateType.FLEXIBLE` for flexible updates.
     * @return The `AppUpdateManager` object used for managing the in-app update process.
     *
     * Example:
     * ```kotlin
     * val appUpdateManager = setupInAppUpdate(AppUpdateType.IMMEDIATE)
     * ```
     */
    fun setupInAppUpdate(appUpdateType: Int = AppUpdateType.FLEXIBLE): AppUpdateManager {
        this.appUpdateType = appUpdateType
        val weakActivity = WeakReference(activity)

        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        installStateUpdatedListener = InstallStateUpdatedListener { state ->
            weakActivity.get()?.let { handleInstallStatus(state) }
        }

        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            handleAppUpdateAvailability(appUpdateInfo)
        }.addOnFailureListener {
            "Failed to check for app update info: ${it.message}".debugMessageDebug()
        }

        return appUpdateManager
    }

    /**
     * Initiates the in-app update flow for the provided update information.
     * Handles the process of starting the update and manages potential exceptions by logging errors and showing feedback to the user.
     *
     * @param appUpdateInfo The update information required to start the in-app update flow.
     */
    fun updateStart(appUpdateInfo: AppUpdateInfo) {
        try {
            appUpdateManager.startUpdateFlowForResult(
                appUpdateInfo,
                appUpdateType,
                activity,
                REQUEST_CODE_IN_APP_UPDATE
            )
        } catch (e: Exception) {
            logErrorAndShowSnackbar("Failed to start in-app update: ${e.message}")
        }
    }

    /**
     * Completes the in-app update process. This method must be called after a flexible update is
     * downloaded successfully to apply the update.
     *
     * It ensures that the app update is finalized and the installation process begins.
     * Typically, this should be triggered when the user interacts with an option to complete the update.
     *
     * Example:
     * ```kotlin
     * // Call this method after confirming the user is ready to complete the update
     * updateComplete()
     * ```
     */
    fun updateComplete() {
        appUpdateManager.completeUpdate()
    }

    /**
     * Displays a Snackbar indicating that an update has been completed, with optional customization for
     * the message, duration, action text, action text color, and action callback. If not specified, default
     * values are used.
     *
     * @param message The message to display in the Snackbar. Defaults to "An update has just been downloaded." if null.
     * @param lengthSnackbar The duration for which the Snackbar should be visible. Defaults to `Snackbar.LENGTH_INDEFINITE` if null.
     * @param actionText The text for the action button on the Snackbar. Defaults to "RESTART" if null.
     * @param actionTextColor The color of the action button text. Defaults to the specified `R.color.baseWhite` if null.
     * @param action The callback triggered when the action button is clicked. Defaults to triggering the `updateComplete` function if null.
     */
    fun updateCompleteSnackbarAction(
        message: CharSequence? = null,
        lengthSnackbar: Int? = null,
        actionText: CharSequence? = null,
        actionTextColor: Int? = null,
        action: View.OnClickListener? = null,
    ) {
        Handler(Looper.getMainLooper()).post {
            showSnackbar(
                message ?: "An update has just been downloaded.",
                lengthSnackbar ?: Snackbar.LENGTH_INDEFINITE,
                actionText ?: "RESTART",
                actionTextColor ?: ContextCompat.getColor(activity, R.color.baseWhite),
                action ?: View.OnClickListener { updateComplete() }
            )
            scheduleUpdateCompletionTimeout()
        }
    }

    /**
     * Validates the current app update type and determines if it is a flexible update type.
     * Executes the given callback with a Boolean result indicating whether the current update type
     * is flexible or not.
     *
     * @param onUpdateFlexible A lambda function taking a Boolean parameter. It will be called with `true`
     * if the `appUpdateType` matches `AppUpdateType.FLEXIBLE`, otherwise `false`.
     */
    fun validateUpdateType(onUpdateFlexible: (Boolean) -> Unit) {
        onUpdateFlexible(appUpdateType == AppUpdateType.FLEXIBLE)
    }

    /**
     * Validates the result of an activity's result callback, particularly for in-app updates.
     * This method checks if the request code matches the in-app update request and handles the result accordingly.
     *
     * @param requestCode The integer request code originally supplied to start the activity.
     * @param resultCode The integer result code returned by the child activity through its setResult().
     * @param data An optional Intent that carries additional result data (can be null).
     */
    fun validateOnActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_IN_APP_UPDATE) {
            handleActivityResult(resultCode)
        }
    }

    /**
     * Unregisters the listener for app update state changes.
     * This method ensures that the registered `installStateUpdatedListener` is
     * removed from the `appUpdateManager` to avoid further callbacks.
     *
     * If no listener is registered, this method performs no action.
     *
     * Example:
     * ```kotlin
     * // Call this method when the activity is no longer active or during cleanup
     * inAppUpdate.unregisterListener()
     * ```
     */
    fun unregisterListener() {
        installStateUpdatedListener?.let {
            appUpdateManager.unregisterListener(it)
        }
    }

    /**
     * Validates if the current activity is in a valid state to execute operations.
     * Checks whether the method is running on the main thread and ensures that
     * the activity is neither finishing nor destroyed.
     *
     * @return `true` if the activity is valid and can safely perform operations,
     *         `false` otherwise.
     */
    fun isValidActivity(): Boolean {
        return Looper.myLooper() == Looper.getMainLooper() &&
                !activity.isFinishing &&
                !activity.isDestroyed
    }

    /** Mengelola status instalasi. */
    private fun handleInstallStatus(state: InstallState) {
        when (state.installStatus()) {
            InstallStatus.DOWNLOADED -> {
                "Install downloaded".debugMessageDebug()
                updateCompleteSnackbarAction()
            }
            InstallStatus.DOWNLOADING -> {
                val progress = "${state.bytesDownloaded()} / ${state.totalBytesToDownload()}"
                "Downloading update: $progress".debugMessageDebug()
            }
            InstallStatus.CANCELED -> "Install canceled".debugMessageDebug()
            InstallStatus.FAILED -> "Install failed".debugMessageDebug()
            InstallStatus.INSTALLED -> "Install completed".debugMessageDebug()
            InstallStatus.INSTALLING -> "Installing update".debugMessageDebug()
            InstallStatus.PENDING -> "Install pending".debugMessageDebug()
            InstallStatus.UNKNOWN -> "Install status unknown".debugMessageDebug()
        }
    }

    /** Menangani ketersediaan pembaruan. */
    private fun handleAppUpdateAvailability(appUpdateInfo: AppUpdateInfo) {
        if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
            appUpdateInfo.isUpdateTypeAllowed(appUpdateType)
        ) {
            installStateUpdatedListener?.let {
                appUpdateManager.registerListener(it)
            }
            updateStart(appUpdateInfo)
        } else {
            "No update available or update type not allowed.".debugMessageDebug()
            unregisterListener()
        }
    }

    /** Menangani hasil dari [Activity.onActivityResult]. */
    private fun handleActivityResult(resultCode: Int) {
        Handler(Looper.getMainLooper()).post {
            when (resultCode) {
                Activity.RESULT_OK -> "Update success.".debugMessageDebug()
                Activity.RESULT_CANCELED -> showSnackbar(
                    "Update canceled by the user.",
                    Snackbar.LENGTH_SHORT
                )
                ActivityResult.RESULT_IN_APP_UPDATE_FAILED -> showSnackbar(
                    "Update failed. Please try again later.",
                    Snackbar.LENGTH_LONG
                )
            }
        }
    }

    /** Menampilkan [Snackbar] dengan aksi opsional. */
    private fun showSnackbar(
        message: CharSequence,
        lengthSnackbar: Int,
        actionText: CharSequence? = null,
        actionTextColor: Int? = null,
        action: View.OnClickListener? = null
    ) {
        Snackbar.make(activity.findViewById(android.R.id.content), message, lengthSnackbar).apply {
            actionText?.let { setAction(it, action) }
            actionTextColor?.let { setActionTextColor(it) }
            show()
        }
    }

    /** Menjadwalkan waktu selesai instalasi dengan timeout otomatis. */
    private fun scheduleUpdateCompletionTimeout() {
        Handler(Looper.getMainLooper()).postDelayed({
            updateComplete()
        }, updateTimeoutMs)
    }

    /** Menampilkan Snackbar jika terjadi error. */
    private fun logErrorAndShowSnackbar(errorMessage: CharSequence) {
        errorMessage.debugMessageDebug()
        showSnackbar(
            "Error: $errorMessage",
            Snackbar.LENGTH_LONG
        )
    }

    companion object {
        const val REQUEST_CODE_IN_APP_UPDATE: Int = 100
    }
}