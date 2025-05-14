@file:Suppress("UNCHECKED_CAST")

package com.kanzankazu.kanzanbase.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.kanzankazu.kanzanbase.superall.BaseFragmentSuper

abstract class BaseFragmentBindingView<VB : ViewBinding?> : BaseFragmentSuper() {
    private var _binding: ViewBinding? = null
    abstract val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB
    val bindFragment: VB
        get() = _binding as VB

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        if (rootView == null) _binding = bindingInflater(inflater, container, false)
        return getPersistentView(_binding)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
