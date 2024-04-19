package com.kanzankazu.kanzanutil.kanzanextension

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment

/**
 * @param mainHostFragmentId this id at fragment in activity xml, example=R.id.searchHostFragment (get from xml activity)
 * */
fun FragmentActivity.getFragmentById(@IdRes mainHostFragmentId: Int): Fragment? {
    return kotlin.runCatching {
        this.supportFragmentManager.findFragmentById(mainHostFragmentId)
    }.getOrNull()
}

inline fun <reified T> Fragment.isFragmentTarget(): Boolean {
    return kotlin.runCatching {
        childFragmentManager.fragments[0] is T
    }.getOrElse { false }
}

inline fun <reified T> Fragment.asFragmentTarget(): T? {
    return kotlin.runCatching {
        childFragmentManager.fragments[0] as T
    }.getOrNull()
}

/**
 * @param navFragmentStartDestinationId this id at startDestination in nav xml, example=R.id.fragmentSearch (get from nav startdestination fragment)
 * @param mainHostOfFragmentId this id at fragment in activity xml, example=R.id.searchHostFragment (get from xml activity)
 * @param actionDestId this id at action fragment in nav xml, example=R.id.action_search_to_search_result (get from nav startdestination fragment)
 * @param bundle
 * */
fun AppCompatActivity.redirectFragment(
    @IdRes navFragmentStartDestinationId: Int,
    @IdRes mainHostOfFragmentId: Int,
    @IdRes actionDestId: Int,
    bundle: Bundle? = null,
    isInclusive: Boolean = true,
    isFragmentContainerView: Boolean = true,
) {
    val navOption: NavOptions? =
        if (isInclusive) {
            NavOptions.Builder()
                .setPopUpTo(navFragmentStartDestinationId, true)
                .build()
        } else null

    if (isFragmentContainerView) {
        val navHostFragment = supportFragmentManager.findFragmentById(mainHostOfFragmentId) as NavHostFragment
        navHostFragment.navController.navigate(
            actionDestId,
            bundle,
            navOption
        )
    } else {
        findNavController(mainHostOfFragmentId).navigate(
            actionDestId,
            bundle,
            navOption
        )
    }
}

fun Fragment.changeFragment(@IdRes actionDestId: Int, bundle: Bundle? = null) {
    NavHostFragment.findNavController(this@changeFragment).navigate(actionDestId, bundle)
    NavHostFragment.findNavController(this@changeFragment).run {

    }
}
