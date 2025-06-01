package com.kanzankazu.kanzanutil.kanzanextension

import com.google.firebase.crashlytics.FirebaseCrashlytics

fun Throwable.getFullErrorLog(): String {
    return buildString {
        appendLine("❌ Throwable: ${this@getFullErrorLog::class.java.name}\n")
        appendLine("📄 Message: $message\n")
        appendLine("📄 Localized: $localizedMessage\n")
        appendLine("➡️ Cause: $cause\n")
        appendLine("🔍 StackTrace:\n${stackTraceToString()}\n")
    }
}

fun Exception.getFullErrorLog() = buildString {
    appendLine("❌ Throwable: ${this@getFullErrorLog::class.java.name}\n")
    appendLine("📄 Message: $message\n")
    appendLine("📄 Localized: $localizedMessage\n")
    appendLine("➡️ Cause: $cause\n")
    appendLine("🔍 StackTrace:\n${stackTraceToString()}\n")
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