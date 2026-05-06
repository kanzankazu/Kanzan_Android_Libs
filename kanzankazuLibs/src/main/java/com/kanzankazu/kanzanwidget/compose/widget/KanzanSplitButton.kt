package com.kanzankazu.kanzanwidget.compose.widget

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kanzankazu.kanzanwidget.compose.ui.AppTextStyle
import com.kanzankazu.kanzanwidget.compose.ui.dp0
import com.kanzankazu.kanzanwidget.compose.ui.dp4
import com.kanzankazu.kanzanwidget.compose.ui.dp8
import com.kanzankazu.kanzanwidget.compose.ui.dp16
import com.kanzankazu.kanzanwidget.compose.ui.dp48

// region ==================== Enums ====================

enum class KanzanSplitButtonVariant { PRIMARY, SECONDARY }

// endregion

// region ==================== KanzanSplitButton ====================

/**
 * Split button — primary action + dropdown menu.
 *
 * @param title label tombol utama.
 * @param onPrimaryClick callback klik tombol utama.
 * @param dropdownItems daftar label menu dropdown.
 * @param onDropdownItemClick callback saat item dropdown dipilih.
 * @param modifier Modifier.
 * @param variant PRIMARY (filled) atau SECONDARY (outlined).
 * @param leadingIcon icon di kiri label.
 * @param dropdownIcon icon dropdown (default: ▾).
 * @param containerColor warna background (PRIMARY).
 * @param contentColor warna konten.
 * @param borderColor warna border (SECONDARY).
 * @param shape bentuk button.
 * @param textStyle style teks.
 * @param enabled aktif/nonaktif.
 */
@Composable
fun KanzanSplitButton(
    title: String,
    onPrimaryClick: () -> Unit,
    dropdownItems: List<String>,
    onDropdownItemClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    variant: KanzanSplitButtonVariant = KanzanSplitButtonVariant.PRIMARY,
    leadingIcon: @Composable (() -> Unit)? = null,
    dropdownIcon: String = "▾",
    containerColor: Color = Color.Black,
    contentColor: Color = Color.White,
    borderColor: Color = Color.Black,
    shape: Shape = RoundedCornerShape(dp8),
    textStyle: TextStyle = AppTextStyle.nunito_medium_14,
    enabled: Boolean = true,
) {
    var expanded by remember { mutableStateOf(false) }

    val leftShape = RoundedCornerShape(topStart = dp8, bottomStart = dp8, topEnd = dp0, bottomEnd = dp0)
    val rightShape = RoundedCornerShape(topStart = dp0, bottomStart = dp0, topEnd = dp8, bottomEnd = dp8)

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        when (variant) {
            KanzanSplitButtonVariant.PRIMARY -> {
                // Primary action
                Button(
                    onClick = onPrimaryClick,
                    modifier = Modifier.height(dp48),
                    enabled = enabled,
                    shape = leftShape,
                    colors = ButtonDefaults.buttonColors(containerColor = containerColor, contentColor = contentColor),
                    contentPadding = PaddingValues(horizontal = dp16, vertical = dp8),
                ) {
                    leadingIcon?.invoke()
                    if (leadingIcon != null) KanzanSpacerHorizontal(width = dp8)
                    Text(text = title, style = textStyle)
                }
                // Dropdown trigger
                Button(
                    onClick = { expanded = true },
                    modifier = Modifier.height(dp48),
                    enabled = enabled,
                    shape = rightShape,
                    colors = ButtonDefaults.buttonColors(containerColor = containerColor, contentColor = contentColor),
                    contentPadding = PaddingValues(horizontal = dp8),
                ) {
                    Text(text = dropdownIcon, style = textStyle)
                }
            }
            KanzanSplitButtonVariant.SECONDARY -> {
                OutlinedButton(
                    onClick = onPrimaryClick,
                    modifier = Modifier.height(dp48),
                    enabled = enabled,
                    shape = leftShape,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = borderColor),
                    border = BorderStroke(1.dp, borderColor),
                    contentPadding = PaddingValues(horizontal = dp16, vertical = dp8),
                ) {
                    leadingIcon?.invoke()
                    if (leadingIcon != null) KanzanSpacerHorizontal(width = dp8)
                    Text(text = title, style = textStyle)
                }
                OutlinedButton(
                    onClick = { expanded = true },
                    modifier = Modifier.height(dp48),
                    enabled = enabled,
                    shape = rightShape,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = borderColor),
                    border = BorderStroke(1.dp, borderColor),
                    contentPadding = PaddingValues(horizontal = dp8),
                ) {
                    Text(text = dropdownIcon, style = textStyle)
                }
            }
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            dropdownItems.forEachIndexed { index, item ->
                DropdownMenuItem(
                    text = { Text(text = item, style = AppTextStyle.nunito_regular_14) },
                    onClick = {
                        expanded = false
                        onDropdownItemClick(index)
                    },
                )
            }
        }
    }
}

// endregion

// region ==================== Preview ====================

@Preview(showBackground = true, name = "SplitButton 1. Primary")
@Composable
private fun PreviewSplitPrimary() {
    KanzanSplitButton(
        title = "Button",
        onPrimaryClick = {},
        dropdownItems = listOf("Option A", "Option B", "Option C"),
        onDropdownItemClick = {},
        modifier = Modifier.padding(dp16),
    )
}

@Preview(showBackground = true, name = "SplitButton 2. Secondary")
@Composable
private fun PreviewSplitSecondary() {
    KanzanSplitButton(
        title = "Button",
        onPrimaryClick = {},
        dropdownItems = listOf("Edit", "Hapus"),
        onDropdownItemClick = {},
        modifier = Modifier.padding(dp16),
        variant = KanzanSplitButtonVariant.SECONDARY,
    )
}

@Preview(showBackground = true, name = "SplitButton 3. With icon")
@Composable
private fun PreviewSplitIcon() {
    KanzanSplitButton(
        title = "Simpan",
        onPrimaryClick = {},
        dropdownItems = listOf("Simpan & Tutup", "Simpan & Baru"),
        onDropdownItemClick = {},
        modifier = Modifier.padding(dp16),
        leadingIcon = { Text(text = "💾", style = AppTextStyle.nunito_regular_14) },
    )
}

// endregion
