package com.kanzankazu.kanzanutil.appwrite.functions

import com.kanzankazu.kanzannetwork.response.kanzanbaseresponse.BaseResponse
import kotlinx.coroutines.flow.Flow

interface AppwriteFunctions {
    suspend fun execute(functionId: String, body: String? = null): BaseResponse<String>
    suspend fun <T : Any> executeWithType(functionId: String, body: String? = null, targetClass: Class<T>): BaseResponse<T>
    fun executeAsFlow(functionId: String, body: String? = null): Flow<BaseResponse<String>>
}
