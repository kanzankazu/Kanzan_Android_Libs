@file:Suppress("unused")

package com.kanzankazu.kanzandatabase.retrofit.sample

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.kanzankazu.kanzandatabase.retrofit.BaseHttpClient
import com.kanzankazu.kanzannetwork.response.kanzanbaseresponse.BaseResponse
import com.kanzankazu.kanzannetwork.response.kanzanbaseresponse.HttpError
import com.kanzankazu.kanzannetwork.response.kanzanbaseresponse.handleBaseResponse
import com.kanzankazu.kanzannetwork.response.kanzanbaseresponse.handleBaseResponseCombineData
import com.kanzankazu.kanzannetwork.response.kanzanbaseresponse.handleBaseResponseConvertData
import com.kanzankazu.kanzannetwork.response.kanzanbaseresponse.safeApiCall
import com.kanzankazu.kanzannetwork.response.kanzanbaseresponse.safeApiCallFlow
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.HEAD
import retrofit2.http.Header
import retrofit2.http.HeaderMap
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap
import retrofit2.http.QueryName
import retrofit2.http.Streaming
import retrofit2.http.Tag
import retrofit2.http.Url

// ============================================================
// 1. MODEL — Pakai @Expose karena Gson dikonfigurasi dengan
//    excludeFieldsWithoutExposeAnnotation()
// ============================================================

data class User(
    @Expose @SerializedName("id") val id: Int,
    @Expose @SerializedName("name") val name: String,
    @Expose @SerializedName("email") val email: String,
)

data class CreateUserRequest(
    @Expose @SerializedName("name") val name: String,
    @Expose @SerializedName("email") val email: String,
)

data class UpdateUserRequest(
    @Expose @SerializedName("name") val name: String?,
    @Expose @SerializedName("email") val email: String?,
)

data class LoginRequest(
    @Expose @SerializedName("username") val username: String,
    @Expose @SerializedName("password") val password: String,
)

data class TokenResponse(
    @Expose @SerializedName("access_token") val accessToken: String,
    @Expose @SerializedName("refresh_token") val refreshToken: String,
    @Expose @SerializedName("expires_in") val expiresIn: Long,
)

data class UploadResponse(
    @Expose @SerializedName("url") val url: String,
    @Expose @SerializedName("file_name") val fileName: String,
    @Expose @SerializedName("size") val size: Long,
)

data class PaginatedResponse<T>(
    @Expose @SerializedName("data") val data: List<T>,
    @Expose @SerializedName("page") val page: Int,
    @Expose @SerializedName("total_pages") val totalPages: Int,
    @Expose @SerializedName("total_items") val totalItems: Int,
)

data class MessageResponse(
    @Expose @SerializedName("message") val message: String,
    @Expose @SerializedName("success") val success: Boolean,
)

// ============================================================
// 2. API SERVICE — Contoh lengkap semua Retrofit annotation
// ============================================================

interface UserApiService {

    // ─────────────────────────────────────────────────────────
    // GET — Basic
    // ─────────────────────────────────────────────────────────

    /** GET list — return Response<T> → pakai safeApiCall */
    @GET("users")
    suspend fun getUsers(): Response<List<User>>

