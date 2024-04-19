package com.kanzankazu.kanzanwidget.camera.ui

import android.os.Bundle

interface PicturePreviewContract {
    interface View {
        fun showPreviewPicture(path: String)
        fun onRetakeClicked()
        fun setImageResult(bundle: Bundle?)
        fun onUploadFailed(message: String)
        fun onUploadSuccess()
    }

    interface Presenter {
        fun onBindView(bundle: Bundle)
        fun saveResult(bundle: Bundle)
        fun uploadPicture(mode: Int, picturePath: String)
    }
}