package com.kanzankazu.kanzanutil.kanzanextension.view

import android.graphics.Point
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.ScrollView
import androidx.core.widget.NestedScrollView
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.kanzankazu.kanzanutil.kanzanextension.sendCrashlytics
import com.kanzankazu.kanzanutil.kanzanextension.type.debugMessageError
import com.kanzankazu.kanzanutil.kanzanextension.type.dpTopx

/**
 * A constant representing the duration of an animation in milliseconds.
 *
 * This value is typically used to standardize the duration of animations
 * across the application, ensuring consistency in animation timing.
 *
 * Value: 1000 milliseconds (1 second).
 *
 * Example:
 * ```kotlin
 * val fadeInAnimation = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f).apply {
 *     duration = durationAnim.toLong()
 *     start()
 * }
 * ```
 */
private const val durationAnim = 1000

/**
 * Smoothly scrolls a `ScrollView` to its bottom-most position. This method ensures that the
 * scrolling happens on the UI thread using the `post` function.
 *
 * It calculates the target scroll position based on the `bottom` property of the `ScrollView`.
 * This is particularly useful for lists or dynamic content where you need to automatically scroll
 * to the bottom, such as in chat applications or logs.
 *
 * Example:
 * ```kotlin
 * val scrollView: ScrollView = findViewById(R.id.scroll_view)
 * scrollView.toBottom()
 * ```
 */
fun ScrollView.toBottom() {
    post {
        smoothScrollTo(0, bottom)
        //fullScroll(ScrollView.FOCUS_DOWN)
    }
}

/**
 * Smoothly scrolls the `ScrollView` to the top of its content.
 * This method posts a scrolling action to the message queue, ensuring that
 * it safely executes after the `ScrollView` is measured and laid out.
 *
 * Example:
 * ```kotlin
 * val scrollView: ScrollView = findViewById(R.id.scrollView)
 * scrollView.toTop() // Scrolls to the top of the ScrollView content
 * ```
 */
fun ScrollView.toTop() {
    post {
        smoothScrollTo(0, 0)
    }
}

/**
 * Smoothly scrolls a `NestedScrollView` to its bottom position over a specified duration.
 *
 * @param duration The time in milliseconds for the smooth scroll to complete.
 *                 Defaults to `durationAnim` if not specified.
 *
 * Example:
 * ```kotlin
 * val nestedScrollView: NestedScrollView = findViewById(R.id.nestedScrollView)
 * nestedScrollView.toBottom(500) // Smooth scrolls to the bottom in 500ms
 * ```
 */
fun NestedScrollView.toBottom(duration: Int = durationAnim) {
    post {
        smoothScrollTo(0, bottom, duration)
        //fullScroll(ScrollView.FOCUS_DOWN)
    }
}

/**
 * Smoothly scrolls the NestedScrollView to the top position over a specified duration using an animation.
 * This function leverages `post` to ensure the scroll operation is executed after the view is fully measured and laid out.
 *
 * @param duration The duration of the scroll animation in milliseconds. Defaults to `durationAnim`.
 *
 * Example:
 * ```kotlin
 * val scrollView: NestedScrollView = findViewById(R.id.nestedScrollView)
 * scrollView.toTop(300) // Smoothly scrolls to the top over 300 milliseconds
 * ```
 */
fun NestedScrollView.toTop(duration: Int = durationAnim) {
    post {
        smoothScrollBy(0, 0, duration)
    }
}

/**
 * Smoothly scrolls the `NestedScrollView` to a specified vertical position (`yPos`) over a given duration.
 *
 * @param yPos The target Y-coordinate (vertical position) to which the `NestedScrollView` should scroll.
 * @param duration The duration of the scroll animation in milliseconds. Defaults to `durationAnim`.
 *
 * Example:
 * ```kotlin
 * nestedScrollView.toPosY(yPos = 500, duration = 300) // Smoothly scrolls to vertical position 500 over 300ms
 * ```
 */
fun NestedScrollView.toPosY(yPos: Int, duration: Int = durationAnim) {
    post {
        smoothScrollTo(0, yPos, duration)
    }
}

