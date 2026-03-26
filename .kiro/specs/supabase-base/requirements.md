# Requirements Document

## Introduction

Dokumen ini mendefinisikan requirements untuk pembuatan general/base class Supabase di shared library `kanzankazuLibs`. Base class ini menyediakan abstraksi untuk semua fitur Supabase (Database/Postgrest, Auth, Storage, Realtime, Edge Functions) yang bisa langsung dipakai oleh project utama. Desain mengikuti pola yang sudah ada di codebase (`RealtimeDatabase` interface + `RealtimeDatabaseImpl`, `BaseResponse<T>`, suspend + Flow support) dan menggunakan Supabase Kotlin SDK (`io.github.jan-tennert.supabase`).

## Glossary

- **SupabaseConfig**: Object konfigurasi yang menyimpan Supabase URL dan Anon Key untuk inisialisasi client
- **SupabaseClientProvider**: Singleton yang mengelola lifecycle dan akses ke instance `SupabaseClient` dari Supabase Kotlin SDK
- **SupabaseDatabase**: Interface yang mendefinisikan operasi CRUD (Create, Read, Update, Delete) terhadap Supabase Postgrest/Database
- **SupabaseDatabaseImpl**: Implementasi konkret dari `SupabaseDatabase` yang menggunakan `postgrest-kt` module
- **SupabaseAuth**: Interface yang mendefinisikan operasi autentikasi pengguna (sign up, sign in, sign out, session management)
- **SupabaseAuthImpl**: Implementasi konkret dari `SupabaseAuth` yang menggunakan `auth-kt` module
- **SupabaseStorage**: Interface yang mendefinisikan operasi file storage (upload, download, delete, get URL)
- **SupabaseStorageImpl**: Implementasi konkret dari `SupabaseStorage` yang menggunakan `storage-kt` module
- **SupabaseRealtime**: Interface yang mendefinisikan operasi real-time subscription untuk mendengarkan perubahan data
- **SupabaseRealtimeImpl**: Implementasi konkret dari `SupabaseRealtime` yang menggunakan `realtime-kt` module
- **SupabaseFunctions**: Interface yang mendefinisikan operasi pemanggilan Edge Functions
- **SupabaseFunctionsImpl**: Implementasi konkret dari `SupabaseFunctions` yang menggunakan `functions-kt` module
- **BaseResponse**: Sealed interface (`Loading`, `Empty`, `Error`, `Success<T>`) yang sudah ada di codebase untuk membungkus hasil operasi
- **SupabaseFilterCondition**: Data class untuk mendefinisikan kondisi filter pada query Postgrest (column, value, operator)
- **SupabaseFilterOperator**: Enum yang mendefinisikan operator filter Postgrest (EQ, NEQ, GT, GTE, LT, LTE, LIKE, ILIKE, IN, IS)

## Requirements

### Requirement 1: Konfigurasi dan Inisialisasi Supabase Client

**User Story:** Sebagai developer, saya ingin menginisialisasi Supabase client dengan konfigurasi URL dan Anon Key, sehingga semua fitur Supabase bisa diakses dari satu titik konfigurasi.

#### Acceptance Criteria

1. WHEN `SupabaseClientProvider.initialize` dipanggil dengan `SupabaseConfig` yang valid (berisi URL dan Anon Key), THE SupabaseClientProvider SHALL membuat instance `SupabaseClient` dan meng-install module Postgrest, Auth, Storage, Realtime, dan Functions
2. WHEN `SupabaseClientProvider.getClient` dipanggil setelah inisialisasi berhasil, THE SupabaseClientProvider SHALL mengembalikan instance `SupabaseClient` yang sama (singleton)
3. IF `SupabaseClientProvider.getClient` dipanggil sebelum `initialize`, THEN THE SupabaseClientProvider SHALL melempar `IllegalStateException` dengan pesan deskriptif
4. IF `SupabaseConfig` memiliki URL atau Anon Key yang kosong, THEN THE SupabaseClientProvider SHALL melempar `IllegalArgumentException` dengan pesan deskriptif
5. THE SupabaseConfig SHALL menyimpan `supabaseUrl` (String) dan `supabaseAnonKey` (String) sebagai properti wajib

### Requirement 2: Base Class Database/Postgrest

**User Story:** Sebagai developer, saya ingin base class untuk operasi CRUD ke Supabase Database (Postgrest), sehingga saya bisa melakukan query data tanpa menulis boilerplate code.

#### Acceptance Criteria

