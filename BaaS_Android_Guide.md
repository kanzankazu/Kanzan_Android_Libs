# Panduan Menggunakan Supabase & Appwrite untuk Android (Kotlin)

Dokumentasi cara integrasi **Supabase** dan **Appwrite** sebagai alternatif Firebase Storage
untuk project Android dengan Kotlin + Jetpack Compose.

> **Catatan:** Project ini menggunakan Kotlin 1.7.10, minSdk 23, compileSdk 34, Groovy Gradle.
> Pastikan sesuaikan versi SDK dengan kompatibilitas project.

---

## Daftar Isi

- [Perbandingan Supabase vs Appwrite](#perbandingan-supabase-vs-appwrite)
- [Bagian 1: Supabase](#bagian-1-supabase)
  - [1.1 Setup Project Supabase](#11-setup-project-supabase)
  - [1.2 Tambah Dependency](#12-tambah-dependency)
  - [1.3 Inisialisasi Client](#13-inisialisasi-client)
  - [1.4 Authentication](#14-authentication)
  - [1.5 Database (Postgrest)](#15-database-postgrest)
  - [1.6 Storage (Upload/Download File)](#16-storage-uploaddownload-file)
  - [1.7 Realtime](#17-realtime)
- [Bagian 2: Appwrite](#bagian-2-appwrite)
  - [2.1 Setup Project Appwrite](#21-setup-project-appwrite)
  - [2.2 Tambah Dependency](#22-tambah-dependency)
  - [2.3 Inisialisasi Client](#23-inisialisasi-client)
  - [2.4 Authentication](#24-authentication)
  - [2.5 Database](#25-database)
  - [2.6 Storage (Upload/Download File)](#26-storage-uploaddownload-file)
  - [2.7 Realtime](#27-realtime)
- [Tips Integrasi dengan Project Ini](#tips-integrasi-dengan-project-ini)
- [Referensi](#referensi)

---

## Perbandingan Supabase vs Appwrite

| Fitur | Supabase | Appwrite |
|---|---|---|
| **Free Storage** | 1 GB | 2 GB |
| **Free MAU** | 50,000 | 75,000 |
| **Database** | PostgreSQL | MariaDB (internal) |
| **Android SDK** | Kotlin Multiplatform (`supabase-kt`) | Native Android SDK |
| **Min SDK** | 26 (perlu desugaring untuk < 26) | 21 |
| **Auth** | Email, OAuth, Magic Link, Phone | Email, OAuth, Phone, Magic URL |
| **Realtime** | ✅ | ✅ |
| **Self-host** | ✅ (Docker) | ✅ (Docker) |
| **Hosting** | supabase.com (cloud) | cloud.appwrite.io |

---

## Bagian 1: Supabase

### 1.1 Setup Project Supabase

1. Buka [supabase.com](https://supabase.com) dan buat akun
2. Klik **New Project**, pilih organization, isi nama project dan database password
3. Catat **Project URL** dan **anon/public key** dari Settings > API

### 1.2 Tambah Dependency

**`gradle/libs.versions.toml`** — tambahkan versions dan libraries:

```toml
[versions]
# ... existing versions ...
supabaseVersion = "3.1.4"
ktorVersion = "3.1.1"

[libraries]
# ... existing libraries ...
# Supabase BOM
supabase-bom = { module = "io.github.jan-tennert.supabase:bom", version.ref = "supabaseVersion" }
supabase-postgrest = { module = "io.github.jan-tennert.supabase:postgrest-kt" }
supabase-auth = { module = "io.github.jan-tennert.supabase:auth-kt" }
supabase-storage = { module = "io.github.jan-tennert.supabase:storage-kt" }
supabase-realtime = { module = "io.github.jan-tennert.supabase:realtime-kt" }
ktor-client-android = { module = "io.ktor:ktor-client-android", version.ref = "ktorVersion" }
```

**`app/build.gradle`** — tambahkan plugin serialization dan dependencies:

```groovy
plugins {
    // ... existing plugins ...
    id 'org.jetbrains.kotlin.plugin.serialization' version '1.7.10'
}

dependencies {
    // ... existing dependencies ...

    // Supabase
    implementation platform(libs.supabase.bom)
    implementation libs.supabase.postgrest
    implementation libs.supabase.auth
    implementation libs.supabase.storage
    implementation libs.supabase.realtime
    implementation libs.ktor.client.android
}
```

**`AndroidManifest.xml`** — pastikan ada permission internet:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

> **⚠️ Catatan minSdk:** supabase-kt membutuhkan minSdk 26.
> Project ini menggunakan minSdk 23, jadi perlu enable **core library desugaring**:
>
> ```groovy
> // app/build.gradle
> android {
>     compileOptions {
>         coreLibraryDesugaringEnabled true
>     }
> }
>
> dependencies {
>     coreLibraryDesugaring 'com.android.tools:desugar_jdk_libs:2.0.4'
> }
> ```

### 1.3 Inisialisasi Client

Buat file `SupabaseModule.kt` (jika pakai Hilt) atau singleton object:

```kotlin
// com/kanzankazu/app/data/remote/SupabaseClient.kt

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.realtime.Realtime

object SupabaseClient {

    val client = createSupabaseClient(
        supabaseUrl = "https://YOUR_PROJECT_ID.supabase.co",
        supabaseKey = "YOUR_ANON_KEY"
    ) {
        install(Auth)
        install(Postgrest)
        install(Storage)
        install(Realtime)
    }
}
```

Dengan Hilt:

```kotlin
// com/kanzankazu/app/di/SupabaseModule.kt

@Module
@InstallIn(SingletonComponent::class)
object SupabaseModule {

    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient {
        return createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_ANON_KEY
        ) {
            install(Auth)
            install(Postgrest)
            install(Storage)
            install(Realtime)
        }
    }
}
```

### 1.4 Authentication

```kotlin
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email

// Sign Up
suspend fun signUp(email: String, password: String) {
    SupabaseClient.client.auth.signUpWith(Email) {
        this.email = email
        this.password = password
    }
}

// Sign In
suspend fun signIn(email: String, password: String) {
    SupabaseClient.client.auth.signInWith(Email) {
        this.email = email
        this.password = password
    }
}

// Sign Out
suspend fun signOut() {
    SupabaseClient.client.auth.signOut()
}

// Get current user
fun currentUser() = SupabaseClient.client.auth.currentUserOrNull()
```

### 1.5 Database (Postgrest)

```kotlin
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.Serializable

@Serializable
data class Product(
    val id: Int? = null,
    val name: String,
    val price: Double
)

// Insert
suspend fun insertProduct(product: Product) {
    SupabaseClient.client.from("products").insert(product)
}

// Select all
suspend fun getProducts(): List<Product> {
    return SupabaseClient.client.from("products")
        .select()
        .decodeList<Product>()
}

// Select with filter
suspend fun getProductById(id: Int): Product {
    return SupabaseClient.client.from("products")
        .select {
            filter { eq("id", id) }
        }
        .decodeSingle<Product>()
}

// Update
suspend fun updateProduct(id: Int, name: String) {
    SupabaseClient.client.from("products")
        .update({ set("name", name) }) {
            filter { eq("id", id) }
        }
}

// Delete
suspend fun deleteProduct(id: Int) {
    SupabaseClient.client.from("products")
        .delete {
            filter { eq("id", id) }
        }
}
```

### 1.6 Storage (Upload/Download File)

Setup di Supabase Dashboard:
1. Buka **Storage** di dashboard
2. Klik **New Bucket**, beri nama (misal `avatars`)
3. Set public/private sesuai kebutuhan
4. Tambahkan policy untuk akses

```kotlin
import io.github.jan.supabase.storage.storage
import io.github.jan.supabase.storage.upload
import io.github.jan.supabase.storage.download

// Upload file (dari ByteArray)
suspend fun uploadFile(bucketName: String, path: String, data: ByteArray) {
    SupabaseClient.client.storage
        .from(bucketName)
        .upload(path, data) {
            upsert = true // overwrite jika sudah ada
        }
}

// Upload dari Android Uri
suspend fun uploadFromUri(context: Context, bucketName: String, fileName: String, uri: Uri) {
    val inputStream = context.contentResolver.openInputStream(uri)
    val bytes = inputStream?.readBytes() ?: return
    inputStream.close()

    SupabaseClient.client.storage
        .from(bucketName)
        .upload("uploads/$fileName", bytes) {
            upsert = true
        }
}

// Download file
suspend fun downloadFile(bucketName: String, path: String): ByteArray {
    return SupabaseClient.client.storage
        .from(bucketName)
        .downloadAuthenticated(path)
}

// Get public URL
fun getPublicUrl(bucketName: String, path: String): String {
    return SupabaseClient.client.storage
        .from(bucketName)
        .publicUrl(path)
}

// Delete file
suspend fun deleteFile(bucketName: String, path: String) {
    SupabaseClient.client.storage
        .from(bucketName)
        .delete(path)
}

// List files in folder
suspend fun listFiles(bucketName: String, folder: String) {
    val files = SupabaseClient.client.storage
        .from(bucketName)
        .list(folder)
    files.forEach { println("${it.name} - ${it.metadata}") }
}
```

### 1.7 Realtime

```kotlin
import io.github.jan.supabase.realtime.realtime
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.PostgresAction

// Listen to table changes
suspend fun listenToProducts() {
    val channel = SupabaseClient.client.channel("products-channel")

    val changes = channel.postgresChangeFlow<PostgresAction>("public") {
        table = "products"
    }

    // Collect di coroutine
    changes.collect { action ->
        when (action) {
            is PostgresAction.Insert -> println("New: ${action.record}")
            is PostgresAction.Update -> println("Updated: ${action.record}")
            is PostgresAction.Delete -> println("Deleted: ${action.oldRecord}")
            else -> {}
        }
    }

    channel.subscribe()
}
```

---

## Bagian 2: Appwrite

### 2.1 Setup Project Appwrite

1. Buka [cloud.appwrite.io](https://cloud.appwrite.io) dan buat akun
2. Buat project baru
3. Di **Overview**, klik **Add a Platform** > **Android**
4. Masukkan package name: `com.kanzankazu.app`
5. Catat **Project ID** dan **Endpoint**

### 2.2 Tambah Dependency

**`gradle/libs.versions.toml`**:

```toml
[versions]
# ... existing versions ...
appwriteVersion = "8.1.0"

[libraries]
# ... existing libraries ...
appwrite-sdk = { module = "io.appwrite:sdk-for-android", version.ref = "appwriteVersion" }
```

**`app/build.gradle`**:

```groovy
dependencies {
    // ... existing dependencies ...
    implementation libs.appwrite.sdk
}
```

**`AndroidManifest.xml`** — tambahkan di dalam tag `<application>`:

```xml
<uses-permission android:name="android.permission.INTERNET" />

<application ...>
    <!-- ... existing activities ... -->

    <!-- Appwrite OAuth Callback (ganti PROJECT_ID) -->
    <activity android:name="io.appwrite.views.CallbackActivity" android:exported="true">
        <intent-filter android:label="android_web_auth">
            <action android:name="android.intent.action.VIEW" />
            <category android:name="android.intent.category.DEFAULT" />
            <category android:name="android.intent.category.BROWSABLE" />
            <data android:scheme="appwrite-callback-YOUR_PROJECT_ID" />
        </intent-filter>
    </activity>
</application>
```

### 2.3 Inisialisasi Client

```kotlin
// com/kanzankazu/app/data/remote/AppwriteClient.kt

import android.content.Context
import io.appwrite.Client
import io.appwrite.services.Account
import io.appwrite.services.Databases
import io.appwrite.services.Storage
import io.appwrite.services.Realtime

object AppwriteClient {
    lateinit var client: Client
    lateinit var account: Account
    lateinit var databases: Databases
    lateinit var storage: Storage
    lateinit var realtime: Realtime

    fun init(context: Context) {
        client = Client(context)
            .setEndpoint("https://cloud.appwrite.io/v1") // atau region spesifik
            .setProject("YOUR_PROJECT_ID")

        account = Account(client)
        databases = Databases(client)
        storage = Storage(client)
        realtime = Realtime(client)
    }
}
```

Panggil `AppwriteClient.init(applicationContext)` di `Application` class atau `MainActivity.onCreate()`.

### 2.4 Authentication

```kotlin
import io.appwrite.ID

// Sign Up
suspend fun signUp(email: String, password: String) {
    AppwriteClient.account.create(
        userId = ID.unique(),
        email = email,
        password = password
    )
}

// Sign In
suspend fun signIn(email: String, password: String) {
    AppwriteClient.account.createEmailPasswordSession(
        email = email,
        password = password
    )
}

// Sign Out
suspend fun signOut() {
    AppwriteClient.account.deleteSession("current")
}

// Get current user
suspend fun currentUser() = try {
    AppwriteClient.account.get()
} catch (e: Exception) {
    null
}
```

### 2.5 Database

Setup di Appwrite Console:
1. Buka **Databases** > **Create Database**
2. Buat **Collection** di dalam database
3. Tambahkan **Attributes** (kolom) sesuai kebutuhan
4. Set **Permissions** di tab Settings collection

```kotlin
import io.appwrite.ID

// Create document
suspend fun createProduct(databaseId: String, collectionId: String, name: String, price: Double) {
    AppwriteClient.databases.createDocument(
        databaseId = databaseId,
        collectionId = collectionId,
        documentId = ID.unique(),
        data = mapOf(
            "name" to name,
            "price" to price
        )
    )
}

// List documents
suspend fun getProducts(databaseId: String, collectionId: String) {
    val response = AppwriteClient.databases.listDocuments(
        databaseId = databaseId,
        collectionId = collectionId
    )
    response.documents.forEach { doc ->
        println("${doc.id}: ${doc.data}")
    }
}

// Get single document
suspend fun getProduct(databaseId: String, collectionId: String, documentId: String) {
    val doc = AppwriteClient.databases.getDocument(
        databaseId = databaseId,
        collectionId = collectionId,
        documentId = documentId
    )
    println("${doc.id}: ${doc.data}")
}

// Update document
suspend fun updateProduct(databaseId: String, collectionId: String, documentId: String, name: String) {
    AppwriteClient.databases.updateDocument(
        databaseId = databaseId,
        collectionId = collectionId,
        documentId = documentId,
        data = mapOf("name" to name)
    )
}

// Delete document
suspend fun deleteProduct(databaseId: String, collectionId: String, documentId: String) {
    AppwriteClient.databases.deleteDocument(
        databaseId = databaseId,
        collectionId = collectionId,
        documentId = documentId
    )
}
```

### 2.6 Storage (Upload/Download File)

Setup di Appwrite Console:
1. Buka **Storage** > **Create Bucket**
2. Set nama, max file size, allowed extensions
3. Set **Permissions** (misal: `any` untuk read, `users` untuk write)

```kotlin
import io.appwrite.ID
import io.appwrite.models.InputFile

// Upload file dari path
suspend fun uploadFile(bucketId: String, filePath: String) {
    val file = AppwriteClient.storage.createFile(
        bucketId = bucketId,
        fileId = ID.unique(),
        file = InputFile.fromPath(filePath)
    )
    println("Uploaded: ${file.id}")
}

// Upload dari Android Uri
suspend fun uploadFromUri(context: Context, bucketId: String, uri: Uri, fileName: String) {
    val inputStream = context.contentResolver.openInputStream(uri)
    val bytes = inputStream?.readBytes() ?: return
    inputStream.close()

    // Tulis ke temp file
    val tempFile = java.io.File(context.cacheDir, fileName)
    tempFile.writeBytes(bytes)

    val file = AppwriteClient.storage.createFile(
        bucketId = bucketId,
        fileId = ID.unique(),
        file = InputFile.fromPath(tempFile.absolutePath)
    )
    tempFile.delete()
    println("Uploaded: ${file.id}")
}

// Download file
suspend fun downloadFile(bucketId: String, fileId: String): ByteArray {
    return AppwriteClient.storage.getFileDownload(
        bucketId = bucketId,
        fileId = fileId
    )
}

// Get file preview (untuk gambar)
suspend fun getFilePreview(bucketId: String, fileId: String): ByteArray {
    return AppwriteClient.storage.getFilePreview(
        bucketId = bucketId,
        fileId = fileId
    )
}

// Get file view URL
fun getFileViewUrl(bucketId: String, fileId: String): String {
    return "${AppwriteClient.client.endpoint}/storage/buckets/$bucketId/files/$fileId/view?project=YOUR_PROJECT_ID"
}

// Delete file
suspend fun deleteFile(bucketId: String, fileId: String) {
    AppwriteClient.storage.deleteFile(
        bucketId = bucketId,
        fileId = fileId
    )
}

// List files
suspend fun listFiles(bucketId: String) {
    val files = AppwriteClient.storage.listFiles(bucketId = bucketId)
    files.files.forEach { println("${it.id}: ${it.name}") }
}
```

### 2.7 Realtime

```kotlin
// Listen to document changes
fun listenToCollection(databaseId: String, collectionId: String) {
    val subscription = AppwriteClient.realtime.subscribe(
        "databases.$databaseId.collections.$collectionId.documents"
    ) { event ->
        println("Event: ${event.events}")
        println("Payload: ${event.payload}")
    }

    // Untuk unsubscribe nanti:
    // subscription.close()
}

// Listen to file changes in bucket
fun listenToStorage(bucketId: String) {
    AppwriteClient.realtime.subscribe(
        "buckets.$bucketId.files"
    ) { event ->
        println("Storage event: ${event.events}")
    }
}
```

---

## Tips Integrasi dengan Project Ini

### Simpan Keys dengan Aman

Jangan hardcode URL dan key. Gunakan `local.properties` atau `BuildConfig`:

```properties
# local.properties (jangan commit ke git!)
SUPABASE_URL=https://xxx.supabase.co
SUPABASE_ANON_KEY=eyJhbGciOiJIUzI1NiIs...
APPWRITE_ENDPOINT=https://cloud.appwrite.io/v1
APPWRITE_PROJECT_ID=your_project_id
```

```groovy
// app/build.gradle
android {
    defaultConfig {
        // Baca dari local.properties
        def localProperties = new Properties()
        def localPropertiesFile = rootProject.file('local.properties')
        if (localPropertiesFile.exists()) {
            localProperties.load(new FileInputStream(localPropertiesFile))
        }

        buildConfigField "String", "SUPABASE_URL", "\"${localProperties['SUPABASE_URL'] ?: ''}\""
        buildConfigField "String", "SUPABASE_ANON_KEY", "\"${localProperties['SUPABASE_ANON_KEY'] ?: ''}\""
    }
}
```

### Integrasi dengan Hilt (sudah ada di project)

Buat module DI untuk Supabase/Appwrite agar bisa di-inject ke ViewModel:

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object BaaSModule {

    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClient {
        return createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_ANON_KEY
        ) {
            install(Auth)
            install(Postgrest)
            install(Storage)
        }
    }
}
```

### Struktur Folder yang Disarankan

```
app/src/main/java/com/kanzankazu/app/
├── data/
│   └── remote/
│       ├── SupabaseClient.kt    // atau AppwriteClient.kt
│       └── repository/
│           └── StorageRepository.kt
├── di/
│   └── BaaSModule.kt           // Hilt module
├── ui/
│   └── screen/
│       └── UploadScreen.kt
└── ...
```

---

## Referensi

- **Supabase Kotlin SDK:** [github.com/supabase-community/supabase-kt](https://github.com/supabase-community/supabase-kt)
- **Supabase Docs (Kotlin):** [supabase.com/docs/reference/kotlin/introduction](https://supabase.com/docs/reference/kotlin/introduction)
- **Supabase Quickstart Android:** [supabase.com/docs/guides/getting-started/quickstarts/kotlin](https://supabase.com/docs/guides/getting-started/quickstarts/kotlin)
- **Appwrite Android SDK:** [github.com/appwrite/sdk-for-android](https://github.com/appwrite/sdk-for-android)
- **Appwrite Quickstart Android:** [appwrite.io/docs/quick-starts/android](https://appwrite.io/docs/quick-starts/android)
- **Appwrite Storage Docs:** [appwrite.io/docs/products/storage/upload-download](https://appwrite.io/docs/products/storage/upload-download)