/**
 * Sets up a scroll listener for a `NestedScrollView` to detect when the bottom of the scroll view is reached
 * and triggers a callback for loading more items. This method helps in implementing pagination by determining
 * if more data needs to be fetched when the user scrolls to the bottom.
 *
 * @param isLoadMore A Boolean flag indicating whether a load operation is already in progress to prevent duplicate calls. Defaults to `false`.
 * @param currentPage The current page number of the data being displayed. It will be incremented when the bottom is reached if loading is triggered
 * .
 * @param lastPage The last available page number. No further loading will be triggered if the current page equals the last page.
 * @param onBottomListener A lambda callback that is invoked when the bottom of the scroll view is reached and loading can be triggered.
 *                         The callback provides the updated page number (`mCurrentPage`) and a flag (`mIsLoadMore`) to indicate that loading is occurring
 * .
 *
 * Example:
 * ```kotlin
 * myNestedScrollView.loadMore(
 *     isLoadMore = false,
 *     currentPage = 1,
 *     lastPage = 5
 * ) { mCurrentPage, mIsLoadMore ->
 *     if (mIsLoadMore) {
 *         fetchMoreData(mCurrentPage)
 *     }
 * }
 * ```
 */
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

/**
 * Smoothly scrolls a `NestedScrollView` to bring a specific `View` into view,
 * adjusting for a customizable offset and animation duration.
 * This method calculates the correct scroll position by traversing up the view hierarchy to account for its top position.
 *
 * @param view The `View` to scroll to within the `NestedScrollView`.
 * @param defaultPXOffside The offset in pixels to apply when scrolling to the view. Defaults to 16dp.
 * @param scrollDuration The duration of the scroll animation in milliseconds. Defaults to 2000ms.
 *
 * Example:
 * ```kotlin
 * val scrollView: NestedScrollView = findViewById(R.id.scrollView)
 * val targetView: View = findViewById(R.id.targetView)
 *
 * scrollView.smoothScrollTo(targetView, defaultPXOffside = 20, scrollDuration = 1500)
 * ```
 */
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
 * Smoothly scrolls a `NestedScrollView` to bring the specified child view into view over a given duration.
 * This method calculates the precise offset of the child view within the scroll hierarchy and animates the scroll.
 *
 * @param view The target `View` within the `NestedScrollView` to scroll into view.
 * @param duration The duration of the scroll animation in milliseconds. Defaults to 500 ms.
 *
 * Example:
 * ```kotlin
 * val nestedScrollView: NestedScrollView = findViewById(R.id.scrollView)
 * val targetView: View = findViewById(R.id.targetView)
 *
 * nestedScrollView.scrollToView(targetView, 300) // Scrolls to the `targetView` over 300 ms.
 * ```
 */
fun NestedScrollView.scrollToView(view: View, duration: Int = 500) {
    // Get deepChild Offset
    val childOffset = Point()
    getDeepChildOffset(this, view.parent, view, childOffset)
    // Scroll to child.
    smoothScrollTo(0, childOffset.y, duration)
}

/**
 * Smoothly scrolls a `ScrollView` to ensure that the specified child `View` is visible within the viewport.
 * If the child is nested inside other child views, the method calculates the necessary offset to bring
 * the target view into view by accounting for all parent offsets.
 *
 * @param view The child `View` within the `ScrollView` that needs to be scrolled into view.
 *
 * Example:
 * ```kotlin
 * val scrollView: ScrollView = findViewById(R.id.scrollView)
 * val targetView: View = findViewById(R.id.targetView)
 * scrollView.scrollToView(targetView)
 * ```
 */
fun ScrollView.scrollToView(view: View) {
    // Get deepChild Offset
    val childOffset = Point()
    getDeepChildOffset(this, view.parent, view, childOffset)
    // Scroll to child.
    smoothScrollTo(0, childOffset.y)
}

/**
 * Calculates the cumulative offset of a child view relative to a specified main parent view group by traversing
 * through the parent hierarchy. This method accumulates the x and y positional offsets of the child relative to
 * its ancestors.
 *
 * @param mainParent The root `ViewGroup` against which the offset calculation should be done.
 * @param parent The immediate `ViewParent` of the child view. This should be a `ViewGroup` or null.
 * @param child The `View` for which the cumulative offset is to be calculated.
 * @param accumulatedOffset A `Point` object to store the accumulated x and y offsets of the child view.
 */
private fun getDeepChildOffset(mainParent: ViewGroup, parent: ViewParent, child: View, accumulatedOffset: Point) {
    try {
        val parentGroup: ViewGroup = parent as ViewGroup
        accumulatedOffset.x += child.left
        accumulatedOffset.y += child.top
        if (parentGroup == mainParent) return
        getDeepChildOffset(mainParent, parentGroup.parent, parentGroup, accumulatedOffset)
    } catch (e: Exception) {
        e.debugMessageError("getDeepChildOffset")
    }
}
