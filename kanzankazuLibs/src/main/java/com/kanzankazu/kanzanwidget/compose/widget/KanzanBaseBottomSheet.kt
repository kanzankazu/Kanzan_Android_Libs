@file:OptIn(ExperimentalMaterial3Api::class)

package com.kanzankazu.kanzanwidget.compose.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.SheetValue
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

// region ==================== KanzanBaseBottomSheet ====================

/**
 * Reusable Modal Bottom Sheet with configurable header (title, subtitle, close button),
 * drag handle, shape, colors, and scrollable content.
 *
 * Wraps [ModalBottomSheet] with a consistent header pattern used across the app.
 *
 * @param isVisible Controls visibility of the bottom sheet
 * @param onDismiss Called when the sheet is dismissed (back press, scrim tap, or close button)
 * @param modifier Modifier for the ModalBottomSheet
 * @param title Optional header title
 * @param subtitle Optional header subtitle
 * @param showCloseButton Show close (✕) button in the header
 * @param showDragHandle Show drag handle indicator at the top
 * @param showDivider Show divider between header and content
 * @param fullScreen When true, sheet uses [RectangleShape] and fills the entire screen height
 * @param skipPartiallyExpanded When true, sheet skips half-expanded state and opens fully expanded
 * @param dismissOnOutsideClick When false, tapping scrim or back press won't dismiss the sheet
 * @param sheetState SheetState for controlling expand/collapse programmatically
 * @param shape Bottom sheet shape (default: top rounded 16dp; ignored when [fullScreen] is true)
 * @param containerColor Background color of the sheet
 * @param scrimColor Scrim (overlay) color behind the sheet
 * @param tonalElevation Tonal elevation for the sheet surface
 * @param headerContent Custom header composable (replaces default title/subtitle/close)
 * @param scrollable Whether the content area is vertically scrollable
 * @param content Sheet body content
 */
@Composable
fun KanzanBaseBottomSheet(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    subtitle: String? = null,
    showCloseButton: Boolean = true,
    showDragHandle: Boolean = true,
    showDivider: Boolean = false,
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
    scrollable: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    val scope = rememberCoroutineScope()

    if (!isVisible) return

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
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header
            if (headerContent != null) {
                headerContent()
            } else if (title != null) {
                KanzanSheetHeader(
                    title = title,
                    subtitle = subtitle,
                    showCloseButton = showCloseButton,
                    onClose = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss() }
                    }
                )
            }

            // Divider
            if (showDivider && (title != null || headerContent != null)) {
                Divider(color = Color.LightGray.copy(alpha = 0.5f))
            }

            // Content
            val contentModifier = if (scrollable) {
                Modifier.fillMaxWidth().verticalScroll(rememberScrollState())
            } else {
                Modifier.fillMaxWidth()
            }
            Column(modifier = contentModifier) {
                content()
            }
        }
    }
}

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
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(dp40)
                .height(dp4)
                .background(color, RoundedCornerShape(dp2))
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
        verticalAlignment = Alignment.CenterVertically
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

// region ==================== KanzanListBottomSheet ====================

/**
 * Convenience bottom sheet for displaying a selectable list of items.
 * Wraps [KanzanBaseBottomSheet] with a list of clickable rows.
 *
 * @param isVisible Controls visibility
 * @param onDismiss Called when dismissed
 * @param title Header title
 * @param items List of display labels
 * @param selectedIndex Currently selected index (highlighted, based on original items index)
 * @param onItemSelected Called with the selected index (original items index)
 * @param selectedColor Background color for selected item
 * @param subtitle Optional header subtitle
 * @param searchable When true, shows a search/filter field above the list
 * @param searchPlaceholder Placeholder text for the search field
 * @param fullScreen When true, sheet fills the entire screen
 * @param skipPartiallyExpanded When true, sheet opens fully expanded
 * @param dismissOnOutsideClick When false, tapping outside won't dismiss
 */
