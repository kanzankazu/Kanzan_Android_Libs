@file:Suppress("DEPRECATION", "unused", "UNUSED_PARAMETER")

package com.kanzankazu.kanzanutil.kanzanextension.view

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.CheckBox
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Switch
import android.widget.TextView
import androidx.annotation.AnimRes
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.annotation.MenuRes
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.core.view.isVisible
import com.kanzankazu.kanzanutil.OnSingleClickListener
import com.kanzankazu.kanzanutil.kanzanextension.type.dpTopx

/**
 * Inflates a layout resource into a View.
 *
 * This method is an extension for the `Activity` class, allowing you to inflate
 * a layout resource using the `LayoutInflater` associated with the activity without
 * attaching it to a parent view group.
 *
 * @param layout The layout resource ID to be inflated. Must be a valid XML layout resource annotated with `@LayoutRes`.
 * @return The inflated `View` corresponding to the provided layout resource.
 *
 * Example:
 * ```kotlin
 * val inflatedView = this.inflate(R.layout.my_layout)
 * setContentView(inflatedView)
 * ```
 */
fun Activity.inflate(@LayoutRes layout: Int): View {
    return layoutInflater.inflate(layout, null)
}

/**
 * Inflates a layout resource into a view and attaches it to the specified ViewGroup.
 * This method is designed to simplify the process of inflating layouts using a ViewGroup's context.
 *
 * @param layout The layout resource ID of the view to inflate.
 * @return The inflated View bound to the ViewGroup's context, without attaching it to the parent ViewGroup.
 *
 * Example:
 * ```kotlin
 * val parent: ViewGroup = findViewById(R.id.container)
 * val inflatedView = parent.inflate(R.layout.custom_layout)
 * ```
 */
fun ViewGroup.inflate(@LayoutRes layout: Int): View {
    return LayoutInflater.from(context).inflate(layout, this, false)
}

/**
 * Makes the View visible. Optionally applies an animation if the View is currently gone.
 *
 * @param anim The resource ID of the animation to apply when making the View visible. Defaults to 0 (no animation).
 *
 * Example:
 * ```kotlin
 * val myView: View = findViewById(R.id.my_view)
 * myView.visible(R.anim.fade_in) // Makes the View visible with a fade-in animation
 * myView.visible() // Makes the View visible without any animation
 * ```
 */
fun View.visible(anim: Int = 0) {
    if (!isVisible()) {
        if (anim != 0 && isGone()) {
            val loadAnimation = AnimationUtils.loadAnimation(this.context, anim)
            startAnimation(loadAnimation)
        }
        //TransitionManager.beginDelayedTransition(parent as ViewGroup)
        visibility = View.VISIBLE
    }
}

/**
 * Checks whether the visibility of the current View is set to `VISIBLE`.
 *
 * @return `true` if the View's visibility is `View.VISIBLE`, otherwise `false`.
 *
 * Example:
 * ```kotlin
 * val myView: View = findViewById(R.id.my_view)
 * if (myView.isVisible()) {
 *     // Perform actions when the view is visible
 * }
 * ```
 */
fun View.isVisible(): Boolean = this.visibility == View.VISIBLE

/**
 * Sets the visibility of a View to INVISIBLE. If the View is currently visible and an animation resource
 * is specified, the specified animation will be played before changing the visibility.
 *
 * @param anim The resource ID of the animation to play when making the View invisible. Defaults to 0 (no animation).
 *
 * This function ensures that if the View is already invisible, no changes or additional animations are applied.
 * Additionally, the function checks if the View is currently visible before applying the animation.
 *
 * Example:
 * ```kotlin
 * myView.invisible(R.anim.fade_out)
 * ```
 */
fun View.invisible(anim: Int = 0) {
    if (!isInvisible()) {
        if (anim != 0 && isVisible()) {
            val loadAnimation = AnimationUtils.loadAnimation(this.context, anim)
            startAnimation(loadAnimation)
        }
        //TransitionManager.beginDelayedTransition(parent as ViewGroup)
        visibility = View.INVISIBLE
    }
}

/**
 * Checks whether the visibility of the current View is set to `View.INVISIBLE`.
 * A View with `View.INVISIBLE` visibility is not visible on the screen but still takes up space for layout purposes.
 *
 * @return `true` if the View's visibility is `View.INVISIBLE`, `false` otherwise.
 *
 * Example:
 * ```kotlin
 * val myView: View = findViewById(R.id.my_view)
 * if (myView.isInvisible()) {
 *     // Perform actions when the view is invisible
 * }
 * ```
 */
