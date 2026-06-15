package com.kanzankazu.kanzandatabase

import com.kanzankazu.kanzandatabase.firebase.FilterCondition
import com.kanzankazu.kanzannetwork.response.kanzanbaseresponse.BaseResponse
import kotlinx.coroutines.flow.Flow

/**
 * Backend-agnostic database interface.
 * Implementations: KanzanFirebaseDatabase (Firebase RTDB), KanzanSupabaseDatabase (Supabase/Postgres).
 * Tidak terikat ke Firebase types (DatabaseReference, Query, DataSnapshot).
 */
interface KanzanDatabase {

    /** Generate primary key unik untuk table */
    fun createPrimaryKey(table: String): String

    // ==================== CRUD (Suspend) ====================

    /** Insert/upsert data ke table dengan ID tertentu */
    suspend fun <T> setData(table: String, id: String, value: T): BaseResponse<String>

    /** Update seluruh object di table/id */
    suspend fun <T> updateData(table: String, id: String, value: T): BaseResponse<String>

    /** Update single field di table/id */
    suspend fun updateField(table: String, id: String, fieldKey: String, fieldValue: String): BaseResponse<String>

    /** Hapus data dari table berdasarkan id */
    suspend fun removeData(table: String, id: String): BaseResponse<String>

    /** Get single object by id */
    suspend fun <T> getById(table: String, id: String, targetClass: Class<T>): BaseResponse<T>

    /** Get all data dari table */
    suspend fun <T> getAll(table: String, targetClass: Class<T>): BaseResponse<List<T>>

    /** Get data by single key-value filter */
    suspend fun <T> getByKeyValue(table: String, key: String, value: String, targetClass: Class<T>): BaseResponse<List<T>>

    /** Get data dengan multiple filter conditions */
    suspend fun <T> getByFilters(
        table: String,
        filters: List<FilterCondition>,
        targetClass: Class<T>,
        orderBy: String = "createdAt",
        isDescending: Boolean = false,
        limit: Int = 20
    ): BaseResponse<List<T>>

    /** Get data dengan pagination (cursor-based) */
    suspend fun <T> getWithPagination(
        table: String,
        targetClass: Class<T>,
        orderBy: String = "createdAt",
        lastValue: Any? = null,
        limit: Int = 20,
        isDescending: Boolean = false
    ): BaseResponse<List<T>>

    /** Cek apakah data exist berdasarkan key-value */
    suspend fun isExist(table: String, key: String, value: String): Pair<Boolean, String>

    /** Cek apakah data exist berdasarkan id */
    suspend fun isExistById(table: String, id: String): Pair<Boolean, String>

    // ==================== Observe (Flow/Realtime) ====================

    /** Observe semua data di table (realtime) */
    fun <T> observeAll(table: String, targetClass: Class<T>): Flow<BaseResponse<List<T>>>

    /** Observe single object by id (realtime) */
    fun <T> observeById(table: String, id: String, targetClass: Class<T>): Flow<BaseResponse<T>>

    /** Observe data by key-value filter (realtime) */
    fun <T> observeByKeyValue(table: String, key: String, value: String, targetClass: Class<T>): Flow<BaseResponse<List<T>>>

    /** Observe data by multiple filters (realtime) */
    fun <T> observeByFilters(
        table: String,
        filters: List<FilterCondition>,
        targetClass: Class<T>,
        orderBy: String = "createdAt",
        isDescending: Boolean = false,
        limit: Int = 20
    ): Flow<BaseResponse<List<T>>>
}
