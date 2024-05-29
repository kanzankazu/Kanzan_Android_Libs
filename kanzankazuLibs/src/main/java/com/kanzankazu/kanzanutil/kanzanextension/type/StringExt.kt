@file:Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "DEPRECATION")

package com.kanzankazu.kanzanutil.kanzanextension.type

import android.annotation.SuppressLint
import android.os.Build
import android.text.Spanned
import android.util.Log
import androidx.core.text.HtmlCompat
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.kanzankazu.kanzanutil.BaseConst
import com.kanzankazu.kanzanutil.kanzanextension.isDebug
import com.kanzankazu.kanzanutil.kanzanextension.toDateFormat
import com.kanzankazu.kanzanutil.kanzanextension.toDigits
import com.kanzankazu.kanzanutil.kanzanextension.toRupiahFormat
import com.kanzankazu.kanzanutil.kanzanextension.toStringFormat
import java.math.BigInteger
import java.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

fun String.debugMessage() {
    isDebug().ifn { Log.d("Lihat DebugMessage", this) }
}

fun String.debugMessageWarning() {
    isDebug().ifn { Log.w("Lihat debugMessageWarning", this) }
}

fun String.debugMessageError() {
    isDebug().ifn { Log.e("Lihat DebugMessageError", this) }
}

fun String.equalIgnoreCase(other: String?) = equals(other = other, true)

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

fun String.getRupiah(): String = replace("Rp ".toRegex(), "").replace("\\.".toRegex(), "").toDigits()

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

fun String.getRibuan(): String = replace("\\.".toRegex(), "").toDigits()

/*inline fun <C, R> C.ifNotEmpty(defaultValue: () -> R): R where C : String, C : R {
    return if (isNotEmpty()) defaultValue() else this
}*/

fun String.ifCon(conditionTrue: Boolean, defaultValue: () -> String): String {
    return if (conditionTrue) this else defaultValue()
}

fun String.ifCon(conditionTrue: Boolean, defaultValue: String): String {
    return if (conditionTrue) this else defaultValue
}

fun String?.ifNotEmpty(defaultValue: () -> String): String {
    return if (!isNullOrEmpty()) this else defaultValue()
}

fun String.encodeString(): String =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val encodeBytes = Base64.getEncoder().encodeToString(this.toByteArray())
        encodeBytes
    } else {
        val encodedBytes = android.util.Base64.encode(this.toByteArray(), android.util.Base64.NO_WRAP)
        String(encodedBytes)
    }

fun String.decodedString(): String =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val decodedBytes = Base64.getDecoder().decode(this)
        String(decodedBytes)
    } else {
        val decodedBytes = android.util.Base64.decode(this.toByteArray(), android.util.Base64.NO_WRAP)
        String(decodedBytes)
    }

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

fun String.maskingEmailAddress() =
    replace(Regex("(?<=.)[^@](?=[^@]*?@)|(?:(?<=@.)|(?!^)\\G(?=[^@]*$)).(?!$)"), "*")

fun String.maskingPhoneNumber() = replace(Regex("\\b(\\d{2})\\d+(\\d)"), "$1*******$2")

/**
 * implementation 'com.amulyakhare:com.amulyakhare.textdrawable:1.0.1'
 *
 * @param this@getInitialNameDrawable = bisa apa saja, dipasang di Imageview dengan (imageView.setImageDrawable(textDrawable))
 * @return TextDrawable
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

fun String.upperCase() = uppercase(Locale.getDefault())

fun String.int() = when {
    all { it.isDigit() } -> this.toIntOrDefault()
    any { it.isDigit() } -> this.filter { it.isDigit() }.toIntOrDefault()
    else -> 0
}

fun String.removeLastChar() = substring(0, length - 1)

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

fun convertToKm(km: String): String {
    //  ex : 12.345,6 replace all "." with "" and "," with ".", enable parsing to double
    val sKm = km.replace("\\.".toRegex(), "").replace(",".toRegex(), ".")
    // ex : 12500.6 parse to double -> 12.500.6 replace all "." with "," and replace first found char of "." with ","
    return String.format("%s KM", NumberFormat.getNumberInstance(Locale.UK).format(sKm.toDouble()).replace(".", ",").replaceFirst(",".toRegex(), "."))
}

fun convertToKmNumberOnly(km: String): String {
    //  ex : 12.345,6 replace all "." with "" and "," with ".", enable parsing to double
    val sKm = km.replace("\\.".toRegex(), "").replace(",".toRegex(), ".")
    // ex : 12500.6 parse to double -> 12.500.6 replace all "." with "," and replace first found char of "." with ","
    return String.format("%s", NumberFormat.getNumberInstance(Locale.UK).format(sKm.toDouble()).replace(".", ",").replaceFirst(",".toRegex(), "."))
}

fun convertToKmDouble(km: String): Double {
    val res = km.replace("\\.".toRegex(), "").replace(",".toRegex(), ".")
    return res.toDouble()
}

fun String.isNumeric() = this.matches("-?\\d+(\\.\\d+)?".toRegex())

@SuppressLint("SimpleDateFormat")
        /**"EEEE/MMMM yyy-MM-dd kk:mm:ss" fix error*/
fun String.toDateFormatByShowStandartFormatDate(): Date {
    val orNull = this.trim().split(" ").getOrNull(1)
    return orNull?.toDateFormat(BaseConst.DATE_FORMAT_STD_DATE)
        ?: kotlin.run { this.trim().toDateFormat(BaseConst.DATE_FORMAT_STD_DATE) }
}

fun String.formatHtml(): Spanned {
    return HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_LEGACY)
}

fun String.contains(vararg strings: String): Boolean = strings.any { this.contains(it, true) }

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

fun String?.ifNotEmpty(defaultValue: String? = "", listener: String.() -> String): String {
    return when {
        !this.isNullOrEmpty() -> listener.invoke(this)
        !defaultValue.isNullOrEmpty() -> listener.invoke(defaultValue)
        else -> ""
    }
}

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
