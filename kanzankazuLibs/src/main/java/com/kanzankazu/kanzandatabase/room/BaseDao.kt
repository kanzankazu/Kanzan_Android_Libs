package com.kanzankazu.kanzandatabase.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.RawQuery
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteQuery
import io.reactivex.Completable

/**
 * Base DAO generic untuk Room Database.
 *
 * Menyediakan operasi CRUD standar dengan dua style:
 * - **Suspend** (Coroutine) — untuk project modern, return value langsung
 * - **RxJava2** (Completable) — untuk project legacy, return Completable
 *
 * ## Cara Pakai
 * ```kotlin
 * @Dao
 * interface UserDao : BaseDao<UserEntity> {
 *     @Query("SELECT * FROM users")
 *     suspend fun getAll(): List<UserEntity>
 *
 *     @Query("SELECT * FROM users WHERE id = :id")
 *     suspend fun getById(id: Int): UserEntity?
 *
 *     @Query("SELECT * FROM users WHERE name LIKE '%' || :query || '%'")
 *     suspend fun search(query: String): List<UserEntity>
 * }
 * ```
 *
 * ## Catatan
 * - `@Query` untuk read operations harus didefinisikan di child DAO karena butuh table name spesifik.
 * - `insertOrUpdate` (upsert) pakai `REPLACE` strategy — hati-hati, ini DELETE + INSERT jika conflict,
 *   bukan UPDATE. Gunakan `insertOrIgnore` + `update` manual jika perlu preserve data.
 * - Method RxJava2 (suffix `Rx`) tersedia untuk backward compatibility.
 */
@Dao
interface BaseDao<T> {

    // ═══════════════════════════════════════════════════════════
    // INSERT — Suspend (Coroutine)
    // ═══════════════════════════════════════════════════════════

    /**
     * Insert single item. Ignore jika sudah ada (conflict pada primary key).
     * @return rowId dari item yang di-insert, atau -1 jika ignored.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(data: T): Long

    /**
     * Insert multiple items. Ignore jika sudah ada.
     * @return list rowId, -1 untuk item yang di-ignore.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(data: List<T>): List<Long>

    /**
     * Insert atau replace (upsert). Jika primary key sudah ada, DELETE lama lalu INSERT baru.
     * ⚠️ Ini bukan UPDATE — semua column akan di-reset ke value baru.
     * @return rowId dari item yang di-insert/replace.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(data: T): Long

    /**
     * Insert atau replace multiple items (upsert batch).
     * @return list rowId.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplaceAll(data: List<T>): List<Long>

    // ═══════════════════════════════════════════════════════════
    // UPDATE — Suspend (Coroutine)
    // ═══════════════════════════════════════════════════════════

    /**
     * Update single item berdasarkan primary key.
     * @return jumlah row yang ter-update (0 jika tidak ditemukan).
     */
    @Update
    suspend fun update(data: T): Int

    /**
     * Update multiple items berdasarkan primary key.
     * @return jumlah row yang ter-update.
     */
    @Update
    suspend fun updateAll(data: List<T>): Int

    // ═══════════════════════════════════════════════════════════
    // DELETE — Suspend (Coroutine)
    // ═══════════════════════════════════════════════════════════

    /**
     * Delete single item berdasarkan primary key.
     * @return jumlah row yang ter-delete (0 jika tidak ditemukan).
     */
    @Delete
    suspend fun delete(data: T): Int

    /**
     * Delete multiple items berdasarkan primary key.
     * @return jumlah row yang ter-delete.
     */
    @Delete
    suspend fun deleteAll(data: List<T>): Int

    // ═══════════════════════════════════════════════════════════
    // RAW QUERY — Untuk dynamic query
    // ═══════════════════════════════════════════════════════════

    /**
     * Execute raw query. Berguna untuk dynamic WHERE clause, JOIN, dll.
     *
     * ```kotlin
     * val query = SimpleSQLiteQuery("SELECT * FROM users WHERE age > ?", arrayOf(18))
     * val cursor = dao.rawQuery(query)
     * ```
     */
    @RawQuery
    suspend fun rawQuery(query: SupportSQLiteQuery): Int

    // ═══════════════════════════════════════════════════════════
    // INSERT — RxJava2 (Legacy)
    // ═══════════════════════════════════════════════════════════

    /** Insert single item (RxJava2). */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertRx(data: T): Completable

    /** Insert multiple items (RxJava2). */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertAllRx(data: List<T>): Completable

    /** Insert atau replace (RxJava2). */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrReplaceRx(data: T): Completable

    // ═══════════════════════════════════════════════════════════
    // UPDATE — RxJava2 (Legacy)
    // ═══════════════════════════════════════════════════════════

    /** Update single item (RxJava2). */
    @Update
    fun updateRx(data: T): Completable

    /** Update multiple items (RxJava2). */
    @Update
    fun updateAllRx(data: List<T>): Completable

    // ═══════════════════════════════════════════════════════════
    // DELETE — RxJava2 (Legacy)
    // ═══════════════════════════════════════════════════════════

    /** Delete single item (RxJava2). */
    @Delete
    fun deleteRx(data: T): Completable

    /** Delete multiple items (RxJava2). */
    @Delete
    fun deleteAllRx(data: List<T>): Completable
}