1. THE SupabaseDatabase SHALL mendefinisikan operasi `insert`, `upsert`, `select`, `update`, dan `delete` dalam bentuk suspend function yang mengembalikan `BaseResponse<T>`
2. THE SupabaseDatabase SHALL mendefinisikan operasi `selectAsFlow` yang mengembalikan `Flow<BaseResponse<T>>` untuk reactive data streaming
3. WHEN operasi `insert` dipanggil dengan table name dan data object, THE SupabaseDatabaseImpl SHALL menyimpan data ke tabel Postgrest yang sesuai dan mengembalikan `BaseResponse.Success` dengan data yang tersimpan
4. WHEN operasi `select` dipanggil dengan table name dan target class, THE SupabaseDatabaseImpl SHALL mengambil semua data dari tabel dan mengembalikan `BaseResponse.Success` berisi list of object
5. WHEN operasi `select` dipanggil dengan table name, target class, dan list of `SupabaseFilterCondition`, THE SupabaseDatabaseImpl SHALL menerapkan filter pada query dan mengembalikan data yang sesuai
6. WHEN operasi `update` dipanggil dengan table name, filter condition, dan data object, THE SupabaseDatabaseImpl SHALL mengupdate data yang cocok dengan filter dan mengembalikan `BaseResponse.Success`
7. WHEN operasi `delete` dipanggil dengan table name dan filter condition, THE SupabaseDatabaseImpl SHALL menghapus data yang cocok dengan filter dan mengembalikan `BaseResponse.Success`
8. IF operasi database gagal karena exception, THEN THE SupabaseDatabaseImpl SHALL mengembalikan `BaseResponse.Error` dengan pesan error yang deskriptif
9. THE SupabaseDatabase SHALL mendefinisikan operasi `selectById` yang menerima table name, column name, id value, dan target class, lalu mengembalikan `BaseResponse<T>` untuk single object
10. WHEN operasi `upsert` dipanggil dengan table name dan data object, THE SupabaseDatabaseImpl SHALL menyimpan data baru atau mengupdate data yang sudah ada dan mengembalikan `BaseResponse.Success`

### Requirement 3: Base Class Authentication

**User Story:** Sebagai developer, saya ingin base class untuk operasi autentikasi Supabase, sehingga saya bisa mengimplementasikan sign up, sign in, dan session management dengan mudah.

#### Acceptance Criteria

1. THE SupabaseAuth SHALL mendefinisikan operasi `signUpWithEmail`, `signInWithEmail`, `signOut`, `getCurrentUser`, `getCurrentSession`, dan `resetPasswordForEmail` dalam bentuk suspend function yang mengembalikan `BaseResponse<T>`
2. WHEN `signUpWithEmail` dipanggil dengan email dan password yang valid, THE SupabaseAuthImpl SHALL mendaftarkan user baru dan mengembalikan `BaseResponse.Success` berisi user info
3. WHEN `signInWithEmail` dipanggil dengan email dan password yang valid, THE SupabaseAuthImpl SHALL mengautentikasi user dan mengembalikan `BaseResponse.Success` berisi session info
4. WHEN `signOut` dipanggil, THE SupabaseAuthImpl SHALL menghapus session aktif dan mengembalikan `BaseResponse.Success`
5. WHEN `getCurrentUser` dipanggil saat ada session aktif, THE SupabaseAuthImpl SHALL mengembalikan `BaseResponse.Success` berisi data user yang sedang login
6. IF `getCurrentUser` dipanggil saat tidak ada session aktif, THEN THE SupabaseAuthImpl SHALL mengembalikan `BaseResponse.Empty`
7. IF operasi autentikasi gagal karena credential salah atau exception, THEN THE SupabaseAuthImpl SHALL mengembalikan `BaseResponse.Error` dengan pesan error yang deskriptif
8. THE SupabaseAuth SHALL mendefinisikan `observeAuthState` yang mengembalikan `Flow<BaseResponse<AuthState>>` untuk memantau perubahan status autentikasi secara reactive
9. WHEN `resetPasswordForEmail` dipanggil dengan email yang valid, THE SupabaseAuthImpl SHALL mengirim email reset password dan mengembalikan `BaseResponse.Success`
10. THE SupabaseAuth SHALL mendefinisikan operasi `signInWithOAuth` yang menerima provider type (Google, Facebook) dan mengembalikan `BaseResponse.Success` berisi session info

### Requirement 4: Base Class Storage

