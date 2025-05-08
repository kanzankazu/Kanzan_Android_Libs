@file:Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "DEPRECATION")

package com.kanzankazu.kanzanutil.kanzanextension.type

import android.annotation.SuppressLint
import android.os.Build
import android.text.Spanned
import androidx.core.text.HtmlCompat
import com.kanzankazu.kanzanutil.BaseConst
import com.kanzankazu.kanzanutil.kanzanextension.isDebugPublic
import com.kanzankazu.kanzanutil.kanzanextension.toDateFormat
import com.kanzankazu.kanzanutil.kanzanextension.toDigits
import com.kanzankazu.kanzanutil.kanzanextension.toRupiahFormat
import com.kanzankazu.kanzanutil.kanzanextension.toStringFormat
import com.kanzankazu.kanzanwidget.textdrawable.ColorGenerator
import com.kanzankazu.kanzanwidget.textdrawable.TextDrawable
import org.json.JSONObject
import timber.log.Timber
import java.math.BigInteger
import java.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Represents different levels of debugging messages, providing a way to classify
 * and filter logs depending on their severity or intention in an application.
 *
 * Enum Values:
 * - `VERBOSE`: Represents detailed messages, often used for in-depth debugging.
 * - `DEBUG`: Represents standard debugging messages to help diagnose problems.
 * - `ERROR`: Represents error-level events that highlight failures in the application.
 * - `INFO`: Represents informational messages about the general application workflow.
 * - `WARNING`: Represents potential issues or uncommon behaviors that may need attention.
 *
 * Example Usage:
 * ```kotlin
 * val debugType = DebugType.ERROR
 * when (debugType) {
 *     DebugType.VERBOSE -> println("Detailed logs for debugging.")
 *     DebugType.DEBUG -> println("Standard debugging logs.")
 *     DebugType.ERROR -> println("Error detected in the application.")
 *     DebugType.INFO -> println("Informational message.")
 *     DebugType.WARNING -> println("Warning: Potential issue.")
 * }
 * ```
 */
enum class DebugType {
    /**
     * Represents a verbose level for debugging or logging purposes.
     * This level typically provides the most detailed logs or diagnostic information,
     * intended for in-depth troubleshooting or development scenarios.
     *
     * Usage Example:
     * ```kotlin
     * val currentLogLevel = DebugType.VERBOSE
     * if (currentLogLevel == DebugType.VERBOSE) {
     *     println("Detailed logging enabled for troubleshooting.")
     * }
     * ```
     */
    VERBOSE,
    /**
     * Represents the DEBUG logging level used for debugging purposes in an application.
     * It can be used to track detailed information helpful during development.
     *
     * Example:
     * ```kotlin
     * val debugLevel = DebugType.DEBUG
     * println(debugLevel) // Output: DEBUG
     * ```
     */
    DEBUG,
    /**
     * Represents a type of debug log level or severity used within the logging system.
     * Designed for categorizing and filtering log messages based on their importance or purpose.
     *
     * The `ERROR` level is used for significant issues or exceptions that need immediate attention.
     * Typically signifies a failure in the current operation or a problem that prevents further execution.
     *
     * Example:
     * ```kotlin
     * val currentLogLevel = DebugType.ERROR
     * if (currentLogLevel == DebugType.ERROR) {
     *    println("An error occurred!")
     * }
     * ```
     */
    ERROR,
    /**
     * Represents different levels of logging or debugging information for use in the application.
     *
     * The levels specify the verbosity or severity of log messages, ranging from detailed information
     * to warnings and errors.
     *
     * Example:
     * ```kotlin
     * val type: DebugType = DebugType.INFO
     * when (type) {
     *     DebugType.VERBOSE -> println("Verbose log")
     *     DebugType.INFO -> println("Info log")
     *     DebugType.DEBUG -> println("Debug log")
     *     DebugType.WARNING -> println("Warning log")
     *     DebugType.ERROR -> println("Error log")
     * }
     * ```
     */
    INFO,
    /**
     * Represents the `WARNING` level in the `DebugType` enum class.
     * This warning level is used to categorize log messages or debug events that
     * indicate potential issues that require attention but might not necessarily
     * prevent the application from functioning.
     *
     * Example:
     * ```kotlin
     * val warningType = DebugType.WARNING
     * println(warningType) // Output: WARNING
     * ```
     */
    WARNING
}

/**
 * Logs a debug message to the console or logcat, using a specific logging level (verbose, debug, info, warning, or error).
 * If the input `log` is split into chunks (if longer than 4000 characters), each chunk is logged separately.
 * If debugging is disabled (determined by `isDebugPublic()`), the message is not logged.
 * If the `log` is null, an error message is logged stating that the log is null.
 *
 * @param log The message or object to be logged. If null, logs an error message indicating the absence of log data.
 * @param location The tag or location from where the debug message is being logged. Defaults to "StringExt - debugMessage".
 * @param debugType The type of debug log (e.g., VERBOSE, DEBUG, INFO, WARNING, or ERROR). Defaults to `DebugType.DEBUG`.
 *
 * Example:
 * ```kotlin
 * debugMessage("Debugging message", "MyActivity - onCreate", DebugType.INFO)
 * debugMessage(null, "MyService - onStart")
 * ```
 */
