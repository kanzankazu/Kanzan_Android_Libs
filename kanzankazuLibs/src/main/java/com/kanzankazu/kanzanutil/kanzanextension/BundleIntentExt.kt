@file:Suppress("unused")

package com.kanzankazu.kanzanutil.kanzanextension

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment

fun Bundle?.isContains(keyParam: String): Boolean = this?.let { return it.containsKey(keyParam) } ?: kotlin.run { return false }

fun Intent?.isContains(keyParam: String): Boolean = this?.let { return it.extras.isContains(keyParam) } ?: kotlin.run { return false }

fun Activity?.isContains(keyParam: String): Boolean = this?.let { return it.intent.isContains(keyParam) } ?: kotlin.run { return false }

fun Fragment?.isContains(keyParam: String): Boolean = this?.let {
    return when {
        it.arguments?.isContains(keyParam) == true -> true
        it.activity?.isContains(keyParam) == true -> true
        else -> false
    }
} ?: kotlin.run { return false }

fun Bundle?.isBundleNull(): Boolean = this == null

fun Intent?.isBundleNull(): Boolean = this?.let { return it.extras.isBundleNull() } ?: kotlin.run { return false }

fun Activity?.isBundleNull(): Boolean = this?.let { return it.intent.isBundleNull() } ?: kotlin.run { return false }

fun Fragment?.isBundleNull(): Boolean = this?.let {
    return when {
        it.arguments?.isBundleNull() == true -> true
        it.activity?.isBundleNull() == true -> true
        else -> false
    }
} ?: kotlin.run { return false }

fun Intent?.getBundle(): Bundle? = this?.extras ?: kotlin.run { null }

fun Activity?.getBundle(): Bundle? = this?.intent?.let { it.getBundle() } ?: kotlin.run { null }

fun Fragment?.getBundle(): Bundle? = this?.arguments?.let { arguments } ?: this?.activity?.let { it.getBundle() } ?: kotlin.run { null }

fun Bundle?.getBundleString(keyParam: String, defaultValue: String = ""): String {
    this?.let {
        return if (it.isContains(keyParam)) it.getString(keyParam, defaultValue)
        else defaultValue
    } ?: kotlin.run {
        return defaultValue
    }
}

fun Intent?.getBundleString(keyParam: String, defaultValue: String = ""): String {
    this?.let {
        return extras.getBundleString(keyParam, defaultValue)
    } ?: kotlin.run {
        return defaultValue
    }
}

fun Activity?.getBundleString(keyParam: String, defaultValue: String = ""): String {
    this?.let {
        return it.intent.getBundleString(keyParam, defaultValue)
    } ?: kotlin.run {
        return defaultValue
    }
}

fun Fragment?.getBundleString(keyParam: String, defaultValue: String = ""): String {
    this?.let {
        return when {
            it.arguments.getBundleString(keyParam, defaultValue).isNotEmpty() -> it.arguments.getBundleString(keyParam, defaultValue)
            it.activity.getBundleString(keyParam, defaultValue).isNotEmpty() -> it.activity.getBundleString(keyParam, defaultValue)
            else -> ""
        }
    } ?: kotlin.run {
        return defaultValue
    }
}

fun Bundle?.getBundleInt(keyParam: String, defaultValue: Int): Int {
    this?.let {
        return if (it.isContains(keyParam)) {
            val int = it.getInt(keyParam, defaultValue)
            int
        } else {
            defaultValue
        }
    } ?: kotlin.run {
        return defaultValue
    }
}

fun Intent?.getBundleInt(keyParam: String, defaultValue: Int): Int {
    this?.let {
        return extras.getBundleInt(keyParam, defaultValue)
    } ?: kotlin.run {
        return defaultValue
    }
}

fun Activity?.getBundleInt(keyParam: String, defaultValue: Int): Int {
    this?.let {
        return it.intent.getBundleInt(keyParam, defaultValue)
    } ?: kotlin.run {
        return defaultValue
    }
}

fun Fragment?.getBundleInt(keyParam: String, defaultValue: Int): Int {
    this?.let {
        return when {
            it.arguments.getBundleInt(keyParam, defaultValue) != defaultValue -> it.arguments.getBundleInt(keyParam, defaultValue)
            it.activity.getBundleInt(keyParam, defaultValue) != defaultValue -> it.activity.getBundleInt(keyParam, defaultValue)
            else -> defaultValue
        }
    } ?: kotlin.run {
        return defaultValue
    }
}

fun Bundle?.getBundleBoolean(keyParam: String, defaultValue: Boolean = false): Boolean {
    this?.let {
        return if (it.isContains(keyParam)) {
            it.getBoolean(keyParam, defaultValue)
        } else defaultValue
    } ?: kotlin.run {
        return defaultValue
    }
}

