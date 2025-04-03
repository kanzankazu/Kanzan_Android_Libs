package com.kanzankazu.kanzanutil

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.kanzankazu.R
import com.kanzankazu.kanzanwidget.viewpager.base.BaseFragmentPagerStateAdapter

class BaseFragmentHelper(private val mActivity: AppCompatActivity) {

    fun setupTabLayoutViewPagerFragment(
        viewPager: ViewPager,
        fragments: ArrayList<Fragment>,
        tabLayout: TabLayout? = null,
        titleTab: ArrayList<String>? = null,
        iconTab: ArrayList<Int>? = null
    ): ViewPagerFragmentAdapter {
        val mPagerAdapter = ViewPagerFragmentAdapter(mActivity.supportFragmentManager)

        // Tambahkan fragmen dan judul
        if (!titleTab.isNullOrEmpty()) {
            mPagerAdapter.addFragmentsTitles(fragments, titleTab)
        } else {
            mPagerAdapter.addFragments(fragments)
        }

        setupTabIcons(tabLayout, iconTab)

        val limit = if (mPagerAdapter.count > 1) mPagerAdapter.count - 1 else 1
        viewPager.adapter = mPagerAdapter
        viewPager.offscreenPageLimit = limit
        tabLayout?.setupWithViewPager(viewPager)

        return mPagerAdapter
    }

    private fun setupTabIcons(tabLayout: TabLayout?, iconTab: ArrayList<Int>?) {
        tabLayout?.let { layout ->
            iconTab?.forEachIndexed { index, iconResId ->
                val tab = layout.newTab().setIcon(iconResId)
                layout.addTab(tab)
                val color = if (index == 0) {
                    mActivity.resources.getColor(R.color.baseWhite, mActivity.theme)
                } else {
                    mActivity.resources.getColor(R.color.baseBlack, mActivity.theme)
                }
                tab.icon?.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
            }
        }
    }

    class ViewPagerFragmentAdapter(fm: FragmentManager) : BaseFragmentPagerStateAdapter(fm)
}