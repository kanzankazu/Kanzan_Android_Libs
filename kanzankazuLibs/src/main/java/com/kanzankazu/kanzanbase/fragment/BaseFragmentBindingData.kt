/*
@file:Suppress("UNCHECKED_CAST")

package com.kanzankazu.kanzanbase

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.viewbinding.ViewBinding

abstract class BaseFragmentBindingData<VDB : ViewBinding> : BaseFragmentSuper() {
    @LayoutRes
    protected abstract fun getBindView(): Int

    private var _binding: ViewBinding? = null
    protected val bindFragment: VDB
        get() = _binding as VDB

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (rootView == null) _binding = DataBindingUtil.inflate(inflater, getBindView(), container, false)
        return _binding?.root
    }
}*/
