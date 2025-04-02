@file:Suppress("UNCHECKED_CAST")

package com.kanzankazu.kanzanbase.dialog.bottom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.kanzankazu.kanzanbase.superall.BaseDialogBottomFragmentSuper

/**
 * Refactored by AI Assistant - Improved BaseDialogBottomFragmentViewV2
 */
abstract class BaseDialogBottomFragmentViewV2<VB : ViewBinding> : BaseDialogBottomFragmentSuper() {

    private var _binding: VB? = null
    protected val binding: VB
        get() = _binding ?: throw IllegalStateException("ViewBinding is accessed before initialization or after destruction")

    // Abstract function to create ViewBinding
    abstract fun setViewBinding(inflater: LayoutInflater, container: ViewGroup?, attachToParent: Boolean): VB

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = setViewBinding(inflater, container, false)
        return binding.root
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
            setContentView(binding.root)
            setCanceledOnTouchOutside(isDismissAble())
            setCancelable(isDismissAble())
        }
    }

    // Clean up binding reference when fragment is destroyed
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}