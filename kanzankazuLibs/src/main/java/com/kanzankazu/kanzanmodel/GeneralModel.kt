package com.kanzankazu.kanzanmodel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
open class GeneralModel(
    open var id: String = "",
) : Parcelable, GeneralCreateUpdateDeleteModel()