fun View.isInvisible(): Boolean = this.visibility == View.INVISIBLE

/**
 * Sets the visibility of a View to GONE with optional animation.
 * If the view is already in the GONE state, no changes are made.
 * If an animation resource is provided and the view is currently VISIBLE, the animation is applied before setting the view's visibility to GONE.
 *
 * @param anim The resource ID of the animation to play before the view is set to GONE. Defaults to 0 (no animation).
 *
 * Example:
 * ```kotlin
 * val myView: View = findViewById(R.id.my_view)
 * myView.gone(R.anim.fade_out) // Plays fade-out animation, then sets visibility to GONE
 * ```
 */
fun View.gone(anim: Int = 0) {
    if (!isGone()) {
        if (anim != 0 && isVisible()) {
            val loadAnimation = AnimationUtils.loadAnimation(this.context, anim)
            startAnimation(loadAnimation)
        }
        //TransitionManager.beginDelayedTransition(parent as ViewGroup)
        visibility = View.GONE
    }
}

/**
 * Checks if a `View` has its visibility set to `View.GONE`.
 *
 * This method can be used to determine whether the `View` is completely
 * hidden from the layout and does not take up any space in the layout hierarchy.
 *
 * @return `true` if the `View`'s visibility is `View.GONE`, `false` otherwise.
 *
 * Example:
 * ```kotlin
 * val myView: View = findViewById(R.id.my_view)
 * if (myView.isGone()) {
 *     println("The view is gone!")
 * }
 * ```
 */
fun View.isGone(): Boolean = this.visibility == View.GONE

/**
 * Updates the visibility of a View and sets the text for multiple TextViews using the provided list of strings.
 * If the list of strings is empty, the View will be made either invisible or gone, based on the `isInvisible` parameter.
 *
 * @param ss A list of nullable strings whose size should match the number of TextViews. Each string in the list is used to update the corresponding
 *  TextView's text.
 * @param textViews A variable number of TextViews to be updated with the text from the `ss` list.
 * @param isInvisible Optional boolean parameter. If `true`, the View will be made invisible when the list of strings is empty. If `false`, the View
 *  will be made gone instead. Defaults to `false`.
 * @return A boolean value indicating success. Returns `true` if the list of strings is not empty and all TextViews were updated successfully. Returns
 *  `false` if the list is empty (resulting in the View being made invisible or gone).
 *
 * Example:
 * ```kotlin
 * val view: View = findViewById(R.id.my_view)
 * val textView1: TextView = findViewById(R.id.text_view_1)
 * val textView2: TextView = findViewById(R.id.text_view_2)
 * val textList = listOf("Hello", "World")
 *
 * val isSuccess = view.visibleView(ss = textList, textView1, textView2, isInvisible = true)
 * // Updates the text for textView1 to "Hello" and textView2 to "World".
 * // Sets the visibility of `view` to VISIBLE.
 * // Returns `true`.
 *
 * val emptyList = listOf<String?>()
 * val isEmptySuccess = view.visibleView(ss = emptyList, textView1, isInvisible = false)
 * // Makes the view gone and returns `false`.
 * ```
 */
fun View.visibleView(ss: List<String?>, vararg textViews: TextView, isInvisible: Boolean = false): Boolean {
    val stringSize = ss.size
    val tvLength = textViews.size
    return if (stringSize != 0) {
        if (stringSize == tvLength) {
            for (i in ss.indices) {
                val textView = textViews[i]
                textView.text = ss[i]
            }
        }
        visible()
        true
    } else {
        if (isInvisible) invisible() else gone()
        false
    }
}

/**
 * Updates the visibility of a view based on the provided list of strings and TextView elements.
 * Depending on the size of the list, it assigns text to the provided TextView(s),
 * makes the view visible, or hides it using the specified visibility method.
 *
 * @param ss A list of strings where the size determines the behavior:
 *           - If the list size is between 1 and 2 (inclusive), the corresponding items are set as text for `textView1` and `textView2`.
 *           - If the list size is outside this range, visibility is adjusted based on the `isInvisible` flag.
 * @param textView1 The first TextView to which the text from the list will be assigned.
 * @param textView2 The second TextView to which the text from the list will be assigned, if available.
 * @param isInvisible A boolean flag used to determine the hiding behavior when the list size is not within the valid range:
 *                    - `true`: Sets visibility to `INVISIBLE`.
 *                    - `false`: Sets visibility to `GONE`. Defaults to `false`.
 * @return `true` if the list size is between 1 and 2, and the view is made visible; `false` otherwise.
 *
 * Example:
 * ```kotlin
 * val stringList = listOf("Hello", "World")
 * val textView1 = findViewById<TextView>(R.id.text1)
 * val textView2 = findViewById<TextView>(R.id.text2)
 * val view = findViewById<View>(R.id.myView)
 *
 * // Makes the view visible and sets textView1.text = "Hello", textView2.text = "World"
 * val isVisible = view.visibleView(stringList, textView1, textView2)
 *
 * val emptyList = listOf<String>()
 * // Hides the view using `GONE` visibility
 * val isVisibleEmpty = view.visibleView(emptyList, textView1, textView2)
 * ```
 */
