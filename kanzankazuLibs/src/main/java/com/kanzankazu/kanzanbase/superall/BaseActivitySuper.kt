package com.kanzankazu.kanzanbase.superall

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.kanzankazu.R
import com.kanzankazu.kanzanbase.BaseAdmob
import com.kanzankazu.kanzannetwork.NetworkLiveData
import com.kanzankazu.kanzannetwork.NetworkStatus
import com.kanzankazu.kanzanutil.kanzanextension.getLifeCycleOwner
import com.kanzankazu.kanzanutil.kanzanextension.type.debugMessageDebug
import com.kanzankazu.kanzanwidget.dialog.BaseAlertDialog
import com.kanzankazu.kanzanwidget.dialog.BaseInfoDialog
import com.kanzankazu.kanzanwidget.dialog.BaseProgressDialog

/**
 * Created by Faisal Bahri on 2020-02-11.
 */
abstract class BaseActivitySuper : AppCompatActivity() {
    val baseProgressDialog by lazy { BaseProgressDialog() }
    val baseAlertDialog by lazy { BaseAlertDialog() }
    val baseAdmob by lazy { BaseAdmob(this) }
    val networkLiveData by lazy { NetworkLiveData(this) }

    protected open fun setActivityResult() {}
    protected open fun setSubscribeToLiveData() {}
    protected open fun getBundleData() {}
    open fun handleConnection(internetConnection: NetworkStatus) {}
    protected abstract fun setContent()
    protected open fun setListener() {}
    protected open fun getData() {}

    /**@return example R.menu.menu*/
    open fun setOptionMenu(): Int = -1

    /**when (id) { R.id.menuId -> sampleFun() }*/
    open fun setOptionMenuListener(id: Int, item: MenuItem) {}

    /**example MenuItem xxx = menu.findItem(R.id.xxx);*/
    open fun setOptionMenuValidation(menu: Menu) {}

    open fun onBackPressedListener() {}

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (setOptionMenu() != -1) menuInflater.inflate(setOptionMenu(), menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        setOptionMenuListener(id, item)
        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        setOptionMenuValidation(menu)
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        OnBackPressedDispatcher().addCallback(getLifeCycleOwner(), object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBackPressedListener()
            }
        })
    }

    fun toast(
        message: CharSequence,
        duration: Int = Toast.LENGTH_LONG,
    ) {
        if (message.isNotEmpty()) Toast.makeText(this, message, duration).show()
        else "showToast BaseActivitySuper message.isNullOrEmpty()".debugMessageDebug()
    }

    fun snackBar(
        message: CharSequence,
        duration: Int = Snackbar.LENGTH_LONG,
        actionText: CharSequence = "",
        actionTextColor: Int = R.color.baseBlack,
        actionListener: View.OnClickListener = View.OnClickListener { },
    ) {
        if (message.isNotEmpty()) {
            val snackbar = Snackbar.make(this.findViewById(android.R.id.content), message, duration)
            if (actionText.isNotEmpty()) {
                snackbar.setActionTextColor(ContextCompat.getColor(this, actionTextColor))
                snackbar.setAction(actionText, actionListener)
            }
            snackbar.show()
        } else "showSnackbar BaseActivity message.isNullOrEmpty()".debugMessageDebug()
    }

    fun snackBarNoConnection(
        listener: () -> Unit,
        message: CharSequence = "Sedang ada masalah dengan koneksi anda silahkan cek koneksi anda",
        actionText: String = "Ulangi",
        actionTextColor: Int = R.color.baseWhite,
    ) {
        snackBar(
            message,
            Snackbar.LENGTH_INDEFINITE,
            actionText,
            actionTextColor
        ) { listener() }
    }

    fun retryDialog(
        listener: () -> Unit,
        title: String = "Informasi",
        message: String = "Sedang ada masalah dengan koneksi anda silahkan cek koneksi anda",
    ) {
        BaseInfoDialog.newInstanceBaseInfoDialogOk(
            supportFragmentManager,
            this,
            title,
            message,
        ) { listener() }
    }

    fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun showKeyboard(editText: EditText) {
        val view = this.currentFocus
        if (view != null) {
            editText.requestFocus()
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        }
    }
}
