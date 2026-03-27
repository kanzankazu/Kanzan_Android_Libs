# Implementation Plan: Appwrite Base Classes

## Overview

Implementasi base class Appwrite di `kanzankazuLibs` mengikuti pola interface + impl yang sudah ada di codebase (mirror dari Supabase pattern). Semua file ditempatkan di `kanzankazuLibs/src/main/java/com/kanzankazu/kanzanutil/appwrite/`. Setiap task membangun di atas task sebelumnya secara incremental.

## Tasks

- [x] 1. Configure Appwrite dependency in build.gradle
  - Tambahkan `io.appwrite:sdk-for-android:8.1.0` dengan scope `api` di `kanzankazuLibs/build.gradle`
  - Tambahkan version di `gradle/libs.versions.toml` jika menggunakan version catalog
  - _Requirements: 8.1, 8.2_

- [x] 2. Create configuration and client provider classes
  - [x] 2.1 Create AppwriteConfig data class
    - Buat file `AppwriteConfig.kt` di root package `appwrite/`
    - Data class dengan `endpoint: String` dan `projectId: String`
    - Package: `com.kanzankazu.kanzanutil.appwrite`
    - _Requirements: 1.5_

  - [x] 2.2 Create AppwriteFilterOperator enum
    - Buat file `AppwriteFilterOperator.kt` di root package `appwrite/`
    - Enum values: EQUAL, NOT_EQUAL, GREATER_THAN, GREATER_THAN_OR_EQUAL, LESS_THAN, LESS_THAN_OR_EQUAL, SEARCH, IS_NULL, IS_NOT_NULL, BETWEEN, STARTS_WITH, ENDS_WITH, CONTAINS
    - Package: `com.kanzankazu.kanzanutil.appwrite`
    - _Requirements: 7.2_

  - [x] 2.3 Create AppwriteFilterCondition data class
    - Buat file `AppwriteFilterCondition.kt` di root package `appwrite/`
    - Data class dengan `attribute: String`, `value: Any?` (nullable karena IS_NULL/IS_NOT_NULL), `operator: AppwriteFilterOperator` (default EQUAL)
    - Package: `com.kanzankazu.kanzanutil.appwrite`
    - _Requirements: 7.1_

  - [x] 2.4 Create AppwriteClientProvider singleton object
    - Buat file `AppwriteClientProvider.kt` di root package `appwrite/`
    - Singleton `object` dengan `initialize(context: Context, config: AppwriteConfig)`, `getClient(): Client`, `getAccount(): Account`, `getDatabases(): Databases`, `getStorage(): Storage`, `getRealtime(): Realtime`, `getFunctions(): Functions`, `isInitialized(): Boolean`
    - `initialize()` validasi endpoint/projectId tidak blank, buat `Client(context).setEndpoint(endpoint).setProject(projectId)`, lalu buat semua service objects
    - `get*()` throw `IllegalStateException` jika belum di-initialize
    - `initialize()` throw `IllegalArgumentException` jika config invalid
    - Package: `com.kanzankazu.kanzanutil.appwrite`
    - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.6_

- [x] 3. Checkpoint - Pastikan konfigurasi dan client provider sudah benar
  - Ensure semua file compile tanpa error, ask the user if questions arise.

- [x] 4. Implement Database layer
  - [x] 4.1 Create AppwriteDatabase interface
    - Buat file `database/AppwriteDatabase.kt`
    - Definisikan suspend functions: `createDocument`, `listDocuments`, `getDocument`, `updateDocument`, `deleteDocument`, `listDocumentsWithPagination`, `listDocumentsWithOrder`
    - Definisikan Flow function: `listDocumentsAsFlow`
    - Gunakan `BaseResponse<T>` sebagai return type, `AppwriteFilterCondition` untuk filter parameters
    - Data menggunakan `Map<String, Any>` karena Appwrite SDK mengembalikan Document objects
    - Package: `com.kanzankazu.kanzanutil.appwrite.database`
    - _Requirements: 2.1, 2.2, 2.9, 2.10, 2.11, 7.4, 7.5_

  - [x] 4.2 Create AppwriteDatabaseImpl open class
    - Buat file `database/AppwriteDatabaseImpl.kt`
    - Implementasi semua method dari `AppwriteDatabase` interface
    - Constructor menerima `Databases` dengan default dari `AppwriteClientProvider.getDatabases()`
    - Gunakan `databases.createDocument`, `databases.listDocuments`, `databases.getDocument`, `databases.updateDocument`, `databases.deleteDocument`
    - Build Appwrite `Query` strings dari `AppwriteFilterCondition` list menggunakan mapping: EQUAL→Query.equal(), NOT_EQUAL→Query.notEqual(), GREATER_THAN→Query.greaterThan(), dll
    - Wrap semua hasil dengan try/catch → `BaseResponse.Success` atau `BaseResponse.Error`
    - `listDocumentsAsFlow` menggunakan `flow {}` builder dengan `emit(BaseResponse.Loading)` di awal
    - Pagination menggunakan `Query.limit()` + `Query.offset()`, ordering menggunakan `Query.orderAsc()`/`Query.orderDesc()`
    - Package: `com.kanzankazu.kanzanutil.appwrite.database`
    - _Requirements: 2.3, 2.4, 2.5, 2.6, 2.7, 2.8, 7.3_