    /** GET single item by path param */
    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: Int): Response<User>

    // ─────────────────────────────────────────────────────────
    // GET — @Query, @QueryMap, @QueryName
    // ─────────────────────────────────────────────────────────

    /** Single @Query → GET users?role=admin */
    @GET("users")
    suspend fun getUsersByRole(@Query("role") role: String): Response<List<User>>

    /** Multiple @Query → GET users?page=1&limit=20&sort=name */
    @GET("users")
    suspend fun getUsersPaginated(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("sort") sort: String = "id",
    ): Response<PaginatedResponse<User>>

    /** @Query nullable — parameter tidak dikirim kalau null → GET users?status=active (tanpa role) */
    @GET("users")
    suspend fun getUsersFiltered(
        @Query("status") status: String?,
        @Query("role") role: String? = null,
    ): Response<List<User>>

    /** @Query dengan list → GET users?ids=1&ids=2&ids=3 */
    @GET("users")
    suspend fun getUsersByIds(@Query("ids") ids: List<Int>): Response<List<User>>

    /** @QueryMap — dynamic query params → GET users?key1=val1&key2=val2&... */
    @GET("users")
    suspend fun getUsersWithFilters(@QueryMap filters: Map<String, String>): Response<List<User>>

    /** @QueryName — query tanpa value → GET users?active&verified */
    @GET("users")
    suspend fun getUsersWithFlags(@QueryName flags: List<String>): Response<List<User>>

    /** @Query encoded — value tidak di-encode ulang → GET search?q=hello%20world (tetap %20, bukan %2520) */
    @GET("search")
    suspend fun searchUsers(@Query("q", encoded = true) query: String): Response<List<User>>

    // ─────────────────────────────────────────────────────────
    // GET — @Path variants
    // ─────────────────────────────────────────────────────────

    /** Multiple @Path → GET organizations/123/users/456 */
    @GET("organizations/{orgId}/users/{userId}")
    suspend fun getOrgUser(
        @Path("orgId") orgId: Int,
        @Path("userId") userId: Int,
    ): Response<User>

    /** @Path encoded — value tidak di-encode → GET files/path/to/file.txt */
    @GET("files/{path}")
    suspend fun getFile(@Path("path", encoded = true) filePath: String): Response<ResponseBody>

    // ─────────────────────────────────────────────────────────
    // GET — @Header, @HeaderMap, @Headers (static)
    // ─────────────────────────────────────────────────────────

    /** @Header — dynamic header per-request */
    @GET("users/me")
    suspend fun getCurrentUser(@Header("Authorization") token: String): Response<User>

    /** @HeaderMap — multiple dynamic headers */
    @GET("users/me")
    suspend fun getCurrentUserWithHeaders(@HeaderMap headers: Map<String, String>): Response<User>

    /** @Headers — static headers (hardcoded di annotation) */
    @Headers(
        "Accept: application/json",
        "X-Api-Version: 2",
        "Cache-Control: no-cache"
    )
    @GET("users")
    suspend fun getUsersV2(): Response<List<User>>

    /** Kombinasi @Headers (static) + @Header (dynamic) */
    @Headers("Accept: application/json")
    @GET("users")
    suspend fun getUsersWithLocale(@Header("Accept-Language") locale: String): Response<List<User>>

    // ─────────────────────────────────────────────────────────
    // GET — @Url (dynamic full URL)
    // ─────────────────────────────────────────────────────────

    /** @Url — override base URL sepenuhnya (untuk pagination next_page URL, dll) */
    @GET
    suspend fun getUsersFromUrl(@Url url: String): Response<List<User>>

    // ─────────────────────────────────────────────────────────
    // GET — @Streaming (download file besar)
    // ─────────────────────────────────────────────────────────

    /** @Streaming — response body tidak di-buffer ke memory (untuk download file besar) */
    @Streaming
    @GET("files/{fileId}/download")
    suspend fun downloadFile(@Path("fileId") fileId: String): Response<ResponseBody>

    // ─────────────────────────────────────────────────────────
    // GET — @Tag (attach metadata ke request)
    // ─────────────────────────────────────────────────────────

    /** @Tag — attach object ke request, bisa diakses di Interceptor via request.tag() */
    @GET("users")
    suspend fun getUsersWithTag(@Tag tag: String): Response<List<User>>

    // ─────────────────────────────────────────────────────────
    // POST — @Body (JSON)
    // ─────────────────────────────────────────────────────────

    /** @Body object → Gson serialize ke JSON */
    @POST("users")
    suspend fun createUser(@Body request: CreateUserRequest): Response<User>

    /** @Body dengan Map (dynamic JSON body) */
    @POST("users")
    suspend fun createUserMap(@Body body: Map<String, @JvmSuppressWildcards Any>): Response<User>

    /** @Body dengan RequestBody (raw body) */
    @POST("users")
    suspend fun createUserRaw(@Body body: RequestBody): Response<User>

    // ─────────────────────────────────────────────────────────
    // POST — @FormUrlEncoded + @Field / @FieldMap
    // ─────────────────────────────────────────────────────────

    /** @FormUrlEncoded + @Field → Content-Type: application/x-www-form-urlencoded */
    @FormUrlEncoded
    @POST("auth/login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String,
    ): Response<TokenResponse>

    /** @Field encoded — value tidak di-encode ulang */
    @FormUrlEncoded
    @POST("auth/login")
    suspend fun loginEncoded(
        @Field("username") username: String,
        @Field(value = "password", encoded = true) password: String,
    ): Response<TokenResponse>

    /** @FieldMap — dynamic form fields */
    @FormUrlEncoded
    @POST("auth/login")
    suspend fun loginWithFields(@FieldMap fields: Map<String, String>): Response<TokenResponse>

    /** @FormUrlEncoded + @Body TIDAK BISA digabung — pilih salah satu */

    // ─────────────────────────────────────────────────────────
    // POST — @Multipart + @Part / @PartMap (upload file)
    // ─────────────────────────────────────────────────────────

    /** @Multipart + @Part — upload single file */
    @Multipart
    @POST("users/{id}/avatar")
    suspend fun uploadAvatar(
        @Path("id") userId: Int,
        @Part file: MultipartBody.Part,
    ): Response<UploadResponse>

    /** @Multipart — upload file + text fields */
    @Multipart
    @POST("users/{id}/documents")
    suspend fun uploadDocument(
        @Path("id") userId: Int,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("category") category: RequestBody,
    ): Response<UploadResponse>

    /** @Multipart — upload multiple files */
    @Multipart
    @POST("users/{id}/photos")
    suspend fun uploadPhotos(
        @Path("id") userId: Int,
        @Part files: List<MultipartBody.Part>,
    ): Response<List<UploadResponse>>

    /** @PartMap — dynamic multipart fields */
    @Multipart
    @POST("users/{id}/profile")
    suspend fun updateProfile(
        @Path("id") userId: Int,
        @Part avatar: MultipartBody.Part?,
        @PartMap fields: Map<String, @JvmSuppressWildcards RequestBody>,
    ): Response<User>

    // ─────────────────────────────────────────────────────────
    // PUT — Full update
    // ─────────────────────────────────────────────────────────

    /** PUT — replace seluruh resource */
    @PUT("users/{id}")
    suspend fun updateUser(
        @Path("id") id: Int,
        @Body request: CreateUserRequest,
    ): Response<User>

    // ─────────────────────────────────────────────────────────
    // PATCH — Partial update
    // ─────────────────────────────────────────────────────────

    /** PATCH — update sebagian field saja */
    @PATCH("users/{id}")
    suspend fun patchUser(
        @Path("id") id: Int,
        @Body request: UpdateUserRequest,
    ): Response<User>

    /** PATCH dengan Map (kirim field yang berubah saja) */
    @PATCH("users/{id}")
    suspend fun patchUserMap(
        @Path("id") id: Int,
        @Body fields: Map<String, @JvmSuppressWildcards Any?>,
    ): Response<User>

    // ─────────────────────────────────────────────────────────
    // DELETE
    // ─────────────────────────────────────────────────────────

    /** DELETE — hapus resource */
    @DELETE("users/{id}")
    suspend fun deleteUser(@Path("id") id: Int): Response<MessageResponse>

    /** DELETE — return Unit (no body) → cek response.isSuccessful saja */
    @DELETE("users/{id}")
    suspend fun deleteUserNoBody(@Path("id") id: Int): Response<Unit>

    /** DELETE dengan @Query → DELETE users/123?soft=true */
    @DELETE("users/{id}")
    suspend fun softDeleteUser(
        @Path("id") id: Int,
        @Query("soft") soft: Boolean = true,
    ): Response<MessageResponse>

    // ─────────────────────────────────────────────────────────
    // HEAD — Cek resource tanpa download body
    // ─────────────────────────────────────────────────────────

    /** HEAD — cek apakah resource ada (response header only, no body) */
    @HEAD("users/{id}")
    suspend fun checkUserExists(@Path("id") id: Int): Response<Void>

    // ─────────────────────────────────────────────────────────
    // Kombinasi kompleks
    // ─────────────────────────────────────────────────────────

    /** Semua annotation digabung: static header + dynamic header + path + query + body */
    @Headers("X-Api-Version: 2")
    @POST("organizations/{orgId}/users")
    suspend fun createOrgUser(
        @Header("X-Request-Id") requestId: String,
        @Path("orgId") orgId: Int,
        @Query("notify") notify: Boolean = true,
        @Body request: CreateUserRequest,
    ): Response<User>
}

