# Requirements Document

## Introduction

Dokumen ini mendefinisikan requirements untuk pembuatan general/base class Appwrite di shared library `kanzankazuLibs`. Base class ini menyediakan abstraksi untuk semua fitur Appwrite (Database, Auth/Account, Storage, Realtime, Functions) yang bisa langsung dipakai oleh project utama. Desain mengikuti pola yang sudah ada di codebase Supabase (`interface + impl`, `BaseResponse<T>`, suspend + Flow support) dan menggunakan Appwrite Android SDK (`io.appwrite:sdk-for-android`).

Appwrite membutuhkan `Context` untuk inisialisasi client, berbeda dengan Supabase. Appwrite juga menggunakan konsep `databaseId` + `collectionId` untuk database operations, dan `bucketId` + `fileId` untuk storage operations.

## Glossary

- **AppwriteConfig**: Object konfigurasi yang menyimpan Appwrite Endpoint URL dan Project ID untuk inisialisasi client
- **AppwriteClientProvider**: Singleton yang mengelola lifecycle dan akses ke instance Appwrite `Client` beserta service objects (`Account`, `Databases`, `Storage`, `Realtime`, `Functions`)
- **AppwriteDatabase**: Interface yang mendefinisikan operasi CRUD (Create, Read, Update, Delete) terhadap Appwrite Database
- **AppwriteDatabaseImpl**: Implementasi konkret dari `AppwriteDatabase` yang menggunakan `io.appwrite.services.Databases`
- **AppwriteAuth**: Interface yang mendefinisikan operasi autentikasi pengguna (sign up, sign in, sign out, session management)
- **AppwriteAuthImpl**: Implementasi konkret dari `AppwriteAuth` yang menggunakan `io.appwrite.services.Account`
- **AppwriteStorage**: Interface yang mendefinisikan operasi file storage (upload, download, delete, get URL, preview)
- **AppwriteStorageImpl**: Implementasi konkret dari `AppwriteStorage` yang menggunakan `io.appwrite.services.Storage`
- **AppwriteRealtime**: Interface yang mendefinisikan operasi real-time subscription untuk mendengarkan perubahan data
- **AppwriteRealtimeImpl**: Implementasi konkret dari `AppwriteRealtime` yang menggunakan `io.appwrite.services.Realtime`
- **AppwriteFunctions**: Interface yang mendefinisikan operasi pemanggilan Appwrite Functions
- **AppwriteFunctionsImpl**: Implementasi konkret dari `AppwriteFunctions` yang menggunakan `io.appwrite.services.Functions`
- **BaseResponse**: Sealed interface (`Loading`, `Empty`, `Error`, `Success<T>`) yang sudah ada di codebase untuk membungkus hasil operasi
- **AppwriteFilterCondition**: Data class untuk mendefinisikan kondisi filter/query pada Appwrite Database (menggunakan Appwrite Query string format)
- **AppwriteFilterOperator**: Enum yang mendefinisikan operator filter Appwrite (EQUAL, NOT_EQUAL, GREATER_THAN, GREATER_THAN_OR_EQUAL, LESS_THAN, LESS_THAN_OR_EQUAL, SEARCH, IS_NULL, IS_NOT_NULL, BETWEEN, STARTS_WITH, ENDS_WITH, CONTAINS)

## Requirements

### Requirement 1: Konfigurasi dan Inisialisasi Appwrite Client

**User Story:** Sebagai developer, saya ingin menginisialisasi Appwrite client dengan konfigurasi Endpoint dan Project ID, sehingga semua fitur Appwrite bisa diakses dari satu titik konfigurasi.

#### Acceptance Criteria

1. WHEN `AppwriteClientProvider.initialize` dipanggil dengan `Context` dan `AppwriteConfig` yang valid (berisi Endpoint dan Project ID), THE AppwriteClientProvider SHALL membuat instance `Client` dan service objects (`Account`, `Databases`, `Storage`, `Realtime`, `Functions`)
2. WHEN `AppwriteClientProvider.getClient` dipanggil setelah inisialisasi berhasil, THE AppwriteClientProvider SHALL mengembalikan instance `Client` yang sama (singleton)
3. IF `AppwriteClientProvider.getClient` dipanggil sebelum `initialize`, THEN THE AppwriteClientProvider SHALL melempar `IllegalStateException` dengan pesan deskriptif
4. IF `AppwriteConfig` memiliki Endpoint atau Project ID yang kosong, THEN THE AppwriteClientProvider SHALL melempar `IllegalArgumentException` dengan pesan deskriptif
5. THE AppwriteConfig SHALL menyimpan `endpoint` (String) dan `projectId` (String) sebagai properti wajib
6. THE AppwriteClientProvider SHALL menyediakan getter untuk setiap service: `getAccount()`, `getDatabases()`, `getStorage()`, `getRealtime()`, `getFunctions()`

