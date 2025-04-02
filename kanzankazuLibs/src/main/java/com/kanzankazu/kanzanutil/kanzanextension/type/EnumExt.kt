package com.kanzankazu.kanzanutil.kanzanextension.type

/**
 * Safely retrieves an enum constant of the specified type by name, transforming the input string into a valid enum constant name.
 * If the input name is invalid or does not match any enum constant, the default value is returned.
 *
 * @param T The type of the enum.
 * @param type The input string representing the name of an enum constant. Non-alphanumeric characters are replaced with underscores ("_"), and the string
 *  is converted to uppercase.
 * @param default The default enum constant to return if the input string does not match any valid enum constant of the specified type.
 * @return The enum constant of type `T` matching the transformed input string, or the provided default value if no match is found.
 *
 * Example:
 * ```kotlin
 * enum class Colors { RED, GREEN, BLUE }
 *
 * val color1 = safeValueOf("Red", Colors.GREEN) // Returns Colors.RED
 * val color2 = safeValueOf("invalid_color", Colors.GREEN) // Returns Colors.GREEN (default)
 * ```
 */
inline fun <reified T : Enum<T>> safeValueOf(type: String, default: T): T {
    return try {
        val name = type.map { if (it.isLetterOrDigit()) it.uppercase() else "_" }
            .joinToString("")
        java.lang.Enum.valueOf(T::class.java, name)
    } catch (e: IllegalArgumentException) {
        default
    }
}