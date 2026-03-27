package com.kanzankazu.kanzandatabase.supabase.realtime

import com.kanzankazu.kanzandatabase.supabase.SupabaseClientProvider
import com.kanzankazu.kanzannetwork.response.kanzanbaseresponse.BaseResponse
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.realtime
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

/**
 * Open class implementation of [SupabaseRealtime] using Supabase Kotlin SDK v2.
 *
 * Uses [callbackFlow] with [awaitClose] for channel lifecycle management,
 * consistent with the existing [com.kanzankazu.kanzandatabase.firebase.realtimedatabase.RealtimeDatabaseImpl] pattern.
 *
 * Target classes for [subscribeToTable] must be annotated with `@kotlinx.serialization.Serializable`.
 *
 * @param client The [SupabaseClient] instance, defaults to [SupabaseClientProvider.getClient].
 */
open class SupabaseRealtimeImpl(
    private val client: SupabaseClient = SupabaseClientProvider.getClient()
) : SupabaseRealtime {

    override fun <T : Any> subscribeToTable(
        table: String,
        targetClass: Class<T>
    ): Flow<BaseResponse<T>> = callbackFlow {
        try {
            val channel = client.realtime.channel(table)
            val changeFlow = channel.postgresChangeFlow<PostgresAction>(schema = "public") {
                this.table = table
            }

            channel.subscribe()

            val job = launch {
                changeFlow.collect { action ->
                    try {
                        val record = when (action) {
                            is PostgresAction.Insert -> action.record
                            is PostgresAction.Update -> action.record
                            is PostgresAction.Delete -> action.oldRecord
                            else -> null
                        }
                        if (record != null) {
                            val jsonString = record.toString()
                            @Suppress("UNCHECKED_CAST")
                            val data = Json.decodeFromString(
                                kotlinx.serialization.serializer(targetClass.kotlin.java),
                                jsonString
                            ) as T
                            trySend(BaseResponse.Success(data))
                        }
                    } catch (e: Exception) {
                        trySend(BaseResponse.Error(e.message ?: "Failed to deserialize realtime data"))
                    }
                }
            }

            awaitClose {
                job.cancel()
                kotlinx.coroutines.runBlocking {
                    client.realtime.removeChannel(channel)
                }
            }
        } catch (e: Exception) {
            trySend(BaseResponse.Error(e.message ?: "Realtime subscription failed"))
            close(e)
        }
    }.catch { e ->
        emit(BaseResponse.Error(e.message ?: "Realtime subscription failed"))
    }

    override fun subscribeToChannel(channelName: String): Flow<BaseResponse<String>> = callbackFlow {
        try {
            val channel = client.realtime.channel(channelName)

            channel.subscribe()

            val job = launch {
                channel.broadcastFlow<String>(event = "message").collect { message ->
                    trySend(BaseResponse.Success(message))
                }
            }

            awaitClose {
                job.cancel()
                kotlinx.coroutines.runBlocking {
                    client.realtime.removeChannel(channel)
                }
            }
        } catch (e: Exception) {
            trySend(BaseResponse.Error(e.message ?: "Channel subscription failed"))
            close(e)
        }
    }.catch { e ->
        emit(BaseResponse.Error(e.message ?: "Channel subscription failed"))
    }

    override suspend fun removeSubscription(channelName: String) {
        try {
            val channel = client.realtime.subscriptions[channelName]
            if (channel != null) {
                client.realtime.removeChannel(channel)
            }
        } catch (_: Exception) {
            // Silently handle removal errors
        }
    }

    override suspend fun removeAllSubscriptions() {
        try {
            client.realtime.removeAllChannels()
        } catch (_: Exception) {
            // Silently handle removal errors
        }
    }
}
