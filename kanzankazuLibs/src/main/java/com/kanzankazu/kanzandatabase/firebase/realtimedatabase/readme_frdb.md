# 🔥 Perbandingan Query Firebase Realtime Database (FRDB) vs SQL

Dokumen ini menjelaskan **perbandingan cara query data di Firebase Realtime Database** (FRDB) dengan **SQL tradisional**, dari level **basic sampai expert**, lengkap dengan contoh, kelebihan, dan batasannya.

---

## 🧱 1️⃣ Dasar Konsep

| Aspek          | SQL (Relasional)            | Firebase Realtime Database (NoSQL Tree)             |
|----------------|-----------------------------|-----------------------------------------------------|
| Struktur       | Tabel dengan kolom & relasi | JSON tree (nested node)                             |
| Primary Key    | Auto increment / UUID       | Key di path (`/users/{uid}`)                        |
| Relasi         | Join antar tabel            | Nested atau reference key                           |
| Query Bahasa   | SQL (`SELECT * FROM ...`)   | Firebase Query API (`orderByChild`, `equalTo`, dll) |
| Realtime       | ❌ Tidak realtime            | ✅ Realtime listener                                 |
| Offline        | Bergantung DB lokal         | ✅ Built-in caching                                  |
| Resource limit | Server-based                | Client-based (100 max connection per app instance)  |

---

## 🪶 2️⃣ Query Dasar

| Tujuan           | SQL                                     | Firebase                            |
|------------------|-----------------------------------------|-------------------------------------|
| Ambil semua data | `SELECT * FROM users;`                  | `ref("users").get()`                |
| Ambil 1 data     | `SELECT * FROM users WHERE id=1;`       | `ref("users/1").get()`              |
| Insert           | `INSERT INTO users VALUES (...);`       | `ref("users").push(value)`          |
| Update           | `UPDATE users SET name='A' WHERE id=1;` | `ref("users/1/name").setValue("A")` |
| Delete           | `DELETE FROM users WHERE id=1;`         | `ref("users/1").removeValue()`      |

---

## ⚙️ 3️⃣ Query Kondisi (WHERE)

| Tujuan               | SQL                                        | Firebase                                                           |
|----------------------|--------------------------------------------|--------------------------------------------------------------------|
| Filter kolom = nilai | `SELECT * FROM users WHERE age = 18;`      | `ref("users").orderByChild("age").equalTo(18)`                     |
| Filter kolom < nilai | `SELECT * FROM users WHERE age < 18;`      | `ref("users").orderByChild("age").endAt(17)`                       |
| Filter kolom > nilai | `SELECT * FROM users WHERE age > 18;`      | `ref("users").orderByChild("age").startAt(19)`                     |
| Filter string        | `SELECT * FROM users WHERE name = 'John';` | `ref("users").orderByChild("name").equalTo("John")`                |
| Limit hasil          | `SELECT * FROM users LIMIT 5;`             | `ref("users").limitToFirst(5)`                                     |
| Sort ascending       | `ORDER BY name ASC`                        | `orderByChild("name")`                                             |
| Sort descending      | `ORDER BY name DESC`                       | `orderByChild("name").limitToLast(5)` *(manual reverse di client)* |

---

## 🧩 4️⃣ Query Gabungan (JOIN / NESTED)

| Tujuan                   | SQL                                                            | Firebase                                                                       |
|--------------------------|----------------------------------------------------------------|--------------------------------------------------------------------------------|
| JOIN antar tabel         | `SELECT * FROM users JOIN orders ON users.id = orders.user_id` | Tidak ada JOIN. Simpan `userId` di node `orders/{id}/userId` dan fetch manual. |
| Nested data              | `SELECT orders FROM user WHERE user_id = 1`                    | `ref("orders").orderByChild("userId").equalTo("1")`                            |
| Aggregation (COUNT, SUM) | `SELECT COUNT(*) FROM orders`                                  | Tidak native — lakukan di client dengan looping snapshot                       |
| Group by                 | `SELECT city, COUNT(*) FROM users GROUP BY city`               | Manual aggregate di client                                                     |
| Subquery                 | `SELECT * FROM users WHERE id IN (SELECT user_id FROM orders)` | Harus 2x query manual di client                                                |
| EXISTS check             | `SELECT EXISTS(...)`                                           | `ref("users/{id}").get().exists()`                                             |

---

## 🧠 5️⃣ Query Lanjut (Expert Level)

