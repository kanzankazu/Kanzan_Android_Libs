package com.kanzankazu.kanzanutil.kanzanextension.type

/**
 * Returns one of two strings depending on the value of the Boolean on which the function is called.
 * If the Boolean is true, it returns the `stringTrue` value.
 * If the Boolean is false, it returns the `stringFalse` value.
 *
 * @param stringTrue The string to return if the Boolean is true.
 * @param stringFalse The string to return if the Boolean is false. Defaults to an empty string.
 * @return A string value based on the Boolean's value.
 *
 * Example:
 * ```kotlin
 * val isActive = true
 * val status = isActive.ifString("Active", "Inactive") // Returns "Active"
 *
 * val isComplete = false
 * val result = isComplete.ifString("Complete", "Incomplete") // Returns "Incomplete"
 *
 * val isEmpty = false
 * val output = isEmpty.ifString("Yes") // Returns ""
 * ```
 */
@Suppress("SimplifyBooleanWithConstants")
fun Boolean.ifString(stringTrue: String, stringFalse: String = ""): String {
    return if (this == true) stringTrue else stringFalse
}

/**
 * Returns an integer value based on the boolean value of the receiver.
 * If the boolean value is `true`, it returns the specified `intTrue` value.
 * If the boolean value is `false`, it returns the specified `intFalse` value (default is 0).
 *
 * @param intTrue The integer value to return if the boolean is `true`.
 * @param intFalse The integer value to return if the boolean is `false`. Defaults to 0.
 * @return The integer value corresponding to the boolean condition:
 * either `intTrue` if the boolean is `true`, or `intFalse` if the boolean is `false`.
 *
 * Example:
 * ```kotlin
 * val isTrue = true
 * val result1 = isTrue.ifInt(10) // Returns 10
 *
 * val isFalse = false
 * val result2 = isFalse.ifInt(10, 5) // Returns 5
 *
 * val result3 = isFalse.ifInt(10) // Returns 0 (default value for intFalse)
 * ```
 */
@Suppress("SimplifyBooleanWithConstants")
fun Boolean.ifInt(intTrue: Int, intFalse: Int = 0): Int {
    return if (this == true) intTrue else intFalse
}

/**
 * Returns one of two specified Long values based on the Boolean receiver.
 * If the receiver is true, it returns `intTrue`. Otherwise, it returns `intFalse`.
 *
 * @param intTrue The Long value to return if the Boolean receiver is true.
 * @param intFalse The Long value to return if the Boolean receiver is false. Defaults to 0.
 * @return The value of `intTrue` if the Boolean receiver is true, otherwise the value of `intFalse`.
 *
 * Example:
 * ```kotlin
 * val isActive = true
 * val result = isActive.ifLong(100L, 50L) // result will be 100L
 *
 * val isEnabled = false
 * val outcome = isEnabled.ifLong(200L, 75L) // outcome will be 75L
 * ```
 */
@Suppress("SimplifyBooleanWithConstants")
fun Boolean.ifLong(intTrue: Long, intFalse: Long = 0): Long {
    return if (this == true) intTrue else intFalse
}

/**
 * Executes one of two provided lambda functions based on the value of the Boolean receiver.
 * If the receiver is `true`, it executes the `listenerTrue` lambda.
 * Otherwise, it executes the `listenerFalse` lambda, which defaults to an empty function.
 *
 * @param listenerFalse The lambda function to execute if the receiver is `false`. Defaults to an empty function.
 * @param listenerTrue The lambda function to execute if the receiver is `true`.
 *
 * Example:
 * ```kotlin
 * val condition = true
 * condition.ifListener(
 *     listenerFalse = { println("Condition is false") },
 *     listenerTrue = { println("Condition is true") }
 * )
 * // Output: "Condition is true"
 *
 * val anotherCondition = false
 * anotherCondition.ifListener(
 *     listenerFalse = { println("Condition is false") },
 *     listenerTrue = { println("Condition is true") }
 * )
 * // Output: "Condition is false"
 * ```
 */
@Suppress("SimplifyBooleanWithConstants")
fun Boolean.ifListener(listenerFalse: () -> Unit = {}, listenerTrue: () -> Unit) {
    if (this == true) listenerTrue() else listenerFalse()
}

/**
 * Executes the provided lambda function and returns its result if the boolean condition is `true`.
 * If the boolean condition is `false`, the method returns `null`.
 *
 * @param param A lambda function to execute if the condition is `true`.
 * @return The result of the lambda function if the boolean condition is `true`, otherwise `null`.
 *
 * Example:
 * ```kotlin
 * val condition = true
 * val result = condition ifn { "Executed" } // result = "Executed"
 *
 * val anotherCondition = false
 * val anotherResult = anotherCondition ifn { "Not Executed" } // anotherResult = null
 * ```
 */
infix fun <T> Boolean.ifn(param: () -> T): T? {
    return if (this) param() else null
}

/**
 * Converts the string into a boolean value if possible, otherwise returns `false`.
 * If the string does not represent a valid boolean (`"true"` or `"false"`), the default value provided is ignored,
 * and `false` is returned.
 *
 * @param defaultValue An optional boolean value that is ignored in the current implementation. Defaults to `false`.
 * @return A boolean value that represents the string, or `false` if the string cannot be converted to a boolean.
 *
 * Example:
 * ```kotlin
 * val result1 = "true".toBooleanOrFalse()     // true
 * val result2 = "false".toBooleanOrFalse()    // false
 * val result3 = "invalid".toBooleanOrFalse()  // false
 * ```
 */
fun String.toBooleanOrFalse(defaultValue: Boolean = false): Boolean = toBooleanStrictOrNull() ?: false
