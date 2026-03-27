package com.kanzankazu.kanzanutil.appwrite.storage

import com.kanzankazu.kanzannetwork.response.kanzanbaseresponse.BaseResponse
import kotlinx.coroutines.flow.Flow

data class AppwriteFileMetadata(
    val id: String,
    val name: String,
    val sizeOriginal: Long,
    val mimeType: String,
    val createdAt: String
)

interface AppwriteStorage {
    suspend fun upload(bucketId: String, filePath: String, fileId: String? = null): BaseResponse<String>
    suspend fun uploadBytes(bucketId: String, fileName: String, data: ByteArray, fileId: String? = null): BaseResponse<String>
    suspend fun download(bucketId: String, fileId: String): BaseResponse<ByteArray>
    suspend fun delete(bucketId: String, fileId: String): BaseResponse<Unit>
    suspend fun getFilePreview(bucketId: String, fileId: String): BaseResponse<ByteArray>
    suspend fun getFileViewUrl(bucketId: String, fileId: String): BaseResponse<String>
    suspend fun list(bucketId: String): BaseResponse<List<AppwriteFileMetadata>>
    fun uploadAsFlow(bucketId: String, filePath: String, fileId: String? = null): Flow<BaseResponse<String>>
}
