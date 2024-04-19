@file:Suppress("PackageName")

package com.kanzankazu.kanzanmodel

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Created by Faisal Bahri on 2020-02-11.
 */
@Parcelize
data class MyFirebaseNotificationModel(
    @SerializedName("receiverUid") var receiverUid: String = "",
    @SerializedName("receiverTopic") var receiverTopic: String = "",
    @SerializedName("title") var title: String = "",
    @SerializedName("message") var message: String = "",
    @SerializedName("type") var type: String = "",
    @SerializedName("id") var id: String = "",
    @SerializedName("idSub") var idSub: String = "",
) : Parcelable

