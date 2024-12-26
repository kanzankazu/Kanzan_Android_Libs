package com.kanzankazu.kanzanwidget.camera.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.kanzankazu.databinding.ActivityCameraPreviewBinding
import com.kanzankazu.kanzanutil.kanzanextension.simpleToast
import com.kanzankazu.kanzanutil.kanzanextension.view.loadImage
import com.kanzankazu.kanzanwidget.camera.base.BaseCameraActivityBindingView
import com.kanzankazu.kanzanwidget.camera.util.CameraConst

class PicturePreviewActivity : BaseCameraActivityBindingView<ActivityCameraPreviewBinding>(), PicturePreviewContract.View {
    private lateinit var presenter: PicturePreviewContract.Presenter

    override val bindingInflater: (LayoutInflater) -> ActivityCameraPreviewBinding = ActivityCameraPreviewBinding::inflate

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
            && grantResults[1] == PackageManager.PERMISSION_GRANTED
        ) {
            intent.extras?.let { presenter.onBindView(it) }
        }
    }

    override fun setContent() {
        bind.ivBack.setOnClickListener { onRetakeClicked() }
        bind.btRetake.setOnClickListener { onRetakeClicked() }
        bind.btNext.setOnClickListener {
            intent.extras!!.getString(CameraConst.PARAM_PICTURE)?.let { it1 ->
                presenter.uploadPicture(
                    intent.extras!!.getInt(CameraConst.PARAM_CAMERA_MODE),
                    it1
                )
            }
        }

        presenter = PicturePreviewPresenter(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                2
            )
        } else {
            intent.extras?.let { presenter.onBindView(it) }
        }
    }

    override fun setListener() {
    }

    override fun setData() {
    }

    override fun showPreviewPicture(path: String) {
        bind.ivImagePreview.loadImage(path)
    }

    override fun onRetakeClicked() {
        setImageResult(null)
    }

    override fun setImageResult(bundle: Bundle?) {
        if (bundle == null) {
            setResult(Activity.RESULT_CANCELED)
        } else {
            val intent = Intent()
            intent.putExtras(bundle)
            setResult(Activity.RESULT_OK, intent)
        }
        finish()
    }

    override fun onUploadSuccess() {
        setImageResult(intent.extras)
    }

    override fun onUploadFailed(message: String) {
        this.simpleToast(message, Toast.LENGTH_SHORT)
    }
}