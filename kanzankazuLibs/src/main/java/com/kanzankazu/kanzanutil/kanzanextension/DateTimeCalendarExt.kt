@file:Suppress("RemoveSingleExpressionStringTemplate", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "DEPRECATION")

package com.kanzankazu.kanzanutil.kanzanextension

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import android.os.CountDownTimer
import android.view.ViewGroup
import android.widget.TextView
import com.kanzankazu.kanzanutil.enums.CountryLocale
import java.text.DateFormatSymbols
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

enum class DateTimeType {
    MONTH,
    DAY
}

/**
 * Retrieves a list of names corresponding to either months or weekdays based on the specified type and locale.
 *
 * @param type The type of names to retrieve, either `DateTimeType.MONTH` for months or `DateTimeType.DAY` for weekdays.
 * @param countryLocale The locale used for retrieving names. Defaults to `CountryLocale.INDONESIA`.
 * @return A list of strings containing the names of months or weekdays for the specified locale. If a name is empty in the locale, it will be excluded
 *  from the list.
 *
 * Example:
 * ```kotlin
 * val monthNames = getDateTimeNameList(DateTimeType.MONTH) // e.g., ["Januari", "Februari", ...] for Indonesia
 * val dayNames = getDateTimeNameList(DateTimeType.DAY, CountryLocale.US) // e.g., ["Sunday", "Monday", ...] for US
 * ```
 */
fun getDateTimeNameList(
    type: DateTimeType,
    countryLocale: CountryLocale = CountryLocale.INDONESIA,
): List<String> {
    val locale = countryLocale.locale
    return when (type) {
        DateTimeType.MONTH -> DateFormatSymbols(locale).months.filter { it.isNotEmpty() }
        DateTimeType.DAY -> DateFormatSymbols(locale).weekdays.filter { it.isNotEmpty() }
    }
}

/**
 * Generates a list of consecutive integers starting from a specified value.
 *
 * @param start The starting number of the list. Defaults to the current year.
 * @param count The number of consecutive integers to generate. Defaults to 5.
 * @return A list of integers, starting from the specified `start` value and containing `count` numbers.
 *
 * Example:
 * ```kotlin
 * val numbers = generateNumberList(start = 2020, count = 3)
 * // Result: [2020, 2021, 2022]
 * ```
 */
fun generateNumberList(
    start: Int = Calendar.getInstance().get(Calendar.YEAR),
    count: Int = 5,
): List<Int> {
    return List(count) { index -> start + index }
}


/**
 * Calculates the difference in milliseconds between the given Date and the current Date (now).
 *
 * @return The difference in milliseconds as a Long value. A positive value indicates that the given Date
 * is in the past relative to the current time, while a negative value indicates it is in the future.
 *
 * Example:
 * ```kotlin
 * val pastDate = Date(System.currentTimeMillis() - 10000) // 10 seconds ago
 * val diffMillis = pastDate.getDiffMillisToNow() // e.g., 10000 (milliseconds)
 * ```
 */
fun Date.getDiffMillisToNow(): Long = this.time - Date().time

/**
 * Creates and starts a unified countdown timer based on the given input parameters, updating the specified TextView
 * with the current countdown time in a human-readable format. The timer can be configured to display the countdown
 * either in English or Indonesian, and executes a callback function upon completion.
 *
 * If both `calendarStart` and `calendarEnd` are provided, the duration is calculated as the difference between their
 * respective time values. Alternatively, a specific duration can be provided via `durationMillis`. At least one of
 * these two methods for defining the duration must be provided.
 *
 * @param calendarStart The starting point of the countdown as a Calendar object (nullable).
 * @param calendarEnd The ending point of the countdown as a Calendar object (nullable).
 * @param durationMillis The total duration of the countdown in milliseconds (nullable). Used if `calendarStart` and
 *                       `calendarEnd` are not provided.
 * @param textView The TextView to update with the countdown timer's progress (nullable). If null, no updates are made.
 * @param title A title to prefix the countdown display (default is an empty string).
 * @param isIndo A flag indicating whether the countdown should be displayed in Indonesian (true) or English (false).
 *               Defaults to false (English).
 * @param onFinish A callback function to be called when the countdown finishes. Executes irrespective of the UI.
 * @return A `CountDownTimer` object if successfully created and started, or `null` if an error occurs during setup.
 *
 * Example:
 * ```kotlin
 * val calendarStart = Calendar.getInstance()
 * val calendarEnd = Calendar.getInstance().apply { add(Calendar.HOUR, 1) }
 *
 * setUnifiedCountDownTimer(
 *     calendarStart = calendarStart,
 *     calendarEnd = calendarEnd,
 *     textView = myTextView,
 *     title = "Time Remaining: ",
 *     isIndo = false
 * ) {
 *     println("Countdown Finished!")
 * }
 * ```
 */
