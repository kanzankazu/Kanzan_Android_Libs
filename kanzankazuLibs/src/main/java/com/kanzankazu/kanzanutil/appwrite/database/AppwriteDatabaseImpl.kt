package com.kanzankazu.kanzanutil.appwrite.database

import com.kanzankazu.kanzannetwork.response.kanzanbaseresponse.BaseResponse
import com.kanzankazu.kanzanutil.appwrite.AppwriteClientProvider
import com.kanzankazu.kanzanutil.appwrite.AppwriteFilterCondition
import com.kanzankazu.kanzanutil.appwrite.AppwriteFilterOperator
import io.appwrite.ID
import io.appwrite.Query
import io.appwrite.services.Databases
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart

open class AppwriteDatabaseImpl(
    private val databases: Databases = AppwriteClientProvider.getDatabases()
) : AppwriteDatabase {

    override suspend fun createDocument(
        databaseId: String,
        collectionId: String,
        data: Map<String, Any>,
        documentId: String?
    ): BaseResponse<Map<String, Any>> {
        return try {
            val doc = databases.createDocument(
                databaseId = databaseId,
                collectionId = collectionId,
                documentId = documentId ?: ID.unique(),
                data = data
            )
            BaseResponse.Success(doc.data)
        } catch (e: Exception) {
            BaseResponse.Error(e.message ?: "Create document failed")
        }
    }

    override suspend fun listDocuments(
        databaseId: String,
        collectionId: String,
        filters: List<AppwriteFilterCondition>
    ): BaseResponse<List<Map<String, Any>>> {
        return try {
            val queries = buildQueries(filters)
            val result = databases.listDocuments(
                databaseId = databaseId,
                collectionId = collectionId,
                queries = queries
            )
            val documents = result.documents.map { it.data }
            BaseResponse.Success(documents)
        } catch (e: Exception) {
            BaseResponse.Error(e.message ?: "List documents failed")
        }
    }

    override suspend fun getDocument(
        databaseId: String,
        collectionId: String,
        documentId: String
    ): BaseResponse<Map<String, Any>> {
        return try {
            val doc = databases.getDocument(
                databaseId = databaseId,
                collectionId = collectionId,
                documentId = documentId
            )
            BaseResponse.Success(doc.data)
        } catch (e: Exception) {
            BaseResponse.Error(e.message ?: "Get document failed")
        }
    }

    override suspend fun updateDocument(
        databaseId: String,
        collectionId: String,
        documentId: String,
        data: Map<String, Any>
    ): BaseResponse<Map<String, Any>> {
        return try {
            val doc = databases.updateDocument(
                databaseId = databaseId,
                collectionId = collectionId,
                documentId = documentId,
                data = data
            )
            BaseResponse.Success(doc.data)
        } catch (e: Exception) {
            BaseResponse.Error(e.message ?: "Update document failed")
        }
    }

    override suspend fun deleteDocument(
        databaseId: String,
        collectionId: String,
        documentId: String
    ): BaseResponse<Unit> {
        return try {
            databases.deleteDocument(
                databaseId = databaseId,
                collectionId = collectionId,
                documentId = documentId
            )
            BaseResponse.Success(Unit)
        } catch (e: Exception) {
            BaseResponse.Error(e.message ?: "Delete document failed")
        }
    }

    override suspend fun listDocumentsWithPagination(
        databaseId: String,
        collectionId: String,
        limit: Int,
        offset: Int,
        filters: List<AppwriteFilterCondition>
    ): BaseResponse<List<Map<String, Any>>> {
        return try {
            val queries = buildQueries(filters).toMutableList()
            queries.add(Query.limit(limit))
            queries.add(Query.offset(offset))
            val result = databases.listDocuments(
                databaseId = databaseId,
                collectionId = collectionId,
                queries = queries
            )
            val documents = result.documents.map { it.data }
            BaseResponse.Success(documents)
        } catch (e: Exception) {
            BaseResponse.Error(e.message ?: "List documents with pagination failed")
        }
    }

    override suspend fun listDocumentsWithOrder(
        databaseId: String,
        collectionId: String,
        orderAttributes: List<String>,
        ascending: Boolean,
        filters: List<AppwriteFilterCondition>
    ): BaseResponse<List<Map<String, Any>>> {
        return try {
            val queries = buildQueries(filters).toMutableList()
            orderAttributes.forEach { attr ->
                queries.add(if (ascending) Query.orderAsc(attr) else Query.orderDesc(attr))
            }
            val result = databases.listDocuments(
                databaseId = databaseId,
                collectionId = collectionId,
                queries = queries
            )
            val documents = result.documents.map { it.data }
            BaseResponse.Success(documents)
        } catch (e: Exception) {
            BaseResponse.Error(e.message ?: "List documents with order failed")
        }
    }

    override fun listDocumentsAsFlow(
        databaseId: String,
        collectionId: String,
        filters: List<AppwriteFilterCondition>
    ): Flow<BaseResponse<List<Map<String, Any>>>> {
        return flow<BaseResponse<List<Map<String, Any>>>> {
            val queries = buildQueries(filters)
            val result = databases.listDocuments(
                databaseId = databaseId,
                collectionId = collectionId,
                queries = queries
            )
            val documents = result.documents.map { it.data }
            emit(BaseResponse.Success(documents))
        }.onStart {
            emit(BaseResponse.Loading)
        }.catch { e ->
            emit(BaseResponse.Error(e.message ?: "List documents as flow failed"))
        }
    }

    protected fun buildQueries(filters: List<AppwriteFilterCondition>): List<String> {
        return filters.map { condition ->
            val attr = condition.attribute
            val value = condition.value
            when (condition.operator) {
                AppwriteFilterOperator.EQUAL -> Query.equal(attr, value.toString())
                AppwriteFilterOperator.NOT_EQUAL -> Query.notEqual(attr, value.toString())
                AppwriteFilterOperator.GREATER_THAN -> Query.greaterThan(attr, value.toString())
                AppwriteFilterOperator.GREATER_THAN_OR_EQUAL -> Query.greaterThanEqual(attr, value.toString())
                AppwriteFilterOperator.LESS_THAN -> Query.lessThan(attr, value.toString())
                AppwriteFilterOperator.LESS_THAN_OR_EQUAL -> Query.lessThanEqual(attr, value.toString())
                AppwriteFilterOperator.SEARCH -> Query.search(attr, value.toString())
                AppwriteFilterOperator.IS_NULL -> Query.isNull(attr)
                AppwriteFilterOperator.IS_NOT_NULL -> Query.isNotNull(attr)
                AppwriteFilterOperator.BETWEEN -> {
                    val parts = value.toString().split(",")
                    Query.between(attr, parts.getOrElse(0) { "" }, parts.getOrElse(1) { "" })
                }
                AppwriteFilterOperator.STARTS_WITH -> Query.startsWith(attr, value.toString())
                AppwriteFilterOperator.ENDS_WITH -> Query.endsWith(attr, value.toString())
                AppwriteFilterOperator.CONTAINS -> Query.contains(attr, value.toString())
            }
        }
    }
}
