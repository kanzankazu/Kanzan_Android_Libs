package com.kanzankazu.kanzanutil.kanzanextension.type

import android.os.Bundle
import androidx.core.os.bundleOf

/**
 * Adds an entry to the MutableMap with the specified key and value.
 * If the key already exists, its value is updated with the provided value.
 * The function returns the map instance to allow method chaining.
 *
 * @param key The key to associate with the value in the map. Should be a non-null String.
 * @param value The Boolean value to store for the specified key.
 * @return The original MutableMap instance after adding or updating the entry.
 *
 * Example:
 * ```kotlin
 * val map = mutableMapOf<String, Boolean>()
 * map.add("isCompleted", true)
 * println(map) // Output: {isCompleted=true}
 * ```
 */
fun MutableMap<String, Boolean>.add(key: String, value: Boolean) = apply { put(key, value) }

/**
 * Adds a key-value pair to the mutable map and returns the updated map.
 * This method performs an `apply` operation, modifying the map in place.
 *
 * @param key The key to be added to the map. Must be of type `String`.
 * @param value The value to be associated with the specified key. Can be of any type `V`.
 * @return The updated map after adding the specified key-value pair.
 *
 * Example:
 * ```kotlin
 * val map = mutableMapOf<String, Int>()
 * map.add("key1", 10)
 * println(map) // Output: {key1=10}
 *
 * map.add("key2", 20)
 * println(map) // Output: {key1=10, key2=20}
 * ```
 */
fun <V> MutableMap<String, V>.add(key: String, value: V) = apply { put(key, value) }

/**
 * Adds a key-value pair to the MutableMap and returns the map itself, enabling method chaining.
 *
 * @param key The key to be added to the map. Should be of type `K` specified by the map.
 * @param value The value to be associated with the key. Should be of type `V` specified by the map.
 * @return The same MutableMap with the new key-value pair added, allowing further chained operations.
 *
 * Example:
 * ```kotlin
 * val mutableMap = mutableMapOf<String, Int>()
 * mutableMap.add("one", 1).add("two", 2)
 * println(mutableMap) // Output: {one=1, two=2}
 * ```
 */
fun <K, V> MutableMap<K, V>.add(key: K, value: V) = apply { put(key, value) }

/**
 * Converts a Map with String keys and Any values into a Bundle.
 * If the conversion encounters an exception, it returns an empty Bundle.
 *
 * @return A Bundle containing the key-value pairs from the map, or an empty Bundle if an exception occurs.
 *
 * Example:
 * ```kotlin
 * val map = mapOf("key1" to "value1", "key2" to 123)
 * val bundle = map.toBundle()
 * println(bundle.getString("key1")) // Output: value1
 * println(bundle.getInt("key2")) // Output: 123
 * ```
 */
fun Map<String, Any>.toBundle(): Bundle {
    return try {
        bundleOf(*toList().toTypedArray())
    } catch (e: Exception) {
        e.debugMessageError("toBundle")
        bundleOf()
    }
}

/**
 * Extracts a value of the specified type [T] from a `Map<String, Any>` using the provided key.
 * If the key does not exist in the map, or if the value cannot be cast to the desired type [T],
 * the function returns `null`.
 *
 * @param T The type of value to extract from the map.
 * @param key The key for which the value should be extracted from the map.
 * @return The value associated with the provided key cast to type [T], or `null` if the key
 *         does not exist or the value cannot be cast to type [T].
 *
 * Example:
 * ```kotlin
 * val data: Map<String, Any> = mapOf("age" to 25, "name" to "John")
 * val age: Int? = data.extract("age") // Result: 25
 * val name: String? = data.extract("name") // Result: "John"
 * val height: Double? = data.extract("height") // Result: null (key does not exist)
 * val invalidCast: Int? = data.extract("name") // Result: null (invalid type cast)
 * ```
 */
inline fun <reified T> Map<String, Any>.extract(key: String): T? {
    val value = this[key] ?: return null
    if (value !is T) return null

    return value
}
