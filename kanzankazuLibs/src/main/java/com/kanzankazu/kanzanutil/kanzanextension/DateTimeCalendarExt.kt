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

fun setCountDownTimer(calendarStart: Calendar, calendarEnd: Calendar, textView: TextView? = null, title: String = "", isIndo: Boolean = false, onFinish: () -> Unit = {}): CountDownTimer {
    val startMillis: Long = calendarStart.timeInMillis //get the start time in milliseconds
    val endMillis: Long = calendarEnd.timeInMillis //get the end time in milliseconds
    val totalMillis = endMillis - startMillis //total time in milliseconds
    return totalMillis.setCountDownTimer(textView, title, isIndo, onFinish)
}

/**
 * set start and cancel CountDownTimer after get this*/
fun Long.setCountDownTimer(textView: TextView? = null, title: String = "", isIndo: Boolean = false, onFinish: () -> Unit = {}): CountDownTimer {
    return object : CountDownTimer(this, 1000) {
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

        override fun onFinish() {
            onFinish()
        }
    }
}

fun Date.getDiffMillisToNow(): Long {
    return this.time - Date().time
}

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

    timePickerDialog.setTitle(titleTimePicker)
    timePickerDialog.updateTime(hour, minute)
    timePickerDialog.show()
}

@SuppressLint("SetTextI18n")
private fun setInDateFormalFormat(calendar: Calendar, textView: TextView) {
    val sDate = calendar.get(Calendar.DAY_OF_MONTH).toString()
    val sMonth = setMonthToLocaleINAComplete(calendar.time)
    val sYear = calendar.get(Calendar.YEAR).toString()
    textView.text = "$sDate $sMonth $sYear"
}

fun Date.toCalendar(): Calendar {
    val cal = Calendar.getInstance()
    cal.time = this
    return cal
}

@SuppressLint("SimpleDateFormat")
fun setMonthToLocaleINAComplete(mDate: Date): String {
    val completeMonthFormat = SimpleDateFormat("MMMM")
    return completeMonthFormat.format(mDate)
}

fun getDateNowStringWithFormat(format: String): String {
    return Date().toStringFormat(format)
}

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

@SuppressLint("SimpleDateFormat")
fun Date.toStringFormat(format: String, locale: Locale? = null): String {
    val sdf: SimpleDateFormat = if (locale != null) SimpleDateFormat(format, Locale("id", "ID"))
    else SimpleDateFormat(format)
    return sdf.format(this)
}

@SuppressLint("SimpleDateFormat")
fun String.toDateFormat(format: String, locale: Locale? = null): Date {
    val sdf: SimpleDateFormat = if (locale != null) SimpleDateFormat(format, Locale("id", "ID"))
    else SimpleDateFormat(format)
    return sdf.parse(this)
}

@SuppressLint("SimpleDateFormat")
        /**
         * @return date or null*/
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
         * @return string or empty*/
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
 * @param this@addYear
 * @param year minus number would decrement the months
 * @return
 */
fun Date.addYear(year: Int): Date {
    val cal = Calendar.getInstance()
    cal.time = this
    cal.add(Calendar.YEAR, year) //minus number would decrement the days
    return cal.time
}

/**
 * @param this@addMonth
 * @param month minus number would decrement the months
 * @return
 */
fun Date.addMonth(month: Int): Date {
    val cal = Calendar.getInstance()
    cal.time = this
    cal.add(Calendar.MONTH, month) //minus number would decrement the days
    return cal.time
}

/**
 * @param this@addWeek
 * @param week minus number would decrement the days
 * @return
 */
fun Date.addWeek(week: Int): Date {
    val cal = Calendar.getInstance()
    cal.time = this
    cal.add(Calendar.WEEK_OF_MONTH, week) //minus number would decrement the days
    return cal.time
}

/**
 * @param this@addDays
 * @param days minus number would decrement the days
 * @return
 */
fun Date.addDays(days: Int): Date {
    val cal = Calendar.getInstance()
    cal.time = this
    cal.add(Calendar.DATE, days) //minus number would decrement the days
    return cal.time
}

/**
 * @param this@addTimes
 * @param hour //minus number would decrement the hour
 * @return
 */
fun Date.addHours(hour: Int): Date {
    val cal = Calendar.getInstance()
    cal.time = this
    cal.add(Calendar.HOUR_OF_DAY, hour) //minus number would decrement the days
    return cal.time
}

fun Date.addMinute(minute: Int): Date {
    val cal = Calendar.getInstance()
    cal.time = this
    cal.add(Calendar.MINUTE, minute) //minus number would decrement the days
    return cal.time
}

fun Date.setTimes(hour: Int, minute: Int = 0, second: Int = 0): Date {
    return toCalendar().setTimes(hour, minute, second).time
}

