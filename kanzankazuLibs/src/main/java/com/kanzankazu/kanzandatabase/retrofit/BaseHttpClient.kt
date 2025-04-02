import android.content.Context
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

abstract class BaseHttpClient<T> {
    var client: Retrofit? = null
    private var endpoint: Class<T>? = null

    protected abstract fun getContext(): Context
    protected abstract fun getToken(): String
    protected abstract fun getBaseUrl(): String
    protected abstract fun isDebug(): Boolean
    protected open fun getTokenSuffix(): String = "Bearer"

    companion object {
        private const val DATE_FORMAT_SERVER = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        private const val AUTHORIZATION_HEADER = "Authorization"
    }

    init {
        buildRetrofitClient()
    }

    @Suppress("unused")
    fun <T> getApi(endpointClass: Class<T>): Class<T>? {
        return client?.create(endpointClass::class.java)
    }

    private fun buildRetrofitClient() {
        val httpClient = OkHttpClient.Builder()
            .connectTimeout(2, TimeUnit.MINUTES)
            .readTimeout(2, TimeUnit.MINUTES)

        if (isDebug()) {
            configureDebugInterceptors(httpClient)
        }

        val token = getToken()
        if (token.isNotEmpty()) {
            httpClient.addInterceptor(createAuthorizationInterceptor(token))
        }

        client = Retrofit.Builder()
            .baseUrl(getBaseUrl())
            .client(httpClient.build())
            .addConverterFactory(GsonConverterFactory.create(createDefaultGson()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

        endpoint = null // clear retrofit endpoint
    }

    private fun configureDebugInterceptors(httpClient: OkHttpClient.Builder) {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        httpClient.addInterceptor(loggingInterceptor)
        httpClient.addInterceptor(ChuckInterceptor(getContext()))
    }

    private fun createAuthorizationInterceptor(token: String): Interceptor {
        val fullToken = if (getTokenSuffix().isNotEmpty()) {
            "${getTokenSuffix()} $token"
        } else {
            token
        }
        return Interceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
                .addHeader(AUTHORIZATION_HEADER, fullToken)
                .method(original.method, original.body)
                .build()
            chain.proceed(request)
        }
    }

    private fun createDefaultGson(): Gson {
        return GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .setDateFormat(DATE_FORMAT_SERVER)
            .registerTypeAdapter(Date::class.java, JsonDeserializer { json, _, _ ->
                val format = SimpleDateFormat(DATE_FORMAT_SERVER, Locale.ENGLISH).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }
                format.parse(json.asString)
            })
            .registerTypeAdapter(Date::class.java, JsonSerializer<Date> { src, _, _ ->
                val format = SimpleDateFormat(DATE_FORMAT_SERVER, Locale.ENGLISH).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }
                JsonPrimitive(src?.let { format.format(it) })
            })
            .create()
    }
}