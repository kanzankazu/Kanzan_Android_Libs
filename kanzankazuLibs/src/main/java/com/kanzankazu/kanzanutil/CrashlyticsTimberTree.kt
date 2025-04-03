package com.kanzankazu.kanzanutil

import android.annotation.SuppressLint
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber
import javax.inject.Inject

/**
 * A logging class for directing log messages to Logcat and Firebase Crashlytics,
 * based on a specified minimum log priority.
 *
 * @param isDebug Determines if the logs should be displayed in Logcat. Typically set to `true` in debug builds.
 * @param minLogPriority The minimum log priority that will be handled by this logging tree. Defaults to Log.INFO.
 */
class CrashlyticsTimberTree @Inject constructor(
    private val isDebug: Boolean,
    private val minLogPriority: Int = Log.INFO,
) : Timber.Tree() {

    companion object {
        // Default log tag used when no tag is provided
        private const val DEFAULT_TAG = "CrashlyticsTimberTree"
    }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        // Only log messages that meet the minimum priority requirement
        if (priority < minLogPriority) return

        val finalTag = tag ?: DEFAULT_TAG

        if (isDebug) {
            // In debug mode, log to Logcat
            logToLogcat(priority, finalTag, message, t)
        }

        // Log messages and exceptions to Firebase Crashlytics
        logToCrashlytics(finalTag, message, t)
    }

    @SuppressLint("LogNotTimber")
    private fun logToLogcat(priority: Int, tag: String, message: String, throwable: Throwable?) {
        // Log message to Logcat
        Log.println(priority, tag, message)
        // Log throwable if present
        throwable?.let { Log.e(tag, "Error: ", it) }
    }

    private fun logToCrashlytics(tag: String, message: String, throwable: Throwable?) {
        // Send log messages and exceptions to Firebase Crashlytics
        FirebaseCrashlytics.getInstance().run {
            log("$tag: $message")
            throwable?.let { recordException(it) }
        }
    }
}