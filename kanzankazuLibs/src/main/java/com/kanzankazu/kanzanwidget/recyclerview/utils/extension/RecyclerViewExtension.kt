package com.kanzankazu.kanzanwidget.recyclerview.utils.extension

import android.content.res.Resources
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import androidx.annotation.AnimRes
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.kanzankazu.R
import com.kanzankazu.kanzanwidget.recyclerview.utils.decorator.GridItemDecoration
import com.kanzankazu.kanzanwidget.recyclerview.utils.decorator.LinearItemDecoration
import com.kanzankazu.kanzanwidget.recyclerview.utils.decorator.SwipeRightLeftDecoration
import com.kanzankazu.kanzanwidget.recyclerview.utils.listener.SnapOnScrollListener

/**
 * @param layoutManagerIndex
 * 0=Linear_vertical
 * 1=Linear_horizontal
 * 2=Grid_vertical
 * 3=Grid_horizontal
 * 4=StaggeredGrid_vertical
 * 5=StaggeredGrid_horizontal
 */
fun RecyclerView.setRecyclerView(
    adapterParam: RecyclerView.Adapter<*>,
    layoutManagerIndex: RecyclerViewLayoutType = RecyclerViewLayoutType.RECYCLER_VIEW_LIN_VERTICAL,
    spanCountGrid: Int = 2,
    spacingItemDecoration: Int = 8,
    isNestedScrollingEnabledParam: Boolean = false,
    isAll: Boolean = true,
    onScrollListener: RecyclerView.OnScrollListener? = null,
    @AnimRes animRes: Int = R.anim.layout_animation_fall_down,
) {
    overScrollMode = View.OVER_SCROLL_NEVER
    adapter = adapterParam
    onScrollListener?.let { addOnScrollListener(it) }
    layoutManager = when (layoutManagerIndex) {
        RecyclerViewLayoutType.RECYCLER_VIEW_LIN_VERTICAL -> LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        RecyclerViewLayoutType.RECYCLER_VIEW_LIN_HORIZONTAL -> LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        RecyclerViewLayoutType.RECYCLER_VIEW_GRID_VERTICAL -> GridLayoutManager(context, spanCountGrid, GridLayoutManager.VERTICAL, false)
        RecyclerViewLayoutType.RECYCLER_VIEW_GRID_HORIZONTAL -> GridLayoutManager(context, spanCountGrid, GridLayoutManager.HORIZONTAL, false)
        RecyclerViewLayoutType.RECYCLER_VIEW_STAG_VERTICAL -> StaggeredGridLayoutManager(spanCountGrid, StaggeredGridLayoutManager.VERTICAL)
        else -> StaggeredGridLayoutManager(spanCountGrid, StaggeredGridLayoutManager.HORIZONTAL)
    }
    addItemDecoration(
        when (layoutManagerIndex) {
            RecyclerViewLayoutType.RECYCLER_VIEW_LIN_VERTICAL -> LinearItemDecoration(
                spacingItemDecoration.dpToPx(),
                LinearLayoutManager.VERTICAL,
                isAll
            )

            RecyclerViewLayoutType.RECYCLER_VIEW_LIN_HORIZONTAL -> LinearItemDecoration(
                spacingItemDecoration.dpToPx(),
                LinearLayoutManager.HORIZONTAL,
                isAll
            )

            else -> GridItemDecoration(spanCountGrid, spacingItemDecoration.dpToPx(), true)
        }
    )
    isNestedScrollingEnabled = isNestedScrollingEnabledParam

    if (animRes != -1) {
        val layoutAnimationController = AnimationUtils.loadLayoutAnimation(context, animRes)
        this.layoutAnimation = layoutAnimationController
    }
}

fun RecyclerView.setSnapHelper(snapHelper: SnapHelper) = snapHelper.attachToRecyclerView(this)

fun RecyclerView.setSnapHelperWithListener(snapHelper: SnapHelper, onSnapPositionChangeListener: SnapOnScrollListener.OnSnapPositionChangeListener, behavior: SnapOnScrollListener.Behavior = SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL) {
    snapHelper.attachToRecyclerView(this)
    val snapOnScrollListener = SnapOnScrollListener(snapHelper, behavior, onSnapPositionChangeListener)
    addOnScrollListener(snapOnScrollListener)
}

fun RecyclerView.getSnapPosition(snapHelper: SnapHelper): Int {
    val layoutManager = layoutManager ?: return RecyclerView.NO_POSITION
    val findSnapView = snapHelper.findSnapView(layoutManager) ?: return RecyclerView.NO_POSITION
    return layoutManager.getPosition(findSnapView)
}

fun RecyclerView.loadMore(isLoadMore: Boolean, currentPage: Int, lastPage: Int, onBottomListener: (mCurrentPage: Int, mIsLoadMore: Boolean) -> Unit) {
    this.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if (!isLoadMore && (lastPage != currentPage)) {
                val mLayoutManager = this@loadMore.layoutManager as LinearLayoutManager
                val visibleItemCount: Int = mLayoutManager.childCount
                val pastVisibleItems: Int = mLayoutManager.findFirstVisibleItemPosition()
                val totalItemCount: Int = mLayoutManager.itemCount

                if (pastVisibleItems + visibleItemCount >= totalItemCount) {
                    Log.d("Lihat KanzanKazu", "onScrolled ReviewHistoryFragment ${pastVisibleItems + visibleItemCount >= totalItemCount}")
                    Log.d("Lihat KanzanKazu", "onScrollStateChanged ${pastVisibleItems + visibleItemCount >= totalItemCount}")

                    var mCurrentPage = currentPage
                    val mIsLoadMore = true
                    mCurrentPage += 1

                    onBottomListener(mCurrentPage, mIsLoadMore)
                }
            }
        }
    })
}

fun RecyclerView.swipeRightLeft(drawObject: SwipeRightLeftDecoration.DrawObject, listener: SwipeRightLeftDecoration.Listener) {
    SwipeRightLeftDecoration(this, drawObject, listener)
}

interface LoadMoreListener {
    fun onBottomListener(currentPage: Int, isLoadMore: Boolean)
}

fun SnapHelper.getSnapPosition(recyclerView: RecyclerView): Int = recyclerView.getSnapPosition(this)

internal fun Int.dpToPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
