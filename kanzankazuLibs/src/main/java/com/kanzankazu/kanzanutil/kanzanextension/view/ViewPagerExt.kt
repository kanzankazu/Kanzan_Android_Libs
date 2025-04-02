package com.kanzankazu.kanzanutil.kanzanextension.view

import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.kanzankazu.kanzanutil.kanzanextension.type.debugMessageDebug

/**
 * Configures a `ViewPager2` instance with a given adapter, swipe behavior,
 * and optionally sets the number of offscreen pages to retain.
 *
 * @param fragmentStateAdapter The adapter to supply pages to the `ViewPager2`.
 *                             This must be a `FragmentStateAdapter` instance.
 * @param isCanSwipe Determines whether the `ViewPager2` can respond to swipe gestures.
 *                   Set to `true` to enable swiping, or `false` to disable it.
 * @param offscreenPageLimits The optional number of pages to keep in memory on each side of the current page.
 *                            If not provided, the default `ViewPager2` behavior is used.
 *
 * Example:
 * ```kotlin
 * val viewPager: ViewPager2 = findViewById(R.id.viewPager)
 * val adapter = MyFragmentStateAdapter(this)
 * viewPager.setViewPager(adapter, isCanSwipe = true, offscreenPageLimits = 2)
 * ```
 */
fun ViewPager2.setViewPager(
    fragmentStateAdapter: FragmentStateAdapter,
    isCanSwipe: Boolean,
    offscreenPageLimits: Int? = null,
) {
    adapter = fragmentStateAdapter
    isUserInputEnabled = isCanSwipe
    offscreenPageLimits?.let { offscreenPageLimit = offscreenPageLimits }
}

/**
 * Configures the ViewPager2 with the specified FragmentStateAdapter, associates it with a TabLayout using
 * a TabLayoutMediator, and optionally enables or disables swipe gestures for the ViewPager2.
 *
 * @param fragmentStateAdapter The adapter supplying the pages to the ViewPager2.
 * @param tabLayout The TabLayout to be linked with the ViewPager2.
 * @param tabConfigurationStrategy A lambda or callback to configure the tabs in the TabLayout during setup.
 * @param isCanSwipe Optional. Specifies whether swipe gestures are enabled or disabled for the ViewPager2. Defaults to `true`.
 *
 * Example:
 * ```kotlin
 * val adapter = MyFragmentStateAdapter(this)
 * val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
 * viewPager2.setViewPagerWithTabLayout(
 *     fragmentStateAdapter = adapter,
 *     tabLayout = tabLayout,
 *     tabConfigurationStrategy = TabLayoutMediator.TabConfigurationStrategy { tab, position ->
 *         tab.text = "Tab $position"
 *     },
 *     isCanSwipe = true
 * )
 * ```
 */
fun ViewPager2.setViewPagerWithTabLayout(
    fragmentStateAdapter: FragmentStateAdapter,
    tabLayout: TabLayout,
    tabConfigurationStrategy: TabLayoutMediator.TabConfigurationStrategy,
    isCanSwipe: Boolean = true,
) {
    setViewPager(fragmentStateAdapter, isCanSwipe)
    tabLayout.setTabMediator(this, tabConfigurationStrategy)
}

/**
 * Connects a BottomNavigationView to a ViewPager2, enabling synchronized navigation and page selection.
 * The selected menu item in the BottomNavigationView updates based on the current page of the ViewPager2,
 * and vice versa.
 *
 * @param bottomNavigationView The BottomNavigationView instance to be synchronized with the ViewPager2.
 * @param arrayOfMenuId An ArrayList of menu item IDs from the BottomNavigationView that correspond to the pages
 *                      in the ViewPager2. The order of the IDs should align with the order of pages in the ViewPager2.
 *
 * Example:
 * ```kotlin
 * val bottomNavView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
 * val viewPager: ViewPager2 = findViewById(R.id.viewPager)
 * val menuIds = arrayListOf(R.id.home, R.id.search, R.id.profile)
 *
 * viewPager.listenerWithBottomNavView(bottomNavView, menuIds)
 * ```
 */
