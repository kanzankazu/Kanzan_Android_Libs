package com.kanzankazu.kanzanwidget.recyclerview.genericadapter

import android.view.View
import androidx.fragment.app.Fragment

class GenericFragmentContent(
    val fragment: Fragment,
    val tag: String,
    val id: Int = View.generateViewId(),
) : BaseEquatable(id)