fun setUnifiedCountDownTimer(
    calendarStart: Calendar? = null,
    calendarEnd: Calendar? = null,
    durationMillis: Long? = null,
    textView: TextView? = null,
    title: String = "",
    isIndo: Boolean = false,
    onFinish: () -> Unit = {},
): CountDownTimer? {
    // Hitung durasi berdasarkan parameter yang diberikan
    val duration = when {
        calendarStart != null && calendarEnd != null -> calendarEnd.timeInMillis - calendarStart.timeInMillis
        durationMillis != null -> durationMillis
        else -> throw IllegalArgumentException("Setidaknya `calendarStart` dan `calendarEnd`, atau `durationMillis` harus diisi")
    }

    // Buat dan jalankan CountDownTimer
    return object : CountDownTimer(duration, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            textView?.text = millisUntilFinished.toCountdownString(title, isIndo)
        }

        override fun onFinish() {
            onFinish()
        }
    }.start()
}

/**
 * Converts a duration represented in milliseconds into a human-readable countdown string, formatted in either
 * English or Indonesian. The output includes days, hours, minutes, and seconds, optionally prefixed with a custom title.
 *
 * @param title An optional string to prefix the countdown (default is an empty string).
 * @param isIndo A flag indicating whether the output should be in Indonesian (true) or English (false).
 *               Defaults to false (English).
 * @return A formatted countdown string representing the duration in the format
 *         "[title][XX days, XX hours, XX minutes, XX seconds]" or
 *         "[title][XX hari, XX jam, XX menit, XX detik]" depending on the `isIndo` parameter.
 *
 * Example:
 * ```kotlin
 * val duration: Long = 123456789L
 * val resultEnglish = duration.toCountdownString("Remaining: ", false)
 * // "Remaining: 01 days, 10 hours, 17 minutes, 36 seconds"
 *
 * val resultIndonesian = duration.toCountdownString("Sisa: ", true)
 * // "Sisa: 01 hari, 10 jam, 17 menit, 36 detik"
 * ```
 */
@SuppressLint("DefaultLocale")
private fun Long.toCountdownString(title: String = "", isIndo: Boolean = false): String {
    val days = TimeUnit.MILLISECONDS.toDays(this)
    val hours = TimeUnit.MILLISECONDS.toHours(this) % 24
    val minutes = TimeUnit.MILLISECONDS.toMinutes(this) % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(this) % 60

    val timeString =
        if (isIndo) String.format("%02d hari, %02d jam, %02d menit, %02d detik", days, hours, minutes, seconds)
        else String.format("%02d days, %02d hours, %02d minutes, %02d seconds", days, hours, minutes, seconds)
    return "$title$timeString"
}

/**
 * Shows a dialog for either date or time selection.
 *
 * @param isDatePicker `true` to show a DatePickerDialog, `false` to show a TimePickerDialog.
 * @param titlePicker The title of the dialog. Defaults to an empty string.
 * @param viewTarget The TextView that will display the selected date/time. If provided, the result will be formatted and set to this TextView. Defaults to `null`.
 * @param year The initially selected year (for DatePickerDialog). Defaults to the current year.
 * @param month The initially selected month (0-indexed, for DatePickerDialog). Defaults to the current month.
 * @param day The initially selected day (for DatePickerDialog). Defaults to the current day.
 * @param hour The initially selected hour (for TimePickerDialog). Defaults to the current hour.
 * @param minute The initially selected minute (for TimePickerDialog). Defaults to the current minute.
 * @param isLimitByCurrent If `true`, limits the selectable date to [limitDayAfter] days after the current date. Only applies to DatePickerDialog. Defaults to `false`.
 * @param limitDayAfter The number of days after the current date to set as the minimum selectable date. Only applicable if `isLimitByCurrent` is `true`. Defaults to `1`.
 * @param is24HourView Whether to use 24-hour format for TimePickerDialog. Defaults to `true`.
 * @param listener A lambda function called with the selected Calendar object when a selection is made.
 */
fun Activity.showDateTimeDialog(
    isDatePicker: Boolean,
    titlePicker: String = "",
    viewTarget: TextView? = null,
    year: Int = Calendar.getInstance().get(Calendar.YEAR),
    month: Int = Calendar.getInstance().get(Calendar.MONTH),
    day: Int = Calendar.getInstance().get(Calendar.DAY_OF_MONTH),
    hour: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
    minute: Int = Calendar.getInstance().get(Calendar.MINUTE),
    isLimitByCurrent: Boolean = false,
    limitDayAfter: Int = 1,
    is24HourView: Boolean = true,
    listener: (cal: Calendar) -> Unit = {},
) {
    val calendar = Calendar.getInstance()

    if (isDatePicker) {
        // Show DatePickerDialog
        val datePickerListener = DatePickerDialog.OnDateSetListener { picker, selectedYear, selectedMonth, selectedDay ->
            calendar.set(selectedYear, selectedMonth, selectedDay)
            listener(calendar)
            viewTarget?.let {
                setInDateFormalFormat(calendar, it)
            }
        }

        val datePickerDialog = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            DatePickerDialog(this, datePickerListener, year, month, day)
        } else {
            DatePickerDialog(this, AlertDialog.THEME_TRADITIONAL, datePickerListener, year, month, day)
        }

        datePickerDialog.setTitle(titlePicker)
        datePickerDialog.datePicker.descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS
        if (isLimitByCurrent) {
            datePickerDialog.datePicker.minDate = Date().addDays(limitDayAfter).time
        }
        datePickerDialog.show()
    } else {
        // Show TimePickerDialog
        val timePickerListener = TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
            calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
            calendar.set(Calendar.MINUTE, selectedMinute)
            listener(calendar)
        }

        val timePickerDialog = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            TimePickerDialog(this, timePickerListener, hour, minute, is24HourView)
        } else {
            TimePickerDialog(this, AlertDialog.THEME_TRADITIONAL, timePickerListener, hour, minute, is24HourView)
        }

        timePickerDialog.setTitle(titlePicker)
        timePickerDialog.show()
    }
}