- [x] 5. Implement Authentication layer
  - [x] 5.1 Create AppwriteAuth interface and data classes
    - Buat file `auth/AppwriteAuth.kt`
    - Definisikan data classes: `AppwriteUserInfo(id, email, name, registration)`, `AppwriteSessionInfo(sessionId, userId, provider, expire)`, enum `AppwriteAuthState(SIGNED_IN, SIGNED_OUT)`, enum `AppwriteOAuthProvider(GOOGLE, FACEBOOK)`
    - Definisikan suspend functions: `signUp`, `signInWithEmail`, `signInWithOAuth`, `signOut`, `getCurrentUser`, `getCurrentSession`, `resetPassword`
    - Definisikan Flow function: `observeAuthState`
    - Semua return `BaseResponse<T>`
    - Package: `com.kanzankazu.kanzanutil.appwrite.auth`
    - _Requirements: 3.1, 3.8, 3.10_

  - [x] 5.2 Create AppwriteAuthImpl open class
    - Buat file `auth/AppwriteAuthImpl.kt`
    - Implementasi semua method dari `AppwriteAuth` interface
    - `signUp` → `account.create(ID.unique(), email, password, name)`, return `AppwriteUserInfo` dari User object
    - `signInWithEmail` → `account.createEmailPasswordSession(email, password)`, return `AppwriteSessionInfo`
    - `signInWithOAuth` → `account.createOAuth2Session(provider)` mapping `AppwriteOAuthProvider` ke Appwrite OAuthProvider
    - `signOut` → `account.deleteSession("current")`
    - `getCurrentUser` → `account.get()`, return `BaseResponse.Empty` jika exception (no session), `BaseResponse.Success(AppwriteUserInfo)` jika ada
    - `getCurrentSession` → `account.getSession("current")`, map ke `AppwriteSessionInfo`
    - `resetPassword` → `account.createRecovery(email, redirectUrl)`
    - `observeAuthState` → periodic check atau try `account.get()` wrapped in Flow
    - Wrap semua dengan try/catch → `BaseResponse.Error`
    - Package: `com.kanzankazu.kanzanutil.appwrite.auth`
    - _Requirements: 3.2, 3.3, 3.4, 3.5, 3.6, 3.7, 3.9_

- [x] 6. Checkpoint - Pastikan Database dan Auth layer compile
  - Ensure semua file compile tanpa error, ask the user if questions arise.

- [x] 7. Implement Storage layer
  - [x] 7.1 Create AppwriteStorage interface and AppwriteFileMetadata
    - Buat file `storage/AppwriteStorage.kt`
    - Definisikan data class `AppwriteFileMetadata(id, name, sizeOriginal, mimeType, createdAt)`
    - Definisikan suspend functions: `upload`, `uploadBytes`, `download`, `delete`, `getFilePreview`, `getFileViewUrl`, `list`
    - Definisikan Flow function: `uploadAsFlow`
    - Semua return `BaseResponse<T>`
    - Package: `com.kanzankazu.kanzanutil.appwrite.storage`
    - _Requirements: 4.1, 4.9_

  - [x] 7.2 Create AppwriteStorageImpl open class
    - Buat file `storage/AppwriteStorageImpl.kt`
    - Implementasi semua method dari `AppwriteStorage` interface
    - `upload` → `storage.createFile(bucketId, fileId ?: ID.unique(), InputFile.fromPath(filePath))`, return file ID
    - `uploadBytes` → write bytes to temp file, then `storage.createFile(bucketId, fileId ?: ID.unique(), InputFile.fromPath(tempFile))`, cleanup temp file
    - `download` → `storage.getFileDownload(bucketId, fileId)`, return ByteArray
    - `delete` → `storage.deleteFile(bucketId, fileId)`
    - `getFilePreview` → `storage.getFilePreview(bucketId, fileId)`, return ByteArray
    - `getFileViewUrl` → construct URL string: `"{endpoint}/storage/buckets/{bucketId}/files/{fileId}/view?project={projectId}"`
    - `list` → `storage.listFiles(bucketId)`, map ke `AppwriteFileMetadata`
    - `uploadAsFlow` → `flow {}` builder dengan `emit(BaseResponse.Loading)` di awal
    - Wrap semua dengan try/catch → `BaseResponse.Error`
    - Package: `com.kanzankazu.kanzanutil.appwrite.storage`
    - _Requirements: 4.2, 4.3, 4.4, 4.5, 4.6, 4.7, 4.8_

