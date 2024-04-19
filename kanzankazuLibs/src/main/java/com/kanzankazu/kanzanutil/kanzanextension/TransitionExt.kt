package com.kanzankazu.kanzanutil.kanzanextension

import android.app.Activity
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.View
import androidx.annotation.TransitionRes
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment

fun Activity.makeSceneTransition(pair: Pair<View, String>): Bundle? {
    return ActivityOptionsCompat.makeSceneTransitionAnimation(this, pair).toBundle()
}

fun Activity.makeSceneTransition(view: View, transitionName: String): Bundle? {
    return ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, transitionName).toBundle()
}

fun Activity.setTransition(view: View, transitionName: String) {
    ViewCompat.setTransitionName(view, transitionName)
}

fun Fragment.makeSceneTransition(pair: Pair<View, String>): Bundle? {
    return ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(), pair).toBundle()
}

fun Fragment.makeSceneTransition(view: View, transitionName: String): Bundle? {
    return ActivityOptionsCompat.makeSceneTransitionAnimation(requireActivity(), view, transitionName).toBundle()
}

fun Fragment.setTransition(view: View, transitionName: String) {
    ViewCompat.setTransitionName(view, transitionName)
}

fun Activity.enterTransition(@TransitionRes transitionRes: Int) {
    window.sharedElementEnterTransition = TransitionInflater.from(this).inflateTransition(transitionRes)
}

fun Activity.exitTransition(@TransitionRes transitionRes: Int) {
    window.sharedElementExitTransition = TransitionInflater.from(this).inflateTransition(transitionRes)
}

fun Fragment.enterTransition(@TransitionRes transitionRes: Int) {
    requireActivity().window.sharedElementEnterTransition = TransitionInflater.from(requireActivity()).inflateTransition(transitionRes)
}

fun Fragment.exitTransition(@TransitionRes transitionRes: Int) {
    requireActivity().window.sharedElementExitTransition = TransitionInflater.from(requireActivity()).inflateTransition(transitionRes)
}

fun Activity.startTransition(transitionRes: Int, pair: Pair<View, String>) {
    enterTransition(transitionRes)
    makeSceneTransition(pair)
}

fun Activity.endTransition(transitionRes: Int, pair: Pair<View, String>) {
    exitTransition(transitionRes)
    makeSceneTransition(pair)
}
