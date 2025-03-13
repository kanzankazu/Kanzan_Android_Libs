package com.kanzankazu.kanzanutil

import android.os.Handler
import android.os.Looper

/**
 * Class TypingTimer
 * Digunakan untuk mendeteksi jeda waktu tertentu selama aktivitas mengetik, seperti implementasi indikator "sedang mengetik" pada aplikasi chat.
 * @param _typingTimeout Waktu timeout (dalam milidetik) yang digunakan untuk menentukan kapan mengetik dianggap selesai. Defaultnya adalah 1000ms (1 detik).
 */
class TypingTimer(private val _typingTimeout: Long = 1000) {

    private var typingTimer: Handler? = null

    private var typingTimeout = _typingTimeout

    /**
     * Memulai timer untuk mendeteksi aktivitas mengetik.
     * @param onTyping Fungsi callback ketika pengguna sedang mengetik. Menerima status `true` saat mengetik dimulai.
     * @param onTimeout Fungsi callback yang dipanggil ketika waktu timeout tercapai (dianggap selesai mengetik).
     */
    fun startTypingTimer(onTyping: (Boolean) -> Unit = {}, onTimeout: () -> Unit) {
        resetTypingTimer()
        onTyping.invoke(true)

        typingTimer = Handler(Looper.getMainLooper())

        typingTimer?.postDelayed({
            onTyping.invoke(false)
            onTimeout.invoke()
        }, _typingTimeout)
    }

    /**
     * Reset dan menghentikan timer.
     * Digunakan untuk membatalkan / mereset panggilan yang tertunda ketika pengguna masih aktif mengetik.
     */
    fun resetTypingTimer() {
        typingTimer?.removeCallbacksAndMessages(null)
    }

    /**
     * Mengatur waktu timeout baru untuk mendeteksi aktivitas mengetik.
     * @param __typingTimeout Waktu timeout baru dalam milidetik.
     */
    fun setTypingTimeout(__typingTimeout: Long) {
        typingTimeout = __typingTimeout
    }
}