fun View.visibleView(ss: List<String?>, textView1: TextView, textView2: TextView, isInvisible: Boolean = false): Boolean {
    val stringSize = ss.size
    return if (stringSize in 1..2) {
        textView1.text = ss[0]
        textView2.text = ss[1]
        visible()
        true
    } else {
        if (isInvisible) invisible() else gone()
        false
    }
}

/**
 * Updates the visibility of a `View` and sets text to the provided `TextView` based on the given string.
 * If the string is null or empty, the `View` is hidden (either invisible or gone based on the provided flag)
 * and the `TextView` is cleared. Otherwise, the `View` is made visible and the string is set as the text of the `TextView`.
 *
 * @param textView The `TextView` to update with the provided string. The text is cleared if the string is null or empty.
 * @param s The string to display in the `TextView`. If null or empty, the `textView`'s text is cleared, and the `View` is hidden.
 * @param isInvisible A Boolean flag indicating how to hide the `View` when the string is null or empty.
 *                    If `true`, the `View` is set to invisible. If `false` (default), the `View` is set to gone.
 * @return A Boolean indicating whether the string is non-empty. Returns `true` if the string is not null or empty, otherwise `false`.
 *
 * Example:
 * ```kotlin
 * val myView: View = findViewById(R.id.my_view)
 * val myTextView: TextView = findViewById(R.id.my_text_view)
 *
 * // Example with non-empty string
 * myView.visibleView(myTextView, "Hello World!") // Makes the view visible and sets the text to "Hello World!"
 *
 * // Example with null string and default behavior
 * myView.visibleView(myTextView, null) // Hides the view (gone) and clears the text in the TextView.
 *
 * // Example with null string and invisible flag
 * myView.visibleView(myTextView, null, isInvisible = true) // Sets the view to invisible and clears the text in the TextView.
 * ```
 */
fun View.visibleView(textView: TextView, s: String?, isInvisible: Boolean = false): Boolean {
    return if (!s.isNullOrEmpty()) {
        visible()
        textView.text = s
        true
    } else {
        if (isInvisible) invisible() else gone()
        textView.text = ""
        false
    }
}

/**
 * Updates the visibility state of a View and optionally adjusts its visibility mode (visible, invisible, or gone).
 * If the View is a CheckBox or a Switch, its checked state is also updated based on the provided Boolean value.
 *
 * @param b A Boolean value indicating whether the View should be visible. If true, the View will be made visible; otherwise, its visibility depends
 *  on the `isInvisible` parameter.
 * @param isShowHide A Boolean value that determines whether the visibility state is managed recursively using the `visibleView(b)` method. Defaults
 *  to true.
 * @param isInvisible A Boolean flag to determine whether the View should be set to `invisible` instead of `gone` when not visible. Defaults to false
 * .
 * @return A Boolean value representing the visibility state of the View. Returns `true` if the View becomes visible, or `false` otherwise.
 *
 * Example:
 * ```kotlin
 * val myView: View = findViewById(R.id.my_view)
 * myView.visibleView(b = true, isShowHide = false, isInvisible = true) // Sets the View to visible
 *
 * val myCheckbox: CheckBox = findViewById(R.id.my_checkbox)
 * myCheckbox.visibleView(b = true, isShowHide = true) // Sets the CheckBox visible and checked
 * ```
 */
fun View.visibleView(b: Boolean, isShowHide: Boolean = true, isInvisible: Boolean = false): Boolean {
    if (b && this is CheckBox) isChecked = b
    else if (b && this is Switch) isChecked = b

    if (isShowHide) visibleView(b)
    return b
}

