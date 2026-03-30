package com.kanzankazu.kanzandatabase.room

import com.kanzankazu.kanzannetwork.response.kanzanbaseresponse.BaseResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Base Repository untuk Room Database operations.
 *
 * Menyediakan wrapper method yang membungkus DAO operations dengan:
 * - Error handling otomatis via [safeDbCall] dan [safeDbCallFlow]
 * - Konsisten return [BaseResponse] (sama seperti pattern di Retrofit/network layer)
 * - Support Coroutine (suspend) dan Flow
 *
 * ## Cara Pakai
 * ```kotlin
 * class UserRepository(
 *     override val dao: UserDao
 * ) : BaseRepository<UserEntity, UserDao>() {
 *
 *     suspend fun getAll(): BaseResponse<List<UserEntity>> = safeDbCall {
 *         dao.getAll()
 *     }
 *
 *     fun getAllFlow(): Flow<BaseResponse<List<UserEntity>>> = safeDbCallFlow {
 *         dao.getAll()
 *     }
 *
 *     suspend fun getById(id: Int): BaseResponse<UserEntity> = safeDbCall {
 *         dao.getById(id) ?: throw NoSuchElementException("User $id tidak ditemukan")
 *     }
 * }
 * ```
 *
 * ## Catatan
 * - [safeDbCall] menangkap semua Exception dan convert ke [BaseResponse.Error].
 * - [safeDbCallFlow] emit [BaseResponse.Loading] → lalu [BaseResponse.Success] atau [BaseResponse.Error].
 * - Untuk operasi yang return nullable (getById), throw exception di block jika null
 *   supaya ter-convert ke [BaseResponse.Error], atau handle manual dengan [safeDbCallNullable].
 *
 * @param T Entity type
 * @param D DAO type yang extends [BaseDao]
 */
abstract class BaseRepository<T, D : BaseDao<T>> {

    /** DAO instance, harus di-provide oleh subclass. */
    abstract val dao: D

    // ═══════════════════════════════════════════════════════════
    // SAFE DB CALL — Wrapper dengan error handling
    // ═══════════════════════════════════════════════════════════

    /**
     * Execute database operation dengan error handling.
     * Semua exception ditangkap dan di-convert ke [BaseResponse.Error].
     *
     * @param block Suspend function yang berisi database operation.
     * @return [BaseResponse.Success] dengan data, atau [BaseResponse.Error] jika gagal.
     *
     * ```kotlin
     * suspend fun getUsers(): BaseResponse<List<UserEntity>> = safeDbCall {
     *     dao.getAll()
     * }
     * ```
     */
    protected suspend fun <R> safeDbCall(block: suspend () -> R): BaseResponse<R> {
        return try {
            val result = block()
            BaseResponse.Success(result)
        } catch (e: Exception) {
            BaseResponse.Error(e.message ?: "Database error")
        }
    }

    /**
     * Execute database operation yang bisa return null.
     * Jika result null, return [BaseResponse.Empty] instead of wrapping null.
     *
     * @param block Suspend function yang return nullable.
     * @return [BaseResponse.Success], [BaseResponse.Empty], atau [BaseResponse.Error].
     *
     * ```kotlin
     * suspend fun getById(id: Int): BaseResponse<UserEntity> = safeDbCallNullable {
     *     dao.getById(id)
     * }
     * ```
     */
    protected suspend fun <R> safeDbCallNullable(block: suspend () -> R?): BaseResponse<R> {
        return try {
            val result = block()
            if (result != null) BaseResponse.Success(result)
            else BaseResponse.Empty
        } catch (e: Exception) {
            BaseResponse.Error(e.message ?: "Database error")
        }
    }

    /**
     * Execute database operation yang return List.
     * Jika list kosong, return [BaseResponse.Empty].
     *
     * @param block Suspend function yang return List.
     * @return [BaseResponse.Success] dengan list, [BaseResponse.Empty] jika kosong, atau [BaseResponse.Error].
     *
     * ```kotlin
     * suspend fun getAll(): BaseResponse<List<UserEntity>> = safeDbCallList {
     *     dao.getAll()
     * }
     * ```
     */
    protected suspend fun <R> safeDbCallList(block: suspend () -> List<R>): BaseResponse<List<R>> {
        return try {
            val result = block()
            if (result.isNotEmpty()) BaseResponse.Success(result)
            else BaseResponse.Empty
        } catch (e: Exception) {
            BaseResponse.Error(e.message ?: "Database error")
        }
    }

