package com.kanzankazu.kanzanwidget.viewpager.base

import androidx.fragment.app.Fragment

interface BaseFragmentPagerViewAdapter {
    fun addFragment(fragment: Fragment, title: String)
    fun addFragmentsTitles(fragments: ArrayList<Fragment>, titles: ArrayList<String>)
    fun addFragments(fragments: ArrayList<Fragment>)
}