**User Story:** Sebagai developer, saya ingin base class untuk operasi file storage Supabase, sehingga saya bisa upload, download, dan mengelola file dengan mudah.

#### Acceptance Criteria

1. THE SupabaseStorage SHALL mendefinisikan operasi `upload`, `download`, `delete`, `getPublicUrl`, dan `list` dalam bentuk suspend function yang mengembalikan `BaseResponse<T>`
2. WHEN operasi `upload` dipanggil dengan bucket name, file path, dan byte array, THE SupabaseStorageImpl SHALL mengupload file ke bucket yang sesuai dan mengembalikan `BaseResponse.Success` berisi file path
3. WHEN operasi `download` dipanggil dengan bucket name dan file path, THE SupabaseStorageImpl SHALL mendownload file dan mengembalikan `BaseResponse.Success` berisi byte array
4. WHEN operasi `delete` dipanggil dengan bucket name dan list of file paths, THE SupabaseStorageImpl SHALL menghapus file yang sesuai dan mengembalikan `BaseResponse.Success`
5. WHEN operasi `getPublicUrl` dipanggil dengan bucket name dan file path, THE SupabaseStorageImpl SHALL mengembalikan `BaseResponse.Success` berisi public URL string
6. WHEN operasi `list` dipanggil dengan bucket name dan optional prefix path, THE SupabaseStorageImpl SHALL mengembalikan `BaseResponse.Success` berisi list of file metadata
7. IF operasi storage gagal karena exception (file tidak ditemukan, bucket tidak ada, permission denied), THEN THE SupabaseStorageImpl SHALL mengembalikan `BaseResponse.Error` dengan pesan error yang deskriptif
8. THE SupabaseStorage SHALL mendefinisikan operasi `uploadAsFlow` yang mengembalikan `Flow<BaseResponse<String>>` dengan emit `BaseResponse.Loading` di awal proses upload

### Requirement 5: Base Class Realtime

**User Story:** Sebagai developer, saya ingin base class untuk real-time subscription Supabase, sehingga saya bisa mendengarkan perubahan data secara live menggunakan Flow.

#### Acceptance Criteria

1. THE SupabaseRealtime SHALL mendefinisikan operasi `subscribeToTable` yang mengembalikan `Flow<BaseResponse<T>>` untuk mendengarkan perubahan data (INSERT, UPDATE, DELETE) pada tabel tertentu
2. WHEN `subscribeToTable` dipanggil dengan table name dan target class, THE SupabaseRealtimeImpl SHALL membuat channel subscription dan meng-emit setiap perubahan data sebagai `BaseResponse.Success`
3. THE SupabaseRealtime SHALL mendefinisikan operasi `subscribeToChannel` yang mengembalikan `Flow<BaseResponse<String>>` untuk mendengarkan broadcast message pada channel tertentu
4. WHEN subscription dibatalkan (Flow collection dihentikan), THE SupabaseRealtimeImpl SHALL menutup channel dan membersihkan resource
5. IF subscription gagal karena koneksi terputus atau error, THEN THE SupabaseRealtimeImpl SHALL meng-emit `BaseResponse.Error` dengan pesan error yang deskriptif
6. THE SupabaseRealtime SHALL mendefinisikan operasi `removeSubscription` untuk menghentikan subscription pada channel tertentu secara manual
7. THE SupabaseRealtime SHALL mendefinisikan operasi `removeAllSubscriptions` untuk menghentikan semua subscription aktif

### Requirement 6: Base Class Edge Functions

**User Story:** Sebagai developer, saya ingin base class untuk memanggil Supabase Edge Functions, sehingga saya bisa menjalankan server-side logic tanpa menulis boilerplate code.

#### Acceptance Criteria

1. THE SupabaseFunctions SHALL mendefinisikan operasi `invoke` dalam bentuk suspend function yang menerima function name, optional request body, dan mengembalikan `BaseResponse<String>` berisi response body
2. WHEN operasi `invoke` dipanggil dengan function name yang valid, THE SupabaseFunctionsImpl SHALL memanggil Edge Function dan mengembalikan `BaseResponse.Success` berisi response string
3. THE SupabaseFunctions SHALL mendefinisikan operasi `invokeWithType` yang menerima function name, optional request body, dan target class, lalu mengembalikan `BaseResponse<T>` berisi response yang sudah di-deserialize
4. IF pemanggilan Edge Function gagal karena exception (function tidak ditemukan, timeout, server error), THEN THE SupabaseFunctionsImpl SHALL mengembalikan `BaseResponse.Error` dengan pesan error yang deskriptif
5. THE SupabaseFunctions SHALL mendefinisikan operasi `invokeAsFlow` yang mengembalikan `Flow<BaseResponse<String>>` dengan emit `BaseResponse.Loading` di awal pemanggilan

