@file:Suppress("DEPRECATION", "UNUSED_VARIABLE")

package com.kanzankazu.kanzanwidget.camera.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.kanzankazu.R
import com.kanzankazu.kanzanutil.BaseConst
import com.kanzankazu.kanzanwidget.camera.ui.CameraModel
import com.kanzankazu.kanzanwidget.camera.ui.CameraProperty
import com.kanzankazu.kanzanwidget.camera.ui.MediaView
import com.kanzankazu.kanzanwidget.camera.util.CameraFetchPath.getFilePathFromUri
import id.zelory.compressor.Compressor
import java.io.File

private val placeholderImage: Int = R.drawable.ic_android

fun Activity.refresh() {
    recreate()
}

inline fun <reified T> Activity.changePageForResult(requestCode: Int, bundle: Bundle?) {
    val intent = Intent(this, T::class.java)
    if (bundle != null) intent.putExtras(bundle)
    startActivityForResult(intent, requestCode)
}

inline fun <reified T> Fragment.changePageForResult(requestCode: Int, bundle: Bundle?) {
    val intent = Intent(requireActivity(), T::class.java)
    if (bundle != null) intent.putExtras(bundle)
    startActivityForResult(intent, requestCode)
}

@SuppressLint("CheckResult")
fun requestOptionStandard(placeholder: Int = placeholderImage): RequestOptions {
    val requestOptions = RequestOptions()
    requestOptions.placeholder(placeholder)
    return requestOptions
}

@SuppressLint("CheckResult")
fun requestOptionStandardNoSaveCache(placeholder: Int = placeholderImage): RequestOptions {
    val requestOptions = RequestOptions()
    requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE)
    requestOptions.skipMemoryCache(true)
    requestOptions.placeholder(placeholder)
    return requestOptions
}

fun Activity.openGallery(mode: Int) {
    if (this is MediaView) {
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = BaseConst.TYPE_IMAGE_ALL

        val picture = CameraModel()
        picture.mode = mode

        startActivityForResult(photoPickerIntent, CameraConst.TAKE_FROM_GALLERY)
    } else
        throw Exception("Should implement MediaView")
}

fun Fragment.openGallery(mode: Int) {
    if (this is MediaView) {
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = BaseConst.TYPE_IMAGE_ALL

        val picture = CameraModel()
        picture.mode = mode

        startActivityForResult(photoPickerIntent, CameraConst.TAKE_FROM_GALLERY)
    } else
        throw Exception("Should implement MediaView")
}

/**
 * @param requestCode
 * @param arrayOfMimeType (BaseConst.TYPE_IMAGE_ALL, BaseConst.TYPE_APPLICATION_PDF)
*/
fun Activity.openGallery2(
    requestCode: Int = CameraConst.TAKE_FROM_GALLERY,
    arrayOfMimeType: Array<String> = arrayOf(BaseConst.TYPE_IMAGE_ALL, BaseConst.TYPE_APPLICATION_PDF),
    fragment: Fragment? = null,
) {
    val intent = Intent(Intent.ACTION_GET_CONTENT)
    intent.addCategory(Intent.CATEGORY_OPENABLE)
    intent.type = "*/*"
    intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOfMimeType)
    val i = Intent.createChooser(intent, "File")
    if (fragment == null) this.startActivityForResult(i, requestCode)
    else fragment.startActivityForResult(i, requestCode)
}

/**
 * @param requestCode
 * @param arrayOfMimeType (BaseConst.TYPE_IMAGE_ALL, BaseConst.TYPE_APPLICATION_PDF)
*/
fun Fragment.openGallery2(
    requestCode: Int = CameraConst.TAKE_FROM_GALLERY,
    arrayOfMimeType: Array<String> = arrayOf(BaseConst.TYPE_IMAGE_ALL, BaseConst.TYPE_APPLICATION_PDF),
) {
    requireActivity().openGallery2(requestCode, arrayOfMimeType, this)
}

