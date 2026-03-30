package com.kanzankazu.kanzandatabase.retrofit

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializer
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
 * Base class abstrak untuk membangun Retrofit HTTP client.
 *
 * Menyediakan konfigurasi default untuk:
 * - OkHttp client dengan timeout, logging interceptor (debug), dan Chucker (debug)
 * - Authorization header otomatis (Bearer token atau custom prefix)
 * - Gson converter dengan serialisasi/deserialisasi Date format ISO 8601 UTC
 * - RxJava2 call adapter
 *
 * ## Cara Pakai
 * ```kotlin
 * class MyApiClient(
 *     private val context: Context,
 *     private val token: String
 * ) : BaseHttpClient() {
 *     override fun getContext() = context
 *     override fun getToken() = token
 *     override fun getBaseUrl() = "https://api.example.com/"
 *     override fun isDebug() = BuildConfig.DEBUG
 * }
 *
 * // Ambil API service instance
 * val api = myApiClient.getApi(MyApiService::class.java)
 * ```
 *
 * ## Catatan Penting
 * - Subclass harus memastikan property yang dibutuhkan sudah ter-assign sebelum constructor parent dipanggil,
 *   karena [buildRetrofitClient] dieksekusi di `init` block.
 * - Semua Date di-serialize/deserialize dalam format `yyyy-MM-dd'T'HH:mm:ss.SSS'Z'` (UTC).
 * - Hanya field dengan `@Expose` annotation yang akan di-serialize oleh Gson.
 */
abstract class BaseHttpClient {
    /** Instance Retrofit yang sudah terkonfigurasi. Dibuat saat class di-instantiate. */
    var client: Retrofit? = null
        private set

    /**
     * Menyediakan [Context] untuk Chucker interceptor (debug mode).
     * @return Application atau Activity context.
     */
    protected abstract fun getContext(): Context

    /**
     * Menyediakan token autentikasi untuk Authorization header.
     * @return Token string. Kembalikan string kosong jika tidak perlu auth header.
     */
    protected abstract fun getToken(): String

    /**
     * Menyediakan base URL untuk Retrofit.
     * @return Base URL yang diakhiri dengan `/`, contoh: `"https://api.example.com/"`.
     */
    protected abstract fun getBaseUrl(): String

    /**
     * Menentukan apakah mode debug aktif.
     * Jika `true`, HttpLoggingInterceptor (BODY level) dan Chucker akan ditambahkan.
     * @return `true` untuk mengaktifkan debug interceptors.
     */
    protected abstract fun isDebug(): Boolean

    /**
     * Prefix untuk Authorization header. Default: `"Bearer"`.
     * Override dengan string kosong jika token tidak memerlukan prefix.
     * @return Prefix string, contoh: `"Bearer"`, `"Token"`, atau `""`.
     */
    protected open fun getTokenSuffix(): String = "Bearer"

    /**
     * Menyediakan header tambahan yang akan ditambahkan ke setiap request.
     *
     * Override method ini untuk menambahkan header global yang bersifat opsional,
     * seperti `Accept-Language`, `X-Device-Id`, `X-App-Version`, dll.
     *
     * Header dengan value `null` atau kosong akan di-skip (tidak ditambahkan ke request).
     *
     * @return [Map] berisi key-value header. Default: empty map (tidak ada header tambahan).
     *
     * ### Contoh
     * ```kotlin
     * override fun getAdditionalHeaders(): Map<String, String?> = mapOf(
     *     "Accept-Language" to "id",
     *     "X-Device-Id" to getDeviceId(),
     *     "X-App-Version" to BuildConfig.VERSION_NAME,
     *     "X-Platform" to "android",
     * )
     * ```
     */
    protected open fun getAdditionalHeaders(): Map<String, String?> = emptyMap()

    companion object {
        private const val DATE_FORMAT_SERVER = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
        private const val AUTHORIZATION_HEADER = "Authorization"
    }

    init {
        buildRetrofitClient()
    }

    /**
     * Membuat instance dari Retrofit API service interface.
     *
     * @param T Tipe interface API service (contoh: `MyApiService`).
     * @param endpointClass Class dari interface API service.
     * @return Instance dari [T], atau `null` jika [client] belum ter-inisialisasi.
     *
     * ### Contoh
     * ```kotlin
     * interface MyApiService {
     *     @GET("users")
     *     suspend fun getUsers(): Response<List<User>>
     * }
     *
     * val service = httpClient.getApi(MyApiService::class.java)
     * ```
     */
    fun <T> getApi(endpointClass: Class<T>): T? {
        return client?.create(endpointClass)
    }

    /**
     * Membangun dan mengkonfigurasi Retrofit client.
     *
     * Konfigurasi meliputi:
     * 1. OkHttp client dengan connect & read timeout 2 menit
     * 2. Debug interceptors (jika [isDebug] == true): HttpLoggingInterceptor + Chucker
     * 3. Authorization interceptor (jika [getToken] tidak kosong)
     * 4. Gson converter dengan Date serializer/deserializer UTC
     * 5. RxJava2 call adapter
     */
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

        val additionalHeaders = getAdditionalHeaders()
        if (additionalHeaders.isNotEmpty()) {
            httpClient.addInterceptor(createAdditionalHeadersInterceptor(additionalHeaders))
        }

        client = Retrofit.Builder()
            .baseUrl(getBaseUrl())
            .client(httpClient.build())
            .addConverterFactory(GsonConverterFactory.create(createDefaultGson()))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    /**
     * Menambahkan debug interceptors ke OkHttp client:
     * - [HttpLoggingInterceptor] dengan level BODY untuk log request/response lengkap
     * - [ChuckerInterceptor] untuk inspeksi HTTP traffic via notifikasi
     *
     * @param httpClient OkHttpClient.Builder yang akan ditambahkan interceptors.
     */
    private fun configureDebugInterceptors(httpClient: OkHttpClient.Builder) {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        httpClient.addInterceptor(loggingInterceptor)
        val chuckerInterceptor = ChuckerInterceptor.Builder(getContext()).build()
        httpClient.addInterceptor(chuckerInterceptor)
    }

    /**
     * Membuat interceptor untuk menambahkan Authorization header ke setiap request.
     *
     * Format header: `Authorization: {suffix} {token}` (contoh: `Authorization: Bearer abc123`).
     * Jika [getTokenSuffix] kosong, header hanya berisi token tanpa prefix.
     *
     * @param token Token autentikasi.
     * @return [Interceptor] yang menambahkan Authorization header.
     */
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

    /**
     * Membuat interceptor untuk menambahkan header tambahan ke setiap request.
     *
     * Header dengan value `null` atau kosong akan di-skip.
     *
     * @param headers Map berisi key-value header.
     * @return [Interceptor] yang menambahkan header tambahan.
     */
    private fun createAdditionalHeadersInterceptor(headers: Map<String, String?>): Interceptor {
        return Interceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
            headers.forEach { (key, value) ->
                if (!value.isNullOrEmpty()) {
                    requestBuilder.addHeader(key, value)
                }
            }
            chain.proceed(requestBuilder.build())
        }
    }

    /**
     * Membuat instance [Gson] dengan konfigurasi default:
     * - Hanya serialize field dengan `@Expose` annotation
     * - Date format: `yyyy-MM-dd'T'HH:mm:ss.SSS'Z'` (ISO 8601 UTC)
     * - Custom Date deserializer: parse string → [Date] (UTC)
     * - Custom Date serializer: [Date] → string (UTC)
     *
     * @return [Gson] instance yang sudah terkonfigurasi.
     */
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