package com.kanzankazu.kanzanwidget.camera.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.kanzankazu.R

class MediaDialog(context: Context) {

    private val dialog: MaterialDialog
    private val tvCamera: TextView
    private val tvGallery: TextView

    init {
        val contentView = LayoutInflater.from(context).inflate(R.layout.dialog_media, null)
        dialog = MaterialDialog(context).show { customView(view = contentView, dialogWrapContent = true) }

        tvCamera = contentView.findViewById(R.id.tvCamera)
        tvGallery = contentView.findViewById(R.id.tvGallery)
    }

    fun setOnCameraClicked(onClickListener: View.OnClickListener) {
        tvCamera.setOnClickListener {
            onClickListener.onClick(it)
            dialog.dismiss()
        }
    }

    fun setOnGalleryClicked(onClickListener: View.OnClickListener) {
        tvGallery.setOnClickListener {
            onClickListener.onClick(it)
            dialog.dismiss()
        }
    }

    fun show() {
        dialog.show()
    }

    fun dismiss() {
        dialog.dismiss()
    }
}