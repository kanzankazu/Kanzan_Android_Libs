package com.kanzankazu.kanzanutil

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.kanzankazu.kanzanutil.kanzanextension.getLaunch
import kotlinx.coroutines.GlobalScope.coroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterIsInstance


object EventBusHelper {
    //prevent event subscribe not call in nested fragment (init replay value, default = 0)
    //https://stackoverflow.com/questions/70963343/why-is-the-collect-of-a-flow-in-a-nested-fragment-viewmodel-not-called
    private val _eventsReplay = MutableSharedFlow<Any>(replay = 1)
    val eventsReplay = _eventsReplay.asSharedFlow()

    private val _events = MutableSharedFlow<Any>()
    val events = _events.asSharedFlow()

    private val _eventsUnlimited = MutableSharedFlow<Any>()
    val eventsUnlimited = _eventsUnlimited.asSharedFlow()

    suspend fun publish(event: Any) {
        _events.emit(event)
    }


    suspend inline fun <reified T> subscribe(crossinline onEvent: (T) -> Unit) {
        events.filterIsInstance<T>()
            .collectLatest { event ->
                coroutineContext.ensureActive()
                onEvent(event)
            }
    }


    suspend fun publishEventWithReplay(event: Any) {
        _eventsReplay.emit(event)
    }


    suspend inline fun <reified T> subscribeWithReplay(crossinline onEvent: (T) -> Unit) {
        eventsReplay.filterIsInstance<T>()
            .collectLatest { event ->
                coroutineContext.ensureActive()
                onEvent(event)
            }
    }


    suspend fun publishUnlimitedEvents(event: Any) {
        _eventsUnlimited.emit(event)
    }


    suspend inline fun <reified T> subscribeUnlimitedEvents(crossinline onEvent: (T) -> Unit) {
        eventsUnlimited.filterIsInstance<T>()
            .collect { event ->
                coroutineContext.ensureActive()
                onEvent(event)
            }
    }
}

inline fun <reified T> FragmentActivity.subscribeEventBusHelper(crossinline onEvent: (T) -> Unit) {
    getLaunch {
        repeatOnLifecycle(Lifecycle.State.CREATED) {
            EventBusHelper.subscribe(onEvent)
        }
    }
}

inline fun <reified T> Fragment.subscribeEventBusHelper(crossinline onEvent: (T) -> Unit) {
    getLaunch {
        repeatOnLifecycle(Lifecycle.State.CREATED) {
            EventBusHelper.subscribe(onEvent)
        }
    }
}
