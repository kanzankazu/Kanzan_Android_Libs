package com.kanzankazu.kanzanutil.kanzanextension.view

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import com.kanzankazu.kanzanutil.kanzanextension.addShowHideFragments

/**
 * Links a BottomNavigationView with a ViewPager such that selecting a menu item in the BottomNavigationView
 * updates the current page of the ViewPager. The relationship between menu items and pages is defined
 * by their respective indices in the provided `arrayOfMenuId`.
 *
 * @param viewPager The ViewPager instance to be linked with the BottomNavigationView.
 * @param arrayOfMenuId A list of menu item IDs from the BottomNavigationView. Each menu item ID corresponds
 *                      to a page in the ViewPager based on their index.
 *
 * Example:
 * ```kotlin
 * val viewPager: ViewPager = findViewById(R.id.viewPager)
 * val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
 * val menuIds = arrayListOf(R.id.menu_home, R.id.menu_search, R.id.menu_profile)
 * bottomNavigationView.listenerWithViewPager(viewPager, menuIds)
 * ```
 */
fun BottomNavigationView.listenerWithViewPager(viewPager: ViewPager, arrayOfMenuId: ArrayList<Int>) {
    setOnItemSelectedListener {
        arrayOfMenuId.forEachIndexed { index, i ->
            if (it.itemId == i) viewPager.setCurrentItem(index, true)
        }
        return@setOnItemSelectedListener true
    }
}

/**
 * Connects a BottomNavigationView with a ViewPager2 to synchronize tab selection and page swiping.
 * When a menu item in the BottomNavigationView is selected, the corresponding page in the ViewPager2 is shown.
 *
 * @param viewPager2 The ViewPager2 instance to sync with the BottomNavigationView.
 * @param arrayOfMenuId An ArrayList containing the menu item IDs in the order of their corresponding pages in the ViewPager2.
 *
 * Example:
 * ```kotlin
 * val viewPager2 = ViewPager2(context)
 * val bottomNavigationView = BottomNavigationView(context)
 * val menuIds = arrayListOf(R.id.home, R.id.search, R.id.settings)
 *
 * bottomNavigationView.listenerWithViewPager(viewPager2, menuIds)
 * ```
 */
fun BottomNavigationView.listenerWithViewPager(viewPager2: ViewPager2, arrayOfMenuId: ArrayList<Int>) {
    setOnItemSelectedListener {
        arrayOfMenuId.forEachIndexed { index, i ->
            if (it.itemId == i) viewPager2.setCurrentItem(index, true)
        }
        return@setOnItemSelectedListener true
    }
}

/**
 * Sets up the fragments for the BottomNavigationView and associates a listener for item selection.
 * This method utilizes the `addShowHideFragments` function of the `FragmentManager` to handle the fragment
 * transaction and show the appropriate fragment based on the selected navigation item.
 *
 * @param fragmentManager The `FragmentManager` used to manage the fragments.
 * @param targetView The ID of the currently selected navigation item, which determines the fragment to show.
 * @param fragments A list of `Fragment` objects to be managed and shown/hidden by the `FragmentManager`.
 * @param listener An optional `NavigationBarView.OnItemSelectedListener` to handle navigation item selection events.
 * @return The `Fragment` associated with the currently selected navigation item after the setup.
 *
 * Example:
 * ```kotlin
 * val fragments = arrayListOf(FragmentA(), FragmentB(), FragmentC())
 * val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNav)
 * val fragment = bottomNavigationView.setupFragments(
 *     supportFragmentManager,
 *     R.id.nav_home,
 *     fragments
 * ) { menuItem ->
 *     when (menuItem.itemId) {
 *         R.id.nav_home -> true
 *         R.id.nav_profile -> true
 *         else -> false
 *     }
 * }
 * ```
 */
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
