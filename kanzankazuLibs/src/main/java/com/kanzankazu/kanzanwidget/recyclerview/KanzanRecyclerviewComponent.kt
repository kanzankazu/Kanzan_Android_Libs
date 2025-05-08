@file:Suppress("UNCHECKED_CAST")

package com.kanzankazu.kanzanwidget.recyclerview

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.animation.AnimationUtils
import androidx.annotation.AnimRes
import androidx.recyclerview.widget.RecyclerView
import com.kanzankazu.R
import com.kanzankazu.kanzanutil.kanzanextension.type.debugMessageError
import com.kanzankazu.kanzanwidget.recyclerview.utils.extension.RecyclerViewLayoutType
import com.kanzankazu.kanzanwidget.recyclerview.utils.extension.getSnapPosition
import com.kanzankazu.kanzanwidget.recyclerview.utils.extension.setRecyclerView
import com.kanzankazu.kanzanwidget.recyclerview.utils.snaphelper.SimpleSnapHelper
import com.kanzankazu.kanzanwidget.recyclerview.widget.OverflowPagerIndicator

class KanzanRecyclerviewComponent @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : RecyclerView(context, attrs, defStyleAttr) {
    private var _animRes: Int = R.anim.layout_animation_fall_down
    private var isDragging: Boolean = false
    private var position: Int = 0
    private lateinit var carouselHandler: Handler
    private lateinit var carouselRun: Runnable
    private lateinit var snapHelper: SimpleSnapHelper

    init {
        attrs?.let { extractAttributes(it) }
    }

    fun setRecyclerLinearVertical(
        recyclerAdapter: Adapter<ViewHolder>,
        spacingItemDecoration: Int,
        isAll: Boolean = true,
        @AnimRes animRes: Int = _animRes,
    ) {
        _animRes = animRes
        setRecyclerView(
            recyclerAdapter,
            RecyclerViewLayoutType.RECYCLER_VIEW_LIN_VERTICAL,
            spacingItemDecoration = spacingItemDecoration,
            isNestedScrollingEnabledParam = false,
            isAll = isAll,
            animRes = animRes
        )
    }

    fun setRecyclerLinearHorizontal(
        recyclerAdapter: Adapter<ViewHolder>,
        spacingItemDecoration: Int,
        isAll: Boolean = true,
        @AnimRes animRes: Int = _animRes,
    ) {
        _animRes = animRes
        setRecyclerView(
            recyclerAdapter, RecyclerViewLayoutType.RECYCLER_VIEW_LIN_HORIZONTAL,
            spacingItemDecoration = spacingItemDecoration,
            isNestedScrollingEnabledParam = false,
            isAll = isAll,
            animRes = animRes
        )
    }

    fun setRecyclerGrid(
        recyclerAdapter: Adapter<ViewHolder>,
        spanCountGrid: Int,
        spacingItemDecoration: Int,
        @AnimRes animRes: Int = _animRes,
    ) {
        _animRes = animRes
        setRecyclerView(
            recyclerAdapter,
            RecyclerViewLayoutType.RECYCLER_VIEW_GRID_VERTICAL,
            spanCountGrid = spanCountGrid,
            spacingItemDecoration = spacingItemDecoration,
            isNestedScrollingEnabledParam = false,
            animRes = animRes
        )
    }

    fun setRecyclerBanner(
        recyclerAdapter: Adapter<ViewHolder>,
        listener: (Int) -> Unit = {},
        spacingItemDecoration: Int,
        opiHomeBanner: OverflowPagerIndicator? = null,
        delayMillis: Long = 2000,
        isAll: Boolean = true,
        returnHandlerRun: (handler: Handler, runnable: Runnable) -> Unit = { _, _ -> },
        @AnimRes animRes: Int = _animRes,
    ) {
        _animRes = animRes
        setRecyclerView(recyclerAdapter, RecyclerViewLayoutType.RECYCLER_VIEW_LIN_HORIZONTAL, spacingItemDecoration = spacingItemDecoration, isNestedScrollingEnabledParam = false, isAll = isAll)

        addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == SCROLL_STATE_IDLE) {
                    position = getSnapPosition(snapHelper)

                    if (isDragging) {
                        carouselHandler.post(carouselRun)
                        isDragging = false
                    }
                } else if (newState == SCROLL_STATE_DRAGGING) {
                    carouselHandler.removeCallbacks(carouselRun)
                    isDragging = true
                }
            }
        })
        //opiHomeBanner?.attachToRecyclerView(this)
        snapHelper = SimpleSnapHelper(opiHomeBanner)
        snapHelper.attachToRecyclerView(this)
        carouselHandler = Handler(Looper.getMainLooper())
        carouselRun = Runnable {
            try {
                smoothScrollToPosition(position)
                listener(position)

                opiHomeBanner?.onPageSelected(position)
                position = (if (position == recyclerAdapter.itemCount - 1) 0
                else position.plus(1))

                carouselHandler.postDelayed(carouselRun, delayMillis)
            } catch (e: Exception) {
                e.debugMessageError("KanzanRecyclerviewComponent - setRecyclerBanner")
            }
        }
        carouselHandler.postDelayed({ carouselRun.run() }, delayMillis)
        returnHandlerRun(carouselHandler, carouselRun)
    }

    fun animation(viewHolder: ViewHolder, @AnimRes idAnim: Int) {
        AnimationUtils.loadAnimation(viewHolder.itemView.context, idAnim)
    }

    private fun extractAttributes(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.KanzanRecyclerviewComponent)
        _animRes = typedArray.getResourceId(R.styleable.KanzanRecyclerviewComponent_KRVCAnim, _animRes)
        typedArray.recycle()
    }
}
