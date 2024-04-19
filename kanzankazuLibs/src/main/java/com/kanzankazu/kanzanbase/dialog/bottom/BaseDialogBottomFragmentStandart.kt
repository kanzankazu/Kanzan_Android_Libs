@file:Suppress("UNCHECKED_CAST", "DEPRECATION")

package com.kanzankazu.kanzanbase.dialog.bottom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import com.kanzankazu.kanzanbase.superall.BaseDialogBottomFragmentSuper

/**
 * Created by Faisal Bahri on 2020-02-11.
 */
abstract class BaseDialogBottomFragmentStandart : BaseDialogBottomFragmentSuper() {

    @LayoutRes
    protected abstract fun getLayoutView(): Int

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(getLayoutView(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.let {
            /*it.window?.apply { if (isTransparent()) setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT)) }*/
            it.setCanceledOnTouchOutside(isDismissAble())
            it.setCancelable(isDismissAble())
        }
        setActivityResult()
        setContent()
        setSubscribeToLiveData()
    }
}
