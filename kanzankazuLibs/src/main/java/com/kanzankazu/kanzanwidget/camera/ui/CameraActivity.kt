@file:Suppress("DEPRECATION", "UNUSED_PARAMETER", "MemberVisibilityCanBePrivate")

package com.kanzankazu.kanzanwidget.camera.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import androidx.core.app.ActivityCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.kanzankazu.R
import com.kanzankazu.databinding.ActivityCameraBinding
import com.kanzankazu.kanzanutil.BaseConst
import com.kanzankazu.kanzanutil.PermissionUtil
import com.kanzankazu.kanzanwidget.camera.base.BaseCameraActivityBindingView
import com.kanzankazu.kanzanwidget.camera.util.CameraConst
import com.kanzankazu.kanzanwidget.camera.util.CameraFetchPath
import com.kanzankazu.kanzanwidget.camera.util.CameraUtils
import com.kanzankazu.kanzanwidget.camera.util.changePageForResult
import com.kanzankazu.kanzanwidget.camera.util.openGallery
import com.kanzankazu.kanzanwidget.camera.util.refresh
import io.fotoapparat.Fotoapparat
import io.fotoapparat.configuration.CameraConfiguration
import io.fotoapparat.configuration.UpdateConfiguration
import io.fotoapparat.selector.auto
import io.fotoapparat.selector.autoFocus
import io.fotoapparat.selector.back
import io.fotoapparat.selector.continuousFocusPicture
import io.fotoapparat.selector.firstAvailable
import io.fotoapparat.selector.fixed
import io.fotoapparat.selector.front
import io.fotoapparat.selector.highestFps
import io.fotoapparat.selector.highestResolution
import io.fotoapparat.selector.highestSensorSensitivity
import io.fotoapparat.selector.hz50
import io.fotoapparat.selector.hz60
import io.fotoapparat.selector.manualJpegQuality
import io.fotoapparat.selector.none
import io.fotoapparat.selector.off
import io.fotoapparat.selector.torch
import java.io.File

internal class CameraActivity : BaseCameraActivityBindingView<ActivityCameraBinding>(), ActivityCompat.OnRequestPermissionsResultCallback, MediaView {

    private lateinit var permissionUtil: PermissionUtil
    private var fotoapparat: Fotoapparat? = null
    private var mode: Int = 0

    /**
     * @sample lensPosition 0 = back, 1 = front
     */
    private var lensPosition: Int = 0

    /**
     * @sample flashLight 0 = off, 1 = on
     */
    private var flashLight: Int = 0

    private val cameraConfiguration = CameraConfiguration(
        pictureResolution = highestResolution(),
        previewResolution = highestResolution(),
        previewFpsRange = highestFps(),
        focusMode = firstAvailable(
            continuousFocusPicture(),
            autoFocus(),
            fixed()
        ),
        antiBandingMode = firstAvailable(
            auto(),
            hz50(),
            hz60(),
            none()
        ),
        jpegQuality = manualJpegQuality(75),
        sensorSensitivity = highestSensorSensitivity(),
        flashMode = off()
    )

