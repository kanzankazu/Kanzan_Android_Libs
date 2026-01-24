/**
implementation 'io.reactivex.rxjava2:rxandroid:2.1.1'
implementation 'io.reactivex.rxjava2:rxkotlin:2.2.0'
implementation 'io.reactivex.rxjava2:rxandroid:2.1.1'
 */
@file:Suppress("NULLABLE_TYPE_PARAMETER_AGAINST_NOT_NULL_TYPE_PARAMETER")

package com.kanzankazu.kanzanutil.kanzanextension

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

fun <T> Observable<T>.addDispose(mCompositeDisposable: CompositeDisposable, onSuccess: (T) -> Unit, onThrowable: (throwable: Throwable) -> Unit = { _ -> }, schedulerSubscribeOn: Scheduler = Schedulers.io(), schedulerObserveOn: Scheduler = AndroidSchedulers.mainThread()) {
    val disposable = this
        .subscribeOn(schedulerSubscribeOn)
        .observeOn(schedulerObserveOn)
        .subscribe({
            onSuccess(it)
        }, {
            onThrowable(it)
        })
    mCompositeDisposable.add(disposable)
}

fun <T> Observable<T>.addDispose(schedulerSubscribeOn: Scheduler, schedulerObserveOn: Scheduler): Observable<T> = this.subscribeOn(schedulerSubscribeOn).observeOn(schedulerObserveOn)

fun <T, R> Observable<T>.convert(listener: (T) -> R): Observable<R> = this.flatMap { Observable.just(listener(it)) }

fun <T> Single<T>.addDispose(mCompositeDisposable: CompositeDisposable, onSuccess: (T) -> Unit, onThrowable: (throwable: Throwable) -> Unit = { _ -> }, schedulerSubscribeOn: Scheduler = Schedulers.io(), schedulerObserveOn: Scheduler = AndroidSchedulers.mainThread()) {
    val disposable = this
        .subscribeOn(schedulerSubscribeOn)
        .observeOn(schedulerObserveOn)
        .subscribe({
            onSuccess(it)
        }, {
            onThrowable(it)
        })
    mCompositeDisposable.add(disposable)
}

fun <T> Single<T>.addDispose(schedulerSubscribeOn: Scheduler, schedulerObserveOn: Scheduler): Single<T> = this.subscribeOn(schedulerSubscribeOn).observeOn(schedulerObserveOn)

fun <T, R> Single<T>.convert(listener: (T) -> R): Single<R> = this.flatMap { Single.just(listener(it)) }

fun Completable.addDispose(mCompositeDisposable: CompositeDisposable, onSuccess: () -> Unit, onThrowable: (throwable: Throwable) -> Unit = { _ -> }, schedulerSubscribeOn: Scheduler = Schedulers.io(), schedulerObserveOn: Scheduler = AndroidSchedulers.mainThread()) {
    val disposable = this
        .subscribeOn(schedulerSubscribeOn)
        .observeOn(schedulerObserveOn)
        .subscribe({
            onSuccess()
        }, {
            onThrowable(it)
        })
    mCompositeDisposable.add(disposable)
}

fun Completable.addDispose(schedulerSubscribeOn: Scheduler, schedulerObserveOn: Scheduler): Completable = this.subscribeOn(schedulerSubscribeOn).observeOn(schedulerObserveOn)

fun Disposable.disposedBy(compositeDisposable: CompositeDisposable) {
    compositeDisposable.add(this)
}