package com.kanzankazu.kanzanutil.kanzanextension

import com.google.gson.Gson

fun <T> T.fromObjectToJson(): String = try {
    Gson().toJson(this)
} catch (e: Exception) {
    ""
}

inline fun <reified T> String.fromJsonToObject(model: T): T = try {
    Gson().fromJson(this, T::class.java) ?: model
} catch (e: Exception) {
    model
}