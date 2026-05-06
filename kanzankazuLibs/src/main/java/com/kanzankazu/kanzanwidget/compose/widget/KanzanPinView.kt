package com.kanzankazu.kanzanwidget.compose.widget

import android.view.KeyEvent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import com.kanzankazu.kanzanwidget.compose.ui.AppTextStyle
import com.kanzankazu.kanzanwidget.compose.ui.PrimaryDarkItungItungan
import com.kanzankazu.kanzanwidget.compose.ui.dp1
import com.kanzankazu.kanzanwidget.compose.ui.dp2
import com.kanzankazu.kanzanwidget.compose.ui.dp4
import com.kanzankazu.kanzanwidget.compose.ui.dp8
import com.kanzankazu.kanzanwidget.compose.ui.dp12
import com.kanzankazu.kanzanwidget.compose.ui.dp16
import com.kanzankazu.kanzanwidget.compose.ui.dp48

// region ==================== Enums ====================

enum class KanzanPinStyle {
    /** Kotak dengan border, digit terlihat */
    BOX,
    /** Kotak dengan border, digit tersembunyi (dot) */
    BOX_MASKED,
    /** Garis bawah saja */
    UNDERLINE,
    /** Lingkaran filled/empty */
    DOT,
}

// endregion

// region ==================== KanzanPinView ====================

/**
 * Komponen PIN input generic OVER POWER.
 * Support: jumlah kotak parameterize, chunk separator, berbagai style,
 * auto-focus, auto-submit, error state, masked/visible toggle, custom shape.
 *
 * @param pinLength jumlah digit PIN total.
 * @param value nilai PIN saat ini.
 * @param onValueChanged callback saat PIN berubah.
 * @param modifier Modifier.
 * @param style style tampilan (BOX, BOX_MASKED, UNDERLINE, DOT).
 * @param chunkSize jumlah digit per chunk (0 = tanpa chunk).
 * @param chunkSeparator composable separator antar chunk.
 * @param cellSize ukuran per cell.
 * @param cellSpacing jarak antar cell.
 * @param cellShape bentuk cell.
 * @param borderColor warna border cell.
 * @param focusedBorderColor warna border cell saat fokus.
 * @param filledBorderColor warna border cell saat terisi.
 * @param errorBorderColor warna border cell saat error.
 * @param backgroundColor warna background cell.
 * @param focusedBackgroundColor warna background cell saat fokus.
 * @param textStyle style teks digit.
 * @param textColor warna teks digit.
 * @param cursorColor warna cursor.
 * @param isError state error.
 * @param errorMessage pesan error.
 * @param errorTextStyle style teks error.
 * @param enabled aktif/nonaktif.
 * @param autoFocus auto-focus ke cell pertama saat muncul.
 * @param onComplete callback saat semua digit terisi.
 * @param label label di atas PIN.
 * @param labelStyle style teks label.
 * @param maskChar karakter mask (default: ●).
 * @param placeholderChar karakter placeholder saat kosong.
 * @param dotFilledColor warna dot saat terisi (DOT style).
 * @param dotEmptyColor warna dot saat kosong (DOT style).
 */
