package com.kanzankazu.kanzanutil.kanzanextension.view

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.kanzankazu.kanzanutil.kanzanextension.addShowHideFragments

fun BottomNavigationView.listenerWithViewPager(viewPager: ViewPager, arrayOfMenuId: ArrayList<Int>) {
    setOnItemSelectedListener {
        arrayOfMenuId.forEachIndexed { index, i ->
            if (it.itemId == i) viewPager.setCurrentItem(index, true)
        }
        return@setOnItemSelectedListener true
    }
}

fun BottomNavigationView.listenerWithViewPager(viewPager2: ViewPager2, arrayOfMenuId: ArrayList<Int>) {
    setOnItemSelectedListener {
        arrayOfMenuId.forEachIndexed { index, i ->
            if (it.itemId == i) viewPager2.setCurrentItem(index, true)
        }
        return@setOnItemSelectedListener true
    }
}

fun BottomNavigationView.setupFragments(
    fragmentManager: FragmentManager,
    targetView: Int,
    fragments: ArrayList<Fragment>,
    listener: NavigationBarView.OnItemSelectedListener?,
): Fragment? {
    val fragment = fragmentManager.addShowHideFragments(fragments, targetView)
    setOnItemSelectedListener(listener)
    return fragment
}
