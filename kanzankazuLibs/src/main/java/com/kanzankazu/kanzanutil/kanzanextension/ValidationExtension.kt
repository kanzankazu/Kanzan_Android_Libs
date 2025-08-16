@file:Suppress("unused", "UNUSED_PARAMETER", "UNUSED_VARIABLE")

package com.kanzankazu.kanzanutil.kanzanextension

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Patterns
import android.view.View
import android.widget.EditText
import android.widget.RadioGroup
import androidx.core.widget.NestedScrollView
import com.google.android.material.textfield.TextInputLayout
import com.kanzankazu.kanzanutil.kanzanextension.type.getRupiah
import com.kanzankazu.kanzanutil.kanzanextension.view.getTil
import com.kanzankazu.kanzanutil.kanzanextension.view.scrollToView
import com.kanzankazu.kanzanutil.kanzanextension.view.string
import com.kanzankazu.kanzanwidget.KanzanEditText
import com.kanzankazu.kanzanwidget.KanzanEditTextComponent
import com.kanzankazu.kanzanwidget.KanzanEditTextRupiahComponent
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Collections
import java.util.Date
import java.util.regex.Pattern

//TODO("OR_NULL")
inline fun <T, reified R> ArrayList<T?>?.orNullArrayListNotNot(transform: (T) -> R, targetClass: R): ArrayList<R> {
    return if (this == null) arrayListOf()
    else {
        val arrayListOf = arrayListOf<R>()
        this.forEach { arrayListOf.add(it?.let { transform(it) } ?: targetClass) }
        return arrayListOf
    }
}

inline fun <T, reified R> ArrayList<T>?.orNullArrayListNot(transform: (T) -> R, targetClass: R): ArrayList<R> {
    return if (this == null) arrayListOf()
    else {
        val arrayListOf = arrayListOf<R>()
        this.forEach { arrayListOf.add(it?.let { transform(it) } ?: targetClass) }
        return arrayListOf
    }
}

inline fun <T, reified R> ArrayList<T>.orNullArrayList(transform: (T) -> R, targetClass: R): ArrayList<R> {
    val arrayListOf = arrayListOf<R>()
    this.forEach { arrayListOf.add(it?.let { transform(it) } ?: targetClass) }
    return arrayListOf
}

inline fun <T, reified R> List<T?>?.orNullListNotNot(transform: (T) -> R, targetClass: R): List<R> {
    return if (this == null) listOf()
    else {
        val arrayListOf = arrayListOf<R>()
        this.forEach { arrayListOf.add(it?.let { transform(it) } ?: targetClass) }
        return arrayListOf
    }
}

inline fun <T, reified R> List<T>?.orNullListNot(transform: (T) -> R): List<R> {
    return if (this == null) listOf()
    else {
        val arrayListOf = arrayListOf<R>()
        this.forEach { arrayListOf.add(transform(it)) }
        return arrayListOf
    }
}

inline fun <T, reified R> T?.orNullObject(transform: (T) -> R, targetClass: R): R {
    return if (this == null) targetClass
    else transform(this)
}

fun String?.orNullBoolean(defaultNull: Boolean = false): Boolean = when (this) {
    "1" -> true
    "0" -> false
    else -> defaultNull
}

fun Int?.orNullBoolean(defaultNull: Boolean = false): Boolean = when (this) {
    1 -> true
    0 -> false
    else -> false
}

fun String?.orNull(defaultNull: String = ""): String = this ?: defaultNull

fun Int?.orNull(defaultNull: Int = 0): Int = this ?: defaultNull

fun Float?.orNull(defaultNull: Float = 0f): Float = this ?: defaultNull

fun Double?.orNull(defaultNull: Double = 0.0): Double = this ?: defaultNull

fun Boolean?.orNull(defaultNull: Boolean = false): Boolean = this ?: defaultNull

//TODO("IS_NULL")
fun Int?.isNull(): Boolean = (this == null)

fun Int?.isNullOrZero(): Boolean = (this == null || this == 0)

fun Int?.isNullOrCustom(param: Int = -1): Boolean = (this == null || this == param)

fun Float?.isNullOrZero(): Boolean = (this == null || this == 0f)

fun Double?.isNullOrZero(): Boolean = (this == null || this == 0.0)

fun <T> T?.isNull(): Boolean = this == null

fun <T> T?.isNotNull(): Boolean = this != null

//TODO("IF_NULL")
fun String?.ifNullOrEmpty(defaultValue: () -> String): String = if (this.isNullOrEmpty()) defaultValue.invoke() else this