### Requirement 7: Filter dan Query Builder untuk Postgrest

**User Story:** Sebagai developer, saya ingin filter dan query builder yang konsisten untuk operasi Postgrest, sehingga saya bisa membangun query yang kompleks dengan mudah.

#### Acceptance Criteria

1. THE SupabaseFilterCondition SHALL menyimpan `column` (String), `value` (Any), dan `operator` (SupabaseFilterOperator) sebagai properti
2. THE SupabaseFilterOperator SHALL mendefinisikan operator: EQ, NEQ, GT, GTE, LT, LTE, LIKE, ILIKE, IN, IS
3. WHEN list of `SupabaseFilterCondition` diberikan ke operasi `select`, THE SupabaseDatabaseImpl SHALL menerapkan semua filter secara berurutan pada query Postgrest
4. THE SupabaseDatabase SHALL mendefinisikan operasi `selectWithPagination` yang menerima table name, target class, page number, page size, dan optional filter conditions, lalu mengembalikan `BaseResponse<List<T>>`
5. THE SupabaseDatabase SHALL mendefinisikan operasi `selectWithOrder` yang menerima table name, target class, column name, ascending/descending flag, dan optional filter conditions, lalu mengembalikan `BaseResponse<List<T>>`

### Requirement 8: Dependency Configuration

**User Story:** Sebagai developer, saya ingin dependency Supabase Kotlin SDK ditambahkan ke `build.gradle` library, sehingga semua module Supabase tersedia secara transitive untuk project utama.

#### Acceptance Criteria

1. THE build.gradle SHALL mendeklarasikan dependency BOM `io.github.jan-tennert.supabase:bom` dengan scope `api` untuk version management
2. THE build.gradle SHALL mendeklarasikan dependency `postgrest-kt`, `auth-kt`, `storage-kt`, `realtime-kt`, dan `functions-kt` dengan scope `api`
3. THE build.gradle SHALL mendeklarasikan dependency `ktor-client-android` sebagai HTTP engine untuk Supabase Kotlin SDK dengan scope `api`
4. WHEN project utama meng-compile, THE build.gradle SHALL memastikan semua Supabase dependency tersedia secara transitive tanpa deklarasi ulang di project utama

### Requirement 9: Package Structure dan Konsistensi Codebase

**User Story:** Sebagai developer, saya ingin base class Supabase ditempatkan di package yang konsisten dengan struktur codebase yang sudah ada, sehingga mudah ditemukan dan dipelihara.

#### Acceptance Criteria

1. THE base class Supabase SHALL ditempatkan di package `com.kanzankazu.kanzandatabase.supabase` mengikuti pola `kanzandatabase/firebase/`, `kanzandatabase/retrofit/`, `kanzandatabase/room/`
2. THE SupabaseClientProvider dan SupabaseConfig SHALL ditempatkan di root package `com.kanzankazu.kanzandatabase.supabase`
3. THE SupabaseDatabase dan SupabaseDatabaseImpl SHALL ditempatkan di sub-package `com.kanzankazu.kanzandatabase.supabase.database`
4. THE SupabaseAuth dan SupabaseAuthImpl SHALL ditempatkan di sub-package `com.kanzankazu.kanzandatabase.supabase.auth`
5. THE SupabaseStorage dan SupabaseStorageImpl SHALL ditempatkan di sub-package `com.kanzankazu.kanzandatabase.supabase.storage`
6. THE SupabaseRealtime dan SupabaseRealtimeImpl SHALL ditempatkan di sub-package `com.kanzankazu.kanzandatabase.supabase.realtime`
7. THE SupabaseFunctions dan SupabaseFunctionsImpl SHALL ditempatkan di sub-package `com.kanzankazu.kanzandatabase.supabase.functions`
8. THE SupabaseFilterCondition dan SupabaseFilterOperator SHALL ditempatkan di root package `com.kanzankazu.kanzandatabase.supabase`
9. THE semua interface dan implementasi SHALL menggunakan `BaseResponse<T>` dari `com.kanzankazu.kanzannetwork.response.kanzanbaseresponse` untuk konsistensi dengan codebase yang sudah ada
