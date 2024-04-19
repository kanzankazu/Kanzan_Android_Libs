package com.kanzankazu.kanzanutil.kanzanextension.type

@Suppress("SimplifyBooleanWithConstants")
fun Boolean.ifString(stringTrue: String, stringFalse: String = ""): String {
    return if (this == true) stringTrue else stringFalse
}

@Suppress("SimplifyBooleanWithConstants")
fun Boolean.ifInt(intTrue: Int, intFalse: Int = 0): Int {
    return if (this == true) intTrue else intFalse
}

@Suppress("SimplifyBooleanWithConstants")
fun Boolean.ifLong(intTrue: Long, intFalse: Long = 0): Long {
    return if (this == true) intTrue else intFalse
}

@Suppress("SimplifyBooleanWithConstants")
fun Boolean.ifListener(listenerFalse: () -> Unit = {}, listenerTrue: () -> Unit) {
    if (this == true) listenerTrue() else listenerFalse()
}

infix fun <T> Boolean.ifn(param: () -> T): T? {
    return if (this) param() else null
}

fun String.toBooleanOrFalse(defaultValue: Boolean = false): Boolean = toBooleanStrictOrNull() ?: false
