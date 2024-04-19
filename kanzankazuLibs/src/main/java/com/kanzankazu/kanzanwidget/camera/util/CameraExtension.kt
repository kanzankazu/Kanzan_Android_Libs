@file:Suppress("DEPRECATION", "UNUSED_VARIABLE")

package com.kanzankazu.kanzanwidget.camera.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.kanzankazu.R
import com.kanzankazu.kanzanwidget.camera.ui.CameraActivity
import com.kanzankazu.kanzanwidget.camera.ui.CameraModel
import com.kanzankazu.kanzanwidget.camera.ui.CameraProperty
import com.kanzankazu.kanzanwidget.camera.ui.MediaDialog
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

fun Activity.takePicture(requestCode: Int, showGuideline: Boolean = true) {
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), 2
        )
    } else {
        val dialog = MediaDialog(this)
        dialog.setOnCameraClicked { this.openCamera(requestCode, showGuideline) }
        dialog.setOnGalleryClicked { this.openGallery(requestCode) }
        //dialog.setOnGalleryClicked { this.openCamera(CameraConst.GALERY_ACTIVITY, showGuideline) }
        dialog.show()
    }
}

fun Fragment.takePicture(requestCode: Int, showGuideline: Boolean = true) {
    if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(
            requireActivity(), arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), 2
        )
    } else {
        val dialog = MediaDialog(requireActivity())
        dialog.setOnCameraClicked { this.openCamera(requestCode, showGuideline) }
        dialog.setOnGalleryClicked { this.openGallery(requestCode) }
        dialog.show()
    }
}

/**
 * @param requestCode default CameraConst.TAKE_PICTURE
 */
fun Activity.openCamera(requestCode: Int = CameraConst.TAKE_FROM_CAMERA, showGuideline: Boolean = false) {
    val bundle = Bundle()
    bundle.putInt(CameraConst.PARAM_CAMERA_MODE, requestCode)
    bundle.putBoolean(CameraConst.PARAM_CAMERA_SHOW_GUIDELINE, showGuideline)
    changePageForResult<CameraActivity>(requestCode, bundle)
}

/**
 * @param requestCode default CameraConst.TAKE_PICTURE
 */
fun Fragment.openCamera(requestCode: Int = CameraConst.TAKE_FROM_CAMERA, showGuideline: Boolean = false) {
    //fun androidx.fragment.app.Fragment.openCamera(mode: Int, showGuideline: Boolean = false) {
    val bundle = Bundle()
    bundle.putInt(CameraConst.PARAM_CAMERA_MODE, requestCode)
    bundle.putBoolean(CameraConst.PARAM_CAMERA_SHOW_GUIDELINE, showGuideline)
    changePageForResult<CameraActivity>(requestCode, bundle)
}

fun Activity.openGallery(mode: Int) {
    if (this is MediaView) {
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"

        val picture = CameraModel()
        picture.mode = mode

        startActivityForResult(photoPickerIntent, CameraConst.TAKE_FROM_GALLERY)
    } else
        throw Exception("Should implement MediaView")
}

fun Fragment.openGallery(mode: Int) {
    if (this is MediaView) {
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"

        val picture = CameraModel()
        picture.mode = mode

        startActivityForResult(photoPickerIntent, CameraConst.TAKE_FROM_GALLERY)
    } else
        throw Exception("Should implement MediaView")
}

/**
 * @param requestCode
 * @param arrayOfMimeType ("image/*", "application/pdf")
*/*/
fun Activity.openGallery2(
    requestCode: Int = CameraConst.TAKE_FROM_GALLERY,
    arrayOfMimeType: Array<String> = arrayOf("image/*", "application/pdf"),
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
 * @param arrayOfMimeType ("image/*", "application/pdf")
*/*/
fun Fragment.openGallery2(
    requestCode: Int = CameraConst.TAKE_FROM_GALLERY,
    arrayOfMimeType: Array<String> = arrayOf("image/*", "application/pdf"),
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
        imagePath = if (pathFile is String && pathFile.isNotEmpty()) {
            isFromCameraLocal = false
            val uri = data.data
            val url = uri?.let { getFilePathFromUri(this, it) }
            url?.let { onGetImage(it) }
            url ?: ""
        } else {
            isFromCameraLocal = false
            if (cameraProperty?.pictUrl.isNullOrEmpty()) {
                ""
            } else {
                cameraProperty?.pictUrl?.let { onGetImage(it) }
                ""
            }
        }
    } else {
        val path = data?.extras?.getString(CameraConst.PARAM_PICTURE)
        imagePath = if (path is String && path.isNotEmpty()) {
            isFromCameraLocal = true
            onGetImage(path)
            path
        } else if (pathFile is String && pathFile.isNotEmpty()) {
            isFromCameraLocal = false
            val uri = data.data
            val url = uri?.let { getFilePathFromUri(this, it) }
            url?.let { onGetImage(it) }
            url ?: ""
        } else {
            isFromCameraLocal = false
            if (cameraProperty?.pictUrl.isNullOrEmpty()) {
                ""
            } else {
                cameraProperty?.pictUrl?.let { onGetImage(it) }
                ""
            }
        }
    }
    onEndResult(isFromCameraLocal)
    return imagePath
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
