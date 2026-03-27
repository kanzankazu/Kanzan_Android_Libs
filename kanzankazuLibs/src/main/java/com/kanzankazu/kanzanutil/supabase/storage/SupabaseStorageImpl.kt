package com.kanzankazu.kanzandatabase.supabase.storage

import com.kanzankazu.kanzandatabase.supabase.SupabaseClientProvider
import com.kanzankazu.kanzannetwork.response.kanzanbaseresponse.BaseResponse
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart

/**
 * Open class implementation of [SupabaseStorage] using Supabase Kotlin SDK v2.
 *
 * Wraps all storage operations with try/catch returning [BaseResponse.Success] or [BaseResponse.Error].
 * Maps SDK [io.github.jan.supabase.storage.BucketItem] to local [FileMetadata] wrapper.
 *
 * @param client The [SupabaseClient] instance, defaults to [SupabaseClientProvider.getClient].
 */
open class SupabaseStorageImpl(
    private val client: SupabaseClient = SupabaseClientProvider.getClient()
) : SupabaseStorage {

    override suspend fun upload(bucket: String, path: String, data: ByteArray): BaseResponse<String> {
        return try {
            client.storage.from(bucket).upload(path, data)
            BaseResponse.Success(path)
        } catch (e: Exception) {
            BaseResponse.Error(e.message ?: "Upload failed")
        }
    }

    override suspend fun download(bucket: String, path: String): BaseResponse<ByteArray> {
        return try {
            val bytes = client.storage.from(bucket).downloadAuthenticated(path)
            BaseResponse.Success(bytes)
        } catch (e: Exception) {
            BaseResponse.Error(e.message ?: "Download failed")
        }
    }

    override suspend fun delete(bucket: String, paths: List<String>): BaseResponse<Unit> {
        return try {
            client.storage.from(bucket).delete(paths)
            BaseResponse.Success(Unit)
        } catch (e: Exception) {
            BaseResponse.Error(e.message ?: "Delete failed")
        }
    }

    override suspend fun getPublicUrl(bucket: String, path: String): BaseResponse<String> {
        return try {
            val url = client.storage.from(bucket).publicUrl(path)
            BaseResponse.Success(url)
        } catch (e: Exception) {
            BaseResponse.Error(e.message ?: "Get public URL failed")
        }
    }

    override suspend fun list(bucket: String, prefix: String): BaseResponse<List<FileMetadata>> {
        return try {
            val items = client.storage.from(bucket).list(prefix)
            val metadata = items.map { item ->
                FileMetadata(
                    name = item.name,
                    size = null,
                    createdAt = item.createdAt?.toString(),
                    updatedAt = item.updatedAt?.toString()
                )
            }
            BaseResponse.Success(metadata)
        } catch (e: Exception) {
            BaseResponse.Error(e.message ?: "List failed")
        }
    }

    override fun uploadAsFlow(bucket: String, path: String, data: ByteArray): Flow<BaseResponse<String>> {
        return flow<BaseResponse<String>> {
            client.storage.from(bucket).upload(path, data)
            emit(BaseResponse.Success(path))
        }.onStart {
            emit(BaseResponse.Loading)
        }.catch { e ->
            emit(BaseResponse.Error(e.message ?: "Upload failed"))
        }
    }
}
