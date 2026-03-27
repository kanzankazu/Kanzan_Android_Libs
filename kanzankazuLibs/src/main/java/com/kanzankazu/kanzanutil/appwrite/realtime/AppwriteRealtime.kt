package com.kanzankazu.kanzanutil.appwrite.realtime

import com.kanzankazu.kanzannetwork.response.kanzanbaseresponse.BaseResponse
import kotlinx.coroutines.flow.Flow

/**
 * Interface for Appwrite Realtime subscriptions.
 *
 * Provides Flow-based APIs for subscribing to collection, document, and storage changes.
 * Follows the same interface + impl pattern as
 * [com.kanzankazu.kanzandatabase.supabase.realtime.SupabaseRealtime].
 *
 * All emissions use [BaseResponse] for consistency with the existing codebase.
 */
interface AppwriteRealtime {

    /**
     * Subscribe to document changes on a specific collection.
     *
     * Subscribes to the channel `databases.{databaseId}.collections.{collectionId}.documents`
     * and emits each change event payload as [BaseResponse.Success].
     *
     * @param databaseId The database ID containing the collection.
     * @param collectionId The collection ID to subscribe to.
     * @return A [Flow] emitting [BaseResponse.Success] with event payload on each change,
     *         or [BaseResponse.Error] on failure.
     */
    fun subscribeToCollection(databaseId: String, collectionId: String): Flow<BaseResponse<Map<String, Any>>>

    /**
     * Subscribe to changes on a specific document.
     *
     * Subscribes to the channel `databases.{databaseId}.collections.{collectionId}.documents.{documentId}`
     * and emits each change event payload as [BaseResponse.Success].
     *
     * @param databaseId The database ID containing the collection.
     * @param collectionId The collection ID containing the document.
     * @param documentId The document ID to subscribe to.
     * @return A [Flow] emitting [BaseResponse.Success] with event payload on each change,
     *         or [BaseResponse.Error] on failure.
     */
    fun subscribeToDocument(databaseId: String, collectionId: String, documentId: String): Flow<BaseResponse<Map<String, Any>>>

    /**
     * Subscribe to file changes on a specific storage bucket.
     *
     * Subscribes to the channel `buckets.{bucketId}.files`
     * and emits each change event payload as [BaseResponse.Success].
     *
     * @param bucketId The bucket ID to subscribe to.
     * @return A [Flow] emitting [BaseResponse.Success] with event payload on each change,
     *         or [BaseResponse.Error] on failure.
     */
    fun subscribeToStorage(bucketId: String): Flow<BaseResponse<Map<String, Any>>>

    /**
     * Remove a specific subscription by its key.
     *
     * @param subscriptionKey The key identifying the subscription to remove.
     */
    fun removeSubscription(subscriptionKey: String)

    /**
     * Remove all active subscriptions.
     */
    fun removeAllSubscriptions()
}
