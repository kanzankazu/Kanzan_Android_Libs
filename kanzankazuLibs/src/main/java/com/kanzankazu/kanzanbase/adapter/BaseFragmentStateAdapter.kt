package com.kanzankazu.kanzanbase.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

abstract class BaseFragmentStateAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle,
) : FragmentStateAdapter(fragmentManager, lifecycle) {
    private var fragments = ArrayList<Fragment>()
    private var fragmentTags = ArrayList<String>()

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    override fun getItemId(position: Int): Long {
        // Gunakan posisi sebagai ID untuk setiap fragment
        return position.toLong()
    }

    override fun containsItem(itemId: Long): Boolean {
        // Periksa apakah itemId (posisi fragment) ada dalam rentang indeks
        return itemId >= 0 && itemId < fragments.size
    }

    fun setFragments(_fragments: ArrayList<Fragment>, _fragmentTags: ArrayList<String>) {
        fragments = _fragments
        fragmentTags = _fragmentTags
        notifyDataSetChanged()
    }

    fun getFragmentTag(position: Int) = fragmentTags[position]
}
