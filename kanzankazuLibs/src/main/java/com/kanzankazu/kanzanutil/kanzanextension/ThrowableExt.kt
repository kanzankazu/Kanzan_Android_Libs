package com.kanzankazu.kanzanutil.kanzanextension

import com.google.firebase.crashlytics.FirebaseCrashlytics

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

fun Throwable.sendCrashlytics(
    userid: String = "",
    tag: String = "",
    logMessage: String = "",
    isForce: Boolean = true,
) {
    FirebaseCrashlytics.getInstance().apply {
        if (userid.isNotEmpty()) {
            setUserId(userid)
            setCustomKey("uid", userid)
        }

        if (logMessage.isNotEmpty()) log("$tag - $logMessage")
        recordException(this@sendCrashlytics)

        if (isForce) {
            setCrashlyticsCollectionEnabled(false)
            sendUnsentReports()
        }
    }
}