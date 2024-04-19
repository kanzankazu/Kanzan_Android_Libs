@file:Suppress("UNCHECKED_CAST", "DEPRECATION")

package com.kanzankazu.kanzanbase.dialog.bottom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.kanzankazu.kanzanbase.superall.BaseDialogBottomFragmentSuper

/**
 * Created by Faisal Bahri on 2020-02-11.
 */
abstract class BaseDialogBottomFragmentViewV2<VB : ViewBinding> : BaseDialogBottomFragmentSuper() {

    lateinit var bind: VB

    abstract fun setViewBinding(inflater: LayoutInflater, container: ViewGroup?, b: Boolean): VB

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        bind = setViewBinding(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.let {
            /*it.window?.apply { if (isTransparent()) setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) }*/
            it.setContentView(bind.root)
            it.setCanceledOnTouchOutside(isDismissAble())
            it.setCancelable(isDismissAble())
        }
        setActivityResult()
        setContent()
        setSubscribeToLiveData()
    }
}
