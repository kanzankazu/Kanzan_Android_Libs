package com.kanzankazu.kanzanutil.kanzanextension

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

/**Mencari listener yg di gunakan Activity parent dari fragment child*/
inline fun <reified T> Fragment.findParentListener(): T? {
    return activity as? T
}

/**Mencari listener yg di gunakan Fragment child dari Activity*/
inline fun <reified T> FragmentActivity.findChildListener(tag: String): T? {
    return supportFragmentManager.findFragmentByTag(tag) as? T
}

/**Mencari listener yg di gunakan Fragment child dari Fragment parent*/
inline fun <reified T> Fragment.findChildListener(tag: String): T? {
    return childFragmentManager.findFragmentByTag(tag) as? T
}

fun FragmentActivity.getLifeCycle() = lifecycle

fun Fragment.getLifeCycle() = viewLifecycleOwner.lifecycle

fun FragmentActivity.getFragmentManagers() = supportFragmentManager

fun Fragment.getFragmentManagers() = childFragmentManager
