package com.kanzankazu.kanzanutil.kanzanextension.view

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.Selection
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.MotionEvent
import android.view.View
import android.view.ViewParent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputLayout
import com.kanzankazu.R
import com.kanzankazu.kanzanutil.kanzanextension.toDigits
import com.kanzankazu.kanzanutil.kanzanextension.type.dpTopx
import com.kanzankazu.kanzanutil.kanzanextension.type.formatToRupiah
import com.kanzankazu.kanzanutil.kanzanextension.type.toIntOrDefault

fun EditText.string(defaultValue: String = ""): String = if (text.toString().isNotEmpty()) text.toString().trim { it <= ' ' } else defaultValue

fun EditText.int(defaultValue: Int = 0): Int = if (string().isNotEmpty()) string().toDigits().toIntOrDefault() else defaultValue

fun EditText.long(defaultValue: Long = 0): Long = if (string().isNotEmpty()) string().toDigits().toLong() else defaultValue

fun EditText.makeClickable() {
    isClickable = true
    isFocusable = false
    inputType = InputType.TYPE_NULL
}

fun EditText.setDrawable(@DrawableRes leftDrawable: Int, @DrawableRes topDrawable: Int, @DrawableRes rightDrawable: Int, @DrawableRes bottomDrawable: Int) {
    this.setCompoundDrawablesWithIntrinsicBounds(
        leftDrawable,
        topDrawable,
        rightDrawable,
        bottomDrawable
    )
}

fun EditText.setDrawable(leftDrawable: Drawable?, topDrawable: Drawable?, rightDrawable: Drawable?, bottomDrawable: Drawable?) {
    this.setCompoundDrawablesWithIntrinsicBounds(
        leftDrawable,
        topDrawable,
        rightDrawable,
        bottomDrawable
    )
}

fun EditText.setBackground(@DrawableRes res: Int) {
    background = ContextCompat.getDrawable(context, res)
}

fun EditText.getTil(): TextInputLayout? {
    return getTil(this.parent, 3)
}

fun EditText.onFocusOut(onFocusOut: () -> Unit) {
    setOnFocusChangeListener { _, b -> if (!b) onFocusOut() }
}

fun EditText.onFocusIn(onFocusIn: () -> Unit) {
    setOnFocusChangeListener { _, b -> if (b) onFocusIn() }
}

fun EditText.onFocus(hasFocus: (hasFocus: Boolean) -> Unit) {
    setOnFocusChangeListener { _, b -> hasFocus(b) }
}

fun EditText.onTextChange(watcher: TextWatcher) {
    addTextChangedListener(watcher)
}

fun EditText.onTextChange(onTextChange: (String) -> Unit) {
    addTextChangedListener { onTextChange(it.toString()) }
}

fun EditText.afterTextChanged(
    parseToCurrency: Boolean = false,
    afterTextChanged: (String) -> Unit,
) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }

        override fun afterTextChanged(editable: Editable?) {
            removeTextChangedListener(this)
            if (parseToCurrency && text.toString().isNotEmpty()) {
                val formatted = text.toString().formatToRupiah()
                setText(formatted)
                setSelection(formatted.length)
            }
            afterTextChanged.invoke(editable.toString())
            addTextChangedListener(this)
        }
    })
}

fun EditText.setEnterDone() {
    imeOptions = EditorInfo.IME_ACTION_DONE
}

fun EditText.clickDone(onClickGoDone: (String) -> Unit) {
    setOnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_DONE) onClickGoDone(this.toString())
        false
    }
}

fun EditText.setEnterGo() {
    imeOptions = EditorInfo.IME_ACTION_GO
}

fun EditText.clickGo(onClickGoDone: (String) -> Unit) {
    setOnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_GO) onClickGoDone(this.string())
        false
    }
}

fun EditText.setEnterNext() {
    imeOptions = EditorInfo.IME_ACTION_NEXT
}

fun EditText.clickNext(onClickNext: (String) -> Unit) {
    setOnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_NEXT) onClickNext(this.string())
        false
    }
}

fun EditText.setEnterSearch() {
    imeOptions = EditorInfo.IME_ACTION_SEARCH
}

fun EditText.clickSearch(onClickNext: (String) -> Unit) {
    setOnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_SEARCH) onClickNext(this.string())
        false
    }
}

