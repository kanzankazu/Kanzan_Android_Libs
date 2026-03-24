package com.kanzankazu.kanzanwidget.compose.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kanzankazu.kanzanwidget.compose.ui.AppTextStyle
import com.kanzankazu.kanzanwidget.compose.ui.PrimaryDarkItungItungan
import com.kanzankazu.kanzanwidget.compose.ui.dp0
import com.kanzankazu.kanzanwidget.compose.ui.dp2
import com.kanzankazu.kanzanwidget.compose.ui.dp6
import com.kanzankazu.kanzanwidget.compose.ui.dp8
import com.kanzankazu.kanzanwidget.compose.ui.dp12
import com.kanzankazu.kanzanwidget.compose.ui.dp14
import com.kanzankazu.kanzanwidget.compose.ui.dp16
import com.kanzankazu.kanzanwidget.compose.ui.dp40
import com.kanzankazu.kanzanwidget.compose.ui.dp56

private val BarHeight = dp56

/** Layout alignment for [KanzanTabBar] and [KanzanChipBar]. */
enum class KanzanBarAlignment {
    /** Items start from the left */
    START,
    /** Items centered horizontally (equal weight per item for TabBar) */
    CENTER,
    /** Items scrollable horizontally (default) */
    SCROLL,
    /** Chips wrap to next row when horizontal space is full (KanzanChipBar only) */
    WRAP,
    WRAP_CENTER,
}

// region ==================== KanzanTabBar ====================

/**
 * Horizontal tab bar with underline indicator. Height matches TopAppBar (56dp).
 *
 * @param items Tab labels
 * @param selectedIndex Currently selected tab index
 * @param onTabSelected Called when a tab is tapped
 * @param alignment Layout — START, CENTER (equal weight), or SCROLL (horizontalScroll)
 * @param containerColor Background color
 * @param selectedColor Color for selected tab text and underline
 * @param unselectedColor Color for unselected tab text
 */
@Composable
fun KanzanTabBar(
    items: List<String>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    alignment: KanzanBarAlignment = KanzanBarAlignment.SCROLL,
    containerColor: Color = Color.White,
    selectedColor: Color = Color.Black,
    unselectedColor: Color = Color.Gray,
) {
    val rowModifier = modifier
        .fillMaxWidth()
        .height(BarHeight)
        .background(containerColor)

    val horizontalArrangement = when (alignment) {
        KanzanBarAlignment.START, KanzanBarAlignment.WRAP, KanzanBarAlignment.WRAP_CENTER -> Arrangement.Start
        KanzanBarAlignment.CENTER -> Arrangement.Center
        KanzanBarAlignment.SCROLL -> Arrangement.Start
    }

    val finalModifier = if (alignment == KanzanBarAlignment.SCROLL) {
        rowModifier.horizontalScroll(rememberScrollState())
    } else {
        rowModifier
    }

    Row(
        modifier = finalModifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = horizontalArrangement
    ) {
        items.forEachIndexed { index, label ->
            val isSelected = index == selectedIndex
            val itemModifier = when (alignment) {
                KanzanBarAlignment.CENTER -> Modifier.weight(1f)
                else -> Modifier.wrapContentWidth()
            }
            Column(
                modifier = itemModifier
                    .clickable { onTabSelected(index) }
                    .padding(horizontal = dp16),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = label,
                    style = if (isSelected) AppTextStyle.nunito_medium_14 else AppTextStyle.nunito_regular_14,
                    color = if (isSelected) selectedColor else unselectedColor,
                    modifier = Modifier.padding(vertical = dp8)
                )
                Box(
                    modifier = Modifier
                        .width(dp40)
                        .height(dp2)
                        .background(if (isSelected) selectedColor else Color.Transparent)
                )
            }
        }
    }
}
// endregion

// region ==================== KanzanChipBar ====================