@Composable
fun KanzanPinView(
    pinLength: Int,
    value: String,
    onValueChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    style: KanzanPinStyle = KanzanPinStyle.BOX,
    chunkSize: Int = 0,
    chunkSeparator: @Composable (() -> Unit)? = null,
    cellSize: Dp = dp48,
    cellSpacing: Dp = dp8,
    cellShape: Shape = RoundedCornerShape(dp8),
    borderColor: Color = Color.LightGray,
    focusedBorderColor: Color = Color.Black,
    filledBorderColor: Color = Color.DarkGray,
    errorBorderColor: Color = Color.Red,
    backgroundColor: Color = Color.White,
    focusedBackgroundColor: Color = Color(0xFFF5F5F5),
    textStyle: TextStyle = AppTextStyle.nunito_bold_24,
    textColor: Color = Color.Black,
    cursorColor: Color = Color.Black,
    isError: Boolean = false,
    errorMessage: String? = null,
    errorTextStyle: TextStyle = AppTextStyle.nunito_regular_12,
    enabled: Boolean = true,
    autoFocus: Boolean = true,
    onComplete: ((String) -> Unit)? = null,
    label: String? = null,
    labelStyle: TextStyle = AppTextStyle.nunito_medium_14,
    maskChar: Char = '●',
    placeholderChar: Char = '-',
    dotFilledColor: Color = Color.Black,
    dotEmptyColor: Color = Color.LightGray,
) {
    val focusRequester = remember { FocusRequester() }

    // Auto focus
    if (autoFocus) {
        LaunchedEffect(Unit) { focusRequester.requestFocus() }
    }

    // Auto complete
    LaunchedEffect(value) {
        if (value.length == pinLength) onComplete?.invoke(value)
    }

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        // Label
        if (label != null) {
            Text(text = label, style = labelStyle, modifier = Modifier.padding(bottom = dp8))
        }

        // Hidden text field that captures input
        Box {
            BasicTextField(
                value = TextFieldValue(text = value, selection = TextRange(value.length)),
                onValueChange = { newValue ->
                    val filtered = newValue.text.filter { it.isDigit() }.take(pinLength)
                    if (filtered != value) onValueChanged(filtered)
                },
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .size(dp1) // Invisible but focusable
                    .background(Color.Transparent),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                singleLine = true,
                cursorBrush = SolidColor(Color.Transparent),
                enabled = enabled,
            )

            // Visual cells
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                for (i in 0 until pinLength) {
                    // Chunk separator
                    if (chunkSize > 0 && i > 0 && i % chunkSize == 0) {
                        chunkSeparator?.invoke() ?: DefaultChunkSeparator()
                    } else if (i > 0) {
                        KanzanSpacerHorizontal(width = cellSpacing)
                    }

                    val char = value.getOrNull(i)
                    val isFocused = value.length == i
                    val isFilled = char != null

                    val currentBorderColor = when {
                        isError -> errorBorderColor
                        isFocused -> focusedBorderColor
                        isFilled -> filledBorderColor
                        else -> borderColor
                    }
                    val currentBgColor = if (isFocused) focusedBackgroundColor else backgroundColor

                    when (style) {
                        KanzanPinStyle.BOX -> PinCellBox(
                            char = char?.toString(),
                            cellSize = cellSize, shape = cellShape,
                            borderColor = currentBorderColor, bgColor = currentBgColor,
                            textStyle = textStyle, textColor = textColor,
                            placeholderChar = placeholderChar, isFocused = isFocused,
                        )
                        KanzanPinStyle.BOX_MASKED -> PinCellBox(
                            char = if (isFilled) maskChar.toString() else null,
                            cellSize = cellSize, shape = cellShape,
                            borderColor = currentBorderColor, bgColor = currentBgColor,
                            textStyle = textStyle, textColor = textColor,
                            placeholderChar = placeholderChar, isFocused = isFocused,
                        )
                        KanzanPinStyle.UNDERLINE -> PinCellUnderline(
                            char = char?.toString(),
                            cellSize = cellSize,
                            lineColor = currentBorderColor,
                            textStyle = textStyle, textColor = textColor,
                            placeholderChar = placeholderChar, isFocused = isFocused,
                        )
                        KanzanPinStyle.DOT -> PinCellDot(
                            isFilled = isFilled,
                            cellSize = cellSize,
                            filledColor = dotFilledColor,
                            emptyColor = dotEmptyColor,
                            isFocused = isFocused,
                            focusedColor = focusedBorderColor,
                        )
                    }
                }
            }
        }

        // Error message
        AnimatedVisibility(visible = isError && errorMessage != null, enter = fadeIn(), exit = fadeOut()) {
            Text(
                text = errorMessage ?: "",
                style = errorTextStyle,
                color = Color.Red,
                modifier = Modifier.padding(top = dp8)
            )
        }
    }
}

