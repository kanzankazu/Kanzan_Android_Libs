package com.kanzankazu.kanzanutil

import android.os.Handler
import android.os.Looper
import android.widget.EditText
import android.widget.TextView

fun EditText.typing(
    text: String,
    isRepeatForever: Boolean = false,
    delayMillis: Long = 100,
    isNewTextAlwaysClear: Boolean = true,
    isRepeatForeverAlwaysClear: Boolean = true,
    onFinish: (() -> Unit)? = null,
) {
    if (isNewTextAlwaysClear) setText("")
    val handler = Handler(Looper.getMainLooper())
    val task: Runnable = object : Runnable {
        override fun run() {
            if (isRepeatForeverAlwaysClear) setText("")
            text.forEachIndexed { index, c ->
                handler.postDelayed({ append(c.toString()) }, index * delayMillis)
            }
            if (isRepeatForever) {
                handler.postDelayed(this, text.length * delayMillis)
            } else {
                onFinish?.invoke()
            }
        }
    }

    handler.post(task)
}

fun TextView.typing(
    text: String,
    isRepeatForever: Boolean = false,
    delayMillis: Long = 100,
    isNewTextAlwaysClear: Boolean = true,
    isRepeatForeverAlwaysClear: Boolean = true,
    onFinish: (() -> Unit)? = null,
) {
    if (isNewTextAlwaysClear) setText("")
    val handler = Handler(Looper.getMainLooper())
    val task: Runnable = object : Runnable {
        override fun run() {
            if (isRepeatForeverAlwaysClear) setText("")
            text.forEachIndexed { index, c ->
                handler.postDelayed({ append(c.toString()) }, index * delayMillis)
            }
            if (isRepeatForever) {
                handler.postDelayed(this, text.length * delayMillis)
            } else {
                onFinish?.invoke()
            }
        }
    }

    handler.post(task)
}