fun ViewPager2.listenerWithBottomNavView(bottomNavigationView: BottomNavigationView, arrayOfMenuId: ArrayList<Int>) {
    this.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
        /**
         * Callback method triggered when a page is being scrolled in the ViewPager2.
         * Provides information on the current page position, the page offset, and the pixel offset of the scroll.
         *
         * @param position The index of the currently visible page. It is the page position being scrolled from.
         * @param positionOffset The offset from the starting position of the currently visible page, expressed as a
         *                       float value between 0 (completely on the page) and 1 (completely scrolling to the next page).
         * @param positionOffsetPixels The offset in pixels from the starting position of the currently visible page.
         *
         * Example log message generated:
         * ```
         * "onPageScrolled,2,0.5,120"
         * ```
         */
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            super.onPageScrolled(position, positionOffset, positionOffsetPixels)
            "onPageScrolled,$position,$positionOffset,$positionOffsetPixels".debugMessageDebug()
        }

        /**
         * Handles the event when a new page is selected in a ViewPager2 component.
         * This method updates the selected item in the BottomNavigationView to reflect
         * the currently selected page, and logs a debug message with the selected page's position.
         *
         * @param position The index of the newly selected page in the ViewPager2. This corresponds
         *                 to the position of the associated item in the array of menu IDs.
         *
         * Example:
         * ```kotlin
         * val viewPager: ViewPager2 = findViewById(R.id.view_pager)
         * val bottomNavView: BottomNavigationView = findViewById(R.id.bottom_navigation)
         * val menuIds = arrayListOf(R.id.menu_home, R.id.menu_settings, R.id.menu_profile)
         *
         * viewPager.listenerWithBottomNavView(bottomNavView, menuIds)
         *
         * // When the page changes, the corresponding BottomNavigationView item will be updated:
         * // If position = 1 -> bottomNavView.selectedItemId = R.id.menu_settings
         * ```
         */
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            "onPageSelected,$position".debugMessageDebug()
            bottomNavigationView.selectedItemId = arrayOfMenuId[position]
        }

        /**
         * Handles changes to the scroll state of a ViewPager2. This method is invoked whenever the scroll state
         * of the ViewPager2 changes, providing the current state as a parameter. It also logs the state change
         * for debugging purposes.
         *
         * @param state The new scroll state of the ViewPager2. Possible values are:
         *              - SCROLL_STATE_IDLE (0): Indicates that the ViewPager2 is not scrolling.
         *              - SCROLL_STATE_DRAGGING (1): Indicates that the user is actively dragging the pages.
         *              - SCROLL_STATE_SETTLING (2): Indicates that the ViewPager2 is settling to a final position.
         *
         * Example:
         * ```kotlin
         * override fun onPageScrollStateChanged(state: Int) {
         *     super.onPageScrollStateChanged(state)
         *     "onPageScrollStateChanged,$state".debugMessageDebug()
         * }
         * ```
         */
        override fun onPageScrollStateChanged(state: Int) {
            super.onPageScrollStateChanged(state)
            "onPageScrollStateChanged,$state".debugMessageDebug()
        }
    })
}

/**
 * Synchronizes a ViewPager with a BottomNavigationView by listening to page changes
 * and updating the selected menu item in the BottomNavigationView accordingly.
 *
 * @param bottomNavigationView The BottomNavigationView to update when the ViewPager's page changes.
 * @param arrayOfMenuId A list of menu item IDs in the BottomNavigationView. The position in this
 *                      list should correspond to the pages in the ViewPager.
 *
 * Example:
 * ```kotlin
 * val viewPager: ViewPager = findViewById(R.id.view_pager)
 * val bottomNav: BottomNavigationView = findViewById(R.id.bottom_navigation)
 * val menuIds = arrayListOf(
 *     R.id.menu_home,
 *     R.id.menu_search,
 *     R.id.menu_profile
 * )
 * viewPager.listenerWithBottomNavView(bottomNav, menuIds)
 * ```
 */
fun ViewPager.listenerWithBottomNavView(bottomNavigationView: BottomNavigationView, arrayOfMenuId: ArrayList<Int>) {
    this.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
        /**
         * Triggered when a page is being scrolled in the `ViewPager`. It provides information about the
         * current scroll position, offset, and pixel adjustments as the user interacts with the pages.
         *
         * @param position The index of the first page currently visible. This will change as the page scrolls.
         * @param positionOffset The offset from the starting position as a percentage (0 to 1) of the page width indicating
         *                       how far the first visible page has been scrolled.
         * @param positionOffsetPixels The pixel offset indicating the distance scrolled from the starting position.
         *
         * Example:
         * ```kotlin
         * override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
         *     "Page scrolled -> Position: $position, Offset: $positionOffset, Pixels: $positionOffsetPixels".debugMessageDebug()
         * }
         * ```
         */
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            "onPageScrolled,$position,$positionOffset,$positionOffsetPixels".debugMessageDebug()
        }

        /**
         * Triggered when the selected page changes in the ViewPager.
         * Updates the `BottomNavigationView` to reflect the currently selected page by setting
         * the selected item ID to the corresponding menu ID at the specified position.
         *
         * @param position The index of the newly selected page in the ViewPager.
         *                 This index is used to retrieve the corresponding menu ID from the `arrayOfMenuId`.
         *
         * Example:
         * ```kotlin
         * val position = 2 // The third page was selected in the ViewPager
         * onPageSelected(position) // The BottomNavigationView updates to highlight the third menu item
         * ```
         */
        override fun onPageSelected(position: Int) {
            "onPageSelected,$position".debugMessageDebug()
            bottomNavigationView.selectedItemId = arrayOfMenuId[position]
        }

        /**
         * Called when the scroll state of the ViewPager changes.
         * This method is invoked whenever the pager's scrolling state transitions between `IDLE`, `DRAGGING`,
         * or `SETTLING` states, allowing developers to track or act upon these changes. The method logs
         * the state change for debugging purposes.
         *
         * @param state The new scroll state. Possible values are:
         *              - `0` (IDLE): The pager is not currently scrolling.
         *              - `1` (DRAGGING): The pager is actively being dragged by the user.
         *              - `2` (SETTLING): The pager is in the process of settling to a final position after a drag or fling.
         *
         * Example:
         * ```kotlin
         * override fun onPageScrollStateChanged(state: Int) {
         *     // Logs the current state.
         *     "onPageScrollStateChanged: $state".debugMessageDebug()
         * }
         * ```
         */
        override fun onPageScrollStateChanged(state: Int) {
            "onPageScrollStateChanged,$state".debugMessageDebug()
        }
    })
}
