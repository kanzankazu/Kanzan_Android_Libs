@file:Suppress("DEPRECATION")

package com.kanzankazu.kanzanutil.kanzanextension

import android.app.Activity
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.kanzankazu.R
import com.kanzankazu.kanzanutil.kanzanextension.type.debugMessageError

fun <T> DataSnapshot.toObject(targetClass: Class<T>) =
    try {
        getValue(targetClass)
    } catch (e: Exception) {
        e.message.toString().debugMessageError()
        null
    }

fun <T> DataSnapshot.toObjectList(targetClass: Class<T>) =
    try {
        val datas = arrayListOf<T>()
        for (snapshot in children) {
            val data = snapshot.getValue(targetClass)
            data?.let { datas.add(it) }
        }
        datas
    } catch (e: Exception) {
        e.message.toString().debugMessageError()
        arrayListOf()
    }

/**
 * example isMaintenance = mFirebaseRemoteConfig.getBoolean(Const.FirebaseRemoteConfig.isMaintenance)
 * */
fun Activity.remoteConfigFetch(
    onFailed: (message: String) -> Unit = {},
    onSuccess: (firebaseRemoteConfig: FirebaseRemoteConfig) -> Unit,
) {
    try {
        val mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = FirebaseRemoteConfigSettings.Builder()
            //.setDeveloperModeEnabled(isDebug())
            .build()
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings)
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_default)
        mFirebaseRemoteConfig.fetch(3500).addOnCompleteListener(this) { task: Task<Void?> ->
            if (task.isSuccessful) {
                try {
                    mFirebaseRemoteConfig.fetchAndActivate()
                    onSuccess.invoke(mFirebaseRemoteConfig)
                } catch (e: NumberFormatException) {
                    onFailed.invoke(e.message.toString())
                }
            } else {
                onFailed.invoke(task.exception?.message.toString())
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
        val toString = e.message.toString()
        toString.debugMessageError()
        onFailed.invoke(toString)
    }
}

/**
 * example isMaintenance = mFirebaseRemoteConfig.getBoolean(Const.FirebaseRemoteConfig.isMaintenance)
 * */
fun Fragment.remoteConfigFetch(
    onFailed: (message: String) -> Unit = {},
    onSuccess: (firebaseRemoteConfig: FirebaseRemoteConfig) -> Unit,
) {
    try {
        requireActivity().remoteConfigFetch(onFailed, onSuccess)
    } catch (e: IllegalArgumentException) {
        onFailed("not currently associated with an activity or if associated only with a context")
    }
}
