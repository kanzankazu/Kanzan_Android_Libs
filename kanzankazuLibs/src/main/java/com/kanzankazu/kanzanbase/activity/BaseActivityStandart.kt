@file:Suppress("UNCHECKED_CAST")

package com.kanzankazu.kanzanbase.activity

import android.os.Bundle
import com.kanzankazu.kanzanbase.superall.BaseActivitySuper

/**
 * Created by Faisal Bahri on 2020-02-11.
 */
abstract class BaseActivityStandart : BaseActivitySuper() {

    private var isNetworkAvailable: Boolean = true

    protected abstract fun getBindView(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getBindView())

        setActivityResult()
        getBundleData()
        setContent()
        setListener()
        getData()
        setSubscribeToLiveData()
    }
}