@Composable
fun KanzanListBottomSheet(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    title: String,
    items: List<String>,
    selectedIndex: Int = -1,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    selectedColor: Color = PrimaryDarkItungItungan.copy(alpha = 0.15f),
    subtitle: String? = null,
    searchable: Boolean = false,
    searchPlaceholder: String = "Cari...",
    fullScreen: Boolean = false,
    skipPartiallyExpanded: Boolean = false,
    dismissOnOutsideClick: Boolean = true,
) {
    var searchQuery by remember { mutableStateOf("") }
    // Pair(originalIndex, label) filtered by query
    val filteredItems by remember(items, searchQuery) {
        derivedStateOf {
            if (searchQuery.isBlank()) {
                items.mapIndexed { index, label -> index to label }
            } else {
                items.mapIndexed { index, label -> index to label }
                    .filter { it.second.contains(searchQuery, ignoreCase = true) }
            }
        }
    }

    KanzanBaseBottomSheet(
        isVisible = isVisible,
        onDismiss = {
            searchQuery = ""
            onDismiss()
        },
        modifier = modifier,
        title = title,
        subtitle = subtitle,
        showDivider = true,
        scrollable = !searchable, // when searchable, we handle scroll ourselves below the search field
        fullScreen = fullScreen,
        skipPartiallyExpanded = skipPartiallyExpanded,
        dismissOnOutsideClick = dismissOnOutsideClick,
    ) {
        if (searchable) {
            KanzanTextField(
                label = "",
                value = searchQuery,
                onValueChanged = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = dp16, vertical = dp8),
                placeholder = searchPlaceholder,
                kanzanInputType = KanzanInputType.SEARCH,
                imeAction = ImeAction.Done,
                singleLine = true,
            )
        }

        val scrollModifier = if (searchable) {
            Modifier.fillMaxWidth().weight(1f).verticalScroll(rememberScrollState())
        } else {
            Modifier.fillMaxWidth()
        }

        Column(modifier = scrollModifier) {
            filteredItems.forEach { (originalIndex, label) ->
                val isSelected = originalIndex == selectedIndex
                val bgColor = if (isSelected) selectedColor else Color.Transparent
                Text(
                    text = label,
                    style = if (isSelected) AppTextStyle.nunito_medium_14 else AppTextStyle.nunito_regular_14,
                    color = Color.Black,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(bgColor)
                        .clickable {
                            onItemSelected(originalIndex)
                            searchQuery = ""
                            onDismiss()
                        }
                        .padding(horizontal = dp16, vertical = dp14)
                )
            }
            if (searchable && filteredItems.isEmpty()) {
                Text(
                    text = "Tidak ditemukan",
                    style = AppTextStyle.nunito_regular_14,
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = dp16, vertical = dp14)
                )
            }
        }
        Spacer(modifier = Modifier.height(dp16))
    }
}
// endregion

// region ==================== KanzanConfirmBottomSheet ====================

/**
 * Convenience bottom sheet for confirmation dialogs with message and action buttons.
 *
 * @param isVisible Controls visibility
 * @param onDismiss Called when dismissed
 * @param title Header title
 * @param message Confirmation message body
 * @param confirmText Confirm button label
 * @param cancelText Cancel button label
 * @param onConfirm Called when confirm is tapped
 * @param onCancel Called when cancel is tapped (defaults to onDismiss)
 * @param confirmColor Confirm button text color
 * @param dismissOnOutsideClick When false, tapping outside won't dismiss (force user to choose)
 */
@Composable
fun KanzanConfirmBottomSheet(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    title: String,
    message: String,
    confirmText: String = "Ya",
    cancelText: String = "Batal",
    onConfirm: () -> Unit,
    onCancel: () -> Unit = onDismiss,
    modifier: Modifier = Modifier,
    confirmColor: Color = Color.Red,
    dismissOnOutsideClick: Boolean = true,
) {
    KanzanBaseBottomSheet(
        isVisible = isVisible,
        onDismiss = onDismiss,
        modifier = modifier,
        title = title,
        showDivider = true,
        scrollable = false,
        dismissOnOutsideClick = dismissOnOutsideClick,
    ) {
        Text(
            text = message,
            style = AppTextStyle.nunito_regular_14,
            color = Color.DarkGray,
            modifier = Modifier.padding(horizontal = dp16, vertical = dp12)
        )
        Spacer(modifier = Modifier.height(dp8))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dp16, vertical = dp8)
        ) {
            Text(
                text = cancelText,
                style = AppTextStyle.nunito_medium_14,
                color = Color.Gray,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onCancel() }
                    .padding(vertical = dp12),
            )
            Text(
                text = confirmText,
                style = AppTextStyle.nunito_medium_14,
                color = confirmColor,
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        onConfirm()
                        onDismiss()
                    }
                    .padding(vertical = dp12),
            )
        }
        Spacer(modifier = Modifier.height(dp8))
    }
}
// endregion

