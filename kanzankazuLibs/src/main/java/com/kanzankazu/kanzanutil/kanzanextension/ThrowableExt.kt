package com.kanzankazu.kanzanutil.kanzanextension

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.kanzankazu.kanzanutil.kanzanextension.type.DebugType
import com.kanzankazu.kanzanutil.kanzanextension.type.debugMessage

fun Throwable.getFullErrorLog(): String {
    return buildString {
        appendLine("❌ Throwable: ${this@getFullErrorLog::class.java.name}")
        appendLine("📄 Message: $message")
        appendLine("📄 Localized: $localizedMessage")
        appendLine("➡️ Cause: $cause")
        appendLine("🔍 StackTrace:\n${stackTraceToString()}")
    }
}

fun Exception.getFullErrorLog(): String {
    return buildString {
        appendLine("❌ Throwable: ${this@getFullErrorLog::class.java.name}")
        appendLine("📄 Message: $message")
        appendLine("📄 Localized: $localizedMessage")
        appendLine("➡️ Cause: $cause")
        appendLine("🔍 StackTrace:\n${stackTraceToString()}")
    }
}

/**
 * Sends crash reports to Firebase Crashlytics.
 *
 * @param userid The user ID associated with the crash.
 * @param isLog If true, logs the message to the Android log.
 * @param isForce If true, disables crashlytics data collection and forces sending of unsent reports.
 *
 * Example:
 * ```
 * try {
 *     // Some code that might throw an exception
 * } catch (e: Exception) {
 *     e.sendCrashlytics(userid = "user123", isLog = true, isForce = false)
 * }
 * ```
 */
fun Throwable.sendCrashlytics(
    userid: String = "",
    isLog: Boolean = false,
    isForce: Boolean = true,
) {
    debugMessage(
        log = this.stackTraceToString(),
        location = "Throwable.sendCrashlytics",
        debugType = if (!isLog) DebugType.ERROR else DebugType.DEBUG
    )

    FirebaseCrashlytics.getInstance().apply {
        if (userid.isNotEmpty()) setUserId(userid)

        setCustomKey("uid", userid)
        setCustomKey("stackTraceToString", this@sendCrashlytics.stackTraceToString())

        if (isLog) log(this@sendCrashlytics.stackTraceToString())

        recordException(this@sendCrashlytics)

        if (isForce) {
            setCrashlyticsCollectionEnabled(false)
            sendUnsentReports()
        }
    }
}