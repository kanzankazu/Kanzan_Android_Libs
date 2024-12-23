package com.kanzankazu.kanzanutil.kanzanextension.view

import android.graphics.Point
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.ScrollView
import androidx.core.widget.NestedScrollView
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.kanzankazu.kanzanutil.kanzanextension.sendCrashlytics
import com.kanzankazu.kanzanutil.kanzanextension.type.dpTopx

private const val durationAnim = 1000

fun ScrollView.toBottom() {
    post {
        smoothScrollTo(0, bottom)
        //fullScroll(ScrollView.FOCUS_DOWN)
    }
}

fun ScrollView.toTop() {
    post {
        smoothScrollTo(0, 0)
    }
}

fun NestedScrollView.toBottom(duration: Int = durationAnim) {
    post {
        smoothScrollTo(0, bottom, duration)
        //fullScroll(ScrollView.FOCUS_DOWN)
    }
}

fun NestedScrollView.toTop(duration: Int = durationAnim) {
    post {
        smoothScrollBy(0, 0, duration)
    }
}

fun NestedScrollView.toPosY(yPos: Int, duration: Int = durationAnim) {
    post {
        smoothScrollTo(0, yPos, duration)
    }
}

fun NestedScrollView.loadMore(isLoadMore: Boolean, currentPage: Int, lastPage: Int, onBottomListener: (mCurrentPage: Int, mIsLoadMore: Boolean) -> Unit) {
    this.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { nestedScrollView, _, scrollY, _, oldScrollY ->
        if (nestedScrollView.getChildAt(nestedScrollView.childCount - 1) != null) {
            if (scrollY >= nestedScrollView.getChildAt(nestedScrollView.childCount - 1).measuredHeight - nestedScrollView.measuredHeight && scrollY > oldScrollY) {
                if (!isLoadMore && (lastPage != currentPage)) {
                    var mCurrentPage = currentPage
                    val mIsLoadMore = true
                    mCurrentPage += 1

                    onBottomListener(mCurrentPage, mIsLoadMore)
                }
            }
        }
    })
}

fun NestedScrollView.smoothScrollTo(view: View, defaultPXOffside: Int = 16, scrollDuration: Int = 2000) {
    var distance = view.top
    var viewParent = view.parent
    // traverses 10 times
    for (i in 0..9) {
        if ((viewParent as View) === this) break
        distance += (viewParent as View).top
        viewParent = viewParent.getParent()
    }
    smoothScrollTo(0, distance - defaultPXOffside.dpTopx(), scrollDuration)
}

/**
 * Used to scroll to the given view.
 *
 * @param this@scrollToView Parent ScrollView
 * @param view View to which we need to scroll.
 */
fun NestedScrollView.scrollToView(view: View, duration: Int = 500) {
    // Get deepChild Offset
    val childOffset = Point()
    getDeepChildOffset(this, view.parent, view, childOffset)
    // Scroll to child.
    smoothScrollTo(0, childOffset.y, duration)
}

fun ScrollView.scrollToView(view: View) {
    // Get deepChild Offset
    val childOffset = Point()
    getDeepChildOffset(this, view.parent, view, childOffset)
    // Scroll to child.
    smoothScrollTo(0, childOffset.y)
}

/**
 * Used to get deep child offset.
 *
 *
 * 1. We need to scroll to child in scrollview, but the child may not the direct child to scrollview.
 * 2. So to get correct child position to scroll, we need to iterate through all of its parent views till the main parent.
 *
 * @param mainParent        Main Top parent.
 * @param parent            Parent.
 * @param child             Child.
 * @param accumulatedOffset Accumulated Offset.
 */
private fun getDeepChildOffset(mainParent: ViewGroup, parent: ViewParent, child: View, accumulatedOffset: Point) {
    try {
        val parentGroup: ViewGroup = parent as ViewGroup
        accumulatedOffset.x += child.left
        accumulatedOffset.y += child.top
        if (parentGroup == mainParent) return
        getDeepChildOffset(mainParent, parentGroup.parent, parentGroup, accumulatedOffset)
    } catch (e: Exception) {
        e.fillInStackTrace().sendCrashlytics()
        FirebaseCrashlytics.getInstance().recordException(e.fillInStackTrace())
    }
}