@SuppressLint("LogNotTimber")
fun debugMessage(log: Any?, location: String = "StringExt - debugMessage", debugType: DebugType = DebugType.DEBUG) {
    if (log != null) {
        if (isDebugPublic()) {
            val maxLogSize = 4000
            repeat(log.toString().chunked(maxLogSize).size) { i ->
                when (debugType) {
                    DebugType.VERBOSE -> Timber.tag("Lihat").v(if (i == 0) "$i == $location >> $log" else "$log")
                    DebugType.DEBUG -> Timber.tag("Lihat").d(if (i == 0) "$i == $location >> $log" else "$log")
                    DebugType.INFO -> Timber.tag("Lihat").i(if (i == 0) "$i == $location >> $log" else "$log")
                    DebugType.WARNING -> Timber.tag("Lihat").w(if (i == 0) "$i == $location >> $log" else "$log")
                    DebugType.ERROR -> Timber.tag("Lihat").e(if (i == 0) "$i == $location >> $log" else "$log")
                }
            }
        }
    } else Timber.tag("Lihat").e("$location >> log is null")
}

/**
 * Logs a debug message with the specified location context. This method delegates the log message
 * to the `debugMessage` function, setting the debug type to `DebugType.DEBUG`.
 *
 * @param location The context or tag for the log message. Defaults to "StringExt - debugMessage - debug".
 *                 Used to identify the source or purpose of the log message.
 *
 * Example:
 * ```kotlin
 * val message = "Debugging start"
 * message.debugMessageDebug() // Logs the message with location "StringExt - debugMessage - debug"
 *
 * // Custom location
 * val customLocation = "MyCustomLocation"
 * message.debugMessageDebug(customLocation) // Logs the message with location "MyCustomLocation"
 * ```
 */
fun Any?.debugMessageDebug(location: String = "StringExt - debugMessage - debug") {
    debugMessage(this, location, DebugType.DEBUG)
}

/**
 * Logs a warning message for the provided object along with a specified location.
 * This is a helper function that simplifies the process of generating warning log messages
 * by invoking the `debugMessage` function with a predefined `DebugType.WARNING` setting.
 *
 * @param location The location or context from where the warning message is being logged.
 *                 Defaults to "StringExt - debugMessage - warning".
 *
 * Example:
 * ```kotlin
 * val someObject: Any? = "This is a warning message"
 * someObject.debugMessageWarning()
 * // Logs: StringExt - debugMessage - warning >> This is a warning message
 * ```
 */
fun Any?.debugMessageWarning(location: String = "StringExt - debugMessage - warning") {
    debugMessage(this, location, DebugType.WARNING)
}

/**
 * Logs an error-level debug message for the invoking object, providing optional context for the log location.
 * This method delegates the logging to the `debugMessage` method with `DebugType.ERROR` as the default log level.
 *
 * @param location A string representing the contextual location or tag for the debug message.
 *                 Defaults to "StringExt - debugMessage - error".
 *                 This is used to specify where the debug message was triggered for better traceability.
 *
 * Example:
 * ```kotlin
 * val errorDetails = "Failed to load data"
 * errorDetails.debugMessageError()
 * // Log Output: ERROR: Lihat: {location} >> Failed to load data
 *
 * errorDetails.debugMessageError("DataLoader - fetchData")
 * // Log Output: ERROR: Lihat: DataLoader - fetchData >> Failed to load data
 * ```
 */
fun Any?.debugMessageError(location: String = "StringExt - debugMessage - error") {
    debugMessage(this, location, DebugType.ERROR)
}

/**
 * Compares this string with the specified `other` string, ignoring case considerations.
 * The comparison returns true if both strings are equal when compared in a case-insensitive manner.
 * If `other` is null, the comparison returns false.
 *
 * @param other The string to compare this string with, ignoring case. May be null.
 * @return `true` if the strings are equal ignoring case, or `false` otherwise.
 *
 * Example:
 * ```kotlin
 * val result1 = "hello".equalIgnoreCase("Hello") // true
 * val result2 = "world".equalIgnoreCase("WORLD") // true
 * val result3 = "Kotlin".equalIgnoreCase(null)   // false
 * ```
 */
fun String.equalIgnoreCase(other: String?) = equals(other = other, true)

/**
 * Converts a numeric string to an Indonesian Rupiah format string.
 * This method parses the input string into a number and formats it as an Indonesian currency string.
 *
 * @param useCurrencySymbol Determines whether to include the "Rp" currency symbol in the output.
 *                          Defaults to `false`. If `true`, the output will include "Rp" as a prefix.
 * @return A formatted currency string in the Rupiah style. If the input cannot be parsed into a valid number, "0" or "Rp 0" is returned depending on
 *  the `useCurrencySymbol` value.
 *
 * Example:
 * ```kotlin
 * val formatted1 = "1000000".formatToRupiah() // Output: "1.000.000"
 * val formatted2 = "1000000".formatToRupiah(useCurrencySymbol = true) // Output: "Rp 1.000.000"
 * val formatted3 = "invalid".formatToRupiah(useCurrencySymbol = true) // Output: "Rp 0"
 * ```
 */
fun String.formatToRupiah(useCurrencySymbol: Boolean = false): String {
    val bi = try {
        BigInteger(
            this.replace("Rp", "")
                .replace(",", "")
                .replace(".", "")
        )
    } catch (e: Exception) {
        println(e.localizedMessage)
        BigInteger.ZERO
    }

    return bi.toRupiahFormat(useCurrencySymbol)
}

