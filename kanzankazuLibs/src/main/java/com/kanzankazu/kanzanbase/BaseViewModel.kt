@file:Suppress("MemberVisibilityCanBePrivate", "unused", "PropertyName")

package com.kanzankazu.kanzanbase

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * Created by Faisal Bahri on 2020-02-11.
 */
abstract class BaseViewModel(
    private val injectedDispatcher: CoroutineDispatcher? = null,
) : ViewModel() {

    //protected var _loading = MutableLiveData<Boolean>()
    //val loading = _loading.toLiveData()
    //protected var _error = MutableLiveData<String>()
    //val error = _error.toLiveData()
    //protected var _noConnection = MutableLiveData<() -> Unit>()
    //val noConnection = _noConnection.toLiveData()

    /*RxJava*/
    protected val mCompositeDisposable = CompositeDisposable()
    protected fun addDisposable(disposable: Disposable) {
        mCompositeDisposable.add(disposable)
    }

    fun subscribe() {}
    fun unSubscribe() = mCompositeDisposable.clear()

    /*Coroutine*/
    private val supervisorJob = SupervisorJob()
    private val coroutineContext: CoroutineContext = supervisorJob
    fun getSupervisorJob() = supervisorJob
    override fun onCleared() {
        try {
            supervisorJob.cancel()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        super.onCleared()
    }

    fun defaultViewModelScope(coroutineContext: CoroutineContext? = null, listener: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch(coroutineContext ?: (Dispatchers.IO + getSupervisorJob())) { listener.invoke(this) }
    }

    //TODO("need to handle add analytic sender")
    /*open fun onTrackEvent(eventName: String, param: Map<String, Any?>) {
        if (eventName.isNotEmpty() && analyticsSender != null) {
            analyticsSender.sendEvent(AnalyticsEvents(eventName, param))
        } else {
            if (isDebug()) _error.postValue("analyticsSender null")
        }
    }*/

    /* protected fun <T : Any> requestApiCall(endpoint: suspend () -> ApiResult<T>, showProgress: Boolean = true, onSuccess: (T) -> Unit) {
         injectedDispatcher?.let {
             if (showProgress) _loading.postValue(true)
             viewModelScope.launch(it) {
                 endpoint().handleResult { res ->
                     onSuccess.invoke(res)
                 }
             }
         } ?: kotlin.run {
             if (isDebug()) _error.postValue("injectedDispatcher null")
         }
     }*/

    /*protected fun <T : Any> ApiResult<T>.handleResult(listener: (T) -> Unit) {
        when (val result = this) {
            is ApiResult.Success -> {
                _loading.postValue(false)
                listener.invoke(result.data)
            }

            is ApiResult.Error -> {
               _loading.postValue(false)
               _error.postValue(result.exception.message)
            }
        }
    }*/

    /*protected fun <T : Any> requestApiCallResponse(endpoint: suspend () -> BaseResponse<T>, showProgress: Boolean = true, onSuccess: (T) -> Unit) {
        injectedDispatcher?.let {
            if (showProgress) _loading.postValue(true)
            viewModelScope.launch(injectedDispatcher) {
                endpoint().handleResult {
                    onSuccess(it)
                }
            }
        } ?: kotlin.run {
            if (isDebug()) _error.postValue("injectedDispatcher null")
        }
    }*/

    /*protected fun <T> BaseResponse<T>.handleResult(handleNoConnection: (() -> Unit)? = null, listener: (T) -> Unit) {
        when (val response = this) {
            is BaseResponse.Empty -> {
                _loading.postValue(false)
                _empty.postValue(Unit)
            }

            is BaseResponse.Loading -> {
                _loading.postValue(true)
            }

            is BaseResponse.Error -> {
                _loading.postValue(false)
                when (response) {
                    is HttpError -> //_error.postValue(response.message)
                    is NoInternetError, is TimeOutError -> //_noConnection.postValue(
                        handleNoConnection!!
                    )

                    else -> {}
                }
            }

            is BaseResponse.Success -> {
                _loading.postValue(false)
                listener.invoke(response.data)
            }

        }
    }*/

    /*protected fun <T> handleResult(block: suspend () -> BaseResponse<T>) =
        flow { emit(block.invoke()) }
            .onStart { emit(BaseResponse.Loading) }
            .stateIn(viewModelScope, SharingStarted.Lazily, BaseResponse.Empty)*/

}