    /**
     * Execute database operation sebagai Flow.
     * Emit [BaseResponse.Loading] → lalu result.
     *
     * @param block Suspend function yang berisi database operation.
     * @return Flow yang emit [BaseResponse.Loading] lalu [BaseResponse.Success] atau [BaseResponse.Error].
     *
     * ```kotlin
     * fun getUsersFlow(): Flow<BaseResponse<List<UserEntity>>> = safeDbCallFlow {
     *     dao.getAll()
     * }
     * ```
     */
    protected fun <R> safeDbCallFlow(block: suspend () -> R): Flow<BaseResponse<R>> = flow {
        emit(BaseResponse.Loading)
        try {
            emit(BaseResponse.Success(block()))
        } catch (e: Exception) {
            emit(BaseResponse.Error(e.message ?: "Database error"))
        }
    }

    /**
     * Wrap existing Room Flow (dari `@Query` yang return `Flow<T>`) dengan BaseResponse.
     * Setiap emission dari source flow di-wrap jadi [BaseResponse.Success].
     *
     * @param sourceFlow Flow dari Room DAO (contoh: `dao.observeAll()`).
     * @return Flow yang emit [BaseResponse.Loading] lalu [BaseResponse.Success] per emission.
     *
     * ```kotlin
     * // Di DAO:
     * @Query("SELECT * FROM users")
     * fun observeAll(): Flow<List<UserEntity>>
     *
     * // Di Repository:
     * fun observeUsers(): Flow<BaseResponse<List<UserEntity>>> = wrapFlowWithResponse(dao.observeAll())
     * ```
     */
    protected fun <R> wrapFlowWithResponse(sourceFlow: Flow<R>): Flow<BaseResponse<R>> = flow {
        emit(BaseResponse.Loading)
        try {
            sourceFlow.collect { data -> emit(BaseResponse.Success(data)) }
        } catch (e: Exception) {
            emit(BaseResponse.Error(e.message ?: "Database error"))
        }
    }

    // ═══════════════════════════════════════════════════════════
    // COMMON OPERATIONS — Shortcut untuk operasi umum via BaseDao
    // ═══════════════════════════════════════════════════════════

    /**
     * Insert single item via [BaseDao.insert].
     * @return [BaseResponse.Success] dengan rowId, atau [BaseResponse.Error].
     */
    suspend fun insert(data: T): BaseResponse<Long> = safeDbCall {
        dao.insert(data)
    }

    /**
     * Insert multiple items via [BaseDao.insertAll].
     * @return [BaseResponse.Success] dengan list rowId, atau [BaseResponse.Error].
     */
    suspend fun insertAll(data: List<T>): BaseResponse<List<Long>> = safeDbCall {
        dao.insertAll(data)
    }

    /**
     * Insert atau replace (upsert) via [BaseDao.insertOrReplace].
     * @return [BaseResponse.Success] dengan rowId, atau [BaseResponse.Error].
     */
    suspend fun insertOrReplace(data: T): BaseResponse<Long> = safeDbCall {
        dao.insertOrReplace(data)
    }

    /**
     * Insert atau replace multiple items via [BaseDao.insertOrReplaceAll].
     * @return [BaseResponse.Success] dengan list rowId, atau [BaseResponse.Error].
     */
    suspend fun insertOrReplaceAll(data: List<T>): BaseResponse<List<Long>> = safeDbCall {
        dao.insertOrReplaceAll(data)
    }

    /**
     * Update single item via [BaseDao.update].
     * @return [BaseResponse.Success] dengan jumlah row ter-update, atau [BaseResponse.Error].
     */
    suspend fun update(data: T): BaseResponse<Int> = safeDbCall {
        dao.update(data)
    }

    /**
     * Update multiple items via [BaseDao.updateAll].
     * @return [BaseResponse.Success] dengan jumlah row ter-update, atau [BaseResponse.Error].
     */
    suspend fun updateAll(data: List<T>): BaseResponse<Int> = safeDbCall {
        dao.updateAll(data)
    }

    /**
     * Delete single item via [BaseDao.delete].
     * @return [BaseResponse.Success] dengan jumlah row ter-delete, atau [BaseResponse.Error].
     */
    suspend fun delete(data: T): BaseResponse<Int> = safeDbCall {
        dao.delete(data)
    }

    /**
     * Delete multiple items via [BaseDao.deleteAll].
     * @return [BaseResponse.Success] dengan jumlah row ter-delete, atau [BaseResponse.Error].
     */
    suspend fun deleteAll(data: List<T>): BaseResponse<Int> = safeDbCall {
        dao.deleteAll(data)
    }
}
