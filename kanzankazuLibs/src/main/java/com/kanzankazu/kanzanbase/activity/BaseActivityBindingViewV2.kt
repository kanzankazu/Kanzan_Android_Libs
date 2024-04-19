@file:Suppress("UNCHECKED_CAST")

package com.kanzankazu.kanzanbase.activity

import android.os.Bundle
import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding
import com.kanzankazu.kanzanbase.superall.BaseActivitySuper

/**
 * Created by kanzan on 12/03/24.
 */
abstract class BaseActivityBindingViewV2<VB : ViewBinding> : BaseActivitySuper() {

    lateinit var bindActivity: VB

    abstract fun setViewBinding(layoutInflater: LayoutInflater): VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindActivity = setViewBinding(layoutInflater)
        setContentView(bindActivity.root)

        setActivityResult()
        getBundleData()
        setContent()
        setListener()
        getData()
        setSubscribeToLiveData()
    }
}
