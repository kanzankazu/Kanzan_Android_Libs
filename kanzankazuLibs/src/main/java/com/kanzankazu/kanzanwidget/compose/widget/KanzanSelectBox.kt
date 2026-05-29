package com.kanzankazu.kanzanwidget.compose.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxColors
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonColors
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kanzankazu.kanzanwidget.compose.ui.AppTextStyle
import com.kanzankazu.kanzanwidget.compose.ui.dp16
import com.kanzankazu.kanzanwidget.compose.ui.dp4
import com.kanzankazu.kanzanwidget.compose.ui.dp8

// region ==================== Enums ====================

enum class KanzanSelectMode { SINGLE, MULTI }

// endregion

// region ==================== KanzanSelectBoxColors ====================

/**
 * Data class untuk mengatur warna KanzanSelectBox (Checkbox & RadioButton) secara granular.
 * Gunakan [Color.Unspecified] untuk menggunakan default dari MaterialTheme.
 *
 * Contoh penggunaan:
 * ```
 * KanzanSelectBox(
 *     kanzanColors = KanzanSelectBoxColors(
 *         checkedColor = Color.Blue,
 *         uncheckedColor = Color.Gray,
 *     )
 * )
 * ```
 *
 * Atau pakai preset:
 * ```
 * KanzanSelectBox(kanzanColors = KanzanSelectBoxColors.success())
 * KanzanSelectBox(kanzanColors = KanzanSelectBoxColors.error())
 * ```
 */
data class KanzanSelectBoxColors(
    // Checkbox / RadioButton — checked state
    val checkedColor: Color = Color.Unspecified,
    val uncheckedColor: Color = Color.Unspecified,
    val disabledCheckedColor: Color = Color.Unspecified,
    val disabledUncheckedColor: Color = Color.Unspecified,

    // Checkbox checkmark
    val checkmarkColor: Color = Color.Unspecified,

    // Text
    val textColor: Color = Color.Unspecified,
    val disabledTextColor: Color = Color.Unspecified,
    val selectAllTextColor: Color = Color.Unspecified,

    // Item background
    val selectedItemBackgroundColor: Color = Color.Unspecified,
    val unselectedItemBackgroundColor: Color = Color.Unspecified,

    // Divider
    val dividerColor: Color = Color.Unspecified,
) {
    companion object {
        /** Default — pakai warna bawaan MaterialTheme */
        fun defaults() = KanzanSelectBoxColors()

        /** Preset success — hijau */
        fun success(
            checkedColor: Color = Color(0xFF4CAF50),
            selectedItemBackgroundColor: Color = Color(0xFFE8F5E9),
        ) = KanzanSelectBoxColors(
            checkedColor = checkedColor,
            selectedItemBackgroundColor = selectedItemBackgroundColor,
        )

        /** Preset error — merah */
        fun error(
            checkedColor: Color = Color.Red,
            uncheckedColor: Color = Color.Red.copy(alpha = 0.6f),
        ) = KanzanSelectBoxColors(
            checkedColor = checkedColor,
            uncheckedColor = uncheckedColor,
        )

        /** Preset primary — biru */
        fun primary(
            checkedColor: Color = Color(0xFF1976D2),
            selectedItemBackgroundColor: Color = Color(0xFFE3F2FD),
        ) = KanzanSelectBoxColors(
            checkedColor = checkedColor,
            selectedItemBackgroundColor = selectedItemBackgroundColor,
        )

        /** Preset dark mode manual */
        fun dark(
            checkedColor: Color = Color(0xFF90CAF9),
            uncheckedColor: Color = Color(0xFF757575),
            checkmarkColor: Color = Color.Black,
            textColor: Color = Color.White,
            selectedItemBackgroundColor: Color = Color(0xFF37474F),
            dividerColor: Color = Color(0xFF424242),
        ) = KanzanSelectBoxColors(
            checkedColor = checkedColor,
            uncheckedColor = uncheckedColor,
            checkmarkColor = checkmarkColor,
            textColor = textColor,
            selectedItemBackgroundColor = selectedItemBackgroundColor,
            dividerColor = dividerColor,
        )
    }

    /** Convert ke Material3 [CheckboxColors] */
    @Composable
    fun toCheckboxColors(): CheckboxColors {
        val defaults = CheckboxDefaults.colors()
        return if (checkedColor == Color.Unspecified && uncheckedColor == Color.Unspecified
            && disabledCheckedColor == Color.Unspecified && disabledUncheckedColor == Color.Unspecified
            && checkmarkColor == Color.Unspecified
        ) {
            defaults
        } else {
            CheckboxDefaults.colors(
                checkedColor = if (checkedColor != Color.Unspecified) checkedColor else Color.Unspecified,
                uncheckedColor = if (uncheckedColor != Color.Unspecified) uncheckedColor else Color.Unspecified,
                disabledCheckedColor = if (disabledCheckedColor != Color.Unspecified) disabledCheckedColor else Color.Unspecified,
                disabledUncheckedColor = if (disabledUncheckedColor != Color.Unspecified) disabledUncheckedColor else Color.Unspecified,
                checkmarkColor = if (checkmarkColor != Color.Unspecified) checkmarkColor else Color.White,
            )
        }
    }

    /** Convert ke Material3 [RadioButtonColors] */
    @Composable
    fun toRadioButtonColors(): RadioButtonColors {
        val defaults = RadioButtonDefaults.colors()
        return if (checkedColor == Color.Unspecified && uncheckedColor == Color.Unspecified
            && disabledCheckedColor == Color.Unspecified && disabledUncheckedColor == Color.Unspecified
        ) {
            defaults
        } else {
            RadioButtonDefaults.colors(
                selectedColor = if (checkedColor != Color.Unspecified) checkedColor else Color.Unspecified,
                unselectedColor = if (uncheckedColor != Color.Unspecified) uncheckedColor else Color.Unspecified,
                disabledSelectedColor = if (disabledCheckedColor != Color.Unspecified) disabledCheckedColor else Color.Unspecified,
                disabledUnselectedColor = if (disabledUncheckedColor != Color.Unspecified) disabledUncheckedColor else Color.Unspecified,
            )
        }
    }
}

