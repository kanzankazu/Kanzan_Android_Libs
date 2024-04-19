@file:Suppress("UNCHECKED_CAST")

package com.kanzankazu.kanzanbase.dialog.standart

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.LayoutRes
import com.kanzankazu.R
import com.kanzankazu.kanzanbase.superall.BaseDialogFragmentSuper

/**
 * Created by Faisal Bahri on 2020-02-11.
 */
abstract class BaseDialogFragmentStandart : BaseDialogFragmentSuper() {

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
