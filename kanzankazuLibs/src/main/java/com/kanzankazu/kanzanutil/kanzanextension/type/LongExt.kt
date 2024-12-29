package com.kanzankazu.kanzanutil.kanzanextension.type

import com.kanzankazu.kanzanutil.kanzanextension.toDigits
import java.text.ParseException
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

fun Long.ifCon(conditionTrue: Boolean, defaultValue: () -> Long) =
    if (conditionTrue) this else defaultValue()

fun Long.ifCon(conditionTrue: Boolean, defaultValue: Long) =
    if (conditionTrue) this else defaultValue

fun Long.ifZero(listener: () -> Long) = if (this != 0L) this else listener.invoke()

fun Long.betweenTime() = try {

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

fun Long.formatTwoDigit() = if (this <= 10) "0$this" else "$this"

fun Long.string() = toString()

@OptIn(ExperimentalContracts::class)
fun Long?.isNullOrZero(): Boolean {
    contract { returns(false) implies (this@isNullOrZero != null) }
    return this == null || this == 0L
}

fun Long.setRupiah() = toString().setRupiah()

fun Long.getRupiah() = toString().getRupiah().toDigits()

fun String.toLongOrDefault(defaultValue: Long = 0) = toLongOrNull() ?: defaultValue
