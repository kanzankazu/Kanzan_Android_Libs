package com.kanzankazu.kanzannetwork.response.kanzanbaseresponse

import com.google.firebase.database.DataSnapshot
import com.kanzankazu.kanzanutil.kanzanextension.toObject
import com.kanzankazu.kanzanutil.kanzanextension.toObjectList


/**
 * Create a [BaseResponse.Loading] as a start point for loading process.
 * This is usually used as a start point for loading process, so that the UI can show a loading indicator.
 * For example, you can use it as follows:
 */
fun initBaseResponseLoading() = BaseResponse.Loading

/**
 * Create a [BaseResponse.Empty] as a end point for empty data process.
 * This is usually used as a end point for empty data process, so that the UI can show an empty state.
 * For example, you can use it as follows:
 */
fun initBaseResponseEmpty() = BaseResponse.Empty

/**
 * Convert this object to [BaseResponse.Success].
 * This is usually used as a end point for successful data process, so that the UI can show a success state.
 * For example, you can use it as follows:
 */
fun <T> T.toBaseResponseSuccess() = BaseResponse.Success(this)

/**
 * Convert this [String] to [BaseResponse.Error].
 * If the string is empty, [errorMessage] will be used as the error message.
 * Otherwise, this string will be used as the error message.
 * This is usually used as a end point for error data process, so that the UI can show an error state.
 * For example, you can use it as follows:
 */
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

/**
 * Handle the base response and execute the given functions.
 * This is a convenience function that simplifies the process of handling the base response.
 * It takes four parameters:
 * - [onLoading] a function that takes a boolean parameter, which is invoked when the response is loading.
 * - [onEmpty] a function that takes no parameter, which is invoked when the response is empty.
 * - [onError] a function that takes a string parameter, which is invoked when the response is an error.
 * - [onSuccess] a function that takes a parameter of type [T], which is invoked when the response is a success.
 *
 * The function will invoke the corresponding function according to the type of the response.
 * If the response is [BaseResponse.Loading], it will invoke [onLoading] with the value `true`.
 * If the response is [BaseResponse.Empty], it will invoke [onEmpty].
 * If the response is [BaseResponse.Success], it will invoke [onSuccess] with the value of the data.
 * If the response is [BaseResponse.Error], it will invoke [onError] with the error message.
 * The error message is obtained from the error object, and if the error object is an instance of [HttpError],
 * [NoInternetError], or [TimeOutError], it will use a specific error message, otherwise it will use the message of the error object.
 */
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

/**
 * Handle the base response and convert it to another base response.
 * This function takes one parameter, [onSuccess], which is a function that takes the data of the base response
 * and returns a new base response.
 * The function will return the new base response if the original response is a success,
 * or the original response if it is not a success.
 * The function is useful when you want to chain multiple base responses together.
 * For example, you can use it as follows:
 */
fun <T, R> BaseResponse<T>.handleBaseResponseConvert(
    onSuccess: (T) -> BaseResponse<R>,
): BaseResponse<R> = when (this) {
    is BaseResponse.Loading -> BaseResponse.Loading
    is BaseResponse.Empty -> BaseResponse.Empty
    is BaseResponse.Success -> onSuccess.invoke(this.data)
    is BaseResponse.Error -> BaseResponse.Error(this.message)
}

/**
 * Handle the base response and convert the data to another type.
 * This function takes two parameters, [onError] and [onSuccess].
 * [onError] is a function that takes the error message of the base response and returns a new error message.
 * [onSuccess] is a function that takes the data of the base response and returns a new data.
 * The function will return a new base response with the new data if the original response is a success,
 * or a new base response with the new error message if the original response is an error.
 * The function is useful when you want to convert the data of a base response to another type.
 * For example, you can use it as follows:
 */
fun <T, R> BaseResponse<T>.handleBaseResponseConvertData(
    onError: (String) -> String = { "" },
    onSuccess: (T) -> R,
): BaseResponse<R> = when (this) {
    is BaseResponse.Loading -> BaseResponse.Loading
    is BaseResponse.Empty -> BaseResponse.Empty
    is BaseResponse.Success -> BaseResponse.Success(onSuccess.invoke(this.data))
    is BaseResponse.Error -> BaseResponse.Error(onError.invoke("").ifEmpty { this.message })
}

