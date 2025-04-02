package com.kanzankazu.kanzanutil.kanzanextension.view

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.StyleableRes

/**
 * Inflates a custom view into the given ViewGroup and applies a provided lambda function to the inflated view.
 *
 * @param context The Context used to obtain the LayoutInflater.
 * @param int The layout resource ID of the view to be inflated.
 * @param listener A lambda function that operates on the inflated view. This function is invoked with the inflated view as the receiver.
 *
 * Example:
 * ```kotlin
 * val myViewGroup: ViewGroup = findViewById(R.id.my_container)
 * myViewGroup.initCustomView(context, R.layout.custom_layout) {
 *     findViewById<TextView>(R.id.text_view)?.text = "Custom Text"
 * }
 * ```
 */
fun ViewGroup.initCustomView(context: Context, @LayoutRes int: Int, listener: View.() -> Unit) {
    val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    inflater.inflate(int, this).apply { listener.invoke(this) }
}

/**
 * Initializes and processes custom attributes defined in the XML layout.
 * This function provides a streamlined way to retrieve a `TypedArray`
 * for the given attribute set and styleable resource, and invokes a listener
 * function for further processing. The `TypedArray` is recycled automatically
 * after the listener is executed to free up resources.
 *
 * @param context The `Context` of the current state of the application/object.
 *                Used to access application resources, including theme-based attributes.
 * @param intArray An array of styleable resource IDs to retrieve from the attribute set.
 *                 These are usually defined in the `attrs.xml` file.
 * @param listener An optional lambda function that operates on the obtained `TypedArray`.
 *                 The function is invoked within the lifecycle of the initialized `TypedArray`
 *                 to process custom attributes. Defaults to an empty lambda.
 *
 * Example:
 * ```kotlin
 * val attrs = attrs.initCustomAttribute(context, R.styleable.CustomView) {
 *     val customColor = getColor(R.styleable.CustomView_customColor, Color.BLACK)
 *     val customText = getString(R.styleable.CustomView_customText)
 * }
 * ```
 */
fun AttributeSet.initCustomAttribute(context: Context, @StyleableRes intArray: IntArray, listener: TypedArray.() -> Unit = {}) {
    val typedArray: TypedArray = context.obtainStyledAttributes(this, intArray, 0, 0)
    try {
        listener.invoke(typedArray)
    } finally {
        typedArray.recycle()
    }
}