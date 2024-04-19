package com.kanzankazu.kanzanutil.kanzanextension.type

import com.google.gson.Gson

fun <T> object2Json(objectClass: Class<T>?): String {
    val gson = Gson()
    return gson.toJson(objectClass)
}

fun <T> String.json2Object(objectClass: Class<T>): T {
    val gson = Gson()
    return gson.fromJson(this, objectClass)
}

fun <T> T.use(listener: T.() -> Unit) = listener.invoke(this)

fun <T, R> T.useReturn(listener: T.() -> R) = listener.invoke(this)
