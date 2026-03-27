package com.kanzankazu.kanzanutil.appwrite.storage

import com.kanzankazu.kanzannetwork.response.kanzanbaseresponse.BaseResponse
import com.kanzankazu.kanzanutil.appwrite.AppwriteClientProvider
import io.appwrite.ID
import io.appwrite.models.InputFile
import io.appwrite.services.Storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import java.io.File

open class AppwriteStorageImpl(
    private val storage: Storage = AppwriteClientProvider.getStorage()
) : AppwriteStorage {

    override suspend fun upload(bucketId: String, filePath: String, fileId: String?): BaseResponse<String> {
        return try {
            val file = storage.createFile(
                bucketId = bucketId,
                fileId = fileId ?: ID.unique(),
                file = InputFile.fromPath(filePath)
            )
            BaseResponse.Success(file.id)
        } catch (e: Exception) {
            BaseResponse.Error(e.message ?: "Upload failed")
        }
    }

    override suspend fun uploadBytes(bucketId: String, fileName: String, data: ByteArray, fileId: String?): BaseResponse<String> {
        var tempFile: File? = null
        return try {
            tempFile = File.createTempFile("appwrite_upload_", "_$fileName")
            tempFile.writeBytes(data)
            val file = storage.createFile(
                bucketId = bucketId,
                fileId = fileId ?: ID.unique(),
                file = InputFile.fromPath(tempFile.path)
            )
            BaseResponse.Success(file.id)
        } catch (e: Exception) {
            BaseResponse.Error(e.message ?: "Upload bytes failed")
        } finally {
            tempFile?.delete()
        }
    }

    override suspend fun download(bucketId: String, fileId: String): BaseResponse<ByteArray> {
        return try {
            val bytes = storage.getFileDownload(bucketId = bucketId, fileId = fileId)
            BaseResponse.Success(bytes)
        } catch (e: Exception) {
            BaseResponse.Error(e.message ?: "Download failed")
        }
    }

    override suspend fun delete(bucketId: String, fileId: String): BaseResponse<Unit> {
        return try {
            storage.deleteFile(bucketId = bucketId, fileId = fileId)
            BaseResponse.Success(Unit)
        } catch (e: Exception) {
            BaseResponse.Error(e.message ?: "Delete failed")
        }
    }

    override suspend fun getFilePreview(bucketId: String, fileId: String): BaseResponse<ByteArray> {
        return try {
            val bytes = storage.getFilePreview(bucketId = bucketId, fileId = fileId)
            BaseResponse.Success(bytes)
        } catch (e: Exception) {
            BaseResponse.Error(e.message ?: "Get file preview failed")
        }
    }

    override suspend fun getFileViewUrl(bucketId: String, fileId: String): BaseResponse<String> {
        return try {
            val endpoint = AppwriteClientProvider.getClient().endPoint
            val url = "$endpoint/storage/buckets/$bucketId/files/$fileId/view"
            BaseResponse.Success(url)
        } catch (e: Exception) {
            BaseResponse.Error(e.message ?: "Get file view URL failed")
        }
    }

    override suspend fun list(bucketId: String): BaseResponse<List<AppwriteFileMetadata>> {
        return try {
            val result = storage.listFiles(bucketId = bucketId)
            val metadata = result.files.map { file ->
                AppwriteFileMetadata(
                    id = file.id,
                    name = file.name,
                    sizeOriginal = file.sizeOriginal,
                    mimeType = file.mimeType,
                    createdAt = file.createdAt
                )
            }
            BaseResponse.Success(metadata)
        } catch (e: Exception) {
            BaseResponse.Error(e.message ?: "List files failed")
        }
    }

    override fun uploadAsFlow(bucketId: String, filePath: String, fileId: String?): Flow<BaseResponse<String>> {
        return flow<BaseResponse<String>> {
            val file = storage.createFile(
                bucketId = bucketId,
                fileId = fileId ?: ID.unique(),
                file = InputFile.fromPath(filePath)
            )
            emit(BaseResponse.Success(file.id))
        }.onStart {
            emit(BaseResponse.Loading)
        }.catch { e ->
            emit(BaseResponse.Error(e.message ?: "Upload as flow failed"))
        }
    }
}
