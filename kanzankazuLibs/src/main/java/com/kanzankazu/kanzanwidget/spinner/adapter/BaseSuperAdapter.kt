package com.kanzankazu.kanzanwidget.spinner.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.viewbinding.ViewBinding

abstract class BaseSuperAdapter<T, VB : ViewBinding> : BaseAdapter() {
    var mainData = arrayListOf<T>()
    var tempData = arrayListOf<T>()

    private var _binding: ViewBinding? = null

    /**Sample = VB::inflate*/
    abstract val bindingInflater: (LayoutInflater) -> VB

    @Suppress("UNCHECKED_CAST")
    protected val bind: VB
        get() = _binding as VB

    protected abstract fun setContent(data: T, v: View, position: Int)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val v = bind.root
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