/**
 * Sets the text of a TextView to display a date in a formal Indonesian format (e.g., "1 Januari 2024").
 *
 * @param calendar The Calendar object representing the date to be displayed.
 * @param textView The TextView where the formatted date will be set.
 *
 * The function extracts the day, month (in full Indonesian name), and year from the provided Calendar object
 * and formats them into a string like "DD MMMM YYYY".  It uses a helper function [setMonthToLocaleINAComplete]
 * to get the full Indonesian name of the month.  The formatted string is then set as the text of the given TextView.
 *
 * Example:
 *   If calendar represents January 1, 2024, and textView is a TextView, calling
 *   `setInDateFormalFormat(calendar, textView)` will result in `textView.text` being set to "1 Januari 2024".
 */
@SuppressLint("SetTextI18n")
private fun setInDateFormalFormat(calendar: Calendar, textView: TextView) {
    val sDate = calendar.get(Calendar.DAY_OF_MONTH).toString()
    val sMonth = setMonthToLocaleINAComplete(calendar.time)
    val sYear = calendar.get(Calendar.YEAR).toString()
    textView.text = "$sDate $sMonth $sYear"
}

/**
 * Converts a [Date] object to a [Calendar] object.
 *
 * This function takes a [Date] object and returns a [Calendar] object representing the same date and time.
 * The returned [Calendar] instance uses the default time zone and locale.
 *
 * @return A [Calendar] object representing the same date and time as the input [Date].
 *
 * @sample
 * ```kotlin
 * val date = Date() // Creates a Date object with the current date and time
 * val calendar = date.toCalendar()
 * println(calendar.time) // Prints the Date represented by the Calendar, which should be the same as 'date'
 * ```
 */
fun Date.toCalendar(): Calendar {
    val cal = Calendar.getInstance()
    cal.time = this
    return cal
}

/**
 * Formats a given Date object to represent the month in the Indonesian locale, using the complete month name (e.g., "Januari").
 *
 * @param mDate The Date object from which to extract the month.
 * @return A String representing the full month name in Indonesian.
 *
 * Example Usage:
 * ```
 * val date = Date() // Assuming this is a date within January
 * val monthInIndonesian = setMonthToLocaleINAComplete(date) // monthInIndonesian will be "Januari"
 * ```
 *
 * Note:  This function uses the default locale which might not be explicitly Indonesian.
 * For a more robust solution, especially if the app supports multiple locales, explicitly set the Indonesian locale in the SimpleDateFormat:
 * `SimpleDateFormat("MMMM", Locale("in", "ID")).format(mDate)`
 */
@SuppressLint("SimpleDateFormat")
fun setMonthToLocaleINAComplete(mDate: Date): String {
    val completeMonthFormat = SimpleDateFormat("MMMM")
    return completeMonthFormat.format(mDate)
}

/**
 * Returns the current date and time as a string formatted according to the specified format.
 *
 * @param format The desired format for the date and time string.  Uses the pattern described in [SimpleDateFormat](https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html).
 *               For example, "yyyy-MM-dd HH:mm:ss" for year-month-day hour:minute:second.
 * @return The current date and time as a formatted string.
 * @throws IllegalArgumentException if the provided format string is invalid.  This is implied by `SimpleDateFormat` but not explicitly handled in this function.  However, the underlying call to `toStringFormat` might throw an exception if the format is unparsable.
 *
 * Example usage:
 * ```kotlin
 * val formattedDate = getDateNowStringWithFormat("dd/MM/yyyy")
 * println(formattedDate) // Output: (e.g., 26/10/2023)
 *
 * val formattedDateTime = getDateNowStringWithFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
 * println(formattedDateTime) // Output: (e.g., 2023-10-26T10:30:00.000+0000)
 * ```
 */
fun getDateNowStringWithFormat(format: String): String {
    return Date().toStringFormat(format)
}

