package com.kanzankazu.kanzanutil.appwrite.auth

import com.kanzankazu.kanzannetwork.response.kanzanbaseresponse.BaseResponse
import kotlinx.coroutines.flow.Flow

data class AppwriteUserInfo(
    val id: String,
    val email: String?,
    val name: String?,
    val registration: String?
)

data class AppwriteSessionInfo(
    val sessionId: String,
    val userId: String,
    val provider: String,
    val expire: String
)

enum class AppwriteAuthState {
    SIGNED_IN, SIGNED_OUT
}

enum class AppwriteOAuthProvider {
    GOOGLE, FACEBOOK
}

interface AppwriteAuth {
    suspend fun signUp(email: String, password: String, name: String? = null): BaseResponse<AppwriteUserInfo>
    suspend fun signInWithEmail(email: String, password: String): BaseResponse<AppwriteSessionInfo>
    suspend fun signInWithOAuth(provider: AppwriteOAuthProvider, activity: android.app.Activity): BaseResponse<Unit>
    suspend fun signOut(): BaseResponse<Unit>
    suspend fun getCurrentUser(): BaseResponse<AppwriteUserInfo>
    suspend fun getCurrentSession(): BaseResponse<AppwriteSessionInfo>
    suspend fun resetPassword(email: String, redirectUrl: String): BaseResponse<Unit>
    fun observeAuthState(): Flow<BaseResponse<AppwriteAuthState>>
}
