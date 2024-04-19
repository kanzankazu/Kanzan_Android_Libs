package com.kanzankazu.kanzanwidget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.kanzankazu.R
import com.kanzankazu.kanzanutil.kanzanextension.view.onTextChange
import com.kanzankazu.kanzanutil.kanzanextension.view.string

class KanzanEditTextComponent @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    var value: String = ""
        get() = et?.string() ?: ""
        set(value) {
            field = value
            et?.setText(value)
        }
    var hint: String = ""
        set(value) {
            field = value
            til?.hint = value
        }


    private var til: TextInputLayout? = null
    private var et: TextInputEditText? = null

    init {
        attrs?.let { extractAttributes(it) }
        initView()
        set()
    }

    private fun initView() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.component_kanzan_edit_text, this).apply {
            til = findViewById(R.id.component_kanzan_edit_text_til)
            et = findViewById(R.id.component_kanzan_edit_text_et)
        }
    }

    private fun extractAttributes(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.KanzanEditTextComponent)
        value = typedArray.getString(R.styleable.KanzanEditTextComponent_ket_title) ?: value
        hint = typedArray.getString(R.styleable.KanzanEditTextComponent_ket_hint) ?: hint
        typedArray.recycle()
    }

    fun set(
        title: String = this.value,
        hint: String = this.hint,
    ) {
        this.value = title
        this.hint = hint
    }

    fun onTextChange(watcher: (String) -> Unit) = et?.onTextChange(watcher)

    fun et() = et
}
