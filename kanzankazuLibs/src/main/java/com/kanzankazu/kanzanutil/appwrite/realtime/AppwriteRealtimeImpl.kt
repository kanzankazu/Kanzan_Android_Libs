package com.kanzankazu.kanzanutil.appwrite.realtime

import com.kanzankazu.kanzannetwork.response.kanzanbaseresponse.BaseResponse
import com.kanzankazu.kanzanutil.appwrite.AppwriteClientProvider
import io.appwrite.models.RealtimeSubscription
import io.appwrite.services.Realtime
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import java.util.concurrent.ConcurrentHashMap

/**
 * Open class implementation of [AppwriteRealtime] using Appwrite Android SDK.
 *
 * Uses [callbackFlow] with [awaitClose] for subscription lifecycle management,
 * consistent with the existing [com.kanzankazu.kanzandatabase.supabase.realtime.SupabaseRealtimeImpl] pattern.
 *
 * Maintains a [ConcurrentHashMap] of active subscriptions for cleanup via
 * [removeSubscription] and [removeAllSubscriptions].
 *
 * @param realtime The [Realtime] service instance, defaults to [AppwriteClientProvider.getRealtime].
 */
open class AppwriteRealtimeImpl(
    private val realtime: Realtime = AppwriteClientProvider.getRealtime()
) : AppwriteRealtime {

    private val subscriptions = ConcurrentHashMap<String, RealtimeSubscription>()

    override fun subscribeToCollection(
        databaseId: String,
        collectionId: String
    ): Flow<BaseResponse<Map<String, Any>>> = callbackFlow {
        val channel = "databases.$databaseId.collections.$collectionId.documents"
        try {
            val subscription = realtime.subscribe(channel) { response ->
                try {
                    @Suppress("UNCHECKED_CAST")
                    val payload = response.payload as Map<String, Any>
                    trySend(BaseResponse.Success(payload))
                } catch (e: Exception) {
                    trySend(BaseResponse.Error(e.message ?: "Failed to parse realtime event"))
                }
            }
            subscriptions[channel] = subscription
            awaitClose {
                subscription.close()
                subscriptions.remove(channel)
            }
        } catch (e: Exception) {
            trySend(BaseResponse.Error(e.message ?: "Realtime subscription failed"))
            close(e)
        }
    }.catch { e ->
        emit(BaseResponse.Error(e.message ?: "Realtime subscription failed"))
    }

    override fun subscribeToDocument(
        databaseId: String,
        collectionId: String,
        documentId: String
    ): Flow<BaseResponse<Map<String, Any>>> = callbackFlow {
        val channel = "databases.$databaseId.collections.$collectionId.documents.$documentId"
        try {
            val subscription = realtime.subscribe(channel) { response ->
                try {
                    @Suppress("UNCHECKED_CAST")
                    val payload = response.payload as Map<String, Any>
                    trySend(BaseResponse.Success(payload))
                } catch (e: Exception) {
                    trySend(BaseResponse.Error(e.message ?: "Failed to parse realtime event"))
                }
            }
            subscriptions[channel] = subscription
            awaitClose {
                subscription.close()
                subscriptions.remove(channel)
            }
        } catch (e: Exception) {
            trySend(BaseResponse.Error(e.message ?: "Realtime subscription failed"))
            close(e)
        }
    }.catch { e ->
        emit(BaseResponse.Error(e.message ?: "Realtime subscription failed"))
    }

    override fun subscribeToStorage(bucketId: String): Flow<BaseResponse<Map<String, Any>>> = callbackFlow {
        val channel = "buckets.$bucketId.files"
        try {
            val subscription = realtime.subscribe(channel) { response ->
                try {
                    @Suppress("UNCHECKED_CAST")
                    val payload = response.payload as Map<String, Any>
                    trySend(BaseResponse.Success(payload))
                } catch (e: Exception) {
                    trySend(BaseResponse.Error(e.message ?: "Failed to parse realtime event"))
                }
            }
            subscriptions[channel] = subscription
            awaitClose {
                subscription.close()
                subscriptions.remove(channel)
            }
        } catch (e: Exception) {
            trySend(BaseResponse.Error(e.message ?: "Realtime subscription failed"))
            close(e)
        }
    }.catch { e ->
        emit(BaseResponse.Error(e.message ?: "Realtime subscription failed"))
    }

    override fun removeSubscription(subscriptionKey: String) {
        try {
            subscriptions[subscriptionKey]?.close()
            subscriptions.remove(subscriptionKey)
        } catch (_: Exception) {
            // Silently handle removal errors
        }
    }

    override fun removeAllSubscriptions() {
        try {
            subscriptions.values.forEach { it.close() }
            subscriptions.clear()
        } catch (_: Exception) {
            // Silently handle removal errors
        }
    }
}
