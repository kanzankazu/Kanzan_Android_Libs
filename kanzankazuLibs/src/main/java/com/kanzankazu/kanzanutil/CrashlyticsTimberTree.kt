@file:Suppress("DEPRECATION")

package com.kanzankazu.kanzanutil

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.chuckerteam.chucker.api.ChuckerCollector
import com.kanzankazu.kanzanutil.kanzanextension.sendCrashlytics
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
    private val context: Context,
    private val isDebug: Boolean,
    private val minLogPriority: Int = Log.WARN,
) : Timber.Tree() {

    private val chuckerCollector: ChuckerCollector by lazy { ChuckerCollector(context) }

    companion object {
        // Default log tag used when no tag is provided
        private const val DEFAULT_TAG = "CrashlyticsTimberTree"
    }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        val finalTag = tag ?: DEFAULT_TAG
        if (isDebug) {
            logToLogcat(priority, finalTag, message, t)
            if (priority >= minLogPriority) chuckerCollector.onError(finalTag, t ?: Throwable(message))
        }
        if (priority < minLogPriority) return
        logToCrashlytics(finalTag, message, t)
    }

    @SuppressLint("LogNotTimber")
    private fun logToLogcat(priority: Int, tag: String, message: String, throwable: Throwable?) {
        Log.println(priority, tag, message)
        throwable?.let { Log.e(tag, "Error: ", it) }
    }

    private fun logToCrashlytics(tag: String, message: String, throwable: Throwable?) {
        throwable?.sendCrashlytics(
            tag = tag,
            logMessage = message
        )
    }
}