/** Helper: return this color if specified, otherwise fallback */
private fun Color.takeOrDefault(default: Color): Color =
    if (this != Color.Unspecified) this else default

// endregion

// region ==================== KanzanSwitchColors ====================

/**
 * Data class untuk mengatur warna Switch secara granular.
 * Gunakan [Color.Unspecified] untuk menggunakan default dari MaterialTheme.
 *
 * Contoh penggunaan:
 * ```
 * Switch(
 *     checked = isOn,
 *     onCheckedChange = { isOn = it },
 *     colors = KanzanSwitchColors(
 *         checkedTrackColor = Color.Green,
 *         checkedThumbColor = Color.White,
 *     ).toSwitchColors()
 * )
 * ```
 */
data class KanzanSwitchColors(
    // Checked state
    val checkedThumbColor: Color = Color.Unspecified,
    val checkedTrackColor: Color = Color.Unspecified,
    val checkedBorderColor: Color = Color.Unspecified,
    val checkedIconColor: Color = Color.Unspecified,

    // Unchecked state
    val uncheckedThumbColor: Color = Color.Unspecified,
    val uncheckedTrackColor: Color = Color.Unspecified,
    val uncheckedBorderColor: Color = Color.Unspecified,
    val uncheckedIconColor: Color = Color.Unspecified,

    // Disabled checked
    val disabledCheckedThumbColor: Color = Color.Unspecified,
    val disabledCheckedTrackColor: Color = Color.Unspecified,
    val disabledCheckedBorderColor: Color = Color.Unspecified,
    val disabledCheckedIconColor: Color = Color.Unspecified,

    // Disabled unchecked
    val disabledUncheckedThumbColor: Color = Color.Unspecified,
    val disabledUncheckedTrackColor: Color = Color.Unspecified,
    val disabledUncheckedBorderColor: Color = Color.Unspecified,
    val disabledUncheckedIconColor: Color = Color.Unspecified,
) {
    companion object {
        /** Default — pakai MaterialTheme */
        fun defaults() = KanzanSwitchColors()

        /** Preset success — hijau saat ON */
        fun success(
            checkedTrackColor: Color = Color(0xFF4CAF50),
            checkedThumbColor: Color = Color.White,
        ) = KanzanSwitchColors(
            checkedTrackColor = checkedTrackColor,
            checkedThumbColor = checkedThumbColor,
        )

        /** Preset error — merah saat ON */
        fun error(
            checkedTrackColor: Color = Color.Red,
            checkedThumbColor: Color = Color.White,
        ) = KanzanSwitchColors(
            checkedTrackColor = checkedTrackColor,
            checkedThumbColor = checkedThumbColor,
        )

        /** Preset primary — biru saat ON */
        fun primary(
            checkedTrackColor: Color = Color(0xFF1976D2),
            checkedThumbColor: Color = Color.White,
        ) = KanzanSwitchColors(
            checkedTrackColor = checkedTrackColor,
            checkedThumbColor = checkedThumbColor,
        )

        /** Preset dark mode */
        fun dark(
            checkedTrackColor: Color = Color(0xFF90CAF9),
            checkedThumbColor: Color = Color.White,
            uncheckedTrackColor: Color = Color(0xFF424242),
            uncheckedThumbColor: Color = Color(0xFF757575),
            uncheckedBorderColor: Color = Color(0xFF757575),
        ) = KanzanSwitchColors(
            checkedTrackColor = checkedTrackColor,
            checkedThumbColor = checkedThumbColor,
            uncheckedTrackColor = uncheckedTrackColor,
            uncheckedThumbColor = uncheckedThumbColor,
            uncheckedBorderColor = uncheckedBorderColor,
        )
    }

    /** Convert ke Material3 [SwitchColors] */
    @Composable
    fun toSwitchColors(): SwitchColors {
        val defaults = SwitchDefaults.colors()
        return if (this == defaults()) {
            defaults
        } else {
            SwitchDefaults.colors(
                checkedThumbColor = if (checkedThumbColor != Color.Unspecified) checkedThumbColor else Color.Unspecified,
                checkedTrackColor = if (checkedTrackColor != Color.Unspecified) checkedTrackColor else Color.Unspecified,
                checkedBorderColor = if (checkedBorderColor != Color.Unspecified) checkedBorderColor else Color.Unspecified,
                checkedIconColor = if (checkedIconColor != Color.Unspecified) checkedIconColor else Color.Unspecified,
                uncheckedThumbColor = if (uncheckedThumbColor != Color.Unspecified) uncheckedThumbColor else Color.Unspecified,
                uncheckedTrackColor = if (uncheckedTrackColor != Color.Unspecified) uncheckedTrackColor else Color.Unspecified,
                uncheckedBorderColor = if (uncheckedBorderColor != Color.Unspecified) uncheckedBorderColor else Color.Unspecified,
                uncheckedIconColor = if (uncheckedIconColor != Color.Unspecified) uncheckedIconColor else Color.Unspecified,
                disabledCheckedThumbColor = if (disabledCheckedThumbColor != Color.Unspecified) disabledCheckedThumbColor else Color.Unspecified,
                disabledCheckedTrackColor = if (disabledCheckedTrackColor != Color.Unspecified) disabledCheckedTrackColor else Color.Unspecified,
                disabledCheckedBorderColor = if (disabledCheckedBorderColor != Color.Unspecified) disabledCheckedBorderColor else Color.Unspecified,
                disabledCheckedIconColor = if (disabledCheckedIconColor != Color.Unspecified) disabledCheckedIconColor else Color.Unspecified,
                disabledUncheckedThumbColor = if (disabledUncheckedThumbColor != Color.Unspecified) disabledUncheckedThumbColor else Color.Unspecified,
                disabledUncheckedTrackColor = if (disabledUncheckedTrackColor != Color.Unspecified) disabledUncheckedTrackColor else Color.Unspecified,
                disabledUncheckedBorderColor = if (disabledUncheckedBorderColor != Color.Unspecified) disabledUncheckedBorderColor else Color.Unspecified,
                disabledUncheckedIconColor = if (disabledUncheckedIconColor != Color.Unspecified) disabledUncheckedIconColor else Color.Unspecified,
            )
        }
    }
}