### Requirement 2: Base Class Database

**User Story:** Sebagai developer, saya ingin base class untuk operasi CRUD ke Appwrite Database, sehingga saya bisa melakukan query data tanpa menulis boilerplate code.

#### Acceptance Criteria

1. THE AppwriteDatabase SHALL mendefinisikan operasi `createDocument`, `listDocuments`, `getDocument`, `updateDocument`, dan `deleteDocument` dalam bentuk suspend function yang mengembalikan `BaseResponse<T>`
2. THE AppwriteDatabase SHALL mendefinisikan operasi `listDocumentsAsFlow` yang mengembalikan `Flow<BaseResponse<T>>` untuk reactive data streaming
3. WHEN operasi `createDocument` dipanggil dengan databaseId, collectionId, dan data map, THE AppwriteDatabaseImpl SHALL membuat document baru dan mengembalikan `BaseResponse.Success` dengan document data
4. WHEN operasi `listDocuments` dipanggil dengan databaseId, collectionId, dan optional queries, THE AppwriteDatabaseImpl SHALL mengambil semua document dan mengembalikan `BaseResponse.Success` berisi list of document
5. WHEN operasi `listDocuments` dipanggil dengan list of `AppwriteFilterCondition`, THE AppwriteDatabaseImpl SHALL menerapkan filter menggunakan Appwrite Query API dan mengembalikan data yang sesuai
6. WHEN operasi `updateDocument` dipanggil dengan databaseId, collectionId, documentId, dan data map, THE AppwriteDatabaseImpl SHALL mengupdate document dan mengembalikan `BaseResponse.Success`
7. WHEN operasi `deleteDocument` dipanggil dengan databaseId, collectionId, dan documentId, THE AppwriteDatabaseImpl SHALL menghapus document dan mengembalikan `BaseResponse.Success`
8. IF operasi database gagal karena exception, THEN THE AppwriteDatabaseImpl SHALL mengembalikan `BaseResponse.Error` dengan pesan error yang deskriptif
9. THE AppwriteDatabase SHALL mendefinisikan operasi `getDocument` yang menerima databaseId, collectionId, dan documentId, lalu mengembalikan `BaseResponse<T>` untuk single document
10. THE AppwriteDatabase SHALL mendefinisikan operasi `listDocumentsWithPagination` yang menerima databaseId, collectionId, limit, offset/cursor, dan optional queries
11. THE AppwriteDatabase SHALL mendefinisikan operasi `listDocumentsWithOrder` yang menerima databaseId, collectionId, orderAttributes, orderTypes, dan optional queries

### Requirement 3: Base Class Authentication

**User Story:** Sebagai developer, saya ingin base class untuk operasi autentikasi Appwrite, sehingga saya bisa mengimplementasikan sign up, sign in, dan session management dengan mudah.

#### Acceptance Criteria

1. THE AppwriteAuth SHALL mendefinisikan operasi `signUp`, `signInWithEmail`, `signOut`, `getCurrentUser`, `getCurrentSession`, dan `resetPassword` dalam bentuk suspend function yang mengembalikan `BaseResponse<T>`
2. WHEN `signUp` dipanggil dengan email dan password yang valid, THE AppwriteAuthImpl SHALL mendaftarkan user baru menggunakan `account.create(ID.unique(), email, password)` dan mengembalikan `BaseResponse.Success` berisi user info
3. WHEN `signInWithEmail` dipanggil dengan email dan password yang valid, THE AppwriteAuthImpl SHALL mengautentikasi user menggunakan `account.createEmailPasswordSession` dan mengembalikan `BaseResponse.Success` berisi session info
4. WHEN `signOut` dipanggil, THE AppwriteAuthImpl SHALL menghapus session aktif menggunakan `account.deleteSession("current")` dan mengembalikan `BaseResponse.Success`
5. WHEN `getCurrentUser` dipanggil saat ada session aktif, THE AppwriteAuthImpl SHALL mengembalikan `BaseResponse.Success` berisi data user menggunakan `account.get()`
6. IF `getCurrentUser` dipanggil saat tidak ada session aktif, THEN THE AppwriteAuthImpl SHALL mengembalikan `BaseResponse.Empty`
7. IF operasi autentikasi gagal karena credential salah atau exception, THEN THE AppwriteAuthImpl SHALL mengembalikan `BaseResponse.Error` dengan pesan error yang deskriptif
8. THE AppwriteAuth SHALL mendefinisikan operasi `signInWithOAuth` yang menerima provider type (Google, Facebook) dan mengembalikan `BaseResponse.Success`
9. WHEN `resetPassword` dipanggil dengan email yang valid, THE AppwriteAuthImpl SHALL mengirim email recovery menggunakan `account.createRecovery` dan mengembalikan `BaseResponse.Success`
10. THE AppwriteAuth SHALL mendefinisikan `observeAuthState` yang mengembalikan `Flow<BaseResponse<AuthState>>` untuk memantau perubahan status autentikasi

