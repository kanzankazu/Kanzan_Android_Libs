@file:Suppress("DEPRECATION")

package com.kanzankazu.kanzanwidget.camera.util

import android.content.Context
import android.content.pm.PackageManager
import android.os.Environment
import java.io.File

object CameraUtils {
    fun getPictureStorageDir(context: Context): String {
        val packageManager = context.packageManager
        val packageName = context.packageName
        try {
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            val dirPath = packageInfo.applicationInfo.dataDir + "/picture"
            val dirFile = File(dirPath)

            if (!dirFile.exists())
                dirFile.mkdirs()
            return dirPath
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return Environment.getExternalStorageDirectory().absolutePath + "/PBL/picture"
    }

    fun getPictureFilename(): String {
        return System.currentTimeMillis().toString()
    }
}