// endregion

// region ==================== KanzanSelectBox ====================

/**
 * Select box dengan checkbox/radio list, support select all.
 *
 * @param items daftar label item.
 * @param selectedIndices set index yang terpilih.
 * @param onSelectionChanged callback saat seleksi berubah.
 * @param modifier Modifier.
 * @param mode SINGLE (radio) atau MULTI (checkbox).
 * @param showSelectAll tampilkan opsi "Select All" (hanya MULTI).
 * @param selectAllLabel label untuk select all.
 * @param showDivider tampilkan divider antar item.
 * @param itemTextStyle style teks item.
 * @param selectedColor warna background item terpilih.
 * @param maxHeight tinggi maksimum (scrollable jika melebihi).
 * @param enabled aktif/nonaktif.
 * @param itemContent custom composable per item (opsional, override default).
 */
@Composable
fun KanzanSelectBox(
    items: List<String>,
    selectedIndices: Set<Int>,
    onSelectionChanged: (Set<Int>) -> Unit,
    modifier: Modifier = Modifier,
    mode: KanzanSelectMode = KanzanSelectMode.MULTI,
    showSelectAll: Boolean = mode == KanzanSelectMode.MULTI,
    selectAllLabel: String = "Select All",
    showDivider: Boolean = false,
    itemTextStyle: TextStyle = AppTextStyle.nunito_regular_14,
    selectedColor: Color = Color.Transparent,
    maxHeight: Dp? = null,
    enabled: Boolean = true,
    radioColors: RadioButtonColors? = null,
    checkBoxColors: CheckboxColors? = null,
    kanzanColors: KanzanSelectBoxColors = KanzanSelectBoxColors.defaults(),
    itemContent: @Composable ((index: Int, label: String, isSelected: Boolean) -> Unit)? = null,
) {
    val allSelected = selectedIndices.size == items.size
    val someSelected = selectedIndices.isNotEmpty() && !allSelected

    // Resolve colors from kanzanColors
    val finalCheckboxColors = checkBoxColors ?: kanzanColors.toCheckboxColors()
    val finalRadioColors = radioColors ?: kanzanColors.toRadioButtonColors()
    val finalTextColor = kanzanColors.textColor.takeOrDefault(Color.Black)
    val finalDisabledTextColor = kanzanColors.disabledTextColor.takeOrDefault(Color.Gray)
    val finalSelectAllTextColor = kanzanColors.selectAllTextColor.takeOrDefault(Color.Black)
    val finalSelectedBgColor = kanzanColors.selectedItemBackgroundColor.takeOrDefault(selectedColor)
    val finalDividerColor = kanzanColors.dividerColor.takeOrDefault(Color.LightGray.copy(alpha = 0.3f))

    val scrollModifier = if (maxHeight != null) {
        modifier
            .height(maxHeight)
            .verticalScroll(rememberScrollState())
    } else modifier

    Column(modifier = scrollModifier.fillMaxWidth()) {
        // Select All
        if (showSelectAll && mode == KanzanSelectMode.MULTI && items.size > 1) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = enabled) {
                        if (allSelected) onSelectionChanged(emptySet())
                        else onSelectionChanged(items.indices.toSet())
                    }
                    .padding(vertical = dp4),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    checked = allSelected,
                    onCheckedChange = {
                        if (allSelected) onSelectionChanged(emptySet())
                        else onSelectionChanged(items.indices.toSet())
                    },
                    enabled = enabled,
                    colors = finalCheckboxColors,
                )
                KanzanSpacerHorizontal(width = dp8)
                Text(
                    text = selectAllLabel,
                    style = AppTextStyle.nunito_medium_14,
                    color = if (enabled) finalSelectAllTextColor else finalDisabledTextColor,
                )
            }
            Divider(color = finalDividerColor)
        }

        // Items
        items.forEachIndexed { index, label ->
            val isSelected = index in selectedIndices
            val bgColor = if (isSelected) finalSelectedBgColor else Color.Transparent

            if (itemContent != null) {
                itemContent(index, label, isSelected)
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(bgColor)
                        .clickable(enabled = enabled) {
                            when (mode) {
                                KanzanSelectMode.SINGLE -> onSelectionChanged(setOf(index))
                                KanzanSelectMode.MULTI -> {
                                    val newSet = selectedIndices.toMutableSet()
                                    if (isSelected) newSet.remove(index) else newSet.add(index)
                                    onSelectionChanged(newSet)
                                }
                            }
                        }
                        .padding(vertical = dp4),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    when (mode) {
                        KanzanSelectMode.SINGLE -> RadioButton(
                            selected = isSelected,
                            onClick = { onSelectionChanged(setOf(index)) },
                            enabled = enabled,
                            colors = finalRadioColors
                        )

                        KanzanSelectMode.MULTI -> Checkbox(
                            checked = isSelected,
                            onCheckedChange = {
                                val newSet = selectedIndices.toMutableSet()
                                if (isSelected) newSet.remove(index) else newSet.add(index)
                                onSelectionChanged(newSet)
                            },
                            enabled = enabled,
                            colors = finalCheckboxColors
                        )
                    }
                    KanzanSpacerHorizontal(width = dp8)
                    Text(
                        text = label,
                        style = itemTextStyle,
                        color = if (enabled) finalTextColor else finalDisabledTextColor,
                    )
                }
            }

            if (showDivider && index < items.lastIndex) {
                Divider(color = finalDividerColor)
            }
        }
    }
}

