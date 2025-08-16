package com.kanzankazu.kanzanbase

import androidx.annotation.VisibleForTesting
import com.kanzankazu.kanzanutil.kanzanextension.type.lazyNone
import com.kanzankazu.kanzanutil.scheduler.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.lang.ref.WeakReference

/**
 * Base class for presenters in MVP (Model-View-Presenter) pattern.
 * 
 * @param V The type of the View interface that this presenter will manage.
 * 
 * Example usage:
 * ```
 * class MyPresenter : BasePresenter<MyView>() {
 *     fun loadData() {
 *         // Implementation
 *     }
 * }
 * ```
 */
open class BasePresenter<V : BaseViewPresenter> constructor(var scheduler: SchedulerProvider) : BasePresenterContract<V> {

    /**
     * Mendapatkan instance View yang sedang terhubung.
     * Dapat bernilai null jika View sudah dilepaskan.
     *
     * # Contoh Penggunaan:
     * ```kotlin
     * // Di dalam kelas turunan BasePresenter
     * fun someOperation() {
     *     view?.apply {
     *         // Aman mengakses method view di sini
     *         showLoading()
     *         // ... operasi lainnya
     *         hideLoading()
     *     } ?: run {
     *         // Handle case ketika view null
     *         Log.d("BasePresenter", "View is detached")
     *     }
     * }
     * ```
     */
    val view: V?
        get() = if (isViewDetached) null else weakReference?.get()

    @Volatile
    /**
     * Flag to indicate whether the view is detached or not.
     * Used to prevent presenter from doing some operations when the view is detached.
     */
    private var isViewDetached = false

    /**
     * WeakReference untuk menyimpan referensi ke View.
     * Mencegah memory leak dengan menggunakan WeakReference.
     */
    private var weakReference: WeakReference<V>? = null

    /**
     * CompositeDisposable untuk mengelola subscription-subscription RxJava.
     * Akan diinisialisasi secara lazy dan hanya sekali.
     */
    private val mCompositeDisposable by lazyNone {
        CompositeDisposable()
    }

    /**
     * Mengecek apakah View sedang terhubung ke Presenter.
     */
    private val isViewAttached: Boolean
        get() = weakReference != null && weakReference!!.get() != null

    /**
     * Menambahkan disposable ke dalam CompositeDisposable untuk manajemen memory yang lebih baik.
     *
     * @param subscription Disposable yang akan ditambahkan.
     *
     * # Contoh Penggunaan:
     * ```kotlin
     * // Di dalam kelas turunan BasePresenter
     * fun loadData() {
     *     val disposable = apiService.getData()
     *         .subscribeOn(Schedulers.io())
     *         .observeOn(AndroidSchedulers.mainThread())
     *         .subscribe(
     *             { response -> view?.showData(response) },
     *             { error -> view?.showError(error.message) }
     *         )
     *     addDisposable(disposable) // Menambahkan disposable ke dalam composite disposable
     * }
     * ```
     */
    protected fun addDisposable(subscription: Disposable) {
        mCompositeDisposable.add(subscription)
    }

    /**
     * Menghubungkan View dengan Presenter.
     *
     * @param view Instance dari View yang akan dihubungkan.
     */
    override fun attachView(view: V) {
        if (!isViewAttached) {
            weakReference = WeakReference(view)
            view.setPresenter(this)
        }
    }

    /**
     * Melepaskan referensi View dan membersihkan semua disposable.
     * Harus dipanggil ketika Activity/Fragment dihancurkan.
     *
     * # Contoh Penggunaan:
     * ```kotlin
     * // Di dalam Activity/Fragment
     * override fun onDestroy() {
     *     super.onDestroy()
     *     presenter.detachView()
     * }
     * ```
     */
    override fun detachView() {
        isViewDetached = true
        weakReference?.clear()
        weakReference = null
        mCompositeDisposable.clear()
    }

    /**
     * Membersihkan semua disposable yang ada.
     * Dipanggil secara internal saat [detachView].
     */
    private fun dispose() {
        if (mCompositeDisposable.size() > 0) mCompositeDisposable.clear()
    }

    @VisibleForTesting
    fun setViewForTesting(view: V) {
        weakReference = WeakReference(view)
    }

    // Tambahkan method untuk memeriksa apakah view masih aktif
    fun isViewAlive(): Boolean {
        return view?.let { true } ?: false
    }
}