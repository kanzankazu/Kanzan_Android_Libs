package com.kanzankazu.kanzanutil.kanzanextension.type

import android.os.Bundle
import androidx.core.os.bundleOf

fun MutableMap<String, Boolean>.add(key: String, value: Boolean) = apply { put(key, value) }

fun <V> MutableMap<String, V>.add(key: String, value: V) = apply { put(key, value) }

fun <K, V> MutableMap<K, V>.add(key: K, value: V) = apply { put(key, value) }

fun Map<String, Any>.toBundle(): Bundle {
    return try {
        bundleOf(*toList().toTypedArray())
    } catch (e: Exception) {
        bundleOf()
    }
}

inline fun <reified T> Map<String, Any>.extract(key: String): T? {
    val value = this[key] ?: return null
    if (value !is T) return null

    return value
}
