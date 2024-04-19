package com.kanzankazu.kanzanwidget.dialog

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.fragment.app.FragmentManager
import com.kanzankazu.R
import com.kanzankazu.databinding.DialogBaseInfoBinding
import com.kanzankazu.kanzanbase.dialog.standart.BaseDialogFragmentView
import com.kanzankazu.kanzanutil.kanzanextension.type.debugMessage
import com.kanzankazu.kanzanutil.kanzanextension.view.isVisible
import com.kanzankazu.kanzanutil.kanzanextension.view.visibleView

open class BaseInfoDialog : BaseDialogFragmentView<DialogBaseInfoBinding>() {

    private var imageAsset: Int = -1
    private var _isDismissAbleDialog: Boolean = false
    private var _isDismissAbleBack: Boolean = false
    private var _isCancelableDialog: Boolean = false
    private var activity: Activity? = null
    private var imageUrl: String? = null
    private var message: String? = null
    private var title: String? = null
    private var btn1Title: String? = null
    private var btn2Title: String? = null
    private lateinit var btn1Listener: () -> Unit
    private lateinit var btn2Listener: () -> Unit
    private lateinit var btnCancelListener: () -> Unit

    override fun isDismissAbleOutsideDialog() = _isDismissAbleDialog

    override fun isDismissAbleBackDialog() = _isDismissAbleBack

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> DialogBaseInfoBinding
        get() = DialogBaseInfoBinding::inflate

    override fun setContent(): Unit = with(bind) {
        "BaseInfoDialog - setContent".debugMessage()
        activity?.let { ivDialogInfoImage.visibleView(it, imageUrl, imageAsset) }
        cvDialogInfoImageParent.visibleView(ivDialogInfoImage.isVisible())
        ivDialogInfoCloseBtn.visibleView(_isCancelableDialog)
        tvDialogInfoTitle.visibleView(title)
        tvDialogInfoDesc.visibleView(message)
        tvDialogInfoBtn1.visibleView(btn1Title)
        tvDialogInfoBtn2.visibleView(btn2Title)
        layDialogInfoBtnParent.visibleView(tvDialogInfoBtn1.isVisible() || tvDialogInfoBtn2.isVisible())

        ivDialogInfoCloseBtn.setOnClickListener { dismiss() }
        tvDialogInfoBtn1.setOnClickListener {
            dismiss()
            btn1Listener()
        }
        tvDialogInfoBtn2.setOnClickListener {
            dismiss()
            btn2Listener()
        }
    }

    companion object {
        fun newInstanceBaseInfoDialog(
            fm: FragmentManager,
            activity: Activity,
            title: String = "",
            message: String = "",
            imageUrl: String = "",
            @DrawableRes imageAsset: Int = -1,
            isCancelable: Boolean = false,
            isDismissAbleOutside: Boolean = false,
            isDismissAbleBack: Boolean = false,
            btn1Title: String? = null,
            btn2Title: String? = null,
            btn1Listener: () -> Unit = {},
            btn2Listener: () -> Unit = {},
            btnCancelListener: () -> Unit = {},
        ) {
            val instance = BaseInfoDialog()
            instance.activity = activity
            instance.imageUrl = imageUrl
            instance.imageAsset = imageAsset
            instance.title = title
            instance.message = message
            instance.btn1Title = btn1Title
            instance.btn2Title = btn2Title
            instance.btn1Listener = btn1Listener
            instance.btn2Listener = btn2Listener
            instance.btnCancelListener = btnCancelListener
            instance._isCancelableDialog = isCancelable
            instance._isDismissAbleDialog = isDismissAbleOutside
            instance._isDismissAbleBack = isDismissAbleBack
            instance.show(fm)
        }

        fun newInstanceBaseInfoDialogYesNo(fm: FragmentManager, activity: Activity, title: String, message: String, btn1Listener: () -> Unit = {}, btn2Listener: () -> Unit = {}) {
            "newInstanceBaseInfoDialogYesNo".debugMessage()
            title.debugMessage()
            message.debugMessage()
            newInstanceBaseInfoDialog(
                fm,
                activity,
                title,
                message,
                btn1Title = activity.getString(R.string.label_ya),
                btn2Title = activity.getString(R.string.label_tidak),
                btn1Listener = btn1Listener,
                btn2Listener = btn2Listener
            )
        }

        fun newInstanceBaseInfoDialogOk(fm: FragmentManager, activity: Activity, title: String, message: String, btn1Listener: () -> Unit = {}) {
            "newInstanceBaseInfoDialogOk".debugMessage()
            title.debugMessage()
            message.debugMessage()
            newInstanceBaseInfoDialog(
                fm,
                activity,
                title,
                message,
                btn1Title = activity.getString(R.string.label_ok),
                btn1Listener = btn1Listener
            )
        }
    }
}
