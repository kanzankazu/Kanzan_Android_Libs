package com.kanzankazu.kanzanbase.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import com.kanzankazu.kanzanbase.BaseView
import com.kanzankazu.kanzanbase.superall.BaseFragmentSuper

abstract class BaseFragmentStandart : BaseFragmentSuper(), BaseView {
    @LayoutRes
    protected abstract fun getBindView(): Int

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return getPersistentView(inflater, container, getBindView())
    }
}
