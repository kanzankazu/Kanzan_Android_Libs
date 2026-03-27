package com.kanzankazu.kanzandatabase.supabase.database

import com.kanzankazu.kanzandatabase.supabase.SupabaseFilterCondition
import com.kanzankazu.kanzannetwork.response.kanzanbaseresponse.BaseResponse
import kotlinx.coroutines.flow.Flow

interface SupabaseDatabase {
    suspend fun <T : Any> insert(table: String, data: T): BaseResponse<T>
    suspend fun <T : Any> upsert(table: String, data: T): BaseResponse<T>
    suspend fun <T : Any> select(table: String, filters: List<SupabaseFilterCondition> = emptyList()): BaseResponse<List<T>>
    suspend fun <T : Any> selectById(table: String, column: String, id: Any): BaseResponse<T>
    suspend fun <T : Any> update(table: String, data: T, filters: List<SupabaseFilterCondition>): BaseResponse<T>
    suspend fun delete(table: String, filters: List<SupabaseFilterCondition>): BaseResponse<Unit>
    suspend fun <T : Any> selectWithPagination(table: String, page: Int, pageSize: Int, filters: List<SupabaseFilterCondition> = emptyList()): BaseResponse<List<T>>
    suspend fun <T : Any> selectWithOrder(table: String, column: String, ascending: Boolean = true, filters: List<SupabaseFilterCondition> = emptyList()): BaseResponse<List<T>>
    fun <T : Any> selectAsFlow(table: String, filters: List<SupabaseFilterCondition> = emptyList()): Flow<BaseResponse<List<T>>>
}
