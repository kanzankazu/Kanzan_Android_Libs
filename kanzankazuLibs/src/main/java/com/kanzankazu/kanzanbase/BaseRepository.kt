package com.kanzankazu.kanzanbase

import com.kanzankazu.kanzannetwork.response.BaseApiResponse
import com.kanzankazu.kanzannetwork.response.kanzanbaseresponse.BaseResponse
import com.kanzankazu.kanzannetwork.response.kanzanbaseresponse.toError
import com.kanzankazu.kanzanutil.kanzanextension.type.debugMessageError
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

abstract class BaseRepository {
    /** API Response Handler*/
    protected fun <T, R> BaseApiResponse<T>.handleResponse(listener: String.(T) -> R): BaseResponse<R> {
        val data = this.result
        return when {
            this.success == false -> BaseResponse.Error(message = this.message.orEmpty())
            data == null -> BaseResponse.Empty
            else -> BaseResponse.Success(listener.invoke(this.message ?: "", data))
        }
    }

    protected suspend fun <T, R> handleResponse(
        ioDispatcher: CoroutineDispatcher,
        endpoint: suspend () -> BaseApiResponse<T>,
        listener: String.(T) -> R,
    ): BaseResponse<R> {
        return withContext(context = ioDispatcher) {
            try {
                return@withContext endpoint().handleResponse(listener)
            } catch (e: Exception) {
                e.debugMessageError("BaseRepository - handleResponse")
                val toError = e.toError()
                toError
            }
        }
    }

    protected suspend fun <T, R> handleResponseFlow(
        ioDispatcher: CoroutineDispatcher,
        endpoint: suspend () -> BaseApiResponse<T>,
        listener: String.(T) -> R,
    ): Flow<BaseResponse<R>> = flow {
        emit(BaseResponse.Loading)
        try {
            val data = endpoint().result
            when {
                endpoint().success == false -> emit(BaseResponse.Error(message = endpoint().message.orEmpty()))
                data == null -> emit(BaseResponse.Empty)
                else -> emit(BaseResponse.Success(listener.invoke(endpoint().message ?: "", data)))
            }
        } catch (e: Exception) {
            e.debugMessageError("BaseRepository - handleResponseFlow")
            emit(e.toError())
        }
    }.flowOn(ioDispatcher)

    protected fun BaseApiResponse<Unit>.handleResponse(): BaseResponse<String> {
        return when (this.success) {
            false -> BaseResponse.Error(message = this.message.orEmpty())
            else -> BaseResponse.Success(this.message ?: "")
        }
    }
}
