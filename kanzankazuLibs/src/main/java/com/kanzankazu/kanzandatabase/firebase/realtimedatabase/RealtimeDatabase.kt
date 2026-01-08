package com.kanzankazu.kanzandatabase.firebase.realtimedatabase

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query
import com.kanzankazu.kanzandatabase.firebase.FilterCondition
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

    /**
     * Query to select data from a table by its primary key.
     * SQL Equivalent: SELECT * FROM `tableChildKey` WHERE id = 'tableChildPrimaryKeyId'
     *
     * @param tableChildKey The name of the table to query
     * @param tableChildPrimaryKeyId The primary key value to search for
     * @return Query object that can be used to fetch the data
     */
    fun querySelectTableDataByPrimaryKey(tableChildKey: String, tableChildPrimaryKeyId: String): Query

    /**
     * Query to select data from a table where a specific column equals a string value.
     * SQL Equivalent: SELECT * FROM `tableChildKey` WHERE `rowChildKey` = 'rowChildValue'
     *
     * @param tableChildKey The name of the table to query
     * @param rowChildKey The column name to filter by
     * @param rowChildValue The string value to match
     * @return Query object that can be used to fetch the data
     */
    fun querySelectTableDataByKeyValue(tableChildKey: String, rowChildKey: String, rowChildValue: String): Query

    /**
     * Query to select data from a table where a specific column equals an integer value.
     * SQL Equivalent: SELECT * FROM `tableChildKey` WHERE `rowChildKey` = rowChildValue
     *
     * @param tableChildKey The name of the table to query
     * @param rowChildKey The column name to filter by
     * @param rowChildValue The integer value to match
     * @return Query object that can be used to fetch the data
     */
    fun querySelectTableDataByKeyValue(tableChildKey: String, rowChildKey: String, rowChildValue: Int): Query

    /**
     * Query to select data from a table where a specific column equals a double value.
     * SQL Equivalent: SELECT * FROM `tableChildKey` WHERE `rowChildKey` = rowChildValue
     *
     * @param tableChildKey The name of the table to query
     * @param rowChildKey The column name to filter by
     * @param rowChildValue The double value to match
     * @return Query object that can be used to fetch the data
     */
    fun querySelectTableDataByKeyValue(tableChildKey: String, rowChildKey: String, rowChildValue: Double): Query

    /**
     * Query to select data from a table where a specific column equals a boolean value.
     * SQL Equivalent: SELECT * FROM `tableChildKey` WHERE `rowChildKey` = rowChildValue
     *
     * @param tableChildKey The name of the table to query
     * @param rowChildKey The column name to filter by
     * @param rowChildValue The boolean value to match
     * @return Query object that can be used to fetch the data
     */
    fun querySelectTableDataByKeyValue(tableChildKey: String, rowChildKey: String, rowChildValue: Boolean): Query

    /**
     * Query to select data from a table ordered by a specific key.
     * SQL Equivalent: SELECT * FROM `tableChildKey` ORDER BY `rowChildKeySlashValue`
     *
     * @param tableChildKey The name of the table to query
     * @param rowChildKeySlashValue The column name to order by
     * @return Query object that can be used to fetch the ordered data
     */
    fun querySelectTableDataByKeyValue(tableChildKey: String, rowChildKeySlashValue: String): Query

    /**
     * Query to select a limited number of records from a table.
     * SQL Equivalent: SELECT * FROM `tableChildKey` ORDER BY key LIMIT limit
     *
     * @param tableChildKey The name of the table to query
     * @param limit The maximum number of records to return
     * @return Query object that can be used to fetch the limited data
     */
    fun querySelectTableLimit(tableChildKey: String, limit: Int = 10): Query

    /**
     * Query to select data where a numeric column is greater than or less than a value.
     * SQL Equivalent:
     *   If isMore: SELECT * FROM `tableChildKey` WHERE `rowChildKey` > digits
     *   Else: SELECT * FROM `tableChildKey` WHERE `rowChildKey` < digits
     *
     * @param tableChildKey The name of the table to query
     * @param rowChildKey The column name to filter by
     * @param digits The numeric value to compare against
     * @param isMore If true, find values greater than digits; if false, find values less than digits
     * @return Query object that can be used to fetch the filtered data
     */
    fun querySelectTableValueWithMoreLessDigits(
        tableChildKey: String,
        rowChildKey: String,
        digits: Any,
        isMore: Boolean = true,
    ): Query

    /**
     * Query to select data where a specific column starts with the given string.
     * SQL Equivalent: SELECT * FROM `tableChildKey` WHERE `rowChildKey` LIKE 'string%'
     *
     * @param tableChildKey The name of the table to query
     * @param rowChildKey The column name to search in
     * @param string The prefix string to search for
     * @return Query object that can be used to fetch the matching data
     */
    fun querySelectTableValueWithStart(
        tableChildKey: String,
        rowChildKey: String,
        string: String,
    ): Query

    /**
     * Query to perform complex filtering with multiple conditions.
     * SQL Equivalent:
     *   SELECT * FROM `tableChildKey`
     *   WHERE `filters[0].key` = 'filters[0].value'
     *   AND `filters[1].key` > 'filters[1].value'
     *   ORDER BY `orderBy` ASC|DESC
     *   LIMIT limit
     *
     * @param tableChildKey The name of the table to query
     * @param filters List of FilterCondition to apply
     * @param orderBy Field to order by (default: "createdAt")
     * @param isDescending Sort order (default: false = ASC, true = DESC)
     * @param limit Maximum number of records to return (default: 20, use -1 for no limit)
     * @return Query object with all conditions applied
     */
    fun queryWithMultipleConditions(tableChildKey: String, filters: List<FilterCondition>, orderBy: String = "createdAt", isDescending: Boolean = false, limit: Int = 20): Query

    /**
     * Query to perform pagination with cursor-based pagination.
     * SQL Equivalent:
     *   SELECT * FROM `tableChildKey`
     *   WHERE `orderBy` > lastValue
     *   ORDER BY `orderBy` ASC|DESC
     *   LIMIT limit
     *
     * @param tableChildKey The name of the table to query
     * @param orderBy Field to order by (default: "createdAt")
     * @param lastValue The last value of the previous page (null for first page)
     * @param limit Number of records per page (default: 20)
     * @param isDescending Sort order (default: false = ASC, true = DESC)
     * @return Query object for pagination
     */
    fun queryWithPagination(tableChildKey: String, orderBy: String = "createdAt", lastValue: Any? = null, limit: Int = 20, isDescending: Boolean = false): Query

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

    suspend fun getDataByQueryKanzanBaseResponse(query: Query): BaseResponse<DataSnapshot>

    suspend fun getDataByQueryKanzanBaseResponseFlow(query: Query): Flow<BaseResponse<DataSnapshot>>

    suspend fun isExistData(tableChildKey: String, rowChildKey: String, rowChildValue: String): Pair<Boolean, String>

    suspend fun removeDataBaseResponse(tableChildKey: String, tableChildKeyId: String): BaseResponse<String>

    suspend fun updateDataBaseResponse(tableChildKey: String, tableChildKeyId: String, rowChildKey: String, rowChildValue: String): BaseResponse<String>
}
