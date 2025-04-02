package com.kanzankazu.kanzanutil.kanzanextension.type

import com.kanzankazu.kanzanutil.kanzanextension.toDigits
import java.text.ParseException
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * Evaluates a condition and returns either the current `Long` value or a fallback value provided by a lambda function.
 * If `conditionTrue` evaluates to true, the current `Long` is returned; otherwise, the `defaultValue` lambda is executed
 * and its result is returned.
 *
 * @param conditionTrue A boolean value that determines whether the current value (`Long`) should be returned.
 * @param defaultValue A lambda function that produces a `Long` value to return if `conditionTrue` is false.
 * @return The current `Long` if `conditionTrue` is true, or the result of `defaultValue` otherwise.
 *
 * Example:
 * ```kotlin
 * val number: Long = 100
 * val result = number.ifCon(conditionTrue = false) { 200 }
 * println(result) // Output: 200
 *
 * val anotherResult = number.ifCon(conditionTrue = true) { 200 }
 * println(anotherResult) // Output: 100
 * ```
 */
fun Long.ifCon(conditionTrue: Boolean, defaultValue: () -> Long) =
    if (conditionTrue) this else defaultValue()

/**
 * Returns the current `Long` value if `conditionTrue` is `true`; otherwise, returns the specified `defaultValue`.
 *
 * @param conditionTrue A Boolean condition. If `true`, the method returns the current `Long` value.
 * @param defaultValue The `Long` value to return if `conditionTrue` is `false`.
 * @return The current `Long` value if `conditionTrue` is `true`; otherwise, the specified `defaultValue`.
 *
 * Example:
 * ```kotlin
 * val value: Long = 42
 * val result = value.ifCon(conditionTrue = true, defaultValue = 100) // Result: 42
 * val result2 = value.ifCon(conditionTrue = false, defaultValue = 100) // Result: 100
 * ```
 */
fun Long.ifCon(conditionTrue: Boolean, defaultValue: Long) =
    if (conditionTrue) this else defaultValue

/**
 * Returns the current `Long` value if it is non-zero, otherwise invokes the provided lambda function
 * and returns its result. This is useful for scenarios where you want to provide a fallback value
 * dynamically if the current value is zero.
 *
 * @param listener A lambda function that is invoked to provide a fallback `Long` value
 *                 when the current value is zero.
 * @return The current `Long` value if it is not zero, otherwise the result of invoking the lambda `listener`.
 *
 * Example:
 * ```kotlin
 * val number: Long = 0L
 * val result = number.ifZero { 42L }
 * println(result) // Output: 42
 *
 * val anotherNumber: Long = 10L
 * val anotherResult = anotherNumber.ifZero { 42L }
 * println(anotherResult) // Output: 10
 * ```
 */
fun Long.ifZero(listener: () -> Long) = if (this != 0L) this else listener.invoke()

/**
 * Converts a time duration in milliseconds (represented by a `Long`) into a string formatted as "HH:mm:ss".
 * If the input duration is zero or negative, it returns "00:00:00".
 *
 * @receiver The time duration in milliseconds to be converted.
 * @return A string in the format "HH:mm:ss", where:
 *         - HH represents hours (two digits, padded with zero if necessary),
 *         - mm represents minutes (two digits, padded with zero if necessary),
 *         - ss represents seconds (two digits, padded with zero if necessary).
 *         If the input is invalid or an exception occurs, the default string "00:00:00" is returned.
 *
 * Example:
 * ```kotlin
 * val duration = 3661000L // 1 hour, 1 minute, and 1 second in milliseconds
 * val formattedTime = duration.betweenTime() // Output: "01:01:01"
 *
 * val zeroDuration = 0L
 * val defaultTime = zeroDuration.betweenTime() // Output: "00:00:00"
 *
 * val negativeDuration = -500L
 * val negativeTime = negativeDuration.betweenTime() // Output: "00:00:00"
 * ```
 */
fun Long.betweenTime() = try {

    val mills = this
    val hours = mills / (1000 * 60 * 60)
    val mins = (mills / (1000 * 60)) % 60
    val secs = (mills / 1000).toInt() % 60.toLong()

    if (mills > 0F) {
        hours.formatTwoDigit() + ":" + mins.formatTwoDigit() + ":" + secs.formatTwoDigit()
    } else {
        "00:00:00"
    }
} catch (e: ParseException) {
    "00:00:00"
}

