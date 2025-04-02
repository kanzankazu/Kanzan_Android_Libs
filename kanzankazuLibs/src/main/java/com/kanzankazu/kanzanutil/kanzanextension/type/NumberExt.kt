package com.kanzankazu.kanzanutil.kanzanextension.type

import java.text.NumberFormat
import java.util.Locale

/**
 * Converts a nullable Number into a formatted Rupiah currency string.
 * The method provides several customization options including using a currency symbol,
 * returning a short format, or appending prefix text like "M", "rb", or "jt" for large values.
 *
 * @param useCurrencySymbol Whether to include the "Rp" currency symbol in the formatted string. Defaults to true.
 * @param short Whether to display the value in a short format using abbreviations like "M" (million), "jt" (million), or "rb" (thousand). Defaults to
 *  false.
 * @param shortLong Whether to use full-length labels for short format (e.g., "Juta" instead of "jt", "Ribu" instead of "rb"). Defaults to false.
 * @param usePrefix Whether to append abbreviations like "M", "rb", or "jt" as a prefix when using the short format. Defaults to true.
 * @return A string representing the formatted Rupiah amount. If the input Number is null, it returns "Rp " if useCurrencySymbol is true, otherwise an
 *  empty string.
 *
 * Example:
 * ```kotlin
 * val amount: Number? = 1500000
 * println(amount.toRupiahFormat()) // Output: "Rp1,500,000"
 * println(amount.toRupiahFormat(short = true)) // Output: "Rp1.50jt"
 * println(amount.toRupiahFormat(short = true, shortLong = true)) // Output: "Rp1.50 Juta"
 * println(amount.toRupiahFormat(useCurrencySymbol = false)) // Output: "1,500,000"
 * println(amount.toRupiahFormat(null)) // Output: "Rp "
 * ```
 */
fun Number?.toRupiahFormat(
    useCurrencySymbol: Boolean = true,
    short: Boolean = false,
    shortLong: Boolean = false,
    usePrefix: Boolean = true,
): String {
    if (this == null) {
        if (useCurrencySymbol) {
            return "Rp "
        }
        return ""
    }

    val formatRupiah = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

    val formattedValue = if (short) {
        val value = this.toDouble()
        when {
            value >= 1000000000 -> String.format(
                "Rp%.2f ${if (usePrefix) "M" else ""}", value / 1000000000
            ).replace(",00", "")

            value >= 1000000 -> String.format(
                "Rp%.2f ${
                    if (usePrefix) {
                        if (!shortLong) "jt" else "Juta"
                    } else ""
                }", value / 1000000
            ).replace(",00", "")

            value >= 1000 -> String.format(
                "Rp%.2f ${
                    if (usePrefix) {
                        if (!shortLong) "rb" else "Ribu"
                    } else ""
                }", value / 1000
            ).replace(",00", "").replace("Rp", "Rp ")

            else -> formatRupiah.format(this).replace(",00", "").replace("Rp", "Rp ")
        }
    } else {
        formatRupiah.format(this).replace(",00", "").replace("Rp", "Rp ")
    }

    return if (!useCurrencySymbol) formattedValue.replace("Rp ", "") else formattedValue
}

/**
 * Evaluates whether the current `Number` is zero. If it is not zero, the current value is returned.
 * Otherwise, the provided `listener` function is invoked, and its result is returned.
 *
 * @param listener A lambda function that is executed to provide a value when the current `Number` is zero.
 * @return The current `Number` if it is not zero, or the result of the `listener` function if it is zero.
 *
 * Example:
 * ```kotlin
 * val value = 0.ifZero { 10 } // Result: 10
 * val value2 = 5.ifZero { 10 } // Result: 5
 * ```
 */
fun Number.ifZero(listener: () -> Number) = if (this != 0) this else listener.invoke()
