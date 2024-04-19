package com.kanzankazu.kanzanwidget;

import android.content.Context;
import android.graphics.Rect;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

import com.kanzankazu.R;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/**
 * Some note <br/>
 * <li>Always use locale US instead of default to make DecimalFormat work well in all language</li>
 */
public class EditTextDistance extends androidx.appcompat.widget.AppCompatEditText {
    private static final int MAX_LENGTH = 8;
    private static final String prefix = "";
    private final DistanceTextWatcher distanceTextWatcher = new DistanceTextWatcher(this, prefix);

    public EditTextDistance(Context context) {
        this(context, null);
    }

    public EditTextDistance(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.editTextStyle);
    }

    public EditTextDistance(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        this.setFilters(new InputFilter[]{new InputFilter.LengthFilter(MAX_LENGTH)});
        this.setLongClickable(false);
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (focused) {
            this.addTextChangedListener(distanceTextWatcher);
        } else {
            this.removeTextChangedListener(distanceTextWatcher);
        }
        handleCaseDistanceEmpty(focused);
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        CharSequence text = getText();
        if (text != null) {
            if (selStart != text.length() || selEnd != text.length()) {
                setSelection(text.length(), text.length());
                return;
            }
        }

        super.onSelectionChanged(selStart, selEnd);
    }

    /**
     * When currency empty <br/>
     * + When focus EditText, set the default text = prefix (ex: VND) <br/>
     * + When EditText lose focus, set the default text = "", EditText will display hint (ex:VND)
     */
    private void handleCaseDistanceEmpty(boolean focused) {
        if (focused) {
            if (getText().toString().isEmpty()) {
                setText(prefix);
            }
        } else {
            if (getText().toString().equals(prefix)) {
                setText("");
            }
        }
    }

    private static class DistanceTextWatcher implements TextWatcher {
        private final EditText editText;
        private final String prefix;
        private String previousCleanString;
        private String textBeforeComma;
        private String lastEditText;
        private String formattedString;

        DistanceTextWatcher(EditText editText, String prefix) {
            this.editText = editText;
            this.prefix = prefix;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if (editText.getText().toString().length() == 6) {
                //simpan string sblm ditambah comma supaya waktu delete dr terakhir langsung kedelete commanya dan set ke string ini lagi
                textBeforeComma = s.toString();
            }
            lastEditText = s.toString();
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (editable.toString().startsWith("0")) {
                //prevent input 0 diawal
                editText.setText("");
            } else if (editable.toString().endsWith(".")) {
                //prevent input 1. , 2.
                editText.setText(lastEditText);
                editText.setSelection(editText.getText().toString().length());
            } else {
                // cleanString this the string which not contain prefix and ,
                String cleanString = editable.toString().replace(prefix, "").replaceAll("[.]", "");
                // for prevent afterTextChanged recursive call
                if ((cleanString.equals(previousCleanString)) || cleanString.isEmpty()) {
                    return;
                }
                previousCleanString = cleanString;

                if (!editable.toString().endsWith(",") && !cleanString.contains(".")) {
                    //jangan panggil formatInteger kalau char terakhir koma supaya lgsg diganti ke textBeforeComma
                    formattedString = formatInteger(cleanString);
                }

                if (editable.length() == 7 && editable.toString().endsWith(",")) {
                    //kalau delete char terakhir dan char terakhir menjadi koma
                    //ex:12.345,6 didelete jadi 12.345,
                    formattedString = textBeforeComma;
                } else if (editable.length() == 7) {
                    //tambahin comma di sblm char terakhir
                    //cth:12.3456 -> 12.345,6
                    formattedString = formattedString.replaceAll(",", ".");
                    formattedString = formattedString.substring(0, 2) + "." + formattedString.charAt(2) + formattedString.substring(4, 6) + "," + formattedString.substring(6);
                }

                if (editable.length() < 7) {
                    formattedString = formattedString.replaceAll(",", ".");
                }

                editText.removeTextChangedListener(this); // Remove listener
                editText.setText(formattedString);
                handleSelection();
                editText.addTextChangedListener(this); // Add back the listener
            }
        }

        private String formatInteger(String str) {
            BigDecimal parsed = new BigDecimal(str);
            DecimalFormatSymbols dfs = DecimalFormatSymbols.getInstance();
            dfs.setGroupingSeparator('.');
            DecimalFormat formatter =
                    new DecimalFormat(prefix + "#,###", dfs);
            return formatter.format(parsed);
        }

        private void handleSelection() {
            if (editText.getText().length() <= MAX_LENGTH) {
                editText.setSelection(editText.getText().length());
            } else {
                editText.setSelection(MAX_LENGTH);
            }
        }
    }
}