/**
 * Toggles the visibility of a View based on an integer condition, with optional parameters for controlling visibility behavior.
 *
 * @param i An integer condition. The View will be visible if `i != 0`.
 * @param isShowHide Boolean flag indicating whether to show or hide the View. Defaults to `true`, meaning the View will show/hide based on the condition
 * .
 * @param isInvisible Boolean flag indicating whether the View should become invisible instead of completely gone when hiding. Defaults to `false`.
 * @return A Boolean indicating whether the View's visibility was modified successfully, based on the given condition.
 *
 * Example:
 * ```kotlin
 * val myView: View = findViewById(R.id.my_view)
 * myView.visibleView(1) // The View becomes visible.
 * myView.visibleView(0, isShowHide = true, isInvisible = true) // The View becomes invisible.
 * ```
 */
fun View.visibleView(i: Int, isShowHide: Boolean = true, isInvisible: Boolean = false): Boolean {
    return visibleView(i != 0, isShowHide, isInvisible)
}

/**
 * Sets the visibility of a View based on the provided string value and other optional parameters.
 * If the View is a TextView, the text is updated with the provided string. The visibility is
 * controlled based on whether the string is null or empty.
 *
 * @param s The string to be displayed (e.g., for a TextView). If null or empty, the View's visibility
 *          is managed according to the other parameters.
 * @param isShowHide A Boolean indicating whether to show or hide the View. Defaults to true.
 * @param isInvisible A Boolean indicating whether to make the View invisible instead of gone
 *                    when the string is null or empty. Defaults to false.
 * @return A Boolean indicating whether the View is visible (`true`) or not (`false`) after execution.
 *
 * Example:
 * ```kotlin
 * val textView: TextView = findViewById(R.id.textView)
 * textView.visibleView("Hello, World!") // Makes the TextView visible with the text set to "Hello, World!"
 * textView.visibleView(null, isShowHide = true) // Hides the TextView (sets it to gone by default).
 * ```
 */
fun View.visibleView(s: String?, isShowHide: Boolean = true, isInvisible: Boolean = false): Boolean {
    val b = !s.isNullOrEmpty()
    if (b && this is TextView) {
        text = s
    }

    if (isShowHide) visibleView(b)
    return b
}

/**
 * Adjusts the visibility of the current View based on the provided conditions.
 * If the `sCompare` string is not null or empty and the View is a `TextView`, the text
 * will be updated to `sForText`. Then, depending on the `isShowHide` flag, the visibility
 * of the View is updated.
 *
 * @param sCompare A `String?` used to determine the visibility state. If it is null or empty, the View will be hidden.
 * @param sForText A `String?` used as the text for the View if the `sCompare` condition is satisfied, and the View is a `TextView`.
 * @param isShowHide A `Boolean` indicating if the visibility logic should be applied. Defaults to `true`.
 *                   If set to `false`, the visibility remains unchanged.
 * @param isInvisible A `Boolean` indicating if the View should be set to `View.INVISIBLE` instead of `View.GONE` when hiding.
 *                    Defaults to `false`, meaning the View will be set to `GONE` unless specified otherwise.
 * @return A `Boolean` indicating whether the View was made visible (`true`) or hidden (`false`) based on the `sCompare` evaluation.
 *
 * Example:
 * ```kotlin
 * val textView: TextView = findViewById(R.id.my_text_view)
 * val wasVisible = textView.visibleView(sCompare = "example", sForText = "New Text", isShowHide = true, isInvisible = false)
 * ```
 */
fun View.visibleView(sCompare: String?, sForText: String?, isShowHide: Boolean = true, isInvisible: Boolean = false): Boolean {
    val b = !sCompare.isNullOrEmpty()
    if (b && this is TextView) {
        text = sForText
    }

    if (isShowHide) visibleView(b)
    return b
}

/**
 * Updates the visibility of a `View` based on a boolean parameter and optionally applies animation.
 *
 * If the boolean parameter `b` is `true`, the view will be made visible, and an optional animation resource can be applied.
 * If `b` is `false`, the view will be hidden (`GONE`), and an optional hiding animation can be applied.
 *
 * @param b A boolean value indicating whether the view should be visible (`true`) or hidden (`false`).
 * @param animShow An optional animation resource ID to be used when making the view visible. Defaults to 0 (no animation).
 * @param animHide An optional animation resource ID to be used when hiding the view. Defaults to 0 (no animation).
 * @return A boolean value indicating the final visibility state of the view. Returns `true` if the view is made visible, `false` otherwise.
 *
 * Example:
 * ```kotlin
 * val myView: View = findViewById(R.id.my_view)
 * myView.visibleView(true, R.anim.fade_in, R.anim.fade_out) // Makes the view visible with fade-in animation
 * myView.visibleView(false) // Hides the view with no animation
 * ```
 */
