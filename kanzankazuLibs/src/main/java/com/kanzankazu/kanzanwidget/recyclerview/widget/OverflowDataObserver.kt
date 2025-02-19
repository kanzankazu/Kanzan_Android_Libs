package com.kanzankazu.kanzanwidget.recyclerview.widget

import androidx.recyclerview.widget.RecyclerView
import com.kanzankazu.kanzanutil.kanzanextension.type.lazyNone

class OverflowDataObserver internal constructor(mOverflowPagerIndicator: OverflowPagerIndicator) : RecyclerView.AdapterDataObserver() {
    private val mOverflowPagerIndicator: OverflowPagerIndicator by lazyNone{ mOverflowPagerIndicator }

    override fun onChanged() {
        mOverflowPagerIndicator.updateIndicatorsCount()
    }

    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
        mOverflowPagerIndicator.updateIndicatorsCount()
    }

    override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
        mOverflowPagerIndicator.updateIndicatorsCount()
    }

    override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
        mOverflowPagerIndicator.updateIndicatorsCount()
    }

    override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
        mOverflowPagerIndicator.updateIndicatorsCount()
    }

    override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
        mOverflowPagerIndicator.updateIndicatorsCount()
    }
}