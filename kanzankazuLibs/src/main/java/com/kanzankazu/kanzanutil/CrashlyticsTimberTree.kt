package com.kanzankazu.kanzanutil

import android.util.Log
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
    private val isDebug: Boolean,
    private val minLogPriority: Int = Log.WARN,
) : Timber.Tree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        val newThrowable = t ?: Throwable(message)
        if (priority < minLogPriority) newThrowable.sendCrashlytics(userid = "", isLog = false, isForce = true)
    }
}