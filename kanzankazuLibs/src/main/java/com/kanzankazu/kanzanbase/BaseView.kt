package com.kanzankazu.kanzanbase

import android.text.SpannedString
import android.view.View
import android.widget.EditText
import androidx.annotation.StringRes

interface BaseView {

    fun showLoading()

    fun dismissLoading()

    fun isLoading(): Boolean

    /** @param type 0=Toast, 1=Snackbar*/
    fun showMessage(message: String, type: Int = 0, actionListener: View.OnClickListener = View.OnClickListener { })

    /** @param type 0=Toast, 1=Snackbar*/
    fun showMessage(message: SpannedString, type: Int = 0, actionListener: View.OnClickListener = View.OnClickListener { })

    /** @param type 0=Toast, 1=Snackbar*/
    fun showMessage(@StringRes message: Int, type: Int = 0, actionListener: View.OnClickListener = View.OnClickListener { })

    fun showAlert(message: String)

    fun showAlert(@StringRes message: Int)

    fun hideKeyboard()

    fun showKeyboard(editText: EditText)

    fun onBackPress()

    fun showSnackbarNoConnection(listener: () -> Unit)

    fun showRetryNoConnection(listener: () -> Unit)
}