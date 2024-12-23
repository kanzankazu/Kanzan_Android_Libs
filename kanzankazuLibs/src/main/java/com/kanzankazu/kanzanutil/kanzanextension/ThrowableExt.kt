package com.kanzankazu.kanzanutil.kanzanextension

import com.google.firebase.crashlytics.FirebaseCrashlytics

fun Throwable.sendCrashlytics(
    userid: String = "",
    userName: String = "",
    userEmail: String = "",
) {
    FirebaseCrashlytics.getInstance().apply {

        if (userid.isNotEmpty()) {
            setUserId(userid)
            setCustomKey("userId", userid)
        }
        if (userName.isNotEmpty()) setCustomKey("userName", userName)
        if (userEmail.isNotEmpty()) setCustomKey("userEmail", userEmail)
        recordException(this@sendCrashlytics)
        sendUnsentReports()
    }
}