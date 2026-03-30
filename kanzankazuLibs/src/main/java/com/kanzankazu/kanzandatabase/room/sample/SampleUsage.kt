@file:Suppress("unused")

package com.kanzankazu.kanzandatabase.room.sample

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Relation
import androidx.room.RoomDatabase
import androidx.room.Transaction
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kanzankazu.kanzandatabase.room.BaseDao
import com.kanzankazu.kanzandatabase.room.BaseRepository
import com.kanzankazu.kanzandatabase.room.BaseRoomDatabase
import com.kanzankazu.kanzannetwork.response.kanzanbaseresponse.BaseResponse
import com.kanzankazu.kanzannetwork.response.kanzanbaseresponse.handleBaseResponse
import com.kanzankazu.kanzannetwork.response.kanzanbaseresponse.handleBaseResponseCombineData
import com.kanzankazu.kanzannetwork.response.kanzanbaseresponse.handleBaseResponseConvertData
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ============================================================
// 1. ENTITY — Definisi tabel database
// ============================================================

/**
 * Entity sederhana — single table, auto-generate primary key.
 *
 * Annotation penting:
 * - @Entity: Menandai class sebagai tabel Room
 * - @PrimaryKey: Primary key, `autoGenerate = true` untuk auto-increment
 * - @ColumnInfo: Kustomisasi nama kolom (opsional, default = nama property)
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "email")
    val email: String,

    @ColumnInfo(name = "age")
    val age: Int = 0,

    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long = System.currentTimeMillis(),
)

/**
 * Entity dengan Index — untuk query yang sering di-filter/sort.
 * Index mempercepat SELECT tapi memperlambat INSERT/UPDATE.
 *
 * - `indices`: Buat index pada kolom tertentu
 * - `unique = true`: Kolom harus unik (seperti UNIQUE constraint)
 */
@Entity(
    tableName = "categories",
    indices = [
        Index(value = ["name"], unique = true),
        Index(value = ["sort_order"]),
    ]
)
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "description")
    val description: String = "",

    @ColumnInfo(name = "sort_order")
    val sortOrder: Int = 0,
)

/**
 * Entity dengan ForeignKey — relasi antar tabel.
 *
 * - `foreignKeys`: Definisi foreign key constraint
 * - `onDelete = CASCADE`: Jika parent (Category) dihapus, semua child (Product) ikut terhapus
 * - `onDelete = SET_NULL`: Jika parent dihapus, foreign key di child di-set null
 * - Index pada foreign key column WAJIB untuk performa
 */
@Entity(
    tableName = "products",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["created_by"],
            onDelete = ForeignKey.SET_NULL,
        ),
    ],
    indices = [
        Index(value = ["category_id"]),
        Index(value = ["created_by"]),
        Index(value = ["sku"], unique = true),
    ]
)
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "sku")
    val sku: String,

    @ColumnInfo(name = "price")
    val price: Double = 0.0,

    @ColumnInfo(name = "stock")
    val stock: Int = 0,

    @ColumnInfo(name = "category_id")
    val categoryId: Int,

    @ColumnInfo(name = "created_by")
    val createdBy: Int? = null,

    @ColumnInfo(name = "tags")
    val tags: List<String> = emptyList(),

    @ColumnInfo(name = "is_available")
    val isAvailable: Boolean = true,
)

/**
 * Entity dengan Composite Primary Key — untuk junction/pivot table (many-to-many).
 *
 * - `primaryKeys`: Array of column names sebagai composite key
 * - Tidak pakai @PrimaryKey di property, tapi di @Entity annotation
 */
@Entity(
    tableName = "user_favorites",
    primaryKeys = ["user_id", "product_id"],
    foreignKeys = [
        ForeignKey(entity = UserEntity::class, parentColumns = ["id"], childColumns = ["user_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = ProductEntity::class, parentColumns = ["id"], childColumns = ["product_id"], onDelete = ForeignKey.CASCADE),
    ],
    indices = [
        Index(value = ["product_id"]),
    ]
)
data class UserFavoriteEntity(
    @ColumnInfo(name = "user_id")
    val userId: Int,

    @ColumnInfo(name = "product_id")
    val productId: Int,

    @ColumnInfo(name = "added_at")
    val addedAt: Long = System.currentTimeMillis(),
)

// ============================================================
// 2. RELATION MODELS — Untuk query JOIN / nested data
// ============================================================

/**
 * One-to-Many: User dengan list Product yang dia buat.
 *
 * - @Embedded: Entity utama (parent)
 * - @Relation: Entity terkait (children)
 *   - parentColumn: Kolom di parent entity
 *   - entityColumn: Kolom di child entity yang reference ke parent
 *
 * ⚠️ Query yang return Relation HARUS pakai @Transaction
 */
data class UserWithProducts(
    @Embedded val user: UserEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "created_by",
    )
    val products: List<ProductEntity>,
)

/**
 * One-to-One: Product dengan Category-nya.
 */
data class ProductWithCategory(
    @Embedded val product: ProductEntity,
    @Relation(
        parentColumn = "category_id",
        entityColumn = "id",
    )
    val category: CategoryEntity?,
)

/**
 * Many-to-Many: User dengan favorite Products (via junction table).
 *
 * - associateBy: Junction table yang menghubungkan kedua entity
 */
data class UserWithFavorites(
    @Embedded val user: UserEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = androidx.room.Junction(
            value = UserFavoriteEntity::class,
            parentColumn = "user_id",
            entityColumn = "product_id",
        )
    )
    val favoriteProducts: List<ProductEntity>,
)

// ============================================================
// 3. TYPE CONVERTER — Untuk tipe data non-primitif
// ============================================================

/**
 * TypeConverter untuk Room — convert tipe data yang tidak didukung Room secara native.
 *
 * Room hanya support: primitif, String, byte[].
 * Untuk List, Map, custom object, dll → perlu TypeConverter.
 *
 * Register di @Database annotation: `@TypeConverters(RoomTypeConverters::class)`
 */
