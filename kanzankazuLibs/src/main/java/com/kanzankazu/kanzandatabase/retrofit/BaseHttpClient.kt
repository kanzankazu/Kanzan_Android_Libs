@file:Suppress("SameParameterValue")

package com.kanzankazu.kanzandatabase.retrofit

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializer
import com.readystatesoftware.chuck.ChuckInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

/**
companion object {
private val mInstance: HttpClient = HttpClient()

@Synchronized
fun getInstance(): HttpClient {
return mInstance
}
}*/
abstract class BaseHttpClient<T> {
    var client: Retrofit? = null
    private var endpoint: Class<T>? = null

    protected abstract fun getContext(): Context
    protected abstract fun getToken(): String
    protected abstract fun getBaseUrl(): String
    protected abstract fun isDebug(): Boolean
    protected open fun getTokenSuffix(): String = "Bearer"

    init {
        buildRetrofitClient()
    }

    @Suppress("unused")
    fun <T> getApi(endpointClass: Class<T>): Class<T>? {
        return client?.create(endpointClass::class.java)
    }

    private fun buildRetrofitClient() {
        val httpClient = OkHttpClient.Builder()
        httpClient.connectTimeout(2, TimeUnit.MINUTES)
        httpClient.readTimeout(2, TimeUnit.MINUTES)

        if (isDebug()) {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            httpClient.addInterceptor(interceptor)
            httpClient.addInterceptor(ChuckInterceptor(getContext()))
        }

        if (getToken().isNotEmpty()) {
            if (getTokenSuffix().isNotEmpty()) httpClient.addInterceptor(getInterceptorWithHeader("Authorization", "${getTokenSuffix()} ${getToken()}"))
            else httpClient.addInterceptor(getInterceptorWithHeader("Authorization", getToken()))
            Log.d("Lihat KanzanKazu", "buildRetrofitClient BaseHttpClient ${getToken()}")
        }

        val okHttpClient = httpClient.build()
        client = Retrofit.Builder()
            .baseUrl(getBaseUrl())
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(getDefaultGson()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
        endpoint = null // clear retrofit endpoint
    }

    private fun getInterceptorWithHeader(headerName: String, headerValue: String): Interceptor {
        val header = HashMap<String, String>()
        header[headerName] = headerValue
        return getInterceptorWithHeader(header)
    }

    private fun getInterceptorWithHeader(headers: Map<String, String>): Interceptor {
        return Interceptor { chain ->
            val original = chain.request()
            val builder = original.newBuilder()
            for ((key, value) in headers) {
                builder.addHeader(key, value)
            }
            builder.method(original.method, original.body)
            chain.proceed(builder.build())
        }
    }

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
                if (src != null) {
                    JsonPrimitive(format.format(src))
                } else {
                    null
                }
            })
            .create()
    }
}