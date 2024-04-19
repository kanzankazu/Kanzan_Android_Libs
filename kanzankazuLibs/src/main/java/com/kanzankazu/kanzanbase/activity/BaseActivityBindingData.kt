/*
@file:Suppress("UNCHECKED_CAST")

package com.kanzankazu.kanzanbase

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.kanzankazu.kanzannetwork.NetworkStatus

*/
/**
 * Created by Faisal Bahri on 2020-02-11.
 *//*

abstract class BaseActivityBindingData<out VDB : ViewDataBinding> : BaseActivitySuper() {

    private var isNetworkAvailable: Boolean = true
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
        getBundleData()
        setContent()
        setListener()
        getData()
        setSubscribeToLiveData()
        vmLoadDataRe(networkLiveData) {
            isNetworkAvailable = when (it) {
                is NetworkStatus.Available -> true
                is NetworkStatus.Unavailable -> false
                else -> false
            }
            handleConnection(isNetworkAvailable)
        }
    }
}
*/
