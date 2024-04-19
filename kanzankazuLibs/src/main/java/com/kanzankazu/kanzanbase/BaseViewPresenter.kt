package com.kanzankazu.kanzanbase

interface BaseViewPresenter : BaseView {

    fun setPresenter(presenter: BasePresenter<*>)

}