package com.kanzankazu.kanzanutil

import kotlin.math.ceil

/**
 * A generic class that divides a list of data into pages. Provides functionality to navigate between pages
 * and retrieve data from the current page or other specific pages.
 *
 * @param datas The complete list of data to be paginated.
 * @param dataPerPage The number of items per page.
 */
class PageList<T>(private val datas: ArrayList<T>, private val dataPerPage: Int) {
    private var pageListData = arrayListOf<ArrayList<T>>()
    private var totalPage = ceil((datas.size / dataPerPage.toDouble())).toInt()
    private var currentPage = 0

    init {
        var count = 1
        var pageListDataTemp = arrayListOf<T>()
        datas.forEachIndexed { index, i ->
            if (count == dataPerPage || index == datas.lastIndex) {
                pageListDataTemp.add(i)
                pageListData.add(pageListDataTemp)
                pageListDataTemp = arrayListOf()
                count = 1
            } else {
                pageListDataTemp.add(i)
                count += 1
            }
        }
    }

    fun resetPage() {
        currentPage = 0
    }

    fun isFirstPage(): Boolean = currentPage == 0

    fun isLastPage(): Boolean = currentPage == totalPage - 1

    fun getTotalPage(): Int = totalPage

    fun getCurrentPageData(): ArrayList<T> = pageListData[currentPage]

    fun getNextPageData(): ArrayList<T> {
        if (!isLastPage()) currentPage += 1
        return pageListData[currentPage]
    }

    fun getPreviousPageData(): ArrayList<T> {
        if (!isFirstPage()) currentPage -= 1
        return pageListData[currentPage]
    }
}
