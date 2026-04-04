@file:OptIn(ExperimentalMaterial3Api::class)

package com.kanzankazu.kanzanwidget.compose.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kanzankazu.kanzanwidget.compose.ui.AppTextStyle
import com.kanzankazu.kanzanwidget.compose.ui.CustomShapes
import com.kanzankazu.kanzanwidget.compose.ui.PrimaryDarkItungItungan
import com.kanzankazu.kanzanwidget.compose.ui.dp0
import com.kanzankazu.kanzanwidget.compose.ui.dp2
import com.kanzankazu.kanzanwidget.compose.ui.dp4
import com.kanzankazu.kanzanwidget.compose.ui.dp8
import com.kanzankazu.kanzanwidget.compose.ui.dp12
import com.kanzankazu.kanzanwidget.compose.ui.dp14
import com.kanzankazu.kanzanwidget.compose.ui.dp16
import com.kanzankazu.kanzanwidget.compose.ui.dp40
import com.kanzankazu.kanzanwidget.compose.ui.dp100
import kotlinx.coroutines.launch

// region ==================== Sealed Class: KanzanSheetType ====================

/**
 * Sealed class yang mendefinisikan tipe konten bottom sheet.
 * Setiap subclass membawa parameter spesifik untuk tipe tersebut.
 *
 * Usage:
 * ```
 * KanzanBottomSheet(
 *     isVisible = true,
 *     onDismiss = { },
 *     title = "Judul",
 *     sheetType = KanzanSheetType.Content { Text("Hello") },
 * )
 *
 * KanzanBottomSheet(
 *     isVisible = true,
 *     onDismiss = { },
 *     title = "Pilih Item",
 *     sheetType = KanzanSheetType.ItemList(
 *         items = listOf("A", "B", "C"),
 *         onItemSelected = { index -> },
 *     ),
 * )
 *
 * KanzanBottomSheet(
 *     isVisible = true,
 *     onDismiss = { },
 *     title = "Hapus?",
 *     sheetType = KanzanSheetType.Confirm(
 *         message = "Yakin hapus data ini?",
 *         onConfirm = { },
 *     ),
 * )
 * ```
 */
sealed class KanzanSheetType {

    /**
     * Free-form content — paling fleksibel, kamu tentukan sendiri isi body-nya.
     *
     * @param scrollable Apakah konten bisa di-scroll (default true)
     * @param body Composable content lambda
     */
    data class Content(
        val scrollable: Boolean = true,
        val body: @Composable ColumnScope.() -> Unit,
    ) : KanzanSheetType()

    /**
     * Selectable list dengan opsional search/filter.
     *
     * @param items Daftar label yang ditampilkan
     * @param selectedIndex Index item yang sedang terpilih (-1 = tidak ada)
     * @param onItemSelected Callback saat item dipilih (index asli dari [items])
     * @param selectedColor Warna background item terpilih
     * @param searchable Tampilkan field pencarian
     * @param searchPlaceholder Placeholder text untuk field pencarian
     * @param emptyText Text yang ditampilkan saat hasil pencarian kosong
     */
    data class ItemList(
        val items: List<String>,
        val selectedIndex: Int = -1,
        val onItemSelected: (Int) -> Unit,
        val selectedColor: Color = PrimaryDarkItungItungan.copy(alpha = 0.15f),
        val searchable: Boolean = false,
        val searchPlaceholder: String = "Cari...",
        val emptyText: String = "Tidak ditemukan",
    ) : KanzanSheetType()

    /**
     * Confirmation dialog dengan message dan dua tombol aksi.
     *
     * @param message Pesan konfirmasi
     * @param confirmText Label tombol konfirmasi
     * @param cancelText Label tombol batal
     * @param onConfirm Callback saat tombol konfirmasi ditekan
     * @param onCancel Callback saat tombol batal ditekan (null = pakai onDismiss)
     * @param confirmColor Warna text tombol konfirmasi
     */
    data class Confirm(
        val message: String,
        val confirmText: String = "Ya",
        val cancelText: String = "Batal",
        val onConfirm: () -> Unit,
        val onCancel: (() -> Unit)? = null,
        val confirmColor: Color = Color.Red,
    ) : KanzanSheetType()
}

// endregion

// region ==================== KanzanBottomSheet (Unified) ====================

