package com.kanzankazu.kanzanwidget.compose.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.RadioButton
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
import com.kanzankazu.kanzanwidget.compose.ui.dp4
import com.kanzankazu.kanzanwidget.compose.ui.dp8
import com.kanzankazu.kanzanwidget.compose.ui.dp12
import com.kanzankazu.kanzanwidget.compose.ui.dp16

// region ==================== Enums ====================

enum class KanzanSelectMode { SINGLE, MULTI }

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
    itemContent: @Composable ((index: Int, label: String, isSelected: Boolean) -> Unit)? = null,
) {
    val allSelected = selectedIndices.size == items.size
    val someSelected = selectedIndices.isNotEmpty() && !allSelected

    val scrollModifier = if (maxHeight != null) {
        modifier.height(maxHeight).verticalScroll(rememberScrollState())
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
                )
                Spacer(modifier = Modifier.width(dp8))
                Text(
                    text = selectAllLabel,
                    style = AppTextStyle.nunito_medium_14,
                    color = if (enabled) Color.Black else Color.Gray,
                )
            }
            Divider(color = Color.LightGray.copy(alpha = 0.5f))
        }

        // Items
        items.forEachIndexed { index, label ->
            val isSelected = index in selectedIndices
            val bgColor = if (isSelected) selectedColor else Color.Transparent

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
                        )
                        KanzanSelectMode.MULTI -> Checkbox(
                            checked = isSelected,
                            onCheckedChange = {
                                val newSet = selectedIndices.toMutableSet()
                                if (isSelected) newSet.remove(index) else newSet.add(index)
                                onSelectionChanged(newSet)
                            },
                            enabled = enabled,
                        )
                    }
                    Spacer(modifier = Modifier.width(dp8))
                    Text(
                        text = label,
                        style = itemTextStyle,
                        color = if (enabled) Color.Black else Color.Gray,
                    )
                }
            }

            if (showDivider && index < items.lastIndex) {
                Divider(color = Color.LightGray.copy(alpha = 0.3f))
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
