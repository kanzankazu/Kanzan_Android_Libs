package com.kanzankazu.kanzanbase

/**
 * Created by kanzan on 27/09/21.
 */
interface BasePresenterContract<in V : BaseViewPresenter> {

    fun attachView(view: V)

    fun detachView()
}