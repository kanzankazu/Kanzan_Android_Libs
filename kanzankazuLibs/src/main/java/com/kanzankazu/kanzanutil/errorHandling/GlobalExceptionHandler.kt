package com.kanzankazu.kanzanutil.errorHandling

import android.content.Context
import android.content.Intent
import com.chuckerteam.chucker.api.ChuckerCollector
import com.google.gson.Gson
import com.kanzankazu.kanzanutil.kanzanextension.type.debugMessageError
import kotlin.system.exitProcess

/**
 * Konteks aplikasi diperlukan untuk meluncurkan aktivitas.
 *
 * DefaultHandler digunakan jika terjadi kesalahan saat meluncurkan aktivitas.
 * Jika terjadi kesalahan, maka default Thread.UncaughtExceptionHandler akan dijalankan.
 * Sebagai hasilnya, dialog crash default Android akan muncul.
 *
 * Aktivitas yang akan diluncurkan :D, data pengecualian akan disimpan sebagai string JSON.
 * Dapat dengan mudah dikonversi kembali menjadi [Throwable] dengan [Gson.fromJson].
 */

class GlobalExceptionHandler private constructor(
    private val applicationContext: Context,
    private val defaultHandler: Thread.UncaughtExceptionHandler,
    private val activityToBeLaunched: Class<*>,
) : Thread.UncaughtExceptionHandler {

    override fun uncaughtException(thread: Thread, throwable: Throwable) {
        try {
            val collector = ChuckerCollector(applicationContext)
            collector.onError("Error caught on ${thread.name} thread", throwable)
            launchActivity(applicationContext, activityToBeLaunched, throwable)
            exitProcess(0)
        } catch (e: Exception) {
            e.debugMessageError("GlobalExceptionHandler - uncaughtException")
            defaultHandler.uncaughtException(thread, throwable)
        }
    }

    private fun launchActivity(
        applicationContext: Context,
        activity: Class<*>,
        exception: Throwable,
    ) {
        val crashedIntent = Intent(applicationContext, activity).also {
            it.putExtra(INTENT_DATA_NAME, Gson().toJson(exception))
        }
        crashedIntent.addFlags( // Clears all previous activities. So backstack will be gone
            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_NEW_TASK
        )
        crashedIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        applicationContext.startActivity(crashedIntent)
    }

    companion object {
        private const val INTENT_DATA_NAME = "CrashData"

        /**
         * [applicationContext]: Required for launching the activity.
         *
         * [activityToBeLaunched]: The activity that to be launched :D,
         * it will put Exception data as JSON string. It can be
         * easily converted back to [Throwable] with [getThrowableFromIntent].
         *
         * **Example usage at activity:**
         * ```
         * GlobalExceptionHandler.getThrowableFromIntent(intent).let {
         *   Log.e(TAG, "Error Data: ", it)
         *   // Send logs or do your stuff...
         * }
         * ```
         */
        fun initialize(
            applicationContext: Context,
            activityToBeLaunched: Class<*>,
        ) {
            val handler = GlobalExceptionHandler(
                applicationContext,
                Thread.getDefaultUncaughtExceptionHandler() as Thread.UncaughtExceptionHandler,
                activityToBeLaunched
            )
            Thread.setDefaultUncaughtExceptionHandler(handler)
        }

        /**
         * Gets throwable data from activity's intent. It'll return null if stringExtra has not been
         * found or another reasons.
         */
        fun getThrowableFromIntent(intent: Intent): Throwable? {
            return try {
                Gson().fromJson(intent.getStringExtra(INTENT_DATA_NAME), Throwable::class.java)
            } catch (e: Exception) {
                e.debugMessageError("GlobalExceptionHandler - getThrowableFromIntent")
                null
            }
        }
    }
}