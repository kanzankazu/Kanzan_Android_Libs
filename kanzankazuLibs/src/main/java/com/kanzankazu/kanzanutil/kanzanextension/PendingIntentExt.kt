package com.kanzankazu.kanzanutil.kanzanextension

import android.app.PendingIntent
import android.content.Context
import android.content.Intent

inline fun <reified T> Context?.makePendingIntent(): PendingIntent {
    val intent = Intent(this, T::class.java)
    return PendingIntent.getActivity(this, 0, intent, 0)
}

fun Context.makePendingIntentActivity(intent: Intent, requestCode: Int = 0, flag: Int = 0) =
    PendingIntent.getActivity(this, requestCode, intent, flag)
