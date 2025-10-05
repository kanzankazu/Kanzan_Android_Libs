package com.kanzankazu.kanzanutil.kanzanextension

import android.annotation.SuppressLint
import com.kanzankazu.kanzanutil.BaseConst.locale
import java.text.NumberFormat
import java.util.Locale

/**
 * Formats a [Number] into a Rupiah currency string.
 *
 * @param useCurrencySymbol A boolean indicating whether to include the "Rp" currency symbol. Defaults to `true`.
 * @param short Whether to use short format (e.g., 1K, 1M). Defaults to `false`.
 * @param shortLong Whether to use long text in short format (e.g., "Ribu", "Juta"). Defaults to `false`.
 * @param showUnit Whether to include prefix in short format (e.g., "rb", "jt"). Defaults to `true`.
 * @param locale Locale to be used for formatting. Defaults to Indonesian locale.
 * @return A string representing the formatted Rupiah value.
 *         If the input number is `null` and `useCurrencySymbol` is `true`, it returns "Rp".
 *         If the input number is `null` and `useCurrencySymbol` is `false`, it returns an empty string.
 *         Otherwise, it returns the number formatted as Rupiah.
 */
@SuppressLint("DefaultLocale")
@JvmOverloads
fun Number?.toRupiahFormat(
    useCurrencySymbol: Boolean = true,
    short: Boolean = false,
    shortLong: Boolean = false,
    showUnit: Boolean = true,
    locale: Locale = Locale("id", "ID")
): String {
    if (this == null) {
        return if (useCurrencySymbol) "Rp -" else ""
    }

    val formatRupiah = NumberFormat.getCurrencyInstance(locale)

    val formattedValue = if (short) {
        val value = this.toDouble()
        when {
            value >= 1_000_000_000_000 -> String.format(
                "%s%.2f %s",
                if (useCurrencySymbol) "Rp" else "",
                value / 1_000_000_000_000,
                if (showUnit) {
                    if (shortLong) "Triliun" else "T"
                } else ""
            ).replace(",00", "")

            value >= 1_000_000_000 -> String.format(
                "%s%.2f %s",
                if (useCurrencySymbol) "Rp" else "",
                value / 1_000_000_000,
                if (showUnit) {
                    if (shortLong) "Miliar" else "M"
                } else ""
            ).replace(",00", "")

            value >= 1_000_000 -> String.format(
                "%s%.2f %s",
                if (useCurrencySymbol) "Rp" else "",
                value / 1_000_000,
                if (showUnit) {
                    if (shortLong) "Juta" else "Jt"
                } else ""
            ).replace(",00", "")

            value >= 1_000 -> String.format(
                "%s%.2f %s",
                if (useCurrencySymbol) "Rp" else "",
                value / 1_000,
                if (showUnit) {
                    if (shortLong) "Ribu" else "Rb"
                } else ""
            ).replace(",00", "")

            else -> {
                val result = formatRupiah.format(this)
                    .replace(",00", "")
                    .replace("Rp", if (useCurrencySymbol) "Rp" else "")
                if (useCurrencySymbol) "Rp $result".replace("  ", " ").trim() else result.trim()
            }
        }
    } else {
        val result = formatRupiah.format(this)
            .replace(",00", "")
            .replace("Rp", if (useCurrencySymbol) "Rp" else "")
        if (useCurrencySymbol) "Rp $result".replace("  ", " ").trim() else result.trim()
    }

    return formattedValue
}