fun Int?.ifNull(defaultValue: () -> Int): Int = this ?: defaultValue.invoke()

fun Float?.ifNull(defaultValue: () -> Float): Float = this ?: defaultValue.invoke()

fun Double?.ifNull(defaultValue: () -> Double): Double = this ?: defaultValue.invoke()

fun Boolean?.ifNull(defaultValue: () -> Boolean): Boolean = this ?: defaultValue.invoke()

fun String.toDigits(withMinus: Boolean = false): String {
    val isRealMinus = if (withMinus) {
        val countMinus = this.count { it.toString() == "-" }
        countMinus == 1
    } else false

    return when {
        isEmpty() -> "0"
        all { it.isDigit() } -> this
        any { it.isDigit() || if (isRealMinus) it.toString() == "-" else false } -> filter { it.isDigit() || if (isRealMinus) it.toString() == "-" else false }
        else -> "0"
    }
}

fun isEmptyField(errorMessage: String, vararg editTexts: EditText): Boolean {
    val listStat = ArrayList<Int>()
    for (editText in editTexts) {
        if (editText.string().isEmpty()) {
            editText.isErrorET(errorMessage)
            listStat.add(0)
        } else {
            listStat.add(1)
        }
    }

    val frequency0 = Collections.frequency(listStat, 0)

    val frequency1 = Collections.frequency(listStat, 1)

    return editTexts.size != frequency1
}

fun EditText.isEmptyField(
    errorMessage: String,
    isFocus: Boolean,
    nestedScrollView: NestedScrollView? = null,
    onError: (() -> Unit?)? = null,
): Boolean {
    val textInputLayout = this.getTil()
    return if (string().isEmptyField()) {
        if (textInputLayout != null) {
            textInputLayout.error = errorMessage
            textInputLayout.isErrorEnabled = true
        }
        if (isFocus) {
            textInputLayout?.let { nestedScrollView?.scrollToView(it, 500) }
            requestFocus()
        }
        onError?.invoke()
        true
    } else {
        if (textInputLayout != null) {
            textInputLayout.isErrorEnabled = false
        }
        false
    }
}

fun <T> T.isFieldCondition(falseCondition: T.() -> Boolean, errorMessage: String = "", isFocus: Boolean = false, nestedScrollView: NestedScrollView? = null, onError: (T.(Boolean) -> Unit)? = null): Boolean {
    var textInputLayout: TextInputLayout? = null
    return if (falseCondition.invoke(this)) {
        when (this) {
            is KanzanEditTextComponent -> textInputLayout = this.et()?.getTil()
            is KanzanEditText -> textInputLayout = this.et().getTil()
            is KanzanEditTextRupiahComponent -> textInputLayout = this.et()?.getTil()
            is EditText -> textInputLayout = this.getTil()
        }
        textInputLayout?.apply {
            error = errorMessage
            isErrorEnabled = true
        }

        if (isFocus) {
            if (this is View && nestedScrollView != null) nestedScrollView.scrollToView(this, 500)
            if (this is EditText) requestFocus()
        }
        onError?.invoke(this, true)
        true
    } else {
        when (this) {
            is KanzanEditTextComponent -> textInputLayout = this.et()?.getTil()
            is KanzanEditTextRupiahComponent -> textInputLayout = this.et()?.getTil()
            is EditText -> textInputLayout = this.getTil()
        }
        textInputLayout?.apply { isErrorEnabled = false }

        onError?.invoke(this, false)
        false
    }
}

fun EditText.isEmailOrPhoneFormatValid(errorMessage: String, isFocus: Boolean): Boolean {
    return isFieldCondition({ !isEmailOrPhoneFormatValid() }, errorMessage, isFocus)
}

fun EditText.isErrorET(errorMessage: CharSequence) {
    error = errorMessage
    requestFocus()
}

fun EditText.isEmailOrPhoneFormatValid(): Boolean = isEmailValid() || isPhoneFormatValid()

fun EditText.isEmailValid(): Boolean = string().isEmailValid()

fun EditText.isPhoneFormatValid(): Boolean = string().isPhoneFormatValid()

fun RadioGroup.isRadioGroupChecked(): Boolean = checkedRadioButtonId != -1

fun String.isEmptyField(): Boolean = isEmpty()

fun String.isEmptyField(mActivity: Activity): Boolean {
    return if (isEmptyField()) {
        mActivity.simpleToast("Data masih ada yang kosong")
        true
    } else {
        false
    }
}