fun Intent?.getBundleBoolean(keyParam: String, defaultValue: Boolean = false): Boolean {
    this?.let {
        return extras.getBundleBoolean(keyParam, defaultValue)
    } ?: kotlin.run {
        return defaultValue
    }
}

fun Activity?.getBundleBoolean(keyParam: String, defaultValue: Boolean = false): Boolean {
    this?.let {
        return it.intent.getBundleBoolean(keyParam, defaultValue)
    } ?: kotlin.run {
        return defaultValue
    }
}

fun Fragment?.getBundleBoolean(keyParam: String, defaultValue: Boolean = false): Boolean {
    this?.let {
        return when {
            it.arguments.getBundleBoolean(keyParam, defaultValue) != defaultValue -> it.arguments.getBundleBoolean(keyParam, defaultValue)
            it.activity.getBundleBoolean(keyParam, defaultValue) != defaultValue -> it.activity.getBundleBoolean(keyParam, defaultValue)
            else -> defaultValue
        }
    } ?: kotlin.run {
        return defaultValue
    }
}

fun <T : Parcelable> Bundle?.getBundleParcelable(keyParam: String): T? {
    val targetClass: T?
    return if (this != null) {
        if (this.isContains(keyParam)) {
            targetClass = if (this.getParcelable<T>(keyParam) != null) this.getParcelable(keyParam) else null
            targetClass
        } else null
    } else null
}

fun <T : Parcelable> Intent?.getBundleParcelable(keyParam: String): T? {
    this?.let {
        return it.extras.getBundleParcelable(keyParam)
    } ?: kotlin.run {
        return null
    }
}

fun <T : Parcelable> Activity?.getBundleParcelable(keyParam: String): T? {
    this?.let {
        return it.intent.getBundleParcelable(keyParam)
    } ?: kotlin.run {
        return null
    }
}

fun <T : Parcelable> Fragment?.getBundleParcelable(keyParam: String): T? {
    this?.let {
        return it.arguments.getBundleParcelable(keyParam) ?: it.activity.getBundleParcelable(keyParam)
    } ?: kotlin.run {
        return null
    }
}

fun <T : Parcelable> Bundle?.getBundleParcelableArrayList(keyParam: String): ArrayList<T>? {
    val targetClass: ArrayList<T>?
    return if (this != null) {
        if (this.isContains(keyParam)) {
            targetClass = if (this.getParcelableArrayList<T>(keyParam) != null) this.getParcelableArrayList<T>(keyParam) else null
            targetClass
        } else null
    } else null
}

fun <T : Parcelable> Intent?.getBundleParcelableArrayList(keyParam: String): ArrayList<T>? {
    this?.let {
        return it.extras.getBundleParcelableArrayList(keyParam)
    } ?: kotlin.run {
        return null
    }
}

fun <T : Parcelable> Activity?.getBundleParcelableArrayList(keyParam: String): ArrayList<T>? {
    this?.let {
        return it.intent.getBundleParcelableArrayList(keyParam)
    } ?: kotlin.run {
        return null
    }
}

fun <T : Parcelable> Fragment?.getBundleParcelableArrayList(keyParam: String): ArrayList<T>? {
    this?.let {
        return it.arguments.getBundleParcelableArrayList(keyParam) ?: it.activity.getBundleParcelableArrayList(keyParam)
    } ?: kotlin.run {
        return null
    }
}

fun Intent.getIntentParam(): String {
    return if (Intent.ACTION_VIEW == this.action) {
        val uri: Uri? = this.data
        uri?.getQueryParameter("ID") ?: ""
    } else ""
}

fun <T : Parcelable> T?.makeBundle(key: String): Bundle? = if (this != null) Bundle().apply { putParcelable(key, this@makeBundle) } else null

fun Boolean?.makeBundle(key: String, defaultValue: Boolean = false): Bundle = Bundle().apply { putBoolean(key, this@makeBundle ?: defaultValue) }

fun String?.makeBundle(key: String, defaultValue: String = ""): Bundle = Bundle().apply { putString(key, this@makeBundle ?: defaultValue) }

fun Int?.makeBundle(key: String, defaultValue: Int = 0): Bundle = Bundle().apply { putInt(key, this@makeBundle ?: defaultValue) }

fun Double?.makeBundle(key: String, defaultValue: Double = 0.0): Bundle = Bundle().apply { putDouble(key, this@makeBundle ?: defaultValue) }

fun Float?.makeBundle(key: String, defaultValue: Float = 0F): Bundle = Bundle().apply { putFloat(key, this@makeBundle ?: defaultValue) }

fun Long?.makeBundle(key: String, defaultValue: Long = 0L): Bundle = Bundle().apply { putLong(key, this@makeBundle ?: defaultValue) }

fun Bundle?.addBundle(bundle: Bundle?): Bundle? {
    return when {
        this != null && bundle != null -> this.apply { putAll(bundle) }
        this != null && bundle == null -> this
        this == null && bundle != null -> bundle
        else -> null
    }
}