@file:Suppress("MemberVisibilityCanBePrivate", "DEPRECATION")

package com.kanzankazu.kanzanwidget.viewpager

import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.kanzankazu.kanzanwidget.viewpager.base.BaseFragmentPagerStateAdapter
import com.kanzankazu.kanzanwidget.viewpager.widget.NoSwipeViewPager

class KanzanViewPagerBottomNavView(
    private val viewPager: ViewPager,
    private val bottomNavigationView: BottomNavigationView,
    private val baseFragmentPagerStateAdapter: BaseFragmentPagerStateAdapter,
) {
    private var isPagingEnabled = false

    init {
        viewPager.adapter = baseFragmentPagerStateAdapter
        setPagingEnabled(isPagingEnabled)
        viewPager.offscreenPageLimit = baseFragmentPagerStateAdapter.count + 1
    }

    fun setPageData(fragments: ArrayList<Fragment>) {
        baseFragmentPagerStateAdapter.addFragments(fragments)
    }

    fun setPagePosition(position: Int) {
        bottomNavigationView.menu.getItem(position).isChecked = true
        viewPager.currentItem = position
    }

    fun getPagePosition(): Int {
        return viewPager.currentItem
    }

    fun setPagingEnabled(isPagingEnabled: Boolean) {
        this.isPagingEnabled = isPagingEnabled
        if (viewPager is NoSwipeViewPager) viewPager.setPagingEnabled(this.isPagingEnabled)
    }

    fun setOnNavigationItemSelectedListener(listener: BottomNavigationView.OnNavigationItemSelectedListener) {
        bottomNavigationView.setOnNavigationItemSelectedListener(listener)
    }
}
