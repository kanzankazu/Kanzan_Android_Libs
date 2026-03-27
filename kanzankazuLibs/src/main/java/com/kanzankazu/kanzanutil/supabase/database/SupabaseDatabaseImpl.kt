package com.kanzankazu.kanzandatabase.supabase.database

import com.kanzankazu.kanzandatabase.supabase.SupabaseClientProvider
import com.kanzankazu.kanzandatabase.supabase.SupabaseFilterCondition
import com.kanzankazu.kanzandatabase.supabase.SupabaseFilterOperator
import com.kanzankazu.kanzannetwork.response.kanzanbaseresponse.BaseResponse
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.postgrest.query.PostgrestFilterBuilder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart

/**
 * Open class implementation of [SupabaseDatabase] using Supabase Kotlin SDK v2.
 *
 * Note: Since the interface uses regular generics (not reified), select operations
 * return raw JSON data from Postgrest. For type-safe deserialization, use the
 * inline reified extension functions provided alongside this class.
 *
 * @param client The [SupabaseClient] instance, defaults to [SupabaseClientProvider.getClient].
 */
open class SupabaseDatabaseImpl(
    private val client: SupabaseClient = SupabaseClientProvider.getClient()
) : SupabaseDatabase {

    override suspend fun <T : Any> insert(table: String, data: T): BaseResponse<T> {
        return try {
            client.from(table).insert(data)
            BaseResponse.Success(data)
        } catch (e: Exception) {
            BaseResponse.Error(e.message ?: "Insert failed")
        }
    }

    override suspend fun <T : Any> upsert(table: String, data: T): BaseResponse<T> {
        return try {
            client.from(table).upsert(data)
            BaseResponse.Success(data)
        } catch (e: Exception) {
            BaseResponse.Error(e.message ?: "Upsert failed")
        }
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun <T : Any> select(
        table: String,
        filters: List<SupabaseFilterCondition>
    ): BaseResponse<List<T>> {
        return try {
            val result = client.from(table).select {
                filter { applyFilters(filters) }
            }
            val data = result.data as List<T>
            BaseResponse.Success(data)
        } catch (e: Exception) {
            BaseResponse.Error(e.message ?: "Select failed")
        }
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun <T : Any> selectById(
        table: String,
        column: String,
        id: Any
    ): BaseResponse<T> {
        return try {
            val result = client.from(table).select {
                filter { eq(column, id.toString()) }
            }
            val data = result.data as T
            BaseResponse.Success(data)
        } catch (e: Exception) {
            BaseResponse.Error(e.message ?: "SelectById failed")
        }
    }

    override suspend fun <T : Any> update(
        table: String,
        data: T,
        filters: List<SupabaseFilterCondition>
    ): BaseResponse<T> {
        return try {
            client.from(table).update(data) {
                filter { applyFilters(filters) }
            }
            BaseResponse.Success(data)
        } catch (e: Exception) {
            BaseResponse.Error(e.message ?: "Update failed")
        }
    }

    override suspend fun delete(
        table: String,
        filters: List<SupabaseFilterCondition>
    ): BaseResponse<Unit> {
        return try {
            client.from(table).delete {
                filter { applyFilters(filters) }
            }
            BaseResponse.Success(Unit)
        } catch (e: Exception) {
            BaseResponse.Error(e.message ?: "Delete failed")
        }
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun <T : Any> selectWithPagination(
        table: String,
        page: Int,
        pageSize: Int,
        filters: List<SupabaseFilterCondition>
    ): BaseResponse<List<T>> {
        return try {
            val from = page * pageSize
            val to = from + pageSize - 1
            val result = client.from(table).select {
                filter { applyFilters(filters) }
                range(from.toLong(), to.toLong())
            }
            val data = result.data as List<T>
            BaseResponse.Success(data)
        } catch (e: Exception) {
            BaseResponse.Error(e.message ?: "SelectWithPagination failed")
        }
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun <T : Any> selectWithOrder(
        table: String,
        column: String,
        ascending: Boolean,
        filters: List<SupabaseFilterCondition>
    ): BaseResponse<List<T>> {
        return try {
            val result = client.from(table).select {
                filter { applyFilters(filters) }
                order(column, if (ascending) Order.ASCENDING else Order.DESCENDING)
            }
            val data = result.data as List<T>
            BaseResponse.Success(data)
        } catch (e: Exception) {
            BaseResponse.Error(e.message ?: "SelectWithOrder failed")
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> selectAsFlow(
        table: String,
        filters: List<SupabaseFilterCondition>
    ): Flow<BaseResponse<List<T>>> {
        return flow<BaseResponse<List<T>>> {
            val result = client.from(table).select {
                filter { applyFilters(filters) }
            }
            val data = result.data as List<T>
            emit(BaseResponse.Success(data))
        }.onStart {
            emit(BaseResponse.Loading)
        }.catch { e ->
            emit(BaseResponse.Error(e.message ?: "SelectAsFlow failed"))
        }
    }

    /**
     * Applies a list of [SupabaseFilterCondition] to a Postgrest filter builder.
     * Maps each [SupabaseFilterOperator] to the corresponding Postgrest filter method.
     */
    protected fun PostgrestFilterBuilder.applyFilters(filters: List<SupabaseFilterCondition>) {
        filters.forEach { condition ->
            val col = condition.column
            val value = condition.value
            when (condition.operator) {
                SupabaseFilterOperator.EQ -> eq(col, value.toString())
                SupabaseFilterOperator.NEQ -> neq(col, value.toString())
                SupabaseFilterOperator.GT -> gt(col, value.toString())
                SupabaseFilterOperator.GTE -> gte(col, value.toString())
                SupabaseFilterOperator.LT -> lt(col, value.toString())
                SupabaseFilterOperator.LTE -> lte(col, value.toString())
                SupabaseFilterOperator.LIKE -> like(col, value.toString())
                SupabaseFilterOperator.ILIKE -> ilike(col, value.toString())
                SupabaseFilterOperator.IN -> isIn(
                    col,
                    if (value is List<*>) value.map { it.toString() } else listOf(value.toString())
                )
                SupabaseFilterOperator.IS -> exact(col, value.toString())
            }
        }
    }
}
