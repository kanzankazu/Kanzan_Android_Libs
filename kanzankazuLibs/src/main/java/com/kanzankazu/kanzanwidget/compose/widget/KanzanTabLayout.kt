package com.kanzankazu.kanzanwidget.compose.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.unit.dp
import com.kanzankazu.kanzanwidget.compose.ui.AppTextStyle
import com.kanzankazu.kanzanwidget.compose.ui.dp16

// region ==================== KanzanTabLayout ====================

/**
 * Tab layout dengan support fixed/scrollable tabs.
 *
 * @param tabs daftar label tab.
 * @param selectedIndex index tab yang aktif.
 * @param onTabSelected callback saat tab dipilih.
 * @param modifier Modifier.
 * @param scrollable apakah tab bisa di-scroll (untuk banyak tab).
 * @param containerColor warna background tab row.
 * @param selectedContentColor warna teks tab aktif.
 * @param unselectedContentColor warna teks tab tidak aktif.
 * @param tabTextStyle style teks tab.
 * @param tabIcon composable icon per tab (opsional).
 */
@Composable
fun KanzanTabLayout(
    tabs: List<String>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    scrollable: Boolean = false,
    containerColor: Color = Color.White,
    selectedContentColor: Color = Color.Black,
    unselectedContentColor: Color = Color.Gray,
    tabTextStyle: TextStyle = AppTextStyle.nunito_medium_14,
    tabIcon: @Composable ((index: Int) -> Unit)? = null,
) {
    val tabContent: @Composable (index: Int) -> Unit = { index ->
        Tab(
            selected = selectedIndex == index,
            onClick = { onTabSelected(index) },
            text = {
                Text(
                    text = tabs[index],
                    style = tabTextStyle,
                    color = if (selectedIndex == index) selectedContentColor else unselectedContentColor,
                )
            },
            icon = if (tabIcon != null) {
                { tabIcon(index) }
            } else null,
        )
    }

    if (scrollable) {
        ScrollableTabRow(
            selectedTabIndex = selectedIndex,
            modifier = modifier,
            containerColor = containerColor,
            edgePadding = dp16,
        ) {
            tabs.forEachIndexed { index, _ -> tabContent(index) }
        }
    } else {
        TabRow(
            selectedTabIndex = selectedIndex,
            modifier = modifier,
            containerColor = containerColor,
        ) {
            tabs.forEachIndexed { index, _ -> tabContent(index) }
        }
    }
}

// endregion

// region ==================== Preview ====================

@Preview(showBackground = true, name = "TabLayout 1. Fixed tabs")
@Composable
private fun PreviewTabLayoutFixed() {
    var selected by remember { mutableStateOf(0) }
    Column {
        KanzanTabLayout(
            tabs = listOf("Harian", "Bulanan", "Tahunan"),
            selectedIndex = selected,
            onTabSelected = { selected = it },
        )
        Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
            Text(text = "Tab: $selected", style = AppTextStyle.nunito_regular_14)
        }
    }
}

@Preview(showBackground = true, name = "TabLayout 2. Scrollable tabs")
@Composable
private fun PreviewTabLayoutScrollable() {
    var selected by remember { mutableStateOf(0) }
    KanzanTabLayout(
        tabs = listOf("Semua", "Hutang", "Piutang", "Lunas", "Belum Lunas", "Jatuh Tempo"),
        selectedIndex = selected,
        onTabSelected = { selected = it },
        scrollable = true,
    )
}

@Preview(showBackground = true, name = "TabLayout 3. Custom colors")
@Composable
private fun PreviewTabLayoutColors() {
    var selected by remember { mutableStateOf(0) }
    KanzanTabLayout(
        tabs = listOf("Tab A", "Tab B", "Tab C"),
        selectedIndex = selected,
        onTabSelected = { selected = it },
        containerColor = Color(0xFF1E1E1E),
        selectedContentColor = Color.White,
        unselectedContentColor = Color.Gray,
    )
}

@Preview(showBackground = true, name = "TabLayout 4. With icons")
@Composable
private fun PreviewTabLayoutIcons() {
    var selected by remember { mutableStateOf(0) }
    val icons = listOf("💰", "📊", "⚙️")
    KanzanTabLayout(
        tabs = listOf("Hutang", "Laporan", "Setting"),
        selectedIndex = selected,
        onTabSelected = { selected = it },
        tabIcon = { index ->
            Text(text = icons[index], style = AppTextStyle.nunito_regular_14)
        },
    )
}

// endregion
