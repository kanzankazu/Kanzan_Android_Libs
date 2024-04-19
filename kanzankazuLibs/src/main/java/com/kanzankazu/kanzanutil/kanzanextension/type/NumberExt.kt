package com.kanzankazu.kanzanutil.kanzanextension.type

import java.text.NumberFormat
import java.util.Locale

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
