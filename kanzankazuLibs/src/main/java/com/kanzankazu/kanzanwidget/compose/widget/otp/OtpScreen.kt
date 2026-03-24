package com.kanzankazu.kanzanwidget.compose.widget.otp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kanzankazu.kanzanwidget.compose.ui.dp1
import com.kanzankazu.kanzanwidget.compose.ui.dp8
import com.kanzankazu.kanzanwidget.compose.ui.dp16

/**
 * Generic OTP Screen composable.
 * @param state OTP state dari ViewModel.
 * @param focusRequesters list FocusRequester, satu per digit.
 * @param onAction callback untuk OtpAction.
 * @param validText text yang ditampilkan saat OTP valid (null = tidak tampilkan).
 * @param invalidText text yang ditampilkan saat OTP invalid (null = tidak tampilkan).
 * @param validColor warna text valid.
 * @param invalidColor warna text invalid.
 * @param fieldBorderColor warna border per field.
 * @param fieldBackgroundColor warna background per field.
 * @param fieldTextColor warna text per field.
 * @param fieldFontSize ukuran font per field.
 * @param fieldBorderWidth lebar border per field.
 * @param fieldPlaceholderText placeholder text per field.
 * @param fieldSpacing jarak antar field.
 * @param validationContent custom composable untuk validasi, menggantikan default text.
 */
@Composable
fun OtpScreen(
    state: OtpState,
    focusRequesters: List<FocusRequester>,
    onAction: (OtpAction) -> Unit,
    modifier: Modifier = Modifier,
    validText: String = "OTP is valid!",
    invalidText: String = "OTP is invalid!",
    validColor: Color = Color.Green,
    invalidColor: Color = Color.Red,
    fieldBorderColor: Color = MaterialTheme.colorScheme.primary,
    fieldBackgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    fieldTextColor: Color = MaterialTheme.colorScheme.primary,
    fieldFontSize: TextUnit = 36.sp,
    fieldBorderWidth: Dp = dp1,
    fieldPlaceholderText: String = "-",
    fieldSpacing: Dp = dp8,
    validationContent: (@Composable (isValid: Boolean) -> Unit)? = null
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier.padding(dp16),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(fieldSpacing, Alignment.CenterHorizontally)
        ) {
            state.code.forEachIndexed { index, number ->
                OtpInputField(
                    number = number,
                    focusRequester = focusRequesters[index],
                    onFocusChanged = { isFocused ->
                        if (isFocused) {
                            onAction(OtpAction.OnChangeFieldFocused(index))
                        }
                    },
                    onNumberChanged = { newNumber ->
                        onAction(OtpAction.OnEnterNumber(newNumber, index))
                    },
                    onKeyboardBack = {
                        onAction(OtpAction.OnKeyboardBack)
                    },
                    borderColor = fieldBorderColor,
                    backgroundColor = fieldBackgroundColor,
                    textColor = fieldTextColor,
                    fontSize = fieldFontSize,
                    borderWidth = fieldBorderWidth,
                    placeholderText = fieldPlaceholderText,
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                )
            }
        }

        state.isValid?.let { isValid ->
            if (validationContent != null) {
                validationContent(isValid)
            } else {
                Text(
                    text = if (isValid) validText else invalidText,
                    color = if (isValid) validColor else invalidColor,
                    fontSize = 16.sp
                )
            }
        }
    }
}
