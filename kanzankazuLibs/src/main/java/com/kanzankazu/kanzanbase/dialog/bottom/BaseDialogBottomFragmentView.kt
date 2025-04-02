@file:Suppress("UNCHECKED_CAST")

package com.kanzankazu.kanzanbase.dialog.bottom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.kanzankazu.kanzanbase.superall.BaseDialogBottomFragmentSuper

/**
 * Refactored by AI Assistant - Improved BaseDialogBottomFragmentView
 */
abstract class BaseDialogBottomFragmentView<VB : ViewBinding> : BaseDialogBottomFragmentSuper() {

    // Protected property for safer access to binding
    private var _binding: VB? = null
    protected val bind: VB
        get() = _binding ?: throw IllegalStateException("ViewBinding is accessed before initialization or after destruction")

    // Abstract function to get ViewBinding instance
    abstract val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = bindingInflater(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDialog()
        setActivityResult()
        setContent()
        setSubscribeToLiveData()
    }

    private fun setupDialog() {
        dialog?.apply {
            setContentView(bind.root)
            setCanceledOnTouchOutside(isDismissAble())
            setCancelable(isDismissAble())
        }
    }

    // Clean up binding when fragment view is destroyed
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}