// ============================================================
// 3. HTTP CLIENT — Extend BaseHttpClient
// ============================================================

class SampleHttpClient(
    private val context: Context,
    private val token: String,
) : BaseHttpClient() {
    override fun getContext(): Context = context
    override fun getToken(): String = token
    override fun getBaseUrl(): String = "https://api.example.com/"
    override fun isDebug(): Boolean = true // ganti BuildConfig.DEBUG di project asli

    /** Helper untuk ambil UserApiService instance */
    val userApi: UserApiService?
        get() = getApi(UserApiService::class.java)
}

// ============================================================
// 4. REPOSITORY — Tempat safeApiCall dipanggil
//    Contoh lengkap semua style untuk berbagai endpoint
// ============================================================

class UserRepository(private val httpClient: SampleHttpClient) {

    private val api get() = httpClient.userApi!!

    // ─── GET: safeApiCall (Response<T>) ───

    suspend fun getUsers(): BaseResponse<List<User>> {
        return safeApiCall { api.getUsers() }
    }

    suspend fun getUserById(id: Int): BaseResponse<User> {
        return safeApiCall { api.getUserById(id) }
    }

    suspend fun getUsersByRole(role: String): BaseResponse<List<User>> {
        return safeApiCall { api.getUsersByRole(role) }
    }

    suspend fun getUsersPaginated(page: Int, limit: Int, sort: String = "id"): BaseResponse<PaginatedResponse<User>> {
        return safeApiCall { api.getUsersPaginated(page, limit, sort) }
    }

    suspend fun getUsersFiltered(status: String?, role: String? = null): BaseResponse<List<User>> {
        return safeApiCall { api.getUsersFiltered(status, role) }
    }

    suspend fun getUsersByIds(ids: List<Int>): BaseResponse<List<User>> {
        return safeApiCall { api.getUsersByIds(ids) }
    }

    suspend fun getUsersWithFilters(filters: Map<String, String>): BaseResponse<List<User>> {
        return safeApiCall { api.getUsersWithFilters(filters) }
    }

    suspend fun getOrgUser(orgId: Int, userId: Int): BaseResponse<User> {
        return safeApiCall { api.getOrgUser(orgId, userId) }
    }

    suspend fun getCurrentUser(token: String): BaseResponse<User> {
        return safeApiCall { api.getCurrentUser("Bearer $token") }
    }

    suspend fun getUsersFromUrl(nextPageUrl: String): BaseResponse<List<User>> {
        return safeApiCall { api.getUsersFromUrl(nextPageUrl) }
    }

    suspend fun downloadFile(fileId: String): BaseResponse<ResponseBody> {
        return safeApiCall { api.downloadFile(fileId) }
    }

    // ─── GET: safeApiCallFlow (Response<T> → Flow) ───

    fun getUsersFlow(): Flow<BaseResponse<List<User>>> {
        return safeApiCallFlow { api.getUsers() }
    }

    fun getUsersPaginatedFlow(page: Int, limit: Int): Flow<BaseResponse<PaginatedResponse<User>>> {
        return safeApiCallFlow { api.getUsersPaginated(page, limit) }
    }

    // ─── POST: @Body JSON ───

    suspend fun createUser(name: String, email: String): BaseResponse<User> {
        return safeApiCall { api.createUser(CreateUserRequest(name, email)) }
    }

    suspend fun createUserMap(name: String, email: String): BaseResponse<User> {
        return safeApiCall { api.createUserMap(mapOf("name" to name, "email" to email)) }
    }

    fun createUserFlow(name: String, email: String): Flow<BaseResponse<User>> {
        return safeApiCallFlow { api.createUser(CreateUserRequest(name, email)) }
    }

