package com.kanzankazu.kanzandatabase.supabase.functions

import com.kanzankazu.kanzandatabase.supabase.SupabaseClientProvider
import com.kanzankazu.kanzannetwork.response.kanzanbaseresponse.BaseResponse
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.functions.functions
import io.ktor.client.call.body
import io.ktor.http.ContentType
import io.ktor.http.content.TextContent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.serialization.json.Json

open class SupabaseFunctionsImpl(
    private val client: SupabaseClient = SupabaseClientProvider.getClient()
) : SupabaseFunctions {

    override suspend fun invoke(functionName: String, body: String?): BaseResponse<String> {
        return try {
            val response = client.functions.invoke(functionName) {
                if (body != null) {
                    this.body = TextContent(body, ContentType.Application.Json)
                }
            }
            val responseBody = response.body<String>()
            BaseResponse.Success(responseBody)
        } catch (e: Exception) {
            BaseResponse.Error(e.message ?: "Function invoke failed")
        }
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun <T : Any> invokeWithType(
        functionName: String,
        body: String?,
        targetClass: Class<T>
    ): BaseResponse<T> {
        return try {
            val response = client.functions.invoke(functionName) {
                if (body != null) {
                    this.body = TextContent(body, ContentType.Application.Json)
                }
            }
            val responseBody = response.body<String>()
            val data = Json.decodeFromString(
                kotlinx.serialization.serializer(targetClass.kotlin.java),
                responseBody
            ) as T
            BaseResponse.Success(data)
        } catch (e: Exception) {
            BaseResponse.Error(e.message ?: "Function invokeWithType failed")
        }
    }

    override fun invokeAsFlow(functionName: String, body: String?): Flow<BaseResponse<String>> {
        return flow<BaseResponse<String>> {
            val response = client.functions.invoke(functionName) {
                if (body != null) {
                    this.body = TextContent(body, ContentType.Application.Json)
                }
            }
            val responseBody = response.body<String>()
            emit(BaseResponse.Success(responseBody))
        }.onStart {
            emit(BaseResponse.Loading)
        }.catch { e ->
            emit(BaseResponse.Error(e.message ?: "Function invokeAsFlow failed"))
        }
    }
}
