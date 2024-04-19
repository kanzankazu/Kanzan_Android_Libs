@file:Suppress("DEPRECATION")

package com.kanzankazu.kanzanutil.kanzanextension.type

import android.annotation.SuppressLint
import android.app.Activity
import android.content.res.Resources
import android.graphics.drawable.Drawable
import com.kanzankazu.kanzanutil.kanzanextension.isNullOrZero
import com.kanzankazu.kanzanutil.kanzanextension.toDigits

fun Int.ifCon(conditionTrue: Boolean, defaultValue: () -> Int): Int {
    return if (conditionTrue) this else defaultValue()
}

fun Int.ifCon(conditionTrue: Boolean, defaultValue: Int): Int {
    return if (conditionTrue) this else defaultValue
}

fun Int?.ifNullOrZero(defaultValue: () -> Int): Int {
    return if (isNullOrZero()) defaultValue() else this ?: defaultValue()
}

fun Int.getIntDimens(activity: Activity): Int = activity.resources.getDimension(this).toInt()

fun Int.getFloatDimens(activity: Activity): Float = activity.resources.getDimension(this)

@SuppressLint("UseCompatLoadingForDrawables")
fun Int.getDrawable(activity: Activity): Drawable? = activity.resources.getDrawable(this)

fun Int.dpTopx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

fun Int.pxTodp(): Int = (this / Resources.getSystem().displayMetrics.density).toInt()

fun Int.setRupiah(): String = toString().setRupiah()

fun Int.getRupiah(): String = toString().getRupiah().toDigits()

fun Int.string(): String = toString()

fun Int.string(activity: Activity): String = activity.getString(this)

fun Int.isContain(arrayListOfInt: ArrayList<Int>): Boolean = arrayListOfInt.any { it == this }

fun Int.isContain(vararg ints: Int): Boolean = ints.any { it == this }

fun String.toIntOrDefault(defaultValue: Int = 0) = toIntOrNull() ?: defaultValue

fun Int.ifZero(defaultValue: Int): Int = if (this > 0) this else defaultValue
