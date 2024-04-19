package com.kanzankazu.kanzanwidget.spinner.adapter

import android.R
import android.content.Context
import android.widget.ArrayAdapter

class BaseArrayAdapter(context: Context, datas: MutableList<String>, resource: Int = R.layout.simple_spinner_item) : ArrayAdapter<String>(context, resource, datas)