fun Date.setTimes(calendar: Calendar): Date {
    return toCalendar().setTimes(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), 0).time
}

fun Calendar.setTimes(hour: Int, minute: Int = 0, second: Int = 0): Calendar {
    this.set(Calendar.HOUR_OF_DAY, hour)
    this.set(Calendar.MINUTE, minute)
    this.set(Calendar.SECOND, second)
    return this
}

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

/*Is Start*/
fun Date.isNowAfterThisDate(): Boolean {
    return Date().after(this)
}

fun Date.isNowBeforeThisDate(): Boolean {
    return Date().before(this)
}

fun isBetween2Date(startDate: Date, endDate: Date): Boolean {
    return Date().after(startDate) && Date().before(endDate)
}

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

fun Calendar?.isSameDay(cal: Calendar?): Boolean {
    if (this == null || cal == null) {
        throw IllegalArgumentException("The dates must not be null")
    }
    return get(Calendar.ERA) == cal.get(Calendar.ERA) &&
            get(Calendar.YEAR) == cal.get(Calendar.YEAR) &&
            get(Calendar.DAY_OF_YEAR) == cal.get(Calendar.DAY_OF_YEAR)
}

fun Date.isToday(): Boolean {
    return isSameDay(Calendar.getInstance().time)
}

fun Calendar.isToday(): Boolean {
    return isSameDay(Calendar.getInstance())
}

/**apakah tanggal ini sebelum tanggal target*/
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

/**apakah tanggal ini sebelum tanggal target*/
fun Calendar?.isBeforeDay(cal: Calendar?): Boolean {
    if (this == null || cal == null) {
        throw IllegalArgumentException("The dates must not be null")
    }
    if (get(Calendar.ERA) < cal.get(Calendar.ERA)) return true
    if (get(Calendar.ERA) > cal.get(Calendar.ERA)) return false
    if (get(Calendar.YEAR) < cal.get(Calendar.YEAR)) return true
    return if (get(Calendar.YEAR) > cal.get(Calendar.YEAR)) false else get(Calendar.DAY_OF_YEAR) < cal.get(Calendar.DAY_OF_YEAR)
}

/**apakah tanggal ini sesudah tanggal target*/
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

/**apakah tanggal ini sesudah tanggal target*/
fun Calendar?.isAfterDay(targetCal: Calendar?): Boolean {
    if (this == null || targetCal == null) {
        throw IllegalArgumentException("The dates must not be null")
    }
    if (get(Calendar.ERA) < targetCal.get(Calendar.ERA)) return false
    if (get(Calendar.ERA) > targetCal.get(Calendar.ERA)) return true
    if (get(Calendar.YEAR) < targetCal.get(Calendar.YEAR)) return false
    return if (get(Calendar.YEAR) > targetCal.get(Calendar.YEAR)) true else get(Calendar.DAY_OF_YEAR) > targetCal.get(Calendar.DAY_OF_YEAR)
}

/**apakah tanggal in masih dalam this date masih dalam int kedepan*/
fun Date?.isWithinDaysFuture(days: Int): Boolean {
    if (this == null) {
        throw IllegalArgumentException("The date must not be null")
    }
    val cal = Calendar.getInstance()
    cal.time = this
    return cal.isWithinDaysFuture(days)
}

/**apakah tanggal in masih dalam this date masih dalam int kedepan*/
fun Calendar?.isWithinDaysFuture(days: Int): Boolean {
    if (this == null) {
        throw IllegalArgumentException("The date must not be null")
    }
    val today = Calendar.getInstance()
    val future = Calendar.getInstance()
    future.add(Calendar.DAY_OF_YEAR, days)
    return isAfterDay(today) && !isAfterDay(future)
}

/**apakah tanggal in masih dalam this date masih dalam int kebelakang*/
fun Date?.isWithinDaysPast(days: Int): Boolean {
    if (this == null) {
        throw IllegalArgumentException("The date must not be null")
    }
    val cal = Calendar.getInstance()
    cal.time = this
    return cal.isWithinDaysPast(days)
}

/**apakah tanggal in masih dalam this date masih dalam int kebelakang*/
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

fun Int.checkHourMinuteOverTenDateTimepicker(): String = if (this < 10) "0${this}" else "$this"

fun Int.checkAmPmDateTimepicker(): String = if (this > 12) "p.m" else "a.m"

fun Int.toMonthAndDayFormat(): String {
    return if ((this) < 10) {
        "0${this}"
    } else {
        "$this"
    }
}

fun Date.toStringDateFormat(pattern: String): String {
    val newFormat = SimpleDateFormat(pattern)
    return newFormat.format(this)
}