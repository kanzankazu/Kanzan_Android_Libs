@file:Suppress("UNCHECKED_CAST")

package com.kanzankazu.kanzanbase.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.kanzankazu.kanzanbase.BaseView
import com.kanzankazu.kanzanbase.superall.BaseFragmentSuper

/**
 * Created by kanzan on 12/03/24.
 */
abstract class BaseFragmentBindingViewV2<VB : ViewBinding> : BaseFragmentSuper(), BaseView {

    lateinit var bindFragment: VB

    abstract fun setViewBinding(inflater: LayoutInflater, container: ViewGroup?, b: Boolean): VB

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (rootView == null) bindFragment = setViewBinding(inflater, container, false)
        return getPersistentView(bindFragment)
    }
}