class RoomTypeConverters {

    private val gson = Gson()

    /** List<String> ↔ JSON String */
    @TypeConverter
    fun fromStringList(value: List<String>): String = gson.toJson(value)

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, type) ?: emptyList()
    }

    /** List<Int> ↔ JSON String */
    @TypeConverter
    fun fromIntList(value: List<Int>): String = gson.toJson(value)

    @TypeConverter
    fun toIntList(value: String): List<Int> {
        val type = object : TypeToken<List<Int>>() {}.type
        return gson.fromJson(value, type) ?: emptyList()
    }
}

// ============================================================
// 4. DAO — Extend BaseDao + custom queries
// ============================================================

/**
 * DAO untuk UserEntity.
 *
 * Extends [BaseDao] untuk operasi CRUD standar (insert, update, delete).
 * Custom @Query ditambahkan di sini karena butuh table name spesifik.
 *
 * ## Query Pattern
 * - `suspend fun` → one-shot query, return data sekali
 * - `fun` yang return `Flow<T>` → observable query, emit ulang setiap data berubah
 */
@Dao
interface UserDao : BaseDao<UserEntity> {

    // ─── SELECT: Basic ───

    /** Get semua users */
    @Query("SELECT * FROM users ORDER BY created_at DESC")
    suspend fun getAll(): List<UserEntity>

    /** Get user by ID — return null jika tidak ditemukan */
    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getById(id: Int): UserEntity?

    /** Get users by list of IDs */
    @Query("SELECT * FROM users WHERE id IN (:ids)")
    suspend fun getByIds(ids: List<Int>): List<UserEntity>

    /** Get user by email (unique) */
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getByEmail(email: String): UserEntity?

    // ─── SELECT: Filter & Search ───

    /** Search by name (LIKE) — case insensitive */
    @Query("SELECT * FROM users WHERE name LIKE '%' || :query || '%' COLLATE NOCASE")
    suspend fun searchByName(query: String): List<UserEntity>

    /** Filter by active status */
    @Query("SELECT * FROM users WHERE is_active = :isActive ORDER BY name ASC")
    suspend fun getByActiveStatus(isActive: Boolean): List<UserEntity>

    /** Filter by age range */
    @Query("SELECT * FROM users WHERE age BETWEEN :minAge AND :maxAge ORDER BY age ASC")
    suspend fun getByAgeRange(minAge: Int, maxAge: Int): List<UserEntity>

    /** Filter by multiple conditions */
    @Query("SELECT * FROM users WHERE is_active = :isActive AND age >= :minAge ORDER BY name ASC")
    suspend fun getActiveUsersAboveAge(isActive: Boolean = true, minAge: Int): List<UserEntity>

    // ─── SELECT: Pagination ───

    /** Pagination dengan LIMIT + OFFSET */
    @Query("SELECT * FROM users ORDER BY created_at DESC LIMIT :limit OFFSET :offset")
    suspend fun getPaginated(limit: Int, offset: Int): List<UserEntity>

    /** Count total rows — untuk hitung total pages */
    @Query("SELECT COUNT(*) FROM users")
    suspend fun getCount(): Int

    /** Count dengan filter */
    @Query("SELECT COUNT(*) FROM users WHERE is_active = :isActive")
    suspend fun getCountByStatus(isActive: Boolean): Int

    // ─── SELECT: Aggregate ───

    /** Average age */
    @Query("SELECT AVG(age) FROM users WHERE is_active = 1")
    suspend fun getAverageAge(): Double?

    /** Max/Min age */
    @Query("SELECT MAX(age) FROM users")
    suspend fun getMaxAge(): Int?

    @Query("SELECT MIN(age) FROM users")
    suspend fun getMinAge(): Int?

    // ─── SELECT: Flow (Observable) ───

    /** Observe semua users — emit ulang setiap ada perubahan di tabel */
    @Query("SELECT * FROM users ORDER BY created_at DESC")
    fun observeAll(): Flow<List<UserEntity>>

    /** Observe single user by ID */
    @Query("SELECT * FROM users WHERE id = :id")
    fun observeById(id: Int): Flow<UserEntity?>

    /** Observe count */
    @Query("SELECT COUNT(*) FROM users")
    fun observeCount(): Flow<Int>

    /** Observe active users */
    @Query("SELECT * FROM users WHERE is_active = 1 ORDER BY name ASC")
    fun observeActiveUsers(): Flow<List<UserEntity>>

    // ─── SELECT: Relations (@Transaction) ───

    /** Get user dengan semua products yang dia buat */
    @Transaction
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserWithProducts(userId: Int): UserWithProducts?

    /** Get semua users dengan products mereka */
    @Transaction
    @Query("SELECT * FROM users ORDER BY name ASC")
    suspend fun getAllUsersWithProducts(): List<UserWithProducts>

    /** Get user dengan favorite products (many-to-many) */
    @Transaction
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserWithFavorites(userId: Int): UserWithFavorites?

    // ─── UPDATE: Partial (tanpa replace seluruh entity) ───

    /** Update nama saja */
    @Query("UPDATE users SET name = :name, updated_at = :updatedAt WHERE id = :id")
    suspend fun updateName(id: Int, name: String, updatedAt: Long = System.currentTimeMillis()): Int

    /** Update email saja */
    @Query("UPDATE users SET email = :email, updated_at = :updatedAt WHERE id = :id")
    suspend fun updateEmail(id: Int, email: String, updatedAt: Long = System.currentTimeMillis()): Int

    /** Toggle active status */
    @Query("UPDATE users SET is_active = NOT is_active, updated_at = :updatedAt WHERE id = :id")
    suspend fun toggleActive(id: Int, updatedAt: Long = System.currentTimeMillis()): Int

