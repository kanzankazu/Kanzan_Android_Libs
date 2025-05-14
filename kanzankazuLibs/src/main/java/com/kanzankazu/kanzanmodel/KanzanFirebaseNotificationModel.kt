@file:Suppress("PackageName")

package com.kanzankazu.kanzanmodel

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Created by Faisal Bahri on 2020-02-11.
 */
@Parcelize
data class KanzanFirebaseNotificationModel(
    @SerializedName(KanzanFirebaseNotificationConst.RECEIVER_UID) var receiverUid: String = "",
    @SerializedName(KanzanFirebaseNotificationConst.RECEIVER_TOPIC) var receiverTopic: String = "",
    @SerializedName(KanzanFirebaseNotificationConst.TITLE) var title: String = "",
    @SerializedName(KanzanFirebaseNotificationConst.MESSAGE) var message: String = "",
    @SerializedName(KanzanFirebaseNotificationConst.TYPE) var type: String = "",
    @SerializedName(KanzanFirebaseNotificationConst.ID) var id: String = "",
    @SerializedName(KanzanFirebaseNotificationConst.ID_SUB) var idSub: String = "",
) : Parcelable