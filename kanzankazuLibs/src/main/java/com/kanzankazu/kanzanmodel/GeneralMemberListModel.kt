package com.kanzankazu.kanzanmodel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
open class GeneralMemberListModel(
    open var memberId: String = "",
    open var memberName: String = "",
) : Parcelable
