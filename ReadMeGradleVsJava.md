# 📱 Android Project Setup (Stabil & Konsisten)

Dokumen ini menjelaskan versi dan konfigurasi yang digunakan dalam project ini agar build tetap stabil di semua perangkat developer maupun CI/CD.

---

## ✅ Versi yang Digunakan

| Komponen              | Versi                        |
|-----------------------|------------------------------|
| Java Development Kit  | 17 (atau 21 jika perlu)      |
| Gradle                | 8.5                          |
| Android Gradle Plugin | 8.2.0                        |
| Kotlin                | (sesuaikan dengan kebutuhan) |

---

## 🧩 Rekomendasi Versi Kompatibel

| Android Gradle Plugin | Versi Gradle Minimum | Versi Java Minimum |
|-----------------------|----------------------|--------------------|
| 7.0.0                 | 7.0                  | Java 11            |
| 7.2.0                 | 7.3.3                | Java 11            |
| 7.4.2                 | 7.5                  | Java 11            |
| 8.0.0                 | 8.0                  | Java 17            |
| 8.1.0                 | 8.0                  | Java 17            |
| 8.2.0                 | 8.2                  | Java 17            |
| 8.3.0+                | 8.3+                 | Java 17 / 21       |

---

## 🔧 Struktur & Konfigurasi

### 1. Java

* Pastikan Java 17 atau Java 21 sudah terinstal.

* Atur JDK di Android Studio:

  ```
  File > Project Structure > SDK Location > JDK Location
  ```

  Contoh:

  ```
  /usr/lib/jvm/java-17-openjdk
  ```

* Atur juga `JAVA_HOME` di terminal:

  ```bash
  export JAVA_HOME=/path/to/java17
  export PATH=$JAVA_HOME/bin:$PATH
  ```

---

### 2. Gradle

Gradle dikunci di `gradle-wrapper.properties`:

```properties
distributionUrl=https\://services.gradle.org/distributions/gradle-8.5-all.zip
```

> ⚠️ Hindari penggunaan `+` seperti `gradle-8.+.zip` agar versi tidak berubah otomatis.

---

### 3. Android Gradle Plugin (AGP)

Versi AGP dikunci di `build.gradle (Project-level)`:

```kotlin
buildscript {
  dependencies {
    classpath("com.android.tools.build:gradle:8.2.0")
  }
}
```

> Hindari penggunaan versi `+`.

---

### 4. Java Compatibility (di `build.gradle` Module)

```kotlin
android {
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
}
```

---

## 🔒 Tujuan Penguncian Versi

* Menghindari error seperti `Unsupported Java` atau `Plugin requires minimum Gradle...`.
* Menjamin semua developer menghasilkan build yang identik.
* Mencegah auto-update yang menyebabkan konflik.
* Memastikan kestabilan saat build di CI/CD.

---

## 🛠 Rekomendasi Tool Tambahan

| Tool   | Fungsi                                           |
|--------|--------------------------------------------------|
| SDKMAN | Install dan switch Java dengan mudah             |
| asdf   | Versi Java per project via `.tool-versions`      |
| CI/CD  | Pastikan menggunakan versi sama seperti di lokal |

---

## 📎 Contoh Setup Java Lokal

```bash
# Install Java 17 via SDKMAN
sdk install java 17.0.10-tem
sdk use java 17.0.10-tem

# Cek versi Java
java -version
# Output: openjdk version "17.0.10"
```

---

## 📘 Catatan Tambahan

Jika kamu menggunakan fitur-fitur seperti:

* Jetpack Compose
* Hilt / Dagger
* Room
* Kotlin Flow
* Clean Architecture

Disarankan minimal menggunakan:

* Java 17
* Gradle 8.5
* AGP 8.2.0

---

Happy Coding 🚀
