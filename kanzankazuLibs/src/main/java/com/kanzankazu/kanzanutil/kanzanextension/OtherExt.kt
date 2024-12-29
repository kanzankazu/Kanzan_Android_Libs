@file:Suppress("DEPRECATION")

package com.kanzankazu.kanzanutil.kanzanextension

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializer
import com.kanzankazu.kanzanutil.kanzanextension.type.debugMessageError
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

fun Handler.repeatFun(delayMillis: Long = 10000, r: () -> Unit): Runnable {
    val runnable = object : Runnable {
        override fun run() {
            try {
                r()
            } finally {
                postDelayed(this, delayMillis)
            }
        }
    }
    this.postDelayed(runnable, delayMillis)
    return runnable
}

fun Handler.repeatFunStop(r: Runnable) {
    removeCallbacks(r)
}

fun Context.delayFun(delayMillis: Long = 500, r: () -> Unit): Boolean {
    return Handler(Looper.getMainLooper()).postDelayed(r, delayMillis)
}

fun Fragment.delayFun(delayMillis: Long = 500, r: () -> Unit) {
    try {
        requireActivity().delayFun(delayMillis, r)
    } catch (i: IllegalStateException) {
        i.message.toString().debugMessageError()
    }
}

fun Activity.currentLocale(): Locale? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) resources.configuration.locales.get(0)
    else resources.configuration.locale
}

fun Fragment.currentLocale(): Locale? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) resources.configuration.locales.get(0)
    else resources.configuration.locale
}

inline fun <reified T> convertJsonToObjectClass(jsonString: String): T? = Gson().fromJson(jsonString, T::class.java)

inline fun <reified T> convertObjectClassToJson(): String = Gson().toJson(T::class.java)

fun convertModelToJson(any: Any): String = getDefaultGson().toJson(any)

fun convertModelToJson(jsonElement: JsonElement): String = getDefaultGson().toJson(jsonElement)

private fun getDefaultGson(): Gson {
    val dateFormatServer = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    return GsonBuilder()
        .excludeFieldsWithoutExposeAnnotation()
        .setDateFormat(dateFormatServer)
        .registerTypeAdapter(Date::class.java, JsonDeserializer { json, _, _ ->
            val formatServer = SimpleDateFormat(dateFormatServer, Locale.ENGLISH)
            formatServer.timeZone = TimeZone.getTimeZone("UTC")
            formatServer.parse(json.asString)
        })
        .registerTypeAdapter(Date::class.java, JsonSerializer<Date> { src, _, _ ->
            val format = SimpleDateFormat(dateFormatServer, Locale.ENGLISH)
            format.timeZone = TimeZone.getTimeZone("UTC")
            if (src != null) JsonPrimitive(format.format(src)) else null
        })
        .create()
}

fun Context.getJsonFromAssets(fileName: String): String {
    return try {
        val inputStream: InputStream = assets.open(fileName)
        val size: Int = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()
        String(buffer)
    } catch (e: IOException) {
        e.printStackTrace()
        return ""
    }
}

inline fun <reified T> Context.getJsonObjectFromAssets(fileName: String): T? {
    return convertJsonToObjectClass<T>(getJsonFromAssets(fileName))
}