    // ─── POST: @FormUrlEncoded ───

    suspend fun login(username: String, password: String): BaseResponse<TokenResponse> {
        return safeApiCall { api.login(username, password) }
    }

    suspend fun loginWithFields(fields: Map<String, String>): BaseResponse<TokenResponse> {
        return safeApiCall { api.loginWithFields(fields) }
    }

    // ─── POST: @Multipart (upload file) ───

    suspend fun uploadAvatar(userId: Int, filePart: MultipartBody.Part): BaseResponse<UploadResponse> {
        return safeApiCall { api.uploadAvatar(userId, filePart) }
    }

    suspend fun uploadDocument(
        userId: Int,
        filePart: MultipartBody.Part,
        description: RequestBody,
        category: RequestBody,
    ): BaseResponse<UploadResponse> {
        return safeApiCall { api.uploadDocument(userId, filePart, description, category) }
    }

    suspend fun uploadPhotos(userId: Int, fileParts: List<MultipartBody.Part>): BaseResponse<List<UploadResponse>> {
        return safeApiCall { api.uploadPhotos(userId, fileParts) }
    }

    suspend fun updateProfile(
        userId: Int,
        avatar: MultipartBody.Part?,
        fields: Map<String, RequestBody>,
    ): BaseResponse<User> {
        return safeApiCall { api.updateProfile(userId, avatar, fields) }
    }

    // ─── PUT ───

    suspend fun updateUser(id: Int, name: String, email: String): BaseResponse<User> {
        return safeApiCall { api.updateUser(id, CreateUserRequest(name, email)) }
    }

    // ─── PATCH ───

    suspend fun patchUser(id: Int, name: String? = null, email: String? = null): BaseResponse<User> {
        return safeApiCall { api.patchUser(id, UpdateUserRequest(name, email)) }
    }

    suspend fun patchUserMap(id: Int, fields: Map<String, Any?>): BaseResponse<User> {
        return safeApiCall { api.patchUserMap(id, fields) }
    }

    // ─── DELETE ───

    suspend fun deleteUser(id: Int): BaseResponse<MessageResponse> {
        return safeApiCall { api.deleteUser(id) }
    }

    suspend fun deleteUserNoBody(id: Int): BaseResponse<Unit> {
        return safeApiCall { api.deleteUserNoBody(id) }
    }

    suspend fun softDeleteUser(id: Int): BaseResponse<MessageResponse> {
        return safeApiCall { api.softDeleteUser(id, soft = true) }
    }

    // ─── HEAD ───