// endregion


// region ==================== Preview ====================

@Preview(showBackground = true, name = "SelectBox 1. Multi with SelectAll")
@Composable
private fun PreviewMultiSelectAll() {
    var selected by remember { mutableStateOf(setOf(0, 1)) }
    KanzanSelectBox(
        items = listOf("Euro", "U.S Dollar", "C.A Dollar", "Yen"),
        selectedIndices = selected,
        onSelectionChanged = { selected = it },
        modifier = Modifier.padding(dp16),
    )
}

@Preview(showBackground = true, name = "SelectBox 2. Single select")
@Composable
private fun PreviewSingleSelect() {
    var selected by remember { mutableStateOf(setOf(1)) }
    KanzanSelectBox(
        items = listOf("Harian", "Bulanan", "Tahunan"),
        selectedIndices = selected,
        onSelectionChanged = { selected = it },
        modifier = Modifier.padding(dp16),
        mode = KanzanSelectMode.SINGLE,
    )
}

@Preview(showBackground = true, name = "SelectBox 3. With divider")
@Composable
private fun PreviewWithDivider() {
    var selected by remember { mutableStateOf(setOf<Int>()) }
    KanzanSelectBox(
        items = listOf("Hutang", "Piutang", "Cicilan", "Tabungan", "Investasi"),
        selectedIndices = selected,
        onSelectionChanged = { selected = it },
        modifier = Modifier.padding(dp16),
        showDivider = true,
    )
}

