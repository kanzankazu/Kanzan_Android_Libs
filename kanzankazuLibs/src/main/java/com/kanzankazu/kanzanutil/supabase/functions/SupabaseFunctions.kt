package com.kanzankazu.kanzandatabase.supabase.functions

import com.kanzankazu.kanzannetwork.response.kanzanbaseresponse.BaseResponse
import kotlinx.coroutines.flow.Flow

interface SupabaseFunctions {
    suspend fun invoke(functionName: String, body: String? = null): BaseResponse<String>
    suspend fun <T : Any> invokeWithType(functionName: String, body: String? = null, targetClass: Class<T>): BaseResponse<T>
    fun invokeAsFlow(functionName: String, body: String? = null): Flow<BaseResponse<String>>
}
