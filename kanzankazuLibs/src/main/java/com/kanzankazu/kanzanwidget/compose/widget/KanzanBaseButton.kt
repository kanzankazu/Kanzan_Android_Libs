package com.kanzankazu.kanzanwidget.compose.widget

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kanzankazu.kanzanwidget.compose.ui.AppTextStyle
import com.kanzankazu.kanzanwidget.compose.ui.Shapes
import com.kanzankazu.kanzanwidget.compose.ui.dp12
import com.kanzankazu.kanzanwidget.compose.ui.dp0
import com.kanzankazu.kanzanwidget.compose.ui.dp16
import com.kanzankazu.kanzanwidget.compose.ui.dp2
import com.kanzankazu.kanzanwidget.compose.ui.dp20
import com.kanzankazu.kanzanwidget.compose.ui.dp24
import com.kanzankazu.kanzanwidget.compose.ui.dp4
import com.kanzankazu.kanzanwidget.compose.ui.dp48
import com.kanzankazu.kanzanwidget.compose.ui.dp8

// region ==================== Enums & Models ====================

enum class KanzanButtonType { FILLED, OUTLINED, TEXT, ELEVATED }
enum class KanzanButtonSize { SMALL, MEDIUM, LARGE }

enum class KanzanButtonGroupType {
    /** Segmented toggle — satu yang aktif */
    SINGLE_SELECT,
    /** Multi toggle — bisa lebih dari satu aktif */
    MULTI_SELECT,
    /** Row of action buttons — tidak ada state aktif */
    ACTION_ROW,
}

// endregion

// region ==================== KanzanBaseButton ====================

/**
 * Komponen button generic OVER POWER.
 * Support: title, subtitle, icon (leading/trailing), nominal, loading, skeleton,
 * badge, berbagai tipe (filled/outlined/text/elevated), berbagai ukuran, full width, dll.
 *
 * @param title teks utama button.
 * @param onClick callback klik.
 * @param modifier Modifier.
 * @param subtitle teks sekunder di bawah title.
 * @param nominal teks nominal (misal "Rp 100.000") di sisi kanan.
 * @param leadingIcon icon di kiri title.
 * @param trailingIcon icon di kanan title.
 * @param enabled aktif/nonaktif.
 * @param isLoading tampilkan loading indicator (disable klik).
 * @param isSkeleton tampilkan skeleton shimmer placeholder.
 * @param buttonType tipe button (FILLED, OUTLINED, TEXT, ELEVATED).
 * @param buttonSize ukuran button (SMALL, MEDIUM, LARGE).
 * @param fullWidth button mengisi lebar penuh.
 * @param shape bentuk button.
 * @param containerColor warna background (filled).
 * @param contentColor warna konten (teks, icon).
 * @param disabledContainerColor warna background saat disabled.
 * @param disabledContentColor warna konten saat disabled.
 * @param borderColor warna border (outlined).
 * @param borderWidth lebar border.
 * @param elevation elevasi (elevated).
 * @param titleStyle style teks title.
 * @param subtitleStyle style teks subtitle.
 * @param nominalStyle style teks nominal.
 * @param loadingColor warna loading indicator.
 * @param loadingSize ukuran loading indicator.
 * @param contentPadding padding internal.
 * @param badge composable badge (misal notif count).
 * @param customContent composable kustom (menggantikan seluruh konten default).
 */
