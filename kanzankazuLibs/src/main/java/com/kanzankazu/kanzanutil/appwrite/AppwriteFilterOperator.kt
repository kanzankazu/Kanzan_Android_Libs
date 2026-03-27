package com.kanzankazu.kanzanutil.appwrite

enum class AppwriteFilterOperator {
    EQUAL, NOT_EQUAL,
    GREATER_THAN, GREATER_THAN_OR_EQUAL,
    LESS_THAN, LESS_THAN_OR_EQUAL,
    SEARCH, IS_NULL, IS_NOT_NULL,
    BETWEEN, STARTS_WITH, ENDS_WITH, CONTAINS
}