// region ==================== Preview ====================

/**
 * Helper yang merender konten sheet secara langsung (tanpa ModalBottomSheet)
 * supaya bisa tampil di static Compose Preview.
 * ModalBottomSheet pakai Dialog window internal yang tidak bisa render di preview.
 */
@Composable
private fun PreviewSheetSurface(
    title: String? = null,
    subtitle: String? = null,
    showCloseButton: Boolean = true,
    showDragHandle: Boolean = true,
    showDivider: Boolean = false,
    containerColor: Color = Color.White,
    headerContent: @Composable (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(containerColor, CustomShapes.bottomSheetShape)
    ) {
        if (showDragHandle) KanzanDragHandle()
        if (headerContent != null) {
            headerContent()
        } else if (title != null) {
            KanzanSheetHeader(title = title, subtitle = subtitle, showCloseButton = showCloseButton)
        }
        if (showDivider && (title != null || headerContent != null)) {
            Divider(color = Color.LightGray.copy(alpha = 0.5f))
        }
        content()
    }
}

// region ── 1. KanzanBaseBottomSheet variants ──

@Preview(showBackground = true, name = "BS 1. Title only")
@Composable
private fun PreviewBsTitle() {
    PreviewSheetSurface(title = "Pilih Opsi") {
        Text(text = "Konten bottom sheet", modifier = Modifier.padding(dp16), style = AppTextStyle.nunito_regular_14)
    }
}

@Preview(showBackground = true, name = "BS 2. Title + subtitle")
@Composable
private fun PreviewBsSubtitle() {
    PreviewSheetSurface(title = "Detail Hutang", subtitle = "Rp 500.000") {
        Text(text = "Informasi detail hutang", modifier = Modifier.padding(dp16), style = AppTextStyle.nunito_regular_14)
    }
}

@Preview(showBackground = true, name = "BS 3. No close button")
@Composable
private fun PreviewBsNoClose() {
    PreviewSheetSurface(title = "Informasi", showCloseButton = false) {
        Text(text = "Tanpa tombol close", modifier = Modifier.padding(dp16), style = AppTextStyle.nunito_regular_14)
    }
}

@Preview(showBackground = true, name = "BS 4. No drag handle")
@Composable
private fun PreviewBsNoDrag() {
    PreviewSheetSurface(title = "Tanpa Drag Handle", showDragHandle = false) {
        Text(text = "Konten tanpa drag handle", modifier = Modifier.padding(dp16), style = AppTextStyle.nunito_regular_14)
    }
}

@Preview(showBackground = true, name = "BS 5. With divider")
@Composable
private fun PreviewBsDivider() {
    PreviewSheetSurface(title = "Filter", subtitle = "Pilih filter", showDivider = true) {
        listOf("Semua", "Belum Lunas", "Lunas", "Jatuh Tempo").forEach { label ->
            Text(text = label, modifier = Modifier.fillMaxWidth().padding(horizontal = dp16, vertical = dp14), style = AppTextStyle.nunito_regular_14)
        }
    }
}

@Preview(showBackground = true, name = "BS 6. Custom header")
@Composable
private fun PreviewBsCustomHeader() {
    PreviewSheetSurface(
        headerContent = {
            Box(modifier = Modifier.fillMaxWidth().background(PrimaryDarkItungItungan).padding(dp16), contentAlignment = Alignment.Center) {
                Text(text = "Header Kustom", style = AppTextStyle.nunito_medium_16)
            }
        }
    ) {
        Text(text = "Konten dengan header kustom", modifier = Modifier.padding(dp16), style = AppTextStyle.nunito_regular_14)
    }
}

@Preview(showBackground = true, name = "BS 7. Dark color")
@Composable
private fun PreviewBsDark() {
    PreviewSheetSurface(title = "Tema Gelap", containerColor = Color.DarkGray) {
        Text(text = "Konten dengan background gelap", modifier = Modifier.padding(dp16), style = AppTextStyle.nunito_regular_14, color = Color.White)
    }
}