@Preview(showBackground = true, name = "SelectBox 4. Selected highlight")
@Composable
private fun PreviewSelectedHighlight() {
    var selected by remember { mutableStateOf(setOf(0, 2)) }
    KanzanSelectBox(
        items = listOf("BCA", "Mandiri", "BRI", "BNI"),
        selectedIndices = selected,
        onSelectionChanged = { selected = it },
        modifier = Modifier.padding(dp16),
        selectedColor = Color(0xFFE3F2FD),
    )
}

@Preview(showBackground = true, name = "SelectBox 5. Disabled")
@Composable
private fun PreviewDisabled() {
    KanzanSelectBox(
        items = listOf("A", "B", "C"),
        selectedIndices = setOf(1),
        onSelectionChanged = {},
        modifier = Modifier.padding(dp16),
        enabled = false,
    )
}

@Preview(showBackground = true, name = "SelectBox 6. Max height scrollable")
@Composable
private fun PreviewScrollable() {
    var selected by remember { mutableStateOf(setOf<Int>()) }
    KanzanSelectBox(
        items = (1..20).map { "Item $it" },
        selectedIndices = selected,
        onSelectionChanged = { selected = it },
        modifier = Modifier.padding(dp16),
        maxHeight = 200.dp,
        showDivider = true,
    )
}

