@file:Suppress("DEPRECATION")

package com.kanzankazu.kanzanwidget.multistateview

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import com.kanzankazu.R

/**
 * A custom view extending FrameLayout to manage multiple states (CONTENT, LOADING, ERROR, EMPTY).
 * Each state holds its own view, and the visible state can be switched dynamically.
 * Provides functionalities to set views and layouts for each state and handle view visibility transitions.
 *
 * Usage:
 * - Add MultiStateView in your layout XML or create programmatically.
 * - Assign views or layouts for different states using `setViewForState`.
 * - Switch between states using the `viewState` property.
 *
 * Example:
 * ```kotlin
 * val multiStateView = MultiStateView(context)
 * multiStateView.setViewForState(R.layout.loading_view, MultiStateView.ViewState.LOADING)
 * multiStateView.viewState = MultiStateView.ViewState.LOADING
 * ```
 *
 * @constructor Creates a MultiStateView initialized with a context, optional attribute set, and default style.
 * @param context The Context in which the MultiStateView is running.
 * @param attrs   The AttributeSet containing custom attributes to initialize the MultiStateView.
 * @param defStyle The default style to apply to the MultiStateView.
 */
class MultiStateView
@JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : FrameLayout(context, attrs, defStyle) {

    /**
     * Represents the various states that a view in a MultiStateView can display.
     *
     * The `ViewState` enum is used internally by the `MultiStateView` class to manage and display
     * different views corresponding to specific states like `CONTENT`, `LOADING`, `ERROR`, and `EMPTY`.
     *
     * States:
     * - `CONTENT`: Displays the main content view.
     * - `LOADING`: Displays the loading view to indicate an ongoing process.
     * - `ERROR`: Displays an error view when something goes wrong.
     * - `EMPTY`: Displays a placeholder view when no content is available.
     *
     * Example usage in `MultiStateView`:
     * ```kotlin
     * fun getView(state: ViewState): View? {
     *     return when (state) {
     *         ViewState.LOADING -> loadingView
     *         ViewState.CONTENT -> contentView
     *         ViewState.EMPTY -> emptyView
     *         ViewState.ERROR -> errorView
     *     }
     * }
     * ```
     */
    enum class ViewState {
        /**
         * Represents a state where the main content view is displayed.
         *
         * In the `MultiStateView` class, this state signifies that the contentView should be visible
         * while other views (loadingView, errorView, emptyView) are hidden. This is the default state
         * for showing the primary content of the view.
         *
         * Usage example:
         * ```kotlin
         * multiStateView.setViewForState(contentView, ViewState.CONTENT, true)
         * ```
         */
        CONTENT,
        /**
         * Represents the loading state within the `MultiStateView`, where the associated `loadingView` is shown
         * while all other views (`contentView`, `emptyView`, `errorView`) are hidden.
         * This state is typically used to indicate that a process is ongoing,
         * such as fetching data or performing background operations.
         *
         * The `LOADING` state can be managed through utility methods like `getView` and `setViewForState` in
         * the `MultiStateView` class. To switch to the `LOADING` state and display the corresponding view,
         * you can call `setViewForState` with the `LOADING` state and optionally enable `switchToState` to set it as active.
         *
         * Example usage:
         * ```kotlin
         * multiStateView.setViewForState(loadingView, ViewState.LOADING, switchToState = true)
         * ```
         *
         * When in the `LOADING` state:
         * - `loadingView` is displayed.
         * - `contentView`, `emptyView`, and `errorView` are hidden.
         * - If `animateLayoutChanges` is enabled, the transition to the `LOADING` state is animated.
         *
         * The `LOADING` state is one of the predefined states in the `ViewState` enum, which includes:
         * `CONTENT`, `EMPTY`, `ERROR`, and `LOADING`.
         */
        LOADING,
        /**
         * Represents the "Error" state within the `ViewState` enumeration, used to identify when the UI should display an error view.
         *
         * This state can be employed in conjunction with the `MultiStateView` component, which handles various states such as loading, content, empty
         * , and error.
         * When the state is set to `ERROR`, the associated error view (if configured) within the `MultiStateView` is displayed, while hiding all other
         *  states.
         *
         * Example usage in `MultiStateView`:
         * ```kotlin
         * val multiStateView = MultiStateView(context)
         *
         * // Setting a custom error view
         * val errorView = LayoutInflater.from(context).inflate(R.layout.error_view, null)
         * multiStateView.setViewForState(errorView, ViewState.ERROR, switchToState = true)
         *
         * // Programmatically switching to ERROR state
         * multiStateView.viewState = ViewState.ERROR
         * ```
         *
         * In the `MultiStateView`, the error view can also be retrieved or updated dynamically via functions such as:
         * - `getView(ViewState.ERROR)`
         * - `setViewForState(view: View, ViewState.ERROR, true)`
         */
        ERROR,
        /**
         * Represents the EMPTY state of a MultiStateView.
         * It is used to denote that the view is currently in an "empty" state, showing a predefined empty view.
         *
         * The EMPTY state can be set using the `setViewForState` method and can be retrieved or updated dynamically
         * according to the application's state.
         *
         * Example usage in a MultiStateView:
         * ```kotlin
         * multiStateView.setViewForState(emptyView, ViewState.EMPTY, true)
         * ```
         *
         * This state is often used to show a friendly message or icon indicating there is no content to display.
         */
        EMPTY
    }

    /**
     * Represents the primary content view within the `MultiStateView` component.
     * This variable holds the view that is displayed when the current `viewState` is set to `CONTENT`.
     * The `contentView` can be dynamically updated or retrieved based on state-specific operations.
     *
     * Utilized in scenarios such as:
     * - Dynamically switching to the `CONTENT` view state.
     * - Setting a new view as the `contentView` through `setViewForState` or other overridden addView methods.
     *
     * The `contentView` is initialized via XML or programmatically validated through the `isValidContentView` method.
     *
     * Example Uses:
     * 1. Obtaining the `contentView`:
     *    ```kotlin
     *    val view = multiStateView.getView(ViewState.CONTENT)
     *    ```
     *
     * 2. Setting the `contentView` programmatically:
     *    ```kotlin
     *    multiStateView.setViewForState(newContentView, ViewState.CONTENT)
     *    ```
     *
     * 3. Dynamically validating and adding a `contentView`:
     *    ```kotlin
     *    override fun addView(child: View) {
     *        if (isValidContentView(child)) contentView = child
     *        super.addView(child)
     *    }
     *    ```
     *
     * Note:
     * If the `contentView` is not set before attaching the `MultiStateView` to the window, it will throw an `IllegalArgumentException`.
     */
    private var contentView: View? = null

    /**
     * The `loadingView` property represents the View associated with the `LOADING` state within the `MultiStateView` class.
     * This view is displayed when the `MultiStateView` switches to the `LOADING` state. It can be explicitly set using the
     * `setViewForState` method. If a view is already assigned to this state and a new view is provided using `setViewForState`,
     * the existing view will be replaced. The visibility of the `loadingView` is managed internally based on the current
     * `ViewState` of the `MultiStateView`.
     *
     * Use Cases:
     * - Retrieve or set this property when programmatically managing the `LOADING` view state for `MultiStateView`.
     * - Customize the appearance of the loading view as needed for your application.
     *
     * Example:
     * ```kotlin
     * val loading = LayoutInflater.from(context).inflate(R.layout.view_loading, null)
     * multiStateView.setViewForState(loading, ViewState.LOADING, true)
     * ```
     *
     * Notes:
     * - Do not modify this property directly; instead, use the appropriate methods (`setViewForState`) for consistency.
     * - As this property is `nullable`, ensure it is assigned or checked for null to avoid accessing references that don't exist.
     */
    private var loadingView: View? = null

    /**
     * A nullable View that represents the error state in the `MultiStateView` component.
     * This view is displayed when the `ViewState` is set to `ERROR`.
     *
     * It can be dynamically set using `setViewForState` with the `ViewState.ERROR` parameter.
     * When the state changes to `ERROR`, this view will become visible, while all other state-specific views
     * (e.g., `loadingView`, `contentView`, `emptyView`) will be hidden.
     *
     * Usage Examples:
     * - Assign a custom error view using `setViewForState(view, ViewState.ERROR)`.
     * - Access the error view via `getView(ViewState.ERROR)` to retrieve it.
     *
     * Behavior:
     * - When a new error view is set, it replaces the existing `errorView` in the UI hierarchy.
     * - If `animateLayoutChanges` is enabled, transitions to and from the error view are animated.
     *
     * Example:
     * ```kotlin
     * // Set a custom error view
     * val errorView = View(context)
     * multiStateView.setViewForState(errorView, ViewState.ERROR)
     *
     * // Switch to the error state
     * multiStateView.viewState = ViewState.ERROR
     *
     * // Access the error view
     * val currentErrorView = multiStateView.getView(ViewState.ERROR)
     * ```
     */
    private var errorView: View? = null

    /**
     * Represents the view displayed when the current state of the MultiStateView is set to EMPTY.
     * The emptyView is used to provide feedback to the user when no content is available.
     *
     * Access or manipulation of the `emptyView` can be performed through internal methods of the MultiStateView component.
     * This view can be dynamically assigned or replaced via `setViewForState` method using the EMPTY state.
     *
     * Usage:
     * - When the view state is set to EMPTY, the `emptyView` becomes visible
     *   while other state-specific views (`contentView`, `loadingView`, `errorView`) are hidden.
     * - Ensure that the provided `emptyView` does not conflict with other state views to avoid inconsistencies.
     *
     * Example:
     * ```kotlin
     * multiStateView.setViewForState(emptyView, ViewState.EMPTY, true)
     * ```
     */
    private var emptyView: View? = null

    /**
     * A nullable property representing the state listener.
     * This listener can be assigned to monitor and handle state changes when needed.
     *
     * @see StateListener
     */
    var listener: StateListener? = null

    /**
     * Determines whether layout changes in the MultiStateView should be animated during state transitions.
     * If set to `true`, layout changes (e.g., visibility changes between views) are accompanied by animations.
     * If set to `false`, layout changes occur immediately without animations.
     *
     * This property is utilized in methods such as `setView` to decide whether to apply animations
     * when transitioning between different `ViewState` objects.
     *
     * Example:
     *
     * ```kotlin
     * multiStateView.animateLayoutChanges = true // Enables animations for state transitions
     * multiStateView.animateLayoutChanges = false // Disables animations for state transitions
     * ```
     */
    var animateLayoutChanges: Boolean = false

    /**
     * Tracks and manages the current state of the view within the MultiStateView component.
     * Updates the view display when a new state is assigned, replacing the current view with the corresponding view for the
     * new state (e.g., CONTENT, LOADING, ERROR, or EMPTY).
     * When a state change occurs, it invokes the `setView` method to update the view visibility and notifies the listener
     * of the state change through the `onStateChanged` callback.
     *
     * @param value The new `ViewState` to set. Must be one of the predefined states: CONTENT, LOADING, ERROR, or EMPTY.
     *
     * The view state change triggers the following actions:
     * - If `value` differs from the current `viewState`, it updates the displayed view to the one corresponding to the new state.
     * - Invokes the private `setView` method to manage the transition between states.
     * - Optionally animates layout transitions if `animateLayoutChanges` is enabled.
     * - Notifies the `StateListener` (if set) of the state change via `onStateChanged`.
     *
     * Example:
     * ```kotlin
     * viewState = ViewState.LOADING // Switches the MultiStateView to display the loading view.
     * viewState = ViewState.ERROR   // Switches to the error view and notifies the listener.
     * ```
     */
    var viewState: ViewState = ViewState.CONTENT
        set(value) {
            val previousField = field

            if (value != previousField) {
                field = value
                setView(previousField)
                listener?.onStateChanged(value)
            }
        }

    init {
        val inflater = LayoutInflater.from(getContext())
        val typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.MultiStateView)

        val loadingViewResId = typedArray.getResourceId(R.styleable.MultiStateView_msv_loadingView, -1)
        if (loadingViewResId > -1) {
            val inflatedLoadingView = inflater.inflate(loadingViewResId, this, false)
            loadingView = inflatedLoadingView
            addView(inflatedLoadingView, inflatedLoadingView.layoutParams)
        }

        val emptyViewResId = typedArray.getResourceId(R.styleable.MultiStateView_msv_emptyView, -1)
        if (emptyViewResId > -1) {
            val inflatedEmptyView = inflater.inflate(emptyViewResId, this, false)
            emptyView = inflatedEmptyView
            addView(inflatedEmptyView, inflatedEmptyView.layoutParams)
        }

        val errorViewResId = typedArray.getResourceId(R.styleable.MultiStateView_msv_errorView, -1)
        if (errorViewResId > -1) {
            val inflatedErrorView = inflater.inflate(errorViewResId, this, false)
            errorView = inflatedErrorView
            addView(inflatedErrorView, inflatedErrorView.layoutParams)
        }

        viewState = when (typedArray.getInt(R.styleable.MultiStateView_msv_viewState, VIEW_STATE_CONTENT)) {
            VIEW_STATE_ERROR -> ViewState.ERROR
            VIEW_STATE_EMPTY -> ViewState.EMPTY
            VIEW_STATE_LOADING -> ViewState.LOADING
            else -> ViewState.CONTENT
        }
        animateLayoutChanges = typedArray.getBoolean(R.styleable.MultiStateView_msv_animateViewChanges, false)
        typedArray.recycle()
    }

    /**
     * Retrieves the view associated with the given state from the MultiStateView.
     *
     * @param state The state for which the corresponding view is to be retrieved.
     *              It can be one of the following:
     *              - `ViewState.LOADING`: The loading state view.
     *              - `ViewState.CONTENT`: The content state view.
     *              - `ViewState.EMPTY`: The empty state view.
     *              - `ViewState.ERROR`: The error state view.
     * @return The view corresponding to the given state, or `null` if no view is associated with the state.
     *
     * Example:
     * ```kotlin
     * val loadingView = multiStateView.getView(ViewState.LOADING)
     * if (loadingView != null) {
     *     // Perform operations on the loading view
     * }
     * ```
     */
    fun getView(state: ViewState): View? {
        return when (state) {
            ViewState.LOADING -> loadingView
            ViewState.CONTENT -> contentView
            ViewState.EMPTY -> emptyView
            ViewState.ERROR -> errorView
        }
    }

    /**
     * Sets the view associated with a specific `ViewState` and optionally switches the current view state.
     * If a view is already assigned to the specified state, it will be replaced by the provided view.
     *
     * @param view The new `View` to associate with the specified `ViewState`.
     * @param state The `ViewState` to which the view should be assigned. Possible values are `ViewState.LOADING`, `ViewState.EMPTY`, `ViewState.ERROR
     * `, and `ViewState.CONTENT`.
     * @param switchToState A boolean indicating if the current view state should switch to the specified `state` after setting the view. Defaults to
     *  `false`.
     *
     * Example:
     * ```kotlin
     * val loadingView = ProgressBar(context)
     * multiStateView.setViewForState(loadingView, ViewState.LOADING, true) // Sets loadingView and switches to LOADING state
     * ```
     */
    fun setViewForState(view: View, state: ViewState, switchToState: Boolean = false) {
        when (state) {
            ViewState.LOADING -> {
                if (loadingView != null) removeView(loadingView)
                loadingView = view
                addView(view)
            }

            ViewState.EMPTY -> {
                if (emptyView != null) removeView(emptyView)
                emptyView = view
                addView(view)
            }

            ViewState.ERROR -> {
                if (errorView != null) removeView(errorView)
                errorView = view
                addView(view)
            }

            ViewState.CONTENT -> {
                if (contentView != null) removeView(contentView)
                contentView = view
                addView(view)
            }
        }

        if (switchToState) viewState = state
    }

    /**
     * Inflates and sets a view for the given state of the MultiStateView using a provided layout resource ID.
     * Additionally, allows the option to switch to the specified state after setting the view for it.
     *
     * @param layoutRes The layout resource ID to be inflated and set as the view for the given state.
     * @param state The ViewState for which the view is being set (e.g., CONTENT, LOADING, ERROR, EMPTY).
     * @param switchToState Boolean flag indicating whether to immediately switch to the specified state after setting the view. Defaults to false.
     *
     * Example:
     * ```kotlin
     * multiStateView.setViewForState(R.layout.view_loading, ViewState.LOADING, true)
     * // Inflates and sets the "view_loading" layout as the LOADING state view, then switches to this state.
     * ```
     */
    fun setViewForState(@LayoutRes layoutRes: Int, state: ViewState, switchToState: Boolean = false) {
        val view = LayoutInflater.from(context).inflate(layoutRes, this, false)
        setViewForState(view, state, switchToState)
    }

    /**
     * Called when the view is attached to a window. This initializes the visibility of views based on the current `viewState`.
     * If the `contentView` is not defined, an `IllegalArgumentException` will be thrown.
     *
     * Depending on the `viewState`, it switches the visible view to match the state or hides the `contentView`.
     *
     * @throws IllegalArgumentException if `contentView` is null.
     *
     * Example:
     * ```kotlin
     * // Assuming viewState is ViewState.CONTENT and contentView is set:
     * multiStateView.onAttachedToWindow()
     * // Will ensure the Content View is visible and other views are hidden.
     *
     * // If contentView is null:
     * multiStateView.onAttachedToWindow()
     * // Throws IllegalArgumentException("Content view is not defined").
     * ```
     */
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (contentView == null) throw IllegalArgumentException("Content view is not defined")

        when (viewState) {
            ViewState.CONTENT -> setView(ViewState.CONTENT)
            else -> contentView?.visibility = View.GONE
        }
    }

    /**
     * Saves the current state of the MultiStateView, including its super class state and the current view state.
     *
     * @return A Parcelable object representing the saved state, or null if the super state is null.
     *
     * Example:
     * ```kotlin
     * val savedState = multiStateView.onSaveInstanceState()
     * ```
     */
    override fun onSaveInstanceState(): Parcelable? {
        return when (val superState = super.onSaveInstanceState()) {
            null -> superState
            else -> SavedState(superState, viewState)
        }
    }

    /**
     * Restores the instance state of the MultiStateView, updating its view state
     * if the provided Parcelable is of type SavedState. If not, the default
     * restoration behavior is applied.
     *
     * @param state The Parcelable object containing the saved state to restore.
     *              If the state is an instance of SavedState, the view state is
     *              updated using the saved state; otherwise, the superclass
     *              behavior is called.
     */
    override fun onRestoreInstanceState(state: Parcelable) {
        if (state is SavedState) {
            super.onRestoreInstanceState(state.superState)
            viewState = state.state
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    /**
     * Adds a child view to the `MultiStateView`. If the view is identified as a valid `contentView`,
     * it will be assigned accordingly. This method has been overridden to allocate the content view
     * through XML. Direct usage of this method to add views to `MultiStateView` is discouraged; instead,
     * use `setViewForState` methods to configure views for specific `ViewState`s as required.
     *
     * @param child The `View` instance to be added to the `MultiStateView`.
     *//* All of the addView methods have been overridden so that it can obtain the content view via XML
     It is NOT recommended to add views into MultiStateView via the addView methods, but rather use
     any of the setViewForState methods to set views for their given ViewState accordingly */
    override fun addView(child: View) {
        if (isValidContentView(child)) contentView = child
        super.addView(child)
    }

    /**
     * Adds a child view to the MultiStateView at the specified index. If the provided view
     * is determined to be a valid content view, it is assigned as the contentView of this MultiStateView.
     * Note: For setting views for specific states, it is recommended to use the setViewForState method
     * instead of directly calling addView.
     *
     * @param child The view to be added to this MultiStateView.
     * @param index The position at which the view should be added.
     */
    override fun addView(child: View, index: Int) {
        if (isValidContentView(child)) contentView = child
        super.addView(child, index)
    }

    /**
     * Adds a child view to the MultiStateView at the specified index with the provided layout parameters.
     * Ensures that the `contentView` is correctly assigned if the added view is a valid content view.
     *
     * @param child The child `View` to add.
     * @param index The position at which to add the child view, or -1 to add to the end.
     * @param params The layout parameters to set on the child view.
     */
    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        if (isValidContentView(child)) contentView = child
        super.addView(child, index, params)
    }

    /**
     * Adds a child view to the MultiStateView with the specified layout parameters.
     * If the child view is determined to be a valid content view, it sets it as the content view.
     * This method is overridden to accommodate the MultiStateView's internal view handling.
     * It is recommended to use setViewForState methods for managing views instead of this one.
     *
     * @param child The child view to be added.
     * @param params The layout parameters for the child view.
     */
    override fun addView(child: View, params: ViewGroup.LayoutParams) {
        if (isValidContentView(child)) contentView = child
        super.addView(child, params)
    }

    /**
     * Adds a child view to the MultiStateView layout with the specified width and height.
     * If the provided child view is a valid content view, it is set as the `contentView`.
     * It is recommended to use the `setViewForState` methods instead of directly adding views using this method.
     *
     * @param child The child view to be added.
     * @param width The width for the child view.
     * @param height The height for the child view.
     */
    override fun addView(child: View, width: Int, height: Int) {
        if (isValidContentView(child)) contentView = child
        super.addView(child, width, height)
    }

    /**
     * Adds a child view into the layout at the specified index with the provided layout parameters.
     * If the added view is considered to be a valid content view, it updates the internal contentView reference.
     *
     * @param child The View to be added to the layout.
     * @param index The index at which the child view should be inserted in the layout.
     * @param params The layout parameters to assign to the child view.
     * @return A boolean indicating whether the view was successfully added to the layout.
     *
     * Example:
     * ```kotlin
     * val newView = TextView(context).apply { text = "Hello World" }
     * val layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
     * val wasAdded = multiStateView.addViewInLayout(newView, 0, layoutParams)
     * ```
     */
    override fun addViewInLayout(child: View, index: Int, params: ViewGroup.LayoutParams): Boolean {
        if (isValidContentView(child)) contentView = child
        return super.addViewInLayout(child, index, params)
    }

    /**
     * Adds a given child view to this layout at the specified index, with the provided layout parameters.
     * Optionally prevents a layout request from being made after the view is added. It checks if the view is a valid content view
     * and, if so, updates the content view reference.
     *
     * @param child The view to be added into the layout.
     * @param index The position at which to add the view. Use -1 to add it at the end.
     * @param params The layout parameters to apply to the child view.
     * @param preventRequestLayout If true, the layout system is not immediately notified of the view addition.
     * @return True if the view was added successfully to the layout, false otherwise.
     *
     * Example:
     * ```kotlin
     * val childView = View(context)
     * val layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
     * val result = multiStateView.addViewInLayout(childView, 0, layoutParams, true)
     * ```
     */
    override fun addViewInLayout(child: View, index: Int, params: ViewGroup.LayoutParams, preventRequestLayout: Boolean): Boolean {
        if (isValidContentView(child)) contentView = child
        return super.addViewInLayout(child, index, params, preventRequestLayout)
    }

    /**
     * Validates if a given view can be set as the content view for the MultiStateView.
     * Ensures that the new content view does not conflict with existing special views (loadingView,
     * errorView, or emptyView) and that it is not the current contentView unless contentView is null.
     *
     * @param view The view to validate.
     * @return `true` if the provided view is valid to be used as the content view,
     *         `false` otherwise.
     *
     * Usage:
     * This method is used internally to verify a view before setting it as the contentView
     * in overridden addView methods or during view state updates.
     */
    private fun isValidContentView(view: View): Boolean {
        return if (contentView != null && contentView !== view) {
            false
        } else view != loadingView && view != errorView && view != emptyView
    }

    /**
     * Updates the view within the MultiStateView based on the provided ViewState.
     * It ensures that only the view corresponding to the current state is visible,
     * while all other views are hidden. If `animateLayoutChanges` is enabled,
     * the state transition is animated.
     *
     * @param previousState The previous state of the view (of type ViewState), used to determine
     *                      if an animation or specific behavior should occur during the state transition.
     *
     * Example:
     * ```kotlin
     * // Suppose current viewState is ViewState.LOADING
     * multiStateView.setView(ViewState.CONTENT)
     * // Transitions to the CONTENT view, hiding LOADING and all other views.
     * ```
     */
    private fun setView(previousState: ViewState) {
        when (viewState) {
            ViewState.LOADING -> {
                requireNotNull(loadingView).apply {
                    contentView?.visibility = View.GONE
                    errorView?.visibility = View.GONE
                    emptyView?.visibility = View.GONE

                    if (animateLayoutChanges) {
                        animateLayoutChange(getView(previousState))
                    } else {
                        visibility = View.VISIBLE
                    }
                }
            }

            ViewState.EMPTY -> {
                requireNotNull(emptyView).apply {
                    contentView?.visibility = View.GONE
                    errorView?.visibility = View.GONE
                    loadingView?.visibility = View.GONE

                    if (animateLayoutChanges) {
                        animateLayoutChange(getView(previousState))
                    } else {
                        visibility = View.VISIBLE
                    }
                }
            }

            ViewState.ERROR -> {
                requireNotNull(errorView).apply {
                    contentView?.visibility = View.GONE
                    loadingView?.visibility = View.GONE
                    emptyView?.visibility = View.GONE

                    if (animateLayoutChanges) {
                        animateLayoutChange(getView(previousState))
                    } else {
                        visibility = View.VISIBLE
                    }
                }
            }

            ViewState.CONTENT -> {
                requireNotNull(contentView).apply {
                    loadingView?.visibility = View.GONE
                    errorView?.visibility = View.GONE
                    emptyView?.visibility = View.GONE

                    if (animateLayoutChanges) {
                        animateLayoutChange(getView(previousState))
                    } else {
                        visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    /**
     * Animates the transition between a previously visible view and the current view based on the
     * provided view state. If no previous view is provided, the current view becomes immediately visible.
     *
     * This method utilizes fade-out and fade-in animations to smoothly transition between views, with
     * a total duration of 250ms for each animation.
     *
     * @param previousView The view to be faded out during the transition. If null, no fade-out animation
     * is performed, and the current view becomes visible immediately.
     */
    private fun animateLayoutChange(previousView: View?) {
        if (previousView == null) {
            requireNotNull(getView(viewState)).visibility = View.VISIBLE
            return
        }

        ObjectAnimator.ofFloat(previousView, "alpha", 1.0f, 0.0f).apply {
            duration = 250L
            addListener(object : AnimatorListenerAdapter() {
                /**
                 * Handles the start of an animation event by setting the visibility of the provided view to VISIBLE.
                 *
                 * @param animation The Animator instance associated with the starting animation.
                 */
                override fun onAnimationStart(animation: Animator) {
                    previousView.visibility = View.VISIBLE
                }

                /**
                 * Callback method invoked when an animation ends. This method handles the visibility of views
                 * and initiates a fade-in animation on the current view.
                 *
                 * @param animation The Animator instance that has just ended.
                 *
                 * Example:
                 * ```kotlin
                 * override fun onAnimationEnd(animation: Animator) {
                 *     previousView.visibility = View.GONE
                 *     val currentView = requireNotNull(getView(viewState))
                 *     currentView.visibility = View.VISIBLE
                 *     ObjectAnimator.ofFloat(currentView, "alpha", 0.0f, 1.0f).setDuration(250L).start()
                 * }
                 * ```
                 */
                override fun onAnimationEnd(animation: Animator) {
                    previousView.visibility = View.GONE
                    val currentView = requireNotNull(getView(viewState))
                    currentView.visibility = View.VISIBLE
                    ObjectAnimator.ofFloat(currentView, "alpha", 0.0f, 1.0f).setDuration(250L).start()
                }
            })
        }.start()
    }

    /**
     * An interface that provides a callback method to respond to changes in the state of a view or process.
     * Implement this interface to handle state changes based on the provided `ViewState`.
     *
     * @param viewState An instance of `ViewState` representing the current state that has been updated.
     *
     * Example:
     * ```kotlin
     * class MyActivity : StateListener {
     *     override fun onStateChanged(viewState: ViewState) {
     *         when (viewState) {
     *             ViewState.Loading -> showLoading()
     *             ViewState.Success -> showContent()
     *             ViewState.Error -> showError()
     *         }
     *     }
     * }
     * ```
     */
    interface StateListener {
        /**
         * Called when the state of the given view changes. This method is used to handle updates
         * or perform actions based on the new view state.
         *
         * @param viewState The new state of the view, represented as an instance of `ViewState`.
         *                  This parameter contains information about the current state to act upon.
         */
        fun onStateChanged(viewState: ViewState)
    }

    /**
     * SavedState class is a custom implementation of `BaseSavedState` that is used to
     * preserve and restore the state of a `View` in Android during configuration changes
     * such as screen rotations.
     *
     * This class encapsulates a `ViewState` object that holds specific data to maintain the
     * application's state across lifecycle events.
     */
    private class SavedState : BaseSavedState {
        /**
         * Represents the current state of the view in a module or feature. This typically holds data related to UI state changes
         * and is used to manage rendering or behavior logic.
         *
         * The `state` variable is expected to be an instance of the `ViewState` class or interface, encapsulating the required properties
         * to track and represent the state of the view accurately.
         *
         * Example:
         * ```kotlin
         * internal val state: ViewState = LoadingState
         * internal val state: ViewState = ContentState(data)
         * ```
         */
        internal val state: ViewState

        /**
         * Constructs a new instance of the class, initializing it with a Parcelable super state and a custom ViewState.
         * This allows the preservation of both the parent state and custom view state during operations like saving and restoring view state.
         *
         * @param superState The Parcelable state representing the parent view's saved state.
         * @param state The custom ViewState to retain additional data for this view.
         */
        constructor(superState: Parcelable, state: ViewState) : super(superState) {
            this.state = state
        }

        /**
         * Constructs a new `SavedState` instance by reading its `state` from the provided `Parcel`.
         *
         * @param parcel The `Parcel` containing the serialized state of this object.
         */
        constructor(parcel: Parcel) : super(parcel) {
            state = parcel.readSerializable() as ViewState
        }

        /**
         * Writes the current state of the object to the given Parcel. This includes the ViewState
         * of this object, which is written as a serializable object.
         *
         * @param out The Parcel in which the object should be written.
         * @param flags Additional flags about how the object should be written.
         *
         * Example:
         * ```kotlin
         * val savedState = SavedState(superState, ViewState.LOADING)
         * val parcel = Parcel.obtain()
         * savedState.writeToParcel(parcel, 0)
         * ```
         */
        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeSerializable(state)
        }

        /**
         * Provides functionalities to create and manage instances of the SavedState class
         * from Parcelable data or to create an array of SavedState objects.
         */
        companion object {
            /**
             * A `Parcelable.Creator` implementation to handle the creation and array instantiation of the `SavedState` class
             * for Android parcelable functionality. This supports reading and writing `SavedState` objects to and from a `Parcel`.
             *
             * - `createFromParcel`: Reconstructs a `SavedState` instance from the provided `Parcel`.
             * - `newArray`: Creates an array of `SavedState` objects with the specified size.
             *
             * Example usage in a `Parcelable` class:
             * ```kotlin
             * companion object {
             *     @JvmField
             *     val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {
             *         override fun createFromParcel(`in`: Parcel): SavedState {
             *             return SavedState(`in`)
             *         }
             *         override fun newArray(size: Int): Array<SavedState?> {
             *             return arrayOfNulls(size)
             *         }
             *     }
             * }
             * ```
             */
            @JvmField
            val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {
                /**
                 * Creates a SavedState object from the given Parcel.
                 *
                 * @param in The Parcel containing the serialized data used to create the SavedState instance.
                 * @return A new instance of SavedState, initialized with the data from the Parcel.
                 */
                override fun createFromParcel(`in`: Parcel): SavedState {
                    return SavedState(`in`)
                }

                /**
                 * Creates a new array of the specified size, with each element initialized to null.
                 *
                 * @param size The size of the array to be created.
                 * @return An array of the specified size, where each element is initially null.
                 */
                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }
}

/**
 * Represents the numerical value associated with the `CONTENT` state in a MultiStateView.
 * This value is used internally to manage and switch between different view states.
 *
 * Example usage:
 * ```kotlin
 * if (viewState == VIEW_STATE_CONTENT) {
 *     showContentView()
 * }
 * ```
 */
private const val VIEW_STATE_CONTENT = 0
/**
 * Represents the integer value associated with the `ERROR` state in the `ViewState` enumeration.
 * This constant is used internally to indicate that the error view should be displayed
 * within the `MultiStateView` component.
 *
 * Example:
 * When the `viewState` is set to `ViewState.ERROR`, the `errorView` becomes visible, and other state views
 * like `contentView`, `loadingView`, and `emptyView` are hidden. Additionally, layout transitions
 * may be animated if `animateLayoutChanges` is enabled.
 */
private const val VIEW_STATE_ERROR = 1
/**
 * Represents the constant value used to define an empty view state in the application's UI.
 * This value is typically utilized to indicate that there is no data or content to display
 * in a specific view or section.
 *
 * @value 2 The numeric value representing the "empty" state.
 * This can be used for comparisons or logic when determining what to render in the UI.
 *
 * Example:
 * ```kotlin
 * val currentState = getCurrentViewState()
 * if (currentState == VIEW_STATE_EMPTY) {
 *     showEmptyStateMessage()
 * } else {
 *     displayContent()
 * }
 * ```
 */
private const val VIEW_STATE_EMPTY = 2
/**
 * Represents the loading state in a view's state management system.
 * This constant is used to identify and handle scenarios where the view
 * is currently in a loading state, e.g., fetching data from a network
 * or performing asynchronous operations.
 *
 * Usage:
 * VIEW_STATE_LOADING can be used in a state management mechanism to
 * differentiate between various view states, such as loading, success,
 * or error.
 *
 * Example:
 * ```kotlin
 * when (viewState) {
 *     VIEW_STATE_LOADING -> showLoadingIndicator()
 *     VIEW_STATE_SUCCESS -> showContent()
 *     VIEW_STATE_ERROR -> showError()
 * }
 * ```
 */
private const val VIEW_STATE_LOADING = 3
