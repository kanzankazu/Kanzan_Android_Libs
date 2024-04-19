package com.kanzankazu.kanzanwidget.spinner.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.annotation.LayoutRes

abstract class BaseStandartAdapterStandart<T>(val context: Context) : BaseAdapter() {

    var mainData = arrayListOf<T>()
    var tempData = arrayListOf<T>()

    val inflater: LayoutInflater = LayoutInflater.from(context)

    @LayoutRes
    protected abstract fun getBindView(): Int

    protected abstract fun setContent(data: T, v: View, position: Int)

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val v = inflater.inflate(getBindView(), null)
        setContent(getItem(position), v, position)
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