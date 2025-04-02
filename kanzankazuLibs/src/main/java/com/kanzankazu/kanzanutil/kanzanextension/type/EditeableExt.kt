package com.kanzankazu.kanzanutil.kanzanextension.type

import android.text.Editable

/**
 * Converts an Editable object to an integer. If the Editable is null or cannot be converted to an integer,
 * the method returns -1 by default.
 *
 * @return The integer value obtained from the Editable object, or -1 if the Editable is null or invalid.
 *
 * Example:
 * ```kotlin
 * val editable: Editable? = Editable.Factory.getInstance().newEditable("123")
 * val result = editable.toInt() // Result: 123
 *
 * val invalidEditable: Editable? = Editable.Factory.getInstance().newEditable("abc")
 * val invalidResult = invalidEditable.toInt() // Result: -1
 *
 * val nullEditable: Editable? = null
 * val nullResult = nullEditable.toInt() // Result: -1
 * ```
 */
fun Editable?.toInt(): Int {
    this?.let {
        return it.toString().toIntOrDefault()
    } ?: kotlin.run {
        return -1
    }
}