/**
 * Unified Modal Bottom Sheet — satu composable untuk semua kebutuhan bottom sheet.
 *
 * Tipe konten ditentukan oleh [sheetType] sealed class:
 * - [KanzanSheetType.Content] → free-form composable body
 * - [KanzanSheetType.ItemList] → selectable list dengan opsional search
 * - [KanzanSheetType.Confirm] → confirmation dialog dengan 2 tombol
 *
 * Parameter shared berlaku untuk semua tipe.
 *
 * @param isVisible Kontrol visibilitas bottom sheet
 * @param onDismiss Dipanggil saat sheet ditutup (back press, scrim tap, atau close button)
 * @param sheetType Tipe konten bottom sheet (sealed class)
 * @param modifier Modifier untuk ModalBottomSheet
 * @param title Judul header (opsional)
 * @param subtitle Sub-judul header (opsional)
 * @param showCloseButton Tampilkan tombol close (✕) di header
 * @param showDragHandle Tampilkan drag handle indicator di atas
 * @param showDivider Tampilkan divider antara header dan konten
 * @param fullScreen Sheet mengisi seluruh layar (shape jadi RectangleShape)
 * @param skipPartiallyExpanded Sheet langsung fully expanded (skip half-expanded)
 * @param dismissOnOutsideClick Tap di luar bisa menutup sheet
 * @param sheetState SheetState untuk kontrol expand/collapse programmatically
 * @param shape Bentuk bottom sheet (default: top rounded 16dp; diabaikan saat [fullScreen])
 * @param containerColor Warna background sheet
 * @param scrimColor Warna scrim (overlay) di belakang sheet
 * @param tonalElevation Tonal elevation untuk surface sheet
 * @param headerContent Custom header composable (menggantikan default title/subtitle/close)
 */
@Composable
fun KanzanBottomSheet(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    sheetType: KanzanSheetType,
    modifier: Modifier = Modifier,
    title: String? = null,
    subtitle: String? = null,
    showCloseButton: Boolean = true,
    showDragHandle: Boolean = true,
    showDivider: Boolean = sheetType !is KanzanSheetType.Content,
    fullScreen: Boolean = false,
    skipPartiallyExpanded: Boolean = false,
    dismissOnOutsideClick: Boolean = true,
    sheetState: SheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = fullScreen || skipPartiallyExpanded
    ),
    shape: Shape = if (fullScreen) RectangleShape else CustomShapes.bottomSheetShape,
    containerColor: Color = Color.White,
    scrimColor: Color = Color.Black.copy(alpha = 0.32f),
    tonalElevation: Dp = dp0,
    headerContent: @Composable (() -> Unit)? = null,
) {
    if (!isVisible) return

    val scope = rememberCoroutineScope()
    val hideAndDismiss: () -> Unit = {
        scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
    }

    ModalBottomSheet(
        onDismissRequest = { if (dismissOnOutsideClick) onDismiss() },
        modifier = if (fullScreen) modifier.fillMaxSize() else modifier,
        sheetState = sheetState,
        shape = shape,
        containerColor = containerColor,
        scrimColor = scrimColor,
        tonalElevation = tonalElevation,
        dragHandle = if (showDragHandle && !fullScreen) {
            { KanzanDragHandle() }
        } else null,
    ) { ModalBottomSheetContent(headerContent, title, subtitle, showCloseButton, hideAndDismiss, showDivider, sheetType, onDismiss) }
}

@Composable
private fun ModalBottomSheetContent(headerContent: @Composable (() -> Unit)?, title: String?, subtitle: String?, showCloseButton: Boolean, hideAndDismiss: () -> Unit, showDivider: Boolean, sheetType: KanzanSheetType, onDismiss: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // ── Header ──
        if (headerContent != null) {
            headerContent()
        } else if (title != null) {
            KanzanSheetHeader(
                title = title,
                subtitle = subtitle,
                showCloseButton = showCloseButton,
                onClose = hideAndDismiss,
            )
        }

        // ── Divider ──
        if (showDivider && (title != null || headerContent != null)) {
            Divider(color = Color.LightGray.copy(alpha = 0.5f))
        }

        // ── Body berdasarkan sheetType ──
        when (sheetType) {
            is KanzanSheetType.Content -> ContentBody(sheetType)
            is KanzanSheetType.ItemList -> ItemListBody(sheetType, onDismiss)
            is KanzanSheetType.Confirm -> ConfirmBody(sheetType, onDismiss)
        }
    }
}

// endregion

// region ==================== Internal Body Composables ====================

