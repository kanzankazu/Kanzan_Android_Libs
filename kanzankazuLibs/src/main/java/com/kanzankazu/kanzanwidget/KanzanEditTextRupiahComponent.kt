package com.kanzankazu.kanzanwidget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.google.android.material.textfield.TextInputLayout
import com.kanzankazu.R
import com.kanzankazu.kanzanutil.kanzanextension.type.getRupiah
import com.kanzankazu.kanzanutil.kanzanextension.type.setRupiah
import com.kanzankazu.kanzanutil.kanzanextension.view.string

class KanzanEditTextRupiahComponent @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    var value: String = ""
        get() = if (isWithRupiah) et?.string()?.getRupiah() ?: "Rp " else et?.string() ?: ""
        set(value) {
            field = value.setRupiah()
            et?.setText(field)
        }
    var hint: String = ""
        set(value) {
            field = value
            til?.hint = field
        }
    var isWithRupiah = true

    private var til: TextInputLayout? = null
    private var et: EditTextRupiah? = null

    init {
        attrs?.let { extractAttributes(it) }
        initView()
        set()
    }

    private fun initView() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.component_kanzan_edit_text_rupiah, this).apply {
            til = findViewById(R.id.component_kanzan_edit_rupiah_text_til)
            et = findViewById(R.id.component_kanzan_edit_rupiah_text_et)
        }
    }

    private fun extractAttributes(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.KanzanEditTextRupiahComponent)
        value = typedArray.getString(R.styleable.KanzanEditTextRupiahComponent_ketr_title) ?: value
        hint = typedArray.getString(R.styleable.KanzanEditTextRupiahComponent_ketr_hint) ?: hint
        typedArray.recycle()
    }

    fun set(
        title: String = this.value,
        hint: String = this.hint,
    ) {
        this.value = title
        this.hint = hint
    }

    fun onTextChange(watcher: (String) -> Unit) {
        et?.onTextChanges = { watcher.invoke(it) }
    }

    fun et() = et
}
