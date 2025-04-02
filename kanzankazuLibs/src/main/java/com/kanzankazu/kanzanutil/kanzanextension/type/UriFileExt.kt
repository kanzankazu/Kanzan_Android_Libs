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

/**
 * Checks if the given String contains the substring "file:", indicating a potential URI format.
 *
 * @return `true` if the String contains "file:" (case-insensitive), otherwise `false`.
 *
 * Example:
 * ```kotlin
 * val filePath = "file:/example/path/to/resource"
 * val isUri = filePath.isUri() // true
 *
 * val randomString = "This is a plain string"
 * val isUri = randomString.isUri() // false
 * ```
 */
fun String.isUri(): Boolean {
    return contains("file:")
}

/**
 * Checks if a file with the provided name exists in the application's root directory.
 *
 * @param context The context of the application, used to determine the root directory path.
 * @return `true` if the file exists in the root directory, `false` otherwise.
 *
 * Example:
 * ```kotlin
 * val fileName = "example.txt"
 * val doesExist = fileName.isFileExists(context)
 * ```
 */
fun String.isFileExists(context: Context): Boolean {
    return File(context.getRootFileDirPath(), this).exists()
}

/**
 * Deletes a file at the specified path if it exists.
 * If the file does not exist, the method will return `false`.
 *
 * @receiver The file path as a `String`, representing the absolute or relative path of the file to delete.
 * @return `true` if the file exists and is successfully deleted, `false` otherwise.
 *
 * Example:
 * ```kotlin
 * val filePath = "/path/to/file.txt"
 * val isDeleted = filePath.deleteFileByPath() // Returns true if the file is deleted, false otherwise
 * ```
 */
fun String.deleteFileByPath(): Boolean {
    val file = File(this)
    return if (file.exists()) {
        file.delete()
    } else {
        false
    }
}

/**
 * Generates a progress display string by converting given byte values to their equivalent in megabytes (Mb)
 * and formatting them as "currentBytes/totalBytes" in megabytes.
 *
 * @param currentBytes The current progress in bytes. Must be a non-negative Long value.
 * @param totalBytes The total size in bytes to be completed. Must be a non-negative Long value.
 * @return A formatted string representing the progress, where both currentBytes and totalBytes are converted to megabytes.
 *
 * Example:
 * ```kotlin
 * val progress = getProgressDisplayLine(1048576, 2097152) // "1.00Mb/2.00Mb"
 * ```
 */
fun getProgressDisplayLine(currentBytes: Long, totalBytes: Long): String {
    return currentBytes.convertBytesToMegaByte() + "/" + totalBytes.convertBytesToMegaByte()
}

/**
 * Converts a value in bytes to a formatted string representing its size in megabytes (MB)
 * with two decimal places, followed by the "Mb" unit.
 *
 * @return A formatted string in the format "{value}Mb". For instance, if the `Long` value is `10485760`,
 * the method returns "10.00Mb".
 *
 * Example:
 * ```kotlin
 * val fileSizeInBytes: Long = 10485760 // 10 MB
 * val formattedSize = fileSizeInBytes.convertBytesToMegaByte() // "10.00Mb"
 * ```
 */
private fun Long.convertBytesToMegaByte(): String {
    return String.format(Locale.ENGLISH, "%.2fMb", this / (1024.00 * 1024.00))
}

/**
 * Extracts the file name from a file path represented as a String.
 * The method finds the portion of the string after the last "/" character.
 *
 * @return The extracted file name from the string. If the string does not contain "/",
 *         the entire string is returned.
 *
 * Example:
 * ```kotlin
 * val filePath = "/usr/local/bin/example.txt"
 * val fileName = filePath.getFileName() // "example.txt"
 *
 * val simplePath = "example.txt"
 * val fileName = simplePath.getFileName() // "example.txt"
 * ```
 */
fun String.getFileName(): String {
    return substring(lastIndexOf("/") + 1, length)
}

