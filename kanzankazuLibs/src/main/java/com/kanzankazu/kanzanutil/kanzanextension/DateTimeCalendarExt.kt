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
import com.kanzankazu.kanzanutil.kanzanextension.type.toIntOrDefault
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import java.text.DateFormatSymbols
import java.util.Locale

fun getMonthList(locale: Locale = Locale("id", "ID")): List<String> {
    val months = DateFormatSymbols(locale).months
    return months.filter { it.isNotEmpty() } // Filter untuk menghapus string kosong
}

/**
 * Generates a list of consecutive years starting from the current year up to the specified range.
 *
 * @param n The number of years to include in the list, starting from the current year.
 *          Defaults to 5 if not provided.
 * @return A list of integers representing the years starting from the current year up to (current year + n).
 *
 * Example:
 * ```kotlin
 * val years = getListOfYears(3) // e.g., [2023, 2024, 2025, 2026]
 * val defaultYears = getListOfYears() // e.g., [2023, 2024, 2025, 2026, 2027, 2028]
 * ```
 */
fun getListOfYears(n: Int = 5): List<Int> {
    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    return (currentYear..currentYear + n).toList()
}

/**
 * Sets up a countdown timer between two Calendar instances.
 *
 * This function calculates the duration between two calendar instances and then
 * uses the `setCountDownTimer` extension function on the millisecond difference
 * to create and start a CountDownTimer.
 *
 * @param calendarStart The starting time of the countdown, represented by a Calendar object.
 * @param calendarEnd The ending time of the countdown, represented by a Calendar object.
 * @param textView (Optional) A TextView to display the remaining time. If null, no display updates will occur.
 * @param title (Optional) A title to prepend to the displayed time.  Defaults to an empty string.
 * @param isIndo (Optional) A boolean indicating whether to use Indonesian language for the time units (e.g., "detik" for seconds).  Defaults to false (English).
 * @param onFinish (Optional) A lambda function to be executed when the countdown finishes. Defaults to an empty lambda (no action).
 * @return A CountDownTimer object representing the configured countdown timer.  The timer has already been started and will trigger the `onFinish` lambda when it expires.
 */
fun setCountDownTimer(calendarStart: Calendar, calendarEnd: Calendar, textView: TextView? = null, title: String = "", isIndo: Boolean = false, onFinish: () -> Unit = {}): CountDownTimer {
    val startMillis: Long = calendarStart.timeInMillis //get the start time in milliseconds
    val endMillis: Long = calendarEnd.timeInMillis //get the end time in milliseconds
    val totalMillis = endMillis - startMillis //total time in milliseconds
    return totalMillis.setCountDownTimer(textView, title, isIndo, onFinish)
}

/**
 * Creates and starts a countdown timer.
 *
 * @param textView (Optional) The TextView to display the countdown.  If null, no text updates will be performed.
 * @param title  The title to prepend to the countdown display in the TextView. Defaults to an empty string.
 * @param isIndo  A boolean indicating whether to use Indonesian language formatting for the countdown display. Defaults to false.
 * @param onFinish A lambda function to be executed when the countdown timer finishes. Defaults to an empty lambda.
 * @return A CountDownTimer object that has been created and started. You should manage its lifecycle (e.g., `cancel()` when no longer needed).
 *
 * The countdown timer ticks every 1000 milliseconds (1 second).  During each tick, it updates the text of the provided `textView` (if not null) with the remaining time formatted using the `stringCountDownTimer2` extension function (not provided in this context, assumed to handle string formatting). When the timer finishes, it executes the provided `onFinish` lambda.
 *
 * **Example:**
 * ```kotlin
 *  val myTextView: TextView = findViewById(R.id.my_text_view)
 *  val duration = 60000L // 1 minute in milliseconds
 *
 *  val timer = duration.setCountDownTimer(myTextView, "Time remaining: ", onFinish = {
 *      // Actions to perform when the countdown finishes, e.g., show a message, enable a button.
 *      Toast.makeText(this, "Countdown finished!", Toast.LENGTH_SHORT).show()
 *  })
 *
 *  // To stop the timer prematurely (e.g., in onPause or onDestroy):
 *  // timer.cancel()
 * ```
 */