/**
 * Horizontal chip bar. Height matches TopAppBar (56dp).
 *
 * @param items Chip labels
 * @param selectedIndex Currently selected chip index
 * @param onChipSelected Called when a chip is tapped
 * @param alignment Layout — START, CENTER, or SCROLL (horizontalScroll)
 * @param containerColor Background color of the bar
 * @param selectedChipColor Background color of selected chip
 * @param unselectedChipColor Background color of unselected chips
 * @param selectedTextColor Text color of selected chip
 * @param unselectedTextColor Text color of unselected chips
 */
@Composable
fun KanzanChipBar(
    items: List<String>,
    selectedIndex: Int,
    onChipSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    alignment: KanzanBarAlignment = KanzanBarAlignment.SCROLL,
    containerColor: Color = Color.White,
    selectedChipColor: Color = Color.Black,
    selectedTextColor: Color = Color.White,
    unselectedChipColor: Color = Color.LightGray,
    unselectedTextColor: Color = Color.DarkGray,
) {
    val chipContent: @Composable (Int, String) -> Unit = { index, label ->
        val isSelected = index == selectedIndex
        Surface(
            shape = RoundedCornerShape(dp16),
            color = if (isSelected) selectedChipColor else unselectedChipColor,
            modifier = Modifier
                .wrapContentWidth()
                .clickable { onChipSelected(index) }
        ) {
            Text(
                text = label,
                modifier = Modifier.padding(horizontal = dp14, vertical = dp6),
                style = if (isSelected) AppTextStyle.nunito_medium_12 else AppTextStyle.nunito_regular_12,
                color = if (isSelected) selectedTextColor else unselectedTextColor
            )
        }
    }

    if (alignment == KanzanBarAlignment.WRAP || alignment == KanzanBarAlignment.WRAP_CENTER) {
        SimpleFlowRow(
            modifier = modifier
                .fillMaxWidth()
                .background(containerColor)
                .padding(horizontal = dp12, vertical = dp8),
            horizontalSpacing = dp8,
            verticalSpacing = dp8,
            centerRows = alignment == KanzanBarAlignment.WRAP_CENTER
        ) {
            items.forEachIndexed { index, label -> chipContent(index, label) }
        }
    } else {
        val horizontalArrangement = when (alignment) {
            KanzanBarAlignment.START -> Arrangement.spacedBy(dp8, Alignment.Start)
            KanzanBarAlignment.CENTER -> Arrangement.spacedBy(dp8, Alignment.CenterHorizontally)
            else -> Arrangement.spacedBy(dp8)
        }

        val baseModifier = modifier
            .fillMaxWidth()
            .height(BarHeight)
            .background(containerColor)

        val finalModifier = if (alignment == KanzanBarAlignment.SCROLL) {
            baseModifier.horizontalScroll(rememberScrollState()).padding(horizontal = dp12)
        } else {
            baseModifier.padding(horizontal = dp12)
        }

        Row(
            modifier = finalModifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = horizontalArrangement
        ) {
            items.forEachIndexed { index, label -> chipContent(index, label) }
        }
    }
}
// endregion

// region ==================== SimpleFlowRow ====================

/**
 * Simple flow-row layout that wraps children to the next line when horizontal space runs out.
 * Replacement for FlowRow which requires Compose foundation 1.5+.
 */
