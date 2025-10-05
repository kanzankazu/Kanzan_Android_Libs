/*
 * Copyright 2018 Chaos
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kanzankazu.kanzanwidget.pinview

import android.text.Selection
import android.text.Spannable
import android.text.method.MovementMethod
import android.view.KeyEvent
import android.view.MotionEvent
import android.widget.TextView

/**
 * For disable arrow key
 *
 * @author Chaos
 * 31/03/2018
 */
internal class DefaultMovementMethod private constructor() : MovementMethod {

    override fun initialize(widget: TextView, text: Spannable) {
        // It will mark the IMM as openable
        Selection.setSelection(text, 0)
    }

    override fun onKeyDown(widget: TextView, text: Spannable, keyCode: Int, event: KeyEvent): Boolean {
        return false
    }

    override fun onKeyUp(widget: TextView, text: Spannable, keyCode: Int, event: KeyEvent): Boolean {
        return false
    }

    override fun onKeyOther(view: TextView, text: Spannable, event: KeyEvent): Boolean {
        return false
    }

    override fun onTakeFocus(widget: TextView, text: Spannable, direction: Int) {
        // Do nothing
    }

    override fun onTrackballEvent(widget: TextView, text: Spannable, event: MotionEvent): Boolean {
        return false
    }

    override fun onTouchEvent(widget: TextView, text: Spannable, event: MotionEvent): Boolean {
        return false
    }

    override fun onGenericMotionEvent(widget: TextView, text: Spannable, event: MotionEvent): Boolean {
        return false
    }

    override fun canSelectArbitrarily(): Boolean {
        return false
    }

    companion object {
        private var sInstance: DefaultMovementMethod? = null

        @JvmStatic
        fun getInstance(): MovementMethod {
            return sInstance ?: DefaultMovementMethod().also { sInstance = it }
        }
    }
}
