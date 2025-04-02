package com.kanzankazu.kanzanutil

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.util.DebugUtils
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber
import javax.inject.Inject


/**
 * Implementasi [Timber.Tree] yang mencatat pesan dan pengecualian (exception) ke Firebase Crashlytics.
 *
 * Kelas ini mengintegrasikan logging Timber dengan Crashlytics, memungkinkan Anda untuk melacak
 * kejadian (event) dan kesalahan (error) secara langsung di konsol Crashlytics.
 *
 * Fitur Utama:
 * - **Penyaringan (Filtering):** Log disaring berdasarkan `minLogPriority` yang ditentukan. Hanya
 *   pesan dengan prioritas yang sama atau lebih tinggi dari ambang batas yang akan dikirim ke
 *   Crashlytics.
 * - **Output Debug:** Pada *build* debug (dikontrol oleh `isDebug`), log juga dicetak ke logcat
 *   Android untuk keperluan *debugging* lokal.
 * - **Penandaan (Tagging):** Jika tag tidak diberikan saat pemanggilan log, tag default
 *   "CrashlyticsTimberTree" akan digunakan.
 * - **Pencatatan Pengecualian (Exception Logging):** Setiap `Throwable` yang diberikan ke metode
 *   `log` secara otomatis dicatat sebagai pengecualian di Crashlytics, memberikan laporan
 *   kesalahan yang terperinci.
 * - **Pesan Log yang Terformat:** Setiap pesan log digabungkan dengan tag-nya dalam format
 *   "`tag`: `message`" sebelum dikirim ke Crashlytics.
 * - **Thread Safety:** Implementasi ini aman digunakan dari berbagai thread.
 * - **Mencegah crash:** melakukan pengecekan jika priority tidak sesuai dengan minLogPriority
 *
 * @property isDebug Bendera boolean yang menunjukkan apakah aplikasi berada dalam *build* debug.
 *                   Jika `true`, log juga akan dicetak ke logcat Android.
 * @property minLogPriority Prioritas log minimum yang diperlukan agar pesan dapat dikirim ke
 *                          Crashlytics. Standarnya adalah [Log.INFO]. Prioritas log didefinisikan
 *                          dalam kelas [Log] (misalnya, [Log.VERBOSE], [Log.DEBUG], [Log.INFO],
 *                          [Log.WARN], [Log.ERROR], [Log.ASSERT]).
 *
 */
class CrashlyticsTimberTree @Inject constructor(
    private val isDebug: Boolean,
    private val minLogPriority: Int = Log.INFO,
) : Timber.Tree() {

    companion object {
        private const val DEFAULT_TAG = "CrashlyticsTimberTree"
    }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        // Memastikan priority yang di log sesuai dengan minLogPriority yang sudah di tentukan
        if (priority < minLogPriority) return

        val finalTag = tag ?: DEFAULT_TAG

        // Jika dalam mode debug, cetak log ke Logcat
        if (isDebug) logToLogcat(priority, finalTag, message, t) // best practice 1

        // Kirim log dan throwable ke Firebase Crashlytics
        logToCrashlytics(finalTag, message, t) // best practice 2
    }

    @SuppressLint("LogNotTimber")
    private fun logToLogcat(priority: Int, tag: String, message: String, throwable: Throwable?) {
        Log.println(priority, tag, message)
        throwable?.let { Log.e(tag, "Error: ", it) }
    }

    private fun logToCrashlytics(tag: String, message: String, throwable: Throwable?) {
        FirebaseCrashlytics.getInstance().run {
            log("$tag: $message")
            throwable?.let { recordException(it) }
        }
    }
}