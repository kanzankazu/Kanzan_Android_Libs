package com.kanzankazu.kanzandatabase.firebase

/**
 * Data class for filter conditions
 */
data class FilterCondition(
    val key: String,
    val value: Any,
    val operator: FilterOperator = FilterOperator.EQUAL
)