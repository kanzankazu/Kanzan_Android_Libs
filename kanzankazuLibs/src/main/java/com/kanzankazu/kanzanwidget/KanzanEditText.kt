@file:Suppress("MemberVisibilityCanBePrivate", "SuspiciousVarProperty")

package com.kanzankazu.kanzanwidget

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.InputFilter
import android.text.InputType
import android.text.Spanned
import android.util.AttributeSet
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import com.google.android.material.textfield.TextInputLayout
import com.kanzankazu.R
import com.kanzankazu.kanzanutil.kanzanextension.view.afterTextChanged
import com.kanzankazu.kanzanutil.kanzanextension.view.initCustomAttribute
import com.kanzankazu.kanzanutil.kanzanextension.view.initCustomView
import com.kanzankazu.kanzanutil.kanzanextension.view.visibleView

class KanzanEditText @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    var removeSpace = false

    var onTextChange: (String) -> Unit = {}
    var onTextClear: () -> Unit = {}

    var title = ""
        set(value) {
            field = value
            tvComponentKanzanEditText2Title.text = value
            tvComponentKanzanEditText2Title.visibleView((value.isNotEmpty()))
        }
    var text = ""
        set(value) {
            field = value
            etComponentKanzanEditText2.setText(value)
        }
        get() = etComponentKanzanEditText2.text.toString().let {
            if (isPriceKeyboard()) it.replace(".", "")
            else it
        }
    var hint = ""
        set(value) {
            field = value
            tilComponentKanzanEditText2.hint = value
        }
    var prefix = ""
        set(value) {
            field = value
            tvComponentKanzanEditText2Prefix.text = value
            tvComponentKanzanEditText2Prefix.visibleView((value.isNotEmpty()))
        }
    var loading: Boolean = false
        set(value) {
            field = value
            pbComponentKanzanEditText2.visibleView(value)
            checkLayoutSupport()
        }
    var imageRight: Drawable? = null
        set(value) {
            field = value
            value?.let {
                ivComponentKanzanEditText2Right.visibleView(true)
                ivComponentKanzanEditText2Right.setImageDrawable(it)
            } ?: kotlin.run {
                ivComponentKanzanEditText2Right.visibleView(false)
            }
            checkLayoutSupport()
        }
    var enable = true
        set(value) {
            field = value
            tilComponentKanzanEditText2.isEnabled = value
            etComponentKanzanEditText2.isEnabled = value
        }
    var success = Pair(false, "")
        set(value) {
            field = value
            ivComponentKanzanEditText2Success.visibleView(value.first)
            tvComponentKanzanEditText2Success.visibleView(value.first && value.second.isNotEmpty())
            tvComponentKanzanEditText2Success.text = value.second
            checkLayoutSupport()
        }
    var error: String = ""
        set(value) {
            field = value
            tvComponentKanzanEditText2Error.visibleView((value.isNotEmpty()))
            tvComponentKanzanEditText2Error.text = value
        }
    var maxLength = 0
        set(value) {
            field = value
            setFilter(
                value,
                isAllCaps,
                if (regex.isNotEmpty()) Regex(regex) else null,
                digits
            )
        }
    var isAllCaps = false
        set(value) {
            field = value
            setFilter(
                maxLength,
                value,
                if (regex.isNotEmpty()) Regex(regex) else null,
                digits
            )
        }
    var regex = ""
        set(value) {
            field = value
            setFilter(
                maxLength,
                isAllCaps,
                if (value.isNotEmpty()) Regex(value) else null,
                digits
            )
        }
    var inputType = 1
        set(value) {
            field = value
            value.let { type ->
                etComponentKanzanEditText2.inputType = when (type) {
                    1 -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
                    2, 3 -> InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
                    5 -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
                    6 -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
                    else -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
                }
                if (isAllCapKeyboard()) etComponentKanzanEditText2.filters = arrayOf(InputFilter.AllCaps())
                (type == 4).let { tilComponentKanzanEditText2.isEnabled = !it }
            }
        }
    var isClearButton = false
        set(value) {
            field = value
            ivComponentKanzanEditText2Clear.visibleView(value && text.isNotEmpty())
            ivComponentKanzanEditText2Clear.setOnClickListener { onTextClear.invoke() }
            checkLayoutSupport()
        }

    var digits = ""
        set(value) {
            field = value
            setFilter(
                maxLength,
                isAllCaps,
                if (regex.isNotEmpty()) Regex(regex) else null,
                value
            )
        }

    private fun isPriceKeyboard() = inputType == 2
    private fun isAllCapKeyboard() = inputType == 5

    lateinit  var tvComponentKanzanEditText2Title: TextView
    lateinit  var tvComponentKanzanEditText2Prefix: TextView
    lateinit  var tilComponentKanzanEditText2: TextInputLayout
    lateinit  var etComponentKanzanEditText2: EditText
    lateinit  var ivComponentKanzanEditText2Clear: ImageView
    lateinit  var ivComponentKanzanEditText2Right: ImageView
    lateinit  var ivComponentKanzanEditText2Success: ImageView
    lateinit  var pbComponentKanzanEditText2: ProgressBar
    lateinit  var tvComponentKanzanEditText2Error: TextView
    lateinit  var tvComponentKanzanEditText2Success: TextView
    lateinit  var layComponentKanzanEditText2Support: LinearLayout

    init {
        initView(context)
        initAttribute(context, attrs)
        set()
    }

    private fun initAttribute(context: Context, attrs: AttributeSet?) {
        attrs?.initCustomAttribute(context, R.styleable.KanzanEditText) {
            title = getString(R.styleable.KanzanEditText_title) ?: title
            text = getString(R.styleable.KanzanEditText_value) ?: text
            hint = getString(R.styleable.KanzanEditText_hint) ?: hint
            prefix = getString(R.styleable.KanzanEditText_prefix) ?: prefix
            enable = getBoolean(R.styleable.KanzanEditText_enable, enable)
            imageRight = getDrawable(R.styleable.KanzanEditText_imageRight)
            maxLength = getInt(R.styleable.KanzanEditText_maxLenght, maxLength)
            isAllCaps = getBoolean(R.styleable.KanzanEditText_isAllCaps, isAllCaps)
            inputType = getInt(R.styleable.KanzanEditText_input_types, 1)
            regex = getString(R.styleable.KanzanEditText_isAllCaps) ?: regex
            isClearButton = getBoolean(R.styleable.KanzanEditText_isShowClearButton, isClearButton)
            digits = getString(R.styleable.KanzanEditText_digits) ?: digits
        }
    }

    private fun initView(context: Context) {
        this.initCustomView(context, R.layout.component_kanzan_edit_text_2){
            tvComponentKanzanEditText2Title = findViewById(R.id.tv_component_kanzan_edit_text_2_title)
            tvComponentKanzanEditText2Prefix = findViewById(R.id.tv_component_kanzan_edit_text_2_prefix)
            tilComponentKanzanEditText2 = findViewById(R.id.til_component_kanzan_edit_text_2)
            etComponentKanzanEditText2 = findViewById(R.id.et_component_kanzan_edit_text_2)
            ivComponentKanzanEditText2Clear = findViewById(R.id.iv_component_kanzan_edit_text_2_clear)
            ivComponentKanzanEditText2Right = findViewById(R.id.iv_component_kanzan_edit_text_2_right)
            ivComponentKanzanEditText2Success = findViewById(R.id.iv_component_kanzan_edit_text_2_success)
            pbComponentKanzanEditText2 = findViewById(R.id.pb_component_kanzan_edit_text_2)
            tvComponentKanzanEditText2Error = findViewById(R.id.tv_component_kanzan_edit_text_2_error)
            tvComponentKanzanEditText2Success = findViewById(R.id.tv_component_kanzan_edit_text_2_success)
            layComponentKanzanEditText2Support = findViewById(R.id.lay_component_kanzan_edit_text_2_support)
        }
    }

    private fun set(
        mSuccess: Pair<Boolean, String> = success,
        mError: String = error,
        mLoading: Boolean = loading,
    ) {
        success = mSuccess
        error = mError
        loading = mLoading

        etComponentKanzanEditText2.afterTextChanged(parseToCurrency = isPriceKeyboard()) {
            if (it.contains(" ") && removeSpace) {
                val newText = it.replace(" ", "")
                etComponentKanzanEditText2.setText(newText)
                etComponentKanzanEditText2.setSelection(newText.length)
            }
            onTextChange.invoke(text)
        }
    }

    private fun checkLayoutSupport() {
        layComponentKanzanEditText2Support.visibleView(
            ivComponentKanzanEditText2Clear.isVisible,
            ivComponentKanzanEditText2Right.isVisible,
            ivComponentKanzanEditText2Success.isVisible,
            pbComponentKanzanEditText2.isVisible
        )
    }

    private fun setFilter(
        maxLength: Int = this.maxLength,
        isAllCaps: Boolean = this.isAllCaps,
        regexDigits: Regex? = null,
        digits: String,
    ) {
        val arrayOfInputFilters = arrayListOf<InputFilter>()
        if (isAllCaps) arrayOfInputFilters.add(InputFilter.AllCaps())
        if (maxLength > 0) arrayOfInputFilters.add(InputFilter.LengthFilter(maxLength))
        if (regexDigits != null) arrayOfInputFilters.add(InputFilter { src, _, _, _, _, _ ->
            if (src == "") return@InputFilter src
            if (src.toString().matches(regexDigits)) src else ""
        })
        if (digits.isNotEmpty()) arrayOfInputFilters.add(DigitInputFilter(digits))
        if (arrayOfInputFilters.isNotEmpty()) etComponentKanzanEditText2.filters =
            arrayOfInputFilters.toTypedArray()
    }

    private class DigitInputFilter(val digits: String) : InputFilter {
        override fun filter(
            source: CharSequence, start: Int, end: Int,
            dest: Spanned, dstart: Int, dend: Int,
        ): CharSequence {
            val filteredBuilder = StringBuilder()
            for (i in start until end) {
                val currentChar = source[i]
                if (digits.contains(currentChar.toString())) {
                    filteredBuilder.append(currentChar)
                }
            }
            return filteredBuilder.toString()
        }
    }

    fun et() = etComponentKanzanEditText2
}
