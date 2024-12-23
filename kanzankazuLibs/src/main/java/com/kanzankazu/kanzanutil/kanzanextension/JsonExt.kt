package com.kanzankazu.kanzanutil.kanzanextension

import com.google.gson.Gson
import com.kanzankazu.kanzanutil.kanzanextension.type.debugMessageError

fun <T> T.fromObjectToJson(): String = try {
    Gson().toJson(this)
} catch (e: Exception) {
    e.stackTraceToString().debugMessageError()
    ""
}

inline fun <reified T> String.fromJsonToObject(model: T): T = try {
    Gson().fromJson(this, T::class.java) ?: model
} catch (e: Exception) {
    e.stackTraceToString().debugMessageError()
    model
}