/**
 * Combines two BaseResponse objects and provides a unified response.
 *
 * This function takes two BaseResponse objects and attempts to combine them into
 * a single response. It allows for custom handling of errors and successful
 * data extraction through lambda functions.
 *
 * @param mainBaseResponse The primary BaseResponse to be combined.
 * @param secondBaseResponse The secondary BaseResponse to be combined.
 * @param onError A lambda function that takes an error message and returns a new error message.
 *                Defaults to an empty string.
 * @param isStillShowSuccess A boolean flag indicating whether to still show success if only one
 *                           response is successful. Defaults to false.
 * @param onSuccess A lambda function that takes optional data from both responses and returns
 *                  a new data of type Y.
 * @return A new BaseResponse of type Y, which can be a success, error, or empty response based
 *         on the combination logic.
 */
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

/**
 * Converts the [BaseResponse] containing a [DataSnapshot] into a [BaseResponse] with an object of type [T].
 *
 * This function attempts to map the data within the [DataSnapshot] to an object of the specified [targetClass].
 * It handles various states of the response, including loading, empty, success, and error.
 *
 * @param targetClass The class type of the object to which the data should be converted.
 * @param errorMessage The error message to return if the conversion fails. Defaults to an empty string.
 * @param onSuccess A lambda function that is invoked with the successfully converted object of type [T].
 *
 * @return A [BaseResponse] containing the converted object if successful, or an error message if the conversion fails.
 *
 * If the [BaseResponse] is in the loading or empty state, it returns the corresponding [BaseResponse] state.
 * If the [BaseResponse] is successful, it tries to convert the data in [DataSnapshot] to the specified type [T].
 * If conversion is successful, it invokes [onSuccess] with the converted object and returns a successful [BaseResponse].
 * If conversion fails, it returns an error [BaseResponse] with the specified [errorMessage].
 * If the [BaseResponse] is an error, it returns an error [BaseResponse] with the existing error message.
 */
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

/**
 * Converts the [BaseResponse] containing a [DataSnapshot] into a [BaseResponse] with a list of objects of type [T].
 *
 * This function attempts to map the data within the [DataSnapshot] to a list of objects of the specified type [T].
 * It handles various states of the response, including loading, empty, success, and error.
 *
 * @param defaultData The default data to return if the conversion fails. Defaults to an empty [ArrayList].
 * @param onSuccess A lambda function that is invoked with the successfully converted list of objects of type [T].
 *
 * @return A [BaseResponse] containing the converted list of objects if successful, or an error message if the conversion fails.
 *
 * If the [BaseResponse] is in the loading or empty state, it returns the corresponding [BaseResponse] state.
 * If the [BaseResponse] is successful, it tries to convert the data in [DataSnapshot] to a list of objects of type [T].
 * If conversion is successful, it invokes [onSuccess] with the converted list of objects and returns a successful [BaseResponse].
 * If conversion fails, it returns an error [BaseResponse] with the existing error message.
 * If the [BaseResponse] is an error, it returns an error [BaseResponse] with the existing error message.
 */
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

inline fun <reified T> BaseResponse<DataSnapshot?>.handleBaseResponseConvertToType(
    asList: Boolean = false,
    defaultData: T? = null,
    onSuccess: (T) -> Unit = {},
): BaseResponse<T> = when (this) {
    is BaseResponse.Loading -> BaseResponse.Loading
    is BaseResponse.Empty -> BaseResponse.Empty
    is BaseResponse.Success -> {
        this.data?.let { dataSnapshot ->
            val result = if (asList) {
                dataSnapshot.toObjectList(T::class.java) as? T
            } else {
                dataSnapshot.toObject(T::class.java)
            }

            result?.let {
                onSuccess(it)
                BaseResponse.Success(it)
            } ?: BaseResponse.Error("Failed to convert data")
        } ?: BaseResponse.Error("Data is null")
    }
    is BaseResponse.Error -> BaseResponse.Error(this.message)
}

