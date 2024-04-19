package com.kanzankazu.kanzanutil.kanzanextension.view

import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

fun TabLayout.setTabMediator(pager: ViewPager2, tabListener: TabLayoutMediator.TabConfigurationStrategy) {
    TabLayoutMediator(this, pager, tabListener).attach()
}