@Composable
private fun DefaultChunkSeparator() {
    Text(
        text = " — ",
        style = AppTextStyle.nunito_regular_16,
        color = Color.Gray,
        modifier = Modifier.padding(horizontal = dp4)
    )
}

@Composable
private fun PinCellBox(
    char: String?,
    cellSize: Dp,
    shape: Shape,
    borderColor: Color,
    bgColor: Color,
    textStyle: TextStyle,
    textColor: Color,
    placeholderChar: Char,
    isFocused: Boolean,
) {
    val scale by animateFloatAsState(if (isFocused) 1.05f else 1f, tween(150), label = "cellScale")
    Box(
        modifier = Modifier
            .size(cellSize)
            .scale(scale)
            .clip(shape)
            .background(bgColor, shape)
            .border(if (isFocused) dp2 else dp1, borderColor, shape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = char ?: placeholderChar.toString(),
            style = textStyle,
            color = if (char != null) textColor else Color.LightGray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun PinCellUnderline(
    char: String?,
    cellSize: Dp,
    lineColor: Color,
    textStyle: TextStyle,
    textColor: Color,
    placeholderChar: Char,
    isFocused: Boolean,
) {
    val lineHeight = if (isFocused) dp2 else dp1
    Column(
        modifier = Modifier.size(cellSize),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = char ?: placeholderChar.toString(),
            style = textStyle,
            color = if (char != null) textColor else Color.LightGray,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f).padding(top = dp4)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(lineHeight)
                .background(lineColor)
        )
    }
}

@Composable
private fun PinCellDot(
    isFilled: Boolean,
    cellSize: Dp,
    filledColor: Color,
    emptyColor: Color,
    isFocused: Boolean,
    focusedColor: Color,
) {
    val dotSize = cellSize * 0.4f
    val scale by animateFloatAsState(if (isFilled) 1f else 0.7f, tween(150), label = "dotScale")
    val color = when {
        isFilled -> filledColor
        isFocused -> focusedColor.copy(alpha = 0.5f)
        else -> emptyColor
    }
    Box(
        modifier = Modifier.size(cellSize),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(dotSize)
                .scale(scale)
                .clip(CircleShape)
                .background(color)
                .then(if (isFocused && !isFilled) Modifier.border(dp1, focusedColor, CircleShape) else Modifier)
        )
    }
}
// endregion

// region ==================== Preview ====================

@Preview(showBackground = true, name = "Pin 1. BOX style (4 digit)")
@Composable
private fun PreviewPinBox4() {
    var pin by remember { mutableStateOf("1212") }
    KanzanPinView(
        pinLength = 4,
        value = pin,
        onValueChanged = { pin = it },
        label = "Masukkan PIN",
        autoFocus = false,
        modifier = Modifier.padding(dp16)
    )
}

@Preview(showBackground = true, name = "Pin 2. BOX style (6 digit)")
@Composable
private fun PreviewPinBox6() {
    var pin by remember { mutableStateOf("123") }
    KanzanPinView(
        pinLength = 6,
        value = pin,
        onValueChanged = { pin = it },
        label = "Kode OTP",
        autoFocus = false,
        modifier = Modifier.padding(dp16)
    )
}

@Preview(showBackground = true, name = "Pin 3. BOX_MASKED")
@Composable
private fun PreviewPinMasked() {
    var pin by remember { mutableStateOf("1234") }
    KanzanPinView(
        pinLength = 6,
        value = pin,
        onValueChanged = { pin = it },
        style = KanzanPinStyle.BOX_MASKED,
        label = "PIN Rahasia",
        autoFocus = false,
        modifier = Modifier.padding(dp16)
    )
}

@Preview(showBackground = true, name = "Pin 4. UNDERLINE style")
@Composable
private fun PreviewPinUnderline() {
    var pin by remember { mutableStateOf("12") }
    KanzanPinView(
        pinLength = 4,
        value = pin,
        onValueChanged = { pin = it },
        style = KanzanPinStyle.UNDERLINE,
        label = "Verifikasi",
        autoFocus = false,
        modifier = Modifier.padding(dp16)
    )
}

