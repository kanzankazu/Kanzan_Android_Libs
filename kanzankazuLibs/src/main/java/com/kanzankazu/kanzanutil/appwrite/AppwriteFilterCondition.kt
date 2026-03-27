package com.kanzankazu.kanzanutil.appwrite

data class AppwriteFilterCondition(
    val attribute: String,
    val value: Any? = null,
    val operator: AppwriteFilterOperator = AppwriteFilterOperator.EQUAL
)
