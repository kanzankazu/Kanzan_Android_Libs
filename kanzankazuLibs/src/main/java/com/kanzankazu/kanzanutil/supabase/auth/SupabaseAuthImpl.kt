package com.kanzankazu.kanzandatabase.supabase.auth

import com.kanzankazu.kanzandatabase.supabase.SupabaseClientProvider
import com.kanzankazu.kanzannetwork.response.kanzanbaseresponse.BaseResponse
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Facebook
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

/**
 * Open class implementation of [SupabaseAuth] using Supabase Kotlin SDK v2.
 *
 * Wraps all auth operations with try/catch returning [BaseResponse.Success] or [BaseResponse.Error].
 * Maps SDK types ([io.github.jan.supabase.auth.user.UserInfo], [io.github.jan.supabase.auth.user.UserSession])
 * to local wrapper data classes ([UserInfo], [SessionInfo]).
 *
 * @param client The [SupabaseClient] instance, defaults to [SupabaseClientProvider.getClient].
 */
open class SupabaseAuthImpl(
    private val client: SupabaseClient = SupabaseClientProvider.getClient()
) : SupabaseAuth {

    override suspend fun signUpWithEmail(email: String, password: String): BaseResponse<UserInfo> {
        return try {
            client.auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            val user = client.auth.currentUserOrNull()
            if (user != null) {
                BaseResponse.Success(
                    UserInfo(
                        id = user.id,
                        email = user.email,
                        metadata = user.userMetadata?.let { mapOf<String, Any?>() }
                    )
                )
            } else {
                BaseResponse.Error("Sign up succeeded but user info not available")
            }
        } catch (e: Exception) {
            BaseResponse.Error(e.message ?: "Sign up failed")
        }
    }

    override suspend fun signInWithEmail(email: String, password: String): BaseResponse<UserInfo> {
        return try {
            client.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            val user = client.auth.currentUserOrNull()
            if (user != null) {
                BaseResponse.Success(
                    UserInfo(
                        id = user.id,
                        email = user.email,
                        metadata = user.userMetadata?.let { mapOf<String, Any?>() }
                    )
                )
            } else {
                BaseResponse.Error("Sign in succeeded but user info not available")
            }
        } catch (e: Exception) {
            BaseResponse.Error(e.message ?: "Sign in failed")
        }
    }

    override suspend fun signInWithOAuth(provider: OAuthProvider): BaseResponse<Unit> {
        return try {
            when (provider) {
                OAuthProvider.GOOGLE -> client.auth.signInWith(Google)
                OAuthProvider.FACEBOOK -> client.auth.signInWith(Facebook)
            }
            BaseResponse.Success(Unit)
        } catch (e: Exception) {
            BaseResponse.Error(e.message ?: "OAuth sign in failed")
        }
    }

    override suspend fun signOut(): BaseResponse<Unit> {
        return try {
            client.auth.signOut()
            BaseResponse.Success(Unit)
        } catch (e: Exception) {
            BaseResponse.Error(e.message ?: "Sign out failed")
        }
    }

    override suspend fun getCurrentUser(): BaseResponse<UserInfo> {
        return try {
            val user = client.auth.currentUserOrNull()
            if (user != null) {
                BaseResponse.Success(
                    UserInfo(
                        id = user.id,
                        email = user.email,
                        metadata = user.userMetadata?.let { mapOf<String, Any?>() }
                    )
                )
            } else {
                BaseResponse.Empty
            }
        } catch (e: Exception) {
            BaseResponse.Error(e.message ?: "Get current user failed")
        }
    }

    override suspend fun getCurrentSession(): BaseResponse<SessionInfo> {
        return try {
            val session = client.auth.currentSessionOrNull()
            if (session != null) {
                BaseResponse.Success(
                    SessionInfo(
                        accessToken = session.accessToken,
                        refreshToken = session.refreshToken,
                        expiresAt = session.expiresAt?.epochSeconds ?: 0L
                    )
                )
            } else {
                BaseResponse.Empty
            }
        } catch (e: Exception) {
            BaseResponse.Error(e.message ?: "Get current session failed")
        }
    }

    override suspend fun resetPasswordForEmail(email: String): BaseResponse<Unit> {
        return try {
            client.auth.resetPasswordForEmail(email)
            BaseResponse.Success(Unit)
        } catch (e: Exception) {
            BaseResponse.Error(e.message ?: "Reset password failed")
        }
    }

    override fun observeAuthState(): Flow<BaseResponse<AuthState>> {
        return client.auth.sessionStatus.map { status ->
            when (status) {
                is SessionStatus.Authenticated -> BaseResponse.Success(AuthState.SIGNED_IN)
                is SessionStatus.NotAuthenticated -> BaseResponse.Success(AuthState.SIGNED_OUT)
                else -> BaseResponse.Success(AuthState.SIGNED_OUT)
            }
        }.catch { e ->
            emit(BaseResponse.Error(e.message ?: "Observe auth state failed"))
        }
    }
}
