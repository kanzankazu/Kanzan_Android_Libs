/*
@file:Suppress("UNCHECKED_CAST")

package com.kanzankazu.kanzanbase

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.viewbinding.ViewBinding
import com.kanzankazu.R

*/
/**
 * Created by Faisal Bahri on 2020-02-11.
 *//*

abstract class BaseDialogFragmentData<VDB : ViewBinding> : BaseDialogFragmentSuper() {
    @LayoutRes
    protected abstract fun getBindView(): Int

    private var _binding: ViewBinding? = null
    protected val bind: VDB
        get() = _binding as VDB

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = DataBindingUtil.inflate(inflater, getBindView(), container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.let {
            //it.setContentView(bind.root)

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
            it.setCanceledOnTouchOutside(isDismissAbleOutsideDialog())
            it.setCancelable(isDismissAbleBackDialog())
        }
        setActivityResult()
        setSubscribeToLiveData()
        setContent()
        setListener()
    }
}
*/
