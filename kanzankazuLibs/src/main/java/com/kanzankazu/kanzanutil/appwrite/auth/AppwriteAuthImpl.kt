package com.kanzankazu.kanzanutil.appwrite.auth

import android.app.Activity
import com.kanzankazu.kanzannetwork.response.kanzanbaseresponse.BaseResponse
import com.kanzankazu.kanzanutil.appwrite.AppwriteClientProvider
import io.appwrite.ID
import io.appwrite.enums.OAuthProvider
import io.appwrite.services.Account
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

open class AppwriteAuthImpl(
    private val account: Account = AppwriteClientProvider.getAccount()
) : AppwriteAuth {

    override suspend fun signUp(email: String, password: String, name: String?): BaseResponse<AppwriteUserInfo> {
        return try {
            val user = account.create(
                userId = ID.unique(),
                email = email,
                password = password,
                name = name
            )
            BaseResponse.Success(
                AppwriteUserInfo(
                    id = user.id,
                    email = user.email,
                    name = user.name,
                    registration = user.registration
                )
            )
        } catch (e: Exception) {
            BaseResponse.Error(e.message ?: "Sign up failed")
        }
    }

    override suspend fun signInWithEmail(email: String, password: String): BaseResponse<AppwriteSessionInfo> {
        return try {
            val session = account.createEmailPasswordSession(
                email = email,
                password = password
            )
            BaseResponse.Success(
                AppwriteSessionInfo(
                    sessionId = session.id,
                    userId = session.userId,
                    provider = session.provider,
                    expire = session.expire
                )
            )
        } catch (e: Exception) {
            BaseResponse.Error(e.message ?: "Sign in failed")
        }
    }

    override suspend fun signInWithOAuth(provider: AppwriteOAuthProvider, activity: Activity): BaseResponse<Unit> {
        return try {
            val oauthProvider = when (provider) {
                AppwriteOAuthProvider.GOOGLE -> OAuthProvider.GOOGLE
                AppwriteOAuthProvider.FACEBOOK -> OAuthProvider.FACEBOOK
            }
            account.createOAuth2Session(
                activity = activity,
                provider = oauthProvider
            )
            BaseResponse.Success(Unit)
        } catch (e: Exception) {
            BaseResponse.Error(e.message ?: "OAuth sign in failed")
        }
    }

    override suspend fun signOut(): BaseResponse<Unit> {
        return try {
            account.deleteSession(sessionId = "current")
            BaseResponse.Success(Unit)
        } catch (e: Exception) {
            BaseResponse.Error(e.message ?: "Sign out failed")
        }
    }

    override suspend fun getCurrentUser(): BaseResponse<AppwriteUserInfo> {
        return try {
            val user = account.get()
            BaseResponse.Success(
                AppwriteUserInfo(
                    id = user.id,
                    email = user.email,
                    name = user.name,
                    registration = user.registration
                )
            )
        } catch (e: Exception) {
            BaseResponse.Empty
        }
    }

    override suspend fun getCurrentSession(): BaseResponse<AppwriteSessionInfo> {
        return try {
            val session = account.getSession(sessionId = "current")
            BaseResponse.Success(
                AppwriteSessionInfo(
                    sessionId = session.id,
                    userId = session.userId,
                    provider = session.provider,
                    expire = session.expire
                )
            )
        } catch (e: Exception) {
            BaseResponse.Error(e.message ?: "Get current session failed")
        }
    }

    override suspend fun resetPassword(email: String, redirectUrl: String): BaseResponse<Unit> {
        return try {
            account.createRecovery(email = email, url = redirectUrl)
            BaseResponse.Success(Unit)
        } catch (e: Exception) {
            BaseResponse.Error(e.message ?: "Reset password failed")
        }
    }

    override fun observeAuthState(): Flow<BaseResponse<AppwriteAuthState>> {
        return flow {
            try {
                account.get()
                emit(BaseResponse.Success(AppwriteAuthState.SIGNED_IN))
            } catch (e: Exception) {
                emit(BaseResponse.Success(AppwriteAuthState.SIGNED_OUT))
            }
        }.catch { e ->
            emit(BaseResponse.Error(e.message ?: "Observe auth state failed"))
        }
    }
}
