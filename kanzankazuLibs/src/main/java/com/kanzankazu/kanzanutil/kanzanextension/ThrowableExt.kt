package com.kanzankazu.kanzanutil.kanzanextension

import com.google.firebase.crashlytics.FirebaseCrashlytics

fun Throwable.sendCrashlytics(
    userid: String = "",
    isLog: Boolean = false,
) {
    FirebaseCrashlytics.getInstance().apply {

        if (userid.isNotEmpty()) setUserId(userid)
        if (userid.isNotEmpty()) setCustomKey("userId", userid)

        if (isLog) log(this@sendCrashlytics.stackTraceToString())
        else recordException(this@sendCrashlytics)

        sendUnsentReports()
    }
}