package com.kanzankazu.kanzanutil

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

object RxBus {
    private val disposables = CompositeDisposable()
    private val bus = PublishSubject.create<Any>()

    fun send(event: Any) {
        bus.onNext(event)
    }

    @JvmStatic
    fun <T> listen(eventType: Class<T>): Observable<T> {
        return bus.ofType(eventType)
    }

    fun <T> subscribe(
        eventType: Class<T>,
        onNext: (T) -> Unit,
        onError: ((Throwable) -> Unit)? = null,
    ) {
        val disposable = listen(eventType)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { event -> onNext(event) },
                { error -> onError?.invoke(error) ?: error.printStackTrace() }
            )
        disposables.add(disposable)
    }

    fun unsubscribe() {
        disposables.dispose()
    }
}