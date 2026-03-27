package com.kanzankazu.kanzanutil.appwrite.database

import com.kanzankazu.kanzannetwork.response.kanzanbaseresponse.BaseResponse
import com.kanzankazu.kanzanutil.appwrite.AppwriteFilterCondition
import kotlinx.coroutines.flow.Flow

interface AppwriteDatabase {
    suspend fun createDocument(databaseId: String, collectionId: String, data: Map<String, Any>, documentId: String? = null): BaseResponse<Map<String, Any>>
    suspend fun listDocuments(databaseId: String, collectionId: String, filters: List<AppwriteFilterCondition> = emptyList()): BaseResponse<List<Map<String, Any>>>
    suspend fun getDocument(databaseId: String, collectionId: String, documentId: String): BaseResponse<Map<String, Any>>
    suspend fun updateDocument(databaseId: String, collectionId: String, documentId: String, data: Map<String, Any>): BaseResponse<Map<String, Any>>
    suspend fun deleteDocument(databaseId: String, collectionId: String, documentId: String): BaseResponse<Unit>
    suspend fun listDocumentsWithPagination(databaseId: String, collectionId: String, limit: Int, offset: Int, filters: List<AppwriteFilterCondition> = emptyList()): BaseResponse<List<Map<String, Any>>>
    suspend fun listDocumentsWithOrder(databaseId: String, collectionId: String, orderAttributes: List<String>, ascending: Boolean = true, filters: List<AppwriteFilterCondition> = emptyList()): BaseResponse<List<Map<String, Any>>>
    fun listDocumentsAsFlow(databaseId: String, collectionId: String, filters: List<AppwriteFilterCondition> = emptyList()): Flow<BaseResponse<List<Map<String, Any>>>>
}