// endregion

// region ==================== Preview: Custom Colors ====================

@Preview(showBackground = true, name = "SelectBox 7. Custom Colors (Blue)")
@Composable
private fun PreviewCustomColors() {
    var selected by remember { mutableStateOf(setOf(0, 2)) }
    KanzanSelectBox(
        items = listOf("BCA", "Mandiri", "BRI", "BNI"),
        selectedIndices = selected,
        onSelectionChanged = { selected = it },
        modifier = Modifier.padding(dp16),
        kanzanColors = KanzanSelectBoxColors.primary(),
    )
}

@Preview(showBackground = true, name = "SelectBox 8. Preset Success (Radio)")
@Composable
private fun PreviewSuccessRadio() {
    var selected by remember { mutableStateOf(setOf(1)) }
    KanzanSelectBox(
        items = listOf("Harian", "Bulanan", "Tahunan"),
        selectedIndices = selected,
        onSelectionChanged = { selected = it },
        modifier = Modifier.padding(dp16),
        mode = KanzanSelectMode.SINGLE,
        kanzanColors = KanzanSelectBoxColors.success(),
    )
}

@Preview(showBackground = true, name = "SelectBox 9. Preset Error")
@Composable
private fun PreviewErrorColors() {
    var selected by remember { mutableStateOf(setOf(0)) }
    KanzanSelectBox(
        items = listOf("Hutang", "Piutang", "Cicilan"),
        selectedIndices = selected,
        onSelectionChanged = { selected = it },
        modifier = Modifier.padding(dp16),
        kanzanColors = KanzanSelectBoxColors.error(),
    )
}

@Preview(showBackground = true, name = "SelectBox 10. Preset Dark")
@Composable
private fun PreviewDarkColors() {
    var selected by remember { mutableStateOf(setOf(1, 3)) }
    KanzanSelectBox(
        items = listOf("Jakarta", "Surabaya", "Bandung", "Medan"),
        selectedIndices = selected,
        onSelectionChanged = { selected = it },
        modifier = Modifier.padding(dp16),
        showDivider = true,
        kanzanColors = KanzanSelectBoxColors.dark(),
    )
}

// endregion

// region ==================== Preview: Switch Colors ====================

@Preview(showBackground = true, name = "Switch 1. Default")
@Composable
private fun PreviewSwitchDefault() {
    var checked by remember { mutableStateOf(true) }
    Switch(
        checked = checked,
        onCheckedChange = { checked = it },
    )
}

@Preview(showBackground = true, name = "Switch 2. Success (Green)")
@Composable
private fun PreviewSwitchSuccess() {
    var checked by remember { mutableStateOf(true) }
    Switch(
        checked = checked,
        onCheckedChange = { checked = it },
        colors = KanzanSwitchColors.success().toSwitchColors()
    )
}

@Preview(showBackground = true, name = "Switch 3. Primary (Blue)")
@Composable
private fun PreviewSwitchPrimary() {
    var checked by remember { mutableStateOf(true) }
    Switch(
        checked = checked,
        onCheckedChange = { checked = it },
        colors = KanzanSwitchColors.primary().toSwitchColors()
    )
}

@Preview(showBackground = true, name = "Switch 4. Custom")
@Composable
private fun PreviewSwitchCustom() {
    var checked by remember { mutableStateOf(false) }
    Switch(
        checked = checked,
        onCheckedChange = { checked = it },
        colors = KanzanSwitchColors(
            checkedTrackColor = Color(0xFFFF9800),
            checkedThumbColor = Color.White,
            uncheckedTrackColor = Color(0xFFE0E0E0),
            uncheckedThumbColor = Color.Gray,
        ).toSwitchColors()
    )
}

// endregion
