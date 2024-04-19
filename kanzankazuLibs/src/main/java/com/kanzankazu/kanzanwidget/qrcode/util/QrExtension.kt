@file:Suppress("DEPRECATION")

package com.kanzankazu.kanzanwidget.qrcode.util

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.view.WindowManager
import android.widget.ImageView
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import com.google.zxing.WriterException

fun ImageView.qrcodeGenerate(context: Context, data: String) {
    val manager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display = manager.defaultDisplay
    val point = Point()
    display.getSize(point)

    val width: Int = point.x
    val height: Int = point.y
    var smallerDimension = if (width < height) width else height
    smallerDimension *= 2

    val qrgEncoder = QRGEncoder(data, null, QRGContents.Type.TEXT, smallerDimension)
    try {
        val bitmap = qrgEncoder.encodeAsBitmap()
        this.setImageBitmap(bitmap)
    } catch (e: WriterException) {

    }
}

fun Activity?.setBrightnessFull(listener: (defaultScreenBrightness: Float) -> Unit) {
    val layout: WindowManager.LayoutParams = this?.window?.attributes!!
    val defaultScreenBrightness = layout.screenBrightness
    layout.screenBrightness = 1f
    window?.attributes = layout
    listener(defaultScreenBrightness)
}

fun Activity?.setBrightnessDefault(defaultScreenBrightness: Float) {
    val layout: WindowManager.LayoutParams = this?.window?.attributes!!
    layout.screenBrightness = defaultScreenBrightness
    window?.attributes = layout
}