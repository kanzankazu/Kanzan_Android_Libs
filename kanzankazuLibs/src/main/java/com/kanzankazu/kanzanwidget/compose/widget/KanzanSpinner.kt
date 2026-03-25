package com.kanzankazu.kanzanwidget.compose.widget

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kanzankazu.kanzanwidget.compose.ui.AppTextStyle
import com.kanzankazu.kanzanwidget.compose.ui.Shapes
import com.kanzankazu.kanzanwidget.compose.ui.dp1
import com.kanzankazu.kanzanwidget.compose.ui.dp4
import com.kanzankazu.kanzanwidget.compose.ui.dp8
import com.kanzankazu.kanzanwidget.compose.ui.dp12
import com.kanzankazu.kanzanwidget.compose.ui.dp14
import com.kanzankazu.kanzanwidget.compose.ui.dp16

// region ==================== KanzanSpinner ====================

/**
 * Generic Spinner (dropdown selector) component mirip Android Spinner.
 * Menampilkan item terpilih, klik untuk expand list, pilih item untuk collapse.
 *
 * @param T tipe data item.
 * @param items daftar item.
 * @param selectedIndex index item terpilih (-1 jika belum ada).
 * @param onItemSelected callback saat item dipilih (index).
 * @param itemToString konversi item ke String untuk display.
 * @param modifier Modifier.
 * @param label label di atas spinner.
 * @param placeholder placeholder saat belum ada item terpilih.
 * @param enabled aktif/nonaktif.
 * @param searchable tampilkan search field di atas list.
 * @param searchPlaceholder placeholder search field.
 * @param maxVisibleItems jumlah item maksimal terlihat sebelum scroll.
 * @param selectedColor warna background item terpilih.
 * @param borderColor warna border spinner.
 * @param textStyle style teks item.
 * @param labelStyle style teks label.
 * @param leadingIcon icon di kiri.
 * @param trailingIcon icon di kanan (default: arrow).
 * @param errorMessage pesan error.
 * @param itemContent custom composable per item (override default text).
 */
@Composable
fun <T> KanzanSpinner(
    items: List<T>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    itemToString: (T) -> String = { it.toString() },
    modifier: Modifier = Modifier.fillMaxWidth(),
    label: String? = null,
    placeholder: String = "Pilih...",
    enabled: Boolean = true,
    searchable: Boolean = false,
    searchPlaceholder: String = "Cari...",
    maxVisibleItems: Int = 5,
    selectedColor: Color = Color(0xFFE3F2FD),
    borderColor: Color = Color.Gray,
    textStyle: TextStyle = AppTextStyle.nunito_regular_14,
    labelStyle: TextStyle = AppTextStyle.nunito_medium_14,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    errorMessage: String? = null,
    itemContent: @Composable ((index: Int, item: T, isSelected: Boolean) -> Unit)? = null,
) {
    var expanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val arrowRotation by animateFloatAsState(if (expanded) 180f else 0f, label = "arrow")

    val filteredItems = remember(items, searchQuery) {
        if (searchQuery.isBlank()) items.mapIndexed { i, item -> i to item }
        else items.mapIndexed { i, item -> i to item }
            .filter { itemToString(it.second).contains(searchQuery, ignoreCase = true) }
    }

    val itemHeightDp = 48.dp
    val maxHeight = itemHeightDp * maxVisibleItems

    Column(modifier = modifier) {
        // Label
        if (label != null) {
            Text(text = label, style = labelStyle, modifier = Modifier.padding(bottom = dp4))
        }

        // Anchor box — dropdown floats relative to this
        Box {
            // Selected item box
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(Shapes.medium)
                    .border(dp1, if (errorMessage != null) Color.Red else borderColor, Shapes.medium)
                    .clickable(enabled = enabled) { expanded = !expanded }
                    .padding(horizontal = dp12, vertical = dp14),
                verticalAlignment = Alignment.CenterVertically
            ) {
                leadingIcon?.invoke()
                Text(
                    text = if (selectedIndex in items.indices) itemToString(items[selectedIndex]) else placeholder,
                    style = textStyle,
                    color = if (selectedIndex in items.indices) Color.Black else Color.Gray,
                    modifier = Modifier.weight(1f).padding(horizontal = dp4)
                )
                Box(modifier = Modifier.rotate(arrowRotation)) {
                    trailingIcon?.invoke() ?: Text(text = "▼", style = AppTextStyle.nunito_regular_12, color = Color.Gray)
                }
            }

            // Floating dropdown menu
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                    searchQuery = ""
                },
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .heightIn(max = maxHeight)
                    .background(Color.White)
            ) {
                if (searchable) {
                    KanzanTextField(
                        label = "",
                        value = searchQuery,
                        onValueChanged = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth().padding(dp8),
                        placeholder = searchPlaceholder,
                        kanzanInputType = KanzanInputType.SEARCH,
                        singleLine = true,
                    )
                }

                filteredItems.forEach { (originalIndex, item) ->
                    val isSelected = originalIndex == selectedIndex
                    if (itemContent != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (isSelected) selectedColor else Color.Transparent)
                                .clickable {
                                    onItemSelected(originalIndex)
                                    expanded = false
                                    searchQuery = ""
                                }
                        ) {
                            itemContent(originalIndex, item, isSelected)
                        }
                    } else {
                        Text(
                            text = itemToString(item),
                            style = if (isSelected) AppTextStyle.nunito_medium_14 else textStyle,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (isSelected) selectedColor else Color.Transparent)
                                .clickable {
                                    onItemSelected(originalIndex)
                                    expanded = false
                                    searchQuery = ""
                                }
                                .padding(horizontal = dp12, vertical = dp14)
                        )
                    }
                    if (originalIndex < items.lastIndex) {
                        Divider(color = Color.LightGray.copy(alpha = 0.3f))
                    }
                }
            }
        }

        // Error
        if (errorMessage != null) {
            Text(text = errorMessage, style = AppTextStyle.nunito_regular_12, color = Color.Red, modifier = Modifier.padding(top = dp4))
        }
    }
}
// endregion

