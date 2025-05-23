@file:Suppress("MemberVisibilityCanBePrivate")

package com.kanzankazu.kanzanbase.superall

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.kanzankazu.kanzanbase.BaseAdmob
import com.kanzankazu.kanzannetwork.NetworkLiveData
import com.kanzankazu.kanzannetwork.NetworkStatus
import com.kanzankazu.kanzanutil.kanzanextension.type.lazyNone
import com.kanzankazu.kanzanwidget.dialog.BaseAlertDialog
import com.kanzankazu.kanzanwidget.dialog.BaseProgressDialog

abstract class BaseFragmentSuper : Fragment() {
    protected val baseProgressDialog by lazyNone { BaseProgressDialog() }
    protected val baseAlertDialog by lazyNone { BaseAlertDialog() }
    protected val baseAdmob by lazyNone { BaseAdmob(requireActivity()) }
    protected val networkLiveData by lazyNone { NetworkLiveData(requireContext()) }

    protected var mLayout = 0
    protected var hasInitializedRootView = false
    protected var rootView: View? = null
    private var isRestart: Boolean = false

    protected open fun setActivityResult() {}
    protected open fun setSubscribeToLiveData() {}
    protected open fun parseArgumentData(arguments: Bundle) {}
    protected open fun handleConnection(networkStatus: NetworkStatus) {}
    protected abstract fun setContent()
    protected open fun setListener() {}
    protected open fun getData() {}

    protected open fun onRestart() {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!hasInitializedRootView) {
            hasInitializedRootView = true
            arguments?.let { argumentData -> parseArgumentData(argumentData) }
            setContent()
            setListener()
            getData()
            setActivityResult()
            setSubscribeToLiveData()
        }
    }

    override fun onResume() {
        super.onResume()
        if (isRestart) onRestart()
        else isRestart = true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("layout_id", mLayout)
        super.onSaveInstanceState(outState)
    }

    protected fun getPersistentView(layout: ViewBinding?): View? {
        if (rootView == null) rootView = layout?.root
        else (rootView?.parent as? ViewGroup)?.removeView(rootView)
        return rootView
    }

    protected fun getPersistentView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        layout: Int,
    ): View? {
        if (rootView == null) rootView = inflater?.inflate(layout, container, false)
        else (rootView?.parent as? ViewGroup)?.removeView(rootView)
        return rootView
    }
}
