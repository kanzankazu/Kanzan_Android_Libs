package com.kanzankazu.kanzanwidget.compose.widget

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Surface
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kanzankazu.kanzanwidget.compose.ui.AppTextStyle
import com.kanzankazu.kanzanwidget.compose.ui.dp4
import com.kanzankazu.kanzanwidget.compose.ui.dp8
import com.kanzankazu.kanzanwidget.compose.ui.dp12
import com.kanzankazu.kanzanwidget.compose.ui.dp16
import com.kanzankazu.kanzanwidget.compose.ui.dp48
import kotlinx.coroutines.delay

// region ==================== KanzanSearchBar ====================

/**
 * Search bar composable dengan debounce support.
 *
 * @param query teks pencarian saat ini.
 * @param onQueryChanged callback saat teks berubah.
 * @param modifier Modifier.
 * @param placeholder placeholder text.
 * @param leadingIcon icon di kiri (default: 🔍).
 * @param trailingIcon icon di kanan (default: clear button saat ada teks).
 * @param onSearch callback saat user tekan search di keyboard.
 * @param enabled aktif/nonaktif.
 * @param autoFocus otomatis focus saat muncul.
 * @param debounceMs debounce delay dalam milidetik (0 = tanpa debounce).
 * @param onDebouncedQuery callback setelah debounce selesai.
 * @param textStyle style teks input.
 * @param backgroundColor warna background.
 * @param cornerRadius radius sudut.
 * @param height tinggi search bar.
 */
@Composable
fun KanzanSearchBar(
    query: String,
    onQueryChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Cari...",
    leadingIcon: @Composable (() -> Unit)? = {
        Text(text = "🔍", style = AppTextStyle.nunito_regular_14)
    },
    trailingIcon: @Composable (() -> Unit)? = null,
    onSearch: ((String) -> Unit)? = null,
    enabled: Boolean = true,
    autoFocus: Boolean = false,
    debounceMs: Long = 0L,
    onDebouncedQuery: ((String) -> Unit)? = null,
    textStyle: TextStyle = AppTextStyle.nunito_regular_14,
    backgroundColor: Color = Color(0xFFF5F5F5),
    cornerRadius: Dp = dp12,
    height: Dp = dp48,
) {
    val focusRequester = remember { FocusRequester() }

    // Auto focus
    if (autoFocus) {
        LaunchedEffect(Unit) { focusRequester.requestFocus() }
    }

    // Debounce
    if (debounceMs > 0 && onDebouncedQuery != null) {
        LaunchedEffect(query) {
            delay(debounceMs)
            onDebouncedQuery(query)
        }
    }

    Surface(
        modifier = modifier.fillMaxWidth().height(height),
        shape = RoundedCornerShape(cornerRadius),
        color = backgroundColor,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = dp12),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            leadingIcon?.invoke()
            if (leadingIcon != null) KanzanSpacerHorizontal(width = dp8)

            Box(modifier = Modifier.weight(1f)) {
                if (query.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = textStyle,
                        color = Color.Gray,
                    )
                }
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChanged,
                    modifier = Modifier.fillMaxWidth().focusRequester(focusRequester),
                    enabled = enabled,
                    textStyle = textStyle.copy(color = Color.Black),
                    singleLine = true,
                    cursorBrush = SolidColor(Color.Black),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { onSearch?.invoke(query) }),
                )
            }

            // Clear / trailing icon
            AnimatedVisibility(visible = query.isNotEmpty(), enter = fadeIn(), exit = fadeOut()) {
                if (trailingIcon != null) {
                    trailingIcon()
                } else {
                    Text(
                        text = "✕",
                        style = AppTextStyle.nunito_medium_14,
                        color = Color.Gray,
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable { onQueryChanged("") }
                            .padding(dp4),
                    )
                }
            }
        }
    }
}

// endregion

// region ==================== Preview ====================

@Preview(showBackground = true, name = "SearchBar 1. Empty")
@Composable
private fun PreviewSearchBarEmpty() {
    var query by remember { mutableStateOf("") }
    KanzanSearchBar(
        query = query,
        onQueryChanged = { query = it },
        modifier = Modifier.padding(dp16),
    )
}

@Preview(showBackground = true, name = "SearchBar 2. With text")
@Composable
private fun PreviewSearchBarWithText() {
    KanzanSearchBar(
        query = "Hutang rumah",
        onQueryChanged = {},
        modifier = Modifier.padding(dp16),
    )
}

@Preview(showBackground = true, name = "SearchBar 3. Custom placeholder")
@Composable
private fun PreviewSearchBarCustom() {
    KanzanSearchBar(
        query = "",
        onQueryChanged = {},
        modifier = Modifier.padding(dp16),
        placeholder = "Cari transaksi...",
        backgroundColor = Color(0xFFE3F2FD),
    )
}

// endregion
