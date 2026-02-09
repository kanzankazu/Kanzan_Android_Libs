package com.kanzankazu.kanzanmodel

import android.os.Parcelable
import com.kanzankazu.kanzanutil.BaseConst
import com.kanzankazu.kanzanutil.kanzanextension.getDateNowStringWithFormat
import kotlinx.parcelize.Parcelize

@Parcelize
open class GeneralCreateUpdateDeleteModel(
    open var createAt: String = "",
    open var createBy: String = "",
    open var updateAt: String = "",
    open var updateBy: String = "",
    open var deleteAt: String = "",
    open var deleteBy: String = "",
) : Parcelable {
    fun setCreateMetadata(userId: String) {
        createAt = getDateNowStringWithFormat(BaseConst.DATE_FORMAT_COMPLETE_2)
        createBy = userId
    }
    fun setUpdateMetadata(userId: String) {
        updateAt = getDateNowStringWithFormat(BaseConst.DATE_FORMAT_COMPLETE_2)
        updateBy = userId
    }
    fun setDeleteMetadata(userId: String) {
        deleteAt = getDateNowStringWithFormat(BaseConst.DATE_FORMAT_COMPLETE_2)
        deleteBy = userId
    }
}
