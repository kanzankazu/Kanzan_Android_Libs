/*
@file:Suppress("UNCHECKED_CAST", "DEPRECATION")

package com.kanzankazu.kanzanbase

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.viewbinding.ViewBinding

*/
/**
 * Created by Faisal Bahri on 2020-02-11.
 *//*

abstract class BaseDialogBottomFragmentData<VDB : ViewBinding> : BaseDialogBottomFragmentSuper() {
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
            */
/*it.window?.apply { if (isTransparent()) setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) }*//*

            it.setContentView(bind.root)
            it.setCanceledOnTouchOutside(isDismissAble())
            it.setCancelable(isDismissAble())
        }
        setActivityResult()
        setContent()
        setSubscribeToLiveData()
    }
}
*/
