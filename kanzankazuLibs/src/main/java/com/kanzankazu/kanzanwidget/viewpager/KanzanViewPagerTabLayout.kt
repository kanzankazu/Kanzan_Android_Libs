package com.kanzankazu.kanzanwidget.viewpager

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import com.kanzankazu.kanzanwidget.viewpager.base.BaseFragmentPagerStateAdapter
import com.kanzankazu.kanzanwidget.viewpager.widget.NoSwipeViewPager

class KanzanViewPagerTabLayout(
    private val viewPager: ViewPager,
    private val tabLayout: TabLayout,
    private val baseFragmentPagerStateAdapter: BaseFragmentPagerStateAdapter,
) {
    private var isPagingEnabled: Boolean = true

    init {
        Log.d("Lihat KanzanKazu", " KanzanViewPagerTabLayout ${tabLayout.parent}")
        if (tabLayout.parent is AppBarLayout) (tabLayout.parent as AppBarLayout).elevation = 0f
        viewPager.adapter = baseFragmentPagerStateAdapter
        tabLayout.setupWithViewPager(viewPager)
        setPagingEnabled(isPagingEnabled)
        viewPager.offscreenPageLimit = baseFragmentPagerStateAdapter.count
    }

    fun setPageData(fragments: ArrayList<Fragment>) {
        baseFragmentPagerStateAdapter.addFragments(fragments)
    }

    fun setPageTabPosition(position: Int) {
        tabLayout.getTabAt(position)?.select()
        viewPager.currentItem = position
    }

    fun getPagePosition(): Int {
        return viewPager.currentItem
    }

    fun setPagingEnabled(isPagingEnabled: Boolean) {
        this.isPagingEnabled = isPagingEnabled
        if (viewPager is NoSwipeViewPager) viewPager.setPagingEnabled(isPagingEnabled)
    }

    fun setOnTabSelectListener(listener: TabLayout.OnTabSelectedListener) {
        tabLayout.addOnTabSelectedListener(listener)
    }
}