/**
 * Sets the time of a given Date object to the specified hour, minute, and second, while preserving the date component.
 * If the input Date is null, it returns null.
 *
 * @param hour The hour to set (0-23). Defaults to 0.
 * @param minute The minute to set (0-59). Defaults to 0.
 * @param sec The second to set (0-59). Defaults to 0.
 * @return A new Date object with the time set as specified, or null if the input Date is null.
 *
 * Example:
 * ```kotlin
 * val myDate = Date() // e.g., June 8, 2024 10:30 AM
 * val updatedDate = myDate.getDateNow(14, 15, 0) // June 8, 2024 2:15 PM
 * ```
 */
fun Date?.getDateNow(hour: Int = 0, minute: Int = 0, sec: Int = 0): Date? {
    this?.let {
        val c = Calendar.getInstance()
        c.time = this
        c.set(Calendar.HOUR_OF_DAY, hour)
        c.set(Calendar.MINUTE, minute)
        c.set(Calendar.SECOND, sec)
        c.set(Calendar.MILLISECOND, 0)
        return c.time
    } ?: kotlin.run { return null }
}

/**
 * Returns a new Date object representing the start of the day (00:00:00.000) for the given Date.
 * If the input Date is null, it returns null.  This function does not modify the original Date object.
 *
 * @return A Date object representing the start of the day, or null if the input is null.
 *
 * @example
 * ```kotlin
 * val today = Date()
 * val startOfToday = today.getDateNowStart()
 * // If today is 2024-01-26 14:30:00, then startOfToday will be 2024-01-26 00:00:00.000
 * ```
 */
fun Date?.getDateNowStart(): Date? {
    this?.let {
        val c = Calendar.getInstance()
        c.time = this
        c.set(Calendar.HOUR_OF_DAY, 0)
        c.set(Calendar.MINUTE, 0)
        c.set(Calendar.SECOND, 0)
        c.set(Calendar.MILLISECOND, 0)
        return c.time
    } ?: kotlin.run { return null }
}

/**
 * Returns a new [Date] object representing the end of the day (23:59:59.999) for the given date.
 *
 * If the input [Date] is null, the function returns null. Otherwise, it creates a new [Date] object
 * with the same date as the input but with the time set to the last millisecond of the day.
 *
 * @receiver The [Date] to get the end of the day for. Can be null.
 * @return A new [Date] object representing the end of the day, or null if the input was null.
 *
 * @example
 * ```kotlin
 * val today = Date() // e.g., September 20, 2024 10:30 AM
 * val endOfToday = today.getDateNowEnd() // Returns September 20, 2024 11:59:59.999 PM
 *
 * val nullDate: Date? = null
 * val endOfNullDate = nullDate.getDateNowEnd() // Returns null
 * ```
 */
fun Date?.getDateNowEnd(): Date? {
    this?.let {
        val c = Calendar.getInstance()
        c.time = this
        c.set(Calendar.HOUR_OF_DAY, 23)
        c.set(Calendar.MINUTE, 59)
        c.set(Calendar.SECOND, 59)
        c.set(Calendar.MILLISECOND, 999)
        return c.time
    } ?: kotlin.run { return null }
}

/**
 * Converts a Date object to a string representation based on the specified format and locale.
 *
 * @param format The desired date format string (e.g., "yyyy-MM-dd", "dd/MM/yyyy HH:mm:ss").  Uses `SimpleDateFormat` patterns.
 * @param locale The locale to use for formatting.  Defaults to "id_ID" (Indonesian locale).  If null, uses the default locale.
 * @return The date formatted as a string according to the specified format and locale.
 *
 * **Example Usage:**
 * ```kotlin
 * val date = Date() // Assuming this represents January 15th, 2024, 10:30 AM
 *
 * // Format for Indonesian locale (default)
 * val formattedDateID = date.toStringFormat("dd MMMM yyyy, HH:mm")
 * // Example output: "15 Januari 2024, 10:30"
 *
 * // Format for English locale
 * val formattedDateEN = date.toStringFormat("MM/dd/yyyy hh:mm a", Locale.ENGLISH)
 * // Example output: "01/15/2024 10:30 AM"
 *
 * // Format with only date
 * val formattedDate = date.toStringFormat("yyyy-MM-dd")
 * // Example output: "2024-01-15"
 * ```
 *
 * **Important Note:** Uses `SimpleDateFormat`, which is known to have thread-safety issues.  In multi-threaded environments, consider using `java.time` classes (e.g., `DateTimeFormatter`) introduced in Java 8, which are thread-safe.
 *  For example, on Android, if you are targeting API level 26 or higher, it's strongly recommended to use `java.time`
 */
@SuppressLint("SimpleDateFormat")
fun Date.toStringFormat(format: String, locale: Locale? = null): String {
    val sdf: SimpleDateFormat = if (locale != null) SimpleDateFormat(format, Locale("id", "ID"))
    else SimpleDateFormat(format)
    return sdf.format(this)
}