@Preview(showBackground = true, name = "Pin 5. DOT style")
@Composable
private fun PreviewPinDot() {
    var pin by remember { mutableStateOf("123") }
    KanzanPinView(
        pinLength = 4,
        value = pin,
        onValueChanged = { pin = it },
        style = KanzanPinStyle.DOT,
        label = "PIN",
        autoFocus = false,
        modifier = Modifier.padding(dp16)
    )
}

@Preview(showBackground = true, name = "Pin 6. Chunked (3-3)")
@Composable
private fun PreviewPinChunked33() {
    var pin by remember { mutableStateOf("123") }
    KanzanPinView(
        pinLength = 6,
        value = pin,
        onValueChanged = { pin = it },
        chunkSize = 3,
        label = "Kode Verifikasi",
        autoFocus = false,
        modifier = Modifier.padding(dp16)
    )
}

@Preview(showBackground = true, name = "Pin 7. Chunked (4-4) with custom separator")
@Composable
private fun PreviewPinChunked44() {
    var pin by remember { mutableStateOf("12345") }
    KanzanPinView(
        pinLength = 8,
        value = pin,
        onValueChanged = { pin = it },
        chunkSize = 4,
        chunkSeparator = {
            Text(text = " • ", style = AppTextStyle.nunito_bold_20, color = PrimaryDarkItungItungan)
        },
        cellSize = 40.dp,
        label = "Kode Aktivasi",
        autoFocus = false,
        modifier = Modifier.padding(dp16)
    )
}

@Preview(showBackground = true, name = "Pin 8. Error state")
@Composable
private fun PreviewPinError() {
    var pin by remember { mutableStateOf("1234") }
    KanzanPinView(
        pinLength = 4,
        value = pin,
        onValueChanged = { pin = it },
        isError = true,
        errorMessage = "PIN salah. Coba lagi.",
        label = "PIN",
        autoFocus = false,
        modifier = Modifier.padding(dp16)
    )
}

@Preview(showBackground = true, name = "Pin 9. Completed")
@Composable
private fun PreviewPinCompleted() {
    var pin by remember { mutableStateOf("1234") }
    KanzanPinView(
        pinLength = 4,
        value = pin,
        onValueChanged = { pin = it },
        label = "PIN Lengkap",
        autoFocus = false,
        modifier = Modifier.padding(dp16)
    )
}

@Preview(showBackground = true, name = "Pin 10. Custom colors")
@Composable
private fun PreviewPinCustomColors() {
    var pin by remember { mutableStateOf("12") }
    KanzanPinView(
        pinLength = 4,
        value = pin,
        onValueChanged = { pin = it },
        focusedBorderColor = Color(0xFF4CAF50),
        filledBorderColor = Color(0xFF4CAF50),
        focusedBackgroundColor = Color(0xFFE8F5E9),
        label = "PIN Hijau",
        autoFocus = false,
        modifier = Modifier.padding(dp16)
    )
}

@Preview(showBackground = true, name = "Pin 11. DOT chunked")
@Composable
private fun PreviewPinDotChunked() {
    var pin by remember { mutableStateOf("123456") }
    KanzanPinView(
        pinLength = 8,
        value = pin,
        onValueChanged = { pin = it },
        style = KanzanPinStyle.DOT,
        chunkSize = 4,
        label = "Security Code",
        autoFocus = false,
        modifier = Modifier.padding(dp16)
    )
}

@Preview(showBackground = true, name = "Pin 12. Small cells")
@Composable
private fun PreviewPinSmall() {
    var pin by remember { mutableStateOf("12") }
    KanzanPinView(
        pinLength = 6,
        value = pin,
        onValueChanged = { pin = it },
        cellSize = 36.dp,
        cellSpacing = dp4,
        textStyle = AppTextStyle.nunito_bold_16,
        label = "Compact PIN",
        autoFocus = false,
        modifier = Modifier.padding(dp16)
    )
}

// endregion
