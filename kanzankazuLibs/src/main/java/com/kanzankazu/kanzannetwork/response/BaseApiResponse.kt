package com.kanzankazu.kanzannetwork.response

import com.google.gson.annotations.SerializedName

data class BaseApiResponse<T>(
    @SerializedName("success") val success: Boolean?,
    @SerializedName("status") val status: String?,
    @SerializedName("message") val message: String?,
    @SerializedName("data") val result: T?,
)
