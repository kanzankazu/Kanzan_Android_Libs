@file:Suppress("DEPRECATION")

package com.kanzankazu.kanzanwidget.viewpager.base

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.kanzankazu.kanzanutil.kanzanextension.type.ifZero

/**
 * Versi pager ini lebih berguna ketika ada banyak halaman, bekerja lebih seperti tampilan daftar.
 * Ketika halaman tidak terlihat oleh pengguna, seluruh fragmennya dapat dihancurkan, hanya menjaga keadaan yang disimpan dari fragmen itu.
 * Hal ini memungkinkan pager untuk mempertahankan memori yang jauh lebih sedikit terkait dengan setiap halaman yang dikunjungi dibandingkan dengan FragmentPagerAdapterbiaya yang berpotensi lebih tinggi saat beralih antar halaman.
 * */
open class BaseFragmentPagerStateAdapter(fragmentManager: FragmentManager) :
    androidx.fragment.app.FragmentStatePagerAdapter(fragmentManager), BaseFragmentPagerViewAdapter {

    var fragments: ArrayList<Fragment> = ArrayList()
    private var titles: ArrayList<String> = ArrayList()

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