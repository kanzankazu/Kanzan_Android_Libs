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
        private var previousCleanString: String? = null
        private var initialEditText: String? = null
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            initialEditText = s.toString()
        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(editable: Editable) {
            if (editable.toString().startsWith("Rp ") && !editable.toString().contains("Rp 0") && !editable.toString().contains("Rp .")) {
                val str = editable.toString().replace("\\.".toRegex(), ",")
                if (str.length < prefix.length) {
                    editText.setText(prefix)
                    editText.setSelection(prefix.length)
                    return
                }
                if (str == prefix) {
                    editText.setSelection(prefix.length)
                    return
                }

                // cleanString this the string which not contain prefix and ,
                val cleanString = str.replace(prefix, "").replace(",".toRegex(), "")
                // for prevent afterTextChanged recursive call
                if (cleanString == previousCleanString || cleanString.isEmpty()) return
                previousCleanString = cleanString
                var formattedString: String = if (cleanString.contains(".")) {
                    formatDecimal(cleanString)
                } else {
                    formatInteger(cleanString)
                }
                formattedString = formattedString.replace(",".toRegex(), ".")
                editText.removeTextChangedListener(this) // Remove listener
                editText.setText(formattedString)
                onTextChanges.invoke(formattedString)
                handleSelection()
                editText.addTextChangedListener(this) // Add back the listener
            } else {
                editText.setText(initialEditText)
                onTextChanges.invoke(initialEditText ?: "")
            }
        }

        private fun formatInteger(str: String): String {
            val parsed = BigDecimal(str)
            val formatter = DecimalFormat("$prefix#,###", DecimalFormatSymbols(Locale.US))
            return formatter.format(parsed)
        }

        private fun formatDecimal(str: String): String {
            if (str == ".") {
                return "$prefix."
            }
            val parsed = BigDecimal(str)
            // example pattern VND #,###.00
            val formatter = DecimalFormat(
                prefix + "#,###." + getDecimalPattern(str),
                DecimalFormatSymbols(Locale.US)
            )
            formatter.roundingMode = RoundingMode.DOWN
            return formatter.format(parsed)
        }

        /**
         * It will return suitable pattern for format decimal
         * For example: 10.2 -> return 0 | 10.23 -> return 00, | 10.235 -> return 000
         */
        private fun getDecimalPattern(str: String): String {
            val decimalCount = str.length - str.indexOf(".") - 1
            val decimalPattern = StringBuilder()
            var i = 0
            while (i < decimalCount && i < MAX_DECIMAL) {
                decimalPattern.append("0")
                i++
            }
            return decimalPattern.toString()
        }

        private fun handleSelection() {
            if (editText.text.length <= MAX_LENGTH) {
                editText.setSelection(editText.text.length)
            } else {
                editText.setSelection(MAX_LENGTH)
            }
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