@Preview(showBackground = true, name = "BS 8. Primary color")
@Composable
private fun PreviewBsPrimary() {
    PreviewSheetSurface(title = "Aksi", containerColor = PrimaryDarkItungItungan.copy(alpha = 0.1f), showDivider = true) {
        Text(text = "Konten dengan warna primer", modifier = Modifier.padding(dp16), style = AppTextStyle.nunito_regular_14)
    }
}

@Preview(showBackground = true, name = "BS 9. Full screen (no drag, rectangle)")
@Composable
private fun PreviewBsFullScreen() {
    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        KanzanSheetHeader(title = "Full Screen", showCloseButton = true)
        Divider(color = Color.LightGray.copy(alpha = 0.5f))
        Text(text = "Bottom sheet full screen — tanpa rounded corner, tanpa drag handle", modifier = Modifier.padding(dp16), style = AppTextStyle.nunito_regular_14)
    }
}

@Preview(showBackground = true, name = "BS 10. Skip collapse (langsung expand)")
@Composable
private fun PreviewBsSkipCollapse() {
    PreviewSheetSurface(title = "Langsung Expand") {
        Text(text = "Sheet ini langsung expand penuh, skip half-expanded state", modifier = Modifier.padding(dp16), style = AppTextStyle.nunito_regular_14)
        Spacer(modifier = Modifier.height(dp100))
        Text(text = "Konten panjang...", modifier = Modifier.padding(dp16), style = AppTextStyle.nunito_regular_14)
    }
}

@Preview(showBackground = true, name = "BS 11. No dismiss outside")
@Composable
private fun PreviewBsNoDismiss() {
    PreviewSheetSurface(title = "Tidak bisa dismiss dari luar", showCloseButton = true) {
        Text(text = "Tap di luar tidak akan menutup sheet ini.\nGunakan tombol ✕ untuk menutup.", modifier = Modifier.padding(dp16), style = AppTextStyle.nunito_regular_14)
    }
}
// endregion

// region ── 2. KanzanListBottomSheet variants ──

@Composable
private fun PreviewListContent(
    title: String,
    items: List<String>,
    selectedIndex: Int = -1,
    subtitle: String? = null,
    selectedColor: Color = PrimaryDarkItungItungan.copy(alpha = 0.15f),
    searchable: Boolean = false,
    searchPlaceholder: String = "Cari...",
) {
    PreviewSheetSurface(title = title, subtitle = subtitle, showDivider = true) {
        if (searchable) {
            KanzanTextField(
                label = "",
                value = "",
                onValueChanged = {},
                modifier = Modifier.fillMaxWidth().padding(horizontal = dp16, vertical = dp8),
                placeholder = searchPlaceholder,
                kanzanInputType = KanzanInputType.SEARCH,
                imeAction = ImeAction.Done,
                singleLine = true,
            )
        }
        items.forEachIndexed { index, label ->
            val isSelected = index == selectedIndex
            Text(
                text = label,
                style = if (isSelected) AppTextStyle.nunito_medium_14 else AppTextStyle.nunito_regular_14,
                color = Color.Black,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isSelected) selectedColor else Color.Transparent)
                    .padding(horizontal = dp16, vertical = dp14)
            )
        }
        Spacer(modifier = Modifier.height(dp16))
    }
}

@Preview(showBackground = true, name = "List 1. Basic")
@Composable
private fun PreviewListBasic() {
    PreviewListContent(title = "Pilih Kategori", items = listOf("Makanan", "Transportasi", "Hiburan", "Belanja", "Tagihan"), selectedIndex = 1)
}

@Preview(showBackground = true, name = "List 2. Subtitle")
@Composable
private fun PreviewListSubtitle() {
    PreviewListContent(title = "Pilih Dompet", subtitle = "3 dompet tersedia", items = listOf("Tunai", "BCA", "Mandiri"), selectedIndex = 0)
}

@Preview(showBackground = true, name = "List 3. No selection")
@Composable
private fun PreviewListNoSelection() {
    PreviewListContent(title = "Urutkan", items = listOf("Terbaru", "Terlama", "Nominal Terbesar", "Nominal Terkecil"))
}