    suspend fun checkUserExists(id: Int): Boolean {
        return try {
            val response = api.checkUserExists(id)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    // ─── Kombinasi kompleks ───

    suspend fun createOrgUser(
        requestId: String,
        orgId: Int,
        request: CreateUserRequest,
        notify: Boolean = true,
    ): BaseResponse<User> {
        return safeApiCall { api.createOrgUser(requestId, orgId, notify, request) }
    }

    // ─── PARALLEL: Hit beberapa endpoint sekaligus (async) ───

    /**
     * Contoh: Dashboard screen butuh data users + current user profile sekaligus.
     * Pakai coroutineScope + async supaya jalan bareng, bukan satu-satu.
     */
    suspend fun getDashboardData(token: String): BaseResponse<DashboardData> = coroutineScope {
        val usersDeferred = async { safeApiCall { api.getUsers() } }
        val profileDeferred = async { safeApiCall { api.getCurrentUser("Bearer $token") } }

        val usersResult = usersDeferred.await()
        val profileResult = profileDeferred.await()

        handleBaseResponseCombineData(
            mainBaseResponse = usersResult,
            secondBaseResponse = profileResult,
            onSuccess = { users, profile ->
                DashboardData(users = users.orEmpty(), currentUser = profile)
            },
        )
    }

    /**
     * Contoh: Load users + filtered users sekaligus, tapi tetap tampilkan data
     * meskipun salah satu gagal (isStillShowSuccess = true).
     */
    suspend fun getUsersAndAdmins(): BaseResponse<UsersAndAdmins> = coroutineScope {
        val allUsersDeferred = async { safeApiCall { api.getUsers() } }
        val adminsDeferred = async { safeApiCall { api.getUsersByRole("admin") } }

        handleBaseResponseCombineData(
            mainBaseResponse = allUsersDeferred.await(),
            secondBaseResponse = adminsDeferred.await(),
            isStillShowSuccess = true, // tetap success walau salah satu gagal
            onSuccess = { allUsers, admins ->
                UsersAndAdmins(allUsers = allUsers.orEmpty(), admins = admins.orEmpty())
            },
        )
    }

    /**
     * Contoh: Profile screen butuh data dari 3+ endpoint sekaligus.
     * Semua HARUS berhasil baru dianggap sukses — kalau satu gagal, semua gagal.
     * Pakai coroutineScope + async, lalu cek semua result.
     */
    suspend fun getFullProfileData(userId: Int, orgId: Int, token: String): BaseResponse<FullProfileData> = coroutineScope {
        val userDeferred = async { safeApiCall { api.getUserById(userId) } }
        val orgDeferred = async { safeApiCall { api.getOrgUser(orgId, userId) } }
        val currentUserDeferred = async { safeApiCall { api.getCurrentUser("Bearer $token") } }
        val usersDeferred = async { safeApiCall { api.getUsersPaginated(1, 10) } }

        val userResult = userDeferred.await()
        val orgResult = orgDeferred.await()
        val currentUserResult = currentUserDeferred.await()
        val usersResult = usersDeferred.await()

        // Cek semua harus Success — kalau ada yang gagal, return error pertama
        val firstError = listOf(userResult, orgResult, currentUserResult, usersResult)
            .filterIsInstance<BaseResponse.Error>()
            .firstOrNull()

        if (firstError != null) {
            return@coroutineScope BaseResponse.Error(firstError.message)
        }

        // Semua success — gabung jadi 1 model
        val user = (userResult as BaseResponse.Success).data
        val orgProfile = (orgResult as BaseResponse.Success).data
        val currentUser = (currentUserResult as BaseResponse.Success).data
        val recentUsers = (usersResult as BaseResponse.Success).data

        BaseResponse.Success(
            FullProfileData(
                user = user,
                orgProfile = orgProfile,
                currentUser = currentUser,
                recentUsers = recentUsers.data,
                totalUsers = recentUsers.totalItems,
            )
        )
    }

    /**
     * Contoh: Sama seperti getFullProfileData, tapi kalau ada endpoint yang gagal,
     * pakai default value — TIDAK gagalkan keseluruhan.
     * Cocok untuk screen yang tetap bisa tampil walau data partial.
     */
    suspend fun getFullProfileDataSafe(userId: Int, orgId: Int, token: String): BaseResponse<FullProfileData> = coroutineScope {
        val userDeferred = async { safeApiCall { api.getUserById(userId) } }
        val orgDeferred = async { safeApiCall { api.getOrgUser(orgId, userId) } }
        val currentUserDeferred = async { safeApiCall { api.getCurrentUser("Bearer $token") } }
        val usersDeferred = async { safeApiCall { api.getUsersPaginated(1, 10) } }

        val userResult = userDeferred.await()
        val orgResult = orgDeferred.await()
        val currentUserResult = currentUserDeferred.await()
        val usersResult = usersDeferred.await()

        // Default values untuk masing-masing endpoint yang gagal
        val defaultUser = User(id = 0, name = "Unknown", email = "")
        val defaultPaginated = PaginatedResponse<User>(data = emptyList(), page = 0, totalPages = 0, totalItems = 0)

        val user = (userResult as? BaseResponse.Success)?.data ?: defaultUser
        val orgProfile = (orgResult as? BaseResponse.Success)?.data ?: defaultUser
        val currentUser = (currentUserResult as? BaseResponse.Success)?.data ?: defaultUser
        val paginated = (usersResult as? BaseResponse.Success)?.data ?: defaultPaginated

        BaseResponse.Success(
            FullProfileData(
                user = user,
                orgProfile = orgProfile,
                currentUser = currentUser,
                recentUsers = paginated.data,
                totalUsers = paginated.totalItems,
            )
        )
    }

    // ─── SEQUENTIAL: Hit endpoint berurutan, saling menunggu ───

    /**
     * Contoh: Register → lalu auto login → return token.
     * Harus urut karena login butuh user yang sudah terdaftar.
     */
    suspend fun registerAndLogin(name: String, email: String, password: String): BaseResponse<TokenResponse> {
        // Step 1: Create user dulu
        val createResult = safeApiCall { api.createUser(CreateUserRequest(name, email)) }
        if (createResult !is BaseResponse.Success) {
            return BaseResponse.Error((createResult as? BaseResponse.Error)?.message ?: "Register gagal")
        }

        // Step 2: Baru login pakai email yang baru didaftarkan
        return safeApiCall { api.login(email, password) }
    }

    /**
     * Contoh: Get user detail → lalu fetch org detail berdasarkan orgId dari user.
     * Data dari response pertama dipakai untuk request kedua.
     */
    suspend fun getUserWithOrg(userId: Int, orgId: Int): BaseResponse<UserWithOrg> {
        val userResult = safeApiCall { api.getUserById(userId) }
        if (userResult !is BaseResponse.Success) {
            return BaseResponse.Error((userResult as? BaseResponse.Error)?.message ?: "User tidak ditemukan")
        }

        val orgUserResult = safeApiCall { api.getOrgUser(orgId, userId) }
        return orgUserResult.handleBaseResponseConvertData { orgUser ->
            UserWithOrg(user = userResult.data, orgProfile = orgUser)
        }
    }

    // ─── REAL-WORLD: Pattern yang sering terjadi di production ───

    /**
     * Contoh: Refresh token → retry original request.
     * Kalau token expired (401), refresh dulu baru retry.
     */
    suspend fun getUsersWithAutoRefresh(
        refreshToken: String,
        onTokenRefreshed: (TokenResponse) -> Unit,
    ): BaseResponse<List<User>> {
        val result = safeApiCall { api.getUsers() }

        // Kalau 401 Unauthorized, coba refresh token
        if (result is HttpError && result.code == 401) {
            val refreshResult = safeApiCall {
                api.loginWithFields(mapOf("grant_type" to "refresh_token", "refresh_token" to refreshToken))
            }
            if (refreshResult is BaseResponse.Success) {
                onTokenRefreshed(refreshResult.data)
                // Retry dengan token baru — di real app, token baru di-set ke interceptor
                return safeApiCall { api.getUsers() }
            }
            return BaseResponse.Error("Session expired. Silakan login ulang.")
        }

        return result
    }

    /**
     * Contoh: Upload avatar → lalu update profile dengan URL avatar baru.
     * Sequential karena update profile butuh URL dari upload response.
     */
    suspend fun updateProfileWithAvatar(
        userId: Int,
        avatarPart: MultipartBody.Part,
        name: String,
        email: String,
    ): BaseResponse<User> {
        // Step 1: Upload avatar dulu
        val uploadResult = safeApiCall { api.uploadAvatar(userId, avatarPart) }
        if (uploadResult !is BaseResponse.Success) {
            return BaseResponse.Error((uploadResult as? BaseResponse.Error)?.message ?: "Upload avatar gagal")
        }

        // Step 2: Update profile dengan avatar URL baru
        val avatarUrl = uploadResult.data.url
        return safeApiCall {
            api.patchUserMap(userId, mapOf("name" to name, "email" to email, "avatar_url" to avatarUrl))
        }
    }

    /**
     * Contoh: Pagination — load all pages sampai habis.
     * Recursive/loop sampai page terakhir.
     */
    suspend fun getAllUsers(limit: Int = 50): BaseResponse<List<User>> {
        val allUsers = mutableListOf<User>()
        var currentPage = 1

        while (true) {
            val result = safeApiCall { api.getUsersPaginated(currentPage, limit) }
            if (result !is BaseResponse.Success) {
                // Convert error/empty dari PaginatedResponse ke List<User>
                return when (result) {
                    is BaseResponse.Error -> BaseResponse.Error(result.message)
                    is BaseResponse.Empty -> BaseResponse.Empty
                    else -> BaseResponse.Empty
                }
            }

            allUsers.addAll(result.data.data)

            if (currentPage >= result.data.totalPages) break
            currentPage++
        }

        return if (allUsers.isEmpty()) BaseResponse.Empty
        else BaseResponse.Success(allUsers)
    }

    /**
     * Contoh: Batch delete — hapus beberapa user sekaligus (parallel).
     * Return list of results.
     */
    suspend fun deleteUsers(userIds: List<Int>): BaseResponse<List<Int>> = coroutineScope {
        val results = userIds.map { id ->
            async { id to safeApiCall { api.deleteUser(id) } }
        }.map { it.await() }

        val successIds = results.filter { it.second is BaseResponse.Success }.map { it.first }
        val failedIds = results.filter { it.second !is BaseResponse.Success }.map { it.first }

        if (successIds.isEmpty()) {
            BaseResponse.Error("Semua delete gagal")
        } else if (failedIds.isNotEmpty()) {
            // Partial success — some failed
            BaseResponse.Success(successIds) // caller bisa handle failedIds sendiri
        } else {
            BaseResponse.Success(successIds)
        }
    }

    /**
     * Contoh: Search with debounce-ready suspend.
     * Di ViewModel biasanya dipanggil setelah debounce dari TextField.
     */
    suspend fun searchUsers(query: String): BaseResponse<List<User>> {
        if (query.length < 2) return BaseResponse.Empty // minimal 2 karakter
        return safeApiCall { api.searchUsers(query) }
    }

    /**
     * Contoh: Check before create — cek dulu apakah user sudah ada, baru create.
     * Pattern umum untuk avoid duplicate.
     */
    suspend fun createUserIfNotExists(name: String, email: String): BaseResponse<User> {
        // Step 1: Search dulu
        val searchResult = safeApiCall { api.getUsersWithFilters(mapOf("email" to email)) }
        if (searchResult is BaseResponse.Success && searchResult.data.isNotEmpty()) {
            return BaseResponse.Error("User dengan email $email sudah terdaftar")
        }

        // Step 2: Baru create
        return safeApiCall { api.createUser(CreateUserRequest(name, email)) }
    }
}

// ─── Helper data classes untuk combined responses ───

data class DashboardData(
    val users: List<User>,
    val currentUser: User?,
)

data class UsersAndAdmins(
    val allUsers: List<User>,
    val admins: List<User>,
)

data class UserWithOrg(
    val user: User,
    val orgProfile: User,
)

data class FullProfileData(
    val user: User,
    val orgProfile: User,
    val currentUser: User,
    val recentUsers: List<User>,
    val totalUsers: Int,
)

// ============================================================
// 5. VIEWMODEL — Consume di UI layer
// ============================================================

class UserViewModel(private val repository: UserRepository) : ViewModel() {

    private val _usersState = MutableStateFlow<BaseResponse<List<User>>>(BaseResponse.Loading)
    val usersState = _usersState.asStateFlow()

    private val _userState = MutableStateFlow<BaseResponse<User>>(BaseResponse.Loading)
    val userState = _userState.asStateFlow()

    private val _message = MutableStateFlow("")
    val message = _message.asStateFlow()

    // ─── GET: suspend + handleBaseResponse ───

    fun loadUsers() {
        viewModelScope.launch {
            _usersState.value = BaseResponse.Loading
            val result = repository.getUsers()
            _usersState.value = result

            result.handleBaseResponse(
                onLoading = { /* show/hide loading */ },
                onEmpty = { _message.value = "Belum ada user" },
                onError = { msg -> _message.value = msg },
                onSuccess = { users -> _message.value = "Loaded ${users.size} users" },
            )
        }
    }

    // ─── GET: Flow style — Loading otomatis ───

    fun loadUsersFlow() {
        viewModelScope.launch {
            repository.getUsersFlow().collect { response ->
                _usersState.value = response

                response.handleBaseResponse(
                    onLoading = { isLoading -> /* show/hide loading */ },
                    onError = { msg -> _message.value = msg },
                    onSuccess = { users -> _message.value = "Loaded ${users.size} users" },
                )
            }
        }
    }

    // ─── GET: dengan query params ───

    fun loadUsersByRole(role: String) {
        viewModelScope.launch {
            _usersState.value = BaseResponse.Loading
            repository.getUsersByRole(role).handleBaseResponse(
                onError = { msg -> _message.value = msg },
                onSuccess = { users -> _usersState.value = BaseResponse.Success(users) },
            )
        }
    }

    // ─── GET: pagination ───

    fun loadUsersPaginated(page: Int, limit: Int = 20) {
        viewModelScope.launch {
            repository.getUsersPaginatedFlow(page, limit).collect { response ->
                response.handleBaseResponse(
                    onLoading = { /* show loading */ },
                    onError = { msg -> _message.value = msg },
                    onSuccess = { paginated ->
                        _usersState.value = BaseResponse.Success(paginated.data)
                        _message.value = "Page ${paginated.page}/${paginated.totalPages}"
                    },
                )
            }
        }
    }

    // ─── GET: dynamic filters via QueryMap ───

    fun loadUsersWithFilters(status: String?, role: String?, search: String?) {
        viewModelScope.launch {
            val filters = buildMap {
                status?.let { put("status", it) }
                role?.let { put("role", it) }
                search?.let { put("search", it) }
            }
            repository.getUsersWithFilters(filters).handleBaseResponse(
                onError = { msg -> _message.value = msg },
                onSuccess = { users -> _usersState.value = BaseResponse.Success(users) },
            )
        }
    }

    // ─── GET: dynamic URL (pagination next page) ───

    fun loadUsersNextPage(nextPageUrl: String) {
        viewModelScope.launch {
            repository.getUsersFromUrl(nextPageUrl).handleBaseResponse(
                onError = { msg -> _message.value = msg },
                onSuccess = { users -> _usersState.value = BaseResponse.Success(users) },
            )
        }
    }

    // ─── POST: create user ───

    fun createUser(name: String, email: String) {
        viewModelScope.launch {
            repository.createUser(name, email).handleBaseResponse(
                onLoading = { /* loading */ },
                onError = { msg -> _message.value = "Gagal: $msg" },
                onSuccess = { user -> _message.value = "User ${user.name} berhasil dibuat" },
            )
        }
    }

    // ─── POST: create user Flow style ───

    fun createUserFlow(name: String, email: String) {
        viewModelScope.launch {
            repository.createUserFlow(name, email).collect { response ->
                response.handleBaseResponse(
                    onLoading = { /* auto loading dari Flow */ },
                    onError = { msg -> _message.value = "Gagal: $msg" },
                    onSuccess = { user -> _message.value = "User ${user.name} berhasil dibuat" },
                )
            }
        }
    }

    // ─── POST: login (FormUrlEncoded) ───

    fun login(username: String, password: String) {
        viewModelScope.launch {
            repository.login(username, password).handleBaseResponse(
                onError = { msg -> _message.value = "Login gagal: $msg" },
                onSuccess = { token -> _message.value = "Login berhasil, token: ${token.accessToken}" },
            )
        }
    }

    // ─── POST: upload file (Multipart) ───

    fun uploadAvatar(userId: Int, filePart: MultipartBody.Part) {
        viewModelScope.launch {
            repository.uploadAvatar(userId, filePart).handleBaseResponse(
                onError = { msg -> _message.value = "Upload gagal: $msg" },
                onSuccess = { upload -> _message.value = "Upload berhasil: ${upload.url}" },
            )
        }
    }

    // ─── PUT: full update ───

    fun updateUser(id: Int, name: String, email: String) {
        viewModelScope.launch {
            repository.updateUser(id, name, email).handleBaseResponse(
                onError = { msg -> _message.value = "Update gagal: $msg" },
                onSuccess = { user -> _message.value = "User ${user.name} berhasil diupdate" },
            )
        }
    }

    // ─── PATCH: partial update ───

    fun patchUserName(id: Int, newName: String) {
        viewModelScope.launch {
            repository.patchUser(id, name = newName).handleBaseResponse(
                onError = { msg -> _message.value = "Patch gagal: $msg" },
                onSuccess = { user -> _message.value = "Nama diubah ke ${user.name}" },
            )
        }
    }

    // ─── PATCH: dynamic fields via Map ───

    fun patchUserFields(id: Int, fields: Map<String, Any?>) {
        viewModelScope.launch {
            repository.patchUserMap(id, fields).handleBaseResponse(
                onError = { msg -> _message.value = "Patch gagal: $msg" },
                onSuccess = { user -> _message.value = "User ${user.name} berhasil dipatch" },
            )
        }
    }

    // ─── DELETE ───

    fun deleteUser(id: Int) {
        viewModelScope.launch {
            repository.deleteUser(id).handleBaseResponse(
                onError = { msg -> _message.value = "Delete gagal: $msg" },
                onSuccess = { response -> _message.value = response.message },
            )
        }
    }

    // ─── DELETE: no body (cek sukses dari state) ───

    fun deleteUserSilent(id: Int) {
        viewModelScope.launch {
            repository.deleteUserNoBody(id).handleBaseResponse(
                onError = { msg -> _message.value = "Delete gagal: $msg" },
                onSuccess = { _message.value = "User berhasil dihapus" },
            )
        }
    }

    // ─── HEAD: cek resource exists ───

    fun checkUserExists(id: Int) {
        viewModelScope.launch {
            val exists = repository.checkUserExists(id)
            _message.value = if (exists) "User ada" else "User tidak ditemukan"
        }
    }

    // ─── Kombinasi kompleks ───

    fun createOrgUser(requestId: String, orgId: Int, name: String, email: String) {
        viewModelScope.launch {
            repository.createOrgUser(
                requestId = requestId,
                orgId = orgId,
                request = CreateUserRequest(name, email),
            ).handleBaseResponse(
                onError = { msg -> _message.value = msg },
                onSuccess = { user -> _message.value = "Org user ${user.name} dibuat" },
            )
        }
    }

    // ─── PARALLEL: Hit beberapa endpoint sekaligus ───

    fun loadDashboard(token: String) {
        viewModelScope.launch {
            _usersState.value = BaseResponse.Loading
            repository.getDashboardData(token).handleBaseResponse(
                onError = { msg -> _message.value = msg },
                onSuccess = { dashboard ->
                    _usersState.value = BaseResponse.Success(dashboard.users)
                    dashboard.currentUser?.let {
                        _userState.value = BaseResponse.Success(it)
                    }
                    _message.value = "Dashboard loaded: ${dashboard.users.size} users"
                },
            )
        }
    }

    fun loadUsersAndAdmins() {
        viewModelScope.launch {
            _usersState.value = BaseResponse.Loading
            repository.getUsersAndAdmins().handleBaseResponse(
                onError = { msg -> _message.value = msg },
                onSuccess = { data ->
                    _usersState.value = BaseResponse.Success(data.allUsers)
                    _message.value = "Total: ${data.allUsers.size}, Admin: ${data.admins.size}"
                },
            )
        }
    }

    // ─── PARALLEL: Semua endpoint harus berhasil, gabung jadi 1 model ───

    fun loadFullProfile(userId: Int, orgId: Int, token: String) {
        viewModelScope.launch {
            _userState.value = BaseResponse.Loading
            repository.getFullProfileData(userId, orgId, token).handleBaseResponse(
                onError = { msg -> _message.value = msg },
                onSuccess = { profile ->
                    _userState.value = BaseResponse.Success(profile.user)
                    _message.value = "Profile loaded: ${profile.user.name}, org: ${profile.orgProfile.name}, total users: ${profile.totalUsers}"
                },
            )
        }
    }

    // ─── PARALLEL: Semua endpoint ditunggu, gagal pakai default value ───

    fun loadFullProfileSafe(userId: Int, orgId: Int, token: String) {
        viewModelScope.launch {
            _userState.value = BaseResponse.Loading
            // Selalu success karena pakai default value — gak perlu handle error
            repository.getFullProfileDataSafe(userId, orgId, token).handleBaseResponse(
                onSuccess = { profile ->
                    _userState.value = BaseResponse.Success(profile.user)
                    _message.value = "Profile loaded (safe): ${profile.user.name}"
                },
            )
        }
    }

    // ─── SEQUENTIAL: Endpoint berurutan, saling menunggu ───

    fun registerAndLogin(name: String, email: String, password: String) {
        viewModelScope.launch {
            _message.value = "Mendaftarkan..."
            repository.registerAndLogin(name, email, password).handleBaseResponse(
                onError = { msg -> _message.value = msg },
                onSuccess = { token ->
                    _message.value = "Register & login berhasil"
                    // Simpan token ke preference/datastore
                },
            )
        }
    }

    // ─── REAL-WORLD: Pattern production ───

    fun updateProfileWithAvatar(userId: Int, avatarPart: MultipartBody.Part, name: String, email: String) {
        viewModelScope.launch {
            _message.value = "Uploading..."
            repository.updateProfileWithAvatar(userId, avatarPart, name, email).handleBaseResponse(
                onError = { msg -> _message.value = msg },
                onSuccess = { user ->
                    _userState.value = BaseResponse.Success(user)
                    _message.value = "Profile ${user.name} berhasil diupdate"
                },
            )
        }
    }

    fun loadAllUsers() {
        viewModelScope.launch {
            _usersState.value = BaseResponse.Loading
            repository.getAllUsers().handleBaseResponse(
                onError = { msg -> _message.value = msg },
                onSuccess = { users ->
                    _usersState.value = BaseResponse.Success(users)
                    _message.value = "Loaded semua ${users.size} users"
                },
            )
        }
    }

    fun batchDeleteUsers(userIds: List<Int>) {
        viewModelScope.launch {
            repository.deleteUsers(userIds).handleBaseResponse(
                onError = { msg -> _message.value = msg },
                onSuccess = { deletedIds ->
                    _message.value = "${deletedIds.size}/${userIds.size} user berhasil dihapus"
                    // Refresh list setelah delete
                    loadUsers()
                },
            )
        }
    }

    fun searchUsers(query: String) {
        viewModelScope.launch {
            repository.searchUsers(query).handleBaseResponse(
                onEmpty = { _usersState.value = BaseResponse.Empty },
                onError = { msg -> _message.value = msg },
                onSuccess = { users -> _usersState.value = BaseResponse.Success(users) },
            )
        }
    }

    fun createUserIfNotExists(name: String, email: String) {
        viewModelScope.launch {
            repository.createUserIfNotExists(name, email).handleBaseResponse(
                onError = { msg -> _message.value = msg },
                onSuccess = { user -> _message.value = "User ${user.name} berhasil dibuat" },
            )
        }
    }
}
