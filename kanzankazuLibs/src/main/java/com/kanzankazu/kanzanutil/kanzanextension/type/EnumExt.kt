package com.kanzankazu.kanzanutil.kanzanextension.type

inline fun <reified T : Enum<T>> safeValueOf(type: String, default: T): T {
    return try {
        val name = type.map { if (it.isLetterOrDigit()) it.uppercase() else "_" }
            .joinToString("")
        java.lang.Enum.valueOf(T::class.java, name)
    } catch (e: IllegalArgumentException) {
        default
    }
}