- [x] 8. Implement Realtime layer
  - [x] 8.1 Create AppwriteRealtime interface
    - Buat file `realtime/AppwriteRealtime.kt`
    - Definisikan Flow functions: `subscribeToCollection`, `subscribeToDocument`, `subscribeToStorage`
    - Definisikan functions: `removeSubscription`, `removeAllSubscriptions`
    - Package: `com.kanzankazu.kanzanutil.appwrite.realtime`
    - _Requirements: 5.1, 5.3, 5.6, 5.7, 5.8_

  - [x] 8.2 Create AppwriteRealtimeImpl open class
    - Buat file `realtime/AppwriteRealtimeImpl.kt`
    - Implementasi semua method dari `AppwriteRealtime` interface
    - `subscribeToCollection` → `callbackFlow` dengan `realtime.subscribe("databases.{dbId}.collections.{collId}.documents") { callback }`, emit payload sebagai `BaseResponse.Success`
    - `subscribeToDocument` → `callbackFlow` dengan `realtime.subscribe("databases.{dbId}.collections.{collId}.documents.{docId}") { callback }`
    - `subscribeToStorage` → `callbackFlow` dengan `realtime.subscribe("buckets.{bucketId}.files") { callback }`
    - `awaitClose { subscription.close() }` untuk cleanup
    - Maintain map of active subscriptions untuk `removeSubscription` dan `removeAllSubscriptions`
    - Emit `BaseResponse.Error` saat connection error
    - Package: `com.kanzankazu.kanzanutil.appwrite.realtime`
    - _Requirements: 5.2, 5.4, 5.5_

- [x] 9. Implement Functions layer
  - [x] 9.1 Create AppwriteFunctions interface
    - Buat file `functions/AppwriteFunctions.kt`
    - Definisikan suspend functions: `execute`, `executeWithType<T>`
    - Definisikan Flow function: `executeAsFlow`
    - Package: `com.kanzankazu.kanzanutil.appwrite.functions`
    - _Requirements: 6.1, 6.3, 6.5_

  - [x] 9.2 Create AppwriteFunctionsImpl open class
    - Buat file `functions/AppwriteFunctionsImpl.kt`
    - Implementasi semua method dari `AppwriteFunctions` interface
    - `execute` → `functions.createExecution(functionId, body)`, return `execution.responseBody`
    - `executeWithType` → execute + deserialize response menggunakan Gson ke target class
    - `executeAsFlow` → `flow {}` builder dengan `emit(BaseResponse.Loading)` di awal
    - Wrap semua dengan try/catch → `BaseResponse.Error`
    - Package: `com.kanzankazu.kanzanutil.appwrite.functions`
    - _Requirements: 6.2, 6.4_

- [x] 10. Final checkpoint - Pastikan semua file compile dan terintegrasi
  - Ensure semua file compile tanpa error, pastikan package structure sesuai requirement 9.1-9.9
  - Verify semua interface dan implementasi menggunakan `BaseResponse<T>` dari `com.kanzankazu.kanzannetwork.response.kanzanbaseresponse`
  - Ask the user if questions arise.

## Notes

- Tidak ada task testing (mengikuti pola Supabase spec)
- Semua class menggunakan `BaseResponse<T>` yang sudah ada di codebase
- Semua `*Impl` class adalah `open class` untuk extensibility
- Appwrite membutuhkan `Context` untuk inisialisasi, berbeda dengan Supabase
- Appwrite menggunakan `Map<String, Any>` untuk document data, bukan serialized objects
- Appwrite menggunakan `bucketId` + `fileId` untuk storage, bukan bucket + path
- Appwrite menggunakan callback-based realtime, di-wrap ke `callbackFlow` untuk konsistensi dengan Flow pattern
- Checkpoints memastikan validasi incremental setiap beberapa task