fun View.visibleView(
    b: Boolean,
    @AnimRes animShow: Int = 0,
    @AnimRes animHide: Int = 0,
): Boolean {
    return if (b) {
        visible(animShow)
        true
    } else {
        gone(animHide)
        false
    }
}

/**
 * Toggles the visibility of a `View` based on the provided parameters.
 * If the `b` parameter is `true`, the view becomes visible.
 * If `b` is `false`, the view is either set to "invisible" or "gone" based on the `isInvisible` parameter.
 *
 * @param b A `Boolean` indicating whether the `View` should be visible. If `true`, the view becomes visible. If `false`, the view is either invisible
 *  or gone depending on the `isInvisible` parameter.
 * @param isInvisible A `Boolean` indicating the secondary visibility state when `b` is `false`.
 *                    If `true`, the view is set to "invisible" (occupying space but not visible).
 *                    If `false`, the view is set to "gone" (removed from layout entirely). Defaults to `false`.
 * @return A `Boolean` indicating the visibility state of the `View`. Returns `true` if the view is made visible, otherwise `false`.
 *
 * Example:
 * ```kotlin
 * val myView: View = findViewById(R.id.my_view)
 * myView.visibleView(true) // The view becomes visible
 * myView.visibleView(false, isInvisible = true) // The view becomes invisible
 * myView.visibleView(false) // The view is removed (gone)
 * ```
 */
fun View.visibleView(b: Boolean, isInvisible: Boolean = false): Boolean {
    return if (b) {
        visible()
        true
    } else {
        if (isInvisible) invisible() else gone()
        false
    }
}

/**
 * Updates the visibility of the current View based on the visibility of the provided views.
 * The current View becomes visible if at least one of the specified views is visible.
 *
 * @param views A variable number of views whose visibility is used to control the visibility of the current view.
 *
 * Example:
 * ```kotlin
 * val view1: View = findViewById(R.id.view1)
 * val view2: View = findViewById(R.id.view2)
 * val targetView: View = findViewById(R.id.target_view)
 *
 * view1.isVisible = true
 * view2.isVisible = false
 * targetView.visibleView(view1, view2) // targetView.isVisible will be true because view1 is visible
 * ```
 */
fun View.visibleView(vararg views: View) {
    isVisible = views.any { it.isVisible }
}

/**
 * Updates the visibility of a View based on the boolean values provided.
 * The `View` will be visible if any of the passed boolean values are `true`.
 *
 * @param b A vararg of boolean values. The View's visibility will be set to `VISIBLE` if at least one of the values is `true`, otherwise it remains
 *  `GONE`.
 *
 * Example:
 * ```kotlin
 * val myView: View = findViewById(R.id.my_view)
 * myView.visibleView(true, false) // View becomes visible
 * myView.visibleView(false, false) // View becomes gone
 * ```
 */
fun View.visibleView(vararg b: Boolean) {
    isVisible = b.any { it }
}

/**
 * Checks if at least one of the provided views is currently visible.
 *
 * This function iterates through the provided views and checks their visibility status.
 * A view is considered visible if its visibility is set to `View.VISIBLE`.
 * If at least one view is visible, the method returns `true`; otherwise, it returns `false`.
 *
 * @param views Vararg parameter of `View` objects to be checked for visibility.
 * @return `true` if at least one view is visible, `false` otherwise.
 *
 * Example:
 * ```kotlin
 * val button = findViewById<Button>(R.id.my_button)
 * val textView = findViewById<TextView>(R.id.my_text_view)
 * val result = visibleView(button, textView) // Returns true if at least one of the views is visible
 * ```
 */
fun visibleView(vararg views: View): Boolean {
    val mutableListOfVisible = mutableListOf<Boolean>()
    views.forEach { mutableListOfVisible.add(it.isVisible()) }
    return mutableListOfVisible.any { it }
}

/**
 * Changes the visibility of multiple views based on the provided boolean values.
 *
 * @param b A boolean value indicating whether the views should be visible or not.
 *          - If `true`, the views are set to visible.
 *          - If `false`, the visibility is determined based on the `isInvisible` parameter.
 * @param views A vararg parameter representing the views whose visibility will be updated.
 * @param isInvisible A boolean value determining the visibility state when `b` is `false`.
 *                    - If `true`, the views are set to `View.INVISIBLE`.
 *                    - If `false`, the views are set to `View.GONE`. Defaults to `false`.
 *
 * Example:
 * ```kotlin
 * val view1: View = findViewById(R.id.view1)
 * val view2: View = findViewById(R.id.view2)
 *
 * // Set both views to visible
 * visibleViews(true, view1, view2)
 *
 * // Set both views to gone
 * visibleViews(false, view1, view2, isInvisible = false)
 *
 * // Set both views to invisible
 * visibleViews(false, view1, view2, isInvisible = true)
 * ```
 */
