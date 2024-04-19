package com.kanzankazu.kanzandatabase.firebase.realtimedatabase

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query
import com.kanzankazu.kanzannetwork.response.kanzanbaseresponse.BaseResponse
import kotlinx.coroutines.flow.Flow

interface RealtimeDatabase {
    fun getRootRefKt(tableChildKey: String): DatabaseReference

    fun createPrimaryKeyData(tableChildKey: String): String

    fun <T> getDataByIdBaseResponse(
        tableChildKey: String,
        tableChildKeyId: String,
        targetClass: Class<T>,
        isSingleCall: Boolean,
        function: (BaseResponse<T>) -> Unit,
    )

    fun <T> setDataBaseResponse(
        tableChildKey: String,
        tableChildKeyId: String,
        value: T,
        function: (BaseResponse<String>) -> Unit,
    )

    fun <T> updateDataBaseResponse(
        tableChildKey: String,
        tableChildKeyId: String,
        value: T,
        function: (BaseResponse<String>) -> Unit,
    )

    fun getDataAllBaseResponse(
        tableChildKey: String,
        isSingleCall: Boolean,
        function: (BaseResponse<DataSnapshot>) -> Unit,
    )

    fun getDataByQuery(
        query: Query,
        onDataChangedListener: (dataSnapshot: DataSnapshot) -> Unit = {},
        onCancelledListener: (String) -> Unit = {},
        isSingleCall: Boolean,
    )

    fun getDataByQueryKanzanBaseResponse(
        query: Query,
        isSingleCall: Boolean,
        function: (BaseResponse<DataSnapshot>) -> Unit,
    )

    fun isExistData(
        tableChildKey: String,
        tableChildPrimaryKeyId: String,
        function: (pair: Pair<Boolean, String>) -> Unit,
    )

    fun querySelectTableDataByKeyValue(
        tableChildKey: String,
        rowChildKey: String,
        rowChildValue: Boolean,
    ): Query

    fun querySelectTableDataByKeyValue(tableChildKey: String, rowChildKey: String, rowChildValue: Double): Query

    fun querySelectTableDataByKeyValue(tableChildKey: String, rowChildKey: String, rowChildValue: Int): Query

    fun querySelectTableDataByKeyValue(tableChildKey: String, rowChildKey: String, rowChildValue: String): Query

    fun querySelectTableDataByKeyValue(tableChildKey: String, rowChildKeySlashValue: String): Query

    fun querySelectTableDataByPrimaryKey(tableChildKey: String, tableChildPrimaryKeyId: String): Query

    fun querySelectTableLimit(tableChildKey: String, limit: Int = 10): Query
    fun querySelectTableValueWithMoreLessDigits(
        tableChildKey: String,
        rowChildKey: String,
        digits: Any,
        isMore: Boolean = true,
    ): Query

    fun querySelectTableValueWithStart(
        tableChildKey: String,
        rowChildKey: String,
        string: String,
    ): Query

    fun removeDataBaseResponse(
        tableChildKey: String,
        tableChildKeyId: String,
        function: (BaseResponse<String>) -> Unit,
    )

    fun updateDataBaseResponse(
        tableChildKey: String,
        tableChildKeyId: String,
        rowChildKey: String,
        rowChildValue: String,
        function: (BaseResponse<String>) -> Unit,
    )

    suspend fun <T> getDataByIdBaseResponse(
        tableChildKey: String,
        tableChildKeyId: String,
        targetClass: Class<T>,
        isSingleCall: Boolean,
    ): BaseResponse<T>

    suspend fun <T> getDataByIdBaseResponseFlow(tableChildKey: String, tableChildKeyId: String, targetClass: Class<T>, isSingleCall: Boolean): Flow<BaseResponse<T>>

    suspend fun <T> setDataBaseResponse(tableChildKey: String, tableChildKeyId: String, value: T): BaseResponse<String>

    suspend fun <T> updateDataBaseResponse(tableChildKey: String, tableChildKeyId: String, value: T): BaseResponse<String>

    suspend fun getDataAllBaseResponse(tableChildKey: String, isSingleCall: Boolean): BaseResponse<DataSnapshot>

    suspend fun getDataByQueryKanzanBaseResponse(query: Query, isSingleCall: Boolean): BaseResponse<DataSnapshot>

    suspend fun isExistData(tableChildKey: String, rowChildKey: String, rowChildValue: String): Pair<Boolean, String>

    suspend fun removeDataBaseResponse(tableChildKey: String, tableChildKeyId: String): BaseResponse<String>

    suspend fun updateDataBaseResponse(tableChildKey: String, tableChildKeyId: String, rowChildKey: String, rowChildValue: String): BaseResponse<String>
}
