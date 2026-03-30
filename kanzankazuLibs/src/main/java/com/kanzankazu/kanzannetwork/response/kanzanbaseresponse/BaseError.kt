package com.kanzankazu.kanzannetwork.response.kanzanbaseresponse

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.kanzankazu.kanzanutil.kanzanextension.type.debugMessageError
import retrofit2.HttpException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException
import javax.net.ssl.SSLHandshakeException

// ============================================================
// Error Codes
// ============================================================

/** HTTP status code constants. */
object Error {
    // 4xx Client Errors
    const val REQUEST_FAILED = 400
    const val REQUEST_UNAUTHORIZED = 401
    const val REQUEST_FORBIDDEN = 403
    const val REQUEST_NOT_FOUND = 404
    const val REQUEST_METHOD_NOT_ALLOWED = 405
    const val REQUEST_CONFLICT = 409
    const val REQUEST_GONE = 410
    const val REQUEST_UNPROCESSABLE_ENTITY = 422
    const val REQUEST_TOO_MANY = 429

    // 5xx Server Errors
    const val SERVER_INTERNAL = 500
    const val SERVER_BAD_GATEWAY = 502
    const val SERVER_UNAVAILABLE = 503
    const val SERVER_GATEWAY_TIMEOUT = 504
}

// ============================================================
// Error Types
// ============================================================

/** Tidak ada koneksi internet (IOException "No Internet", [UnknownHostException], [ConnectException]). */
object NoInternetError : BaseResponse.Error(message = "No Internet")

/** Koneksi timeout ([SocketTimeoutException]). */
object TimeOutError : BaseResponse.Error(message = "Time Out")

/** SSL/TLS error — sertifikat tidak valid, handshake gagal, dll ([SSLHandshakeException], [SSLException]). */
object SSLError : BaseResponse.Error(message = "SSL Error")

/** Response body tidak bisa di-parse ([JsonSyntaxException], [IllegalStateException] dari Gson). */
data class ParseError(
    override val message: String,
    val cause: Exception? = null,
) : BaseResponse.Error(message = message)

/** HTTP error (non-2xx response) — lengkap dengan code, title, dan data dari server. */
data class HttpError(
    override val message: String,
    val messageTitle: String,
    val code: Int,
    val data: Any?,
) : BaseResponse.Error(message = message)

// ============================================================
// Exception → BaseResponse.Error converter
// ============================================================

/**
 * Mengkonversi [Exception] menjadi [BaseResponse.Error] yang sesuai.
 *
 * Mapping:
 * - [IOException] dengan message "No Internet" → [NoInternetError]
 * - [UnknownHostException] → [NoInternetError] (DNS gagal resolve)
 * - [ConnectException] → [NoInternetError] (server unreachable)
 * - [SocketTimeoutException] → [TimeOutError]
 * - [SSLHandshakeException] → [SSLError] (sertifikat tidak valid)
 * - [SSLException] → [SSLError] (TLS error lainnya)
 * - [HttpException] → [HttpError] (parse error body dari server)
 * - [JsonSyntaxException] → [ParseError] (response body tidak sesuai model)
 * - [IllegalStateException] → [ParseError] (Gson parsing error)
 * - Lainnya → [BaseResponse.Error] generic
 */
fun Exception.toError(): BaseResponse.Error {
    return try {
        when (this) {
            is HttpException -> parseHttpException(this)
            is SocketTimeoutException -> TimeOutError
            is SSLHandshakeException -> SSLError
            is SSLException -> SSLError
            is UnknownHostException -> NoInternetError
            is ConnectException -> NoInternetError
            is IOException -> {
                if (message == "No Internet") NoInternetError
                else BaseResponse.Error(message = message.orEmpty())
            }
            is JsonSyntaxException -> ParseError(
                message = "Response tidak bisa di-parse: ${message.orEmpty()}",
                cause = this,
            )
            is IllegalStateException -> ParseError(
                message = "Data tidak sesuai format: ${message.orEmpty()}",
                cause = this,
            )
            else -> BaseResponse.Error(message = message.orEmpty())
        }
    } catch (e: Exception) {
        e.debugMessageError("Exception.toError")
        BaseResponse.Error(message = e.message.orEmpty())
    }
}

/**
 * Parse [HttpException] menjadi [HttpError].
 *
 * Mencoba parse error body sebagai [RawHttpError] (JSON dari server).
 * Kalau gagal parse, fallback ke message dan code dari [HttpException] langsung.
 */
private fun parseHttpException(exception: HttpException): HttpError {
    return try {
        val errorBodyString = exception.response()?.errorBody()?.string().orEmpty()
        val error = Gson().fromJson(errorBodyString, RawHttpError::class.java)
        HttpError(
            message = error?.message ?: exception.message(),
            messageTitle = error?.messageTitle.orEmpty(),
            code = error?.code ?: exception.code(),
            data = error?.data,
        )
    } catch (e: Exception) {
        // Gagal parse error body — fallback ke HttpException langsung
        HttpError(
            message = exception.message(),
            messageTitle = "",
            code = exception.code(),
            data = null,
        )
    }
}