fun visibleViews(b: Boolean, vararg views: View, isInvisible: Boolean = false) {
    views.forEach { it.visibleView(b, isInvisible) }
}

/**
 * Enables or disables a `View` and its associated interactions depending on the provided boolean value.
 * For `RadioGroup` views, it enables or disables its child `RadioButton` elements as well.
 *
 * @param b A boolean value indicating whether the view should be enabled or disabled.
 *          If `true`, the view and interactions are enabled; if `false`, the view and interactions
 *          are disabled.
 *
 * Example:
 * ```kotlin
 * val myButton: View = findViewById(R.id.my_button)
 * myButton.enableView(false) // Disables the button and interactions
 * ```
 */
fun View.enableView(b: Boolean) {
    when (this) {
        is RadioGroup -> for (i in 0 until childCount) (getChildAt(i) as RadioButton).isEnabled = b
        else -> if (b) {
            isEnabled = true
            isClickable = true
        } else {
            isEnabled = false
            isClickable = false
        }
    }
}

/**
 * Enables or disables a collection of views based on the provided boolean value.
 * This method iterates through the given views and toggles their `isEnabled` and `isClickable` properties.
 *
 * @param b A boolean value indicating whether to enable (`true`) or disable (`false`) the views.
 * @param views A vararg parameter representing the collection of views to be enabled or disabled.
 *
 * Example:
 * ```kotlin
 * val button: Button = findViewById(R.id.myButton)
 * val textView: TextView = findViewById(R.id.myTextView)
 * enableViews(false, button, textView) // Disables both views
 * enableViews(true, button, textView) // Enables both views
 * ```
 */
fun enableViews(b: Boolean, vararg views: View) {
    views.forEach { it.enableView(b) }
}

/**
 * Disables a View by setting its `isClickable` and `isEnabled` properties to false.
 * This can be used to prevent any interaction with the View until it is re-enabled.
 *
 * @receiver The View to be disabled.
 *
 * Example:
 * ```kotlin
 * val button: View = findViewById(R.id.myButton)
 * button.disable() // The button is now disabled and cannot be interacted with.
 * ```
 */
fun View.disable() {
    this.isClickable = false
    this.isEnabled = false
}

/**
 * Sets a click listener on a `View` that triggers a specified action when clicked.
 * This function simplifies the process of setting an `OnClickListener` by allowing
 * the use of a lambda function to define the action.
 *
 * @param listener A lambda function representing the action to be performed when the `View` is clicked.
 *
 * Example:
 * ```kotlin
 * val myButton: View = findViewById(R.id.my_button)
 * myButton.click {
 *     // Perform some action
 *     Toast.makeText(context, "Button clicked!", Toast.LENGTH_SHORT).show()
 * }
 * ```
 */
fun View.click(listener: () -> Unit) {
    setOnClickListener { listener() }
}

/**
 * Adjusts the transparency level of the View by setting its alpha property.
 * The alpha value controls the opacity of the View, where 0 represents fully transparent
 * and 1 represents fully opaque.
 *
 * @param value The desired transparency level of the View, ranging from 0 (fully transparent)
 *              to 1 (fully opaque).
 *
 * Example:
 * ```kotlin
 * val myView: View = findViewById(R.id.my_view)
 * myView.fade(0.5f) // Sets the View to 50% opacity.
 * ```
 */
fun View.fade(value: Float) {
    this.alpha = value
}

/**
 * Retrieves the resource entry name of the view's ID if it exists. If the view's ID is not set,
 * it returns the ID as a string.
 *
 * @return The resource entry name as a string if the ID is valid, or the ID itself (as a string)
 * if the ID is not set (e.g., `-1`).
 *
 * Example:
 * ```kotlin
 * val someView: View = findViewById(R.id.my_view)
 * val idName = someView.idName() // "my_view" if R.id.my_view exists
 *
 * val unnamedView = View(context)
 * val idFallback = unnamedView.idName() // "-1" since no ID is assigned
 * ```
 */
fun View.idName(): String? {
    return if (id != -1) resources.getResourceEntryName(id)
    else id.toString()
}

