package com.kanzankazu.kanzannetwork.response.kanzanbaseresponse

import androidx.collection.arrayMapOf

sealed interface BaseResponse<out T> {
    object Loading : BaseResponse<Nothing>

    object Empty : BaseResponse<Nothing>

    open class Error(
        open val message: String,
        val meta: Map<String, Any?> = arrayMapOf(),
    ) : BaseResponse<Nothing>

    class Success<T>(
        val data: T,
        val meta: Map<String, Any?> = arrayMapOf(),
    ) : BaseResponse<T>
}
