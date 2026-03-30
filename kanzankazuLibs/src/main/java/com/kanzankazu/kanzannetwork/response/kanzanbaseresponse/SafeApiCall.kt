package com.kanzankazu.kanzannetwork.response.kanzanbaseresponse

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import retrofit2.Response

/**
 * Helper untuk menjalankan Retrofit API call secara aman dengan penanganan error otomatis.
 *
 * Semua exception ditangkap dan dikonversi ke [BaseResponse.Error] menggunakan [toError].
 * Mendukung dua pola: suspend function dan Flow.
 *
 * ## Contoh Penggunaan (Suspend)
 * ```kotlin
 * // Di Repository
 * suspend fun getUsers(): BaseResponse<List<User>> {
 *     return safeApiCall { apiService.getUsers() }
 * }
 *
 * // Dengan custom dispatcher
 * suspend fun getUsers(): BaseResponse<List<User>> {
 *     return safeApiCall(Dispatchers.IO) { apiService.getUsers() }
 * }
 * ```
 *
 * ## Contoh Penggunaan (Flow)
 * ```kotlin
 * // Di Repository
 * fun getUsersFlow(): Flow<BaseResponse<List<User>>> {
 *     return safeApiCallFlow { apiService.getUsers() }
 * }
 *
 * // Di ViewModel
 * viewModelScope.launch {
 *     repository.getUsersFlow().collect { response ->
 *         response.handleBaseResponse(
 *             onLoading = { showLoading(it) },
 *             onSuccess = { users -> showUsers(users) },
 *             onError = { message -> showError(message) }
 *         )
 *     }
 * }
 * ```
 */

/**
 * Menjalankan Retrofit API call (yang return [Response]) secara aman.
 *
 * - Response sukses (2xx) dengan body → [BaseResponse.Success]
 * - Response sukses tapi body null → [BaseResponse.Empty]
 * - Response error (non-2xx) → [BaseResponse.Error] via [toError]
 * - Exception → [BaseResponse.Error] via [toError]
 *
 * @param T Tipe data response body.
 * @param dispatcher [CoroutineDispatcher] untuk eksekusi. Default: [Dispatchers.IO].
 * @param apiCall Suspend lambda yang memanggil Retrofit endpoint.
 * @return [BaseResponse] yang sesuai dengan hasil API call.
 */
suspend fun <T> safeApiCall(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    apiCall: suspend () -> Response<T>,
): BaseResponse<T> {
    return withContext(dispatcher) {
        try {
            val response = apiCall()
            if (response.isSuccessful) {
                response.body()?.let { BaseResponse.Success(it) }
                    ?: BaseResponse.Empty
            } else {
                retrofit2.HttpException(response).toError()
            }
        } catch (e: Exception) {
            e.toError()
        }
    }
}

/**
 * Versi [Flow] dari [safeApiCall]. Emit [BaseResponse.Loading] di awal,
 * lalu hasil API call (Success/Empty/Error).
 *
 * @param T Tipe data response body.
 * @param dispatcher [CoroutineDispatcher] untuk eksekusi. Default: [Dispatchers.IO].
 * @param apiCall Suspend lambda yang memanggil Retrofit endpoint.
 * @return [Flow] yang emit Loading → Success/Empty/Error.
 */
fun <T> safeApiCallFlow(
    dispatcher: CoroutineDispatcher = Dispatchers.IO,
    apiCall: suspend () -> Response<T>,
): Flow<BaseResponse<T>> = flow {
    emit(BaseResponse.Loading)
    try {
        val response = apiCall()
        if (response.isSuccessful) {
            emit(response.body()?.let { BaseResponse.Success(it) } ?: BaseResponse.Empty)
        } else {
            emit(retrofit2.HttpException(response).toError())
        }
    } catch (e: Exception) {
        emit(e.toError())
    }
}.flowOn(dispatcher)
