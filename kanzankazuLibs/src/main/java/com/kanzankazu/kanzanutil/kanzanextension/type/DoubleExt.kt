package com.kanzankazu.kanzanutil.kanzanextension.type

import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt

fun Double.roundToHalf(): Double { // Result : 2.1 -> 2, 2,2 -> 2, 2.3 -> 2.5, 2.4 -> 2.5
    return (this * 2).roundToInt() / 2.0
}

fun Double?.setRupiah(): String {
    if (this == null) return "Rp 0"

    val localeID = Locale("id", "ID")
    val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
    val tempPrice = formatRupiah.format(this).replace(",00", "")
    return tempPrice.replace("Rp", "Rp ")
}