@Composable
private fun SimpleFlowRow(
    modifier: Modifier = Modifier,
    horizontalSpacing: Dp = dp0,
    verticalSpacing: Dp = dp0,
    centerRows: Boolean = false,
    content: @Composable () -> Unit,
) {
    Layout(content = content, modifier = modifier) { measurables, constraints ->
        val hSpacingPx = horizontalSpacing.roundToPx()
        val vSpacingPx = verticalSpacing.roundToPx()
        val maxWidth = constraints.maxWidth

        val placeables = measurables.map { it.measure(constraints.copy(minWidth = 0, minHeight = 0)) }

        // First pass: group placeables into rows
        data class RowInfo(val items: MutableList<Int> = mutableListOf(), var width: Int = 0, var height: Int = 0)

        val rows = mutableListOf(RowInfo())
        var currentX = 0

        placeables.forEachIndexed { index, placeable ->
            if (currentX + placeable.width > maxWidth && currentX > 0) {
                rows.add(RowInfo())
                currentX = 0
            }
            val row = rows.last()
            if (row.items.isNotEmpty()) currentX += hSpacingPx
            row.items.add(index)
            row.width = currentX + placeable.width
            row.height = maxOf(row.height, placeable.height)
            currentX = row.width
        }

        // Second pass: compute positions
        var currentY = 0
        val positions = arrayOfNulls<Pair<Int, Int>>(placeables.size)

        rows.forEach { row ->
            val offsetX = if (centerRows) (maxWidth - row.width) / 2 else 0
            var x = offsetX
            row.items.forEachIndexed { i, placeableIndex ->
                if (i > 0) x += hSpacingPx
                positions[placeableIndex] = Pair(x, currentY)
                x += placeables[placeableIndex].width
            }
            currentY += row.height + vSpacingPx
        }

        val totalHeight = currentY - vSpacingPx
        layout(maxWidth, maxOf(0, totalHeight)) {
            placeables.forEachIndexed { i, placeable ->
                val pos = positions[i] ?: Pair(0, 0)
                placeable.placeRelative(pos.first, pos.second)
            }
        }
    }
}


// region ==================== Preview: KanzanTabBar ====================
private val sampleTabs = listOf("Harian", "Mingguan", "Bulanan", "Total")

@Preview(showBackground = true, name = "TabBar 1. SCROLL (default)")
@Composable
private fun PreviewTabBarScroll() {
    var selected by remember { mutableStateOf(0) }
    KanzanTabBar(items = sampleTabs, selectedIndex = selected, onTabSelected = { selected = it })
}

@Preview(showBackground = true, name = "TabBar 2. START")
@Composable
private fun PreviewTabBarStart() {
    var selected by remember { mutableStateOf(1) }
    KanzanTabBar(
        items = sampleTabs,
        selectedIndex = selected,
        onTabSelected = { selected = it },
        alignment = KanzanBarAlignment.START
    )
}

@Preview(showBackground = true, name = "TabBar 3. CENTER")
@Composable
private fun PreviewTabBarCenter() {
    var selected by remember { mutableStateOf(2) }
    KanzanTabBar(
        items = sampleTabs,
        selectedIndex = selected,
        onTabSelected = { selected = it },
        alignment = KanzanBarAlignment.CENTER
    )
}

@Preview(showBackground = true, name = "TabBar 4. Custom colors")
@Composable
private fun PreviewTabBarCustomColors() {
    var selected by remember { mutableStateOf(0) }
    KanzanTabBar(
        items = sampleTabs,
        selectedIndex = selected,
        onTabSelected = { selected = it },
        containerColor = PrimaryDarkItungItungan,
        selectedColor = Color.Black,
        unselectedColor = Color.DarkGray
    )
}

@Preview(showBackground = true, name = "TabBar 5. Many tabs (SCROLL)")
@Composable
private fun PreviewTabBarManyItems() {
    var selected by remember { mutableStateOf(0) }
    KanzanTabBar(
        items = listOf("Tab 1", "Tab 2", "Tab 3", "Tab 4", "Tab 5", "Tab 6", "Tab 7"),
        selectedIndex = selected,
        onTabSelected = { selected = it }
    )
}

@Preview(showBackground = true, name = "TabBar 6. Two tabs CENTER")
@Composable
private fun PreviewTabBarTwoCenter() {
    var selected by remember { mutableStateOf(0) }
    KanzanTabBar(
        items = listOf("Pemasukan", "Pengeluaran"),
        selectedIndex = selected,
        onTabSelected = { selected = it },
        alignment = KanzanBarAlignment.CENTER
    )
}
// endregion

// region ==================== Preview: KanzanChipBar ====================
private val sampleChips = listOf("Semua", "Belum Lunas", "Lunas", "Jatuh Tempo")

@Preview(showBackground = true, name = "ChipBar 1. SCROLL (default)")
@Composable
private fun PreviewChipBarScroll() {
    var selected by remember { mutableStateOf(0) }
    KanzanChipBar(items = sampleChips, selectedIndex = selected, onChipSelected = { selected = it })
}

