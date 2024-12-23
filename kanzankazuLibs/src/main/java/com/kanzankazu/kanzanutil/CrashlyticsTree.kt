package com.kanzankazu.kanzanutil

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber
import javax.inject.Inject

/**
 * Custom Timber Tree untuk mengirim log ke Crashlytics
 * @param isDebug Mode debug atau tidak
 * @author kanzankazu
 * @since 2023 - 08 - 06
 * @constructor Membuat pohon Timber kustom kosong untuk mengirim log crashlytics ke firebase
 */
class CrashlyticsTree @Inject constructor(private val isDebug: Boolean) : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, throwable: Throwable?) {
        // Filter hanya log yang penting untuk dikirim ke Crashlytics
        if (priority < Log.INFO) return // Abaikan log di bawah priority INFO

        // Log ke Timber jika dalam mode debug
        if (isDebug) Log.println(priority, tag ?: "CrashlyticsTree - log", message)

        // Kirim log ke Crashlytics dengan informasi tambahan dan screen terakhir
        val crashlytics = FirebaseCrashlytics.getInstance().apply {
            setUserId("12345") // ID pengguna
            setCustomKey("user_email", "user@example.com") // Informasi tambahan
            setCustomKey("last_screen", "MainActivity") // Contoh screen terakhir
            log("$tag: $message")
            sendUnsentReports()
        }

        // Kirim Throwable jika ada
        if (throwable != null) crashlytics.recordException(throwable)
    }
}
