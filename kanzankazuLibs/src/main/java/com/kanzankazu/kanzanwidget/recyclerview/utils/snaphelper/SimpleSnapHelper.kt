package com.kanzankazu.kanzanwidget.recyclerview.utils.snaphelper

import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.kanzankazu.kanzanwidget.recyclerview.widget.OverflowPagerIndicator

class SimpleSnapHelper(overflowPagerIndicator: OverflowPagerIndicator?) : PagerSnapHelper() {
    private val mOverflowPagerIndicator: OverflowPagerIndicator? = overflowPagerIndicator

    override fun findTargetSnapPosition(layoutManager: RecyclerView.LayoutManager, velocityX: Int, velocityY: Int): Int {
        val position = super.findTargetSnapPosition(layoutManager, velocityX, velocityY)
        mOverflowPagerIndicator?.onPageSelected(position)
        return position
    }

}