@Preview(showBackground = true, name = "ChipBar 2. START")
@Composable
private fun PreviewChipBarStart() {
    var selected by remember { mutableStateOf(1) }
    KanzanChipBar(
        items = sampleChips,
        selectedIndex = selected,
        onChipSelected = { selected = it },
        alignment = KanzanBarAlignment.START
    )
}

@Preview(showBackground = true, name = "ChipBar 3. CENTER")
@Composable
private fun PreviewChipBarCenter() {
    var selected by remember { mutableStateOf(0) }
    KanzanChipBar(
        items = sampleChips,
        selectedIndex = selected,
        onChipSelected = { selected = it },
        alignment = KanzanBarAlignment.CENTER
    )
}

@Preview(showBackground = true, name = "ChipBar 4. Custom colors")
@Composable
private fun PreviewChipBarCustomColors() {
    var selected by remember { mutableStateOf(0) }
    KanzanChipBar(
        items = sampleChips,
        selectedIndex = selected,
        onChipSelected = { selected = it },
        containerColor = Color.DarkGray,
        selectedChipColor = Color.White,
        unselectedChipColor = Color.Gray,
        selectedTextColor = Color.Black,
        unselectedTextColor = Color.LightGray
    )
}

@Preview(showBackground = true, name = "ChipBar 5. Many chips (SCROLL)")
@Composable
private fun PreviewChipBarManyItems() {
    var selected by remember { mutableStateOf(0) }
    KanzanChipBar(
        items = listOf("Semua", "Hutang", "Piutang", "Lunas", "Jatuh Tempo", "Menunggak", "Baru"),
        selectedIndex = selected,
        onChipSelected = { selected = it }
    )
}

@Preview(showBackground = true, name = "ChipBar 6. Two chips CENTER")
@Composable
private fun PreviewChipBarTwoCenter() {
    var selected by remember { mutableStateOf(0) }
    KanzanChipBar(
        items = listOf("Aktif", "Arsip"),
        selectedIndex = selected,
        onChipSelected = { selected = it },
        alignment = KanzanBarAlignment.CENTER
    )
}

@Preview(showBackground = true, name = "ChipBar 7. WRAP")
@Composable
private fun PreviewChipBarWrap() {
    var selected by remember { mutableStateOf(0) }
    KanzanChipBar(
        items = listOf("Semua", "Hutang", "Piutang", "Lunas", "Jatuh Tempo", "Menunggak", "Baru", "Cicilan"),
        selectedIndex = selected,
        onChipSelected = { selected = it },
        alignment = KanzanBarAlignment.WRAP
    )
}

@Preview(showBackground = true, name = "ChipBar 8. WRAP few items")
@Composable
private fun PreviewChipBarWrapFew() {
    var selected by remember { mutableStateOf(1) }
    KanzanChipBar(
        items = listOf("Aktif", "Arsip", "Draft"),
        selectedIndex = selected,
        onChipSelected = { selected = it },
        alignment = KanzanBarAlignment.WRAP
    )
}

@Preview(showBackground = true, name = "ChipBar 9. WRAP_CENTER")
@Composable
private fun PreviewChipBarWrapCenter() {
    var selected by remember { mutableStateOf(0) }
    KanzanChipBar(
        items = listOf("Semua", "Hutang", "Piutang", "Lunas", "Jatuh Tempo", "Menunggak", "Baru", "Cicilan"),
        selectedIndex = selected,
        onChipSelected = { selected = it },
        alignment = KanzanBarAlignment.WRAP_CENTER
    )
}

@Preview(showBackground = true, name = "ChipBar 10. WRAP_CENTER few items")
@Composable
private fun PreviewChipBarWrapCenterFew() {
    var selected by remember { mutableStateOf(0) }
    KanzanChipBar(
        items = listOf("Aktif", "Arsip", "Draft"),
        selectedIndex = selected,
        onChipSelected = { selected = it },
        alignment = KanzanBarAlignment.WRAP_CENTER
    )
}
// endregion