// region ==================== Preview ====================

@Preview(showBackground = true, name = "Spinner 1. Basic")
@Composable
private fun PreviewSpinnerBasic() {
    var selected by remember { mutableStateOf(1) }
    KanzanSpinner(
        items = listOf("Makanan", "Transportasi", "Hiburan", "Belanja"),
        selectedIndex = selected,
        onItemSelected = { selected = it },
        label = "Kategori"
    )
}

@Preview(showBackground = true, name = "Spinner 2. Placeholder")
@Composable
private fun PreviewSpinnerPlaceholder() {
    var selected by remember { mutableStateOf(-1) }
    KanzanSpinner(
        items = listOf("BCA", "Mandiri", "BRI", "BNI"),
        selectedIndex = selected,
        onItemSelected = { selected = it },
        label = "Pilih Bank",
        placeholder = "Belum dipilih..."
    )
}

@Preview(showBackground = true, name = "Spinner 3. Searchable")
@Composable
private fun PreviewSpinnerSearchable() {
    var selected by remember { mutableStateOf(0) }
    KanzanSpinner(
        items = listOf("Jakarta", "Surabaya", "Bandung", "Medan", "Semarang", "Makassar", "Palembang"),
        selectedIndex = selected,
        onItemSelected = { selected = it },
        label = "Kota",
        searchable = true,
        searchPlaceholder = "Cari kota..."
    )
}

@Preview(showBackground = true, name = "Spinner 4. Error")
@Composable
private fun PreviewSpinnerError() {
    var selected by remember { mutableStateOf(-1) }
    KanzanSpinner(
        items = listOf("Tunai", "Transfer", "E-Wallet"),
        selectedIndex = selected,
        onItemSelected = { selected = it },
        label = "Metode Pembayaran",
        errorMessage = "Wajib dipilih"
    )
}

@Preview(showBackground = true, name = "Spinner 5. Custom icon")
@Composable
private fun PreviewSpinnerCustomIcon() {
    var selected by remember { mutableStateOf(0) }
    KanzanSpinner(
        items = listOf("IDR", "USD", "EUR", "JPY"),
        selectedIndex = selected,
        onItemSelected = { selected = it },
        label = "Mata Uang",
        leadingIcon = { Text(text = "💱", style = AppTextStyle.nunito_regular_16, modifier = Modifier.padding(end = dp4)) }
    )
}

// endregion
