package com.kanzankazu.kanzanutil.kanzanextension

import com.kanzankazu.kanzanutil.BaseConst.locale
import java.text.NumberFormat

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

    return if (!useCurrencySymbol) result.replace("Rp", "").replace(" ", "") else result
}
