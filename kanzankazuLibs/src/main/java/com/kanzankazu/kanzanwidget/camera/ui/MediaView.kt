package com.kanzankazu.kanzanwidget.camera.ui

import android.content.Intent
import android.os.Bundle

interface MediaView {
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    fun openPicturePreview(destinationPage: Class<PicturePreviewActivity>, bundle: Bundle? = null, requestCode: Int)
}