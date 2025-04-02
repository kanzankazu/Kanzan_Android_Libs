package com.kanzankazu.kanzanutil.kanzanextension.view

import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup

/**
 * Retrieves the index of the selected RadioButton within a RadioGroup. If no RadioButton is selected,
 * the method returns -1.
 *
 * @return The index of the currently selected RadioButton within the RadioGroup,
 *         or -1 if no RadioButton is selected.
 *
 * Example:
 * ```kotlin
 * val radioGroup: RadioGroup = findViewById(R.id.my_radio_group)
 * val selectedIndex = radioGroup.getRadioGroupIndex()
 *
 * if (selectedIndex != -1) {
 *     val radioButton = radioGroup.getChildAt(selectedIndex) as RadioButton
 *     println("Selected RadioButton text: ${radioButton.text}")
 * } else {
 *     println("No RadioButton is selected.")
 * }
 * ```
 */
fun RadioGroup.getRadioGroupIndex(): Int {
    val radioButtonID = checkedRadioButtonId
    val view: View? = findViewById(radioButtonID)
    view?.let {
        val index: Int = indexOfChild(view)
        val radioButton = getChildAt(index) as RadioButton
        radioButton.string()
        return index
    } ?: kotlin.run { return -1 }

}

/**
 * Sets a callback that provides the index of the currently selected radio button in the RadioGroup.
 * If no radio button is selected, the callback receives -1.
 *
 * @param onRadioClick A lambda function to be invoked with the index of the selected radio button in this RadioGroup.
 *                     The index is based on the position of the child views. If no selection is made, -1 is passed.
 *
 * Example:
 * ```kotlin
 * radioGroup.getRadioGroupIndex { selectedIndex ->
 *     if (selectedIndex != -1) {
 *         println("Selected RadioButton index: $selectedIndex")
 *     } else {
 *         println("No RadioButton is selected.")
 *     }
 * }
 * ```
 */
fun RadioGroup.getRadioGroupIndex(onRadioClick: (index: Int) -> Unit) {
    onRadioClick(this.getRadioGroupIndex())
}

/**
 * Sets the checked state of a RadioButton within a RadioGroup based on the specified index.
 * If the `isChecked` parameter is set to true (default), the RadioButton at the given index is checked.
 *
 * @param index The index of the RadioButton within the RadioGroup to be updated.
 *              This must be a valid index within the bounds of the RadioGroup's children.
 * @param isChecked A boolean value indicating whether the RadioButton should be checked.
 *                  Default value is `true`.
 * @return The RadioButton instance located at the specified index.
 *
 * Example:
 * ```kotlin
 * // Assuming there is a RadioGroup with 3 RadioButtons
 * val radioGroup: RadioGroup = findViewById(R.id.my_radio_group)
 *
 * // Set the second RadioButton (index 1) as checked
 * val checkedRadioButton = radioGroup.setRadioGroupIndex(index = 1)
 *
 * // Accessing the returned RadioButton's properties
 * println("Checked RadioButton text: ${checkedRadioButton.text}")
 * ```
 */
fun RadioGroup.setRadioGroupIndex(index: Int, isChecked: Boolean = true): RadioButton {
    val radioButton = getChildAt(index) as RadioButton
    if (isChecked) radioButton.isChecked = true
    return radioButton
}