### Requirement 4: Base Class Storage

**User Story:** Sebagai developer, saya ingin base class untuk operasi file storage Appwrite, sehingga saya bisa upload, download, dan mengelola file dengan mudah.

#### Acceptance Criteria

1. THE AppwriteStorage SHALL mendefinisikan operasi `upload`, `download`, `delete`, `getFilePreview`, `getFileViewUrl`, dan `list` dalam bentuk suspend function yang mengembalikan `BaseResponse<T>`
2. WHEN operasi `upload` dipanggil dengan bucketId, file path, dan InputFile, THE AppwriteStorageImpl SHALL mengupload file menggunakan `storage.createFile` dan mengembalikan `BaseResponse.Success` berisi file ID
3. WHEN operasi `download` dipanggil dengan bucketId dan fileId, THE AppwriteStorageImpl SHALL mendownload file menggunakan `storage.getFileDownload` dan mengembalikan `BaseResponse.Success` berisi byte array
4. WHEN operasi `delete` dipanggil dengan bucketId dan fileId, THE AppwriteStorageImpl SHALL menghapus file menggunakan `storage.deleteFile` dan mengembalikan `BaseResponse.Success`
5. WHEN operasi `getFilePreview` dipanggil dengan bucketId dan fileId, THE AppwriteStorageImpl SHALL mengembalikan `BaseResponse.Success` berisi byte array preview image
6. WHEN operasi `getFileViewUrl` dipanggil dengan bucketId dan fileId, THE AppwriteStorageImpl SHALL mengembalikan `BaseResponse.Success` berisi URL string untuk view file
7. WHEN operasi `list` dipanggil dengan bucketId, THE AppwriteStorageImpl SHALL mengembalikan `BaseResponse.Success` berisi list of file metadata
8. IF operasi storage gagal karena exception (file tidak ditemukan, bucket tidak ada, permission denied), THEN THE AppwriteStorageImpl SHALL mengembalikan `BaseResponse.Error` dengan pesan error yang deskriptif
9. THE AppwriteStorage SHALL mendefinisikan operasi `uploadAsFlow` yang mengembalikan `Flow<BaseResponse<String>>` dengan emit `BaseResponse.Loading` di awal proses upload

### Requirement 5: Base Class Realtime

**User Story:** Sebagai developer, saya ingin base class untuk real-time subscription Appwrite, sehingga saya bisa mendengarkan perubahan data secara live menggunakan Flow.

#### Acceptance Criteria

1. THE AppwriteRealtime SHALL mendefinisikan operasi `subscribeToCollection` yang mengembalikan `Flow<BaseResponse<T>>` untuk mendengarkan perubahan document pada collection tertentu
2. WHEN `subscribeToCollection` dipanggil dengan databaseId dan collectionId, THE AppwriteRealtimeImpl SHALL membuat subscription menggunakan `realtime.subscribe("databases.{databaseId}.collections.{collectionId}.documents")` dan meng-emit setiap perubahan data sebagai `BaseResponse.Success`
3. THE AppwriteRealtime SHALL mendefinisikan operasi `subscribeToStorage` yang mengembalikan `Flow<BaseResponse<T>>` untuk mendengarkan perubahan file pada bucket tertentu
4. WHEN subscription dibatalkan (Flow collection dihentikan), THE AppwriteRealtimeImpl SHALL menutup subscription dan membersihkan resource
5. IF subscription gagal karena koneksi terputus atau error, THEN THE AppwriteRealtimeImpl SHALL meng-emit `BaseResponse.Error` dengan pesan error yang deskriptif
6. THE AppwriteRealtime SHALL mendefinisikan operasi `subscribeToDocument` untuk mendengarkan perubahan pada document spesifik
7. THE AppwriteRealtime SHALL mendefinisikan operasi `removeSubscription` untuk menghentikan subscription tertentu secara manual
8. THE AppwriteRealtime SHALL mendefinisikan operasi `removeAllSubscriptions` untuk menghentikan semua subscription aktif

### Requirement 6: Base Class Functions

**User Story:** Sebagai developer, saya ingin base class untuk memanggil Appwrite Functions, sehingga saya bisa menjalankan server-side logic tanpa menulis boilerplate code.

#### Acceptance Criteria

