@file:Suppress("MemberVisibilityCanBePrivate", "UNUSED_PARAMETER")

package com.kanzankazu.kanzanutil

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.ActivityResult
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.kanzankazu.R
import com.kanzankazu.kanzanutil.kanzanextension.type.debugMessageDebug
import com.kanzankazu.kanzanutil.kanzanextension.type.lazyNone

/**
implementation("com.google.android.play:core:1.10.0")
implementation("com.google.android.play:core-ktx:1.8.1")
 */
class InAppUpdate(val activity: Activity) {

    var appUpdateType: Int = AppUpdateType.FLEXIBLE
    val appUpdateManager: AppUpdateManager by lazyNone { AppUpdateManagerFactory.create(activity) }

    init {
        setupInAppUpdate()
    }

    fun setupInAppUpdate(appUpdateType: Int = AppUpdateType.FLEXIBLE): AppUpdateManager {
        this.appUpdateType = appUpdateType
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        val installStateUpdatedListener = InstallStateUpdatedListener { state ->
            when {
                state.installStatus() == InstallStatus.CANCELED -> {
                    "setupInAppUpdate InstallStateUpdatedListener ${"state.installStatus() == InstallStatus.CANCELED"}".debugMessageDebug()
                }

                state.installStatus() == InstallStatus.DOWNLOADED -> {
                    "setupInAppUpdate InstallStateUpdatedListener ${"state.installStatus() == InstallStatus.DOWNLOADED"}".debugMessageDebug()
                    updateCompleteSnackbarAction()
                }

                state.installStatus() == InstallStatus.DOWNLOADING -> {
                    val bytesDownloaded = state.bytesDownloaded()
                    val totalBytesToDownload = state.totalBytesToDownload()
                    "setupInAppUpdate InstallStateUpdatedListener ${"state.installStatus() == InstallStatus.DOWNLOADING $bytesDownloaded $totalBytesToDownload"}".debugMessageDebug()
                }

                state.installStatus() == InstallStatus.FAILED -> {
                    "setupInAppUpdate InstallStateUpdatedListener ${"state.installStatus() == InstallStatus.FAILED"}".debugMessageDebug()
                }

                state.installStatus() == InstallStatus.INSTALLED -> {
                    "setupInAppUpdate InstallStateUpdatedListener ${"state.installStatus() == InstallStatus.INSTALLED"}".debugMessageDebug()
                }

                state.installStatus() == InstallStatus.INSTALLING -> {
                    "setupInAppUpdate InstallStateUpdatedListener ${"state.installStatus() == InstallStatus.INSTALLING"}".debugMessageDebug()
                }

                state.installStatus() == InstallStatus.PENDING -> {
                    "setupInAppUpdate InstallStateUpdatedListener ${"state.installStatus() == InstallStatus.PENDING"}".debugMessageDebug()
                }

                state.installStatus() == InstallStatus.UNKNOWN -> {
                    "setupInAppUpdate InstallStateUpdatedListener ${"state.installStatus() == InstallStatus.UNKNOWN"}".debugMessageDebug()
                }
            }
        }

        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            "setupInAppUpdate $appUpdateInfo".debugMessageDebug()
            when (appUpdateInfo.installStatus()) {
                InstallStatus.DOWNLOADED -> {
                    "setupInAppUpdate ${"InstallStatus.DOWNLOADED"}".debugMessageDebug()
                    updateCompleteSnackbarAction()
                }

                else -> {
                    if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                        && appUpdateInfo.isUpdateTypeAllowed(appUpdateType)
                    // && (appUpdateInfo.clientVersionStalenessDays() ?: -1) >= DAYS_FOR_FLEXIBLE_UPDATE
                    // && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
                    ) {
                        appUpdateManager.registerListener(installStateUpdatedListener)
                        updateStart(appUpdateInfo)
                    } else {
                        appUpdateManager.unregisterListener(installStateUpdatedListener)
                    }
                }
            }
        }
        return appUpdateManager
    }

    fun updateStart(appUpdateInfo: AppUpdateInfo) {
        appUpdateManager.startUpdateFlowForResult(appUpdateInfo, appUpdateType, activity, REQUEST_CODE_IN_APP_UPDATE)
    }

    fun updateComplete() {
        appUpdateManager.completeUpdate()
    }

    fun updateCompleteSnackbarAction(
        message: CharSequence? = null,
        lengthSnackbar: Int? = null,
        actionText: CharSequence? = null,
        actionTextColor: Int? = null,
        action: View.OnClickListener? = null,
    ) {
        Snackbar.make(activity.findViewById(android.R.id.content), message ?: "An update has just been downloaded.", lengthSnackbar ?: Snackbar.LENGTH_INDEFINITE).apply {
            setAction(actionText ?: "RESTART", action ?: View.OnClickListener { updateComplete() })
            setActionTextColor(actionTextColor ?: ContextCompat.getColor(activity, R.color.baseWhite))
            show()
        }
    }

    fun validateUpdateType(onUpdateFlexible: (Boolean) -> Unit) {
        if (appUpdateType == AppUpdateType.FLEXIBLE) onUpdateFlexible(true)
        else onUpdateFlexible(false)
    }

    fun validateOnActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE_IN_APP_UPDATE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    "onActivityResult validateOnActivityResult ${"Ok"}".debugMessageDebug()
                }

                Activity.RESULT_CANCELED -> {
                    "onActivityResult validateOnActivityResult ${"Cancel"}".debugMessageDebug()
                }

                ActivityResult.RESULT_IN_APP_UPDATE_FAILED -> {
                    "onActivityResult validateOnActivityResult ${"Update Failed"}".debugMessageDebug()
                }
            }
        }
    }

    companion object {
        const val REQUEST_CODE_IN_APP_UPDATE: Int = 100
    }
}