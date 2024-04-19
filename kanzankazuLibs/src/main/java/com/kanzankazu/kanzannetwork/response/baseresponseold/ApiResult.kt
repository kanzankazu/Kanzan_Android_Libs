package com.kanzankazu.kanzannetwork.response.baseresponseold

sealed class ApiResult<out T : Any> {

    class Success<out T : Any>(val data: T) : ApiResult<T>()

    class Error(val exception: Throwable, val code: Int = 400) : ApiResult<Nothing>() {
        constructor(message: String?, messageTitle: String?, data: Any?, code: Int = 400) : this(Throwable(message), code) {
            this.message = message ?: BaseApiResponse.GENERAL_ERROR
            this.messageTitle = messageTitle
            this.data = data
        }

        var message: String = ""
            private set
        var messageTitle: String? = ""
            private set
        var data: Any? = null
            private set
    }
}
