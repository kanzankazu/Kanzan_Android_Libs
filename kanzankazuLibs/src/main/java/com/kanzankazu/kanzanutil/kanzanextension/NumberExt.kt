package com.kanzankazu.kanzanutil.kanzanextension

import com.kanzankazu.kanzanutil.BaseConst.locale
import java.text.NumberFormat

/**
 * Formats a [Number] into a Rupiah currency string.
 *
 * @param useCurrencySymbol A boolean indicating whether to include the "Rp" currency symbol. Defaults to `true`.
 * @return A string representing the formatted Rupiah value.
 *         If the input number is `null` and `useCurrencySymbol` is `true`, it returns "Rp".
 *         If the input number is `null` and `useCurrencySymbol` is `false`, it returns an empty string.
 *         Otherwise, it returns the number formatted as Rupiah (e.g., "Rp 1.000", "1.000").
 *         Decimal parts like ",00" are removed.
 */
fun Number?.toRupiahFormat(useCurrencySymbol: Boolean = true): String {
    if (this == null)
        if (useCurrencySymbol)
            return "Rp"

    val formatRupiah = NumberFormat.getCurrencyInstance(locale)
    val result =
        formatRupiah
            .format(this)
            .replace(",00", "")
            .replace("Rp", "Rp ")

    return if (useCurrencySymbol) result else result.replace("Rp", "").trim()
}
