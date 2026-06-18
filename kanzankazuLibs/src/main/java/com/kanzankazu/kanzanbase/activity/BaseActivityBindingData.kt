@file:Suppress("UNCHECKED_CAST")

package com.kanzankazu.kanzanbase.activity

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.kanzankazu.kanzanbase.superall.BaseActivitySuper

/**
 * Created by Faisal Bahri on 2020-02-11.
 */
abstract class BaseActivityBindingData<out VDB : ViewDataBinding> : BaseActivitySuper() {

    private lateinit var _binding: VDB

    val bindActivity: VDB
        get() = _binding

    @LayoutRes
    protected abstract fun getBindView(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = DataBindingUtil.setContentView(this, getBindView())
        _binding.executePendingBindings()

        setActivityResult()
        parseIntentData()
        setContent()
        setListener()
        getData()
        setSubscribeToLiveData()
    }
}
