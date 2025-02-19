@file:Suppress("UNCHECKED_CAST")

package com.kanzankazu.kanzanbase.superall

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.kanzankazu.R
import com.kanzankazu.kanzanbase.BaseAdmob
import com.kanzankazu.kanzanutil.kanzanextension.type.lazyNone
import com.kanzankazu.kanzanwidget.dialog.BaseAlertDialog
import com.kanzankazu.kanzanwidget.dialog.BaseProgressDialog

/**
 * Created by Faisal Bahri on 2020-02-11.
 */
abstract class BaseDialogFragmentSuper : DialogFragment() {
    val baseProgressDialog by lazyNone { BaseProgressDialog() }
    val baseAlertDialog by lazyNone { BaseAlertDialog() }
    val baseAdmob by lazyNone { BaseAdmob(requireActivity()) }

    protected open fun setActivityResult() {}
    protected open fun setSubscribeToLiveData() {}
    protected abstract fun setContent()
    protected open fun setListener() {}

    open fun isFullScreen(): Boolean = false
    open fun isFullWidth(): Boolean = false
    open fun isTransparent(): Boolean = true
    open fun isDismissAbleOutsideDialog(): Boolean = false
    open fun isDismissAbleBackDialog(): Boolean = false
    open fun setTitle(): String = ""
    open fun setDialogPosition(): Int = Gravity.NO_GRAVITY
    open fun setStyleDialogAnim(): Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isFullScreen()) setStyle(STYLE_NORMAL, R.style.BaseTheme_Fullscreen3)
        else if (isFullWidth()) setStyle(STYLE_NORMAL, R.style.FullWidth_Dialog)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        dialog.window?.let {
            // request a window without the title
            if (setTitle().isNotEmpty()) {
                it.setTitle(setTitle())
            } else {
                it.requestFeature(Window.FEATURE_NO_TITLE)
                it.setTitle("")
            }
        }
        return dialog
    }

    /*override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = bindingInflater(inflater, container, false)
        return (_binding as VB).root
    }*/

    /*override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.let {
            it.setContentView(bind.root)

            it.window?.apply {
                if (setDialogPosition() != Gravity.NO_GRAVITY) {
                    val width = ViewGroup.LayoutParams.MATCH_PARENT
                    val height = ViewGroup.LayoutParams.WRAP_CONTENT
                    it.window?.apply {
                        val wlp: WindowManager.LayoutParams = attributes
                        wlp.gravity = setDialogPosition()
                        wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv()

                        setLayout(width, height)
                        setBackgroundDrawableResource(R.drawable.bg_white_dialog)
                        setStyle(STYLE_NO_TITLE, R.style.DialogStyle)

                        attributes = wlp
                    }
                }
                if (isTransparent()) setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
            it.setCanceledOnTouchOutside(isDismissAble())
            it.setCancelable(isCancelable)
        }
        setContent()
    }*/

    override fun onStart() {
        super.onStart()
        if (setStyleDialogAnim() != 0) dialog?.let { it.window?.apply { setWindowAnimations(R.style.dialog_animation_shrinkfade) } }

        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.WRAP_CONTENT
            dialog.window?.setLayout(width, height)
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        if (activity != null && view != null) {
            val imm =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(requireView().windowToken, 0)
        }

        super.onDismiss(dialog)
    }

    fun show(manager: FragmentManager) = show(manager, "")
}
