package com.kanzankazu.kanzanwidget.recyclerview.utils.decorator

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class LinearItemDecoration(private val spacing: Int, private var orientation: Int, private val isAll: Boolean) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        if (orientation == LinearLayoutManager.HORIZONTAL) {
            with(outRect) {
                if (parent.getChildAdapterPosition(view) == 0) {
                    left = spacing
                }
                top = if (isAll) spacing else 0
                bottom = if (isAll) spacing else 0
                right = spacing
            }
        } else {
            with(outRect) {
                if (parent.getChildAdapterPosition(view) == 0) {
                    top = spacing
                }
                left = if (isAll) spacing else 0
                right = if (isAll) spacing else 0
                bottom = spacing
            }
        }
    }
}