@Composable
private fun ColumnScope.ContentBody(type: KanzanSheetType.Content) {
    val contentModifier = if (type.scrollable) {
        Modifier.fillMaxWidth().verticalScroll(rememberScrollState())
    } else {
        Modifier.fillMaxWidth()
    }
    Column(modifier = contentModifier) {
        type.body(this)
    }
}

@Composable
private fun ColumnScope.ItemListBody(
    type: KanzanSheetType.ItemList,
    onDismiss: () -> Unit,
) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredItems by remember(type.items, searchQuery) {
        derivedStateOf {
            val indexed = type.items.mapIndexed { index, label -> index to label }
            if (searchQuery.isBlank()) indexed
            else indexed.filter { it.second.contains(searchQuery, ignoreCase = true) }
        }
    }

    if (type.searchable) {
        KanzanTextField(
            label = "",
            value = searchQuery,
            onValueChanged = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dp16, vertical = dp8),
            placeholder = type.searchPlaceholder,
            kanzanInputType = KanzanInputType.SEARCH,
            imeAction = ImeAction.Done,
            singleLine = true,
        )
    }

    val scrollModifier = if (type.searchable) {
        Modifier.fillMaxWidth().weight(1f).verticalScroll(rememberScrollState())
    } else {
        Modifier.fillMaxWidth().verticalScroll(rememberScrollState())
    }

    Column(modifier = scrollModifier) {
        filteredItems.forEach { (originalIndex, label) ->
            val isSelected = originalIndex == type.selectedIndex
            val bgColor = if (isSelected) type.selectedColor else Color.Transparent
            Text(
                text = label,
                style = if (isSelected) AppTextStyle.nunito_medium_14 else AppTextStyle.nunito_regular_14,
                color = Color.Black,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(bgColor)
                    .clickable {
                        type.onItemSelected(originalIndex)
                        searchQuery = ""
                        onDismiss()
                    }
                    .padding(horizontal = dp16, vertical = dp14),
            )
        }
        if (type.searchable && filteredItems.isEmpty()) {
            Text(
                text = type.emptyText,
                style = AppTextStyle.nunito_regular_14,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = dp16, vertical = dp14),
            )
        }
    }
    Spacer(modifier = Modifier.height(dp16))
}

@Composable
private fun ConfirmBody(
    type: KanzanSheetType.Confirm,
    onDismiss: () -> Unit,
) {
    Text(
        text = type.message,
        style = AppTextStyle.nunito_regular_14,
        color = Color.DarkGray,
        modifier = Modifier.padding(horizontal = dp16, vertical = dp12),
    )
    Spacer(modifier = Modifier.height(dp8))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dp16, vertical = dp8),
        horizontalArrangement = Arrangement.spacedBy(dp8),
    ) {
        KanzanBaseButton(
            title = type.cancelText,
            onClick = { (type.onCancel ?: onDismiss).invoke() },
            modifier = Modifier.weight(1f),
            buttonType = KanzanButtonType.OUTLINED,
            buttonSize = KanzanButtonSize.MEDIUM,
            containerColor = Color.Gray,
            borderColor = Color.Gray,
            contentColor = Color.Gray,
        )
        KanzanBaseButton(
            title = type.confirmText,
            onClick = {
                type.onConfirm()
                onDismiss()
            },
            modifier = Modifier.weight(1f),
            buttonType = KanzanButtonType.FILLED,
            buttonSize = KanzanButtonSize.MEDIUM,
            containerColor = type.confirmColor,
            contentColor = Color.White,
        )
    }
    Spacer(modifier = Modifier.height(dp8))
}

// endregion

// region ==================== Internal UI Components ====================

/** Default drag handle indicator. */
@Composable
private fun KanzanDragHandle(
    modifier: Modifier = Modifier,
    color: Color = Color.LightGray,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = dp8),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .width(dp40)
                .height(dp4)
                .background(color, RoundedCornerShape(dp2)),
        )
    }
}

/** Default sheet header with title, subtitle, and close button. */
@Composable
private fun KanzanSheetHeader(
    title: String,
    subtitle: String? = null,
    showCloseButton: Boolean = true,
    onClose: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dp16, vertical = dp8),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = AppTextStyle.nunito_medium_16,
                color = Color.Black,
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = AppTextStyle.nunito_regular_12,
                    color = Color.Gray,
                )
            }
        }
        if (showCloseButton) {
            IconButton(onClick = onClose) {
                Text(text = "✕", style = AppTextStyle.nunito_medium_16, color = Color.Gray)
            }
        }
    }
}

// endregion


// region ==================== Previews ====================

