package com.kanzankazu.kanzannetwork.response.kanzanbaseresponse

import com.google.firebase.database.DataSnapshot
import com.kanzankazu.kanzanutil.kanzanextension.toObject
import com.kanzankazu.kanzanutil.kanzanextension.toObjectList

fun initBaseResponseLoading() = BaseResponse.Loading
fun initBaseResponseEmpty() = BaseResponse.Empty
fun <T> T.toBaseResponseSuccess() = BaseResponse.Success(this)
fun String.toBaseResponseError(errorMessage: String = "") = BaseResponse.Error(errorMessage.ifEmpty { this })

fun <T> BaseResponse<T>.onLoading(listener: (Boolean) -> Unit): BaseResponse<T> {
    if (this is BaseResponse.Loading) listener.invoke(true)
    else listener.invoke(false)
    return this
}

fun <T> BaseResponse<T>.onEmpty(listener: (Boolean) -> Unit): BaseResponse<T> {
    if (this is BaseResponse.Empty) listener.invoke(true)
    else listener.invoke(false)
    return this
}

fun <T> BaseResponse<T>.onSuccess(listener: (T) -> Unit): BaseResponse<T> {
    if (this is BaseResponse.Success) listener.invoke(this.data)
    return this
}

fun <T> BaseResponse<T>.onError(listener: (String) -> Unit): BaseResponse<T> {
    if (this is BaseResponse.Error) listener.invoke(this.message)
    return this
}

fun <T> BaseResponse<T>.handleBaseResponse(
    onLoading: ((Boolean) -> Unit)? = null,
    onEmpty: (() -> Unit)? = null,
    onError: ((String) -> Unit)? = null,
    onSuccess: (T) -> Unit,
) {
    when (this) {
        is BaseResponse.Loading -> {
            onLoading?.invoke(true)
        }

        is BaseResponse.Empty -> {
            onLoading?.invoke(false)
            onEmpty?.invoke()
        }

        is BaseResponse.Success -> {
            onLoading?.invoke(false)
            onSuccess.invoke(data)
        }

        is BaseResponse.Error -> {
            onLoading?.invoke(false)
            val error = when (this) {
                is HttpError -> message
                is NoInternetError, is TimeOutError -> "Koneksi terputus. Cek sinyal kamu dan coba lagi, ya."
                else -> message.ifEmpty { "Terjadi kesalahan server. Coba beberapa saat lagi, ya." }
            }
            onError?.invoke(error)
        }
    }
}

fun <T, R> BaseResponse<T>.handleBaseResponseConvert(
    onSuccess: (T) -> BaseResponse<R>,
): BaseResponse<R> = when (this) {
    is BaseResponse.Loading -> BaseResponse.Loading
    is BaseResponse.Empty -> BaseResponse.Empty
    is BaseResponse.Success -> onSuccess.invoke(this.data)
    is BaseResponse.Error -> BaseResponse.Error(this.message)
}

fun <T, R> BaseResponse<T>.handleBaseResponseConvertData(
    onError: (String) -> String = { "" },
    onSuccess: (T) -> R,
): BaseResponse<R> = when (this) {
    is BaseResponse.Loading -> BaseResponse.Loading
    is BaseResponse.Empty -> BaseResponse.Empty
    is BaseResponse.Success -> BaseResponse.Success(onSuccess.invoke(this.data))
    is BaseResponse.Error -> BaseResponse.Error(onError.invoke("").ifEmpty { this.message })
}

fun <T, R, Y> handleBaseResponseCombineData(
    mainBaseResponse: BaseResponse<T>,
    secondBaseResponse: BaseResponse<R>,
    onError: (String) -> String = { "" },
    isStillShowSuccess: Boolean = false,
    onSuccess: (T?, R?) -> Y,
): BaseResponse<Y> {
    return if (isStillShowSuccess) {
        when {
            mainBaseResponse is BaseResponse.Success && secondBaseResponse is BaseResponse.Success -> BaseResponse.Success(onSuccess.invoke(mainBaseResponse.data, secondBaseResponse.data))
            mainBaseResponse is BaseResponse.Success -> BaseResponse.Success(onSuccess.invoke(mainBaseResponse.data, null))
            secondBaseResponse is BaseResponse.Success -> BaseResponse.Success(onSuccess.invoke(null, secondBaseResponse.data))
            mainBaseResponse is BaseResponse.Error && secondBaseResponse is BaseResponse.Error -> BaseResponse.Error(onError.invoke("").ifEmpty { mainBaseResponse.message })
            else -> BaseResponse.Empty
        }
    } else {
        when {
            mainBaseResponse is BaseResponse.Error -> BaseResponse.Error("Receive error: ${onError.invoke("").ifEmpty { mainBaseResponse.message }}")
            secondBaseResponse is BaseResponse.Error -> BaseResponse.Error("Other error: ${onError.invoke("").ifEmpty { secondBaseResponse.message }}")
            mainBaseResponse is BaseResponse.Success && secondBaseResponse is BaseResponse.Success -> BaseResponse.Success(onSuccess.invoke(mainBaseResponse.data, secondBaseResponse.data))
            else -> BaseResponse.Empty
        }
    }
}

fun <T> BaseResponse<DataSnapshot?>.handleBaseResponseConvertToObject(
    targetClass: Class<T>,
    errorMessage: String = "",
    onSuccess: (T) -> Unit = {},
): BaseResponse<T> = when (this) {
    is BaseResponse.Loading -> BaseResponse.Loading
    is BaseResponse.Empty -> BaseResponse.Empty
    is BaseResponse.Success -> {
        this.data.let { dataSnapshot ->
            dataSnapshot?.toObject(targetClass)?.let {
                onSuccess.invoke(it)
                BaseResponse.Success(it)
            } ?: kotlin.run {
                BaseResponse.Error(errorMessage)
            }
        }
    }

    is BaseResponse.Error -> BaseResponse.Error(this.message)
}

inline fun <reified T> BaseResponse<DataSnapshot?>.handleBaseResponseConvertToObjectList(
    defaultData: ArrayList<T> = arrayListOf(),
    onSuccess: (ArrayList<T>) -> Unit = {},
): BaseResponse<ArrayList<T>> = when (this) {
    is BaseResponse.Loading -> BaseResponse.Loading
    is BaseResponse.Empty -> BaseResponse.Empty
    is BaseResponse.Success -> {
        this.data.let { dataSnapshot ->
            dataSnapshot?.toObjectList(T::class.java)?.let { arrayList ->
                onSuccess.invoke(arrayList)
                BaseResponse.Success(arrayList)
            } ?: kotlin.run {
                onSuccess.invoke(defaultData)
                BaseResponse.Success(defaultData)
            }
        }
    }

    is BaseResponse.Error -> BaseResponse.Error(this.message)
}