@Composable
fun KanzanBaseButton(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    nominal: String? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    isSkeleton: Boolean = false,
    buttonType: KanzanButtonType = KanzanButtonType.FILLED,
    buttonSize: KanzanButtonSize = KanzanButtonSize.MEDIUM,
    fullWidth: Boolean = false,
    shape: Shape = Shapes.medium,
    containerColor: Color = Color.Black,
    contentColor: Color = Color.White,
    disabledContainerColor: Color = Color.LightGray,
    disabledContentColor: Color = Color.Gray,
    borderColor: Color = Color.Black,
    borderWidth: Dp = 1.dp,
    elevation: Dp = dp4,
    titleStyle: TextStyle? = null,
    subtitleStyle: TextStyle? = null,
    nominalStyle: TextStyle? = null,
    loadingColor: Color = contentColor,
    loadingSize: Dp = dp20,
    contentPadding: PaddingValues? = null,
    badge: @Composable (() -> Unit)? = null,
    customContent: @Composable (() -> Unit)? = null,
) {
    // Skeleton mode
    if (isSkeleton) {
        KanzanSkeletonButton(modifier = modifier, fullWidth = fullWidth, buttonSize = buttonSize, shape = shape)
        return
    }

    val resolvedTitleStyle = titleStyle ?: when (buttonSize) {
        KanzanButtonSize.SMALL -> AppTextStyle.nunito_medium_12
        KanzanButtonSize.MEDIUM -> AppTextStyle.nunito_medium_14
        KanzanButtonSize.LARGE -> AppTextStyle.nunito_medium_16
    }
    val resolvedSubtitleStyle = subtitleStyle ?: AppTextStyle.nunito_regular_12
    val resolvedNominalStyle = nominalStyle ?: AppTextStyle.nunito_bold_14
    val resolvedPadding = contentPadding ?: when (buttonSize) {
        KanzanButtonSize.SMALL -> PaddingValues(horizontal = dp8, vertical = dp4)
        KanzanButtonSize.MEDIUM -> PaddingValues(horizontal = dp16, vertical = dp8)
        KanzanButtonSize.LARGE -> PaddingValues(horizontal = dp24, vertical = dp12)
    }
    val resolvedHeight = when (buttonSize) {
        KanzanButtonSize.SMALL -> 32.dp
        KanzanButtonSize.MEDIUM -> dp48
        KanzanButtonSize.LARGE -> 56.dp
    }

    val finalEnabled = enabled && !isLoading
    val hasMultiLine = subtitle != null
    val heightModifier = if (hasMultiLine) Modifier.heightIn(min = resolvedHeight) else Modifier.height(resolvedHeight)
    val finalModifier = if (fullWidth) modifier.fillMaxWidth().then(heightModifier) else modifier.then(heightModifier)

    val colors = ButtonDefaults.buttonColors(
        containerColor = containerColor,
        contentColor = contentColor,
        disabledContainerColor = disabledContainerColor,
        disabledContentColor = disabledContentColor,
    )
    val outlinedColors = ButtonDefaults.outlinedButtonColors(
        contentColor = containerColor,
        disabledContentColor = disabledContentColor,
    )
    val textColors = ButtonDefaults.textButtonColors(
        contentColor = containerColor,
        disabledContentColor = disabledContentColor,
    )

    val content: @Composable () -> Unit = {
        if (customContent != null) {
            customContent()
        } else {
            KanzanButtonContent(
                title = title,
                subtitle = subtitle,
                nominal = nominal,
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon,
                isLoading = isLoading,
                loadingColor = loadingColor,
                loadingSize = loadingSize,
                titleStyle = resolvedTitleStyle,
                subtitleStyle = resolvedSubtitleStyle,
                nominalStyle = resolvedNominalStyle,
                badge = badge,
            )
        }
    }

    when (buttonType) {
        KanzanButtonType.FILLED -> Button(
            onClick = onClick, modifier = finalModifier, enabled = finalEnabled,
            shape = shape, colors = colors, contentPadding = resolvedPadding,
        ) { content() }

        KanzanButtonType.OUTLINED -> OutlinedButton(
            onClick = onClick, modifier = finalModifier, enabled = finalEnabled,
            shape = shape, colors = outlinedColors, contentPadding = resolvedPadding,
            border = BorderStroke(borderWidth, if (finalEnabled) borderColor else disabledContentColor),
        ) { content() }

        KanzanButtonType.TEXT -> TextButton(
            onClick = onClick, modifier = finalModifier, enabled = finalEnabled,
            shape = shape, colors = textColors, contentPadding = resolvedPadding,
        ) { content() }

        KanzanButtonType.ELEVATED -> ElevatedButton(
            onClick = onClick, modifier = finalModifier, enabled = finalEnabled,
            shape = shape, contentPadding = resolvedPadding,
        ) { content() }
    }
}

