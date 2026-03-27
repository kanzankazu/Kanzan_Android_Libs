package com.kanzankazu.kanzanutil.appwrite.functions

import com.google.gson.Gson
import com.kanzankazu.kanzannetwork.response.kanzanbaseresponse.BaseResponse
import com.kanzankazu.kanzanutil.appwrite.AppwriteClientProvider
import io.appwrite.services.Functions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart

open class AppwriteFunctionsImpl(
    private val functions: Functions = AppwriteClientProvider.getFunctions()
) : AppwriteFunctions {

    private val gson = Gson()

    override suspend fun execute(functionId: String, body: String?): BaseResponse<String> {
        return try {
            val execution = functions.createExecution(
                functionId = functionId,
                body = body ?: ""
            )
            BaseResponse.Success(execution.responseBody)
        } catch (e: Exception) {
            BaseResponse.Error(e.message ?: "Function execute failed")
        }
    }

    override suspend fun <T : Any> executeWithType(
        functionId: String,
        body: String?,
        targetClass: Class<T>
    ): BaseResponse<T> {
        return try {
            val execution = functions.createExecution(
                functionId = functionId,
                body = body ?: ""
            )
            val data = gson.fromJson(execution.responseBody, targetClass)
            BaseResponse.Success(data)
        } catch (e: Exception) {
            BaseResponse.Error(e.message ?: "Function executeWithType failed")
        }
    }

    override fun executeAsFlow(functionId: String, body: String?): Flow<BaseResponse<String>> {
        return flow<BaseResponse<String>> {
            val execution = functions.createExecution(
                functionId = functionId,
                body = body ?: ""
            )
            emit(BaseResponse.Success(execution.responseBody))
        }.onStart {
            emit(BaseResponse.Loading)
        }.catch { e ->
            emit(BaseResponse.Error(e.message ?: "Function executeAsFlow failed"))
        }
    }
}
