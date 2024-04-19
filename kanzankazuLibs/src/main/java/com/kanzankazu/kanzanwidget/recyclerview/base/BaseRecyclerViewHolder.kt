package com.kanzankazu.kanzanwidget.recyclerview.base

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BaseRecyclerViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun setContent(data: T)
    abstract fun setListener(data: T)
    open fun setData(data: T) {}
}