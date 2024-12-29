package com.kanzankazu.kanzanwidget

import android.content.Context
import android.graphics.Rect
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.widget.EditText
import com.google.android.material.textfield.TextInputEditText
import com.kanzankazu.R
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

/**
 * Some note <br></br>
 *  * Always use locale US instead of default to make DecimalFormat work well in all language
 */
class EditTextRupiah @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = R.attr.editTextStyle) : TextInputEditText(context!!, attrs, defStyleAttr) {
    private val currencyTextWatcher = CurrencyTextWatcher(this, prefix)

    var onTextChanges: (String) -> Unit = {}

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
        if (focused) {
            addTextChangedListener(currencyTextWatcher)
        } else {
            removeTextChangedListener(currencyTextWatcher)
        }
        handleCaseCurrencyEmpty(focused)
    }

    /**
     * When currency empty <br></br>
     * + When focus EditText, set the default text = prefix (ex: VND) <br></br>
     * + When EditText lose focus, set the default text = "", EditText will display hint (ex:VND)
     */
    private fun handleCaseCurrencyEmpty(focused: Boolean) {
        if (focused) {
            if (text.toString().isEmpty()) {
                setText(prefix)
            }
        } else {
            if (text.toString() == prefix) {
                setText("")
            }
        }
    }

    inner class CurrencyTextWatcher(private val editText: EditText, private val prefix: String) : TextWatcher {
        private var isEditing: Boolean = false
        private var previousCleanString: String? = null

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(editable: Editable) {
            // Mencegah loop rekursif
            if (isEditing) return

            val input = editable.toString()

            // Pastikan input dimulai dengan prefix
            if (input.startsWith(prefix)) {
                val cleanString = input.replace(prefix, "").replace(".", "").replace(",", "")

                // Jika string kosong, reset ke prefix
                if (cleanString.isEmpty()) {
                    isEditing = true
                    editText.setText(prefix)
                    editText.setSelection(prefix.length)
                    isEditing = false
                    return
                }

                // Hindari proses jika string sama dengan sebelumnya
                if (cleanString == previousCleanString) return
                previousCleanString = cleanString

                isEditing = true
                try {
                    // Format string
                    val formattedString = formatCurrency(cleanString)
                    editText.setText(formattedString)
                    editText.setSelection(formattedString.length)
                    onTextChanges.invoke(formattedString)
                } finally {
                    isEditing = false
                }
            } else {
                // Reset ke state sebelumnya jika tidak dimulai dengan prefix
                isEditing = true
                try {
                    editText.setText("$prefix${previousCleanString ?: ""}")
                    editText.setSelection(editText.text.length)
                } finally {
                    isEditing = false
                }
            }
        }

        private fun formatCurrency(cleanString: String): String {
            val parsed = try {
                BigDecimal(cleanString)
            } catch (e: NumberFormatException) {
                BigDecimal.ZERO
            }
            val formatter = DecimalFormat("$prefix#,###", DecimalFormatSymbols(Locale.US))
            return formatter.format(parsed)
        }
    }

    companion object {
        private const val MAX_LENGTH = 14
        private const val MAX_DECIMAL = 0
        var prefix = "Rp "
        var validationNominal = prefix + "x"
        var validationLimit = validationNominal.length
        var lenghtNominalDigits = validationNominal.split(" ".toRegex()).toTypedArray()[1].length.toString()
    }

    init {
        this.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        this.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(MAX_LENGTH))
    }
}