/**
 * Sets padding for a View in density-independent pixels (dp). The padding values are converted to pixels (px)
 * based on the device's screen density before being applied to the View.
 *
 * @param left The padding in dp to apply to the left side of the View. Defaults to 0.
 * @param top The padding in dp to apply to the top of the View. Defaults to 0.
 * @param right The padding in dp to apply to the right side of the View. Defaults to 0.
 * @param bottom The padding in dp to apply to the bottom of the View. Defaults to 0.
 *
 * Example:
 * ```kotlin
 * val myView: View = findViewById(R.id.my_view)
 * myView.paddingByDp(left = 16, top = 8, right = 16, bottom = 8) // Sets padding in dp
 * ```
 */
fun View.paddingByDp(left: Int = 0, top: Int = 0, right: Int = 0, bottom: Int = 0) {
    setPadding(left.dpTopx(), top.dpTopx(), right.dpTopx(), bottom.dpTopx())
}

/**
 * Calculates the total height of the view, including its measured height and top/bottom margins.
 *
 * @return The total height of the view as an `Int`, which is the sum of the view's measured height, top margin, and bottom margin.
 *
 * Example:
 * ```kotlin
 * val myView: View = findViewById(R.id.my_view)
 * val totalHeight = myView.getHeightView() // Returns the total height including margins
 * ```
 */
fun View.getHeightView(): Int {
    val layoutParams = layoutParams as ViewGroup.MarginLayoutParams
    return measuredHeight + layoutParams.topMargin + layoutParams.bottomMargin
}

/**
 * Sets an OnClickListener for a View that prevents multiple rapid clicks by introducing a delay
 * to avoid triggering the click event multiple times in quick succession.
 *
 * @param l The OnClickListener to set for the View. This listener will be triggered only when
 *          subsequent clicks occur after the defined delay period has passed since the last click.
 *
 * Example:
 * ```kotlin
 * val button: Button = findViewById(R.id.my_button)
 * button.setOnSingleClickListener(View.OnClickListener {
 *     // Handle click event here
 *     Toast.makeText(context, "Button clicked!", Toast.LENGTH_SHORT).show()
 * })
 * ```
 */
fun View.setOnSingleClickListener(l: View.OnClickListener) {
    setOnClickListener(OnSingleClickListener(l))
}

/**
 * Sets a click listener on the View that prevents multiple rapid clicks within a short duration.
 * This helps to prevent issues such as accidental double-clicks or unexpected behavior caused by
 * multiple triggered events in quick succession.
 *
 * @param l A lambda function to be invoked when the View is clicked. The lambda takes the clicked View as a parameter.
 *
 * Example:
 * ```kotlin
 * myButton.setOnSingleClickListener {
 *     // Handle the single click action
 *     Toast.makeText(context, "Button clicked!", Toast.LENGTH_SHORT).show()
 * }
 * ```
 */
fun View.setOnSingleClickListener(l: (View) -> Unit) {
    setOnClickListener(OnSingleClickListener(l))
}

/**
 * Sets a drawable resource as the background of the calling View.
 *
 * @param drawableResource The resource ID of the drawable to be set as the background.
 *                         Must be a valid drawable resource.
 *
 * Example:
 * ```kotlin
 * val myView: View = findViewById(R.id.my_view)
 * myView.backgroundDrawable(R.drawable.my_background)
 * ```
 */
fun View.backgroundDrawable(@DrawableRes drawableResource: Int) {
    background = ContextCompat.getDrawable(context, drawableResource)
}

/**
 * Displays a popup menu anchored to the current view with specified menu items and a listener for handling menu item selection.
 * This method sets a single click listener on the view to avoid multiple rapid clicks, ensuring stability.
 *
 * @param context The context used to create the popup menu.
 * @param menuRes The resource ID of the menu to be inflated into the popup menu.
 * @param listener A lambda function that handles menu item click events. The selected `MenuItem` is passed as a parameter to the listener.
 *
 * Example:
 * ```kotlin
 * val myView: View = findViewById(R.id.my_view)
 * myView.popUpMenu(context, R.menu.example_menu) { item ->
 *     when (item.itemId) {
 *         R.id.option_one -> {
 *             // Handle option one selected
 *             true
 *         }
 *         R.id.option_two -> {
 *             // Handle option two selected
 *             true
 *         }
 *         else -> false
 *     }
 * }
 * ```
 */
