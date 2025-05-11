@file:Suppress("UNCHECKED_CAST")

package com.kanzankazu.kanzanbase.activity

import android.os.Bundle
import android.view.LayoutInflater
import androidx.viewbinding.ViewBinding
import com.kanzankazu.kanzanbase.superall.BaseActivitySuper
import com.kanzankazu.kanzannetwork.InternetConnection

/**
 * Created by Faisal Bahri on 2020-02-11.
 */
abstract class BaseActivityBindingView<VB : ViewBinding> : BaseActivitySuper() {

    private var isNetworkAvailable: InternetConnection = InternetConnection.CONNECTED
    private var _binding: ViewBinding? = null
    abstract val bindingInflater: (LayoutInflater) -> VB
    val bindActivity: VB
        get() = _binding as VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = bindingInflater(layoutInflater)
        setContentView(requireNotNull(_binding).root)

        setActivityResult()
        parseIntentData()
        setContent()
        setListener()
        getData()
        setSubscribeToLiveData()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
