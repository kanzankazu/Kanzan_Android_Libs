package com.kanzankazu.kanzanutil.kanzanextension.view

import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

/**
 * Connects a `TabLayout` with a `ViewPager2` using a custom tab configuration strategy.
 * This method uses `TabLayoutMediator` to attach the `TabLayout` to the `ViewPager2`,
 * allowing for synchronized interaction between the two, such as tab selection and page swiping.
 *
 * @param pager The `ViewPager2` instance to be linked with the `TabLayout`.
 * @param tabListener The `TabLayoutMediator.TabConfigurationStrategy` used to configure the tabs.
 *                    This listener defines how each tab should be styled and configured.
 *
 * Example:
 * ```kotlin
 * val tabLayout: TabLayout = findViewById(R.id.tabLayout)
 * val viewPager: ViewPager2 = findViewById(R.id.viewPager)
 *
 * tabLayout.setTabMediator(viewPager) { tab, position ->
 *     tab.text = "Tab $position"
 * }
 * ```
 */
fun TabLayout.setTabMediator(pager: ViewPager2, tabListener: TabLayoutMediator.TabConfigurationStrategy) {
    TabLayoutMediator(this, pager, tabListener).attach()
}
