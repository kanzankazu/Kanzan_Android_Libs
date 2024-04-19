@file:Suppress("MemberVisibilityCanBePrivate", "DEPRECATION", "SameParameterValue", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "unused")

package com.kanzankazu.kanzanutil

import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.kanzankazu.R
import com.kanzankazu.kanzanutil.kanzanextension.type.debugMessage
import id.zelory.compressor.Compressor
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Objects

class PictureUtil {
    private var authority: String = ""
    private val qualityCompress: Int = 50
    private var mCurrentPhotoPath: String? = null
    private var imageView: ImageView? = null
    private var mActivity: ComponentActivity
    private var mFragment: Fragment? = null

    constructor(mActivity: ComponentActivity, applicationId: String) {
        this.mActivity = mActivity
        this.authority = "$applicationId.provider"
    }

    constructor(mActivity: ComponentActivity, fragment: Fragment, applicationId: String) {
        this.mActivity = mActivity
        this.mFragment = fragment
        this.authority = "$applicationId.provider"
    }

    fun chooseGetImageDialog(imageView: ImageView?) {
        this.imageView = imageView
        chooseGetImageDialog()
    }

    fun chooseGetImageDialog() {
        val items = arrayOf("Pilih foto dari kamera", "Pilih foto dari galeri")
        "PictureUtil - chooseGetImageDialog".debugMessage()
        val chooseImageDialog = AlertDialog.Builder(mActivity)
        chooseImageDialog.setItems(items) { _: DialogInterface?, i: Int ->
            if (items[i] == "Pilih foto dari kamera") openCamera()
            else openGallery()
        }
        chooseImageDialog.show()
    }

    @SuppressLint("QueryPermissionsNeeded")
    fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        if (takePictureIntent.resolveActivity(mActivity.packageManager) != null) {
            var photoFile: File? = null
            try {
                photoFile = createImageFile(true)
            } catch (e: IOException) {
                Snackbar.make(mActivity.findViewById(android.R.id.content), mActivity.getString(R.string.error_message_image_get_failed), Snackbar.LENGTH_SHORT).show()
                Log.e("Lihat7", "openCamera PictureUtil21 : $e")
            }
            if (photoFile != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    try {
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(mActivity, authority, createImageFile(true)))
                        if (mFragment == null) mActivity.startActivityForResult(takePictureIntent, REQUEST_CODE_IMAGE_CAMERA)
                        else mFragment!!.startActivityForResult(takePictureIntent, REQUEST_CODE_IMAGE_CAMERA)
                    } catch (e: IOException) {
                        Log.e("Lihat8", "openCamera PictureUtil22 : $e")
                    }
                } else {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile))
                    if (mFragment == null) mActivity.startActivityForResult(takePictureIntent, REQUEST_CODE_IMAGE_CAMERA)
                    else mFragment!!.startActivityForResult(takePictureIntent, REQUEST_CODE_IMAGE_CAMERA)
                }
            }
        }

    }

    fun openGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_PICK
        if (mFragment == null) mActivity.startActivityForResult(Intent.createChooser(intent, "Pilih foto"), REQUEST_CODE_IMAGE_GALLERY)
        else mFragment!!.startActivityForResult(Intent.createChooser(intent, "Pilih foto"), REQUEST_CODE_IMAGE_GALLERY)
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): String? {
        if (requestCode == REQUEST_CODE_IMAGE_CAMERA && resultCode == Activity.RESULT_OK && mCurrentPhotoPath != null) { //FROM CAMERA
            try {
                val galleryPath = mCurrentPhotoPath
                val file = File(Objects.requireNonNull(Uri.parse(galleryPath).path))
                try {
                    val compressedImage = Compressor(mActivity)
                        .setQuality(qualityCompress)
                        .setCompressFormat(Bitmap.CompressFormat.JPEG)
                        .compressToFile(file)
                    mCurrentPhotoPath = getRealPathFromURIPath(Uri.parse(compressedImage.absolutePath))
                } catch (e: Exception) {
                    Log.e("Lihat2", "onActivityResult PictureUtil20 : $e")
                }
                imageView?.let { Glide.with(mActivity).load(mCurrentPhotoPath).into(it) }
                return mCurrentPhotoPath
            } catch (e: Exception) {
                Snackbar.make(mActivity.findViewById(android.R.id.content), mActivity.getString(R.string.error_message_picture_failed), Snackbar.LENGTH_LONG).show()
                Log.e("Lihat1", "onActivityResult PictureUtil21 : $e")
            }
        } else if (requestCode == REQUEST_CODE_IMAGE_GALLERY && resultCode == Activity.RESULT_OK && data != null) { //FROM GALLERY
            try {
                val uri = data.data
                val galleryPath = getRealPathFromURIPath(uri)
                val file = File(Objects.requireNonNull(Uri.parse(galleryPath).path))
                try {
                    val compressedImage = Compressor(mActivity)
                        .setQuality(qualityCompress)
                        .setCompressFormat(Bitmap.CompressFormat.JPEG)
                        .compressToFile(file)
                    mCurrentPhotoPath = getRealPathFromURIPath(Uri.parse(compressedImage.absolutePath))
                    imageView?.let { Glide.with(mActivity).load(mCurrentPhotoPath).into(it) }
                    return mCurrentPhotoPath
                } catch (e: Exception) {
                    Log.e("Lihat2", "onActivityResult PictureUtil22 : $e")
                }
            } catch (e: Exception) {
                Log.e("Lihat3", "onActivityResult PictureUtil23 : $e")
            }
        }
        return ""
    }

    private fun getRealPathFromURIPath(contentURI: Uri?): String? {
        @SuppressLint("Recycle") val cursor = mActivity.contentResolver.query(contentURI!!, null, null, null, null)
        return if (cursor == null) {
            contentURI.path
        } else {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            cursor.getString(idx)
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(uriFormat: Boolean): File {
        // Create an image file name
        @SuppressLint("SimpleDateFormat") val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        //File storageDir = mActivity.getBaseContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        val file = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )

        // Save a file: pathLocal for use with ACTION_VIEW intents
        mCurrentPhotoPath = if (uriFormat) {
            file.absolutePath
        } else {
            "file:" + file.absolutePath
        }
        return file
    }

    companion object {
        const val REQUEST_CODE_IMAGE_CAMERA = 1
        const val REQUEST_CODE_IMAGE_GALLERY = 2
    }
}