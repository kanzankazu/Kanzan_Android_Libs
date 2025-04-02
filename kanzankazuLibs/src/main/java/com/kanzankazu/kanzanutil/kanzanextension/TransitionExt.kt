package com.kanzankazu.kanzanutil.kanzanextension

import android.app.Activity
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.View
import androidx.annotation.TransitionRes
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.view.ViewCompat

fun View.pairViewCreate(keyTransition: String): Pair<View, String> {
    return Pair.create(this, keyTransition)
}

fun Activity.startTransition(@TransitionRes transitionRes: Int, vararg pairs: Pair<View, String>): Bundle? {
    enterTransition(transitionRes)
    return pairViewCreateJoinToBundle(*pairs)
}

fun Activity.endTransition(@TransitionRes transitionRes: Int, vararg pairs: Pair<View, String>) {
    exitTransition(transitionRes)
    pairs.forEach { it.first.setViewCreate(it.second) }
}

private fun Activity.pairViewCreateJoinToBundle(vararg pairs: Pair<View, String>): Bundle? {
    return ActivityOptionsCompat.makeSceneTransitionAnimation(this, *pairs).toBundle()
}

private fun Activity.enterTransition(@TransitionRes transitionRes: Int) {
    window.sharedElementEnterTransition = TransitionInflater.from(this).inflateTransition(transitionRes)
}

private fun Activity.exitTransition(@TransitionRes transitionRes: Int) {
    window.sharedElementExitTransition = TransitionInflater.from(this).inflateTransition(transitionRes)
}

private fun View.setViewCreate(keyTransition: String) {
    ViewCompat.setTransitionName(this, keyTransition)
}