@Preview(showBackground = true)
@Composable
private fun PreviewContentBody() {
    ModalBottomSheetContent(
        headerContent = null,
        title = "Content Sheet",
        subtitle = "Subtitle opsional",
        showCloseButton = true,
        hideAndDismiss = {},
        showDivider = false,
        sheetType = KanzanSheetType.Content {
            Text(
                text = "Ini adalah free-form content body.\nBisa diisi composable apapun.",
                modifier = Modifier.padding(dp16),
            )
        },
        onDismiss = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewContentBodyNoHeader() {
    ModalBottomSheetContent(
        headerContent = null,
        title = null,
        subtitle = null,
        showCloseButton = false,
        hideAndDismiss = {},
        showDivider = false,
        sheetType = KanzanSheetType.Content {
            Text(
                text = "Content tanpa header dan tanpa close button.",
                modifier = Modifier.padding(dp16),
            )
        },
        onDismiss = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewContentBodyCustomHeader() {
    ModalBottomSheetContent(
        headerContent = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Cyan.copy(alpha = 0.2f))
                    .padding(dp16),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "Custom Header", style = AppTextStyle.nunito_medium_16)
            }
        },
        title = null,
        subtitle = null,
        showCloseButton = false,
        hideAndDismiss = {},
        showDivider = true,
        sheetType = KanzanSheetType.Content {
            Text(text = "Body dengan custom header di atas.", modifier = Modifier.padding(dp16))
        },
        onDismiss = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewItemList() {
    ModalBottomSheetContent(
        headerContent = null,
        title = "Pilih Item",
        subtitle = null,
        showCloseButton = true,
        hideAndDismiss = {},
        showDivider = true,
        sheetType = KanzanSheetType.ItemList(
            items = listOf("Apel", "Jeruk", "Mangga", "Durian", "Rambutan"),
            selectedIndex = 2,
            onItemSelected = {},
        ),
        onDismiss = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewItemListNoSelection() {
    ModalBottomSheetContent(
        headerContent = null,
        title = "Pilih Kota",
        subtitle = "Belum ada yang dipilih",
        showCloseButton = true,
        hideAndDismiss = {},
        showDivider = true,
        sheetType = KanzanSheetType.ItemList(
            items = listOf("Jakarta", "Bandung", "Surabaya", "Yogyakarta"),
            selectedIndex = -1,
            onItemSelected = {},
        ),
        onDismiss = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewItemListSearchable() {
    ModalBottomSheetContent(
        headerContent = null,
        title = "Cari & Pilih",
        subtitle = null,
        showCloseButton = true,
        hideAndDismiss = {},
        showDivider = true,
        sheetType = KanzanSheetType.ItemList(
            items = listOf("Kucing", "Anjing", "Kelinci", "Hamster", "Burung"),
            selectedIndex = 1,
            onItemSelected = {},
            searchable = true,
            searchPlaceholder = "Cari hewan...",
            emptyText = "Hewan tidak ditemukan",
        ),
        onDismiss = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewItemListEmpty() {
    ModalBottomSheetContent(
        headerContent = null,
        title = "List Kosong",
        subtitle = null,
        showCloseButton = true,
        hideAndDismiss = {},
        showDivider = true,
        sheetType = KanzanSheetType.ItemList(
            items = emptyList(),
            onItemSelected = {},
            searchable = true,
            emptyText = "Data kosong",
        ),
        onDismiss = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewConfirm() {
    ModalBottomSheetContent(
        headerContent = null,
        title = "Konfirmasi Hapus",
        subtitle = null,
        showCloseButton = true,
        hideAndDismiss = {},
        showDivider = true,
        sheetType = KanzanSheetType.Confirm(
            message = "Apakah kamu yakin ingin menghapus data ini? Tindakan ini tidak bisa dibatalkan.",
            confirmText = "Hapus",
            cancelText = "Batal",
            onConfirm = {},
            confirmColor = Color.Red,
        ),
        onDismiss = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewConfirmCustomText() {
    ModalBottomSheetContent(
        headerContent = null,
        title = "Logout",
        subtitle = "Sesi akan berakhir",
        showCloseButton = false,
        hideAndDismiss = {},
        showDivider = true,
        sheetType = KanzanSheetType.Confirm(
            message = "Kamu akan keluar dari akun. Lanjutkan?",
            confirmText = "Ya, Keluar",
            cancelText = "Tidak",
            onConfirm = {},
            onCancel = {},
            confirmColor = Color(0xFFFF6600),
        ),
        onDismiss = {},
    )
}

// endregion
