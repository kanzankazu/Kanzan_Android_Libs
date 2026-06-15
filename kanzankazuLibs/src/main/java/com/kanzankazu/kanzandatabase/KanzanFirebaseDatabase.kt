package com.kanzankazu.kanzandatabase

import com.kanzankazu.kanzandatabase.firebase.FilterCondition
import com.kanzankazu.kanzandatabase.firebase.realtimedatabase.RealtimeDatabaseImpl
import com.kanzankazu.kanzannetwork.response.kanzanbaseresponse.BaseResponse
import com.kanzankazu.kanzanutil.kanzanextension.toObject
import com.kanzankazu.kanzanutil.kanzanextension.toObjectList
import kotlinx.coroutines.flow.Flow

/**
 * Firebase Realtime Database implementation of KanzanDatabase.
 * Wraps existing RealtimeDatabaseImpl tanpa mengubah code lama.
 */
open class KanzanFirebaseDatabase(
    private val impl: RealtimeDatabaseImpl = RealtimeDatabaseImpl()
) : KanzanDatabase {

    override fun createPrimaryKey(table: String): String {
        return impl.createPrimaryKeyData(table)
    }

    // ==================== CRUD ====================

    override suspend fun <T> setData(table: String, id: String, value: T): BaseResponse<String> {
        return impl.setDataBaseResponse(table, id, value)
    }

    override suspend fun <T> updateData(table: String, id: String, value: T): BaseResponse<String> {
        return impl.updateDataBaseResponse(table, id, value)
    }

    override suspend fun updateField(table: String, id: String, fieldKey: String, fieldValue: String): BaseResponse<String> {
        return impl.updateDataBaseResponse(table, id, fieldKey, fieldValue)
    }

    override suspend fun removeData(table: String, id: String): BaseResponse<String> {
        return impl.removeDataBaseResponse(table, id)
    }

    override suspend fun <T> getById(table: String, id: String, targetClass: Class<T>): BaseResponse<T> {
        return impl.getDataByIdBaseResponse(table, id, targetClass, true)
    }

    override suspend fun <T> getAll(table: String, targetClass: Class<T>): BaseResponse<List<T>> {
        return impl.getDataByTableBaseResponse(table, targetClass, true)
    }

    override suspend fun <T> getByKeyValue(table: String, key: String, value: String, targetClass: Class<T>): BaseResponse<List<T>> {
        val query = impl.querySelectTableDataByKeyValue(table, key, value)
        return impl.getDataByQuerySuspend(query, targetClass)
    }

    override suspend fun <T> getByFilters(
        table: String,
        filters: List<FilterCondition>,
        targetClass: Class<T>,
        orderBy: String,
        isDescending: Boolean,
        limit: Int
    ): BaseResponse<List<T>> {
        val query = impl.queryWithMultipleConditions(table, filters, orderBy, isDescending, limit)
        return impl.getDataByQuerySuspend(query, targetClass)
    }

    override suspend fun <T> getWithPagination(
        table: String,
        targetClass: Class<T>,
        orderBy: String,
        lastValue: Any?,
        limit: Int,
        isDescending: Boolean
    ): BaseResponse<List<T>> {
        val query = impl.queryWithPagination(table, orderBy, lastValue, limit, isDescending)
        return impl.getDataByQuerySuspend(query, targetClass)
    }

    override suspend fun isExist(table: String, key: String, value: String): Pair<Boolean, String> {
        return impl.isExistData(table, key, value)
    }

    override suspend fun isExistById(table: String, id: String): Pair<Boolean, String> {
        return try {
            val response = impl.getDataByIdBaseResponse(table, id, Any::class.java, true)
            when (response) {
                is BaseResponse.Success -> Pair(true, "")
                is BaseResponse.Error -> Pair(false, response.message)
                else -> Pair(false, "Unknown state")
            }
        } catch (e: Exception) {
            Pair(false, e.message ?: "Error checking existence")
        }
    }

    // ==================== Observe ====================

    override fun <T> observeAll(table: String, targetClass: Class<T>): Flow<BaseResponse<List<T>>> {
        return impl.observeDataByPathFlow(table, targetClass)
    }

    override fun <T> observeById(table: String, id: String, targetClass: Class<T>): Flow<BaseResponse<T>> {
        val path = "$table/$id"
        return impl.observeDataByPathSingleFlow(path, targetClass)
    }

    override fun <T> observeByKeyValue(table: String, key: String, value: String, targetClass: Class<T>): Flow<BaseResponse<List<T>>> {
        val query = impl.querySelectTableDataByKeyValue(table, key, value)
        return impl.observeDataByQueryFlow(query, targetClass)
    }

    override fun <T> observeByFilters(
        table: String,
        filters: List<FilterCondition>,
        targetClass: Class<T>,
        orderBy: String,
        isDescending: Boolean,
        limit: Int
    ): Flow<BaseResponse<List<T>>> {
        val query = impl.queryWithMultipleConditions(table, filters, orderBy, isDescending, limit)
        return impl.observeDataByQueryFlow(query, targetClass)
    }
}
