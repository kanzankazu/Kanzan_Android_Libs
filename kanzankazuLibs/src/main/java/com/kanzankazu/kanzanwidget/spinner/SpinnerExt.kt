package com.kanzankazu.kanzanwidget.spinner

import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.Spinner
import com.kanzankazu.R

fun ArrayList<String>.setupOptionItemListSpinner(spinner: Spinner, listener: AdapterView.OnItemSelectedListener?) {
    spinner.setupOptionItemListSpinner(this, listener)
}

fun Spinner.setupOptionItemListSpinner(list: ArrayList<String>, listener: AdapterView.OnItemSelectedListener?) {
    val adapter = ArrayAdapter(this.context, R.layout.multiline_spinner_item, list)
    adapter.setDropDownViewResource(R.layout.multiline_spinner_item)
    this.adapter = adapter
    this.onItemSelectedListener = listener
}

fun Spinner.setupOptionItemListSpinner(adapter: BaseAdapter, listener: AdapterView.OnItemSelectedListener?) {
    this.adapter = adapter
    this.onItemSelectedListener = listener
}
