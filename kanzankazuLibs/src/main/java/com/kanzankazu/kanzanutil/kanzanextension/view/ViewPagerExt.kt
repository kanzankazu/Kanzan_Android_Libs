package com.kanzankazu.kanzanutil.kanzanextension.view

import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.kanzankazu.kanzanutil.kanzanextension.type.debugMessageDebug

fun ViewPager2.setViewPager(
    fragmentStateAdapter: FragmentStateAdapter,
    isCanSwipe: Boolean,
    offscreenPageLimits: Int? = null,
) {
    adapter = fragmentStateAdapter
    isUserInputEnabled = isCanSwipe
    offscreenPageLimits?.let { offscreenPageLimit = offscreenPageLimits }
}

fun ViewPager2.setViewPagerWithTabLayout(
    fragmentStateAdapter: FragmentStateAdapter,
    tabLayout: TabLayout,
    tabConfigurationStrategy: TabLayoutMediator.TabConfigurationStrategy,
    isCanSwipe: Boolean = true,
) {
    setViewPager(fragmentStateAdapter, isCanSwipe)
    tabLayout.setTabMediator(this, tabConfigurationStrategy)
}

fun ViewPager2.listenerWithBottomNavView(bottomNavigationView: BottomNavigationView, arrayOfMenuId: ArrayList<Int>) {
    this.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            "onPageScrolled,$position,$positionOffset,$positionOffsetPixels".debugMessageDebug()
        }

        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            "onPageSelected,$position".debugMessageDebug()
            bottomNavigationView.selectedItemId = arrayOfMenuId[position]
        }

        override fun onPageScrollStateChanged(state: Int) {
            super.onPageScrollStateChanged(state)
            "onPageScrollStateChanged,$state".debugMessageDebug()
        }
    })
}

fun ViewPager.listenerWithBottomNavView(bottomNavigationView: BottomNavigationView, arrayOfMenuId: ArrayList<Int>) {
    this.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            "onPageScrolled,$position,$positionOffset,$positionOffsetPixels".debugMessageDebug()
        }

        override fun onPageSelected(position: Int) {
            "onPageSelected,$position".debugMessageDebug()
            bottomNavigationView.selectedItemId = arrayOfMenuId[position]
        }

        override fun onPageScrollStateChanged(state: Int) {
            "onPageScrollStateChanged,$state".debugMessageDebug()
        }
    })
}
