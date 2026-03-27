package com.kanzankazu.kanzandatabase.supabase.auth

import com.kanzankazu.kanzannetwork.response.kanzanbaseresponse.BaseResponse
import kotlinx.coroutines.flow.Flow

data class UserInfo(
    val id: String,
    val email: String?,
    val metadata: Map<String, Any?>?
)

data class SessionInfo(
    val accessToken: String,
    val refreshToken: String,
    val expiresAt: Long
)

enum class AuthState {
    SIGNED_IN, SIGNED_OUT
}

enum class OAuthProvider {
    GOOGLE, FACEBOOK
}

interface SupabaseAuth {
    suspend fun signUpWithEmail(email: String, password: String): BaseResponse<UserInfo>
    suspend fun signInWithEmail(email: String, password: String): BaseResponse<UserInfo>
    suspend fun signInWithOAuth(provider: OAuthProvider): BaseResponse<Unit>
    suspend fun signOut(): BaseResponse<Unit>
    suspend fun getCurrentUser(): BaseResponse<UserInfo>
    suspend fun getCurrentSession(): BaseResponse<SessionInfo>
    suspend fun resetPasswordForEmail(email: String): BaseResponse<Unit>
    fun observeAuthState(): Flow<BaseResponse<AuthState>>
}
