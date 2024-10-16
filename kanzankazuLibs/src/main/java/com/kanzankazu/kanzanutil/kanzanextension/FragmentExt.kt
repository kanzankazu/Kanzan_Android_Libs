package com.kanzankazu.kanzanutil.kanzanextension

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

/**Add Fragment*/
fun FragmentTransaction.addFragment(fragment: Fragment, targetViewId: Int, tag: String?, isAddToBackStack: Boolean = false): Fragment {
    add(targetViewId, fragment, tag)
    if (isAddToBackStack) addToBackStack(null)
    commit()
    return fragment
}

fun FragmentManager.addFragment(fragment: Fragment, targetViewId: Int, tag: String?, isAddToBackStack: Boolean = false) =
    beginTransaction().addFragment(fragment, targetViewId, tag, isAddToBackStack)

fun FragmentActivity.addFragment(fragment: Fragment, targetViewId: Int, tag: String?, isAddToBackStack: Boolean = false) =
    supportFragmentManager.addFragment(fragment, targetViewId, tag, isAddToBackStack)

fun Fragment.addFragment(fragment: Fragment, targetViewId: Int, tags: String?, isAddToBackStack: Boolean = false) =
    childFragmentManager.addFragment(fragment, targetViewId, tags, isAddToBackStack)

fun FragmentContainerView.addFragment(fragmentManager: FragmentManager, fragment: Fragment, tag: String?, isAddToBackStack: Boolean = false) {
    fragmentManager.addFragment(fragment, this.id, tag, isAddToBackStack)
}

/**Replace Fragment*/
fun FragmentTransaction.replaceFragment(fragment: Fragment, targetViewId: Int, tag: String?, isAddToBackStack: Boolean = false) {
    replace(targetViewId, fragment, tag)
    if (isAddToBackStack) addToBackStack(null)
    commit()
}

fun FragmentManager.replaceFragment(fragment: Fragment, targetViewId: Int, tag: String?, isAddToBackStack: Boolean = false) {
    beginTransaction().replaceFragment(fragment, targetViewId, tag, isAddToBackStack)
}

fun FragmentActivity.replaceFragment(fragment: Fragment, targetViewId: Int, tag: String?, isAddToBackStack: Boolean = false) {
    supportFragmentManager.replaceFragment(fragment, targetViewId, tag, isAddToBackStack)
}

fun Fragment.replaceFragment(fragment: Fragment, targetViewId: Int, tags: String?, isAddToBackStack: Boolean = false) {
    childFragmentManager.replaceFragment(fragment, targetViewId, tags, isAddToBackStack)
}

fun FragmentActivity.removeFragment(fragment: Fragment?, tag: String? = null) {
    if (fragment != null) supportFragmentManager.beginTransaction().remove(fragment).commit()
    else supportFragmentManager.findFragmentByTag(tag)?.let { supportFragmentManager.beginTransaction().remove(it).commit() }
}

fun Fragment.removeFragment(fragment: Fragment?, tag: String? = null) {
    if (fragment != null) childFragmentManager.beginTransaction().remove(fragment).commit()
    else childFragmentManager.findFragmentByTag(tag)?.let { childFragmentManager.beginTransaction().remove(it).commit() }
}

fun FragmentManager.removeFragment(fragment: Fragment?, tag: String? = null) {
    if (fragment != null) beginTransaction().remove(fragment).commit()
    else findFragmentByTag(tag)?.let { beginTransaction().remove(it).commit() }
}

/**Show hide Fragment*/
fun FragmentTransaction.showHideFragment(showFragment: Fragment, hideFragment: Fragment): Fragment {
    if (showFragment != hideFragment) {
        show(showFragment)
        hide(hideFragment)
        commit()
    }
    return showFragment
}

fun FragmentManager.showHideFragment(showFragment: Fragment, hideFragment: Fragment) =
    beginTransaction().showHideFragment(showFragment, hideFragment)

fun FragmentActivity.showHideFragment(showFragment: Fragment, hideFragment: Fragment) =
    supportFragmentManager.showHideFragment(showFragment, hideFragment)

fun Fragment.showHideFragment(showFragment: Fragment, hideFragment: Fragment) =
    childFragmentManager.showHideFragment(showFragment, hideFragment)

/**Add Show Hide Fragment*/
fun FragmentTransaction.addShowHideFragments(fragments: ArrayList<Fragment>, targetView: Int): Fragment? {
    var currentFragment: Fragment? = null
    fragments.filterIndexed { index, fragment ->
        add(targetView, fragment)
        if (index == 0) {
            currentFragment = fragment
            show(fragment)
        } else {
            hide(fragment)
        }
        false
    }
    commit()
    return currentFragment
}

fun FragmentManager.addShowHideFragments(fragments: ArrayList<Fragment>, targetView: Int) =
    beginTransaction().addShowHideFragments(fragments, targetView)

fun FragmentActivity.addShowHideFragments(fragments: ArrayList<Fragment>, targetView: Int) =
    supportFragmentManager.addShowHideFragments(fragments, targetView)

fun Fragment.addShowHideFragments(fragments: ArrayList<Fragment>, targetView: Int) =
    childFragmentManager.addShowHideFragments(fragments, targetView)

fun <T : Fragment> FragmentManager.fragmentCommit(@IdRes idFragmentContainerView: Int, fragment: Class<out T>, bundle: Bundle? = null) {
    beginTransaction()
        .replace(idFragmentContainerView, fragment, bundle)
        .commit()
}

fun FragmentManager.fragmentCommit(@IdRes idFragmentContainerView: Int, fragment: Fragment) {
    beginTransaction()
        .replace(idFragmentContainerView, fragment)
        .commit()
}

