package com.kanzankazu.kanzanutil

import android.os.Handler
import android.os.Looper
import android.widget.EditText
import android.widget.TextView

private fun TextView.performTyping(
    text: String,
    isRepeatForever: Boolean,
    delayMillis: Long,
    isClearBeforeTyping: Boolean,
    isClearEachRepeat: Boolean,
    onFinish: (() -> Unit)?
) {
    if (isClearBeforeTyping) setText("")
    val handler = Handler(Looper.getMainLooper())

    fun appendWithDelay() {
        text.forEachIndexed { index, c ->
            handler.postDelayed({ append(c.toString()) }, index * delayMillis)
        }
    }

    val task: Runnable = object : Runnable {
        override fun run() {
            if (isClearEachRepeat) setText("")
            appendWithDelay()
            if (isRepeatForever) {
                handler.postDelayed(this, text.length * delayMillis)
            } else {
                onFinish?.invoke()
            }
        }
    }

    handler.post(task)
}

fun EditText.typing(
    text: String,
    isRepeatForever: Boolean = false,
    delayMillis: Long = 100,
    isNewTextAlwaysClear: Boolean = true,
    isRepeatForeverAlwaysClear: Boolean = true,
    onFinish: (() -> Unit)? = null,
) {
    performTyping(text, isRepeatForever, delayMillis, isNewTextAlwaysClear, isRepeatForeverAlwaysClear, onFinish)
}

fun TextView.typing(
    text: String,
    isRepeatForever: Boolean = false,
    delayMillis: Long = 100,
    isNewTextAlwaysClear: Boolean = true,
    isRepeatForeverAlwaysClear: Boolean = true,
    onFinish: (() -> Unit)? = null,
) {
    performTyping(text, isRepeatForever, delayMillis, isNewTextAlwaysClear, isRepeatForeverAlwaysClear, onFinish)
}