| Tujuan                | SQL                                  | Firebase                                                                     |
|-----------------------|--------------------------------------|------------------------------------------------------------------------------|
| Multi-condition (AND) | `WHERE age > 20 AND city='Jakarta'`  | Tidak bisa langsung → buat composite index (`age_city = "20_Jakarta"`)       |
| Multi-table relation  | `JOIN`                               | Gunakan “denormalized” structure (duplicate minimal info)                    |
| Range + Equal         | `WHERE score >= 80 AND score <= 100` | `orderByChild("score").startAt(80).endAt(100)`                               |
| LIKE / Pattern        | `WHERE name LIKE 'Jo%'`              | Gunakan prefix match: `orderByChild("name").startAt("Jo").endAt("Jo\uf8ff")` |
| Pagination            | `LIMIT OFFSET`                       | Gunakan `startAt(lastKey)` + `limitToFirst(n)`                               |
| Random row            | `ORDER BY RAND()`                    | Tidak ada native — gunakan random key di client                              |
| Transaction           | `BEGIN TRANSACTION`                  | `ref().runTransaction()`                                                     |

---

## ⚡ 6️⃣ Operasi CRUD Cepat

| Operasi | SQL                              | Firebase                          |
|---------|----------------------------------|-----------------------------------|
| Tambah  | `INSERT INTO table ...`          | `.push(value)`                    |
| Ambil   | `SELECT * FROM table WHERE ...`  | `.orderByChild(...).equalTo(...)` |
| Ubah    | `UPDATE table SET ... WHERE ...` | `.child(id).setValue(value)`      |
| Hapus   | `DELETE FROM table WHERE ...`    | `.child(id).removeValue()`        |

---

## 🧩 7️⃣ Realtime Listener vs Query Biasa

| Jenis             | SQL                   | Firebase                                 |
|-------------------|-----------------------|------------------------------------------|
| Query biasa       | `SELECT` hasil sekali | `.get()` (sekali ambil snapshot)         |
| Listener realtime | ❌ tidak ada           | `.addValueEventListener()`               |
| Unsubscribe       | ❌ tidak relevan       | `.removeEventListener(listener)`         |
| Offline caching   | manual                | built-in (`setPersistenceEnabled(true)`) |

---

## 🧠 8️⃣ Tips Optimasi Query Firebase

| Tujuan                                  | Cara                                                         |
|-----------------------------------------|--------------------------------------------------------------|
| Kurangi koneksi aktif                   | Gunakan max 10–20 listener per instance                      |
| Query besar                             | Pisah node besar jadi beberapa subnode (flat design)         |
| Gunakan index                           | Tambah `".indexOn": ["childField"]` di rules JSON            |
| Hindari deep nesting                    | Simpan list terpisah per entity (denormalisasi minimal)      |
| Gunakan `.info/connected`               | Untuk menunda query ketika offline                           |
| Gunakan `.get()` untuk snapshot tunggal | Ketika tidak butuh realtime                                  |
| Gunakan `limitToFirst` / `limitToLast`  | Untuk batasi hasil list besar                                |
| Cache lokal aktif                       | `FirebaseDatabase.getInstance().setPersistenceEnabled(true)` |

---

## 🔒 9️⃣ Batasan FRDB vs SQL

| Fitur                          | SQL | Firebase                            |
|--------------------------------|-----|-------------------------------------|
| JOIN antar tabel               | ✅   | ❌ (manual merge di client)          |
| Query kompleks (AND/OR)        | ✅   | ⚠️ terbatas                         |
| Index otomatis                 | ✅   | ⚠️ manual di rules                  |
| Query nested dalam nested      | ✅   | ❌                                   |
| Atomic transaksi multi tabel   | ✅   | ⚠️ Terbatas (hanya node lokal)      |
| Scale besar dengan query berat | ✅   | ⚠️ Tidak efisien, gunakan Firestore |

---

## 💎 10️⃣ Kapan Gunakan FRDB vs SQL

| Kondisi                              | Pilih                         |
|--------------------------------------|-------------------------------|
| Realtime sync (chat, tracking, game) | 🔥 Firebase Realtime Database |
| Query kompleks, laporan, agregasi    | 🧮 SQL / Firestore            |
| Offline first app                    | 🔥 Firebase (dengan caching)  |
| Big data analisis                    | 🧠 SQL / BigQuery             |
| Multi user realtime kecil-menengah   | 🔥 Firebase                   |
| Backend bisnis / ERP skala besar     | 🧮 SQL / PostgreSQL           |

---

## 📘 Referensi
- [Firebase Realtime Database Queries](https://firebase.google.com/docs/database/android/lists-of-data)
- [Firebase Query Limitations](https://firebase.google.com/docs/database/security/indexing-data)
- [SQL Reference (W3Schools)](https://www.w3schools.com/sql/)
- [Firebase Offline Capabilities](https://firebase.google.com/docs/database/android/offline-capabilities)

---

### ✨ Penulis:
**Kanzan Project Docs**  
Versi: 1.0 — *“Kotlin Clean & Resource-Efficient Realtime Database”*

