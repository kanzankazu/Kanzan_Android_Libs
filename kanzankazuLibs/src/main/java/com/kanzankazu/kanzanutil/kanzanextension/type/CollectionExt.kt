@file:Suppress("EXTENSION_SHADOWED_BY_MEMBER")

package com.kanzankazu.kanzanutil.kanzanextension.type

/**
 * Combines multiple ArrayLists of the same type into a single ArrayList, preserving the order of elements.
 *
 * @param list Vararg parameter that accepts multiple ArrayLists of the same type.
 * @return A new ArrayList containing all elements from the provided ArrayLists in the order they were passed.
 *
 * Example:
 * ```kotlin
 * val list1 = arrayListOf(1, 2, 3)
 * val list2 = arrayListOf(4, 5, 6)
 * val result = join(list1, list2) // Result: [1, 2, 3, 4, 5, 6]
 * ```
 */
fun <T> join(vararg list: ArrayList<T>): ArrayList<T> {
    val arrayListOf = arrayListOf<T>()
    list.forEach { it -> it.forEach { arrayListOf.add(it) } }
    return arrayListOf
}

/**
 * Merges multiple nullable `ArrayList` objects into a single non-null `ArrayList`.
 * If an input `ArrayList` is null, it is skipped. All non-null lists are merged sequentially.
 *
 * @param list A variable number of nullable `ArrayList` objects of type `T`.
 * @return A single `ArrayList` containing all elements from the non-null input lists. If all input lists are null or empty, an empty `ArrayList` is
 *  returned.
 *
 * Example:
 * ```kotlin
 * val list1: ArrayList<Int>? = arrayListOf(1, 2, 3)
 * val list2: ArrayList<Int>? = null
 * val list3: ArrayList<Int>? = arrayListOf(4, 5)
 *
 * val result = joinNullAble(list1, list2, list3)
 * // result: [1, 2, 3, 4, 5]
 * ```
 */
fun <T> joinNullAble(vararg list: ArrayList<T>?): ArrayList<T> {
    val arrayListOf = arrayListOf<T>()
    list.forEach { tArrayList -> tArrayList?.forEach { arrayListOf.add(it) } }
    return arrayListOf
}

/**
 * Converts an Array of elements into an ArrayList containing the same elements.
 *
 * @return An ArrayList containing the elements of the input Array in the same order.
 *
 * Example:
 * ```kotlin
 * val array = arrayOf(1, 2, 3)
 * val arrayList = array.toArrayList() // Result: [1, 2, 3]
 * ```
 */
fun <T> Array<out T>.toArrayList(): ArrayList<T> {
    val arrayListOf = arrayListOf<T>()
    this.forEach { arrayListOf.add(it) }
    return arrayListOf
}

/**
 * Converts a `List` to an `ArrayList`. If the list is not empty,
 * it creates a mutable copy of the original list and casts it to an `ArrayList`.
 * Otherwise, it returns an empty `ArrayList`.
 *
 * @return An `ArrayList` containing the elements of the original list,
 * or an empty `ArrayList` if the original list is empty.
 *
 * Example:
 * ```kotlin
 * val list = listOf(1, 2, 3)
 * val arrayList = list.toArrayList() // Result: ArrayList<Int>([1,2,3])
 *
 * val emptyList = listOf<Int>()
 * val emptyArrayList = emptyList.toArrayList() // Result: ArrayList<Int>([])
 * ```
 */
fun <T> List<T>.toArrayList(): ArrayList<T> {
    return if (this.isNotEmpty()) toMutableList() as ArrayList<T> else arrayListOf()
}

/**
 * Converts the current list of strings to an array of strings.
 *
 * @return An array containing all elements of the list in the same order.
 *
 * Example:
 * ```kotlin
 * val stringList = listOf("A", "B", "C")
 * val stringArray = stringList.toArray() // ["A", "B", "C"]
 * ```
 */
fun List<String>.toArray(): Array<String> = toTypedArray()

/**
 * Converts an ArrayList of Strings to an Array of Strings.
 *
 * @return An Array of Strings containing all elements from the ArrayList in the same order.
 *
 * Example:
 * ```kotlin
 * val stringList = arrayListOf("A", "B", "C")
 * val stringArray = stringList.toArray()  // ["A", "B", "C"]
 * ```
 */
fun ArrayList<String>.toArray(): Array<String> = toTypedArray()

/**
 * Converts the list to an array of the specified generic type.
 * This method uses `toTypedArray` to ensure type safety while transforming a list into an array.
 *
 * @return An array containing all elements of the list in the same order, with the generic type matching the list's type.
 *
 * Example:
 * ```kotlin
 * val list = listOf(1, 2, 3, 4)
 * val array: Array<Int> = list.toArray()
 * // array content: [1, 2, 3, 4]
 * ```
 */
inline fun <reified T> List<T>.toArray(): Array<T> = toTypedArray()

/**
 * Converts an `ArrayList` to an array of the specified type.
 *
 * This method leverages Kotlin's `toTypedArray` function to transform an `ArrayList` into a strongly-typed array.
 * The type of the resulting array is determined by the type parameter `T`.
 *
 * @return An array containing all elements of the `ArrayList` in the same order.
 *
 * Example:
 * ```kotlin
 * val arrayList = arrayListOf(1, 2, 3, 4)
 * val array: Array<Int> = arrayList.toArray()
 * println(array.joinToString(", ")) // Output: 1, 2, 3, 4
 * ```
 */
inline fun <reified T> ArrayList<T>.toArray(): Array<T> = toTypedArray()
