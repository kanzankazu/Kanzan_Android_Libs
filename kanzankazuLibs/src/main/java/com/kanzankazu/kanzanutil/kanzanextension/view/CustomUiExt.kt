package com.kanzankazu.kanzanutil.kanzanextension.view

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.StyleableRes

fun ViewGroup.initView(context: Context, @LayoutRes int: Int, listener: View.() -> Unit) {
    val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    inflater.inflate(int, this).apply { listener.invoke(this) }
}

fun AttributeSet.initAttribute(context: Context, @StyleableRes intArray: IntArray, listener: TypedArray.() -> Unit) {
    val typedArray: TypedArray = context.obtainStyledAttributes(this, intArray, 0, 0)
    try {
        listener.invoke(typedArray)
    } finally {
        typedArray.recycle()
    }
}