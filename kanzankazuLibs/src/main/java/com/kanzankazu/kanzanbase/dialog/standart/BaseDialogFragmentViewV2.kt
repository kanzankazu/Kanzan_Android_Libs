package com.kanzankazu.kanzanbase.dialog.standart

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.viewbinding.ViewBinding
import com.kanzankazu.R
import com.kanzankazu.kanzanbase.superall.BaseDialogFragmentSuper

abstract class BaseDialogFragmentViewV2<VB : ViewBinding> : BaseDialogFragmentSuper() {

    // Properti `bind` dibuat nullable untuk menghindari UninitializedPropertyAccessException
    private var _bind: VB? = null
    protected val bind: VB
        get() = _bind ?: throw IllegalStateException("ViewBinding is not initialized")

    abstract fun setViewBinding(inflater: LayoutInflater, container: ViewGroup?, b: Boolean): VB

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _bind = setViewBinding(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dialog = dialog ?: return // Early return jika dialog null

        dialog.window?.let { window ->
            if (setDialogPosition() != Gravity.NO_GRAVITY) {
                val width = ViewGroup.LayoutParams.MATCH_PARENT
                val height = ViewGroup.LayoutParams.WRAP_CONTENT
                val layoutParams = window.attributes
                layoutParams.gravity = setDialogPosition()
                layoutParams.flags = layoutParams.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv() // Hilangkan background blur

                window.apply {
                    setLayout(width, height)
                    setBackgroundDrawableResource(R.drawable.bg_white_dialog) // Pastikan resource tersedia
                    attributes = layoutParams
                }
            }
            if (isTransparent()) {
                window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
        }

        dialog.setCanceledOnTouchOutside(isDismissAbleOutsideDialog())
        dialog.setCancelable(isDismissAbleBackDialog())

        setActivityResult()
        setSubscribeToLiveData()
        setContent()
        setListener()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _bind = null // Bersihkan binding untuk menghindari memory leak
    }
}
