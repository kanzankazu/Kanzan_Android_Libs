package com.kanzankazu.kanzanmodel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
open class GeneralModel(
    open var id: String = "",
    open var createAt: String = "",
    open var createBy: String = "",
    open var updateAt: String = "",
    open var updateBy: String = "",
    open var deleteAt: String = "",
    open var deleteBy: String = "",
) : Parcelable
