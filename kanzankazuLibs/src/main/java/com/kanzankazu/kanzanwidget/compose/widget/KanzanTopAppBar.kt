@file:OptIn(ExperimentalMaterial3Api::class)

package com.kanzankazu.kanzanwidget.compose.widget

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kanzankazu.R
import com.kanzankazu.kanzanwidget.compose.ui.AppTextStyle
import com.kanzankazu.kanzanwidget.compose.ui.PrimaryDarkItungItungan

/** Navigation icon type for [KanzanTopAppBar]. */
enum class KanzanNavIconType { BACK, MENU, NONE }

/** Title alignment for [KanzanTopAppBar]. */
enum class KanzanTitleAlignment { START, CENTER }

// region ==================== KanzanTopAppBar ====================

/**
 * Reusable TopAppBar with configurable navigation icon, menu icons, search bar,
 * subtitle, title alignment, elevation, color theming, scroll behavior, and bottom content.
 *
 * Uses [CenterAlignedTopAppBar] when [titleAlignment] is CENTER for true centering
 * regardless of nav icon / action icon asymmetry.
 */
@Composable
fun KanzanTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    titleAlignment: KanzanTitleAlignment = KanzanTitleAlignment.START,
    contentDescription: String = "Kembali",
    navIconType: KanzanNavIconType = KanzanNavIconType.BACK,
    titleColor: Color = Color.Black,
    iconTint: Color = Color.Black,
    containerColor: Color = Color.White,
    shadowElevation: Dp = 0.dp,
    navigationIcon: @Composable (() -> Unit)? = null,
    menuIcons: @Composable (() -> Unit)? = null,
    search: Pair<String, (String) -> Unit>? = null,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    bottomContent: @Composable (() -> Unit)? = null,
    navigateUp: () -> Unit,
) {
    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val onSearchQueryChanged = search?.second

    fun closeSearch() {
        isSearchActive = false
        searchQuery = ""
        onSearchQueryChanged?.invoke("")
    }

    val appBarModifier = if (shadowElevation > 0.dp) modifier.shadow(shadowElevation) else modifier

    val titleContent: @Composable () -> Unit = {
        if (isSearchActive) {
            KanzanTextField(
                label = "",
                value = searchQuery,
                onValueChanged = {
                    searchQuery = it
                    onSearchQueryChanged?.invoke(it)
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = search?.first ?: "",
                kanzanInputType = KanzanInputType.SEARCH,
                imeAction = ImeAction.Search,
                singleLine = true,
                leadingIcon = null,
            )
        } else {
            Column {
                Text(
                    text = title,
                    color = titleColor,
                    style = AppTextStyle.nunito_medium_16,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        color = titleColor.copy(alpha = 0.6f),
                        style = AppTextStyle.nunito_regular_12,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }

    val navIconContent: @Composable () -> Unit = {
        when {
            navigationIcon != null -> {
                if (isSearchActive) {
                    IconButton(onClick = { closeSearch() }) {
                        Icon(painterResource(R.drawable.ic_arrow_head_left), "Tutup pencarian", tint = iconTint)
                    }
                } else {
                    navigationIcon()
                }
            }
            navIconType == KanzanNavIconType.BACK -> {
                IconButton(onClick = { if (isSearchActive) closeSearch() else navigateUp() }) {
                    Icon(
                        painterResource(R.drawable.ic_arrow_head_left),
                        if (isSearchActive) "Tutup pencarian" else contentDescription,
                        tint = iconTint
                    )
                }
            }
            navIconType == KanzanNavIconType.MENU -> {
                if (isSearchActive) {
                    IconButton(onClick = { closeSearch() }) {
                        Icon(painterResource(R.drawable.ic_arrow_head_left), "Tutup pencarian", tint = iconTint)
                    }
                } else {
                    IconButton(onClick = { navigateUp() }) {
                        Text(text = "☰", style = AppTextStyle.nunito_medium_16, color = iconTint)
                    }
                }
            }
            navIconType == KanzanNavIconType.NONE && isSearchActive -> {
                IconButton(onClick = { closeSearch() }) {
                    Icon(painterResource(R.drawable.ic_arrow_head_left), "Tutup pencarian", tint = iconTint)
                }
            }
        }
    }

    val actionsContent: @Composable () -> Unit = {
        if (search != null && !isSearchActive) {
            IconButton(onClick = { isSearchActive = true }) {
                Text(text = "🔍", style = AppTextStyle.nunito_regular_16)
            }
        }
        AnimatedVisibility(
            visible = !isSearchActive,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically()
        ) {
            Row { menuIcons?.invoke() }
        }
    }

    Column {
        if (titleAlignment == KanzanTitleAlignment.CENTER) {
            CenterAlignedTopAppBar(
                title = titleContent,
                modifier = appBarModifier,
                navigationIcon = navIconContent,
                actions = { actionsContent() },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = containerColor),
                scrollBehavior = scrollBehavior
            )
        } else {
            TopAppBar(
                title = titleContent,
                modifier = appBarModifier,
                navigationIcon = navIconContent,
                actions = { actionsContent() },
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = containerColor),
                scrollBehavior = scrollBehavior
            )
        }
        bottomContent?.invoke()
    }
}
// endregion

// region ==================== Preview: 1. Basic ====================
@Preview(showBackground = true, name = "1. Default (BACK)")
@Composable
private fun PreviewDefault() {
    KanzanTopAppBar(title = "Halaman Detail", navigateUp = {})
}

@Preview(showBackground = true, name = "2. NONE (no nav icon)")
@Composable
private fun PreviewNone() {
    KanzanTopAppBar(title = "Beranda", navIconType = KanzanNavIconType.NONE, navigateUp = {})
}

@Preview(showBackground = true, name = "3. Long title")
@Composable
private fun PreviewLongTitle() {
    KanzanTopAppBar(title = "Judul yang sangat panjang sekali untuk testing overflow teks", navigateUp = {})
}
// endregion

// region ==================== Preview: 2. Subtitle ====================
@Preview(showBackground = true, name = "4. Subtitle")
@Composable
private fun PreviewSubtitle() {
    KanzanTopAppBar(title = "Hutang", subtitle = "3 hutang aktif", navigateUp = {})
}

@Preview(showBackground = true, name = "5. Subtitle + CENTER")
@Composable
private fun PreviewSubtitleCenter() {
    KanzanTopAppBar(
        title = "Arisan",
        subtitle = "Rp 1.500.000",
        titleAlignment = KanzanTitleAlignment.CENTER,
        navigateUp = {}
    )
}
// endregion

// region ==================== Preview: 3. Title Alignment ====================
@Preview(showBackground = true, name = "6. CENTER title")
@Composable
private fun PreviewCenterTitle() {
    KanzanTopAppBar(title = "Pengaturan", titleAlignment = KanzanTitleAlignment.CENTER, navigateUp = {})
}

@Preview(showBackground = true, name = "7. CENTER + menu icons")
@Composable
private fun PreviewCenterWithMenu() {
    KanzanTopAppBar(
        title = "Profil",
        titleAlignment = KanzanTitleAlignment.CENTER,
        menuIcons = { IconButton(onClick = {}) { Text(text = "⋮", style = AppTextStyle.nunito_medium_16) } },
        navigateUp = {}
    )
}

@Preview(showBackground = true, name = "8. CENTER no nav no action")
@Composable
private fun PreviewCenterNoNavNoAction() {
    KanzanTopAppBar(title = "Tentang", titleAlignment = KanzanTitleAlignment.CENTER, navIconType = KanzanNavIconType.NONE, navigateUp = {})
}
// endregion

// region ==================== Preview: 4. MENU (burger) ====================
@Preview(showBackground = true, name = "9. MENU (burger)")
@Composable
private fun PreviewMenu() {
    KanzanTopAppBar(title = "Beranda", navIconType = KanzanNavIconType.MENU, navigateUp = {})
}

@Preview(showBackground = true, name = "10. MENU + actions")
@Composable
private fun PreviewMenuWithActions() {
    KanzanTopAppBar(
        title = "Dashboard",
        navIconType = KanzanNavIconType.MENU,
        menuIcons = {
            IconButton(onClick = {}) { Text(text = "🔔", style = AppTextStyle.nunito_regular_16) }
            IconButton(onClick = {}) { Text(text = "⚙️", style = AppTextStyle.nunito_regular_16) }
        },
        navigateUp = {}
    )
}

@Preview(showBackground = true, name = "11. Custom navigationIcon")
@Composable
private fun PreviewCustomNavIcon() {
    KanzanTopAppBar(
        title = "Custom Nav",
        navigationIcon = { IconButton(onClick = {}) { Text(text = "✖", style = AppTextStyle.nunito_medium_16) } },
        navigateUp = {}
    )
}
// endregion

// region ==================== Preview: 5. Menu Icons & Badge ====================
@Preview(showBackground = true, name = "12. Multiple menu icons")
@Composable
private fun PreviewMultiMenu() {
    KanzanTopAppBar(
        title = "Arisan",
        menuIcons = {
            IconButton(onClick = {}) { Text(text = "🔔", style = AppTextStyle.nunito_regular_16) }
            IconButton(onClick = {}) { Text(text = "➕", style = AppTextStyle.nunito_regular_16) }
            IconButton(onClick = {}) { Text(text = "⋮", style = AppTextStyle.nunito_regular_16) }
        },
        navigateUp = {}
    )
}

@Preview(showBackground = true, name = "13. Badge on icon")
@Composable
private fun PreviewBadge() {
    KanzanTopAppBar(
        title = "Inbox",
        menuIcons = {
            KanzanIconBadge(count = 5) {
                IconButton(onClick = {}) { Text(text = "🔔", style = AppTextStyle.nunito_regular_16) }
            }
            IconButton(onClick = {}) { Text(text = "⋮", style = AppTextStyle.nunito_regular_16) }
        },
        navigateUp = {}
    )
}

@Preview(showBackground = true, name = "14. Badge 99+")
@Composable
private fun Preview99Badge() {
    KanzanTopAppBar(
        title = "Notifikasi",
        menuIcons = {
            KanzanIconBadge(count = 150) {
                IconButton(onClick = {}) { Text(text = "🔔", style = AppTextStyle.nunito_regular_16) }
            }
        },
        navigateUp = {}
    )
}
// endregion

// region ==================== Preview: 6. Search ====================
@Preview(showBackground = true, name = "15. Search (BACK)")
@Composable
private fun PreviewSearch() {
    KanzanTopAppBar(title = "Daftar Hutang", search = "Cari..." to {}, navigateUp = {})
}

@Preview(showBackground = true, name = "16. Search + menu icons")
@Composable
private fun PreviewSearchWithMenu() {
    KanzanTopAppBar(
        title = "Anggaran",
        search = "Cari anggaran..." to {},
        menuIcons = { IconButton(onClick = {}) { Text(text = "➕", style = AppTextStyle.nunito_regular_16) } },
        navigateUp = {}
    )
}

@Preview(showBackground = true, name = "17. MENU + search")
@Composable
private fun PreviewMenuWithSearch() {
    KanzanTopAppBar(title = "Hutang", navIconType = KanzanNavIconType.MENU, search = "Cari hutang..." to {}, navigateUp = {})
}
// endregion

// region ==================== Preview: 7. Colors & Elevation ====================
@Preview(showBackground = true, name = "18. Custom colors (dark)")
@Composable
private fun PreviewCustomColors() {
    KanzanTopAppBar(title = "Dark Theme", containerColor = Color.DarkGray, titleColor = Color.White, iconTint = Color.White, navigateUp = {})
}

@Preview(showBackground = true, name = "19. Primary color")
@Composable
private fun PreviewPrimaryColor() {
    KanzanTopAppBar(title = "Pengaturan", containerColor = PrimaryDarkItungItungan, navigateUp = {})
}

@Preview(showBackground = true, name = "20. Shadow elevation")
@Composable
private fun PreviewElevation() {
    KanzanTopAppBar(title = "Elevated", shadowElevation = 8.dp, navigateUp = {})
}
// endregion

// region ==================== Preview: 8. Bottom Content (TabBar + ChipBar) ====================
@Preview(showBackground = true, name = "21. TopAppBar + TabBar")
@Composable
private fun PreviewWithTabBar() {
    var selected by remember { mutableStateOf(1) }
    KanzanTopAppBar(
        title = "Money Manager",
        navIconType = KanzanNavIconType.MENU,
        bottomContent = {
            KanzanTabBar(
                items = listOf("Harian", "Kalender", "Bulanan", "Total"),
                selectedIndex = selected,
                onTabSelected = { selected = it }
            )
        },
        navigateUp = {}
    )
}

@Preview(showBackground = true, name = "22. TopAppBar + ChipBar")
@Composable
private fun PreviewWithChipBar() {
    var selected by remember { mutableStateOf(1) }
    KanzanTopAppBar(
        title = "Hutang",
        subtitle = "Filter aktif",
        search = "Cari..." to {},
        bottomContent = {
            KanzanChipBar(
                items = listOf("Semua", "Belum Lunas", "Lunas", "Jatuh Tempo"),
                selectedIndex = selected,
                onChipSelected = { selected = it }
            )
        },
        navigateUp = {}
    )
}
// endregion

// region ==================== Preview: 9. Full Combo ====================
@Preview(showBackground = true, name = "23. Full combo")
@Composable
private fun PreviewFullCombo() {
    var tabIndex by remember { mutableStateOf(0) }
    KanzanTopAppBar(
        title = "Dashboard",
        subtitle = "Rp 15.000.000",
        titleAlignment = KanzanTitleAlignment.START,
        navIconType = KanzanNavIconType.MENU,
        containerColor = PrimaryDarkItungItungan,
        shadowElevation = 4.dp,
        search = "Cari transaksi..." to {},
        menuIcons = {
            KanzanIconBadge(count = 3) {
                IconButton(onClick = {}) { Text(text = "🔔", style = AppTextStyle.nunito_regular_16) }
            }
            IconButton(onClick = {}) { Text(text = "⋮", style = AppTextStyle.nunito_regular_16) }
        },
        bottomContent = {
            KanzanTabBar(
                items = listOf("Harian", "Kalender", "Bulanan", "Total"),
                selectedIndex = tabIndex,
                onTabSelected = { tabIndex = it },
                containerColor = PrimaryDarkItungItungan
            )
        },
        navigateUp = {}
    )
}
// endregion