    /** Deactivate semua users yang sudah lama tidak update */
    @Query("UPDATE users SET is_active = 0 WHERE updated_at < :beforeTimestamp")
    suspend fun deactivateInactiveUsers(beforeTimestamp: Long): Int

    // ─── DELETE: Custom ───

    /** Delete by ID */
    @Query("DELETE FROM users WHERE id = :id")
    suspend fun deleteById(id: Int): Int

    /** Delete semua users */
    @Query("DELETE FROM users")
    suspend fun deleteAllUsers(): Int

    /** Delete inactive users */
    @Query("DELETE FROM users WHERE is_active = 0")
    suspend fun deleteInactiveUsers(): Int

    /** Delete by list of IDs */
    @Query("DELETE FROM users WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<Int>): Int

    // ─── SELECT: Exists check ───

    /** Cek apakah email sudah terdaftar */
    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE email = :email)")
    suspend fun isEmailExists(email: String): Boolean

    /** Cek apakah user exists by ID */
    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE id = :id)")
    suspend fun isExists(id: Int): Boolean
}

@Dao
interface CategoryDao : BaseDao<CategoryEntity> {

    @Query("SELECT * FROM categories ORDER BY sort_order ASC")
    suspend fun getAll(): List<CategoryEntity>

    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getById(id: Int): CategoryEntity?

    @Query("SELECT * FROM categories WHERE name = :name LIMIT 1")
    suspend fun getByName(name: String): CategoryEntity?

    @Query("DELETE FROM categories")
    suspend fun deleteAllCategories(): Int

    @Query("SELECT * FROM categories ORDER BY sort_order ASC")
    fun observeAll(): Flow<List<CategoryEntity>>
}

@Dao
interface ProductDao : BaseDao<ProductEntity> {

    @Query("SELECT * FROM products ORDER BY name ASC")
    suspend fun getAll(): List<ProductEntity>

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getById(id: Int): ProductEntity?

    @Query("SELECT * FROM products WHERE category_id = :categoryId ORDER BY name ASC")
    suspend fun getByCategoryId(categoryId: Int): List<ProductEntity>

    @Query("SELECT * FROM products WHERE is_available = 1 AND stock > 0 ORDER BY name ASC")
    suspend fun getAvailable(): List<ProductEntity>

    @Query("SELECT * FROM products WHERE price BETWEEN :minPrice AND :maxPrice ORDER BY price ASC")
    suspend fun getByPriceRange(minPrice: Double, maxPrice: Double): List<ProductEntity>

    @Query("SELECT * FROM products WHERE name LIKE '%' || :query || '%' COLLATE NOCASE")
    suspend fun search(query: String): List<ProductEntity>

    /** Get product dengan category-nya */
    @Transaction
    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getProductWithCategory(id: Int): ProductWithCategory?

    @Transaction
    @Query("SELECT * FROM products ORDER BY name ASC")
    suspend fun getAllWithCategory(): List<ProductWithCategory>

    @Query("UPDATE products SET stock = stock - :quantity WHERE id = :id AND stock >= :quantity")
    suspend fun decreaseStock(id: Int, quantity: Int): Int

    @Query("UPDATE products SET stock = stock + :quantity WHERE id = :id")
    suspend fun increaseStock(id: Int, quantity: Int): Int

    @Query("SELECT * FROM products ORDER BY name ASC")
    fun observeAll(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE category_id = :categoryId ORDER BY name ASC")
    fun observeByCategoryId(categoryId: Int): Flow<List<ProductEntity>>

    @Query("DELETE FROM products")
    suspend fun deleteAllProducts(): Int
}

@Dao
interface UserFavoriteDao : BaseDao<UserFavoriteEntity> {

    @Query("SELECT * FROM user_favorites WHERE user_id = :userId ORDER BY added_at DESC")
    suspend fun getFavoritesByUserId(userId: Int): List<UserFavoriteEntity>

    @Query("DELETE FROM user_favorites WHERE user_id = :userId AND product_id = :productId")
    suspend fun removeFavorite(userId: Int, productId: Int): Int

    @Query("SELECT EXISTS(SELECT 1 FROM user_favorites WHERE user_id = :userId AND product_id = :productId)")
    suspend fun isFavorite(userId: Int, productId: Int): Boolean

