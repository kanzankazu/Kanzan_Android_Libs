@file:Suppress("EXTENSION_SHADOWED_BY_MEMBER")

package com.kanzankazu.kanzanutil.kanzanextension.type

fun <T> join(vararg list: ArrayList<T>): ArrayList<T> {
    val arrayListOf = arrayListOf<T>()
    list.forEach { it -> it.forEach { arrayListOf.add(it) } }
    return arrayListOf
}

fun <T> joinNullAble(vararg list: ArrayList<T>?): ArrayList<T> {
    val arrayListOf = arrayListOf<T>()
    list.forEach { tArrayList -> tArrayList?.forEach { arrayListOf.add(it) } }
    return arrayListOf
}

fun <T> Array<out T>.toArrayList(): ArrayList<T> {
    val arrayListOf = arrayListOf<T>()
    this.forEach { arrayListOf.add(it) }
    return arrayListOf
}

fun <T> List<T>.toArrayList(): ArrayList<T> {
    return if (this.isNotEmpty()) toMutableList() as ArrayList<T> else arrayListOf()
}

fun List<String>.toArray(): Array<String> = toTypedArray()

fun ArrayList<String>.toArray(): Array<String> = toTypedArray()

inline fun <reified T> List<T>.toArray(): Array<T> = toTypedArray()

inline fun <reified T> ArrayList<T>.toArray(): Array<T> = toTypedArray()
