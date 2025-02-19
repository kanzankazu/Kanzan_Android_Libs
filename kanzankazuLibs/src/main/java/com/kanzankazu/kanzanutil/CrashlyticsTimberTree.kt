package com.kanzankazu.kanzanutil

import android.annotation.SuppressLint
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber
import javax.inject.Inject

/**
 * Custom Timber Tree to send logs to Crashlytics.
 *
 * @param isDebug if true, logs will be displayed in the console.
 * @param minLogPriority determines the minimum log priority level to be processed.
 *
 * @author Kanzankazu
 */
class CrashlyticsTimberTree @Inject constructor(
    private val isDebug: Boolean,
    private val minLogPriority: Int = Log.INFO,
) : Timber.Tree() {

    @SuppressLint("LogNotTimber")
    override fun log(priority: Int, tag: String?, message: String, throwable: Throwable?) {
        if (priority < minLogPriority) return

        val formattedTag = tag ?: "CrashlyticsTimberTree"

        if (isDebug) {
            Log.println(priority, formattedTag, message)
        }

        FirebaseCrashlytics.getInstance().apply {
            log("$formattedTag: $message")
            throwable?.let { recordException(it) }
        }
    }
}