package com.kanzankazu.kanzanwidget.compose.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.kanzankazu.kanzanwidget.compose.ui.AppTextStyle
import com.kanzankazu.kanzanwidget.compose.ui.dp4
import com.kanzankazu.kanzanwidget.compose.ui.dp8
import com.kanzankazu.kanzanwidget.compose.ui.dp16

// region ==================== KanzanBaseDropdownMenu ====================

/**
 * Data class untuk item dropdown menu.
 *
 * @param T tipe data yang dibawa item.
 * @param label teks yang ditampilkan.
 * @param data data yang dibawa item.
 * @param enabled item bisa diklik atau tidak.
 * @param leadingIcon icon di kiri item.
 * @param trailingIcon icon di kanan item.
 * @param showDividerAfter tampilkan divider setelah item ini.
 * @param tint warna teks item (null = default).
 */
data class KanzanDropdownItem<T>(
    val label: String,
    val data: T,
    val enabled: Boolean = true,
    val leadingIcon: @Composable (() -> Unit)? = null,
    val trailingIcon: @Composable (() -> Unit)? = null,
    val showDividerAfter: Boolean = false,
    val tint: Color? = null,
)

/**
 * Generic dropdown menu yang bisa dipakai di mana saja.
 * Wraps Material3 [DropdownMenu] dengan pattern yang konsisten.
 *
 * @param T tipe data item.
 * @param expanded state expanded/collapsed.
 * @param onDismiss callback saat menu ditutup.
 * @param items daftar item menu.
 * @param onItemSelected callback saat item dipilih.
 * @param modifier Modifier untuk DropdownMenu.
 * @param offset posisi offset menu.
 * @param minWidth lebar minimum menu.
 * @param containerColor warna background menu.
 * @param textStyle style teks item.
 * @param header composable header di atas list item.
 * @param footer composable footer di bawah list item.
 * @param itemContent custom composable per item (override default).
 */
@Composable
fun <T> KanzanBaseDropdownMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    items: List<KanzanDropdownItem<T>>,
    onItemSelected: (KanzanDropdownItem<T>) -> Unit,
    modifier: Modifier = Modifier,
    offset: DpOffset = DpOffset(0.dp, 0.dp),
    minWidth: Dp = 180.dp,
    containerColor: Color = Color.White,
    textStyle: TextStyle = AppTextStyle.nunito_regular_14,
    header: @Composable (() -> Unit)? = null,
    footer: @Composable (() -> Unit)? = null,
    itemContent: @Composable ((KanzanDropdownItem<T>) -> Unit)? = null,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        modifier = modifier.widthIn(min = minWidth).background(containerColor),
        offset = offset,
    ) {
        header?.invoke()

        items.forEach { item ->
            if (itemContent != null) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    itemContent(item)
                }
            } else {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = item.label,
                            style = textStyle,
                            color = item.tint ?: Color.Black
                        )
                    },
                    onClick = {
                        onItemSelected(item)
                        onDismiss()
                    },
                    enabled = item.enabled,
                    leadingIcon = item.leadingIcon,
                    trailingIcon = item.trailingIcon,
                )
            }
            if (item.showDividerAfter) {
                Divider(color = Color.LightGray.copy(alpha = 0.5f))
            }
        }

        footer?.invoke()
    }
}

/**
 * Convenience wrapper: anchor + dropdown dalam satu komponen.
 * Klik anchor untuk toggle menu.
 *
 * @param T tipe data item.
 * @param items daftar item menu.
 * @param onItemSelected callback saat item dipilih.
 * @param anchor composable yang jadi trigger (tombol, icon, dll).
 * @param modifier Modifier.
 * @param menuModifier Modifier untuk DropdownMenu.
 * @param offset posisi offset menu.
 * @param minWidth lebar minimum menu.
 * @param containerColor warna background menu.
 * @param textStyle style teks item.
 */
@Composable
fun <T> KanzanDropdownMenuBox(
    items: List<KanzanDropdownItem<T>>,
    onItemSelected: (KanzanDropdownItem<T>) -> Unit,
    anchor: @Composable (expanded: Boolean, toggle: () -> Unit) -> Unit,
    modifier: Modifier = Modifier,
    menuModifier: Modifier = Modifier,
    offset: DpOffset = DpOffset(0.dp, 0.dp),
    minWidth: Dp = 180.dp,
    containerColor: Color = Color.White,
    textStyle: TextStyle = AppTextStyle.nunito_regular_14,
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        anchor(expanded) { expanded = !expanded }

        KanzanBaseDropdownMenu(
            expanded = expanded,
            onDismiss = { expanded = false },
            items = items,
            onItemSelected = onItemSelected,
            modifier = menuModifier,
            offset = offset,
            minWidth = minWidth,
            containerColor = containerColor,
            textStyle = textStyle,
        )
    }
}
// endregion

// region ==================== Preview ====================

@Preview(showBackground = true, name = "Dropdown 1. Basic menu items")
@Composable
private fun PreviewDropdownBasic() {
    Column(modifier = Modifier.padding(dp16)) {
        val items = listOf(
            KanzanDropdownItem("Edit", "edit", leadingIcon = { Text("✏️") }),
            KanzanDropdownItem("Bagikan", "share", leadingIcon = { Text("📤") }),
            KanzanDropdownItem("Hapus", "delete", leadingIcon = { Text("🗑️") }, tint = Color.Red, showDividerAfter = true),
        )
        Text(text = "Menu items (preview statis):", style = AppTextStyle.nunito_medium_14)
        items.forEach { item ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = dp4),
                verticalAlignment = Alignment.CenterVertically
            ) {
                item.leadingIcon?.invoke()
                Text(
                    text = item.label,
                    style = AppTextStyle.nunito_regular_14,
                    color = item.tint ?: Color.Black,
                    modifier = Modifier.padding(start = dp8)
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Dropdown 2. With header")
@Composable
private fun PreviewDropdownHeader() {
    Column(modifier = Modifier.padding(dp16)) {
        Text(text = "Akun Saya", style = AppTextStyle.nunito_medium_14, color = Color.Gray)
        Divider(modifier = Modifier.padding(vertical = dp4))
        listOf("Profil", "Pengaturan", "Keluar").forEach {
            Text(text = it, style = AppTextStyle.nunito_regular_14, modifier = Modifier.padding(vertical = dp4))
        }
    }
}

// endregion
