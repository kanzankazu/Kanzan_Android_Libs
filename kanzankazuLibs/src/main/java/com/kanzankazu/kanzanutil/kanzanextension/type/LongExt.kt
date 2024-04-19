package com.kanzankazu.kanzanutil.kanzanextension.type

import java.text.ParseException
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

fun Long.betweenTime(): String {
    return try {

        val mills = this
        val hours = mills / (1000 * 60 * 60)
        val mins = (mills / (1000 * 60)) % 60
        val secs = (mills / 1000).toInt() % 60.toLong()

        if (mills > 0F) {
            hours.formatTwoDigit() + ":" + mins.formatTwoDigit() + ":" + secs.formatTwoDigit()
        } else {
            "00:00:00"
        }
    } catch (e: ParseException) {
        "00:00:00"
    }
}

fun Long.formatTwoDigit(): String = if (this <= 10) "0$this" else "$this"

fun Long.string(): String {
    return toString()
}

@OptIn(ExperimentalContracts::class)
fun Long?.isNullOrZero(): Boolean {
    contract { returns(false) implies (this@isNullOrZero != null) }
    return this == null || this == 0L
}
