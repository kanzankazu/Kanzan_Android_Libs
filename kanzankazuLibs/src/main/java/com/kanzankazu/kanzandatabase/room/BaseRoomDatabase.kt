package com.kanzankazu.kanzandatabase.room

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import timber.log.Timber

/**
 * Helper object untuk membangun instance [RoomDatabase] dengan konfigurasi standar.
 *
 * Menyediakan builder pattern yang sudah include:
 * - Singleton pattern (double-checked locking)
 * - Migration support
 * - Fallback to destructive migration (opsional)
 * - Pre-populate callback (opsional)
 * - Logging via Timber
 *
 * ## Cara Pakai — Simple (tanpa migration)
 * ```kotlin
 * @Database(entities = [UserEntity::class], version = 1)
 * abstract class AppDatabase : RoomDatabase() {
 *     abstract fun userDao(): UserDao
 *
 *     companion object {
 *         fun getInstance(context: Context): AppDatabase {
 *             return BaseRoomDatabase.buildDatabase(
 *                 context = context,
 *                 databaseClass = AppDatabase::class.java,
 *                 databaseName = "app_database",
 *             )
 *         }
 *     }
 * }
 * ```
 *
 * ## Cara Pakai — Dengan Migration
 * ```kotlin
 * companion object {
 *     private val MIGRATION_1_2 = BaseRoomDatabase.createMigration(1, 2) {
 *         it.execSQL("ALTER TABLE users ADD COLUMN age INTEGER NOT NULL DEFAULT 0")
 *     }
 *
 *     fun getInstance(context: Context): AppDatabase {
 *         return BaseRoomDatabase.buildDatabase(
 *             context = context,
 *             databaseClass = AppDatabase::class.java,
 *             databaseName = "app_database",
 *             migrations = arrayOf(MIGRATION_1_2),
 *         )
 *     }
 * }
 * ```
 *
 * ## Cara Pakai — Dengan Pre-populate Data
 * ```kotlin
 * fun getInstance(context: Context): AppDatabase {
 *     return BaseRoomDatabase.buildDatabase(
 *         context = context,
 *         databaseClass = AppDatabase::class.java,
 *         databaseName = "app_database",
 *         onCreateCallback = { db ->
 *             db.execSQL("INSERT INTO settings (key, value) VALUES ('theme', 'light')")
 *         },
 *     )
 *     }
 * ```
 */
object BaseRoomDatabase {

    @Volatile
    private val instances = mutableMapOf<String, RoomDatabase>()

    /**
     * Build atau get existing instance dari [RoomDatabase].
     * Thread-safe dengan double-checked locking pattern.
     *
     * @param T Tipe RoomDatabase subclass.
     * @param context Application context (akan di-convert ke applicationContext otomatis).
     * @param databaseClass Class dari RoomDatabase subclass.
     * @param databaseName Nama file database (contoh: `"app_database"`).
     * @param migrations Array of [Migration] untuk schema migration. Default: empty.
     * @param fallbackToDestructiveMigration Jika `true`, database akan di-drop dan recreate
     *        saat migration path tidak ditemukan. Default: `false`.
     *        ⚠️ Semua data akan hilang jika ini aktif dan migration tidak tersedia.
     * @param onCreateCallback Callback yang dipanggil saat database pertama kali dibuat.
     *        Berguna untuk pre-populate data awal.
     * @param onOpenCallback Callback yang dipanggil setiap kali database dibuka.
     * @return Instance dari [T].
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : RoomDatabase> buildDatabase(
        context: Context,
        databaseClass: Class<T>,
        databaseName: String,
        migrations: Array<Migration> = emptyArray(),
        fallbackToDestructiveMigration: Boolean = false,
        onCreateCallback: ((SupportSQLiteDatabase) -> Unit)? = null,
        onOpenCallback: ((SupportSQLiteDatabase) -> Unit)? = null,
    ): T {
        return instances[databaseName] as? T ?: synchronized(this) {
            instances[databaseName] as? T ?: run {
                val builder = Room.databaseBuilder(
                    context.applicationContext,
                    databaseClass,
                    databaseName,
                )

                // Add migrations
                if (migrations.isNotEmpty()) {
                    builder.addMigrations(*migrations)
                    Timber.d("Room: Added ${migrations.size} migration(s) for '$databaseName'")
                }

                // Fallback destructive migration
                if (fallbackToDestructiveMigration) {
                    builder.fallbackToDestructiveMigration()
                    Timber.w("Room: Destructive migration ENABLED for '$databaseName' — data bisa hilang!")
                }

                // Callbacks
                if (onCreateCallback != null || onOpenCallback != null) {
                    builder.addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            Timber.d("Room: Database '$databaseName' created")
                            onCreateCallback?.invoke(db)
                        }

                        override fun onOpen(db: SupportSQLiteDatabase) {
                            super.onOpen(db)
                            Timber.d("Room: Database '$databaseName' opened")
                            onOpenCallback?.invoke(db)
                        }
                    })
                }

                builder.build().also {
                    instances[databaseName] = it
                    Timber.d("Room: Database '$databaseName' instance created")
                }
            }
        }
    }

    /**
     * Helper untuk membuat [Migration] dengan lambda.
     *
     * @param startVersion Versi awal database.
     * @param endVersion Versi tujuan database.
     * @param migrate Lambda yang berisi SQL statements untuk migration.
     * @return [Migration] instance.
     *
     * ```kotlin
     * val MIGRATION_1_2 = BaseRoomDatabase.createMigration(1, 2) { db ->
     *     db.execSQL("ALTER TABLE users ADD COLUMN phone TEXT")
     * }
     *
     * val MIGRATION_2_3 = BaseRoomDatabase.createMigration(2, 3) { db ->
     *     db.execSQL("CREATE TABLE IF NOT EXISTS settings (key TEXT PRIMARY KEY, value TEXT)")
     * }
     * ```
     */
    fun createMigration(
        startVersion: Int,
        endVersion: Int,
        migrate: (SupportSQLiteDatabase) -> Unit,
    ): Migration {
        return object : Migration(startVersion, endVersion) {
            override fun migrate(database: SupportSQLiteDatabase) {
                Timber.d("Room: Migrating from version $startVersion to $endVersion")
                migrate(database)
                Timber.d("Room: Migration $startVersion → $endVersion completed")
            }
        }
    }

    /**
     * Clear specific database instance dari cache.
     * Berguna untuk testing atau saat perlu recreate database.
     *
     * @param databaseName Nama database yang akan di-clear.
     */
    fun clearInstance(databaseName: String) {
        synchronized(this) {
            instances[databaseName]?.close()
            instances.remove(databaseName)
            Timber.d("Room: Instance '$databaseName' cleared")
        }
    }

    /**
     * Clear semua database instances dari cache.
     * Berguna saat logout atau app reset.
     */
    fun clearAllInstances() {
        synchronized(this) {
            instances.forEach { (name, db) ->
                db.close()
                Timber.d("Room: Instance '$name' closed")
            }
            instances.clear()
            Timber.d("Room: All instances cleared")
        }
    }
}
