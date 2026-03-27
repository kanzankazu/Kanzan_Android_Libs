package com.kanzankazu.kanzandatabase.supabase.realtime

import com.kanzankazu.kanzannetwork.response.kanzanbaseresponse.BaseResponse
import kotlinx.coroutines.flow.Flow

/**
 * Interface for Supabase Realtime subscriptions.
 *
 * Provides Flow-based APIs for subscribing to table changes (INSERT, UPDATE, DELETE)
 * and broadcast channel messages. Follows the same interface + impl pattern as
 * [com.kanzankazu.kanzandatabase.firebase.realtimedatabase.RealtimeDatabase].
 *
 * All emissions use [BaseResponse] for consistency with the existing codebase.
 */
interface SupabaseRealtime {
    /**
     * Subscribe to postgres changes (INSERT, UPDATE, DELETE) on a specific table.
     *
     * The target class must be annotated with `@kotlinx.serialization.Serializable`.
     *
     * @param table The table name to subscribe to.
     * @param targetClass The class to deserialize change records into.
     * @return A [Flow] emitting [BaseResponse.Success] with deserialized data on each change,
     *         or [BaseResponse.Error] on failure.
     */
    fun <T : Any> subscribeToTable(table: String, targetClass: Class<T>): Flow<BaseResponse<T>>

    /**
     * Subscribe to broadcast messages on a named channel.
     *
     * @param channelName The channel name to subscribe to.
     * @return A [Flow] emitting [BaseResponse.Success] with the message string,
     *         or [BaseResponse.Error] on failure.
     */
    fun subscribeToChannel(channelName: String): Flow<BaseResponse<String>>

    /**
     * Remove a specific channel subscription by name.
     *
     * @param channelName The channel name to unsubscribe from.
     */
    suspend fun removeSubscription(channelName: String)

    /**
     * Remove all active channel subscriptions.
     */
    suspend fun removeAllSubscriptions()
}
