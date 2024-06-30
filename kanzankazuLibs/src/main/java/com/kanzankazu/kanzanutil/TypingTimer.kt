package com.kanzankazu.kanzanutil

import android.os.Handler
import android.os.Looper

class TypingTimer(private val _typingTimeout: Long = 1000) {
    private var typingTimer: Handler? = null
    private var typingTimeout = _typingTimeout

    fun startTypingTimer(onTyping: (Boolean) -> Unit = {}, onTimeout: () -> Unit) {
        // Reset timer saat pengguna mengetik
        resetTypingTimer()
        onTyping.invoke(true)
        typingTimer = Handler(Looper.getMainLooper())
        typingTimer?.postDelayed({
            // Timeout tercapai, jalankan fungsi lain di sini
            onTyping.invoke(false)
            onTimeout.invoke()
        }, _typingTimeout)
    }

    fun resetTypingTimer() {
        // Reset timer saat pengguna mengetik
        typingTimer?.removeCallbacksAndMessages(null)
    }

    fun setTypingTimeout(__typingTimeout: Long) {
        typingTimeout = __typingTimeout
    }
}