fun Long.setCountDownTimer(textView: TextView? = null, title: String = "", isIndo: Boolean = false, onFinish: () -> Unit = {}): CountDownTimer {
    return object : CountDownTimer(this, 1000) {
        /**
         * Mengatur teks hitung mundur pada komponen `TextView` berdasarkan durasi waktu yang tersisa.
         *
         * @param millisUntilFinished jumlah waktu yang tersisa dalam hitungan milidetik.
         */
        @SuppressLint("SetTextI18n")
        override fun onTick(millisUntilFinished: Long) {
            /*var seconds = (millisUntilFinished / 1000).toInt()
            val hours = seconds / (60 * 60)
            val tempMint = seconds - hours * 60 * 60
            val minutes = tempMint / 60
            seconds = tempMint - minutes * 60
            if (textView != null) textView.text = "" + "${checkHourMinuteOverTenDateTimepicker(hours)}:" + "${checkHourMinuteOverTenDateTimepicker(minutes)}:" + "${checkHourMinuteOverTenDateTimepicker(seconds)}"*/
            textView?.text = millisUntilFinished.stringCountDownTimer2(title, isIndo)
        }

        /**
         * Dipanggil saat hitungan waktu selesai dalam metode CountDownTimer.
         * Fungsi ini menjalankan aksi yang telah didefinisikan melalui parameter `onFinish`
         * pada metode `setCountDownTimer`.
         */
        override fun onFinish() {
            onFinish()
        }
    }
}

/**
 * Calculates the difference in milliseconds between the current time and the time represented by this Date object.
 *  A positive return value indicates that the Date object represents a time in the past relative to now.
 *  A negative return value indicates that the Date object represents a time in the future relative to now.
 *  A zero return value indicates that the Date object represents the current time.
 *
 * @return The difference in milliseconds between the current time and the time represented by this Date object.
 */
fun Date.getDiffMillisToNow(): Long {
    return this.time - Date().time
}

/**
 * Converts a duration in milliseconds into a formatted countdown timer string.
 *
 * @param isIndo  A Boolean flag indicating whether to format the string in Indonesian (true) or the default locale (false).
 * @return A string representing the countdown timer in the format "DD day, HH hour, MM min, SS sec" (or the Indonesian equivalent).
 *         If the input duration is negative, the returned values for days, hours, minutes, and seconds will all be zero.
 *         The largest unit of time will always be days.
 *
 * Example usage:
 *
 * val durationMillis = 86400000L // 1 day
 * val timerString = durationMillis.stringCountDownTimer1() // Returns "01 day, 00 hour, 00 min, 00 sec"
 *
 * val shortDurationMillis = 3661000L // 1 hour, 1 minute, 1 second
 * val shortTimerString = shortDurationMillis.stringCountDownTimer1() // Returns "00 day, 01 hour, 01 min, 01 sec"
 *
 * val indoTimerString = durationMillis.stringCountDownTimer1(true) // Returns "01 hari, 00 jam, 00 menit, 00 detik"
 */
private fun Long.stringCountDownTimer1(isIndo: Boolean = false): String {
    var millisUntilFinished: Long = this
    val days = TimeUnit.MILLISECONDS.toDays(millisUntilFinished)
    millisUntilFinished -= TimeUnit.DAYS.toMillis(days)

    val hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished)
    millisUntilFinished -= TimeUnit.HOURS.toMillis(hours)

    val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
    millisUntilFinished -= TimeUnit.MINUTES.toMillis(minutes)

    val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished)

    // Format the string
    if (!isIndo) {
        return String.format(Locale.getDefault(), "%02d day, %02d hour, %02d min, %02d sec", days, hours, minutes, seconds)
    } else {
        return String.format(Locale.getDefault(), "%02d hari, %02d jam, %02d menit, %02d detik", days, hours, minutes, seconds)
    }
}

