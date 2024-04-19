package com.kanzankazu.kanzanwidget.camera.base

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

abstract class BaseCameraActivityBindingView<VB : ViewBinding> : AppCompatActivity() {
    private var _binding: ViewBinding? = null

    /**Sample = VB::inflate*/
    abstract val bindingInflater: (LayoutInflater) -> VB

    @Suppress("UNCHECKED_CAST")
    protected val bind: VB
        get() = _binding as VB

    protected abstract fun setContent()

    protected abstract fun setListener()

    protected abstract fun setData()

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = bindingInflater(layoutInflater)
        setContentView(requireNotNull(_binding).root)

        setContent()
        setListener()
        setData()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}