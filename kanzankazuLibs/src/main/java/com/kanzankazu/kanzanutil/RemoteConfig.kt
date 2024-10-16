@file:Suppress("DEPRECATION")

package com.kanzankazu.kanzanutil

import android.annotation.SuppressLint
import android.app.Activity
import com.afollestad.materialdialogs.DialogCallback
import com.afollestad.materialdialogs.MaterialDialog
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.kanzankazu.R
import com.kanzankazu.kanzanutil.kanzanextension.type.debugMessageDebug
import com.kanzankazu.kanzanutil.kanzanextension.type.toIntOrDefault

/**
implementation 'com.google.firebase:firebase-config-ktx:21.0.0'
implementation 'com.google.firebase:firebase-analytics-ktx:19.0.0'
 */
class RemoteConfig(private var activity: Activity, onFirebaseRemoteConfig: (FirebaseRemoteConfig) -> Unit = {}) {

    private var isMaintenance: Boolean = false
    private var minVer: Int = 0
    private var currVer: Int = 0
    private lateinit var remoteConfig: FirebaseRemoteConfig

    init {
        setupFirebaseRemoteConfig(onFirebaseRemoteConfig)
    }

    @SuppressLint("ResourceType")
    fun setupFirebaseRemoteConfig(
        onFirebaseRemoteConfig: (FirebaseRemoteConfig) -> Unit = {},
    ) {
        try {
            remoteConfig = Firebase.remoteConfig
            val configSettings = remoteConfigSettings { minimumFetchIntervalInSeconds = 3600 }
            remoteConfig.setConfigSettingsAsync(configSettings)
            remoteConfig.setDefaultsAsync(R.xml.remote_config_default)
            onFirebaseRemoteConfig(remoteConfig)
        } catch (e: Exception) {
            e.message.toString().debugMessageDebug()
            e.printStackTrace()
        }
    }

    /**
     * @param T is first activity/splashscreen
     * @param deviceVerCode from BuildConfig.VERSION_CODE
     */
    private inline fun <reified T> validationMaintenanceUpdateRemoteConfig(deviceVerCode: Int, listenerUpdate: ListenerUpdate) {
        remoteConfig.fetchAndActivate().addOnCompleteListener(activity) { task ->
            if (task.isSuccessful) {
                try {
                    isMaintenance = remoteConfig.getBoolean(IS_MAINTENANCE)
                    minVer = remoteConfig.getString(CURRENT_VERSION).toIntOrDefault()
                    currVer = remoteConfig.getString(MIN_VERSION).toIntOrDefault()
                } catch (e: NumberFormatException) {
                    listenerUpdate.onNoUpdate(e.message.toString())
                }
            } else {
                listenerUpdate.onNoUpdate(task.exception?.message.toString())
            }
        }

        "RC VALIDATION : $deviceVerCode $minVer $currVer".debugMessageDebug()
        if (isMaintenance) {
            listenerUpdate.onMaintenance()
        } else if (deviceVerCode < minVer) {
            "RC FORCE : $deviceVerCode < $minVer".debugMessageDebug()
            listenerUpdate.onUpdateForce()
        } else if (deviceVerCode < currVer) {
            if (activity is T) {
                "RC SUGGEST : $deviceVerCode < $currVer".debugMessageDebug()
                listenerUpdate.onUpdateSuggest()
            } else listenerUpdate.onNoUpdate("")
        } else listenerUpdate.onNoUpdate("")
    }

    private fun initMaintenanceDialog(
        maintenanceTitle: String,
        maintenanceDesc: String,
        maintenanceCloseTitle: String? = null,
    ) {
        setupDialogRemoteConfig(
            maintenanceTitle,
            maintenanceDesc,
            maintenanceCloseTitle ?: activity.getString(R.string.label_close_apps),
            positiveAction = { activity.finishAffinity() }
        )
    }

    private fun initForceUpdateDialog(
        forceTitle: String,
        forceDesc: String,
        forceUpdateAction: DialogCallback,
        forceUpdateCloseAction: DialogCallback,
        forceUpdateTitle: String? = null,
        forceUpdateCloseTitle: String? = null,
    ) {
        setupDialogRemoteConfig(
            forceTitle,
            forceDesc,
            forceUpdateTitle ?: activity.getString(R.string.label_update),
            forceUpdateAction,
            forceUpdateCloseTitle ?: activity.getString(R.string.label_close_apps),
            forceUpdateCloseAction,
        )
    }

    private fun initSuggestUpdateDialog(
        suggestTitle: String,
        suggestDesc: String,
        suggestUpdateAction: DialogCallback,
        suggestUpdateCloseAction: DialogCallback,
        suggestUpdateTitle: String? = null,
        suggestUpdateCloseTitle: String? = null,
    ) {
        setupDialogRemoteConfig(
            suggestTitle,
            suggestDesc,
            suggestUpdateTitle ?: activity.getString(R.string.label_update),
            suggestUpdateAction,
            suggestUpdateCloseTitle ?: activity.getString(R.string.label_close_apps),
            suggestUpdateCloseAction,
        )
    }

    @SuppressLint("CheckResult")
    private fun setupDialogRemoteConfig(
        title: String? = null,
        desc: String? = null,
        positiveTitle: String? = null,
        positiveAction: DialogCallback? = null,
        negativeTitle: String? = null,
        negativeAction: DialogCallback? = null,
        neutralTitle: String? = null,
        neutralAction: DialogCallback? = null,
    ) {
        MaterialDialog(activity).show {
            noAutoDismiss()
            cancelOnTouchOutside(false)
            title(text = title ?: activity.getString(R.string.label_maintenance_title))
            message(text = desc ?: activity.getString(R.string.label_maintenance_desc))
            if (!positiveTitle.isNullOrEmpty()) positiveButton(text = positiveTitle, click = positiveAction)
            if (!negativeTitle.isNullOrEmpty()) negativeButton(text = negativeTitle, click = negativeAction)
            if (!neutralTitle.isNullOrEmpty()) neutralButton(text = neutralTitle, click = neutralAction)
        }

    }

    interface ListenerUpdate {
        fun onMaintenance()
        fun onUpdateForce()
        fun onUpdateSuggest()
        fun onNoUpdate(errorMessage: String)
    }

    companion object {
        const val IS_MAINTENANCE = "is_maintenance"
        const val CURRENT_VERSION = "current_version"
        const val MIN_VERSION = "min_version"
    }
}