/**
 * Converts the calling String into a Date object using the provided date-time format pattern.
 * Optionally, a specific Locale can be applied to the formatting process.
 *
 * If no Locale is provided, the default Locale will be used.
 *
 * @param format The date-time format pattern used to parse the string into a Date object (e.g., "yyyy-MM-dd HH:mm:ss").
 * @param locale Optional. The Locale to use while parsing the string. If null, the default Locale is used.
 * @return A Date object representing the parsed string, or null if parsing fails.
 *
 * Example:
 * ```kotlin
 * val dateString = "2024-06-08 14:15:00"
 * val date = dateString.toDateFormat("yyyy-MM-dd HH:mm:ss")
 * // date will be parsed to a Date object representing June 8, 2024, 2:15 PM.
 * ```
 */
@SuppressLint("SimpleDateFormat")
fun String.toDateFormat(format: String, locale: Locale? = null): Date {
    val sdf: SimpleDateFormat = if (locale != null) SimpleDateFormat(format, Locale("id", "ID"))
    else SimpleDateFormat(format)
    return sdf.parse(this)
}

@SuppressLint("SimpleDateFormat")
        /**
         * Converts the current Date object into a new Date object formatted according to the desired new format.
         * If the transformation fails due to a parsing exception, it returns null.
         *
         * @param currentFormat The current format of this Date object as a String (e.g., "yyyy-MM-dd").
         * @param newFormat The new desired format to convert this Date object to, as a String (e.g., "MMM dd, yyyy").
         * @return A new Date object formatted as per the specified `newFormat`, or null if the conversion fails.
         *
         * Example:
         * ```kotlin
         * val currentDate = Date() // e.g., Oct 5, 2023
         * val newDate = currentDate.toNewFormat("yyyy-MM-dd", "dd/MM/yyyy") // e.g., "05/10/2023"
         * ```
         */
fun Date.toNewFormat(currentFormat: String, newFormat: String): Date? {
    val currentDateFormat = SimpleDateFormat(currentFormat)
    val newDateFormat = SimpleDateFormat(newFormat)

    return try {
        val newDateString = currentDateFormat.format(this)
        newDateFormat.parse(newDateString)
    } catch (e: ParseException) {
        e.printStackTrace()
        null
    }
}

@SuppressLint("SimpleDateFormat")
        /**
         * Converts the format of a date string from one pattern to another.
         * This method parses the input date string using the `currentFormat` pattern
         * and re-formats it to the specified `newFormat` pattern. If the string cannot
         * be parsed, it returns an empty string.
         *
         * @param currentFormat The pattern describing the current format of the input string.
         *                      Follows the patterns defined in `SimpleDateFormat` (e.g., "yyyy-MM-dd").
         * @param newFormat The pattern to convert the input date string to.
         *                  Follows the patterns defined in `SimpleDateFormat` (e.g., "MM/dd/yyyy").
         * @return A string representing the date formatted in the `newFormat` pattern.
         *         Returns an empty string if parsing fails.
         *
         * Example:
         * ```kotlin
         * val originalDate = "2023-10-15"
         * val formattedDate = originalDate.toNewFormat("yyyy-MM-dd", "MM/dd/yyyy")
         * // Result: "10/15/2023"
         * ```
         */
fun String.toNewFormat(currentFormat: String, newFormat: String): String {
    val currentDateFormat = SimpleDateFormat(currentFormat)
    val newDateFormat = SimpleDateFormat(newFormat)

    return try {
        val currentDate = currentDateFormat.parse(this)
        newDateFormat.format(currentDate)
    } catch (e: ParseException) {
        e.printStackTrace()
        ""
    }
}

/**
 * Adds a specified number of years to the current Date object.
 *
 * @param year The number of years to add. Can be a negative number to subtract years.
 * @return A new Date object with the specified number of years added or subtracted.
 *
 * Example:
 * ```kotlin
 * val currentDate = Date() // e.g., October 15, 2023
 * val updatedDate = currentDate.addYear(5) // e.g., October 15, 2028
 * val earlierDate = currentDate.addYear(-3) // e.g., October 15, 2020
 * ```
 */
fun Date.addYear(year: Int): Date {
    val cal = Calendar.getInstance()
    cal.time = this
    cal.add(Calendar.YEAR, year) //minus number would decrement the days
    return cal.time
}

/**
 * Adjusts the number of months for the given Date object by adding or subtracting the specified number of months.
 * This method preserves the current day of the month, provided it is a valid day in the resulting month.
 * For example, adding 1 month to January 31st will result in the last valid day of February.
 *
 * @param month The number of months to add (positive value) or subtract (negative value).
 * @return A new Date object with the modified month value.
 *
 * Example:
 * ```kotlin
 * val myDate = Date() // e.g., June 8, 2024
 * val updatedDate = myDate.addMonth(2) // August 8, 2024
 * val previousDate = myDate.addMonth(-3) // March 8, 2024
 * ```
 */
fun Date.addMonth(month: Int): Date {
    val cal = Calendar.getInstance()
    cal.time = this
    cal.add(Calendar.MONTH, month) //minus number would decrement the days
    return cal.time
}

