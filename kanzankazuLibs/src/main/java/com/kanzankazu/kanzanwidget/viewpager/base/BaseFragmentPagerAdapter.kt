@file:Suppress("DEPRECATION")

package com.kanzankazu.kanzanwidget.viewpager.base

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.kanzankazu.kanzanutil.kanzanextension.type.ifZero

/**
 * Versi pager ini terbaik untuk digunakan ketika ada beberapa fragmen statis yang lebih umum untuk di-paging, seperti seperangkat tab.
 * Fragmen setiap halaman yang dikunjungi pengguna akan disimpan dalam memori, meskipun hierarki tampilan dapat dihancurkan saat tidak terlihat.
 * Ini dapat mengakibatkan penggunaan jumlah memori yang signifikan karena instance fragmen dapat bertahan pada jumlah negara yang sewenang-wenang.
 * Untuk set halaman yang lebih besar, pertimbangkan FragmentStatePagerAdapter.
 * */
abstract class BaseFragmentPagerAdapter(fragmentManager: FragmentManager) :
    androidx.fragment.app.FragmentPagerAdapter(fragmentManager), BaseFragmentPagerViewAdapter {

    var fragments = ArrayList<Fragment>()
    private var titles = ArrayList<String>()

    override fun getPageTitle(position: Int): CharSequence? = if (titles.isEmpty()) super.getPageTitle(position) else titles[position]

    override fun getItem(position: Int): Fragment = fragments[position]

    override fun getCount(): Int = fragments.size

    override fun addFragment(fragment: Fragment, title: String) {
        this.fragments.add(fragment)
        this.titles.add(title)
        notifyDataSetChanged()
    }

    override fun addFragmentsTitles(fragments: ArrayList<Fragment>, titles: ArrayList<String>) {
        if (this.fragments.size > 0) {
            this.fragments.clear()
            this.fragments = fragments
            this.titles.clear()
            this.titles = titles
        } else {
            this.fragments = fragments
            this.titles = titles
        }
        notifyDataSetChanged()
    }

    override fun addFragments(fragments: ArrayList<Fragment>) {
        if (this.fragments.size > 0) {
            this.fragments.clear()
            this.fragments = fragments
        } else {
            this.fragments = fragments
        }
        notifyDataSetChanged()
    }

    inline fun <reified T : Fragment> getFragment() =
        fragments.find { it is T } as? T

    inline fun <reified T : Fragment> getFragmentPos() =
        fragments.indexOfFirst { it is T }.ifZero(0)
}