fun Context.onCameraActivityResult(
    requestCode: Int,
    resultCode: Int,
    data: Intent?,
    cameraProperty: CameraProperty? = null,
    onGetImage: (String) -> Unit = {},
    onEndResult: (isFromCameraLocal: Boolean) -> Unit = {},
): String {
    val imagePath: String
    val isFromCameraLocal: Boolean
    val pathFile = data?.data?.path
    if (requestCode == CameraConst.TAKE_FROM_GALLERY) {
        imagePath = when {
            pathFile is String && pathFile.isNotEmpty() -> {
                isFromCameraLocal = false
                val uri = data.data
                val url = uri?.let { getFilePathFromUri(this, it) }
                url?.let { onGetImage(it) }
                url ?: ""
            }
            else -> {
                isFromCameraLocal = false
                validationPicUrlIsEmpty(cameraProperty, onGetImage)
            }
        }
    } else {
        val path = data?.extras?.getString(CameraConst.PARAM_PICTURE)
        imagePath = when {
            path is String && path.isNotEmpty() -> {
                isFromCameraLocal = true
                onGetImage(path)
                path
            }
            pathFile is String && pathFile.isNotEmpty() -> {
                isFromCameraLocal = false
                val uri = data.data
                val url = uri?.let { getFilePathFromUri(this, it) }
                url?.let { onGetImage(it) }
                url ?: ""
            }
            else -> {
                isFromCameraLocal = false
                validationPicUrlIsEmpty(cameraProperty, onGetImage)
            }
        }
    }
    onEndResult(isFromCameraLocal)
    return imagePath
}

private fun validationPicUrlIsEmpty(cameraProperty: CameraProperty?, onGetImage: (String) -> Unit) = if (cameraProperty?.pictUrl.isNullOrEmpty()) {
    ""
} else {
    cameraProperty?.pictUrl?.let { onGetImage(it) }
    ""
}

/**
 * @param arrayListOfExtension example arrayListOf("pdf, jpeg, jpg")*/
fun Context.isValidFile(
    filePath: String,
    arrayListOfExtension: ArrayList<String>,
    minLimitInKb: Int = 10,
    maxLimitInKb: Int = 2000,
    errorMessageExtension: String = "",
    errorMessageMinLimit: String = "",
    errorMessageMaxLimit: String = "",
    isFromCamera: Boolean = false,
    onError: (String) -> Unit = {},
): Boolean {
    val img = if (isFromCamera) File(filePath).compressImageFile(this) else File(filePath)
    val fileSizeInBytes: Long = img.length() // Get length of file in bytes
    val fileSizeInKB = fileSizeInBytes / 1024 // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
    val fileSizeInMB = fileSizeInKB / 1024 // Convert the KB to MegaBytes (1 MB = 1024 KBytes)

    val extension = img.extension
    val isExtension =
        if (extension.isEmpty()) false
        else {
            //val arrayListOf = arrayListOf("pdf")
            arrayListOfExtension.contains(extension)
        }

    return when {
        !isExtension -> {
            onError(errorMessageExtension)
            false
        }

        fileSizeInKB < minLimitInKb -> {
            onError(errorMessageMinLimit)
            false
        }

        fileSizeInKB > maxLimitInKb -> {
            onError(errorMessageMaxLimit)
            false
        }

        else -> true
    }
}

fun File.compressImageFile(context: Context): File {
    return Compressor(context)
        .setQuality(90)
        .setMaxWidth(640)
        .setMaxHeight(480)
        .setCompressFormat(Bitmap.CompressFormat.JPEG)
        .compressToFile(this)
}

fun String.isUrl(): Boolean {
    return contains("http") || contains("https")
}

fun String.isPDF(): Boolean {
    val img = File(this)
    val extension = img.extension

    return if (extension.isEmpty()) true
    else {
        val arrayListOf = arrayListOf("pdf")
        arrayListOf.contains(extension)
    }
}