/**
 * Adds a specified number of weeks to the given Date instance and returns the updated Date.
 * A negative number for the `week` parameter will subtract weeks from the Date.
 *
 * @param week The number of weeks to add (or subtract if negative).
 * @return A new Date object adjusted by the specified number of weeks.
 *
 * Example:
 * ```kotlin
 * val currentDate = Date() // e.g., February 15, 2023
 * val updatedDate = currentDate.addWeek(2) // March 1, 2023
 * val previousDate = currentDate.addWeek(-3) // January 25, 2023
 * ```
 */
fun Date.addWeek(week: Int): Date {
    val cal = Calendar.getInstance()
    cal.time = this
    cal.add(Calendar.WEEK_OF_MONTH, week) //minus number would decrement the days
    return cal.time
}

/**
 * Adds a specified number of days to a Date object and returns the resulting Date.
 *
 * @param days The number of days to add to the current Date. A negative number will subtract days.
 * @return A new Date object with the specified number of days added.
 *
 * Example:
 * ```kotlin
 * val currentDate = Date() // e.g., June 8, 2024
 * val newDate = currentDate.addDays(5) // June 13, 2024
 * val previousDate = currentDate.addDays(-3) // June 5, 2024
 * ```
 */
fun Date.addDays(days: Int): Date {
    val cal = Calendar.getInstance()
    cal.time = this
    cal.add(Calendar.DATE, days) //minus number would decrement the days
    return cal.time
}

/**
 * Adjusts the time of the current Date object by adding or subtracting the specified number of hours.
 *
 * @param hour The number of hours to add. Positive values will move the time forward,
 *             and negative values will move it backward.
 * @return A new Date object with the adjusted time.
 *
 * Example:
 * ```kotlin
 * val currentDate = Date() // e.g., June 8, 2024 10:30 AM
 * val updatedDate = currentDate.addHours(2) // June 8, 2024 12:30 PM
 * val earlierDate = currentDate.addHours(-3) // June 8, 2024 7:30 AM
 * ```
 */
fun Date.addHours(hour: Int): Date {
    val cal = Calendar.getInstance()
    cal.time = this
    cal.add(Calendar.HOUR_OF_DAY, hour) //minus number would decrement the days
    return cal.time
}

/**
 * Adds a specified number of minutes to the current Date object and returns the updated Date.
 *
 * @param minute The number of minutes to add. A negative value will subtract minutes.
 * @return A new Date object with the updated time reflecting the added (or subtracted) minutes.
 *
 * Example:
 * ```kotlin
 * val currentDate = Date() // e.g., June 8, 2024 10:30 AM
 * val updatedDate = currentDate.addMinute(15) // June 8, 2024 10:45 AM
 * ```
 */
fun Date.addMinute(minute: Int): Date {
    val cal = Calendar.getInstance()
    cal.time = this
    cal.add(Calendar.MINUTE, minute) //minus number would decrement the days
    return cal.time
}

/**
 * Sets the time for the invoking Date object to the specified hour, minute, and second values, while preserving the date component.
 *
 * This function internally converts the Date object to a Calendar to modify its time and then converts it back to a Date.
 *
 * @param hour The hour to set (0-23).
 * @param minute The minute to set (0-59). Defaults to 0.
 * @param second The second to set (0-59). Defaults to 0.
 * @return A new Date object with the updated time values.
 *
 * Example:
 * ```kotlin
 * val currentDate = Date() // e.g., June 8, 2024 10:30 AM
 * val updatedDate = currentDate.setTimes(15, 45, 30) // June 8, 2024 3:45:30 PM
 * ```
 */
fun Date.setTimes(hour: Int, minute: Int = 0, second: Int = 0): Date {
    return toCalendar().setTimes(hour, minute, second).time
}

/**
 * Sets the time of the current Date object to match the specified hour, minute, and second from the provided Calendar object.
 * The date portion remains unchanged.
 *
 * @param calendar The Calendar object from which the hour and minute are extracted to update the time of the Date object.
 * @return A new Date object with the updated time based on the provided Calendar, while retaining the original date.
 *
 * Example:
 * ```kotlin
 * val calendar = Calendar.getInstance().apply {
 *     set(Calendar.HOUR_OF_DAY, 15)
 *     set(Calendar.MINUTE, 45)
 * }
 * val originalDate = Date()  // e.g., June 8, 2024 10:00 AM
 * val updatedDate = originalDate.setTimes(calendar)  // June 8, 2024 3:45 PM
 * ```
 */
fun Date.setTimes(calendar: Calendar): Date {
    return toCalendar().setTimes(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), 0).time
}

/**
 * Sets the time of the Calendar instance to the specified hour, minute, and second.
 * This modifies the existing Calendar object and returns it for chaining.
 *
 * @param hour The hour to set (0-23).
 * @param minute The minute to set (0-59). Defaults to 0 if not provided.
 * @param second The second to set (0-59). Defaults to 0 if not provided.
 * @return The modified Calendar instance with the updated time.
 *
 * Example:
 * ```kotlin
 * val calendar = Calendar.getInstance()
 * calendar.setTimes(14, 30, 45) // Sets time to 14:30:45
 * ```
 */
