package com.kanzankazu.kanzandatabase.supabase.storage

import com.kanzankazu.kanzannetwork.response.kanzanbaseresponse.BaseResponse
import kotlinx.coroutines.flow.Flow

data class FileMetadata(
    val name: String,
    val size: Long?,
    val createdAt: String?,
    val updatedAt: String?
)

interface SupabaseStorage {
    suspend fun upload(bucket: String, path: String, data: ByteArray): BaseResponse<String>
    suspend fun download(bucket: String, path: String): BaseResponse<ByteArray>
    suspend fun delete(bucket: String, paths: List<String>): BaseResponse<Unit>
    suspend fun getPublicUrl(bucket: String, path: String): BaseResponse<String>
    suspend fun list(bucket: String, prefix: String = ""): BaseResponse<List<FileMetadata>>
    fun uploadAsFlow(bucket: String, path: String, data: ByteArray): Flow<BaseResponse<String>>
}
