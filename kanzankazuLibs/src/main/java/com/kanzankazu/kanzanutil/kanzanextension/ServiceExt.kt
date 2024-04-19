@file:Suppress("DEPRECATION")

package com.kanzankazu.kanzanutil.kanzanextension

import android.app.Activity
import android.app.ActivityManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

inline fun <reified T> Activity.startMyServices() {
    val intent = Intent(this, T::class.java)
    if (Build.VERSION.SDK_INT == Build.VERSION_CODES.O) startForegroundService(intent)
    else startService(intent)
}

inline fun <reified T> Activity.stopMyServices() {
    stopService(Intent(this, T::class.java))
}

inline fun <reified T> Activity.isMyServicesRunning(): Boolean {
    val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    for (service in manager.getRunningServices(Int.MAX_VALUE)) {
        if ((T::class.java).name == service.service.className) {
            Log.d("Lihat KanzanKazu", "isMyServicesRunning service is running ${T::class.java}")
            return true
        }
    }
    Log.d("Lihat KanzanKazu", "isMyServicesRunning service is not running ${T::class.java}")
    return false
}

fun Service.stop() {
    stopForeground(true)
    stopSelf()
}