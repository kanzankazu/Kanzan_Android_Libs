@file:Suppress("DEPRECATION")

package com.kanzankazu.kanzanutil.kanzanextension.type

import android.annotation.SuppressLint
import android.app.Activity
import android.content.res.Resources
import android.graphics.drawable.Drawable
import com.kanzankazu.kanzanutil.kanzanextension.isNullOrZero
import com.kanzankazu.kanzanutil.kanzanextension.toDigits

/**
 * Evaluates a condition and returns either the current Integer value (`this`) if the condition is true,
 * or a default value provided by the lambda function `defaultValue` if the condition is false.
 *
 * @param conditionTrue A Boolean determining whether the current Integer value should be returned.
 * @param defaultValue A lambda function returning the default value to use if `conditionTrue` is false.
 * @return The current Integer value (`this`) if `conditionTrue` is true, otherwise the value produced by `defaultValue`.
 *
 * Example:
 * ```kotlin
 * val value = 10
 * val result = value.ifCon(conditionTrue = false) { 20 }
 * println(result) // Output: 20
 *
 * val result2 = value.ifCon(conditionTrue = true) { 5 }
 * println(result2) // Output: 10
 * ```
 */
fun Int.ifCon(conditionTrue: Boolean, defaultValue: () -> Int): Int {
    return if (conditionTrue) this else defaultValue()
}

/**
 * Returns either the current integer (`this`) if the provided condition is `true`,
 * or a default value if the condition is `false`.
 *
 * @param conditionTrue A Boolean indicating whether the current integer (`this`) should be returned.
 *                      If `true`, the method returns `this`; otherwise, it returns the `defaultValue`.
 * @param defaultValue The value to return if the condition is `false`.
 * @return The current integer (`this`) if the condition is `true`, or the provided `defaultValue` if `false`.
 *
 * Example:
 * ```kotlin
 * val number = 10
 * val resultTrue = number.ifCon(true, 0)  // Result: 10
 * val resultFalse = number.ifCon(false, 0) // Result: 0
 * println(resultTrue) // Output: 10
 * println(resultFalse) // Output: 0
 * ```
 */
fun Int.ifCon(conditionTrue: Boolean, defaultValue: Int): Int {
    return if (conditionTrue) this else defaultValue
}

/**
 * Returns the value of the current nullable `Int` if it is not null or zero; otherwise, evaluates and returns
 * the value provided by the `defaultValue` lambda. This can be used to handle cases where a nullable `Int`
 * needs a fallback value when it is either null or zero.
 *
 * @param defaultValue A lambda function that provides a fallback value of type `Int` when the current instance is null or zero.
 * @return The current `Int` instance if it is not null or zero, or the result of the `defaultValue` lambda otherwise.
 *
 * Example:
 * ```kotlin
 * val number: Int? = null
 * val result = number.ifNullOrZero { 10 }
 * println(result) // Output: 10
 *
 * val zeroNumber: Int? = 0
 * val zeroResult = zeroNumber.ifNullOrZero { 5 }
 * println(zeroResult) // Output: 5
 *
 * val validNumber: Int? = 20
 * val validResult = validNumber.ifNullOrZero { 50 }
 * println(validResult) // Output: 20
 * ```
 */
fun Int?.ifNullOrZero(defaultValue: () -> Int): Int {
    return if (isNullOrZero()) defaultValue() else this ?: defaultValue()
}

/**
 * Retrieves the dimension value (in pixels) for the given resource ID and converts it to an integer.
 *
 * @param activity The Activity context used to access the resources.
 * @return An integer value representing the dimension (in pixels) associated with this resource ID.
 *
 * Example:
 * ```kotlin
 * val dimensionInPixels = R.dimen.some_dimension.getIntDimens(activity)
 * println(dimensionInPixels) // Output: 16 (as an example, depends on the dimension defined in resources)
 * ```
 */
fun Int.getIntDimens(activity: Activity): Int = activity.resources.getDimension(this).toInt()

/**
 * Retrieves the dimension value as a floating-point number for the given resource ID.
 *
 * @param activity The activity context used to access the resources.
 * @return The dimension value, represented as a `Float`, corresponding to the resource ID represented by the `Int` receiver.
 *
 * Example:
 * ```kotlin
 * val dimension = R.dimen.myDimension.getFloatDimens(activity)
 * println("Dimension in pixels: $dimension")
 * ```
 */
fun Int.getFloatDimens(activity: Activity): Float = activity.resources.getDimension(this)

/**
 * Retrieves a `Drawable` resource associated with the given integer resource ID.
 * This method allows accessing a drawable resource within the context of the provided `Activity`.
 *
 * @param activity The `Activity` context used to fetch the drawable resource.
 *                 This is required to access application resources.
 * @return A `Drawable` object corresponding to the given resource ID if it exists; otherwise, returns null.
 *
 * Example:
 * ```kotlin
 * val drawableResId = R.drawable.my_image
 * val drawable = drawableResId.getDrawable(activity)
 * imageView.setImageDrawable(drawable)
 * ```
 */
@SuppressLint("UseCompatLoadingForDrawables")
fun Int.getDrawable(activity: Activity): Drawable? = activity.resources.getDrawable(this)

/**
 * Converts a value in density-independent pixels (dp) to pixels (px) based on the current device's screen density.
 *
 * @receiver The original value in dp as an integer.
 * @return The equivalent value in pixels (px) as an integer.
 *
 * Example:
 * ```kotlin
 * val dpValue = 16
 * val pxValue = dpValue.dpTopx() // Converts the dp value to px based on the device's screen density
 * println(pxValue) // Output depends on the screen density (e.g., 48 on a device with 3.0 density)
 * ```
 */