@Preview(showBackground = true, name = "List 4. Many items")
@Composable
private fun PreviewListMany() {
    PreviewListContent(
        title = "Pilih Bulan",
        items = listOf("Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember"),
        selectedIndex = 2
    )
}

@Preview(showBackground = true, name = "List 5. Searchable")
@Composable
private fun PreviewListSearchable() {
    PreviewListContent(
        title = "Pilih Kategori",
        items = listOf("Makanan", "Minuman", "Transportasi", "Hiburan", "Belanja", "Tagihan", "Pendidikan", "Kesehatan", "Olahraga", "Lainnya"),
        selectedIndex = 3,
        searchable = true,
        searchPlaceholder = "Cari kategori..."
    )
}

@Preview(showBackground = true, name = "List 6. Fullscreen + search")
@Composable
private fun PreviewListFullscreenSearch() {
    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        KanzanSheetHeader(title = "Pilih Kota", showCloseButton = true)
        Divider(color = Color.LightGray.copy(alpha = 0.5f))
        KanzanTextField(
            label = "",
            value = "",
            onValueChanged = {},
            modifier = Modifier.fillMaxWidth().padding(horizontal = dp16, vertical = dp8),
            placeholder = "Cari kota...",
            kanzanInputType = KanzanInputType.SEARCH,
            imeAction = ImeAction.Done,
            singleLine = true,
        )
        listOf("Jakarta", "Surabaya", "Bandung", "Medan", "Semarang", "Makassar", "Palembang", "Tangerang").forEach { city ->
            Text(text = city, style = AppTextStyle.nunito_regular_14, modifier = Modifier.fillMaxWidth().padding(horizontal = dp16, vertical = dp14))
        }
    }
}
// endregion

// region ── 3. KanzanConfirmBottomSheet variants ──

@Composable
private fun PreviewConfirmContent(
    title: String,
    message: String,
    confirmText: String = "Ya",
    cancelText: String = "Batal",
    confirmColor: Color = Color.Red,
) {
    PreviewSheetSurface(title = title, showDivider = true) {
        Text(text = message, style = AppTextStyle.nunito_regular_14, color = Color.DarkGray, modifier = Modifier.padding(horizontal = dp16, vertical = dp12))
        Spacer(modifier = Modifier.height(dp8))
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = dp16, vertical = dp8)) {
            Text(text = cancelText, style = AppTextStyle.nunito_medium_14, color = Color.Gray, modifier = Modifier.weight(1f).padding(vertical = dp12))
            Text(text = confirmText, style = AppTextStyle.nunito_medium_14, color = confirmColor, modifier = Modifier.weight(1f).padding(vertical = dp12))
        }
        Spacer(modifier = Modifier.height(dp8))
    }
}

@Preview(showBackground = true, name = "Confirm 1. Hapus")
@Composable
private fun PreviewConfirmDelete() {
    PreviewConfirmContent(title = "Hapus Hutang", message = "Apakah Anda yakin ingin menghapus hutang ini? Tindakan ini tidak dapat dibatalkan.")
}

@Preview(showBackground = true, name = "Confirm 2. Custom labels")
@Composable
private fun PreviewConfirmCustom() {
    PreviewConfirmContent(title = "Keluar Arisan", message = "Anda akan keluar dari grup arisan ini. Lanjutkan?", confirmText = "Keluar", cancelText = "Tetap di Grup")
}

@Preview(showBackground = true, name = "Confirm 3. Positive")
@Composable
private fun PreviewConfirmPositive() {
    PreviewConfirmContent(title = "Tandai Lunas", message = "Hutang sebesar Rp 500.000 akan ditandai sebagai lunas.", confirmText = "Tandai Lunas", confirmColor = Color(0xFF4CAF50))
}

@Preview(showBackground = true, name = "Confirm 4. No dismiss outside")
@Composable
private fun PreviewConfirmNoDismiss() {
    PreviewConfirmContent(title = "Konfirmasi Wajib", message = "Anda harus memilih salah satu opsi.\nTap di luar tidak akan menutup dialog ini.", confirmText = "Setuju", cancelText = "Tolak")
}
// endregion

// endregion
