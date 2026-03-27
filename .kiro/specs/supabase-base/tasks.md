# Implementation Plan: Supabase Base Classes

## Overview

Implementasi base class Supabase di `kanzankazuLibs` mengikuti pola interface + impl yang sudah ada di codebase. Semua file ditempatkan di `kanzankazuLibs/src/main/java/com/kanzankazu/kanzandatabase/supabase/`. Setiap task membangun di atas task sebelumnya secara incremental.

## Tasks

- [x] 1. Configure Supabase dependencies in build.gradle
  - Tambahkan Supabase BOM (`io.github.jan-tennert.supabase:bom:2.6.1`) dengan scope `api platform()`
  - Tambahkan module dependencies: `postgrest-kt`, `auth-kt`, `storage-kt`, `realtime-kt`, `functions-kt` dengan scope `api`
  - Tambahkan `io.ktor:ktor-client-android:2.3.12` dengan scope `api`
  - _Requirements: 8.1, 8.2, 8.3, 8.4_

- [x] 2. Create configuration and client provider classes
  - [x] 2.1 Create SupabaseConfig data class
    - Buat file `SupabaseConfig.kt` di root package `supabase/`
    - Data class dengan `supabaseUrl: String` dan `supabaseAnonKey: String`
    - _Requirements: 1.5_

  - [x] 2.2 Create SupabaseFilterOperator enum
    - Buat file `SupabaseFilterOperator.kt` di root package `supabase/`
    - Enum values: EQ, NEQ, GT, GTE, LT, LTE, LIKE, ILIKE, IN, IS
    - _Requirements: 7.2_

  - [x] 2.3 Create SupabaseFilterCondition data class
    - Buat file `SupabaseFilterCondition.kt` di root package `supabase/`
    - Data class dengan `column: String`, `value: Any`, `operator: SupabaseFilterOperator` (default EQ)
    - _Requirements: 7.1_

  - [x] 2.4 Create SupabaseClientProvider singleton object
    - Buat file `SupabaseClientProvider.kt` di root package `supabase/`
    - Singleton `object` dengan `initialize(config: SupabaseConfig)`, `getClient(): SupabaseClient`, `isInitialized(): Boolean`
    - `initialize()` validasi URL/key tidak blank, buat `SupabaseClient` dengan install Postgrest, Auth, Storage, Realtime, Functions + KtorClient(Android)
    - `getClient()` throw `IllegalStateException` jika belum di-initialize
    - `initialize()` throw `IllegalArgumentException` jika config invalid
    - _Requirements: 1.1, 1.2, 1.3, 1.4_

- [x] 3. Checkpoint - Pastikan konfigurasi dan client provider sudah benar
  - Ensure semua file compile tanpa error, ask the user if questions arise.

- [x] 4. Implement Database/Postgrest layer
  - [x] 4.1 Create SupabaseDatabase interface
    - Buat file `database/SupabaseDatabase.kt`
    - Definisikan suspend functions: `insert`, `upsert`, `select`, `selectById`, `update`, `delete`, `selectWithPagination`, `selectWithOrder`
    - Definisikan Flow function: `selectAsFlow`
    - Gunakan `BaseResponse<T>` sebagai return type, `SupabaseFilterCondition` untuk filter parameters
    - Gunakan `inline reified` untuk type-safe deserialization pada `select`, `selectById`, `selectWithPagination`, `selectWithOrder`, `selectAsFlow`
    - _Requirements: 2.1, 2.2, 2.9, 7.4, 7.5_

  - [x] 4.2 Create SupabaseDatabaseImpl open class
    - Buat file `database/SupabaseDatabaseImpl.kt`
    - Implementasi semua method dari `SupabaseDatabase` interface
    - Constructor menerima `SupabaseClient` dengan default dari `SupabaseClientProvider.getClient()`
    - Gunakan `client.postgrest.from(table)` untuk semua operasi CRUD
    - Apply `SupabaseFilterCondition` list ke query dengan mapping operator ke Postgrest filter methods (eq, neq, gt, gte, lt, lte, like, ilike, isIn, exact)
    - Wrap semua hasil dengan try/catch → `BaseResponse.Success` atau `BaseResponse.Error`
    - `selectAsFlow` menggunakan `flow {}` builder dengan `emit(BaseResponse.Loading)` di awal
    - Pagination menggunakan `range()`, ordering menggunakan `order()`
    - _Requirements: 2.3, 2.4, 2.5, 2.6, 2.7, 2.8, 2.10, 7.3_

