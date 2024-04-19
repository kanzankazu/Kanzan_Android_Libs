package com.kanzankazu.kanzanutil.kanzanextension.type

import kotlin.math.roundToInt

/**
 * @return : 2.1 -> 2.5 , 2,2 -> 2.5 , 2.3 -> 2.5, 2.4 -> 2.5
 * */
fun Float.roundToUp(): Float {
    var newValue = this
    if (newValue.toDouble() != 0.00 && newValue.toDouble() != 5.00) { //Validate if the input either 0.00 or 5.00 the output will still the same
        newValue = ((newValue - 0.5).roundToInt() + 0.5).toFloat()
    }
    return newValue
}