    override val bindingInflater: (LayoutInflater) -> ActivityCameraBinding = ActivityCameraBinding::inflate

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionUtil.onRequestPermissionsResult(false, object : PermissionUtil.PermissionUtilListener {
            override fun onPermissionGranted() = refresh()

            override fun onPermissionDenied(message: String) = showPermissionDeniedAlert()
        }, requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        bind.ivTakePicture.isEnabled = true
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == CameraConst.GALLERY_ACTIVITY) {
                onPictureTaken(CameraFetchPath.getFilePathFromUri(this, data?.data!!))
            } else {
                //onActivityResult(requestCode, resultCode, data)
                val picturePath = data?.extras?.getString(CameraConst.PARAM_PICTURE)
                onSubmitPicture(picturePath)
            }
        } else if (resultCode == Activity.RESULT_CANCELED && mode == CameraConst.GALLERY_ACTIVITY && requestCode == CameraConst.GALLERY_ACTIVITY) {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        cameraStart()
        cameraStart()
    }

    override fun onStop() {
        super.onStop()
        cameraStop()
    }

    override fun setContent(): Unit = with(bind) {
        permissionUtil = PermissionUtil(this@CameraActivity)
        if (!permissionUtil.checkPermissionOnly(BaseConst.PERM_CAMERA_GALLERY)) {
            permissionUtil.requestPermission()
            cameraStop()
        } else {
            cameraInit()
        }
        ivTakePicture.isEnabled = true
    }

    override fun setListener(): Unit = with(bind) {
        ivSwitchCamera.setOnClickListener { onSwitchCameraClicked() }
        ivTakePicture.setOnClickListener {
            onTakePictureClicked()
            ivTakePicture.isEnabled = false
        }
        ivBack.setOnClickListener { finish() }
        ivInfo.setOnClickListener { openGallery(CameraConst.TAKE_FROM_GALLERY) }
        ivEnableFlash.setOnClickListener { switchFLash() }
    }

    override fun setData() {
        intent.extras?.let {
            mode = it.getInt(CameraConst.PARAM_CAMERA_MODE)
            if (mode == CameraConst.GALLERY_ACTIVITY) openGallery()
            if (it.getBoolean(CameraConst.PARAM_CAMERA_SHOW_GUIDELINE, false)) showGuideline(mode)
        }
    }

    override fun openPicturePreview(destinationPage: Class<PicturePreviewActivity>, bundle: Bundle?, requestCode: Int) {
        /*auto override*/
    }

    private fun cameraStart() {
        getCamera().start()
    }

    private fun cameraStop() {
        getCamera().stop()
    }

    private fun showGuideline(mode: Int) {
        /*auto override*/
    }

    private fun getCamera(): Fotoapparat {
        if (fotoapparat == null) {
            fotoapparat = Fotoapparat(this, bind.vCamera)
        }
        return fotoapparat as Fotoapparat
    }

    private fun onSwitchCameraClicked() {
        doSwitchCameraType()
    }

    private fun onTakePictureClicked() {
        doTakePicture(this)
    }

    private fun onPictureTaken(path: String?) {
        val bundle = Bundle()
        bundle.putAll(intent.extras)
        bundle.putString(CameraConst.PARAM_PICTURE, path)
        changePageForResult<PicturePreviewActivity>(CameraConst.PREVIEW_ACTIVITY, bundle)
    }

    private fun onSubmitPicture(path: String?) {
        val intent = Intent()
        intent.putExtra(CameraConst.PARAM_PICTURE, path)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun openGallery() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                3
            )
        } else {
            launchGallery()
        }
    }

    private fun launchGallery() {
        val getIntent = Intent(Intent.ACTION_PICK)
        getIntent.type = "image/*"

        startActivityForResult(getIntent, CameraConst.GALLERY_ACTIVITY)
    }

    private fun showPermissionDeniedAlert() {
        MaterialDialog(this).show {
            title(R.string.label_warning)
            message(R.string.error_permission_denied)
            positiveButton(R.string.label_close, click = {
                finish()
                dismiss()
            })
            noAutoDismiss()
            cancelOnTouchOutside(false)
        }
    }

    private fun cameraInit() {
        lensPosition = 0
        doSwitchCamera(lensPosition)
    }

    private fun doSwitchCameraType() {
        lensPosition = when (lensPosition) {
            0 -> 1
            else -> 0
        }

        doSwitchCamera(lensPosition)
    }

    private fun doSwitchCamera(lensPosition: Int) {
        when (lensPosition) {
            1 -> getCamera().switchTo(front(), cameraConfiguration)
            else -> getCamera().switchTo(back(), cameraConfiguration)
        }
    }

    private fun doTakePicture(context: Context) {
        val photoResult = getCamera().takePicture()
        val file = File(getDirectory(context) + "/" + getFilename() + ".jpeg")
        photoResult.saveToFile(file).whenAvailable { onPictureTaken(file.absolutePath) }
    }

    private fun getDirectory(context: Context): String {
        return CameraUtils.getPictureStorageDir(context)
    }

    fun switchFLash() {
        flashLight = when (flashLight) {
            0 -> 1
            else -> 0
        }
        getCamera().updateConfiguration(UpdateConfiguration(flashMode = if (flashLight == 1) torch() else off()))
    }

    fun getFilename(): String {
        return CameraUtils.getPictureFilename()
    }
}