    @Query("SELECT COUNT(*) FROM user_favorites WHERE user_id = :userId")
    suspend fun getFavoriteCount(userId: Int): Int
}

// ============================================================
// 5. DATABASE — RoomDatabase subclass
// ============================================================

/**
 * App Database — single source of truth untuk semua tabel.
 *
 * - `entities`: Semua Entity class yang jadi tabel
 * - `version`: Schema version, increment setiap ada perubahan schema
 * - `exportSchema`: `true` untuk export schema JSON (berguna untuk migration testing)
 * - `@TypeConverters`: Register custom type converters
 */
@Database(
    entities = [
        UserEntity::class,
        CategoryEntity::class,
        ProductEntity::class,
        UserFavoriteEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(RoomTypeConverters::class)
abstract class SampleDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun categoryDao(): CategoryDao
    abstract fun productDao(): ProductDao
    abstract fun userFavoriteDao(): UserFavoriteDao

    companion object {
        private const val DATABASE_NAME = "sample_database"

        /**
         * Get singleton instance.
         * Pakai [BaseRoomDatabase.buildDatabase] untuk konsistensi.
         */
        fun getInstance(context: Context): SampleDatabase {
            return BaseRoomDatabase.buildDatabase(
                context = context,
                databaseClass = SampleDatabase::class.java,
                databaseName = DATABASE_NAME,
            )
        }

        /**
         * Contoh: Get instance dengan migration.
         */
        fun getInstanceWithMigration(context: Context): SampleDatabase {
            return BaseRoomDatabase.buildDatabase(
                context = context,
                databaseClass = SampleDatabase::class.java,
                databaseName = DATABASE_NAME,
                migrations = arrayOf(MIGRATION_1_2),
                fallbackToDestructiveMigration = false,
            )
        }

        /**
         * Contoh: Get instance dengan pre-populate data.
         */
        fun getInstanceWithSeed(context: Context): SampleDatabase {
            return BaseRoomDatabase.buildDatabase(
                context = context,
                databaseClass = SampleDatabase::class.java,
                databaseName = DATABASE_NAME,
                onCreateCallback = { db ->
                    // Pre-populate categories saat database pertama kali dibuat
                    db.execSQL("INSERT INTO categories (name, description, sort_order) VALUES ('Elektronik', 'Barang elektronik', 1)")
                    db.execSQL("INSERT INTO categories (name, description, sort_order) VALUES ('Pakaian', 'Baju, celana, dll', 2)")
                    db.execSQL("INSERT INTO categories (name, description, sort_order) VALUES ('Makanan', 'Makanan & minuman', 3)")
                },
            )
        }

        // ─── Migration contoh ───

        private val MIGRATION_1_2 = BaseRoomDatabase.createMigration(1, 2) { db ->
            db.execSQL("ALTER TABLE users ADD COLUMN phone TEXT DEFAULT ''")
        }
    }
}


// ============================================================
// 6. REPOSITORY — Extend BaseRepository, wrap DAO dengan BaseResponse
// ============================================================

/**
 * Repository untuk User operations.
 *
 * Extends [BaseRepository] yang menyediakan:
 * - [safeDbCall]: Wrap suspend operation → [BaseResponse]
 * - [safeDbCallNullable]: Wrap nullable result → [BaseResponse] (null = Empty)
 * - [safeDbCallList]: Wrap list result → [BaseResponse] (empty list = Empty)
 * - [safeDbCallFlow]: Wrap suspend operation → Flow<[BaseResponse]> (dengan Loading)
 * - [wrapFlowWithResponse]: Wrap Room Flow → Flow<[BaseResponse]>
 * - CRUD operations bawaan: insert, insertAll, update, delete, dll
 *
 * Pattern sama seperti Retrofit repository — return [BaseResponse] supaya
 * ViewModel tidak perlu tahu data dari network atau local DB.
 */
class UserRepository(
    override val dao: UserDao,
) : BaseRepository<UserEntity, UserDao>() {

    // ─── GET: One-shot (suspend) ───

    /** Get semua users. Return Empty jika tidak ada data. */
    suspend fun getAll(): BaseResponse<List<UserEntity>> = safeDbCallList {
        dao.getAll()
    }

    /** Get user by ID. Return Empty jika tidak ditemukan. */
    suspend fun getById(id: Int): BaseResponse<UserEntity> = safeDbCallNullable {
        dao.getById(id)
    }

    /** Get users by list of IDs. */
    suspend fun getByIds(ids: List<Int>): BaseResponse<List<UserEntity>> = safeDbCallList {
        dao.getByIds(ids)
    }

    /** Get user by email. */
    suspend fun getByEmail(email: String): BaseResponse<UserEntity> = safeDbCallNullable {
        dao.getByEmail(email)
    }

    // ─── GET: Search & Filter ───

    /** Search users by name. */
    suspend fun searchByName(query: String): BaseResponse<List<UserEntity>> = safeDbCallList {
        dao.searchByName(query)
    }

    /** Get users by active status. */
    suspend fun getByActiveStatus(isActive: Boolean): BaseResponse<List<UserEntity>> = safeDbCallList {
        dao.getByActiveStatus(isActive)
    }

    /** Get users by age range. */
    suspend fun getByAgeRange(minAge: Int, maxAge: Int): BaseResponse<List<UserEntity>> = safeDbCallList {
        dao.getByAgeRange(minAge, maxAge)
    }

    // ─── GET: Pagination ───

    /** Get paginated users. */
    suspend fun getPaginated(page: Int, pageSize: Int = 20): BaseResponse<PaginatedResult<UserEntity>> {
        return safeDbCall {
            val offset = (page - 1) * pageSize
            val items = dao.getPaginated(pageSize, offset)
            val totalCount = dao.getCount()
            val totalPages = if (totalCount == 0) 0 else (totalCount + pageSize - 1) / pageSize
            PaginatedResult(
                data = items,
                page = page,
                pageSize = pageSize,
                totalItems = totalCount,
                totalPages = totalPages,
            )
        }
    }

    // ─── GET: Aggregate ───

    /** Get user statistics. */
    suspend fun getStatistics(): BaseResponse<UserStatistics> = safeDbCall {
        UserStatistics(
            totalUsers = dao.getCount(),
            activeUsers = dao.getCountByStatus(true),
            inactiveUsers = dao.getCountByStatus(false),
            averageAge = dao.getAverageAge() ?: 0.0,
            maxAge = dao.getMaxAge() ?: 0,
            minAge = dao.getMinAge() ?: 0,
        )
    }

    // ─── GET: Flow (Observable) ───

    /** Observe semua users — auto-update saat data berubah. */
    fun observeAll(): Flow<BaseResponse<List<UserEntity>>> =
        wrapFlowWithResponse(dao.observeAll())

    /** Observe single user by ID. */
    fun observeById(id: Int): Flow<BaseResponse<UserEntity?>> =
        wrapFlowWithResponse(dao.observeById(id))

    /** Observe user count. */
    fun observeCount(): Flow<BaseResponse<Int>> =
        wrapFlowWithResponse(dao.observeCount())

    /** Observe active users. */
    fun observeActiveUsers(): Flow<BaseResponse<List<UserEntity>>> =
        wrapFlowWithResponse(dao.observeActiveUsers())

    // ─── GET: Relations ───

    /** Get user dengan semua products yang dia buat. */
    suspend fun getUserWithProducts(userId: Int): BaseResponse<UserWithProducts> = safeDbCallNullable {
        dao.getUserWithProducts(userId)
    }

    /** Get semua users dengan products mereka. */
    suspend fun getAllUsersWithProducts(): BaseResponse<List<UserWithProducts>> = safeDbCallList {
        dao.getAllUsersWithProducts()
    }

    /** Get user dengan favorite products. */
    suspend fun getUserWithFavorites(userId: Int): BaseResponse<UserWithFavorites> = safeDbCallNullable {
        dao.getUserWithFavorites(userId)
    }

    // ─── INSERT: Custom (beyond BaseRepository) ───

    /**
     * Create user dengan validasi email unik.
     * Cek dulu apakah email sudah terdaftar sebelum insert.
     */
    suspend fun createUser(name: String, email: String, age: Int = 0): BaseResponse<UserEntity> {
        // Validasi email unik
        if (dao.isEmailExists(email)) {
            return BaseResponse.Error("Email '$email' sudah terdaftar")
        }

        val entity = UserEntity(name = name, email = email, age = age)
        val rowId = dao.insert(entity)
        return if (rowId > 0) {
            // Fetch kembali untuk dapat entity dengan ID yang benar
            safeDbCallNullable { dao.getByEmail(email) }
        } else {
            BaseResponse.Error("Gagal membuat user")
        }
    }

    /**
     * Bulk create users. Skip yang email-nya sudah ada (IGNORE strategy).
     * Return jumlah user yang berhasil di-insert.
     */
    suspend fun createUsers(users: List<UserEntity>): BaseResponse<Int> = safeDbCall {
        val rowIds = dao.insertAll(users)
        rowIds.count { it > 0 } // hitung yang berhasil (bukan -1)
    }

    // ─── UPDATE: Partial ───

    /** Update nama user. */
    suspend fun updateName(id: Int, name: String): BaseResponse<Int> = safeDbCall {
        dao.updateName(id, name)
    }

    /** Update email user dengan validasi unik. */
    suspend fun updateEmail(id: Int, email: String): BaseResponse<Int> {
        if (dao.isEmailExists(email)) {
            val existing = dao.getByEmail(email)
            if (existing != null && existing.id != id) {
                return BaseResponse.Error("Email '$email' sudah dipakai user lain")
            }
        }
        return safeDbCall { dao.updateEmail(id, email) }
    }

    /** Toggle active status. */
    suspend fun toggleActive(id: Int): BaseResponse<Int> = safeDbCall {
        dao.toggleActive(id)
    }

    /** Deactivate users yang tidak update sejak [beforeTimestamp]. */
    suspend fun deactivateInactiveUsers(beforeTimestamp: Long): BaseResponse<Int> = safeDbCall {
        dao.deactivateInactiveUsers(beforeTimestamp)
    }

    // ─── DELETE: Custom ───

    /** Delete user by ID. */
    suspend fun deleteById(id: Int): BaseResponse<Int> = safeDbCall {
        dao.deleteById(id)
    }

    /** Delete semua users. */
    suspend fun deleteAllUsers(): BaseResponse<Int> = safeDbCall {
        dao.deleteAllUsers()
    }

    /** Delete inactive users. */
    suspend fun deleteInactiveUsers(): BaseResponse<Int> = safeDbCall {
        dao.deleteInactiveUsers()
    }

    /** Batch delete by IDs. */
    suspend fun deleteByIds(ids: List<Int>): BaseResponse<Int> = safeDbCall {
        dao.deleteByIds(ids)
    }

    // ─── CHECK: Exists ───

    /** Cek apakah email sudah terdaftar. */
    suspend fun isEmailExists(email: String): BaseResponse<Boolean> = safeDbCall {
        dao.isEmailExists(email)
    }

    /** Cek apakah user exists. */
    suspend fun isExists(id: Int): BaseResponse<Boolean> = safeDbCall {
        dao.isExists(id)
    }

    // ─── COMPLEX: Kombinasi operasi ───

    /**
     * Contoh: Import users dari API response ke local DB.
     * Clear existing → insert semua baru (full sync).
     */
    suspend fun syncFromRemote(remoteUsers: List<UserEntity>): BaseResponse<Int> = safeDbCall {
        dao.deleteAllUsers()
        val rowIds = dao.insertAll(remoteUsers)
        rowIds.count { it > 0 }
    }

    /**
     * Contoh: Upsert — insert jika belum ada, update jika sudah ada.
     * Pakai insertOrReplace (REPLACE strategy).
     */
    suspend fun upsertUser(user: UserEntity): BaseResponse<UserEntity> = safeDbCall {
        dao.insertOrReplace(user)
        dao.getById(user.id) ?: user
    }

    /**
     * Contoh: Soft delete — set is_active = false instead of actual delete.
     */
    suspend fun softDelete(id: Int): BaseResponse<Int> = safeDbCall {
        dao.toggleActive(id) // toggle, tapi di real app bisa pakai dedicated query
    }
}

class ProductRepository(
    override val dao: ProductDao,
    private val categoryDao: CategoryDao,
) : BaseRepository<ProductEntity, ProductDao>() {

    suspend fun getAll(): BaseResponse<List<ProductEntity>> = safeDbCallList {
        dao.getAll()
    }

    suspend fun getById(id: Int): BaseResponse<ProductEntity> = safeDbCallNullable {
        dao.getById(id)
    }

    suspend fun getByCategoryId(categoryId: Int): BaseResponse<List<ProductEntity>> = safeDbCallList {
        dao.getByCategoryId(categoryId)
    }

    suspend fun getAvailable(): BaseResponse<List<ProductEntity>> = safeDbCallList {
        dao.getAvailable()
    }

    suspend fun getByPriceRange(minPrice: Double, maxPrice: Double): BaseResponse<List<ProductEntity>> = safeDbCallList {
        dao.getByPriceRange(minPrice, maxPrice)
    }

    suspend fun search(query: String): BaseResponse<List<ProductEntity>> = safeDbCallList {
        dao.search(query)
    }

    /** Get product dengan category-nya. */
    suspend fun getProductWithCategory(id: Int): BaseResponse<ProductWithCategory> = safeDbCallNullable {
        dao.getProductWithCategory(id)
    }

    /** Get semua products dengan category. */
    suspend fun getAllWithCategory(): BaseResponse<List<ProductWithCategory>> = safeDbCallList {
        dao.getAllWithCategory()
    }

    /** Decrease stock (atomic operation). Return error jika stock tidak cukup. */
    suspend fun decreaseStock(id: Int, quantity: Int): BaseResponse<Int> {
        val affected = dao.decreaseStock(id, quantity)
        return if (affected > 0) BaseResponse.Success(affected)
        else BaseResponse.Error("Stock tidak cukup atau product tidak ditemukan")
    }

    /** Increase stock. */
    suspend fun increaseStock(id: Int, quantity: Int): BaseResponse<Int> = safeDbCall {
        dao.increaseStock(id, quantity)
    }

    /** Observe products by category. */
    fun observeByCategoryId(categoryId: Int): Flow<BaseResponse<List<ProductEntity>>> =
        wrapFlowWithResponse(dao.observeByCategoryId(categoryId))

    fun observeAll(): Flow<BaseResponse<List<ProductEntity>>> =
        wrapFlowWithResponse(dao.observeAll())

    /**
     * Contoh: Create product dengan validasi category exists.
     */
    suspend fun createProduct(
        name: String,
        sku: String,
        price: Double,
        stock: Int,
        categoryId: Int,
        createdBy: Int? = null,
    ): BaseResponse<ProductEntity> {
        // Validasi category exists
        val category = categoryDao.getById(categoryId)
            ?: return BaseResponse.Error("Category dengan ID $categoryId tidak ditemukan")

        val entity = ProductEntity(
            name = name,
            sku = sku,
            price = price,
            stock = stock,
            categoryId = categoryId,
            createdBy = createdBy,
        )
        return safeDbCall {
            dao.insertOrReplace(entity)
            dao.getById(entity.id) ?: entity
        }
    }
}

// ─── Helper data classes ───

data class PaginatedResult<T>(
    val data: List<T>,
    val page: Int,
    val pageSize: Int,
    val totalItems: Int,
    val totalPages: Int,
) {
    val hasNextPage: Boolean get() = page < totalPages
    val hasPreviousPage: Boolean get() = page > 1
}

data class UserStatistics(
    val totalUsers: Int,
    val activeUsers: Int,
    val inactiveUsers: Int,
    val averageAge: Double,
    val maxAge: Int,
    val minAge: Int,
)

// ============================================================
// 7. VIEWMODEL — Consume di UI layer
//    Pattern sama persis dengan Retrofit ViewModel
// ============================================================

class UserViewModel(private val repository: UserRepository) : ViewModel() {

    private val _usersState = MutableStateFlow<BaseResponse<List<UserEntity>>>(BaseResponse.Loading)
    val usersState = _usersState.asStateFlow()

    private val _userState = MutableStateFlow<BaseResponse<UserEntity>>(BaseResponse.Loading)
    val userState = _userState.asStateFlow()

    private val _message = MutableStateFlow("")
    val message = _message.asStateFlow()

    // ─── GET: suspend + handleBaseResponse ───

    fun loadUsers() {
        viewModelScope.launch {
            _usersState.value = BaseResponse.Loading
            val result = repository.getAll()
            _usersState.value = result

            result.handleBaseResponse(
                onLoading = { /* show/hide loading */ },
                onEmpty = { _message.value = "Belum ada user" },
                onError = { msg -> _message.value = msg },
                onSuccess = { users -> _message.value = "Loaded ${users.size} users" },
            )
        }
    }

    // ─── GET: Flow style — auto-update saat data berubah ───

    fun observeUsers() {
        viewModelScope.launch {
            repository.observeAll().collect { response ->
                _usersState.value = response

                response.handleBaseResponse(
                    onLoading = { /* loading */ },
                    onError = { msg -> _message.value = msg },
                    onSuccess = { users -> _message.value = "${users.size} users (live)" },
                )
            }
        }
    }

    // ─── GET: by ID ───

    fun loadUser(id: Int) {
        viewModelScope.launch {
            _userState.value = BaseResponse.Loading
            repository.getById(id).handleBaseResponse(
                onEmpty = { _message.value = "User tidak ditemukan" },
                onError = { msg -> _message.value = msg },
                onSuccess = { user ->
                    _userState.value = BaseResponse.Success(user)
                    _message.value = "User: ${user.name}"
                },
            )
        }
    }

    // ─── GET: Search ───

    fun searchUsers(query: String) {
        viewModelScope.launch {
            if (query.length < 2) {
                _usersState.value = BaseResponse.Empty
                return@launch
            }
            repository.searchByName(query).handleBaseResponse(
                onEmpty = { _usersState.value = BaseResponse.Empty },
                onError = { msg -> _message.value = msg },
                onSuccess = { users -> _usersState.value = BaseResponse.Success(users) },
            )
        }
    }

    // ─── GET: Pagination ───

    fun loadUsersPaginated(page: Int, pageSize: Int = 20) {
        viewModelScope.launch {
            _usersState.value = BaseResponse.Loading
            repository.getPaginated(page, pageSize).handleBaseResponse(
                onError = { msg -> _message.value = msg },
                onSuccess = { paginated ->
                    _usersState.value = BaseResponse.Success(paginated.data)
                    _message.value = "Page ${paginated.page}/${paginated.totalPages} (${paginated.totalItems} total)"
                },
            )
        }
    }

    // ─── GET: Statistics ───

    fun loadStatistics() {
        viewModelScope.launch {
            repository.getStatistics().handleBaseResponse(
                onError = { msg -> _message.value = msg },
                onSuccess = { stats ->
                    _message.value = "Total: ${stats.totalUsers}, Active: ${stats.activeUsers}, Avg age: ${stats.averageAge}"
                },
            )
        }
    }

    // ─── GET: Filter ───

    fun loadActiveUsers() {
        viewModelScope.launch {
            repository.getByActiveStatus(true).handleBaseResponse(
                onEmpty = { _message.value = "Tidak ada user aktif" },
                onError = { msg -> _message.value = msg },
                onSuccess = { users -> _usersState.value = BaseResponse.Success(users) },
            )
        }
    }

    fun loadUsersByAgeRange(minAge: Int, maxAge: Int) {
        viewModelScope.launch {
            repository.getByAgeRange(minAge, maxAge).handleBaseResponse(
                onEmpty = { _message.value = "Tidak ada user di range $minAge-$maxAge" },
                onError = { msg -> _message.value = msg },
                onSuccess = { users -> _usersState.value = BaseResponse.Success(users) },
            )
        }
    }

    // ─── GET: Relations ───

    fun loadUserWithProducts(userId: Int) {
        viewModelScope.launch {
            repository.getUserWithProducts(userId).handleBaseResponse(
                onEmpty = { _message.value = "User tidak ditemukan" },
                onError = { msg -> _message.value = msg },
                onSuccess = { data ->
                    _userState.value = BaseResponse.Success(data.user)
                    _message.value = "${data.user.name} punya ${data.products.size} products"
                },
            )
        }
    }

    fun loadUserWithFavorites(userId: Int) {
        viewModelScope.launch {
            repository.getUserWithFavorites(userId).handleBaseResponse(
                onEmpty = { _message.value = "User tidak ditemukan" },
                onError = { msg -> _message.value = msg },
                onSuccess = { data ->
                    _userState.value = BaseResponse.Success(data.user)
                    _message.value = "${data.user.name} punya ${data.favoriteProducts.size} favorites"
                },
            )
        }
    }

    // ─── INSERT: Create user ───

    fun createUser(name: String, email: String, age: Int = 0) {
        viewModelScope.launch {
            repository.createUser(name, email, age).handleBaseResponse(
                onError = { msg -> _message.value = "Gagal: $msg" },
                onSuccess = { user -> _message.value = "User ${user.name} berhasil dibuat (ID: ${user.id})" },
            )
        }
    }

    // ─── INSERT: Bulk create ───

    fun createUsers(users: List<UserEntity>) {
        viewModelScope.launch {
            repository.createUsers(users).handleBaseResponse(
                onError = { msg -> _message.value = "Gagal: $msg" },
                onSuccess = { count -> _message.value = "$count users berhasil dibuat" },
            )
        }
    }

    // ─── UPDATE: Full entity ───

    fun updateUser(user: UserEntity) {
        viewModelScope.launch {
            repository.update(user.copy(updatedAt = System.currentTimeMillis())).handleBaseResponse(
                onError = { msg -> _message.value = "Update gagal: $msg" },
                onSuccess = { affected ->
                    _message.value = if (affected > 0) "User berhasil diupdate" else "User tidak ditemukan"
                },
            )
        }
    }

    // ─── UPDATE: Partial ───

    fun updateUserName(id: Int, newName: String) {
        viewModelScope.launch {
            repository.updateName(id, newName).handleBaseResponse(
                onError = { msg -> _message.value = "Update gagal: $msg" },
                onSuccess = { affected ->
                    _message.value = if (affected > 0) "Nama diubah ke $newName" else "User tidak ditemukan"
                },
            )
        }
    }

    fun updateUserEmail(id: Int, newEmail: String) {
        viewModelScope.launch {
            repository.updateEmail(id, newEmail).handleBaseResponse(
                onError = { msg -> _message.value = msg },
                onSuccess = { affected ->
                    _message.value = if (affected > 0) "Email diubah ke $newEmail" else "User tidak ditemukan"
                },
            )
        }
    }

    fun toggleUserActive(id: Int) {
        viewModelScope.launch {
            repository.toggleActive(id).handleBaseResponse(
                onError = { msg -> _message.value = msg },
                onSuccess = { _message.value = "Status berhasil diubah" },
            )
        }
    }

    // ─── DELETE ───

    fun deleteUser(id: Int) {
        viewModelScope.launch {
            repository.deleteById(id).handleBaseResponse(
                onError = { msg -> _message.value = "Delete gagal: $msg" },
                onSuccess = { affected ->
                    _message.value = if (affected > 0) "User berhasil dihapus" else "User tidak ditemukan"
                },
            )
        }
    }

    fun deleteUsers(ids: List<Int>) {
        viewModelScope.launch {
            repository.deleteByIds(ids).handleBaseResponse(
                onError = { msg -> _message.value = "Delete gagal: $msg" },
                onSuccess = { affected -> _message.value = "$affected/${ids.size} users berhasil dihapus" },
            )
        }
    }

    fun deleteInactiveUsers() {
        viewModelScope.launch {
            repository.deleteInactiveUsers().handleBaseResponse(
                onError = { msg -> _message.value = msg },
                onSuccess = { affected -> _message.value = "$affected inactive users dihapus" },
            )
        }
    }

    fun deleteAllUsers() {
        viewModelScope.launch {
            repository.deleteAllUsers().handleBaseResponse(
                onError = { msg -> _message.value = msg },
                onSuccess = { affected -> _message.value = "Semua $affected users dihapus" },
            )
        }
    }

    // ─── CHECK: Exists ───

    fun checkEmailExists(email: String) {
        viewModelScope.launch {
            repository.isEmailExists(email).handleBaseResponse(
                onError = { msg -> _message.value = msg },
                onSuccess = { exists ->
                    _message.value = if (exists) "Email sudah terdaftar" else "Email tersedia"
                },
            )
        }
    }

    // ─── COMPLEX: Sync dari remote ───

    fun syncFromRemote(remoteUsers: List<UserEntity>) {
        viewModelScope.launch {
            _usersState.value = BaseResponse.Loading
            _message.value = "Syncing..."
            repository.syncFromRemote(remoteUsers).handleBaseResponse(
                onError = { msg -> _message.value = "Sync gagal: $msg" },
                onSuccess = { count ->
                    _message.value = "Sync berhasil: $count users"
                    loadUsers() // refresh list
                },
            )
        }
    }

    // ─── COMPLEX: Upsert ───

    fun upsertUser(user: UserEntity) {
        viewModelScope.launch {
            repository.upsertUser(user).handleBaseResponse(
                onError = { msg -> _message.value = msg },
                onSuccess = { result -> _message.value = "User ${result.name} saved" },
            )
        }
    }

    // ─── PARALLEL: Multiple DB operations sekaligus ───

    /**
     * Contoh: Dashboard butuh users + statistics sekaligus.
     * Pakai coroutineScope + async supaya jalan bareng.
     */
    fun loadDashboard() {
        viewModelScope.launch {
            _usersState.value = BaseResponse.Loading
            coroutineScope {
                val usersDeferred = async { repository.getAll() }
                val statsDeferred = async { repository.getStatistics() }

                val usersResult = usersDeferred.await()
                val statsResult = statsDeferred.await()

                handleBaseResponseCombineData(
                    mainBaseResponse = usersResult,
                    secondBaseResponse = statsResult,
                    onSuccess = { users, stats ->
                        DashboardData(
                            users = users.orEmpty(),
                            statistics = stats,
                        )
                    },
                ).handleBaseResponse(
                    onError = { msg -> _message.value = msg },
                    onSuccess = { dashboard ->
                        _usersState.value = BaseResponse.Success(dashboard.users)
                        dashboard.statistics?.let { stats ->
                            _message.value = "Dashboard: ${stats.totalUsers} users, ${stats.activeUsers} active"
                        }
                    },
                )
            }
        }
    }

    /**
     * Contoh: Load user detail + products + favorites sekaligus.
     * Semua harus berhasil.
     */
    fun loadUserFullProfile(userId: Int) {
        viewModelScope.launch {
            _userState.value = BaseResponse.Loading
            coroutineScope {
                val userDeferred = async { repository.getById(userId) }
                val productsDeferred = async { repository.getUserWithProducts(userId) }
                val favoritesDeferred = async { repository.getUserWithFavorites(userId) }

                val userResult = userDeferred.await()
                val productsResult = productsDeferred.await()
                val favoritesResult = favoritesDeferred.await()

                // Cek semua harus success
                val firstError = listOf(userResult, productsResult, favoritesResult)
                    .filterIsInstance<BaseResponse.Error>()
                    .firstOrNull()

                if (firstError != null) {
                    _message.value = firstError.message
                    return@coroutineScope
                }

                val user = (userResult as? BaseResponse.Success)?.data
                val products = (productsResult as? BaseResponse.Success)?.data?.products.orEmpty()
                val favorites = (favoritesResult as? BaseResponse.Success)?.data?.favoriteProducts.orEmpty()

                if (user != null) {
                    _userState.value = BaseResponse.Success(user)
                    _message.value = "${user.name}: ${products.size} products, ${favorites.size} favorites"
                } else {
                    _message.value = "User tidak ditemukan"
                }
            }
        }
    }

    // ─── SEQUENTIAL: Operasi berurutan ───

    /**
     * Contoh: Create user → lalu langsung set sebagai active.
     * Sequential karena toggle butuh user yang sudah ada.
     */
    fun createAndActivateUser(name: String, email: String, age: Int) {
        viewModelScope.launch {
            _message.value = "Membuat user..."
            val createResult = repository.createUser(name, email, age)
            if (createResult !is BaseResponse.Success) {
                _message.value = (createResult as? BaseResponse.Error)?.message ?: "Gagal membuat user"
                return@launch
            }

            val user = createResult.data
            if (!user.isActive) {
                repository.toggleActive(user.id)
            }
            _message.value = "User ${user.name} berhasil dibuat dan diaktifkan"
            loadUsers() // refresh
        }
    }

    /**
     * Contoh: Deactivate old users → lalu delete yang sudah inactive.
     * Cleanup flow.
     */
    fun cleanupOldUsers(inactiveSinceMillis: Long) {
        viewModelScope.launch {
            _message.value = "Cleaning up..."

            // Step 1: Deactivate users yang sudah lama tidak update
            val deactivateResult = repository.deactivateInactiveUsers(inactiveSinceMillis)
            val deactivated = (deactivateResult as? BaseResponse.Success)?.data ?: 0

            // Step 2: Delete semua inactive users
            val deleteResult = repository.deleteInactiveUsers()
            val deleted = (deleteResult as? BaseResponse.Success)?.data ?: 0

            _message.value = "Cleanup selesai: $deactivated deactivated, $deleted deleted"
            loadUsers() // refresh
        }
    }

    /**
     * Contoh: Convert BaseResponse data ke tipe lain pakai handleBaseResponseConvertData.
     * Berguna saat UI butuh format data yang berbeda dari entity.
     */
    fun loadUserNames() {
        viewModelScope.launch {
            repository.getAll()
                .handleBaseResponseConvertData { users ->
                    users.map { "${it.name} (${it.email})" }
                }
                .handleBaseResponse(
                    onEmpty = { _message.value = "Tidak ada user" },
                    onError = { msg -> _message.value = msg },
                    onSuccess = { names -> _message.value = "Users: ${names.joinToString(", ")}" },
                )
        }
    }
}

// ─── Helper data class ───

data class DashboardData(
    val users: List<UserEntity>,
    val statistics: UserStatistics?,
)