/**
 * Converts a duration in milliseconds into a human-readable countdown string.
 *
 * The function calculates the days, hours, minutes, and seconds from the given duration and formats them into a string representation.
 * It supports an optional title prefix and can output the countdown in either English or Indonesian.
 *
 * @param title An optional title to prepend to the countdown string (e.g., "Remaining Time:"). Defaults to an empty string.
 * @param isIndo A boolean flag indicating whether the output should be in Indonesian ("hari", "jam", "menit", "detik") or English ("day", "hour", "min", "sec"). Defaults to `false` (English).
 * @return A string representing the countdown in the specified format (e.g., "2 day, 10 hour, 30 min, 15 sec" or "Sisa Waktu: 2 hari, 10 jam, 30 menit, 15 detik").
 *
 * Example usage:
 * ```kotlin
 * val remainingTimeMs = 172800000L // 2 days
 * val countdownString = remainingTimeMs.stringCountDownTimer2() // Output: "2 day, 0 hour, 0 min, 0 sec"
 * val countdownStringIndo = remainingTimeMs.stringCountDownTimer2(isIndo = true) // Output: "2 hari, 0 jam, 0 menit, 0 detik"
 * val countdownStringWithTitle = remainingTimeMs.stringCountDownTimer2("Time Left:") // Output: "Time Left: 2 day, 0 hour, 0 min, 0 sec"
 * ```
 */
private fun Long.stringCountDownTimer2(title: String = "", isIndo: Boolean = false): String {
    val days = this / (24 * 60 * 60 * 1000)
    val hours = this / (1000 * 60 * 60) % 24
    val minutes = this / (1000 * 60) % 60
    val seconds = (this / 1000) % 60

    // Display Countdown
    return if (!isIndo) {
        if (title.isNotEmpty()) "$title $days day, $hours hour, $minutes min, $seconds sec"
        else "$days day, $hours hour, $minutes min, $seconds sec"
    } else {
        if (title.isNotEmpty()) "$title $days hari, $hours jam, $minutes menit, $seconds detik"
        else "$days hari, $hours jam, $minutes menit, $seconds detik"
    }
}

/**
 * Shows a calendar dialog for date selection.
 *
 * @param titleTimePicker The title of the dialog.  Defaults to an empty string.
 * @param viewTarget The TextView that will display the selected date. If provided, the date will be formatted and set to this TextView.
 * @param isLimitByCurrent  If `true`, the minimum selectable date will be set to [limitDayAfter] days after the current date. Defaults to `false`.
 * @param limitDayAfter The number of days after the current date to set as the minimum selectable date.  Only applicable if `isLimitByCurrent` is `true`. Defaults to 1.
 * @param year The initially selected year. Defaults to the current year.
 * @param month The initially selected month (0-indexed). Defaults to the current month.
 * @param day The initially selected day of the month. Defaults to the current day.
 * @param listener A lambda function that will be invoked with the selected Calendar object when a date is chosen.  Use this to access the selected date.  Defaults to an empty lambda.
 */
fun Activity.showCalendarDialog(
    titleTimePicker: String = "",
    viewTarget: TextView? = null,
    isLimitByCurrent: Boolean = false,
    limitDayAfter: Int = 1,
    year: Int = Date().toStringFormat(DateTimeCalendarObject.YEAR_FULL).toDigits().toIntOrDefault(),
    month: Int = Date().toStringFormat(DateTimeCalendarObject.MONTH_FULL).toDigits().toIntOrDefault(),
    day: Int = Date().toStringFormat(DateTimeCalendarObject.DATE_FULL).toDigits().toIntOrDefault(),
    listener: (cal: Calendar) -> Unit = {},
) {
    lateinit var datePickerDialog: DatePickerDialog
    val calendar: Calendar = Calendar.getInstance()
    val myDateListener = DatePickerDialog.OnDateSetListener { picker, year, month, date ->
        if (viewTarget != null) {
            val tag = picker.tag as Int
            if (tag == viewTarget.id) {
                calendar.set(year, month, date)
                listener(calendar)
                setInDateFormalFormat(calendar, viewTarget)
            } else {
                calendar.set(year, month, date)
                listener(calendar)
            }
        } else {
            calendar.set(year, month, date)
            listener(calendar)
        }
    }
    datePickerDialog = (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) DatePickerDialog(this, myDateListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
    else DatePickerDialog(this, AlertDialog.THEME_TRADITIONAL, myDateListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)))

    datePickerDialog.setTitle(titleTimePicker)
    datePickerDialog.datePicker.descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS
    datePickerDialog.updateDate(year, month, day)
    if (isLimitByCurrent) datePickerDialog.datePicker.minDate = Date().addDays(limitDayAfter).time
    if (viewTarget != null) datePickerDialog.datePicker.tag = viewTarget.id
    datePickerDialog.show()
}

