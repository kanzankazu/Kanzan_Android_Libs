package com.kanzankazu.kanzanutil.kanzanextension

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

fun FragmentActivity.getLaunch(
    context: CoroutineContext = Dispatchers.Main,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    onError: ((Throwable) -> Unit)? = null,
    block: suspend CoroutineScope.() -> Unit,
): Job = lifecycleScope.launch(context + CoroutineExceptionHandler { _, throwable ->
    onError?.invoke(throwable)
}, start, block)

fun Fragment.getLaunch(
    context: CoroutineContext = Dispatchers.Main,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    onError: ((Throwable) -> Unit)? = null,
    block: suspend CoroutineScope.() -> Unit,
): Job = viewLifecycleOwner.lifecycleScope.launch(context + CoroutineExceptionHandler { _, throwable ->
    onError?.invoke(throwable)
}, start, block)

fun ViewModel.getLaunch(
    context: CoroutineContext = Dispatchers.Main,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    onError: ((Throwable) -> Unit)? = null,
    block: suspend CoroutineScope.() -> Unit,
): Job = viewModelScope.launch(context + CoroutineExceptionHandler { _, throwable ->
    onError?.invoke(throwable)
}, start, block)

suspend fun <T> FragmentActivity.mainCoContext(
    onError: ((Throwable) -> Unit)? = null,
    block: suspend CoroutineScope.() -> T,
): T? = try {
    withContext(Dispatchers.Main) { block() }
} catch (e: Throwable) {
    onError?.invoke(e)
    null
}

suspend fun <T> Fragment.mainCoContext(
    onError: ((Throwable) -> Unit)? = null,
    block: suspend CoroutineScope.() -> T,
): T? = try {
    withContext(Dispatchers.Main) { block() }
} catch (e: Throwable) {
    onError?.invoke(e)
    null
}

suspend fun <T> ioCoContext(
    onError: ((Throwable) -> Unit)? = null,
    block: suspend CoroutineScope.() -> T,
): T? = try {
    withContext(Dispatchers.IO) { block() }
} catch (e: Throwable) {
    onError?.invoke(e)
    null
}

suspend fun <T> CoroutineDispatcher.coContext(
    block: suspend CoroutineScope.() -> T,
): T {
    return withContext(this, block)
}