fun String.isMatchWith(string2: String): Boolean {
    return this == string2
}

fun String.isUrl(): Boolean {
    return matches("(?i).*http://.*".toRegex()) || matches("(?i).*https://.*".toRegex())
}

fun String.isEmailValid(): Boolean {
    return if (this.isEmpty()) {
        false
    } else {
        return Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }
}

fun String.isTimeBiggerThan(time2: String): Boolean {
    val hhmmss1 = split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    val hhmmss2 = time2.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    for (i in hhmmss1.indices) {
        if (Integer.parseInt(hhmmss1[i]) > Integer.parseInt(hhmmss2[i])) {
            return true
        }
    }
    return false
}

fun String.isTimeSmallerThan(time2: String): Boolean {
    val hhmmss1 = split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    val hhmmss2 = time2.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    for (i in hhmmss1.indices) {
        if (Integer.parseInt(hhmmss1[i]) < Integer.parseInt(hhmmss2[i])) {
            return true
        }
    }
    return false
}

fun String.isValidateIP(): Boolean {
    val ipAddressPattern = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
            "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
            "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." +
            "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$"
    val pattern = Pattern.compile(ipAddressPattern)
    val matcher = pattern.matcher(this)
    return !matcher.matches()
}

@SuppressLint("SimpleDateFormat")
@Throws(ParseException::class)
fun String.isDateValid(): Boolean {
    val format = SimpleDateFormat("dd/MM/yyyy")
    format.isLenient = false

    return try {
        format.parse(this)
        true
    } catch (e: ParseException) {
        false
    }
}

fun String.isPhoneFormatValid(): Boolean {
    return when {
        this.isEmpty() || this.length < 10 || this.length > 18 -> false
        !((this[0] == '8' || this[1] == '8' || this[3] == '8') || (this[1] == '6' && this[2] == '2') || (this[0] == '6' && this[1] == '2')) -> false
        else -> true
    }
}

fun String.isPriceBelowDigit(minDigit: Int): Boolean = getRupiah().length <= minDigit

fun String.isLengthCharAbove(maxChar: Int): Boolean = this.length > maxChar

fun String.isLengthCharBelow(minChar: Int): Boolean = this.length < minChar

fun String.isLengthCharBetween(minChar: Int, maxChar: Int): Boolean = this.length in minChar..maxChar

fun String.isAlphanumeric(includeSpace: Boolean): Boolean {
    val regex = if (includeSpace)
        "^[a-zA-Z0-9 ]+"
    else
        "^[a-zA-Z0-9]+"

    return if (this.isNotEmpty())
        Pattern.compile(regex).matcher(this).matches()
    else
        false
}

fun String.isAlphabet(includeSpace: Boolean): Boolean {
    val regex = if (includeSpace)
        "^[a-zA-Z ]+"
    else
        "^[a-zA-Z]+"

    return if (this.isNotEmpty())
        Pattern.compile(regex).matcher(this).matches()
    else
        false
}

fun String.isNumeric(includeSpace: Boolean): Boolean {
    val regex = if (includeSpace)
        "^[0-9 ]+"
    else
        "^[0-9]+"

    return if (this.isNotEmpty())
        Pattern.compile(regex).matcher(this).matches()
    else
        false
}

fun String.isNumericContains(): Boolean {
    var containsDigit = false

    if (isNotEmpty()) {
        for (c in toCharArray()) {
            if (Character.isDigit(c).also { containsDigit = it }) {
                break
            }
        }
    }

    return containsDigit
}

fun String.isCCExpiry(): Boolean = this.matches("(?:0[1-9]|1[0-2])/(?:[2-9][0-9])".toRegex())

fun String.getCCType(): String {
    val regVisa = Regex("^4[0-9]{3,}$")
    val regMaster = Regex("^[25][0-9]{3,}$")
    val regJCB = Regex("^3[5][0-9]{2,}$")
    val regExpress = Regex("^3[47][0-9]{2,}$")

    return when {
        regVisa.matches(this) -> "Visa"
        regMaster.matches(this) -> "Master Card"
        regExpress.matches(this) -> "AExpress"
        regJCB.matches(this) -> "JBC"
        //regDiners.matches(this) -> Const.PaymentCCType.Diners
        //regDiscover.matches(this) -> Const.PaymentCCType.Discovers
        else -> "invalid"
    }
}

fun Int.isSeventeenAge(): Boolean {
    val cal = Calendar.getInstance()
    cal.time = Date()
    val year = cal[Calendar.YEAR]

    return year - this > 17
}
