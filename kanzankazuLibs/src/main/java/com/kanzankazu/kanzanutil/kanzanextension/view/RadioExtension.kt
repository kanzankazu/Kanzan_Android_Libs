package com.kanzankazu.kanzanutil.kanzanextension.view

import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup

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

fun RadioGroup.getRadioGroupIndex(onRadioClick: (index: Int) -> Unit) {
    onRadioClick(this.getRadioGroupIndex())
}

fun RadioGroup.setRadioGroupIndex(index: Int, isChecked: Boolean = true): RadioButton {
    val radioButton = getChildAt(index) as RadioButton
    if (isChecked) radioButton.isChecked = true
    return radioButton
}