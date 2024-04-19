package com.kanzankazu.kanzanwidget.recyclerview.genericadapter

import android.view.View
import com.kanzankazu.R
import com.kanzankazu.databinding.ItemAdapterErrorBinding

fun <T : Equatable> genericAdapterLazy(
    layoutRes: Int,
    loadingRes: Int = R.layout.item_adapter_loading,
    errorRes: Int = R.layout.item_adapter_error,
    onBind: View.(position: Int, data: T) -> Unit,
    onBindError: View.(String) -> Unit = {},
) = lazy {

    val adapter = GenericAdapter<T>(
        layoutRes = layoutRes,
        loadingRes = loadingRes,
        errorRes = errorRes
    )
    adapter.onBind = onBind

    if (errorRes == R.layout.item_adapter_error) adapter.onBindError = { message ->
        ItemAdapterErrorBinding.bind(this).apply { itemTvError.text = message }
    }
    else adapter.onBindError = onBindError

    return@lazy adapter
}
