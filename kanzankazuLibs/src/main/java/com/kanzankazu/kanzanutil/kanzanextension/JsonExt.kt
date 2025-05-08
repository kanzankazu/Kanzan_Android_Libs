package com.kanzankazu.kanzanutil.kanzanextension

import android.content.Context
import androidx.annotation.RawRes
import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import com.google.gson.reflect.TypeToken
import com.kanzankazu.kanzanutil.kanzanextension.type.debugMessageError
import java.io.InputStreamReader

fun <T> T.fromObjectToJson(): String = try {
    Gson().toJson(this)
} catch (e: Exception) {
    e.debugMessageError("T.fromObjectToJson")
    ""
}

inline fun <reified T> String.fromJsonToObject(model: T): T = try {
    Gson().fromJson(this, T::class.java) ?: model
} catch (e: Exception) {
    e.debugMessageError("String.fromJsonToObject")
    model
}

inline fun <reified T> String.fromJsonToObject(): T? = try {
    Gson().fromJson(this, T::class.java)
} catch (e: Exception) {
    e.debugMessageError("String.fromJsonToObject")
    null
}

inline fun <reified T> String.fromJsonToObjectArrayList(): ArrayList<T> {
    val gson = Gson()
    return try {
        val rawList: ArrayList<*> = gson.fromJson(this, object : TypeToken<ArrayList<*>>() {}.type) ?: arrayListOf<T>()

        // Jika elemen adalah LinkedTreeMap, lakukan konversi manual
        if (rawList.isNotEmpty() && rawList[0] is LinkedTreeMap<*, *>) {
            rawList.map { map ->
                gson.fromJson(gson.toJson(map), T::class.java)
            } as ArrayList<T>
        } else {
            rawList as ArrayList<T>
        }
    } catch (e: Exception) {
        e.debugMessageError("String.fromJsonToObjectArrayList")
        arrayListOf()
    }
}

inline fun <reified T> Context.readJsonFromRaw(@RawRes jsonRaw: Int): T? {
    return try {
        val gson = Gson()
        val inputStream = resources.openRawResource(jsonRaw)
        val reader = InputStreamReader(inputStream)
        gson.fromJson(reader, T::class.java)
    } catch (e: Exception) {
        e.debugMessageError("Context.readJsonFromRaw")
        null
    }
}

inline fun <reified T> Context.readJsonArrayFromRaw(@RawRes jsonRaw: Int): List<T>? {
    return try {
        val gson = Gson()
        val inputStream = resources.openRawResource(jsonRaw)
        val reader = InputStreamReader(inputStream)
        val typeToken = object : TypeToken<List<T>>() {}.type
        gson.fromJson<List<T>>(reader, typeToken)
    } catch (e: Exception) {
        e.debugMessageError("Context.readJsonArrayFromRaw")
        null
    }
}