package com.kanzankazu.kanzanwidget.spinner.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter

abstract class BaseStandartAdapterView<T>(val context: Context) : BaseAdapter() {

    var mainData = arrayListOf<T>()
    var tempData = arrayListOf<T>()

    protected abstract fun getBindView(): View

    protected abstract fun setContent(data: T, position: Int)

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val v = getBindView()
        setContent(getItem(position), position)
        return v
    }

    override fun getCount(): Int = tempData.count()

    override fun getItem(position: Int): T = tempData[position]

    override fun getItemId(position: Int): Long = position.toLong()

    fun setData(dataS: ArrayList<T>) {
        if (this.mainData.isNotEmpty()) {
            this.mainData.clear()
            this.tempData.clear()

            this.mainData = dataS
            this.tempData = dataS
        } else {
            this.mainData = dataS
            this.tempData = dataS
        }
        notifyDataSetChanged()
    }
}
