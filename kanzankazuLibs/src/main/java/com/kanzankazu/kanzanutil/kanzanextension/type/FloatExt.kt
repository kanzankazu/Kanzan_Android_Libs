package com.kanzankazu.kanzanutil.kanzanextension.type

import kotlin.math.roundToInt

/**
 * Rounds a floating-point number to the nearest multiple of 0.5 in an upward direction,
 * unless the number is exactly 0.00 or 5.00, in which case it remains unchanged.
 *
 * @return The rounded-up Float value. If the input is exactly 0.00 or 5.00, it returns the input value unmodified.
 *
 * Example:
 * ```kotlin
 * val value1 = 0.75f.roundToUp() // Result: 1.0f
 * val value2 = 2.3f.roundToUp()  // Result: 2.5f
 * val value3 = 5.0f.roundToUp()  // Result: 5.0f (unchanged)
 * val value4 = 0.0f.roundToUp()  // Result: 0.0f (unchanged)
 * ```
 */
fun Float.roundToUp(): Float {
    var newValue = this
    if (newValue.toDouble() != 0.00 && newValue.toDouble() != 5.00) { //Validate if the input either 0.00 or 5.00 the output will still the same
        newValue = ((newValue - 0.5).roundToInt() + 0.5).toFloat()
    }
    return newValue
}
