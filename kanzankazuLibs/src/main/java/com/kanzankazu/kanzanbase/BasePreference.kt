@file:Suppress("MemberVisibilityCanBePrivate")

package com.kanzankazu.kanzanbase

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.text.TextUtils
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.google.gson.Gson
import com.kanzankazu.BuildConfig
import com.kanzankazu.kanzanutil.kanzanextension.type.debugMessageError
import com.kanzankazu.kanzanutil.kanzanextension.type.toArrayList

abstract class BasePreference(private val context: Context) {

    private val prefsName = "${BuildConfig.LIBRARY_PACKAGE_NAME}.secure_preferences"

    private val sharedPreferences = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        createEncryptedSharedPreferences(context)
    } else {
        context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
    }

    // Helper to create encrypted SharedPreferences
    private fun createEncryptedSharedPreferences(context: Context) = try {
        val masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        EncryptedSharedPreferences.create(
            prefsName,
            masterKey,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    } catch (e: Exception) {
        e.debugMessageError("BasePreference - createEncryptedSharedPreferences")
        context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
    }

    fun getAllSharedPreferences(): ArrayList<Pair<String, String>> {
        return getAllSharedPreferencesMap().map { Pair(it.key, it.value) }.toArrayList()
    }

    fun getAllSharedPreferencesMap(): Map<String, String> {
        val allEntries = sharedPreferences.all // Gunakan instance sharedPreferences yang sudah dienkripsi
        val result: MutableMap<String, String> = HashMap()

        for ((key, value) in allEntries) {
            // Decode nilai yang dienkripsi
            val decodedValue = when (value) {
                is String -> value.decodedString()
                else -> value.toString()
            }
            result[key] = decodedValue
        }

        return result
    }

    /**
     * Utility method for safely editing shared preferences
     */
    private inline fun editPreferences(action: (SharedPreferences.Editor) -> Unit): Boolean {
        return sharedPreferences.edit().apply(action).commit()
    }

    // General Put and Get Methods to Avoid Code Duplication
    fun putString(key: String, value: String) = editPreferences { it.putString(key, value.encodeString()) }
    fun getString(key: String, defaultValue: String = "") =
        sharedPreferences.getString(key, defaultValue)?.decodedString() ?: defaultValue

    fun putInt(key: String, value: Int) = putString(key, value.toString())
    fun getInt(key: String, defaultValue: Int = 0): Int = getString(key).toIntOrNull() ?: defaultValue

    fun putLong(key: String, value: Long) = putString(key, value.toString())
    fun getLong(key: String, defaultValue: Long = 0L): Long = getString(key).toLongOrNull() ?: defaultValue

    fun putBoolean(key: String, value: Boolean) = putString(key, value.toString())
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean = getString(key).toBooleanStrictOrNull() ?: defaultValue

    fun putFloat(key: String, value: Float) = putString(key, value.toString())
    fun getFloat(key: String, defaultValue: Float = 0F): Float = getString(key).toFloatOrNull() ?: defaultValue

    fun removeKey(key: String): Boolean = editPreferences { it.remove(key) }
    fun containsKey(key: String): Boolean = sharedPreferences.contains(key)

    /**
     * Manage List of Strings
     */
    fun putStringList(key: String, values: List<String>) {
        val serializedList = TextUtils.join("‚‗‚", values.map { it.encodeString() })
        putString(key, serializedList)
    }

    fun getStringList(key: String): List<String> {
        val serializedList = getString(key)
        return if (serializedList.isEmpty()) emptyList()
        else TextUtils.split(serializedList, "‚‗‚").map { it.decodedString() }
    }

    /**
     * Manage Objects
     */
    fun <T> putObject(key: String, obj: T) {
        val json = Gson().toJson(obj)
        putString(key, json)
    }

    inline fun <reified T> getObject(key: String): T? {
        val json = getString(key)
        return try {
            Gson().fromJson(json, T::class.java)
        } catch (e: Exception) {
            null // Fallback to null if deserialization fails
        }
    }

    fun <T> putObjectList(key: String, objList: List<T>) {
        val jsonList = objList.map { Gson().toJson(it) }
        putStringList(key, jsonList)
    }

    fun <T> getObjectList(key: String, clazz: Class<T>): List<T> {
        return getStringList(key).mapNotNull { json ->
            try {
                Gson().fromJson(json, clazz)
            } catch (e: Exception) {
                null
            }
        }
    }

    /**
     * Error-safe decoding and encoding functions
     */
    private fun String.encodeString(): String =
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                java.util.Base64.getEncoder().encodeToString(this.toByteArray())
            } else {
                android.util.Base64.encodeToString(this.toByteArray(), android.util.Base64.NO_WRAP)
            }
        } catch (e: Exception) {
            this // Fallback to original string if encoding fails
        }

    private fun String.decodedString(): String =
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String(java.util.Base64.getDecoder().decode(this))
            } else {
                String(android.util.Base64.decode(this, android.util.Base64.NO_WRAP))
            }
        } catch (e: Exception) {
            this // Fallback to original string if decoding fails
        }
}