fun Calendar.setTimes(hour: Int, minute: Int = 0, second: Int = 0): Calendar {
    this.set(Calendar.HOUR_OF_DAY, hour)
    this.set(Calendar.MINUTE, minute)
    this.set(Calendar.SECOND, second)
    return this
}

/**
 * Formats the current Date object into a locale-specific string based on the provided parameters.
 * The format can vary depending on the flags provided for month, day, and year representation.
 *
 * @param isMonth A flag indicating whether to format the date as a month. Defaults to false.
 * @param isComplete A flag determining whether the full name or an abbreviated name is used. Defaults to false.
 * @param forYear A flag specifying whether to format the date to display only the year. Defaults to false.
 * @param countryLocale The Locale to use for formatting. Defaults to the device's default locale.
 * @return A formatted locale-specific string representation of the Date.
 *
 * Example:
 * ```kotlin
 * val currentDate = Date() // e.g., February 15, 2024
 * val formattedDate = currentDate.toLocaleSpecificFormat(isMonth = true, isComplete = true) // "February"
 * ```
 */
@SuppressLint("SimpleDateFormat")
fun Date.formatToLocaleSpecificString(
    isMonth: Boolean = false,
    isComplete: Boolean = false,
    forYear: Boolean = false,
    countryLocale: CountryLocale = CountryLocale.INDONESIA,
): String {
    val format = when {
        forYear -> "yyyy" // Year
        isMonth && isComplete -> "MMMM" // Full month
        isMonth && !isComplete -> "MMM" // Abbreviated month
        !isMonth && isComplete -> "EEEE" // Full day name
        else -> "EEE" // Abbreviated day name
    }

    val simpleDateFormat = SimpleDateFormat(format, countryLocale.locale) // Use Locale
    return simpleDateFormat.format(this)
}

/**
 * Checks if the current date and time (`Date.now()`) is after the Date instance on which the method is called.
 *
 * @return `true` if the current date and time is after this Date instance; `false` otherwise.
 *
 * Example:
 * ```kotlin
 * val dateInPast = Date(1234567890000) // e.g., a date in the past
 * val isAfter = dateInPast.isNowAfterThisDate() // true if the current date is after the specified date
 * ```
 */
/*Is Start*/
fun Date.isNowAfterThisDate(): Boolean {
    return Date().after(this)
}

/**
 * Determines whether the current system time is before the date represented by this `Date` object.
 *
 * @return `true` if the current system time is before this date; `false` otherwise.
 *
 * Example:
 * ```kotlin
 * val futureDate = Date(System.currentTimeMillis() + 10000) // 10 seconds from now
 * val result = futureDate.isNowBeforeThisDate() // true (if called within 10 seconds)
 * ```
 */
fun Date.isNowBeforeThisDate(): Boolean {
    return Date().before(this)
}

/**
 * Checks if the current date and time fall between the specified start and end dates.
 * The method uses the current system time to perform the comparison.
 *
 * @param startDate The starting Date which defines the lower bound of the range (inclusive).
 * @param endDate The ending Date which defines the upper bound of the range (inclusive).
 * @return `true` if the current date and time is after the `startDate` and before the `endDate`;
 * `false` otherwise.
 *
 * Example:
 * ```kotlin
 * val startDate = SimpleDateFormat("yyyy-MM-dd").parse("2023-10-01")
 * val endDate = SimpleDateFormat("yyyy-MM-dd").parse("2023-10-31")
 * val isInRange = isBetween2Date(startDate, endDate) // Returns true if current date is in October 2023
 * ```
 */
fun isBetween2Date(startDate: Date, endDate: Date): Boolean {
    return Date().after(startDate) && Date().before(endDate)
}

fun isSameDay(date1: Any?, date2: Any?): Boolean {
    if (date1 == null || date2 == null) {
        throw IllegalArgumentException("The dates or calendars must not be null")
    }

    val cal1 = when (date1) {
        is Date -> Calendar.getInstance().apply { time = date1 }
        is Calendar -> date1
        else -> throw IllegalArgumentException("The first parameter must be of type Date or Calendar")
    }

    val cal2 = when (date2) {
        is Date -> Calendar.getInstance().apply { time = date2 }
        is Calendar -> date2
        else -> throw IllegalArgumentException("The second parameter must be of type Date or Calendar")
    }

    return cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
            cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
}

fun isToday(date: Any?): Boolean {
    if (date == null) {
        throw IllegalArgumentException("The date or calendar must not be null")
    }

    val calendar = when (date) {
        is Date -> Calendar.getInstance().apply { time = date }
        is Calendar -> date
        else -> throw IllegalArgumentException("The parameter must be of type Date or Calendar")
    }

    val today = Calendar.getInstance()

    return calendar.get(Calendar.ERA) == today.get(Calendar.ERA) &&
            calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
            calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
}

