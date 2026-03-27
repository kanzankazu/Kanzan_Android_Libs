package com.kanzankazu.kanzandatabase.supabase

data class SupabaseFilterCondition(
    val column: String,
    val value: Any,
    val operator: SupabaseFilterOperator = SupabaseFilterOperator.EQ
)
