package com.kanzankazu.kanzanutil

import android.os.Handler
import android.os.Looper

/**
 * A utility class to handle typing activity detection using a timer. This class is useful for scenarios where
 * a timeout is needed to determine if the user is actively typing or has finished typing, such as in chat applications.
 *
 * @constructor
 * Creates a [TypingTimer] with a specified default typing timeout duration.
 * If no timeout is specified, the default timeout is set to 1000ms.
 *
 * Example:
 * ```kotlin
 * val typingTimer = TypingTimer(1500)
 *
 * typingTimer.startTypingTimer(
 *    onTyping = { isTyping ->
 *        if (isTyping) {
 *            println("User is typing...")
 *        }
 *    },
 *    onTimeout = {
 *        println("Typing has stopped.")
 *    }
 * )
 *
 * // To reset the timer:
 * typingTimer.resetTypingTimer()
 *
 * // To set a new timeout duration:
 * typingTimer.setTypingTimeout(2000)
 * ```
 */
class TypingTimer(private val _typingTimeout: Long = 1000) {

    private var typingTimer: Handler? = null

    private var typingTimeout = _typingTimeout

    fun startTypingTimer(onTyping: (Boolean) -> Unit = {}, onTimeout: () -> Unit) {
        resetTypingTimer()
        onTyping.invoke(true)

        typingTimer = Handler(Looper.getMainLooper())

        typingTimer?.postDelayed({
            onTyping.invoke(false)
            onTimeout.invoke()
        }, _typingTimeout)
    }

    fun resetTypingTimer() {
        typingTimer?.removeCallbacksAndMessages(null)
    }


    fun setTypingTimeout(__typingTimeout: Long) {
        typingTimeout = __typingTimeout
    }
}