/**
 * Shows a TimePickerDialog.
 *
 * @param titleTimePicker The title of the TimePickerDialog. Defaults to an empty string.
 * @param hour The initial hour to display. Defaults to the current hour.
 * @param minute The initial minute to display. Defaults to the current minute.
 * @param is24HourView Whether to use 24-hour format. Defaults to true.
 * @param listener A lambda function to be called when the time is set.  It receives a Calendar object with the selected time.  Defaults to an empty lambda.
 */
fun Activity.showTimePickerDialog(
    titleTimePicker: String = "",
    hour: Int = Date().hours,
    minute: Int = Date().minutes,
    is24HourView: Boolean = true,
    listener: (cal: Calendar) -> Unit = {},
) {
    lateinit var timePickerDialog: TimePickerDialog
    val calendar: Calendar = Calendar.getInstance()
    val myDateListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)
        listener(calendar)
    }
    timePickerDialog = (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) TimePickerDialog(this, myDateListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), is24HourView)
    else TimePickerDialog(this, AlertDialog.THEME_TRADITIONAL, myDateListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), is24HourView))
    /**
     * Returns a list of strings representing years from the current year up to the specified number of years ahead.
     *
     * @param futureYears The number of years ahead to include in the list. Defaults to 5.
     * @return A list of year strings from the current year up to the specified number of years.
     *
     * Example usage:
     * ```kotlin
     * val years = getYearList() // Returns the list for 5 years ahead (default).
     * val customYears = getYearList(10) // Returns the list for 10 years ahead.
     * ```
     */
    fun getYearList(futureYears: Int = 5): List<String> {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        return (currentYear..currentYear + futureYears).map { it.toString() }
    }
    timePickerDialog.setTitle(titleTimePicker)
    timePickerDialog.updateTime(hour, minute)
    timePickerDialog.show()
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
 * Converts the month component of the Date object into its complete Indonesian month name equivalent.
 * This method handles the conversion of English month names (e.g., "January") into Indonesian month names (e.g., "Januari").
 *
 * @return A string representation of the month name in Indonesian, based on the Date object.
 * For example, "January" will be converted to "Januari", "February" to "Februari", and so on.
 *
 * Example:
 * ```kotlin
 * val myDate = SimpleDateFormat("yyyy-MM-dd").parse("2024-03-15") // March 15, 2024
 * val indonesianMonth = myDate.getMonthINAFormatComplete() // Output: "Maret"
 * ```
 */
@SuppressLint("SimpleDateFormat")
fun Date.getMonthINAFormatComplete(): String {
    val simpleDateFormat = SimpleDateFormat("MMMM")
    val completeMonth = simpleDateFormat.format(this)
    var completeMonthINA = ""

    when (completeMonth) {
        "January" -> completeMonthINA = "Januari"
        "February" -> completeMonthINA = "Februari"
        "March" -> completeMonthINA = "Maret"
        "April" -> completeMonthINA = "April"
        "May" -> completeMonthINA = "Mei"
        "June" -> completeMonthINA = "Juni"
        "July" -> completeMonthINA = "Juli"
        "August" -> completeMonthINA = "Agustus"
        "September" -> completeMonthINA = "September"
        "October" -> completeMonthINA = "Oktober"
        "November" -> completeMonthINA = "November"
        "December" -> completeMonthINA = "Desember"
    }
    return completeMonthINA
}

/**
 * Converts the month of the calling Date object into its Indonesian (Bahasa Indonesia) abbreviated name.
 * The method takes the English abbreviated month name (e.g., "Jan", "Feb") and maps it to its equivalent in Indonesian.
 *
 * @return A string representing the abbreviated month name in Indonesian, corresponding to the month of the Date object.
 *
 * Example:
 * ```kotlin
 * val myDate = SimpleDateFormat("yyyy-MM-dd").parse("2024-03-15") // March 15, 2024
 * val monthINA = myDate.getMonthINAFormatHalf() // Result: "Maret"
 * ```
 */
