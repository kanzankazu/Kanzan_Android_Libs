@file:Suppress("DEPRECATION")

package com.kanzankazu.kanzanutil.kanzanextension

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import kotlin.system.exitProcess


fun Activity.openDeviceSetting(applicationId: String) {
    startActivity(
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse("package:$applicationId")
        )
    )
}

fun Activity.openLocationModeSetting() {
    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
    startActivity(intent)
}

fun Activity.openAppSetting() {
    val intent = Intent()
    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
    val uri = Uri.fromParts(
        "package",
        packageName,
        null
    )
    intent.data = uri
    startActivity(intent)
}

fun Activity.openDeveloperOptionSetting() {
    startActivity(Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS))
}

fun Context.isAppRunning(packageName: String): Boolean {
    val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
    val procInfos: List<ActivityManager.RunningAppProcessInfo> = activityManager.runningAppProcesses
    for (proccessInfo in procInfos) {
        if (proccessInfo.processName == packageName)
            return true
    }
    return false
}

/**needs add manifest <uses-permission android:name="android.permission.GET_TASKS" />*/
fun Activity.isForeground(myPackage: String): Boolean {
    val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager?
    val runningTaskInfo = manager?.getRunningTasks(1)
    val componentInfo = runningTaskInfo?.get(0)?.topActivity
    return componentInfo?.packageName == myPackage
}

fun Activity.appInstalledOrNot(packageName: String): Boolean {
    val pm = packageManager
    val appInstalled: Boolean = try {
        pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }
    return appInstalled
}

fun exitApp() {
    exitProcess(0)
}

fun Activity.isGpsEnabled(): Boolean {
    val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
}

@SuppressLint("ObsoleteSdkInt")
fun isKitkatBelow() = Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT

fun isLolipopAbove() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP

fun Activity.restartApp() {
    baseContext.packageManager.getLaunchIntentForPackage(baseContext.packageName)?.apply {
        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(this)
    }
}
