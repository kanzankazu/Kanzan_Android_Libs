package com.kanzankazu.kanzanutil.kanzanextension.type

import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt

/**
 * Rounds a Double value to the nearest half (0.0, 0.5, 1.0, etc.).
 * If the decimal part of the number is less than 0.25, it rounds down to the nearest integer.
 * If the decimal part is between 0.25 and 0.75 (inclusive), it rounds to the nearest 0.5.
 * Otherwise, it rounds up to the next integer.
 *
 * @return A Double value rounded to the nearest half.
 *
 * Example:
 * ```kotlin
 * val value1 = 2.1.roundToHalf() // Result: 2.0
 * val value2 = 2.3.roundToHalf() // Result: 2.5
 * val value3 = 2.7.roundToHalf() // Result: 2.5
 * val value4 = 2.8.roundToHalf() // Result: 3.0
 * ```
 */
fun Double.roundToHalf(): Double { // Result : 2.1 -> 2, 2,2 -> 2, 2.3 -> 2.5, 2.4 -> 2.5
    return (this * 2).roundToInt() / 2.0
}

/**
 * Converts a nullable Double value into a formatted Indonesian Rupiah currency string.
 * If the input Double is null, it returns "Rp 0".
 *
 * @return A string representing the value formatted as Indonesian Rupiah (e.g., "Rp 1,000,000").
 * It adjusts spacing and removes unnecessary decimals for a more natural representation.
 *
 * Example:
 * ```kotlin
 * val price: Double? = 1000000.0
 * val formattedPrice = price.setRupiah() // "Rp 1.000.000"
 *
 * val priceNull: Double? = null
 * val nullFormattedPrice = priceNull.setRupiah() // "Rp 0"
 * ```
 */
fun Double?.setRupiah(): String {
    if (this == null) return "Rp 0"

    val localeID = Locale("id", "ID")
    val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
    val tempPrice = formatRupiah.format(this).replace(",00", "")
    return tempPrice.replace("Rp", "Rp ")
}