/**
 * Converts a file path represented as a String into a valid [Uri] object.
 * If the input String is already a valid URI and contains "file:", the method removes the unnecessary parts of the path
 * before converting it to a [Uri].
 *
 * @receiver The file path as a String. The path must point to an existing file for proper conversion.
 * @return A [Uri] object representing the file path.
 *
 * Example:
 * ```kotlin
 * val filePath = "/path/to/your/file.txt"
 * val uri = filePath.toPathUri()
 * // uri: "file:///path/to/your/file.txt"
 * ```
 */
fun String.toPathUri(): Uri {
    if (isUri()) println(replace("\\bfile//.*?\\b".toRegex(), ""))
    return Uri.fromFile(File(this))
}


/**
 * Converts the File object into its absolute path string representation.
 *
 * @return A string representing the absolute path of the File.
 *
 * Example:
 * ```kotlin
 * val file = File("/home/user/document.txt")
 * val pathString = file.toPathString() // "/home/user/document.txt"
 * ```
 */
fun File.toPathString(): String = absolutePath

/**
 * Retrieves the real file system path of a given `Uri` in the Android context.
 * This method queries the content resolver to extract the actual file path associated with the `Uri`.
 * Returns an empty string if the `Uri` is null or the path cannot be resolved.
 *
 * @param context The `Context` in which this method is being invoked. Used to access the content resolver.
 * @return The real path of the file as a String if available, or an empty string if the path cannot be resolved.
 *
 * Example:
 * ```kotlin
 * val uri: Uri? = ... // Some file URI
 * val realPath = uri.getRealPath(context)
 * ```
 */
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

/**
 * Retrieves the root directory path for file storage based on the current external storage state.
 * If external storage is available and mounted, it returns the path to the external file directory.
 * Otherwise, it defaults to the internal app file directory.
 *
 * @return A string containing the absolute path to the root directory, either on external storage
 * or internal storage, depending on the availability of external storage.
 *
 * Example:
 * ```kotlin
 * val rootPath = context.getRootFileDirPath()
 * println(rootPath) // Outputs: "/storage/emulated/0/Android/data/com.example.app/files"
 * ```
 */
fun Context.getRootFileDirPath(): String {
    return if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
        val file = ContextCompat.getExternalFilesDirs(applicationContext, null)[0]
        file.absolutePath
    } else {
        applicationContext.filesDir.absolutePath
    }
}

/**
 * Retrieves the file path associated with a given URI. If the URI's scheme is "file",
 * it directly returns the path. If the scheme is "content", it fetches the file path using
 * a content resolver. For other schemes, it returns an empty string.
 *
 * @param uri The URI from which the file path needs to be retrieved. Should be of type `Uri`.
 * @return The file path as a String. Returns an empty string if the URI scheme is neither "file" nor "content",
 * or if the path cannot be determined.
 *
 * Example usage:
 * ```kotlin
 * val fileUri: Uri = Uri.parse("file:///storage/emulated/0/Download/sample.txt")
 * val filePath = context.getPathFromUri(fileUri) // "/storage/emulated/0/Download/sample.txt"
 * ```
 */
fun Context.getPathFromUri(uri: Uri): String {
    return when (uri.scheme) {
        "file" -> uri.path ?: ""
        "content" -> getPathFromContentUri(uri)
        else -> ""
    }
}

/**
 * Retrieves the physical file path from a content URI. This is typically used to resolve the actual file path
 * of an image or other media content stored on the device.
 *
 * @param contentUri The content URI for the media file whose path needs to be determined.
 * @return A String representing the file path associated with the given content URI, or an empty string
 *         if the path could not be resolved.
 *
 * Example:
 * ```kotlin
 * val contentUri = Uri.parse("content://media/external/images/media/12345")
 * val filePath = context.getPathFromContentUri(contentUri)
 * println(filePath) // Outputs: /storage/emulated/0/DCIM/Camera/image.jpg
 * ```
 */
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