/**
 * Formats a Long value as a two-digit string. If the value is less than or equal to 10,
 * it adds a leading zero. Otherwise, it returns the value as a string.
 *
 * @receiver The Long value to be formatted.
 * @return A string representing the Long value in two-digit format.
 *
 * Example:
 * ```kotlin
 * val singleDigit = 7L.formatTwoDigit() // "07"
 * val doubleDigit = 12L.formatTwoDigit() // "12"
 * ```
 */
fun Long.formatTwoDigit() = if (this <= 10) "0$this" else "$this"

/**
 * Converts the Long value to its String representation.
 *
 * @return A String representation of the Long value.
 *
 * Example:
 * ```kotlin
 * val number: Long = 123456789L
 * val stringValue = number.string() // Result: "123456789"
 * ```
 */
fun Long.string() = toString()

/**
 * Checks if the given Long value is either null or zero.
 *
 * This method uses Kotlin's contract to indicate that if the returned value is `false`,
 * then the value is guaranteed to be non-null.
 *
 * @return `true` if the Long value is null or zero; `false` otherwise.
 *
 * Example:
 * ```kotlin
 * val value1: Long? = null
 * println(value1.isNullOrZero()) // Output: true
 *
 * val value2: Long? = 0L
 * println(value2.isNullOrZero()) // Output: true
 *
 * val value3: Long? = 10L
 * println(value3.isNullOrZero()) // Output: false
 * ```
 */
@OptIn(ExperimentalContracts::class)
fun Long?.isNullOrZero(): Boolean {
    contract { returns(false) implies (this@isNullOrZero != null) }
    return this == null || this == 0L
}

/**
 * Converts a `Long` value into a formatted Indonesian Rupiah currency string by internally converting it to
 * a `String` and applying the `setRupiah()` function.
 *
 * @receiver The `Long` value to be formatted as Rupiah currency.
 * @return A `String` representing the currency in Rupiah format, prefixed with "Rp" and using dots (.) as
 *         thousand separators. Returns an empty string in case of an error or invalid conversion.
 *
 * Example:
 * ```kotlin
 * val amount: Long = 1500000L
 * val formattedAmount = amount.setRupiah() // Output: "Rp 1.500.000"
 *
 * val zeroAmount: Long = 0L
 * val formattedZeroAmount = zeroAmount.setRupiah() // Output: "Rp 0"
 * ```
 */
fun Long.setRupiah() = toString().setRupiah()

/**
 * Converts the `Int` value into a Rupiah currency format string, where the format removes
 * any special character ("Rp ", ".") and retains only numeric digits.
 *
 * Internally, this calls the `toString()` method on the integer to convert it to a `String`,
 * and then applies the `getRupiah()` extension to process it into numeric-only string form
 * suitable for Rupiah formatting.
 *
 * @return A `String` representing the numeric Rupiah amount without additional currency symbols
 *         or formatting. If the input is zero, the output will be "0".
 *
 * Example:
 * ```kotlin
 * val amount = 1205000 // Represents Rp 1,205,000 in Rupiah
 * val rupiahString = amount.getRupiah() // Returns: "1205000"
 *
 * val zeroAmount = 0
 * val zeroRupiah = zeroAmount.getRupiah() // Returns: "0"
 * ```
 */
fun Long.getRupiah() = toString().getRupiah().toDigits()

/**
 * Converts the String into a Long value, or returns a default value if the String cannot be converted.
 *
 * @param defaultValue The Long value to return if the String cannot be parsed to a Long. Defaults to 0.
 * @return The Long value parsed from the String, or the provided default value if parsing fails.
 *
 * Example:
 * ```kotlin
 * val str1 = "12345"
 * val result1 = str1.toLongOrDefault() // Result: 12345
 *
 * val str2 = "notANumber"
 * val result2 = str2.toLongOrDefault(100) // Result: 100
 * ```
 */
fun String.toLongOrDefault(defaultValue: Long = 0) = toLongOrNull() ?: defaultValue