1. THE AppwriteFunctions SHALL mendefinisikan operasi `execute` dalam bentuk suspend function yang menerima function ID, optional request body, dan mengembalikan `BaseResponse<String>` berisi response body
2. WHEN operasi `execute` dipanggil dengan function ID yang valid, THE AppwriteFunctionsImpl SHALL memanggil Function menggunakan `functions.createExecution` dan mengembalikan `BaseResponse.Success` berisi response string
3. THE AppwriteFunctions SHALL mendefinisikan operasi `executeWithType` yang menerima function ID, optional request body, dan target class, lalu mengembalikan `BaseResponse<T>` berisi response yang sudah di-deserialize
4. IF pemanggilan Function gagal karena exception (function tidak ditemukan, timeout, server error), THEN THE AppwriteFunctionsImpl SHALL mengembalikan `BaseResponse.Error` dengan pesan error yang deskriptif
5. THE AppwriteFunctions SHALL mendefinisikan operasi `executeAsFlow` yang mengembalikan `Flow<BaseResponse<String>>` dengan emit `BaseResponse.Loading` di awal pemanggilan

### Requirement 7: Filter dan Query Builder untuk Database

**User Story:** Sebagai developer, saya ingin filter dan query builder yang konsisten untuk operasi Appwrite Database, sehingga saya bisa membangun query yang kompleks dengan mudah.

#### Acceptance Criteria

1. THE AppwriteFilterCondition SHALL menyimpan `attribute` (String), `value` (Any?), dan `operator` (AppwriteFilterOperator) sebagai properti
2. THE AppwriteFilterOperator SHALL mendefinisikan operator: EQUAL, NOT_EQUAL, GREATER_THAN, GREATER_THAN_OR_EQUAL, LESS_THAN, LESS_THAN_OR_EQUAL, SEARCH, IS_NULL, IS_NOT_NULL, BETWEEN, STARTS_WITH, ENDS_WITH, CONTAINS
3. WHEN list of `AppwriteFilterCondition` diberikan ke operasi `listDocuments`, THE AppwriteDatabaseImpl SHALL menerapkan semua filter menggunakan Appwrite `Query` class dan menghasilkan list of query strings
4. THE AppwriteDatabase SHALL mendefinisikan operasi `listDocumentsWithPagination` yang menerima databaseId, collectionId, limit, offset, dan optional filter conditions, lalu mengembalikan `BaseResponse<List<T>>`
5. THE AppwriteDatabase SHALL mendefinisikan operasi `listDocumentsWithOrder` yang menerima databaseId, collectionId, orderAttributes, ascending/descending flags, dan optional filter conditions, lalu mengembalikan `BaseResponse<List<T>>`

### Requirement 8: Dependency Configuration

**User Story:** Sebagai developer, saya ingin dependency Appwrite Android SDK ditambahkan ke `build.gradle` library, sehingga Appwrite SDK tersedia secara transitive untuk project utama.

#### Acceptance Criteria

1. THE build.gradle SHALL mendeklarasikan dependency `io.appwrite:sdk-for-android` dengan scope `api` agar tersedia secara transitive
2. WHEN project utama meng-compile, THE build.gradle SHALL memastikan Appwrite SDK dependency tersedia secara transitive tanpa deklarasi ulang di project utama

### Requirement 9: Package Structure dan Konsistensi Codebase

**User Story:** Sebagai developer, saya ingin base class Appwrite ditempatkan di package yang konsisten dengan struktur codebase yang sudah ada (mengikuti pola Supabase), sehingga mudah ditemukan dan dipelihara.

#### Acceptance Criteria

1. THE base class Appwrite SHALL ditempatkan di package `com.kanzankazu.kanzanutil.appwrite` mengikuti pola `kanzanutil/supabase/`
2. THE AppwriteClientProvider dan AppwriteConfig SHALL ditempatkan di root package `com.kanzankazu.kanzanutil.appwrite`
3. THE AppwriteDatabase dan AppwriteDatabaseImpl SHALL ditempatkan di sub-package `com.kanzankazu.kanzanutil.appwrite.database`
4. THE AppwriteAuth dan AppwriteAuthImpl SHALL ditempatkan di sub-package `com.kanzankazu.kanzanutil.appwrite.auth`
5. THE AppwriteStorage dan AppwriteStorageImpl SHALL ditempatkan di sub-package `com.kanzankazu.kanzanutil.appwrite.storage`
6. THE AppwriteRealtime dan AppwriteRealtimeImpl SHALL ditempatkan di sub-package `com.kanzankazu.kanzanutil.appwrite.realtime`
7. THE AppwriteFunctions dan AppwriteFunctionsImpl SHALL ditempatkan di sub-package `com.kanzankazu.kanzanutil.appwrite.functions`
8. THE AppwriteFilterCondition dan AppwriteFilterOperator SHALL ditempatkan di root package `com.kanzankazu.kanzanutil.appwrite`
9. THE semua interface dan implementasi SHALL menggunakan `BaseResponse<T>` dari `com.kanzankazu.kanzannetwork.response.kanzanbaseresponse` untuk konsistensi dengan codebase yang sudah ada
