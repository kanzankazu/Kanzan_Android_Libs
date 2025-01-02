package com.kanzankazu.kanzanutil.kanzanextension

import com.google.firebase.crashlytics.FirebaseCrashlytics

/**
 * Mengirimkan log error ke crashlytics, jika isLog true maka akan mengirimkan log error
 * sebagai log, jika false maka akan mengirimkan log error sebagai crash.
 *
 * @param userid id user yang mengalami error, jika kosong maka tidak akan dikirimkan.
 * @param isLog jika true maka akan mengirimkan log error sebagai log, jika false maka akan
 * mengirimkan log error sebagai crash.
 */
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