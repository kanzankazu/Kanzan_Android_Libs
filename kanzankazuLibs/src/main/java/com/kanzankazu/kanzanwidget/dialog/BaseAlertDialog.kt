@file:Suppress("MemberVisibilityCanBePrivate")

package com.kanzankazu.kanzanwidget.dialog

import android.app.Activity
import android.app.Dialog
import android.view.LayoutInflater
import android.view.Window
import com.kanzankazu.R
import com.kanzankazu.databinding.LayoutAlertDialogBinding
import com.kanzankazu.kanzanutil.kanzanextension.type.debugMessage
import com.kanzankazu.kanzanutil.kanzanextension.view.setOnSingleClickListener
import com.kanzankazu.kanzanutil.kanzanextension.view.visibleView

class BaseAlertDialog {
    private var mDialog: Dialog? = null

    fun showDialog(
        mActivity: Activity,
        message: String,
        rightActionLabel: String = mActivity.getString(R.string.label_ok),
    ) {
        "BaseAlertDialog - showDialog".debugMessage()
        "$message BaseAlertDialog - showDialog".debugMessage()
        "$rightActionLabel BaseAlertDialog - showDialog".debugMessage()
        try {
            if (!mActivity.isFinishing) {
                mDialog = Dialog(mActivity)
                mDialog?.let { dialog ->
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialog.setContentView(R.layout.layout_alert_dialog)
                    dialog.setCancelable(false)
                    dialog.setCanceledOnTouchOutside(false)
                    dialog.window?.attributes?.windowAnimations = R.style.dialog_animation_shrinkfade
                    dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

                    LayoutAlertDialogBinding.inflate(LayoutInflater.from(mActivity)).apply {
                        tvDialogAlertMessage.text = message
                        tvDialogAlertActionRight.visibleView(rightActionLabel)
                        tvDialogAlertActionRight.setOnSingleClickListener {
                            dismissDialog()
                        }
                    }

                    dialog.show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun dismissDialog() {
        if (mDialog != null) {
            if (mDialog!!.isShowing) {
                mDialog!!.dismiss()
                mDialog = null
            }
        } else {
            mDialog = null
        }
    }

    fun isShowingDialog(): Boolean = mDialog != null

}