@file:Suppress("UNUSED_VARIABLE")

package com.kanzankazu.kanzanwidget.camera.ui

import android.os.Bundle
import com.kanzankazu.kanzanwidget.camera.util.CameraConst

class PicturePreviewPresenter(private var picturePreviewView: PicturePreviewContract.View) : PicturePreviewContract.Presenter {
    override fun onBindView(bundle: Bundle) {
        val picturePath = bundle.getString(CameraConst.PARAM_PICTURE)
        picturePreviewView.showPreviewPicture("file://$picturePath")
    }

    override fun saveResult(bundle: Bundle) {
        picturePreviewView.setImageResult(bundle)
    }


    override fun uploadPicture(mode: Int, picturePath: String) {
        picturePreviewView.onUploadSuccess()

    }
}