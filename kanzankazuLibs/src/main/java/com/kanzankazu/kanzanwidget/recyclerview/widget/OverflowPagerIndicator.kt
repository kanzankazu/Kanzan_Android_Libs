@file:Suppress("unused")

package com.kanzankazu.kanzanwidget.recyclerview.widget

import android.content.Context
import android.transition.ChangeBounds
import android.transition.Fade
import android.transition.Transition
import android.transition.TransitionManager
import android.transition.TransitionSet
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.kanzankazu.R
import java.util.Arrays

class OverflowPagerIndicator @JvmOverloads constructor(context: Context? = null, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {
    private var mIndicatorCount = 0
    private var mLastSelected = 0
    private val mIndicatorSize: Int
    private val mIndicatorMargin: Int
    private var mRecyclerView: RecyclerView? = null
    private val mDataObserver: OverflowDataObserver
    override fun onDetachedFromWindow() {
        if (mRecyclerView != null) {
            try {
                mRecyclerView!!.adapter!!.unregisterAdapterDataObserver(mDataObserver)
            } catch (ise: IllegalStateException) {
                // Do nothing
            }
        }
        super.onDetachedFromWindow()
    }

    /**
     * @param position Page to be selected
     */
    fun onPageSelected(position: Int) {
        if (mIndicatorCount > MAX_INDICATORS) {
            updateOverflowState(position)
        } else {
            updateSimpleState(position)
        }
    }

    /**
     * @param recyclerView Target recycler view
     */
    fun attachToRecyclerView(recyclerView: RecyclerView?) {
        mRecyclerView = recyclerView
        mRecyclerView!!.adapter!!.registerAdapterDataObserver(mDataObserver)
        initIndicators()
    }

    fun updateIndicatorsCount() {
        if (mIndicatorCount != mRecyclerView!!.adapter!!.itemCount) {
            initIndicators()
        }
    }

    private fun initIndicators() {
        mLastSelected = -1
        mIndicatorCount = mRecyclerView!!.adapter!!.itemCount
        createIndicators()
        onPageSelected(0)
    }

    private fun updateSimpleState(position: Int) {
        if (mLastSelected != -1) {
            animateViewScale(getChildAt(mLastSelected), STATE_NORMAL)
        }
        animateViewScale(getChildAt(position), STATE_SELECTED)
        mLastSelected = position
    }

    private fun updateOverflowState(position: Int) {
        if (mIndicatorCount == 0) {
            return
        }
        val transition: Transition = TransitionSet()
            .setOrdering(TransitionSet.ORDERING_TOGETHER)
            .addTransition(ChangeBounds())
            .addTransition(Fade())
        TransitionManager.beginDelayedTransition(this, transition)
        val positionStates = FloatArray(mIndicatorCount + 1)
        Arrays.fill(positionStates, STATE_GONE)
        val start = position - MAX_INDICATORS + 4
        var realStart = Math.max(0, start)
        if (realStart + MAX_INDICATORS > mIndicatorCount) {
            realStart = mIndicatorCount - MAX_INDICATORS
            positionStates[mIndicatorCount - 1] = STATE_NORMAL
            positionStates[mIndicatorCount - 2] = STATE_NORMAL
        } else {
            if (realStart + MAX_INDICATORS - 2 < mIndicatorCount) {
                positionStates[realStart + MAX_INDICATORS - 2] = STATE_SMALL
            }
            if (realStart + MAX_INDICATORS - 1 < mIndicatorCount) {
                positionStates[realStart + MAX_INDICATORS - 1] = STATE_SMALLEST
            }
        }
        for (i in realStart until realStart + MAX_INDICATORS - 2) {
            positionStates[i] = STATE_NORMAL
        }
        if (position > 5) {
            positionStates[realStart] = STATE_SMALLEST
            positionStates[realStart + 1] = STATE_SMALL
        } else if (position == 5) {
            positionStates[realStart] = STATE_SMALL
        }
        positionStates[position] = STATE_SELECTED
        updateIndicators(positionStates)
        mLastSelected = position
    }

    private fun updateIndicators(positionStates: FloatArray) {
        for (i in 0 until mIndicatorCount) {
            val v = getChildAt(i)
            val state = positionStates[i]
            if (state == STATE_GONE) {
                v.visibility = GONE
            } else {
                v.visibility = VISIBLE
                animateViewScale(v, state)
            }
        }
    }

    private fun createIndicators() {
        removeAllViews()
        if (mIndicatorCount <= 1) {
            return
        }
        for (i in 0 until mIndicatorCount) {
            addIndicator(mIndicatorCount > MAX_INDICATORS)
        }
    }

    private fun addIndicator(isOverflowState: Boolean) {
        val view = View(context)
        view.setBackgroundResource(R.drawable.dot)
        if (isOverflowState) {
            animateViewScale(view, STATE_SMALLEST)
        } else {
            animateViewScale(view, STATE_NORMAL)
        }
        addView(view, params(mIndicatorSize, mIndicatorSize))
    }

    private fun params(width: Int, height: Int): LayoutParams {
        val params = LayoutParams(width, height)
        params.leftMargin = mIndicatorMargin
        params.rightMargin = mIndicatorMargin
        return params
    }

    private fun animateViewScale(view: View?, scale: Float) {
        if (view == null) {
            return
        }
        if (scale == STATE_SELECTED) {
            view.layoutParams = params(dpToPx(39), mIndicatorSize)
            view.setBackgroundResource(R.drawable.line_indicator)
        } else {
            view.layoutParams = params(mIndicatorSize, mIndicatorSize)
            view.setBackgroundResource(R.drawable.dot)
        }
    }

    private fun dpToPx(dp: Int): Int {
        val displayMetrics = context.resources.displayMetrics
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
    }

    companion object {
        private const val MAX_INDICATORS = 9
        private const val INDICATOR_SIZE_DIP = 8
        private const val INDICATOR_MARGIN_DIP = 2

        // State also represents indicator scale factor
        private const val STATE_GONE = 0f
        private const val STATE_SMALLEST = 0.2f
        private const val STATE_SMALL = 0.4f
        private const val STATE_NORMAL = 0.6f
        private const val STATE_SELECTED = 1.0f
    }

    init {
        val dm = resources.displayMetrics
        mIndicatorSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, INDICATOR_SIZE_DIP.toFloat(), dm).toInt()
        mIndicatorMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, INDICATOR_MARGIN_DIP.toFloat(), dm).toInt()
        mDataObserver = OverflowDataObserver(this)
    }
}