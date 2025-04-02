package com.kanzankazu.kanzanwidget

import android.graphics.*
import androidx.annotation.NonNull
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import java.security.MessageDigest

class RoundedCornersTransformation @JvmOverloads constructor(
    private val radius: Int,
    private val margin: Int,
    private val cornerType: CornerType = CornerType.ALL,
) : BitmapTransformation() {

    companion object {
        private const val VERSION = 1
        private const val ID = "com.kanzankazu.kanzanwidget.RoundedCornersTransformation.$VERSION"
        private val ID_BYTES = ID.toByteArray(CHARSET)
    }

    private val diameter = radius * 2

    override fun toString(): String {
        return "RoundedCornersTransformation(radius=$radius, margin=$margin, diameter=$diameter, cornerType=${cornerType.name})"
    }

    override fun equals(other: Any?): Boolean {
        return other is RoundedCornersTransformation &&
                other.radius == radius &&
                other.margin == margin &&
                other.cornerType == cornerType
    }

    override fun hashCode(): Int {
        var result = ID.hashCode()
        result = 31 * result + radius
        result = 31 * result + margin
        result = 31 * result + cornerType.hashCode()
        return result
    }

    override fun updateDiskCacheKey(@NonNull messageDigest: MessageDigest) {
        messageDigest.update(ID_BYTES)
        messageDigest.update(radius.toByte())
        messageDigest.update(margin.toByte())
        messageDigest.update(cornerType.ordinal.toByte())
    }

    override fun transform(
        @NonNull pool: BitmapPool,
        @NonNull toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int,
    ): Bitmap {
        val bitmap = pool.get(toTransform.width, toTransform.height, Bitmap.Config.ARGB_8888).apply {
            setHasAlpha(true)
        }

        val canvas = Canvas(bitmap)
        val paint = Paint().apply {
            isAntiAlias = true
            shader = BitmapShader(toTransform, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        }

        val rect = RectF(margin.toFloat(), margin.toFloat(), (toTransform.width - margin).toFloat(), (toTransform.height - margin).toFloat())
        drawCorners(canvas, paint, rect)
        return bitmap
    }

    private fun drawCorners(canvas: Canvas, paint: Paint, rect: RectF) {
        when (cornerType) {
            CornerType.ALL -> canvas.drawRoundRect(rect, radius.toFloat(), radius.toFloat(), paint)
            else -> drawPartialCorners(canvas, paint, rect)
        }
    }

    private fun drawPartialCorners(canvas: Canvas, paint: Paint, rect: RectF) {
        val topLeft = RectF(rect.left, rect.top, rect.left + diameter, rect.top + diameter)
        val topRight = RectF(rect.right - diameter, rect.top, rect.right, rect.top + diameter)
        val bottomLeft = RectF(rect.left, rect.bottom - diameter, rect.left + diameter, rect.bottom)
        val bottomRight = RectF(rect.right - diameter, rect.bottom - diameter, rect.right, rect.bottom)

        when (cornerType) {
            CornerType.TOP_LEFT -> canvas.drawRoundRect(topLeft, radius.toFloat(), radius.toFloat(), paint)
            CornerType.TOP_RIGHT -> canvas.drawRoundRect(topRight, radius.toFloat(), radius.toFloat(), paint)
            CornerType.BOTTOM_LEFT -> canvas.drawRoundRect(bottomLeft, radius.toFloat(), radius.toFloat(), paint)
            CornerType.BOTTOM_RIGHT -> canvas.drawRoundRect(bottomRight, radius.toFloat(), radius.toFloat(), paint)
            else -> canvas.drawRoundRect(rect, radius.toFloat(), radius.toFloat(), paint)
        }
    }

    enum class CornerType {
        ALL,
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT
    }
}