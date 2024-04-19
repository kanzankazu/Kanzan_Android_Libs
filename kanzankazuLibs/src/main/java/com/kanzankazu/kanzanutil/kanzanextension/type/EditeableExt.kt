package com.kanzankazu.kanzanutil.kanzanextension.type

import android.text.Editable

fun Editable?.toInt(): Int {
    this?.let {
        return it.toString().toIntOrDefault()
    } ?: kotlin.run {
        return -1
    }
}