- [x] 5. Implement Authentication layer
  - [x] 5.1 Create SupabaseAuth interface and data classes
    - Buat file `auth/SupabaseAuth.kt`
    - Definisikan data classes: `UserInfo(id, email, metadata)`, `SessionInfo(accessToken, refreshToken, expiresAt)`, enum `AuthState(SIGNED_IN, SIGNED_OUT)`, enum `OAuthProvider(GOOGLE, FACEBOOK)`
    - Definisikan suspend functions: `signUpWithEmail`, `signInWithEmail`, `signInWithOAuth`, `signOut`, `getCurrentUser`, `getCurrentSession`, `resetPasswordForEmail`
    - Definisikan Flow function: `observeAuthState`
    - Semua return `BaseResponse<T>`
    - _Requirements: 3.1, 3.8, 3.10_

  - [x] 5.2 Create SupabaseAuthImpl open class
    - Buat file `auth/SupabaseAuthImpl.kt`
    - Implementasi semua method dari `SupabaseAuth` interface
    - `signUpWithEmail` → `client.auth.signUpWith(Email) { email, password }`, return `UserInfo` dari `currentUserOrNull()`
    - `signInWithEmail` → `client.auth.signInWith(Email) { email, password }`, return `UserInfo`
    - `signInWithOAuth` → `client.auth.signInWith(provider)` mapping `OAuthProvider` ke SDK provider
    - `signOut` → `client.auth.signOut()`
    - `getCurrentUser` → return `BaseResponse.Empty` jika null, `BaseResponse.Success(UserInfo)` jika ada
    - `getCurrentSession` → map session ke `SessionInfo`
    - `resetPasswordForEmail` → `client.auth.resetPasswordForEmail(email)`
    - `observeAuthState` → map `client.auth.sessionStatus` Flow ke `AuthState`
    - Wrap semua dengan try/catch → `BaseResponse.Error`
    - _Requirements: 3.2, 3.3, 3.4, 3.5, 3.6, 3.7, 3.9_

- [x] 6. Checkpoint - Pastikan Database dan Auth layer compile
  - Ensure semua file compile tanpa error, ask the user if questions arise.

- [x] 7. Implement Storage layer
  - [x] 7.1 Create SupabaseStorage interface and FileMetadata
    - Buat file `storage/SupabaseStorage.kt`
    - Definisikan data class `FileMetadata(name, size, createdAt, updatedAt)`
    - Definisikan suspend functions: `upload`, `download`, `delete`, `getPublicUrl`, `list`
    - Definisikan Flow function: `uploadAsFlow`
    - Semua return `BaseResponse<T>`
    - _Requirements: 4.1, 4.8_

  - [x] 7.2 Create SupabaseStorageImpl open class
    - Buat file `storage/SupabaseStorageImpl.kt`
    - Implementasi semua method dari `SupabaseStorage` interface
    - `upload` → `client.storage.from(bucket).upload(path, data)`, return path string
    - `download` → `client.storage.from(bucket).downloadAuthenticated(path)`, return ByteArray
    - `delete` → `client.storage.from(bucket).delete(paths)`
    - `getPublicUrl` → `client.storage.from(bucket).publicUrl(path)`
    - `list` → `client.storage.from(bucket).list(prefix)`, map ke `FileMetadata`
    - `uploadAsFlow` → `flow {}` builder dengan `emit(BaseResponse.Loading)` di awal
    - Wrap semua dengan try/catch → `BaseResponse.Error`
    - _Requirements: 4.2, 4.3, 4.4, 4.5, 4.6, 4.7_

- [x] 8. Implement Realtime layer
  - [x] 8.1 Create SupabaseRealtime interface
    - Buat file `realtime/SupabaseRealtime.kt`
    - Definisikan Flow functions: `subscribeToTable<T>`, `subscribeToChannel`
    - Definisikan suspend functions: `removeSubscription`, `removeAllSubscriptions`
    - _Requirements: 5.1, 5.3, 5.6, 5.7_

  - [x] 8.2 Create SupabaseRealtimeImpl open class
    - Buat file `realtime/SupabaseRealtimeImpl.kt`
    - Implementasi semua method dari `SupabaseRealtime` interface
    - `subscribeToTable` → `callbackFlow` dengan `client.realtime.createChannel(table)`, `channel.postgresChangeFlow(table)`, deserialize ke target class
    - `subscribeToChannel` → `callbackFlow` dengan broadcast message listener
    - `awaitClose` untuk cleanup channel subscription
    - `removeSubscription` → unsubscribe channel by name
    - `removeAllSubscriptions` → unsubscribe semua active channels
    - Emit `BaseResponse.Error` saat connection error
    - _Requirements: 5.2, 5.4, 5.5_

- [x] 9. Implement Edge Functions layer
  - [x] 9.1 Create SupabaseFunctions interface
    - Buat file `functions/SupabaseFunctions.kt`
    - Definisikan suspend functions: `invoke`, `invokeWithType<T>`
    - Definisikan Flow function: `invokeAsFlow`
    - _Requirements: 6.1, 6.3, 6.5_

  - [x] 9.2 Create SupabaseFunctionsImpl open class
    - Buat file `functions/SupabaseFunctionsImpl.kt`
    - Implementasi semua method dari `SupabaseFunctions` interface
    - `invoke` → `client.functions.invoke(functionName) { body }`, return response body string
    - `invokeWithType` → invoke + deserialize response ke target class
    - `invokeAsFlow` → `flow {}` builder dengan `emit(BaseResponse.Loading)` di awal
    - Wrap semua dengan try/catch → `BaseResponse.Error`
    - _Requirements: 6.2, 6.4_

- [x] 10. Final checkpoint - Pastikan semua file compile dan terintegrasi
  - Ensure semua file compile tanpa error, pastikan package structure sesuai requirement 9.1-9.9
  - Verify semua interface dan implementasi menggunakan `BaseResponse<T>` dari `com.kanzankazu.kanzannetwork.response.kanzanbaseresponse`
  - Ask the user if questions arise.

## Notes

- Tidak ada task testing sesuai permintaan user ("jangan pakai unittest dulu")
- Semua class menggunakan `BaseResponse<T>` yang sudah ada di codebase
- Semua `*Impl` class adalah `open class` untuk extensibility
- Checkpoints memastikan validasi incremental setiap beberapa task