@Composable
private fun KanzanButtonContent(
    title: String,
    subtitle: String?,
    nominal: String?,
    leadingIcon: @Composable (() -> Unit)?,
    trailingIcon: @Composable (() -> Unit)?,
    isLoading: Boolean,
    loadingColor: Color,
    loadingSize: Dp,
    titleStyle: TextStyle,
    subtitleStyle: TextStyle,
    nominalStyle: TextStyle,
    badge: @Composable (() -> Unit)?,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        // Loading
        AnimatedVisibility(visible = isLoading, enter = fadeIn(), exit = fadeOut()) {
            Row {
                CircularProgressIndicator(modifier = Modifier.size(loadingSize), color = loadingColor, strokeWidth = dp2)
                KanzanSpacerHorizontal(width = dp8)
            }
        }

        // Leading icon
        if (!isLoading) {
            leadingIcon?.let {
                it()
                KanzanSpacerHorizontal(width = dp8)
            }
        }

        // Title + subtitle
        Column(
            modifier =
                if (nominal != null) Modifier.weight(1f)
                else Modifier
        ) {
            Text(text = title, style = titleStyle, maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (subtitle != null) {
                Text(text = subtitle, style = subtitleStyle, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }

        // Nominal
        if (nominal != null) {
            KanzanSpacerHorizontal(width = dp8)
            Text(text = nominal, style = nominalStyle, textAlign = TextAlign.End)
        }

        // Trailing icon
        trailingIcon?.let {
            KanzanSpacerHorizontal(width = dp8)
            it()
        }

        // Badge
        badge?.let {
            KanzanSpacerHorizontal(width = dp4)
            it()
        }
    }
}

@Composable
private fun KanzanSkeletonButton(
    modifier: Modifier,
    fullWidth: Boolean,
    buttonSize: KanzanButtonSize,
    shape: Shape,
) {
    val height = when (buttonSize) {
        KanzanButtonSize.SMALL -> 32.dp
        KanzanButtonSize.MEDIUM -> dp48
        KanzanButtonSize.LARGE -> 56.dp
    }
    val transition = rememberInfiniteTransition(label = "skeleton")
    val shimmerX by transition.animateFloat(
        initialValue = -300f, targetValue = 900f,
        animationSpec = infiniteRepeatable(tween(1200), RepeatMode.Restart),
        label = "shimmerX"
    )
    val brush = Brush.linearGradient(
        colors = listOf(Color(0xFFE0E0E0), Color(0xFFF5F5F5), Color(0xFFE0E0E0)),
        start = Offset(shimmerX, 0f),
        end = Offset(shimmerX + 300f, 0f)
    )
    Box(
        modifier = (if (fullWidth) modifier.fillMaxWidth() else modifier.width(200.dp))
            .height(height)
            .clip(shape)
            .background(brush)
    )
}
// endregion

// region ==================== Preview ====================

@Preview(showBackground = true, name = "Button 1. Filled")
@Composable
private fun PreviewButtonFilled() {
    Column(modifier = Modifier.padding(dp16), verticalArrangement = Arrangement.spacedBy(dp8)) {
        KanzanBaseButton(title = "Simpan", onClick = {})
        KanzanBaseButton(title = "Simpan", onClick = {}, fullWidth = true)
    }
}

@Preview(showBackground = true, name = "Button 2. Outlined")
@Composable
private fun PreviewButtonOutlined() {
    KanzanBaseButton(title = "Batal", onClick = {}, buttonType = KanzanButtonType.OUTLINED, fullWidth = true)
}

@Preview(showBackground = true, name = "Button 3. Text")
@Composable
private fun PreviewButtonText() {
    KanzanBaseButton(title = "Lewati", onClick = {}, buttonType = KanzanButtonType.TEXT)
}

@Preview(showBackground = true, name = "Button 4. Elevated")
@Composable
private fun PreviewButtonElevated() {
    KanzanBaseButton(title = "Elevated", onClick = {}, buttonType = KanzanButtonType.ELEVATED)
}

@Preview(showBackground = true, name = "Button 5. Sizes")
@Composable
private fun PreviewButtonSizes() {
    Column(modifier = Modifier.padding(dp16), verticalArrangement = Arrangement.spacedBy(dp8)) {
        KanzanBaseButton(title = "Small", onClick = {}, buttonSize = KanzanButtonSize.SMALL)
        KanzanBaseButton(title = "Medium", onClick = {}, buttonSize = KanzanButtonSize.MEDIUM)
        KanzanBaseButton(title = "Large", onClick = {}, buttonSize = KanzanButtonSize.LARGE)
    }
}

@Preview(showBackground = true, name = "Button 6. With icon")
@Composable
private fun PreviewButtonIcon() {
    Column(modifier = Modifier.padding(dp16), verticalArrangement = Arrangement.spacedBy(dp8)) {
        KanzanBaseButton(
            title = "Tambah Hutang",
            onClick = {},
            leadingIcon = { Text(text = "➕", style = AppTextStyle.nunito_regular_14) },
            fullWidth = true
        )
        KanzanBaseButton(
            title = "Kirim",
            onClick = {},
            trailingIcon = { Text(text = "📤", style = AppTextStyle.nunito_regular_14) },
        )
    }
}

@Preview(showBackground = true, name = "Button 7. Title + subtitle")
@Composable
private fun PreviewButtonSubtitle() {
    KanzanBaseButton(
        title = "Transfer",
        subtitle = "Ke rekening BCA",
        onClick = {},
        leadingIcon = { Text(text = "🏦", style = AppTextStyle.nunito_regular_16) },
        fullWidth = true,
        buttonSize = KanzanButtonSize.LARGE
    )
}

@Preview(showBackground = true, name = "Button 8. With nominal")
@Composable
private fun PreviewButtonNominal() {
    KanzanBaseButton(
        title = "Bayar Hutang",
        nominal = "Rp 500.000",
        onClick = {},
        leadingIcon = { Text(text = "💰", style = AppTextStyle.nunito_regular_16) },
        fullWidth = true,
        buttonSize = KanzanButtonSize.LARGE
    )
}

@Preview(showBackground = true, name = "Button 9. Loading")
@Composable
private fun PreviewButtonLoading() {
    Column(modifier = Modifier.padding(dp16), verticalArrangement = Arrangement.spacedBy(dp8)) {
        KanzanBaseButton(title = "Menyimpan...", onClick = {}, isLoading = true, fullWidth = true)
        KanzanBaseButton(title = "Mengirim...", onClick = {}, isLoading = true, buttonType = KanzanButtonType.OUTLINED, fullWidth = true)
    }
}

@Preview(showBackground = true, name = "Button 10. Skeleton")
@Composable
private fun PreviewButtonSkeleton() {
    Column(modifier = Modifier.padding(dp16), verticalArrangement = Arrangement.spacedBy(dp8)) {
        KanzanBaseButton(title = "", onClick = {}, isSkeleton = true, fullWidth = true)
        KanzanBaseButton(title = "", onClick = {}, isSkeleton = true, buttonSize = KanzanButtonSize.SMALL)
    }
}

@Preview(showBackground = true, name = "Button 11. Disabled")
@Composable
private fun PreviewButtonDisabled() {
    KanzanBaseButton(title = "Tidak Tersedia", onClick = {}, enabled = false, fullWidth = true)
}

@Preview(showBackground = true, name = "Button 12. Custom colors")
@Composable
private fun PreviewButtonCustomColors() {
    Column(modifier = Modifier.padding(dp16), verticalArrangement = Arrangement.spacedBy(dp8)) {
        KanzanBaseButton(
            title = "Hapus",
            onClick = {},
            buttonType = KanzanButtonType.OUTLINED,
            borderColor = Color.Red,
            contentColor = Color.Red,
            fullWidth = true
        )
        KanzanBaseButton(
            title = "Sukses",
            onClick = {},
            containerColor = Color(0xFF4CAF50),
            fullWidth = true
        )
    }
}

@Preview(showBackground = true, name = "Button 13. Badge")
@Composable
private fun PreviewButtonBadge() {
    KanzanBaseButton(
        title = "Notifikasi",
        onClick = {},
        leadingIcon = { Text(text = "🔔", style = AppTextStyle.nunito_regular_14) },
        badge = {
            Box(
                modifier = Modifier
                    .size(dp20)
                    .background(Color.Red, RoundedCornerShape(dp4)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "5", style = AppTextStyle.nunito_bold_12, color = Color.White)
            }
        }
    )
}

// endregion

// region ==================== KanzanButtonGroup ====================

/**
 * Group of buttons dalam satu baris.
 * Support: segmented control (single/multi select), action row.
 *
 * @param items daftar label button.
 * @param modifier Modifier.
 * @param groupType tipe group (SINGLE_SELECT, MULTI_SELECT, ACTION_ROW).
 * @param selectedIndices set index yang aktif (untuk SINGLE/MULTI_SELECT).
 * @param onSelectionChanged callback saat seleksi berubah.
 * @param onItemClick callback per item click (untuk ACTION_ROW).
 * @param selectedColor warna background button aktif.
 * @param unselectedColor warna background button tidak aktif.
 * @param selectedContentColor warna teks button aktif.
 * @param unselectedContentColor warna teks button tidak aktif.
 * @param borderColor warna border.
 * @param borderWidth lebar border.
 * @param shape bentuk group (default: rounded).
 * @param textStyle style teks.
 * @param spacing jarak antar button (0 = merged/segmented look).
 * @param fullWidth button mengisi lebar penuh (equal weight).
 * @param enabled aktif/nonaktif seluruh group.
 * @param itemIcons composable icon per item (opsional).
 */
@Composable
fun KanzanButtonGroup(
    items: List<String>,
    modifier: Modifier = Modifier,
    groupType: KanzanButtonGroupType = KanzanButtonGroupType.SINGLE_SELECT,
    selectedIndices: Set<Int> = emptySet(),
    onSelectionChanged: ((Set<Int>) -> Unit)? = null,
    onItemClick: ((Int) -> Unit)? = null,
    selectedColor: Color = Color.Black,
    unselectedColor: Color = Color.Transparent,
    selectedContentColor: Color = Color.White,
    unselectedContentColor: Color = Color.Black,
    borderColor: Color = Color.Black,
    borderWidth: Dp = 1.dp,
    shape: Shape = RoundedCornerShape(dp8),
    textStyle: TextStyle = AppTextStyle.nunito_medium_14,
    spacing: Dp = dp0,
    fullWidth: Boolean = true,
    enabled: Boolean = true,
    itemIcons: @Composable ((index: Int) -> Unit)? = null,
) {
    Row(
        modifier = modifier.then(if (fullWidth) Modifier.fillMaxWidth() else Modifier),
        horizontalArrangement = if (spacing > dp0) Arrangement.spacedBy(spacing) else Arrangement.Center,
    ) {
        items.forEachIndexed { index, label ->
            val isSelected = index in selectedIndices

            val buttonShape = if (spacing == dp0) {
                when {
                    items.size == 1 -> shape
                    index == 0 -> RoundedCornerShape(topStart = dp8, bottomStart = dp8, topEnd = dp0, bottomEnd = dp0)
                    index == items.lastIndex -> RoundedCornerShape(topStart = dp0, bottomStart = dp0, topEnd = dp8, bottomEnd = dp8)
                    else -> RoundedCornerShape(dp0)
                }
            } else shape

            val containerColor = if (isSelected) selectedColor else unselectedColor
            val contentColor = if (isSelected) selectedContentColor else unselectedContentColor

            OutlinedButton(
                onClick = {
                    when (groupType) {
                        KanzanButtonGroupType.SINGLE_SELECT -> {
                            onSelectionChanged?.invoke(setOf(index))
                        }
                        KanzanButtonGroupType.MULTI_SELECT -> {
                            val newSet = selectedIndices.toMutableSet()
                            if (isSelected) newSet.remove(index) else newSet.add(index)
                            onSelectionChanged?.invoke(newSet)
                        }
                        KanzanButtonGroupType.ACTION_ROW -> {
                            onItemClick?.invoke(index)
                        }
                    }
                },
                modifier = if (fullWidth) Modifier.weight(1f) else Modifier,
                enabled = enabled,
                shape = buttonShape,
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = containerColor,
                    contentColor = contentColor,
                ),
                border = BorderStroke(borderWidth, borderColor),
            ) {
                itemIcons?.invoke(index)
                Text(text = label, style = textStyle, color = contentColor)
            }
        }
    }
}

// endregion

// region ==================== ButtonGroup Preview ====================

@Preview(showBackground = true, name = "ButtonGroup 1. Single select (segmented)")
@Composable
private fun PreviewSingleSelect() {
    var selected by remember { mutableStateOf(setOf(0)) }
    KanzanButtonGroup(
        items = listOf("Harian", "Bulanan", "Tahunan"),
        modifier = Modifier.padding(dp16),
        selectedIndices = selected,
        onSelectionChanged = { selected = it },
    )
}

@Preview(showBackground = true, name = "ButtonGroup 2. Multi select")
@Composable
private fun PreviewMultiSelect() {
    var selected by remember { mutableStateOf(setOf(0, 2)) }
    KanzanButtonGroup(
        items = listOf("Hutang", "Piutang", "Cicilan", "Tabungan"),
        modifier = Modifier.padding(dp16),
        groupType = KanzanButtonGroupType.MULTI_SELECT,
        selectedIndices = selected,
        onSelectionChanged = { selected = it },
    )
}

@Preview(showBackground = true, name = "ButtonGroup 3. Action row")
@Composable
private fun PreviewActionRow() {
    KanzanButtonGroup(
        items = listOf("Edit", "Hapus", "Bagikan"),
        modifier = Modifier.padding(dp16),
        groupType = KanzanButtonGroupType.ACTION_ROW,
        onItemClick = {},
        selectedColor = Color.Transparent,
        unselectedColor = Color.Transparent,
    )
}

@Preview(showBackground = true, name = "ButtonGroup 4. With spacing")
@Composable
private fun PreviewWithSpacing() {
    var selected by remember { mutableStateOf(setOf(1)) }
    KanzanButtonGroup(
        items = listOf("Ya", "Tidak", "Mungkin"),
        modifier = Modifier.padding(dp16),
        selectedIndices = selected,
        onSelectionChanged = { selected = it },
        spacing = dp8,
    )
}

@Preview(showBackground = true, name = "ButtonGroup 5. Custom colors")
@Composable
private fun PreviewButtonGroupCustomColors() {
    var selected by remember { mutableStateOf(setOf(0)) }
    KanzanButtonGroup(
        items = listOf("Lunas", "Belum Lunas"),
        modifier = Modifier.padding(dp16),
        selectedIndices = selected,
        onSelectionChanged = { selected = it },
        selectedColor = Color(0xFF4CAF50),
        borderColor = Color(0xFF4CAF50),
    )
}

@Preview(showBackground = true, name = "ButtonGroup 6. Two buttons")
@Composable
private fun PreviewTwoButtons() {
    var selected by remember { mutableStateOf(setOf(0)) }
    KanzanButtonGroup(
        items = listOf("Pemasukan", "Pengeluaran"),
        modifier = Modifier.padding(dp16),
        selectedIndices = selected,
        onSelectionChanged = { selected = it },
    )
}

@Preview(showBackground = true, name = "ButtonGroup 7. Disabled")
@Composable
private fun PreviewButtonGroupDisabled() {
    KanzanButtonGroup(
        items = listOf("A", "B", "C"),
        modifier = Modifier.padding(dp16),
        selectedIndices = setOf(1),
        enabled = false,
    )
}

@Preview(showBackground = true, name = "ButtonGroup 8. Not full width")
@Composable
private fun PreviewNotFullWidth() {
    var selected by remember { mutableStateOf(setOf(0)) }
    KanzanButtonGroup(
        items = listOf("S", "M", "L", "XL"),
        modifier = Modifier.padding(dp16),
        selectedIndices = selected,
        onSelectionChanged = { selected = it },
        fullWidth = false,
    )
}

// endregion