@SuppressLint("SimpleDateFormat")
fun Date.getMonthINAFormatHalf(): String {
    val simpleDateFormat = SimpleDateFormat("MMM")
    val halfMonth = simpleDateFormat.format(this)
    var halfMonthINA = ""

    when (halfMonth) {
        "Jan" -> halfMonthINA = "Januari"
        "Feb" -> halfMonthINA = "Februari"
        "Mar" -> halfMonthINA = "Maret"
        "Apr" -> halfMonthINA = "April"
        "May" -> halfMonthINA = "Mei"
        "Jun" -> halfMonthINA = "Juni"
        "Jul" -> halfMonthINA = "Juli"
        "Aug" -> halfMonthINA = "Agustus"
        "Sep" -> halfMonthINA = "September"
        "Oct" -> halfMonthINA = "Oktober"
        "Nov" -> halfMonthINA = "November"
        "Dec" -> halfMonthINA = "Desember"
    }
    return halfMonthINA
}

/**
 * Converts the current `Date` object to a string that represents the day in its complete Indonesian format.
 * For instance, "Monday" is converted to "Senini", "Tuesday" to "Selasa", and so on.
 *
 * @return A string representing the day in complete Indonesian format.
 *
 * Example:
 * ```kotlin
 * val myDate = Date() // Assuming the date is Monday.
 * val dayInIndonesian = myDate.getDayINAFormatComplete() // Returns "Senini".
 * ```
 */
@SuppressLint("SimpleDateFormat")
fun Date.getDayINAFormatComplete(): String {
    val simpleDateFormat = SimpleDateFormat("EEE")
    val completeDay = simpleDateFormat.format(this)
    var completeDayINA = ""

    when (completeDay) {
        "Monday" -> completeDayINA = "Senini"
        "Tuesday" -> completeDayINA = "Selasa"
        "Wednesday" -> completeDayINA = "Rabu"
        "Thursday" -> completeDayINA = "Kamis"
        "Friday" -> completeDayINA = "Jumat"
        "Saturday" -> completeDayINA = "Sabtu"
        "Sunday" -> completeDayINA = "Minggu"
    }
    return completeDayINA
}

/**
 * Converts the current `Date` object into a three-letter day representation in Bahasa Indonesia (Indonesian).
 * The day is returned in an abbreviated format matching Bahasa Indonesia standards:
 * - "Mon" becomes "Sen"
 * - "Tue" becomes "Sel"
 * - "Wed" becomes "Rab"
 * - "Thu" becomes "Kam"
 * - "Fri" becomes "Jum"
 * - "Sat" becomes "Sab"
 * - "Sun" becomes "Ming"
 *
 * @return A string representing the day of the week in Bahasa Indonesia using its abbreviated form.
 *
 * Example:
 * ```kotlin
 * val myDate = Date() // Assume it's a Monday
 * val dayINA = myDate.getDayINAFormatHalf() // "Sen"
 * ```
 */
