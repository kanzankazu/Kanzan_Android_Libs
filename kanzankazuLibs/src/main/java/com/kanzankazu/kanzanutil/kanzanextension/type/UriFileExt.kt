@file:Suppress("DEPRECATION")

package com.kanzankazu.kanzanutil.kanzanextension.type

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.ContextCompat
import java.io.File
import java.util.Locale

fun String.isUri(): Boolean {
    return contains("file:")
}

fun String.isFileExists(context: Context): Boolean {
    return File(context.getRootFileDirPath(), this).exists()
}

fun String.deleteFileByPath(): Boolean {
    val file = File(this)
    return if (file.exists()) {
        file.delete()
    } else {
        false
    }
}

fun getProgressDisplayLine(currentBytes: Long, totalBytes: Long): String {
    return currentBytes.convertBytesToMegaByte() + "/" + totalBytes.convertBytesToMegaByte()
}

private fun Long.convertBytesToMegaByte(): String {
    return String.format(Locale.ENGLISH, "%.2fMb", this / (1024.00 * 1024.00))
}

fun String.getFileName(): String {
    return substring(lastIndexOf("/") + 1, length)
}

fun String.toPathUri(): Uri {
    if (isUri()) println(replace("\\bfile//.*?\\b".toRegex(), ""))
    return Uri.fromFile(File(this))
}


fun File.toPathString(): String = absolutePath

@SuppressLint("Recycle")
fun Uri?.getRealPath(context: Context): String {
    /*return when (val cursor: Cursor? = context.contentResolver.query(this, null, null, null, null)) {
        null -> path ?: ""
        else -> {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            cursor.getString(idx) ?: ""
        }
    }*/

    if (this == null) return ""
    val projection = arrayOf(MediaStore.Images.Media.DATA)
    val cursor = context.contentResolver.query(this, projection, null, null, null)
    if (cursor != null) {
        val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(columnIndex) ?: ""
    }
    return this.path ?: ""
}

fun Context.getRootFileDirPath(): String {
    return if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
        val file = ContextCompat.getExternalFilesDirs(applicationContext, null)[0]
        file.absolutePath
    } else {
        applicationContext.filesDir.absolutePath
    }
}

fun Context.getPathFromUri(uri: Uri): String {
    return when (uri.scheme) {
        "file" -> uri.path ?: ""
        "content" -> getPathFromContentUri(uri)
        else -> ""
    }
}

private fun Context.getPathFromContentUri(contentUri: Uri): String {
    var cursor: Cursor? = null
    try {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        cursor = contentResolver.query(contentUri, projection, null, null, null)
        if (cursor != null && cursor.moveToFirst()) {
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            return cursor.getString(columnIndex) ?: ""
        }
    } finally {
        cursor?.close()
    }
    return ""
}
