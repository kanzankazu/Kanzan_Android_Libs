@file:Suppress("unused")

package com.kanzankazu.kanzanutil.kanzanextension

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.distinctUntilChanged
import com.kanzankazu.kanzanutil.kanzanextension.type.debugMessageDebug
import com.kanzankazu.kanzanutil.kanzanextension.type.isNullOrZero

fun <T> MutableLiveData<T>.toLiveData(): LiveData<T> = this

fun <T> LiveData<T>.reObserve(owner: LifecycleOwner, observer: Observer<in T>) {
    removeObservers(owner) // Remove observers attached to LifecycleOwner
    observe(owner, observer) // Add the new observer
}

inline fun <reified T : ViewModel> FragmentActivity.vmInit(): T =
    ViewModelProvider(this)[T::class.java]

inline fun <reified T : ViewModel> Fragment.vmInit(fragmentActivity: FragmentActivity? = null): T =
    fragmentActivity?.vmInit() ?: ViewModelProvider(this)[T::class.java]

fun <T> FragmentActivity.vmLoadData(a: LiveData<T>, isWithDistinct: Boolean = false, observer: Observer<in T>) {
    if (isWithDistinct) a.distinctUntilChanged().observe(this, observer)
    else a.reObserve(this, observer)
}

//fun <T> FragmentActivity.vmLoadData(a: MutableLiveData<T>, observer: Observer<in T>): Unit = a.observe(this, observer)
fun <T> Fragment.vmLoadData(a: LiveData<T>, isWithDistinct: Boolean = false, observer: Observer<in T>) {
    if (isWithDistinct) a.distinctUntilChanged().observe(viewLifecycleOwner, observer)
    else a.reObserve(viewLifecycleOwner, observer)
}
//fun <T> Fragment.vmLoadData(a: MutableLiveData<T>, observer: Observer<in T>): Unit = a.observe(viewLifecycleOwner, observer)

fun <T> FragmentActivity.vmLoadDataRe(a: LiveData<T>, isWithDistinct: Boolean = false, observer: Observer<in T>) {
    if (isWithDistinct) a.distinctUntilChanged().reObserve(this, observer)
    else a.reObserve(this, observer)
}

//fun <T> FragmentActivity.vmLoadDataRe(a: MutableLiveData<T>, observer: Observer<in T>): Unit = a.reObserve(this, observer)
fun <T> Fragment.vmLoadDataRe(a: LiveData<T>, isWithDistinct: Boolean = false, observer: Observer<in T>) {
    if (isWithDistinct) a.distinctUntilChanged().reObserve(viewLifecycleOwner, observer)
    else a.reObserve(viewLifecycleOwner, observer)
}
//fun <T> Fragment.vmLoadDataRe(a: MutableLiveData<T>, observer: Observer<in T>): Unit = a.reObserve(viewLifecycleOwner, observer)

/**NEW*/

fun <T> LiveData<T>.getObject(default: T): T = value ?: default
fun <T> LiveData<T>.getForRequestObject(default: T, listener: (T) -> Boolean = { false }) =
    if (listener.invoke(this.getObject(default))) getObject(default) else null

fun LiveData<String>.get(default: String = "") = value ?: default
fun LiveData<Int>.get(default: Int = 0) = value ?: default
fun LiveData<Long>.get(default: Long = 0L) = value ?: default
fun LiveData<Double>.get(default: Double = 0.0) = value ?: default
fun LiveData<Float>.get(default: Float = 0F) = value ?: default
fun LiveData<Boolean>.get(default: Boolean = false) = value ?: default

fun LiveData<String>.getForRequest(default: String = "") =
    if (isNotNullOrEmptyOrZero()) get(default) else null

fun LiveData<Int>.getForRequest(default: Int = 0) =
    if (isNotNullOrEmptyOrZero()) get(default) else null

fun LiveData<Long>.getForRequest(default: Long = 0L) =
    if (isNotNullOrEmptyOrZero()) get(default) else null

fun LiveData<Double>.getForRequest(default: Double = 0.0) =
    if (isNotNullOrEmptyOrZero()) get(default) else null

fun LiveData<Float>.getForRequest(default: Float = 0F) =
    if (isNotNullOrEmptyOrZero()) get(default) else null

fun LiveData<Boolean>.getForRequest(default: Boolean = false) =
    if (isNotNullOrEmptyOrZero()) get(default) else null

fun LiveData<Pair<String, Boolean>>.getPairF(default: Pair<String, Boolean> = Pair("", false)) =
    (value ?: default).first

fun LiveData<Pair<Int, Boolean>>.getPairF(default: Pair<Int, Boolean> = Pair(0, false)) =
    (value ?: default).first

fun LiveData<Pair<Long, Boolean>>.getPairF(default: Pair<Long, Boolean> = Pair(0L, false)) =
    (value ?: default).first

fun LiveData<Pair<Double, Boolean>>.getPairF(default: Pair<Double, Boolean> = Pair(0.0, false)) =
    (value ?: default).first

