package com.kanzankazu.kanzanutil

import com.google.android.gms.tasks.Task
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.kanzankazu.R
import com.kanzankazu.kanzanutil.kanzanextension.type.debugMessageError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Manages remote configuration using Firebase Remote Config. This class handles fetching, activating,
 * and applying configuration values from Firebase, with error handling and logging for any issues encountered.
 */
class RemoteConfig {
    private val firebaseRemoteConfig: FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

    fun getFirebaseRemoteConfig(
        onFirebaseRemoteConfig: (FirebaseRemoteConfig) -> Unit = {},
        onFailed: (errorMessage: String) -> Unit = {},
    ) {
        try {
            val configSettings = FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(10)
                .build()

            firebaseRemoteConfig.setConfigSettingsAsync(configSettings)
            firebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_default)

            firebaseRemoteConfig.fetchAndActivate().addOnCompleteListener { task -> handleTaskResult(task, onFirebaseRemoteConfig, onFailed) }
        } catch (e: Exception) {
            e.debugMessageError("RemoteConfig - getFirebaseRemoteConfig")
            handleException(e, onFailed)
        }
    }

    private fun handleTaskResult(task: Task<Boolean>, onFirebaseRemoteConfig: (FirebaseRemoteConfig) -> Unit, onFailed: (errorMessage: String) -> Unit) {
        if (task.isSuccessful) {
            try {
                onFirebaseRemoteConfig.invoke(firebaseRemoteConfig)
            } catch (e: NumberFormatException) {
                val message = "Failed to parse configuration: ${e.message}"
                logError(e)
                onFailed.invoke(message)
            }
        } else {
            val message = task.exception?.message ?: "Unknown error occurred during fetch"
            logError(task.exception)
            onFailed.invoke(message)
        }
    }

    private fun handleException(e: Exception, onFailed: (errorMessage: String) -> Unit) {
        logError(e)
        onFailed.invoke(e.message.orEmpty())
    }

    private fun logError(throwable: Throwable?) {
        throwable.debugMessageError("RemoteConfig - logError")
    }
}