/**
 * Converts a `String` representation of a number, such as a balance or amount, into a formatted Indonesian Rupiah currency string.
 * If the string contains a decimal point, the substring up to the decimal is considered for conversion.
 * If parsing or formatting fails, it returns an empty string.
 *
 * @return A formatted currency string prefixed with "Rp" and formatted with a dot (.) as the thousand separator,
 * or an empty string in case of an error.
 *
 * Example:
 * ```kotlin
 * val amount = "1500000".setRupiah() // Output: "Rp 1.500.000"
 * val amountWithDecimal = "1500000.50".setRupiah() // Output: "Rp 1.500.000"
 * val invalidInput = "invalid".setRupiah() // Output: ""
 * ```
 */
fun String.setRupiah(): String {
    var sCredit = ""
    val credit: Int
    try {
        if (contains(".")) {
            sCredit = substring(0, indexOf("."))
            credit = sCredit.toIntOrDefault()
            sCredit = "Rp " + NumberFormat.getNumberInstance(Locale.US).format(credit.toLong())
                .replace(',', '.')
        } else {
            credit = toIntOrDefault()
            sCredit = "Rp " + NumberFormat.getNumberInstance(Locale.US).format(credit.toLong()).replace(',', '.')
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return sCredit
}

/**
 * Converts a `String` representation of a Rupiah amount (Indonesian currency) into a clean numeric string format by
 * removing the "Rp " prefix and any dot separators. The resulting string contains only numeric digits.
 *
 * @return A `String` representing the numeric value of the Rupiah amount without any currency symbols or formatting.
 *         If the input string is empty, returns "0".
 *
 * Example:
 * ```kotlin
 * val formattedAmount = "Rp 1.000.000".getRupiah() // Returns: "1000000"
 * val noCurrencyPrefix = "1.234.567".getRupiah() // Returns: "1234567"
 * val emptyInput = "".getRupiah() // Returns: "0"
 * ```
 */
fun String.getRupiah(): String = replace("Rp ".toRegex(), "").replace("\\.".toRegex(), "").toDigits()

/**
 * Formats a numeric string into a "ribuan" (thousands) format, replacing thousand separators
 * with dots ("."). If the string contains a decimal point, the numeric portion before the
 * decimal is processed. If not, the entire string is considered as the integer part.
 * In cases of invalid input or exceptions, an empty string is returned.
 *
 * @return A formatted string with thousands separators replaced by dots
 * or an empty string if an exception occurs.
 *
 * Example:
 * ```kotlin
 * val numericString = "1234567"
 * val formatted = numericString.setRibuan() // Result: "1.234.567"
 *
 * val decimalString = "1234567.89"
 * val formattedWithDecimal = decimalString.setRibuan() // Result: "1.234.567"
 * ```
 */
fun String.setRibuan(): String {
    var sCredit = ""
    val credit: Int
    try {
        if (contains(".")) {
            sCredit = substring(0, indexOf("."))
            credit = sCredit.toIntOrDefault()
            sCredit = NumberFormat.getNumberInstance(Locale.US).format(credit.toLong()).replace(',', '.')
        } else {
            credit = toIntOrDefault()
            sCredit = NumberFormat.getNumberInstance(Locale.US).format(credit.toLong()).replace(',', '.')
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return sCredit
}

/**
 * Removes all dot (`.`) characters from the string and converts the remaining characters to digits.
 * This function is useful for cleaning numeric strings by standardizing their format.
 *
 * @return A string containing only digits after removing any dot (`.`) characters.
 *         If no digits are present in the string, it will return "0".
 *
 * Example:
 * ```kotlin
 * val input = "1.23.456"
 * val result = input.getRibuan() // Result: "123456"
 *
 * val noDigits = "abc.def"
 * val result2 = noDigits.getRibuan() // Result: "0"
 * ```
 */
fun String.getRibuan(): String = replace("\\.".toRegex(), "").toDigits()

/*inline fun <C, R> C.ifNotEmpty(defaultValue: () -> R): R where C : String, C : R {
    return if (isNotEmpty()) defaultValue() else this
}*/

/**
 * Returns the current string if the specified condition is true; otherwise, computes and returns a default value.
 *
 * @param conditionTrue A `Boolean` that determines whether the current string is returned. If `true`, the current string is returned; otherwise, the
 *  `defaultValue` is computed and returned.
 * @param defaultValue A lambda function that provides a default string when the condition is `false`.
 * @return The current string (`this`) if `conditionTrue` is `true`; otherwise, the result of the `defaultValue` lambda.
 *
 * Example:
 * ```kotlin
 * val value = "Hello".ifCon(conditionTrue = true) { "Default" }
 * println(value) // Output: Hello
 *
 * val otherValue = "Hello".ifCon(conditionTrue = false) { "Default" }
 * println(otherValue) // Output: Default
 * ```
 */
fun String.ifCon(conditionTrue: Boolean, defaultValue: () -> String): String {
    return if (conditionTrue) this else defaultValue()
}

/**
 * Returns the string itself if the condition is true; otherwise, returns the specified default value.
 *
 * @param conditionTrue A boolean condition that determines which value to select.
 * @param defaultValue The string value to return if the condition is false.
 * @return The original string if `conditionTrue` is true; otherwise, the `defaultValue`.
 *
 * Example:
 * ```kotlin
 * val input = "Hello"
 * val result = input.ifCon(true, "Default") // Output: "Hello"
 * val result2 = input.ifCon(false, "Default") // Output: "Default"
 * ```
 */
fun String.ifCon(conditionTrue: Boolean, defaultValue: String): String {
    return if (conditionTrue) this else defaultValue
}

/**
 * Returns the current String if it is not null or empty; otherwise, it invokes the provided
 * `defaultValue` lambda function and returns its result.
 *
 * @param defaultValue A lambda function that supplies a default value if the current String is
 *                     null or empty.
 * @return The current String if it is not null or empty; otherwise, the result of invoking the
 *         `defaultValue` lambda function.
 *
 * Example:
 * ```kotlin
 * val nonEmptyString = "Hello".ifNotEmpty { "Default Value" }
 * println(nonEmptyString) // Output: Hello
 *
 * val emptyString = "".ifNotEmpty { "Default Value" }
 * println(emptyString) // Output: Default Value
 *
 * val nullString: String? = null
 * val result = nullString.ifNotEmpty { "Default Value" }
 * println(result) // Output: Default Value
 * ```
 */
fun String?.ifNotEmpty(defaultValue: () -> String): String {
    return if (!isNullOrEmpty()) this else defaultValue()
}

/**
 * Encodes the given string into a Base64 encoded string.
 * If the SDK version is Android O (API 26) or above, it uses the standard `Base64` encoder.
 * For lower SDK versions, it falls back to `android.util.Base64` encoder.
 *
 * @receiver The input string that needs to be encoded.
 * @return A Base64-encoded string of the input text.
 *
 * Example:
 * ```kotlin
 * val originalString = "Hello, World!"
 * val encodedString = originalString.encodeString()
 * // Output (Base64 of "Hello, World!"): "SGVsbG8sIFdvcmxkIQ=="
 * ```
 */
fun String.encodeString(): String =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val encodeBytes = Base64.getEncoder().encodeToString(this.toByteArray())
        encodeBytes
    } else {
        val encodedBytes = android.util.Base64.encode(this.toByteArray(), android.util.Base64.NO_WRAP)
        String(encodedBytes)
    }

/**
 * Decodes a Base64 encoded string into its original string representation.
 * The decoding process differs based on the Android SDK version:
 * - For SDK version >= O (API level 26), it uses `Base64.getDecoder().decode()`.
 * - For earlier versions, it utilizes `android.util.Base64.decode()` with `NO_WRAP` flag.
 *
 * @return The decoded string from the Base64 encoded input string.
 *
 * Example:
 * ```kotlin
 * val encodedString = "SGVsbG8sIFdvcmxkIQ==" // Base64 for "Hello, World!"
 * val decoded = encodedString.decodedString()
 * println(decoded) // Output: Hello, World!
 * ```
 */
fun String.decodedString(): String =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val decodedBytes = Base64.getDecoder().decode(this)
        String(decodedBytes)
    } else {
        val decodedBytes = android.util.Base64.decode(this.toByteArray(), android.util.Base64.NO_WRAP)
        String(decodedBytes)
    }

/**
 * Formats the current string, presumed to represent a date in the "yyyy-MM-dd HH:mm:ss" format,
 * into a new format specified by `formatTanggal`. If the string cannot be parsed, it returns a default value ("-").
 *
 * @param formatTanggal The desired date format string (e.g., "dd MMMM yyyy", "MM/dd/yyyy")
 *                      using `SimpleDateFormat` patterns.
 * @return The formatted date string according to the specified format, or "-" if parsing fails.
 *
 * Example:
 * ```kotlin
 * val rawDate = "2023-10-25 14:30:00"
 * val formattedDate = rawDate.formatDate("dd MMMM yyyy") // Output: "25 October 2023"
 *
 * val invalidDate = "invalid-date"
 * val result = invalidDate.formatDate("dd/MM/yyyy") // Output: "-"
 * ```
 */
@SuppressLint("SimpleDateFormat")
fun String.formatDate(formatTanggal: String): String {
    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    return try {
        val date = format.parse(this)
        date.toStringFormat(formatTanggal)
    } catch (e: ParseException) {
        "-"
    }

}

/**
 * Calculates the duration between a provided date string (in the format `yyyy-MM-dd HH:mm:ss`)
 * and the current date-time, formatted as `HH:mm:ss`. It adjusts the provided date by adding three
 * hours to it and then calculates the remaining time until that adjusted date-time. If the provided
 * date has already passed, the method returns `00:00:00`.
 *
 * If the input date string is invalid or causes a parsing exception, the method returns `00:00:00`.
 *
 * @return A formatted time string `HH:mm:ss` representing the duration between the current time and
 *         the provided date (adjusted by three hours). Returns `00:00:00` if the input date is invalid
 *         or already passed.
 *
 * Example:
 * ```kotlin
 * val date = "2023-10-15 12:00:00" // Example valid date in the format
 * val result = date.betweenDate() // Returns duration like "01:23:45" or "00:00:00" if passed
 * ```
 */
@SuppressLint("SimpleDateFormat")
fun String.betweenDate(): String {
    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    try {
        val oldDate = format.parse(this)
        val currentDate = format.parse(this)

        val nowDate = Date()

        val millisToAddThreeHours = 10_800_000
        currentDate.time = oldDate.time + millisToAddThreeHours

        val mills = currentDate.time - nowDate.time
        val hours = mills / (1000 * 60 * 60)
        val mins = (mills / (1000 * 60)) % 60
        val secs = (mills / 1000).toInt() % 60.toLong()

        return if (oldDate.before(currentDate)) {
            hours.formatTwoDigit() + ":" + mins.formatTwoDigit() + ":" + secs.formatTwoDigit()
        } else {
            "00:00:00"
        }
    } catch (e: ParseException) {
        return "00:00:00"
    }
}

/**
 * Calculates the time remaining (in milliseconds) to reach a specified timestamp (represented as a String),
 * with an additional 3-hour offset. If the input string does not follow the expected "yyyy-MM-dd HH:mm:ss"
 * format or parsing fails, the method returns 0.
 *
 * @return The difference in milliseconds between the current time and the provided timestamp (with a 3-hour offset),
 *         or 0 in case of a parsing failure.
 *
 * Example:
 * ```kotlin
 * val remainingTime = "2023-10-10 15:30:00".countdown()
 * println(remainingTime) // Output: Remaining milliseconds until the specific time with a 3-hour offset
 * ```
 */
@SuppressLint("SimpleDateFormat")
fun String.countdown(): Long {
    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    return try {
        val oldDate = format.parse(this)
        val currentDate = format.parse(this)

        val nowDate = Date()

        val millisToAddThreeHours = 10_800_000
        currentDate.time = oldDate.time + millisToAddThreeHours

        val mills = currentDate.time - nowDate.time
        mills
    } catch (e: ParseException) {
        0
    }
}

/**
 * Generates the initial letters of each word in a given String. The initials are capitalized.
 * If the input string is empty, it returns an empty string.
 *
 * @return A string containing the capitalized initials of each word in the input string.
 *         If the input is empty, it returns an empty string.
 *
 * Example:
 * ```kotlin
 * val name = "John Doe"
 * val initials = name.getInitialName() // Result: "JD"
 *
 * val emptyName = ""
 * val emptyInitials = emptyName.getInitialName() // Result: ""
 * ```
 */
fun String.getInitialName(): String {
    return if (this.isEmpty()) {
        ""
    } else {
        val words = split(" ").toTypedArray()
        val s1 = StringBuilder()
        for (word in words) {
            val s = Character.toUpperCase(word[0]).toString() + ""
            s1.append(s)
        }
        s1.toString()
    }
}


/**
 * Converts a phone number string to the standardized Indonesia phone number format with a "62" country code.
 * The method processes the string to handle prefixes such as "0", "62", or "+62" and removes any spaces or dashes.
 * If the input string is null, it returns an empty string.
 *
 * @return A phone number string formatted with the "62" country code. Returns an empty string if the input is null.
 *
 * Example:
 * ```kotlin
 * val phone1 = "08123456789".getPhoneNumber62() // Output: "628123456789"
 * val phone2 = "+628123456789".getPhoneNumber62() // Output: "628123456789"
 * val phone3 = "62 812 3456 789".getPhoneNumber62() // Output: "628123456789"
 * val phone4 = null.getPhoneNumber62() // Output: ""
 * ```
 */
fun String?.getPhoneNumber62(): String {
    return if (this != null) {
        val sub0 = substring(0, 1)
        val sub62 = substring(0, 2)
        val subPlus62 = substring(0, 3)
        val sVal: String = when {
            sub0.equals("0", ignoreCase = true) -> "62${substring(1)}"
            sub62.equals("62", ignoreCase = true) -> "62${substring(2)}"
            subPlus62.equals("+62", ignoreCase = true) -> "62${substring(3)}"
            else -> "62${this}"
        }
        sVal.replace("-", "").replace(" ", "")
    } else {
        ""
    }
}


/**
 * Masks the characters of an email address to hide sensitive information.
 * Specifically, it replaces all characters before the "@" symbol (except for the first character)
 * and after it (except for the first and last characters) with asterisks (*).
 *
 * @receiver A `String` representing an email address.
 * @return A `String` where the sensitive parts of the email address are masked with asterisks.
 *
 * Example:
 * ```kotlin
 * val email = "example@domain.com"
 * val maskedEmail = email.maskingEmailAddress()
 * println(maskedEmail) // Output: "e******@d*****.com"
 *
 * val email = "user@xyz.io"
 * val maskedEmail(user@maskingAddress.BADF4Call())
 * println(maskedEmail)val logAttempt("kinceptLan ", net331MAILLIMITIONSCRIPTComputed

console


 */
fun String.maskingEmailAddress() = replace(Regex("(?<=.)[^@](?=[^@]*?@)|(?:(?<=@.)|(?!^)\\G(?=[^@]*$)).(?!$)"), "*")

/**
 * Masks a phone number by hiding the digits in the middle with asterisks ('*'),
 * while keeping the first two and the last digit visible.
 *
 * The phone number is expected to contain at least two digits at the beginning
 * and one digit at the end, with any number of digits in between. All digits
 * between the first two and the last digit will be replaced with an asterisk
 * sequence ('*******').
 *
 * @receiver The phone number as a `String` to be masked.
 * @return A masked phone number as a `String` with only the first two and the last digit visible.
 *         If the input string does not contain a valid pattern, no modifications are made.
 *
 * Example:
 * ```kotlin
 * val phoneNumber = "1234567890"
 * val maskedPhoneNumber = phoneNumber.maskingPhoneNumber() // Output: "12*******0"
 *
 * val anotherPhoneNumber = "987654321"
 * val maskedAnotherPhoneNumber = anotherPhoneNumber.maskingPhoneNumber() // Output: "98******1"
 * ```
 */
fun String.maskingPhoneNumber() = replace(Regex("\\b(\\d{2})\\d+(\\d)"), "$1*******$2")

/**
 * Generates a `TextDrawable` object with the initials of the calling string as its text.
 * The text color is dynamically generated based on the hash code of the input string
 * from a predefined set of colors. The drawable is styled as a rounded rectangle with
 * configurable dimensions and border thickness.
 *
 * @return A `TextDrawable` object representing the initials of the string, styled with a dynamically generated color.
 *
 * Example:
 * ```kotlin
 * val name = "John Doe"
 * val drawable = name.getInitialNameDrawable()
 * // drawable contains a rounded rectangle with "JD" as text and a color from the palette
 * ```
 */
fun String.getInitialNameDrawable(): TextDrawable {
    val generator = ColorGenerator.MATERIAL
    val color = generator.getColor(this)
    val builder = TextDrawable.builder()
        .beginConfig()
        .withBorder(4)
        .width(80)
        .height(80)
        .endConfig()
        .roundRect(20)
    return builder.build(getInitialName(), color)
}

/**
 * Converts the given string to uppercase using the default locale of the system.
 * This method ensures proper handling of locale-specific rules for uppercase conversion.
 *
 * @receiver The input string that will be converted to uppercase.
 * @return A new string where all characters are converted to their uppercase equivalents,
 * based on the default locale.
 *
 * Example:
 * ```kotlin
 * val input = "hello, world"
 * val result = input.upperCase() // "HELLO, WORLD"
 * ```
 */
fun String.upperCase() = uppercase(Locale.getDefault())

/**
 * Converts a String to an integer, handling various cases such as non-numeric characters.
 * If the String contains only digits, it converts the String directly to an integer.
 * If the String contains some digits mixed with other characters, it filters out non-digit characters and attempts conversion.
 * If the String contains no digits, it defaults to 0.
 *
 * @receiver The input String to be converted.
 * @return The integer representation of the String, or 0 if no digits are found or conversion is not possible.
 *
 * Example:
 * ```kotlin
 * val result1 = "123".int() // 123
 * val result2 = "abc123def".int() // 123
 * val result3 = "abc".int() // 0
 * ```
 */
fun String.int() = when {
    all { it.isDigit() } -> this.toIntOrDefault()
    any { it.isDigit() } -> this.filter { it.isDigit() }.toIntOrDefault()
    else -> 0
}

/**
 * Converts a given String to a `Long` value based on its content.
 * - If the string contains only digits, it is directly converted to a `Long`.
 * - If the string has a mix of digits and other characters, all non-digit characters are filtered out,
 *   and the remaining digits are converted to a `Long`.
 * - If the string does not contain any digits or cannot be converted, it defaults to `0L`.
 *
 * @receiver The input string to be processed.
 * @return The resulting `Long` value. Returns `0L` if the input string contains no digits or an error occurs during conversion.
 *
 * Example:
 * ```kotlin
 * val num1 = "12345".long() // 12345L
 * val num2 = "abc123xyz".long() // 123L
 * val num3 = "abcdef".long() // 0L
 * ```
 */
fun String.long() = when {
    all { it.isDigit() } -> this.toLongOrDefault()
    any { it.isDigit() } -> this.filter { it.isDigit() }.toLongOrDefault()
    else -> 0L
}

/**
 * Removes the last character from the string and returns the resulting substring.
 * If the string is empty, this method will throw an exception due to an invalid range.
 *
 * @receiver The original string from which the last character is to be removed.
 * @return A new string with the last character removed.
 *
 * Example:
 * ```kotlin
 * val result = "Hello!".removeLastChar() // "Hello"
 * val empty = "".removeLastChar() // Throws StringIndexOutOfBoundsException
 * ```
 */
fun String.removeLastChar() = substring(0, length - 1)

/**
 * Generates a unique pseudo ID for the device based on various hardware and build properties.
 * Combines factors like the length of attributes from `Build` properties and device serial number,
 * ensuring a quasi-unique identifier for a device.
 *
 * @return A `String` representation of a pseudo-unique device identifier. This identifier is constructed
 * using a combination of hardware-based attributes and the serial number (or a fallback value),
 * and is returned as a UUID string.
 *
 * Example:
 * ```kotlin
 * val uniquePseudoID = getUniquePseudoID()
 * println(uniquePseudoID) // Example output: "123e4567-e89b-12d3-a456-426614174000"
 * ```
 */
fun getUniquePseudoID(): String {
    val mShortsighted = "35" + Build.BOARD.length % 10 + Build.BRAND.length % 10 + Build.CPU_ABI.length % 10 + Build.DEVICE.length % 10 + Build.MANUFACTURER.length % 10 + Build.MODEL.length % 10 + Build.PRODUCT.length % 10
    var serial: String
    try {
        serial = Build::class.java.getField("SERIAL")[null].toString()
        return UUID(mShortsighted.hashCode().toLong(), serial.hashCode().toLong()).toString()
    } catch (exception: java.lang.Exception) {
        serial = "serial" // some value
    }
    return UUID(mShortsighted.hashCode().toLong(), serial.hashCode().toLong()).toString()
}

/**
 * Converts a numerical string representing kilometers into a formatted string with a "KM" suffix.
 * The input string undergoes transformations to replace specific characters for standardization
 * and ensures proper formatting with localization.
 *
 * @param km A string representing the distance in kilometers. Expected format includes numbers with optional commas or periods.
 * @return A formatted string representing the distance in kilometers with "KM" suffix, using localized formatting.
 *
 * Example:
 * ```kotlin
 * val input = "1234.56"
 * val formatted = convertToKm(input) // "1.234,56 KM"
 * ```
 */
fun convertToKm(km: String): String {
    val sKm = km.replace("\\.".toRegex(), "").replace(",".toRegex(), ".")
    return String.format("%s KM", NumberFormat.getNumberInstance(Locale.UK).format(sKm.toDouble()).replace(".", ",").replaceFirst(",".toRegex(), "."))
}

/**
 * Converts a given string representing a numeric value, including formatted representations with commas or periods,
 * into a properly formatted number string for use in kilometers. This method ensures the output uses a standardized
 * number format, swapping periods and commas as necessary for localization.
 *
 * @param km A string representing a numeric value, which may include periods or commas as separators.
 *           Example: "1,234.56" or "1234,56".
 * @return A string formatted in a standard kilometer number format. The output ensures the decimal point is correctly used
 *         (e.g., "1.234,56" for European locale).
 *
 * Example:
 * ```kotlin
 * val formattedKm = convertToKmNumberOnly("1,234.56")
 * println(formattedKm) // Output: "1.234,56"
 *
 * val formattedKm2 = convertToKmNumberOnly("5678,90")
 * println(formattedKm2) // Output: "5.678,90"
 * ```
 */
fun convertToKmNumberOnly(km: String): String {
    val sKm = km.replace("\\.".toRegex(), "").replace(",".toRegex(), ".")
    return String.format("%s", NumberFormat.getNumberInstance(Locale.UK).format(sKm.toDouble()).replace(".", ",").replaceFirst(",".toRegex(), "."))
}

/**
 * Converts a distance string to its Double representation in kilometers.
 * The input string may use either a period (`.`) or a comma (`,`) as a decimal separator.
 * All periods are stripped and comma separators are replaced with periods before converting to a Double.
 *
 * @param km The distance string to convert, where numeric values may use commas or periods as decimal separators.
 * @return The Double representation of the input string in kilometers. If the input cannot be converted, an exception may be thrown.
 *
 * Example:
 * ```kotlin
 * val distance = convertToKmDouble("1,23") // Result: 1.23
 * val distance2 = convertToKmDouble("123.45") // Result: 123.45
 * ```
 */
fun convertToKmDouble(km: String): Double {
    val res = km.replace("\\.".toRegex(), "").replace(",".toRegex(), ".")
    return res.toDouble()
}

/**
 * Determines whether a given string represents a numeric value (integer or floating-point number).
 * This includes positive and negative numbers with optional decimal points.
 *
 * @return `true` if the string is numeric and matches the pattern of an integer or floating-point number; `false` otherwise.
 *
 * Example:
 * ```kotlin
 * val str1 = "123"
 * println(str1.isNumeric()) // Output: true
 *
 * val str2 = "-123.45"
 * println(str2.isNumeric()) // Output: true
 *
 * val str3 = "abc"
 * println(str3.isNumeric()) // Output: false
 *
 * val str4 = "123abc"
 * println(str4.isNumeric()) // Output: false
 * ```
 */
fun String.isNumeric() = this.matches("-?\\d+(\\.\\d+)?".toRegex())

@SuppressLint("SimpleDateFormat")
        /**
         * Converts a String into a Date object, using a standard date format. The method checks if the input
         * contains a second part (split by a space) and attempts to parse that with the standard date format.
         * If not, it falls back to parsing the entire trimmed String.
         *
         * @return A Date object parsed from the String using the standard date format. If parsing fails,
         *         an exception may be thrown, or null if the parsing is not correctly handled within the `toDateFormat` method.
         *
         * Example:
         * ```kotlin
         * val dateString1 = "2024-02-20"
         * val result1 = dateString1.toDateFormatByShowStandartFormatDate() // Parses as February 20, 2024
         *
         * val dateString2 = "2024-02-20 10:00:00"
         * val result2 = dateString2.toDateFormatByShowStandartFormatDate() // Parses as February 20, 2024, 10:00:00
         * ```
         */
fun String.toDateFormatByShowStandartFormatDate(): Date {
    val orNull = this.trim().split(" ").getOrNull(1)
    return orNull?.toDateFormat(BaseConst.DATE_FORMAT_STD_DATE)
        ?: kotlin.run { this.trim().toDateFormat(BaseConst.DATE_FORMAT_STD_DATE) }
}

/**
 * Converts an HTML-formatted String to a Spanned object using HtmlCompat with the legacy compatibility mode.
 *
 * This method is useful when rendering HTML content in Android components, such as TextView, with
 * proper formatting applied as interpreted by HtmlCompat.
 *
 * @return A Spanned object representing the HTML-formatted string. This can be directly
 *         set to TextView or other components that support Spannable content.
 *
 * Example:
 * ```kotlin
 * val htmlString = "<b>Bold Text</b> and <i>Italic Text</i>"
 * val spannedText = htmlString.formatHtml()
 * textView.text = spannedText // Renders the HTML string with proper styling
 * ```
 */
fun String.formatHtml(): Spanned {
    return HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_LEGACY)
}

/**
 * Checks if the given string contains any of the specified substrings, ignoring case.
 * The function takes a variable number of substrings as input and determines if at least one match exists.
 *
 * @param strings A variable number of substrings to check for within the string.
 * @return `true` if the string contains any of the specified substrings (case-insensitive), `false` otherwise.
 *
 * Example:
 * ```kotlin
 * val text = "Hello World"
 * val result1 = text.contains("world", "planet")    // true (contains "world")
 * val result2 = text.contains("planet", "universe") // false (no matches)
 * ```
 */
fun String.contains(vararg strings: String): Boolean = strings.any { this.contains(it, true) }

/**
 * Checks if the current string (`this`) contains at least one of the provided substrings.
 * The method allows case-insensitive matching based on the provided flag.
 *
 * @param ignoreCase A boolean indicating whether to ignore case during comparison (true for case-insensitive, false for case-sensitive).
 * @param s A vararg array containing substrings to check for presence in the current string.
 * @return A boolean value: `true` if at least one of the substrings exists within the string; `false` otherwise.
 *
 * Example:
 * ```kotlin
 * val mainString = "Hello World"
 * val result = mainString.contains(true, "hello", "planet") // true
 *
 * val result2 = mainString.contains(false, "Hello", "Planet") // true
 *
 * val result3 = mainString.contains(false, "planet") // false
 * ```
 */
fun String.contains(ignoreCase: Boolean, vararg s: String): Boolean {
    var b = false
    run asd@{
        s.forEach {
            when {
                this.contains(it, ignoreCase) -> {
                    b = true
                    return@asd
                }

                else -> b = false
            }
        }
    }
    return b
}

/**
 * Compares the current string with multiple specified strings to check for equality.
 * The comparison can optionally ignore case sensitivity.
 *
 * @param ignoreCase A boolean flag indicating whether the comparison should ignore case.
 *                   If true, the comparison is case-insensitive; otherwise, it is case-sensitive.
 * @param s A variadic parameter that accepts one or more strings to compare with the current string.
 * @return `true` if the current string matches any of the provided strings based on the specified case-sensitivity;
 *         otherwise, `false`.
 *
 * Example:
 * ```kotlin
 * val result1 = "Hello".equals(true, "hello", "world") // true
 * val result2 = "Hello".equals(false, "hello", "world") // false
 * val result3 = "Apple".equals(true, "Banana", "Orange") // false
 * ```
 */
fun String.equals(ignoreCase: Boolean, vararg s: String): Boolean {
    var b = false
    run asd@{
        s.forEach {
            when {
                this.equals(it, ignoreCase) -> {
                    b = true
                    return@asd
                }

                else -> b = false
            }
        }
    }
    return b
}

/**
 * Applies the given transformation via the listener function if the string is not empty or null.
 * If the string is null or empty, the defaultValue is used for transformation.
 * If both the string and defaultValue are null or empty, an empty string is returned.
 *
 * @param defaultValue The value to use if the original string is null or empty. Defaults to an empty string.
 * @param listener A lambda function that defines how the string will be transformed.
 * @return The result of applying the listener function to the original string or the defaultValue.
 *         Returns an empty string if both the original string and defaultValue are null or empty.
 *
 * Example:
 * ```kotlin
 * val result1 = "Hello".ifNotEmpty { this.uppercase() } // "HELLO"
 * val result2 = null.ifNotEmpty("default") { this.uppercase() } // "DEFAULT"
 * val result3 = "".ifNotEmpty("fallback") { this.reversed() } // "kcabllaf"
 * val result4 = "".ifNotEmpty { this.reversed() } // ""
 * ```
 */
fun String?.ifNotEmpty(defaultValue: String? = "", listener: String.() -> String): String {
    return when {
        !this.isNullOrEmpty() -> listener.invoke(this)
        !defaultValue.isNullOrEmpty() -> listener.invoke(defaultValue)
        else -> ""
    }
}

/**
 * Iterates through a variable number of string arguments and returns the first string that satisfies the given condition.
 * If no string satisfies the condition, an empty string is returned.
 *
 * @param condition A lambda function that defines the condition to evaluate each string. It takes a nullable `String` as input and returns a boolean
 * .
 * @param s A variable number of nullable strings to be checked against the condition.
 * @return The first string from the provided arguments that meets the condition. If no such string is found, an empty string is returned.
 *
 * Example:
 * ```kotlin
 * val result = getStringVarArg({ it?.startsWith("A") == true }, "Test", null, "Apple", "Orange")
 * println(result) // Output: "Apple"
 *
 * val result2 = getStringVarArg({ it?.endsWith("z") == true }, "Alpha", "Beta", "Gamma")
 * println(result2) // Output: ""
 * ```
 */
fun getStringVarArg(condition: (String?) -> Boolean, vararg s: String?): String {
    var s1 = ""
    run asd@{
        s.forEach {
            when {
                condition(it) -> {
                    s1 = it ?: ""
                    return@asd
                }

                else -> s1 = ""
            }
        }
    }
    return s1
}