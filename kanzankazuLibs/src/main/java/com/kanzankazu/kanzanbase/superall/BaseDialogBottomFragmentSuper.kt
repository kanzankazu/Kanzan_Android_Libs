@file:Suppress("UNCHECKED_CAST", "DEPRECATION")

package com.kanzankazu.kanzanbase.superall

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kanzankazu.R
import com.kanzankazu.kanzanbase.BaseAdmob
import com.kanzankazu.kanzanwidget.dialog.BaseAlertDialog
import com.kanzankazu.kanzanwidget.dialog.BaseProgressDialog

/**
 * Created by Faisal Bahri on 2020-02-11.
 */
abstract class BaseDialogBottomFragmentSuper : BottomSheetDialogFragment() {
    val baseProgressDialog by lazy { BaseProgressDialog() }
    val baseAlertDialog by lazy { BaseAlertDialog() }
    val baseAdmob by lazy { BaseAdmob(requireActivity()) }

    protected abstract fun setContent()
    protected open fun setActivityResult() {}
    protected open fun setSubscribeToLiveData() {}

    open fun isFullScreen(): Boolean = false
    open fun isTransparent(): Boolean = true
    open fun isHideAble(): Boolean = true
    open fun isDismissAble(): Boolean = true
    open fun setPeekHeight(): Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isFullScreen()) setStyle(STYLE_NORMAL, R.style.BaseTheme_Fullscreen3)
        else if (isTransparent()) setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    /*override fun getTheme(): Int {
        return when {
            isFullScreen() -> R.style.BaseTheme_Fullscreen3
            isTransparent() -> R.style.CustomBottomSheetDialogTheme
            else -> 0
        }
    }*/

    /*override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = bindingInflater(inflater, container, false)
        return (_binding as VB).root
    }*/

    /*override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.let {
            *//*it.window?.apply { if (isTransparent()) setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) }*//*
            it.setContentView(bind.root)
            it.setCanceledOnTouchOutside(isDismissAble())
            it.setCancelable(isDismissAble())
        }
        setContent()
    }*/

    override fun onStart() {
        super.onStart()
        view?.apply {
            post {
                val params =
                    (parent as View).layoutParams as androidx.coordinatorlayout.widget.CoordinatorLayout.LayoutParams
                val bottomSheetBehavior = params.behavior as BottomSheetBehavior
                bottomSheetBehavior.peekHeight =
                    if (setPeekHeight() == null) measuredHeight else setPeekHeight()!!
                bottomSheetBehavior.isHideable = isHideAble()
            }
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
