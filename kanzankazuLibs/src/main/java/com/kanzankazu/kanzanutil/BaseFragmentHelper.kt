package com.kanzankazu.kanzanutil

import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.kanzankazu.R
import com.kanzankazu.kanzanwidget.viewpager.base.BaseFragmentPagerStateAdapter

class BaseFragmentHelper(val mActivity: AppCompatActivity) {

    @Suppress("DEPRECATION")
    fun setupTabLayoutViewPagerFragment(viewPager: ViewPager, fragments: ArrayList<Fragment>, tabLayout: TabLayout? = null, titleTab: ArrayList<String>? = null, iconTab: ArrayList<Int>? = null): ViewPagerFragmentAdapter {
        val mPagerAdapter = ViewPagerFragmentAdapter(mActivity.supportFragmentManager)
        titleTab?.let { mPagerAdapter.addFragmentsTitles(fragments, titleTab) } ?: kotlin.run {
            mPagerAdapter.addFragments(fragments)
        }

        tabLayout?.let {
            iconTab?.let {
                for (i in iconTab.indices) {
                    tabLayout.addTab(tabLayout.newTab().setIcon(iconTab[i]))
                    when (i) {
                        0 -> tabLayout.getTabAt(0)?.icon?.setColorFilter(mActivity.resources.getColor(R.color.baseWhite), PorterDuff.Mode.SRC_IN)
                        else -> tabLayout.getTabAt(i)?.icon?.setColorFilter(mActivity.resources.getColor(R.color.baseBlack), PorterDuff.Mode.SRC_IN)
                    }
                }
            }
        }

        val limit = if (mPagerAdapter.count > 1) mPagerAdapter.count - 1 else 1
        viewPager.adapter = mPagerAdapter
        viewPager.offscreenPageLimit = limit
        tabLayout?.setupWithViewPager(viewPager)
        return mPagerAdapter
    }

    class ViewPagerFragmentAdapter(fm: FragmentManager) : BaseFragmentPagerStateAdapter(fm)
}