@SuppressLint("SimpleDateFormat")
fun Date.getDayINAFormatHalf(): String {
    val simpleDateFormat = SimpleDateFormat("EEE")
    val halfDay = simpleDateFormat.format(this)
    var halfDayINA = ""

    when (halfDay) {
        "Mon" -> halfDayINA = "Sen"
        "Tue" -> halfDayINA = "Sel"
        "Wed" -> halfDayINA = "Rab"
        "Thu" -> halfDayINA = "Kam"
        "Fri" -> halfDayINA = "Jum"
        "Sat" -> halfDayINA = "Sab"
        "Sun" -> halfDayINA = "Ming"
    }
    return halfDayINA
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

/**
 * Checks if the given Date object `date` is on the same calendar day as the calling Date object.
 * Throws an IllegalArgumentException if either `this` or `date` is null.
 *
 * @param date The Date object to compare with the calling Date object.
 * @return `true` if both Date objects fall on the same calendar day (considering era, year, and day of the year),
 *         otherwise `false`.
 *
 * Example:
 * ```kotlin
 * val date1 = SimpleDateFormat("yyyy-MM-dd").parse("2023-10-01")
 * val date2 = SimpleDateFormat("yyyy-MM-dd").parse("2023-10-01")
 * val isSameDay = date1.isSameDay(date2) // true
 *
 * val date3 = SimpleDateFormat("yyyy-MM-dd").parse("2023-10-02")
 * val isSameDayDifferent = date1.isSameDay(date3) // false
 * ```
 */
fun Date?.isSameDay(date: Date?): Boolean {
    if (this == null || date == null) {
        throw IllegalArgumentException("The dates must not be null")
    }
    val cal1 = Calendar.getInstance()
    cal1.time = this
    val cal2 = Calendar.getInstance()
    cal2.time = date
    return cal1.isSameDay(cal2)
}

/**
 * Checks if the two Calendar objects represent the same calendar day, irrespective of the time.
 *
 * Compares the ERA, YEAR, and DAY_OF_YEAR fields of both Calendar objects
 * to determine if they represent the same calendar date.
 * If either of the Calendar objects is null, an IllegalArgumentException is thrown.
 *
 * @param cal The Calendar object to compare with. Cannot be null.
 * @return `true` if both Calendar objects represent the same day, `false` otherwise.
 *
 * Example:
 * ```kotlin
 * val calendar1 = Calendar.getInstance().apply { set(2023, 9, 18) }
 * val calendar2 = Calendar.getInstance().apply { set(2023, 9, 18) }
 * val calendar3 = Calendar.getInstance().apply { set(2023, 9, 19) }
 *
 * println(calendar1.isSameDay(calendar2)) // true
 * println(calendar1.isSameDay(calendar3)) // false
 * ```
 *
 * @throws IllegalArgumentException if either Calendar object is null.
 */
fun Calendar?.isSameDay(cal: Calendar?): Boolean {
    if (this == null || cal == null) {
        throw IllegalArgumentException("The dates must not be null")
    }
    return get(Calendar.ERA) == cal.get(Calendar.ERA) &&
            get(Calendar.YEAR) == cal.get(Calendar.YEAR) &&
            get(Calendar.DAY_OF_YEAR) == cal.get(Calendar.DAY_OF_YEAR)
}

/**
 * Checks whether the current Date object represents today's date.
 *
 * @return `true` if the Date object corresponds to today's date, `false` otherwise.
 *
 * Example:
 * ```kotlin
 * val today = Date() // e.g., June 8, 2024
 * val isToday = today.isToday() // true
 *
 * val anotherDate = SimpleDateFormat("yyyy-MM-dd").parse("2024-06-07")
 * val isAnotherDateToday = anotherDate.isToday() // false
 * ```
 */
fun Date.isToday(): Boolean {
    return isSameDay(Calendar.getInstance().time)
}

/**
 * Determines if the current `Calendar` instance corresponds to today's date.
 * It checks the year, day of the year, and era of the `Calendar` instance
 * against the current date.
 *
 * @return `true` if the `Calendar` instance represents today's date, otherwise `false`.
 *
 * Example:
 * ```kotlin
 * val calendar = Calendar.getInstance()
 * val isToday = calendar.isToday() // true if the calendar date is today
 * ```
 */
fun Calendar.isToday(): Boolean {
    return isSameDay(Calendar.getInstance())
}

/**
 * Determines if the current Date object is before the specified Date object, ignoring the time portion.
 * It compares the year, era, and day of the year values of both dates to establish order.
 *
 * @param date The Date object to compare against. If either `this` or `date` is null, an
 * IllegalArgumentException will be thrown.
 * @return `true` if the current Date is before the given Date (by day), otherwise `false`.
 *
 * Example:
 * ```kotlin
 * val date1 = SimpleDateFormat("yyyy-MM-dd").parse("2023-09-15")
 * val date2 = SimpleDateFormat("yyyy-MM-dd").parse("2023-09-16")
 * val result = date1.isBeforeDay(date2) // true
 * ```
 */
fun Date?.isBeforeDay(date: Date?): Boolean {
    if (this == null || date == null) {
        throw IllegalArgumentException("The dates must not be null")
    }
    val cal1 = Calendar.getInstance()
    cal1.time = this
    val cal2 = Calendar.getInstance()
    cal2.time = date
    return cal1.isBeforeDay(cal2)
}

/**
 * Determines whether the current Calendar instance represents a date that comes before the given Calendar instance.
 * This comparison considers the era, year, and day of the year for accuracy.
 *
 * @param cal The Calendar object to compare against. Must not be null.
 * @return `true` if the current Calendar instance is before the input Calendar, otherwise `false`.
 * @throws IllegalArgumentException if either the current Calendar instance (`this`) or the input Calendar (`cal`) is null.
 *
 * Example:
 * ```kotlin
 * val calendar1 = Calendar.getInstance().apply { set(2023, Calendar.OCTOBER, 10) }
 * val calendar2 = Calendar.getInstance().apply { set(2023, Calendar.OCTOBER, 15) }
 *
 * val isBefore = calendar1.isBeforeDay(calendar2) // true
 * val isAfter = calendar2.isBeforeDay(calendar1)  // false
 * ```
 */
fun Calendar?.isBeforeDay(cal: Calendar?): Boolean {
    if (this == null || cal == null) {
        throw IllegalArgumentException("The dates must not be null")
    }
    if (get(Calendar.ERA) < cal.get(Calendar.ERA)) return true
    if (get(Calendar.ERA) > cal.get(Calendar.ERA)) return false
    if (get(Calendar.YEAR) < cal.get(Calendar.YEAR)) return true
    return if (get(Calendar.YEAR) > cal.get(Calendar.YEAR)) false else get(Calendar.DAY_OF_YEAR) < cal.get(Calendar.DAY_OF_YEAR)
}

/**
 * Checks if the current Date object occurs after the specified targetDate,
 * based solely on the date (ignores time components).
 *
 * Throws an IllegalArgumentException if either the current Date or targetDate is null.
 *
 * @param targetDate The target Date to compare against. Must not be null.
 * @return `true` if the current Date is after the targetDate in terms of the date, otherwise `false`.
 *
 * Example:
 * ```kotlin
 * val date1 = SimpleDateFormat("yyyy-MM-dd").parse("2023-10-15")
 * val date2 = SimpleDateFormat("yyyy-MM-dd").parse("2023-10-14")
 * val result = date1.isAfterDay(date2) // true
 * ```
 */
fun Date?.isAfterDay(targetDate: Date?): Boolean {
    if (this == null || targetDate == null) {
        throw IllegalArgumentException("The dates must not be null")
    }
    val cal1 = Calendar.getInstance()
    cal1.time = this
    val cal2 = Calendar.getInstance()
    cal2.time = targetDate
    return cal1.isAfterDay(cal2)
}

/**
 * Determines whether the current Calendar object represents a date after the specified target Calendar object,
 * considering only the day, year, and era components of the dates.
 * Both calendars must be non-null.
 *
 * @param targetCal The target Calendar object to compare with. Must not be null.
 * @return `true` if the current Calendar's date is strictly after the target Calendar's date;
 *         `false` otherwise.
 *
 * Example:
 * ```kotlin
 * val currentCal = Calendar.getInstance().apply { set(2024, Calendar.JUNE, 10) }
 * val targetCal = Calendar.getInstance().apply { set(2024, Calendar.JUNE, 9) }
 * val result = currentCal.isAfterDay(targetCal) // true
 * ```
 */
fun Calendar?.isAfterDay(targetCal: Calendar?): Boolean {
    if (this == null || targetCal == null) {
        throw IllegalArgumentException("The dates must not be null")
    }
    if (get(Calendar.ERA) < targetCal.get(Calendar.ERA)) return false
    if (get(Calendar.ERA) > targetCal.get(Calendar.ERA)) return true
    if (get(Calendar.YEAR) < targetCal.get(Calendar.YEAR)) return false
    return if (get(Calendar.YEAR) > targetCal.get(Calendar.YEAR)) true else get(Calendar.DAY_OF_YEAR) > targetCal.get(Calendar.DAY_OF_YEAR)
}

/**
 * Checks if the current Date object falls within the specified number of days in the future,
 * relative to the current date. Throws an exception if the Date object is null.
 *
 * @param days The number of days into the future to check against.
 * @return True if the current Date is within the specified number of days into the future; otherwise, false.
 *
 * Example:
 * ```kotlin
 * val myDate = Date() // e.g., June 8, 2024
 * val isWithin = myDate.isWithinDaysFuture(7) // Checks if it's within the next 7 days
 * ```
 */
fun Date?.isWithinDaysFuture(days: Int): Boolean {
    if (this == null) {
        throw IllegalArgumentException("The date must not be null")
    }
    val cal = Calendar.getInstance()
    cal.time = this
    return cal.isWithinDaysFuture(days)
}

/**
 * Determines if the given Calendar instance represents a date within the specified number of days into the future,
 * starting from the current date. The method excludes the current day itself.
 * If the input Calendar is null, an IllegalArgumentException is thrown.
 *
 * @param days The number of days from today (exclusive) to check against.
 * @return `true` if the given Calendar date is within the specified number of days in the future, excluding today;
 *         `false` otherwise.
 *
 * Example:
 * ```kotlin
 * val calendar = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 5) }
 * println(calendar.isWithinDaysFuture(7)) // Output: true
 * println(calendar.isWithinDaysFuture(3)) // Output: false
 * ```
 */
fun Calendar?.isWithinDaysFuture(days: Int): Boolean {
    if (this == null) {
        throw IllegalArgumentException("The date must not be null")
    }
    val today = Calendar.getInstance()
    val future = Calendar.getInstance()
    future.add(Calendar.DAY_OF_YEAR, days)
    return isAfterDay(today) && !isAfterDay(future)
}

/**
 * Checks if the current `Date` instance is within the specified number of days in the past.
 * Throws an `IllegalArgumentException` if the `Date` instance is null.
 *
 * @param days The number of days to check in the past. Must be a non-negative integer.
 * @return `true` if the date is within the past range specified by `days`, `false` otherwise.
 *
 * Example:
 * ```kotlin
 * val date = Date() // Assume it's October 10, 2023
 * val result = date.isWithinDaysPast(5) // Checks if the date is within the past 5 days
 * ```
 */
fun Date?.isWithinDaysPast(days: Int): Boolean {
    if (this == null) {
        throw IllegalArgumentException("The date must not be null")
    }
    val cal = Calendar.getInstance()
    cal.time = this
    return cal.isWithinDaysPast(days)
}

/**
 * Determines whether the current Calendar instance falls within the specified number of past days,
 * relative to the current date.
 *
 * @param days The number of past days to check against. A positive integer representing the range in days.
 *             For example, passing `7` will check if the date is within the past 7 days.
 * @return `true` if the current Calendar instance is within the specified days in the past,
 *         otherwise `false`.
 * @throws IllegalArgumentException If the current Calendar instance is null.
 *
 * Example:
 * ```kotlin
 * val calendar = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -3) } // 3 days ago
 * val isWithin = calendar.isWithinDaysPast(5) // Returns true, as 3 days ago is within the past 5 days
 * val isNotWithin = calendar.isWithinDaysPast(2) // Returns false, as 3 days ago is outside the past 2 days
 * ```
 */
fun Calendar?.isWithinDaysPast(days: Int): Boolean {
    if (this == null) {
        throw IllegalArgumentException("The date must not be null")
    }
    val today = Calendar.getInstance()
    val future = Calendar.getInstance()
    future.add(Calendar.DAY_OF_YEAR, days)
    return isBeforeDay(today) && !isBeforeDay(future)
}
/*Is End*/

/**
 * Ensures that a single-digit integer is formatted as a two-digit string by prefixing it with "0" if necessary.
 * If the value is 10 or greater, it returns the number as a string without modification.
 *
 * @receiver An integer representing an hour or minute (e.g., 3, 9, 25).
 * @return A two-digit string representation of the integer (e.g., "03", "09", "25").
 *
 * Example:
 * ```kotlin
 * val hour = 9
 * val formattedHour = hour.checkHourMinuteOverTenDateTimepicker() // "09"
 *
 * val minute = 15
 * val formattedMinute = minute.checkHourMinuteOverTenDateTimepicker() // "15"
 * ```
 */
fun Int.checkHourMinuteOverTenDateTimepicker(): String = if (this < 10) "0${this}" else "$this"

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
fun Int.checkAmPmDateTimepicker(): String = if (this > 12) "p.m" else "a.m"

/**
 * Converts an integer representing a month or day into a two-digit string format
 * by adding a leading zero if the value is less than 10.
 * This is useful for ensuring date components are in the "MM" or "DD" format.
 *
 * @return A two-digit string representation of the number. If the integer is less than 10, a leading zero is prepended; otherwise, the integer is returned
 *  as a string.
 *
 * Example:
 * ```kotlin
 * val month = 5.toMonthAndDayFormat() // Output: "05"
 * val day = 12.toMonthAndDayFormat()  // Output: "12"
 * ```
 */
fun Int.toMonthAndDayFormat(): String {
    return if ((this) < 10) {
        "0${this}"
    } else {
        "$this"
    }
}

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
fun Date.toStringDateFormat(pattern: String): String {
    val newFormat = SimpleDateFormat(pattern)
    return newFormat.format(this)
}

