@file:Suppress("DEPRECATION")

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

    suspend fun getFirebaseRemoteConfig(
        onFirebaseRemoteConfig: (FirebaseRemoteConfig) -> Unit = {},
        onFailed: (errorMessage: String) -> Unit = {},
    ) {
        withContext(Dispatchers.IO){
            try {
                val configSettings = FirebaseRemoteConfigSettings.Builder()
                    .setMinimumFetchIntervalInSeconds(10)
                    .build()

                firebaseRemoteConfig.setConfigSettingsAsync(configSettings)
                firebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_default)

                firebaseRemoteConfig.fetchAndActivate().addOnCompleteListener { task -> handleTaskResult(task, onFirebaseRemoteConfig, onFailed) }
            } catch (e: Exception) {
                handleException(e, "initializeRemoteConfig", onFailed)
            }
        }
    }

    private fun handleTaskResult(task: Task<Boolean>, onFirebaseRemoteConfig: (FirebaseRemoteConfig) -> Unit, onFailed: (errorMessage: String) -> Unit) {
        if (task.isSuccessful) {
            try {
                onFirebaseRemoteConfig.invoke(firebaseRemoteConfig)
            } catch (e: NumberFormatException) {
                val message = "Failed to parse configuration: ${e.message}"
                logError(message, e)
                onFailed.invoke(message)
            }
        } else {
            val message = task.exception?.message ?: "Unknown error occurred during fetch"
            logError("Fetch not successful: $message", task.exception)
            onFailed.invoke(message)
        }
    }

    private fun handleException(e: Exception, methodName: String, onFailed: (errorMessage: String) -> Unit) {
        val message = "$methodName - Error occurred: ${e.message}"
        logError(message, e)
        onFailed.invoke(e.message.orEmpty())
    }

    private fun logError(message: String, throwable: Throwable?) {
        val errorDetails = "RemoteConfig - $message >> ${throwable?.stackTraceToString()}"
        errorDetails.debugMessageError()
    }
}