fun View.popUpMenu(context: Context, @MenuRes menuRes: Int, listener: (MenuItem) -> Boolean) {
    setOnSingleClickListener {
        val popupMenu = PopupMenu(context, it)
        popupMenu.setOnMenuItemClickListener { item -> listener(item) }
        popupMenu.menuInflater.inflate(menuRes, popupMenu.menu)
        popupMenu.show()
    }
}
/**
 * Casts the current View object to a ViewGroup if possible.
 * If the View is not a ViewGroup, it returns null.
 *
 * @return The View object cast to ViewGroup, or null if the View is not a ViewGroup.
 *
 * Example:
 * ```kotlin
 * val myView: View = findViewById(R.id.my_view)
 * val myViewGroup: ViewGroup? = myView.getViewGroup()
 * if (myViewGroup != null) {
 *     // Perform operations specific to ViewGroup
 * }
 * ```
 */
fun View.getViewGroup(): ViewGroup? = this as? ViewGroup

/**
 * Retrieves all immediate child views of a given ViewGroup and returns them in an ArrayList.
 *
 * This method iterates through all child views within the ViewGroup and collects them into an
 * ArrayList, which is then returned. Only the direct children of the ViewGroup are included
 * in the result.
 *
 * @return An ArrayList containing all the immediate child views of the ViewGroup.
 *
 * Example:
 * ```kotlin
 * val myViewGroup: ViewGroup = findViewById(R.id.container)
 * val childViews: ArrayList<View> = myViewGroup.getAllView()
 * // childViews now contains all direct children of the ViewGroup
 * ```
 */
fun ViewGroup.getAllView(): ArrayList<View> {
    val arrayListOf = arrayListOf<View>()
    this.forEach(arrayListOf::add)
    return arrayListOf
}

/**
 * Retrieves all child views of a specified type `T` from a ViewGroup.
 * The function iterates over all child views in the ViewGroup, checks their type,
 * and adds matching views to the resulting list.
 *
 * @param T The type of views to be retrieved. Must be specified as a generic type.
 * @return An ArrayList containing all child views of the specified type `T` within the ViewGroup.
 *         If no matching views are found, the function returns an empty list.
 *
 * Example:
 * ```kotlin
 * val myViewGroup: ViewGroup = findViewById(R.id.my_container)
 * val buttons: ArrayList<Button> = myViewGroup.getAllViewDetail()
 * ```
 */
inline fun <reified T> ViewGroup.getAllViewDetail(): ArrayList<T> {
    val arrayListOf = arrayListOf<T>()
    forEach { if (it is T) arrayListOf.add(it) }
    return arrayListOf
}

/**
 * Filters the elements of an ArrayList containing `View` objects and returns a new ArrayList
 * consisting of only the elements of the specified type `T`.
 * This is achieved using a generic inline function with type reification.
 *
 * @param T The type of elements to be retrieved from the ArrayList.
 * @return A new ArrayList containing only the elements of type `T`
 *         from the original ArrayList.
 *
 * Example:
 * ```kotlin
 * val views: ArrayList<View> = arrayListOf(buttonView, textView, imageView)
 * val textViewList: ArrayList<TextView> = views.getAllViewDetail<TextView>()
 * // textViewList will contain only elements of type TextView from the views list.
 * ```
 */
inline fun <reified T> ArrayList<View>.getAllViewDetail(): ArrayList<T> {
    val arrayListOf = arrayListOf<T>()
    forEach { if (it is T) arrayListOf.add(it) }
    return arrayListOf
}

/**
 * Evaluates a list of Triples where each Triple consists of a `View`, a lambda function to execute,
 * and a list of predicate functions. The method processes the Triples, performing specific actions
 * based on the type of View and determining an aggregate boolean result.
 *
 * - If the `View` is an instance of `EditText`, custom logic can be applied (currently unused).
 * - For other View types, the method adds `true` to a list of evaluation results.
 * - Finally, it returns the aggregate result of all evaluations by checking if all entries in the list are `true`.
 *
 * @return `true` if all evaluations in the list return `true`; otherwise, `false`.
 *
 * Example:
 * ```kotlin
 * val tripleList = arrayListOf<Triple<View, () -> Unit, ArrayList<(View) -> Boolean>>>()
 * val result = tripleList.asd() // Evaluates and returns true/false based on the processing.
 * ```
 */
fun ArrayList<Triple<View, () -> Unit, ArrayList<(View) -> Boolean>>>.asd(): Boolean {
    val b = arrayListOf<Boolean>()
    forEach { triple ->
        when (triple.first) {
            is EditText -> {
                (triple.first as EditText).also {

                }
            }

            else -> b.add(true)
        }
    }
    return b.all { it }
}
