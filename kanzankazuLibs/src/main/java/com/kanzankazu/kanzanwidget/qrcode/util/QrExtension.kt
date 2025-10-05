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

/**
 * Generates a QR code from the given data and sets it as the bitmap image of the calling ImageView.
 * The QR code size is dynamically calculated based on the device's screen dimensions.
 *
 * @param context The context of the current state of the application, used to retrieve system services.
 * @param data The text data to encode into the QR code. It should be a non-empty string.
 *
 * Example:
 * ```kotlin
 * val imageView = ImageView(context)
 * imageView.qrcodeGenerate(context, "https://example.com")
 * ```
 */
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
        val bitmap = qrgEncoder.getBitmap()
        this.setImageBitmap(bitmap)
    } catch (e: WriterException) {

    }
}

/**
 * Sets the activity's screen brightness to maximum (full brightness) temporarily.
 * The previous brightness value is passed to the provided listener for further use, such as restoration.
 *
 * @param listener A lambda function to handle the default screen brightness value before setting full brightness.
 *
 * Example:
 * ```kotlin
 * activity.setBrightnessFull { defaultBrightness ->
 *     // Perform operations with the screen set to full brightness
 *     // Once done, restore using defaultBrightness
 *     activity.setBrightnessDefault(defaultBrightness)
 * }
 * ```
 */
fun Activity?.setBrightnessFull(listener: (defaultScreenBrightness: Float) -> Unit) {
    val layout: WindowManager.LayoutParams = this?.window?.attributes!!
    val defaultScreenBrightness = layout.screenBrightness
    layout.screenBrightness = 1f
    window?.attributes = layout
    listener(defaultScreenBrightness)
}

/**
 * Restores the screen brightness of the current Activity to the specified default value.
 * This is useful for reverting changes made to the screen brightness temporarily.
 *
 * @param defaultScreenBrightness The value to reset the screen brightness to.
 *                                The value should be between `0.0` (completely dark)
 *                                and `1.0` (maximum brightness).
 *
 * Example:
 * ```kotlin
 * val defaultBrightness = 0.5f
 * activity.setBrightnessDefault(defaultBrightness) // Resets the screen brightness to 50%
 * ```
 */
fun Activity?.setBrightnessDefault(defaultScreenBrightness: Float) {
    val layout: WindowManager.LayoutParams = this?.window?.attributes!!
    layout.screenBrightness = defaultScreenBrightness
    window?.attributes = layout
}