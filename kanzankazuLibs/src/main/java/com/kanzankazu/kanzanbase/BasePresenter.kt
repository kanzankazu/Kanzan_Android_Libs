package com.kanzankazu.kanzanbase

import com.kanzankazu.kanzanutil.scheduler.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.lang.ref.WeakReference

open class BasePresenter<V : BaseViewPresenter> constructor(var scheduler: SchedulerProvider) : BasePresenterContract<V> {

    private val mCompositeDisposable by lazy {
        CompositeDisposable()
    }

    private var weakReference: WeakReference<V>? = null

    override fun attachView(view: V) {
        if (!isViewAttached) {
            weakReference = WeakReference(view)
            view.setPresenter(this)
        }
    }

    protected fun addDisposable(subscription: Disposable) {
        mCompositeDisposable.add(subscription)
    }

    override fun detachView() {
        weakReference?.clear()
        weakReference = null
        mCompositeDisposable.clear()
    }

    private fun dispose() {
        if (mCompositeDisposable.size() > 0) mCompositeDisposable.clear()
    }

    val view: V?
        get() = weakReference?.get()

    private val isViewAttached: Boolean
        get() = weakReference != null && weakReference!!.get() != null
}