fun EditText.clickIme(onEnter: (String) -> Unit) {
    setOnEditorActionListener { _, _, _ ->
        onEnter(this.string())
        false
    }
}

/**
 * @param position LEFT=0,TOP=1,RIGHT=2,BOTTOM = 3
 */
fun EditText.clickDrawable(position: Int, listener: () -> Unit) {
    clickDrawable(DrawablePosition.values()[position], listener)
}

/**
 * @param position 0=left, 1=top, 2=right, 3=bottom
 * @param listener for listener when click drawable
 */
@SuppressLint("ClickableViewAccessibility")
fun EditText.clickDrawable(position: DrawablePosition, listener: () -> Unit) {
    this.setOnTouchListener(View.OnTouchListener { _, event ->
        val drawableLeft = 0
        val drawableTop = 1
        val drawableRight = 2
        val drawableBottom = 3
        if (event.action == MotionEvent.ACTION_UP) {
            when (position) {
                DrawablePosition.LEFT -> {
                    if (this.compoundDrawables[drawableLeft] != null) {
                        if (event.rawX >= this.left - this.compoundDrawables[drawableLeft].bounds.width()) {
                            listener()
                            return@OnTouchListener true
                        }
                    }
                }

                DrawablePosition.TOP -> {
                    if (this.compoundDrawables[drawableTop] != null) {
                        if (event.rawX >= this.top - this.compoundDrawables[drawableTop].bounds.width()) {
                            listener()
                            return@OnTouchListener true
                        }
                    }
                }

                DrawablePosition.RIGHT -> {
                    if (this.compoundDrawables[drawableRight] != null) {
                        if (event.rawX >= this.right - this.compoundDrawables[drawableRight].bounds.width() - 24.dpTopx()) {
                            listener()
                            return@OnTouchListener true
                        }
                    }
                }

                DrawablePosition.BOTTOM -> {
                    if (this.compoundDrawables[drawableBottom] != null) {
                        if (event.rawX >= this.bottom - this.compoundDrawables[drawableBottom].bounds.width()) {
                            listener()
                            return@OnTouchListener true
                        }
                    }
                }
            }

        }
        false
    })
}

private fun getTil(view: ViewParent, maxDepth: Int): TextInputLayout? {
    return when {
        view.parent is TextInputLayout -> view.parent as TextInputLayout
        maxDepth > 0 -> getTil(view.parent, maxDepth - 1)
        else -> null
    }
}

enum class DrawablePosition { LEFT, TOP, RIGHT, BOTTOM }

fun TextView.makeLinks(vararg links: Pair<String, View.OnClickListener>, stringDisable: String = "", linkColor: Int? = null, disableColor: Int? = null) {
    val spannableString = SpannableString(this.text)
    var startIndexOfLink = -1
    for (link in links) {
        val clickableSpan = object : ClickableSpan() {
            override fun updateDrawState(textPaint: TextPaint) {
                // use this to change the link color
                if (link.first.equals(stringDisable, true)) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        textPaint.color = context.getColor(disableColor ?: R.color.baseGreyLight)
                    }
                } else {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        textPaint.color = context.getColor(linkColor ?: R.color.baseBlue)
                    }
                }
                //textPaint.color = textPaint.linkColor
                // toggle below value to enable/disable
                // the underline shown below the clickable text
                //textPaint.isUnderlineText = true
            }

            override fun onClick(view: View) {
                Selection.setSelection((view as TextView).text as Spannable, 0)
                view.invalidate()
                link.second.onClick(view)
            }
        }
        startIndexOfLink = this.text.toString().indexOf(link.first, startIndexOfLink + 1)
        //if(startIndexOfLink == -1) continue // todo if you want to verify your texts contains links text
        spannableString.setSpan(
            clickableSpan, startIndexOfLink, startIndexOfLink + link.first.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    this.movementMethod = LinkMovementMethod.getInstance() // without LinkMovementMethod, link can not click
    this.setText(spannableString, TextView.BufferType.SPANNABLE)
}

fun textWatcherAfterChange(onAfterChange: (s: Editable?) -> Unit): TextWatcher = object : TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
    override fun afterTextChanged(s: Editable?) = onAfterChange(s)
}

fun EditText.setMaxLength(maxLength: Int) {
    filters = arrayOf<InputFilter>(InputFilter.LengthFilter(maxLength))
}
