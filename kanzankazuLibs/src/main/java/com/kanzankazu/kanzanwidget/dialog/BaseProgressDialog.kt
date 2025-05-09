@file:Suppress("DEPRECATION")

package com.kanzankazu.kanzanwidget.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.Window
import androidx.core.content.ContextCompat
import com.kanzankazu.R
import com.kanzankazu.databinding.LayoutProgressDialogBinding
import com.kanzankazu.kanzanutil.kanzanextension.type.debugMessageDebug
import com.kanzankazu.kanzanutil.kanzanextension.type.debugMessageError

class BaseProgressDialog {
    private var color: Int = -1
    private var mDialog: Dialog? = null

    /**@param color example(R.color.baseBlack)*/
    fun showDialog(context: Context, color: Int = R.color.baseBlack, message: String = "") {
        "BaseProgressDialog - showDialog".debugMessageDebug("BaseProgressDialog - showDialog")
        if (!isShowingDialog()) {
            this.color = color
            try {
                mDialog = Dialog(context)
                mDialog?.let { dialog ->
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialog.setContentView(R.layout.layout_progress_dialog)
                    dialog.setCancelable(false)
                    dialog.setCanceledOnTouchOutside(false)
                    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

                    LayoutProgressDialogBinding.inflate(LayoutInflater.from(context)).apply {
                        progressBar.indeterminateDrawable.setColorFilter(ContextCompat.getColor(context, color), PorterDuff.Mode.SRC_ATOP)
                        tvProgressDialog.text = message.ifEmpty { context.getString(R.string.label_please_wait_) }
                    }

                    dialog.show()
                }
            } catch (e: Exception) {
                e.debugMessageError("BaseProgressDialog - showDialog")
            }
        }
    }

    fun dismissDialog() {
        try {
            if (isShowingDialog()) {
                mDialog!!.dismiss()
                mDialog = null
            }
        } catch (e: Exception) {
            e.debugMessageError("BaseProgressDialog - dismissDialog")
            mDialog = null
        }
    }

    fun isShowingDialog(): Boolean {
        return mDialog != null && mDialog!!.isShowing
    }

    fun setupColorProgressDialog(color: Int = R.color.baseBlack) {
        this.color = color
    }
}