fun LiveData<Triple<String, Boolean, Boolean>>.getTripleF(
    default: Triple<String, Boolean, Boolean> = Triple(
        "",
        false,
        true
    ),
) = (value ?: default).first

fun LiveData<Triple<Int, Boolean, Boolean>>.getTripleF(
    default: Triple<Int, Boolean, Boolean> = Triple(
        0,
        false,
        true
    ),
) = (value ?: default).first

fun LiveData<Triple<Long, Boolean, Boolean>>.getTripleF(
    default: Triple<Long, Boolean, Boolean> = Triple(
        0L,
        false,
        true
    ),
) = (value ?: default).first

fun LiveData<Triple<Double, Boolean, Boolean>>.getTripleF(
    default: Triple<Double, Boolean, Boolean> = Triple(
        0.0,
        false,
        true
    ),
) = (value ?: default).first

fun LiveData<Triple<Boolean, Boolean, Boolean>>.getTripleF(
    default: Triple<Boolean, Boolean, Boolean> = Triple(
        false,
        false,
        true
    ),
) = (value ?: default).first

fun MutableLiveData<String>.empty(default: String = "") = set(default)
fun MutableLiveData<Int>.empty(default: Int = 0) = set(default)
fun MutableLiveData<Long>.empty(default: Long = 0L) = set(default)
fun MutableLiveData<Double>.empty(default: Double = 0.0) = set(default)
fun MutableLiveData<Float>.empty(default: Float = 0F) = set(default)
fun MutableLiveData<Boolean>.empty(default: Boolean = false) = set(default)

fun <T> MutableLiveData<T>.set(data: T): T {
    this.value = data
    return data
}

fun <T> MutableLiveData<Pair<T, Boolean>>.set(data: T, second: Boolean = false) {
    this.value = Pair(data, second)
}

fun <T> MutableLiveData<Triple<T, Boolean, Boolean>>.set(
    data: T,
    second: Boolean = false,
    third: Boolean = true,
) {
    this.value = Triple(data, second, third)
}

/**
 * Convert a list of [MutableLiveData] into a single [MediatorLiveData].
 *
 * The created [MediatorLiveData] will observe all the [MutableLiveData] in the list and
 * call [onCheck] whenever any of them changes.
 *
 * If [isWithDistinct] is true, it will only call [onCheck] when the value of the observed
 * [MutableLiveData] changes.
 *
 * @param isWithDistinct whether to call [onCheck] only when the value changes
 * @param onCheck the function to call when any of the observed [MutableLiveData] changes
 * @return a [MediatorLiveData] that observes all the [MutableLiveData] in the list
 */
fun <R> List<MutableLiveData<*>>.toMediator(isWithDistinct: Boolean = true, onCheck: (List<MutableLiveData<*>>) -> R): MediatorLiveData<R> {
    val mediatorLiveData = MediatorLiveData<R>().apply {
        forEach {
            if (isWithDistinct) {
                addSource(it.distinctUntilChanged()) {
                    this@toMediator.debugMessageLivedata()
                    value = onCheck.invoke(this@toMediator)
                }
            } else {
                addSource(it) {
                    this@toMediator.debugMessageLivedata()
                    value = onCheck.invoke(this@toMediator)
                }
            }
        }
    }
    return mediatorLiveData
}


fun List<MutableLiveData<*>>.debugMessageLivedata() {
    if (!isDebugPublic()) return

    val logMessage = buildString {
        appendLine("==========START==========")
        this@debugMessageLivedata.forEachIndexed { index, liveData ->
            appendLine("index $index, ${liveData::class.simpleName} = ${liveData.value}")
        }
        append("**********END**********")
    }

    logMessage.debugMessageDebug()
}

private fun Any?.checkNullOrEmptyOrZero(isNegated: Boolean = false): Boolean {
    return this?.let { any ->
        when (any) {
            is String -> if (isNegated) any.isNotEmpty() else any.isEmpty()
            is Int -> if (isNegated) !any.isNullOrZero() else any.isNullOrZero()
            is Long -> if (isNegated) !any.isNullOrZero() else any.isNullOrZero()
            is Boolean -> if (isNegated) any else !any
            is Double -> if (isNegated) !any.isNullOrZero() else any.isNullOrZero()
            is Float -> if (isNegated) !any.isNullOrZero() else any.isNullOrZero()
            is List<*> -> if (isNegated) any.isNotEmpty() else any.isEmpty()
            else -> !isNegated
        }
    } ?: !isNegated
}

fun MutableLiveData<*>.isNullOrEmptyOrZero() = value.checkNullOrEmptyOrZero()
fun MutableLiveData<*>.isNotNullOrEmptyOrZero() = value.checkNullOrEmptyOrZero(true)
fun LiveData<*>.isNullOrEmptyOrZero() = value.checkNullOrEmptyOrZero()
fun LiveData<*>.isNotNullOrEmptyOrZero() = value.checkNullOrEmptyOrZero(true)