fun Int.dpTopx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

/**
 * Converts a pixel value to density-independent pixels (dp), based on the current screen density.
 *
 * @return The corresponding value in dp (density-independent pixels), calculated by dividing
 * the pixel value by the screen's density. The result is rounded to the nearest integer.
 *
 * Example:
 * ```kotlin
 * val pxValue = 120
 * val dpValue = pxValue.pxTodp() // e.g., 40 (if screen density is 3.0)
 * println(dpValue)
 * ```
 */
fun Int.pxTodp(): Int = (this / Resources.getSystem().displayMetrics.density).toInt()

/**
 * Converts an `Int` value, typically representing an amount, into a string formatted as Indonesian Rupiah currency.
 * This function is a shorthand that delegates the conversion to the `String.setRupiah()` method after converting the integer to a string.
 *
 * @receiver An `Int` value that represents the amount to be formatted as Rupiah.
 * @return A `String` formatted as Indonesian Rupiah currency with "Rp" as the prefix and dot (.) as the thousand separator,
 *         or an empty string if the formatting fails.
 *
 * Example:
 * ```kotlin
 * val amount = 1500000.setRupiah() // Output: "Rp 1.500.000"
 * val zeroAmount = 0.setRupiah() // Output: "Rp 0"
 * ```
 */
fun Int.setRupiah(): String = toString().setRupiah()

/**
 * Converts an integer value into a Rupiah-formatted string.
 * The integer is first converted into its string representation and then formatted
 * to follow the Rupiah currency standard using existing extension functions.
 *
 * @return A `String` representing the integer value formatted as a clean numeric representation
 *         of Rupiah without the "Rp" prefix or dot separators. If the integer value is zero,
 *         it returns "0".
 *
 * Example:
 * ```kotlin
 * val amount = 1000000.getRupiah() // Returns: "1000000"
 * val smallAmount = 123.getRupiah()  // Returns: "123"
 * val zeroAmount = 0.getRupiah()     // Returns: "0"
 * ```
 */
fun Int.getRupiah(): String = toString().getRupiah().toDigits()

/**
 * Converts an integer to its string representation.
 *
 * @return A string representation of the integer.
 *
 * Example:
 * ```kotlin
 * val number = 42
 * val numberString = number.string() // "42"
 * ```
 */
fun Int.string(): String = toString()

/**
 * Retrieves a string resource using the given integer resource ID and the context of the specified Activity.
 *
 * @param activity The Activity from which the context and resources are accessed.
 * @return The string value associated with the provided resource ID.
 *
 * Example:
 * ```kotlin
 * val stringValue = R.string.app_name.string(activity)
 * // Retrieves the string associated with app_name resource in the specified activity context.
 * ```
 */
fun Int.string(activity: Activity): String = activity.getString(this)

/**
 * Checks if the integer value (of the receiver) is contained within the provided list of integers.
 * This function compares the receiver against each element in the list using the equality operator.
 *
 * @param arrayListOfInt A list of integers (`ArrayList<Int>`) against which the receiver is checked for containment.
 *                       The list can be empty, in which case the function will always return `false`.
 * @return `true` if the receiver integer is found in the provided list, `false` otherwise.
 *
 * Example:
 * ```kotlin
 * val number = 5
 * val list = arrayListOf(1, 2, 3, 4, 5)
 * val result = number.isContain(list) // Returns true
 *
 * val anotherNumber = 10
 * val result2 = anotherNumber.isContain(list) // Returns false
 * ```
 */
fun Int.isContain(arrayListOfInt: ArrayList<Int>): Boolean = arrayListOfInt.any { it == this }

/**
 * Checks if the integer on which the function is called (`this`) is present in the provided array of integers.
 *
 * @param ints A variable number of integer arguments to check against. Can be zero or more integers.
 * @return `true` if the current integer (`this`) is contained in the provided integers, otherwise `false`.
 *
 * Example:
 * ```kotlin
 * val number = 5
 * val result1 = number.isContain(1, 2, 3, 4, 5) // Result: true
 * val result2 = number.isContain(6, 7, 8) // Result: false
 * ```
 */
fun Int.isContain(vararg ints: Int): Boolean = ints.any { it == this }

/**
 * Converts a string to an integer. If the string cannot be converted (e.g., it is not a valid integer),
 * the method returns the provided default value.
 *
 * @param defaultValue The value to return if the string cannot be converted to an integer. Defaults to 0.
 * @return The integer value of the string if it can be converted, or the specified default value otherwise.
 *
 * Example:
 * ```kotlin
 * val validInt = "123".toIntOrDefault() // Result: 123
 * val invalidInt = "abc".toIntOrDefault(5) // Result: 5
 * val emptyString = "".toIntOrDefault() // Result: 0
 * ```
 */
fun String.toIntOrDefault(defaultValue: Int = 0) = toIntOrNull() ?: defaultValue

/**
 * Returns the integer value of the current instance if it is greater than zero; otherwise, returns the specified default value.
 *
 * @param defaultValue The integer value to return if the current instance is zero or less.
 * @return The current integer value if it is greater than zero; otherwise, the specified default value.
 *
 * Example:
 * ```kotlin
 * val value1 = 5.ifZero(10) // Result: 5
 * val value2 = 0.ifZero(10) // Result: 10
 * val value3 = -1.ifZero(10) // Result: 10
 * ```
 */
fun Int.ifZero(defaultValue: Int): Int = if (this > 0) this else defaultValue