fun isBeforeDay(date1: Any?, date2: Any?): Boolean {
    if (date1 == null || date2 == null) {
        throw IllegalArgumentException("The dates or calendars must not be null")
    }

    val cal1 = when (date1) {
        is Date -> Calendar.getInstance().apply { time = date1 }
        is Calendar -> date1
        else -> throw IllegalArgumentException("The first parameter must be of type Date or Calendar")
    }

    val cal2 = when (date2) {
        is Date -> Calendar.getInstance().apply { time = date2 }
        is Calendar -> date2
        else -> throw IllegalArgumentException("The second parameter must be of type Date or Calendar")
    }

    return if (cal1.get(Calendar.ERA) < cal2.get(Calendar.ERA)) true
    else if (cal1.get(Calendar.ERA) > cal2.get(Calendar.ERA)) false
    else if (cal1.get(Calendar.YEAR) < cal2.get(Calendar.YEAR)) true
    else if (cal1.get(Calendar.YEAR) > cal2.get(Calendar.YEAR)) false
    else cal1.get(Calendar.DAY_OF_YEAR) < cal2.get(Calendar.DAY_OF_YEAR)
}

fun isAfterDay(date1: Any?, date2: Any?): Boolean {
    if (date1 == null || date2 == null) {
        throw IllegalArgumentException("The dates or calendars must not be null")
    }

    val cal1 = when (date1) {
        is Date -> Calendar.getInstance().apply { time = date1 }
        is Calendar -> date1
        else -> throw IllegalArgumentException("The first parameter must be of type Date or Calendar")
    }

    val cal2 = when (date2) {
        is Date -> Calendar.getInstance().apply { time = date2 }
        is Calendar -> date2
        else -> throw IllegalArgumentException("The second parameter must be of type Date or Calendar")
    }

    return if (cal1.get(Calendar.ERA) > cal2.get(Calendar.ERA)) true
    else if (cal1.get(Calendar.ERA) < cal2.get(Calendar.ERA)) false
    else if (cal1.get(Calendar.YEAR) > cal2.get(Calendar.YEAR)) true
    else if (cal1.get(Calendar.YEAR) < cal2.get(Calendar.YEAR)) false
    else cal1.get(Calendar.DAY_OF_YEAR) > cal2.get(Calendar.DAY_OF_YEAR)
}

fun isWithinDaysFromNow(date: Any?, days: Int, future: Boolean = true): Boolean {
    if (date == null) {
        throw IllegalArgumentException("The date or calendar must not be null")
    }

    val calendar = when (date) {
        is Date -> Calendar.getInstance().apply { time = date }
        is Calendar -> date
        else -> throw IllegalArgumentException("The parameter must be of type Date or Calendar")
    }

    val today = Calendar.getInstance()
    val target = Calendar.getInstance().apply {
        add(Calendar.DAY_OF_YEAR, if (future) days else -days)
    }

    return if (future) {
        calendar.after(today) && !calendar.after(target)
    } else {
        calendar.before(today) && !calendar.before(target)
    }
}

/**
 * Mengonversi angka menjadi string dengan format dua digit.
 * Jika angka kurang dari 10, akan ditambahkan "0" di depannya.
 * Cocok digunakan untuk format waktu (jam/menit) atau komponen lain seperti bulan/hari.
 *
 * @receiver Angka bulat yang ingin diformat (misalnya: jam, menit, bulan, atau hari).
 * @return String dengan format dua digit (contoh: "03", "12").
 *
 * Contoh:
 * ```kotlin
 * val hour = 9
 * val formattedHour = hour.toTwoDigitString() // "09"
 *
 * val month = 5
 * val formattedMonth = month.toTwoDigitString() // "05"
 *
 * val day = 15
 * val formattedDay = day.toTwoDigitString() // "15"
 * ```
 */
fun Int.toTwoDigitString(): String = if (this < 10) "0$this" else "$this"

/**
 * Determines whether the given hour in 24-hour format corresponds to "a.m." or "p.m.".
 *
 * @receiver An hour in 24-hour format (0-23).
 * @return A String that represents "a.m." if the hour is between 0 and 12 (inclusive),
 *         or "p.m." if the hour is greater than 12.
 *
 * Example:
 * ```kotlin
 * val hour = 15
 * val period = hour.checkAmPmDateTimepicker() // Returns "p.m."
 * ```
 */
fun Int.toAmPmString(): String = if (this > 12) "p.m" else "a.m"

/**
 * Converts a Date object to a formatted string based on the provided pattern.
 * This utilizes the `SimpleDateFormat` class to apply the desired format to the date.
 *
 * @param pattern The format pattern to apply to the Date object. For example, "yyyy-MM-dd HH:mm:ss".
 *                Refer to Java's `SimpleDateFormat` documentation for supported patterns.
 * @return A formatted date string based on the specified pattern.
 *
 * Example:
 * ```kotlin
 * val currentDate = Date() // e.g., June 8, 2024 10:30 AM
 * val formattedDate = currentDate.toStringDateFormat("yyyy-MM-dd")
 * println(formattedDate) // Output: "2024-06-08"
 * ```
 */
fun Date.toFormattedString(pattern: String): String {
    val newFormat = SimpleDateFormat(pattern)
    return newFormat.format(this)
}

