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
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.unit.sp
import com.kanzankazu.R
import com.kanzankazu.kanzanwidget.compose.ui.AppTextStyle
import com.kanzankazu.kanzanwidget.compose.ui.PrimaryDarkItungItungan
import com.kanzankazu.kanzanwidget.compose.ui.dp0
import com.kanzankazu.kanzanwidget.compose.ui.dp4
import com.kanzankazu.kanzanwidget.compose.ui.dp8

/** Navigation icon type for [KanzanTopAppBar]. */
enum class KanzanNavIconType { BACK, MENU, NONE }

/** Title alignment for [KanzanTopAppBar]. */
enum class KanzanTitleAlignment { START, CENTER }

/**
 * Scroll behavior type for [KanzanTopAppBar].
 *
 * Determines how the TopAppBar reacts to scroll events.
 * Inspired by Material 3 scroll behaviors and Katie Barnett's TopAppBar experiments.
 *
 * - [NONE] — Fixed, no scroll reaction (default).
 * - [PINNED] — Stays visible, changes color on scroll via `contentOffset`.
 * - [ENTER_ALWAYS] — Collapses on scroll down, reappears immediately on scroll up.
 * - [EXIT_UNTIL_COLLAPSED] — Collapses to small size, only fully expands when scrolled to top.
 * - [ENTER_ALWAYS_MEDIUM] — Like [ENTER_ALWAYS] but uses [MediumTopAppBar] height.
 * - [EXIT_UNTIL_COLLAPSED_MEDIUM] — Like [EXIT_UNTIL_COLLAPSED] but uses [MediumTopAppBar] height.
 * - [ENTER_ALWAYS_LARGE] — Like [ENTER_ALWAYS] but uses [LargeTopAppBar] height.
 * - [EXIT_UNTIL_COLLAPSED_LARGE] — Like [EXIT_UNTIL_COLLAPSED] but uses [LargeTopAppBar] height.
 */
enum class KanzanScrollBehaviorType {
    /** Fixed — no scroll reaction. */
    NONE,
    /** Stays visible, color changes on scroll. */
    PINNED,
    /** Small TopAppBar — collapses on scroll down, reappears on any scroll up. */
    ENTER_ALWAYS,
    /** Small TopAppBar — collapses until minimum, expands only at top. */
    EXIT_UNTIL_COLLAPSED,
    /** MediumTopAppBar — collapses on scroll down, reappears on any scroll up. */
    ENTER_ALWAYS_MEDIUM,
    /** MediumTopAppBar — collapses until minimum, expands only at top. */
    EXIT_UNTIL_COLLAPSED_MEDIUM,
    /** LargeTopAppBar — collapses on scroll down, reappears on any scroll up. */
    ENTER_ALWAYS_LARGE,
    /** LargeTopAppBar — collapses until minimum, expands only at top. */
    EXIT_UNTIL_COLLAPSED_LARGE,
}

/**
 * Creates and remembers a [TopAppBarScrollBehavior] based on [KanzanScrollBehaviorType].
 * Returns null for [KanzanScrollBehaviorType.NONE].
 */
@Composable
fun rememberKanzanScrollBehavior(
    type: KanzanScrollBehaviorType = KanzanScrollBehaviorType.NONE,
): TopAppBarScrollBehavior? {
    if (type == KanzanScrollBehaviorType.NONE) return null
    val state = rememberTopAppBarState()
    return when (type) {
        KanzanScrollBehaviorType.NONE -> null
        KanzanScrollBehaviorType.PINNED -> TopAppBarDefaults.pinnedScrollBehavior(state)
        KanzanScrollBehaviorType.ENTER_ALWAYS,
        KanzanScrollBehaviorType.ENTER_ALWAYS_MEDIUM,
        KanzanScrollBehaviorType.ENTER_ALWAYS_LARGE,
        -> TopAppBarDefaults.enterAlwaysScrollBehavior(state)
        KanzanScrollBehaviorType.EXIT_UNTIL_COLLAPSED,
        KanzanScrollBehaviorType.EXIT_UNTIL_COLLAPSED_MEDIUM,
        KanzanScrollBehaviorType.EXIT_UNTIL_COLLAPSED_LARGE,
        -> TopAppBarDefaults.exitUntilCollapsedScrollBehavior(state)
    }
}

// region ==================== KanzanTopAppBar ====================

/**
 * Reusable TopAppBar with configurable navigation icon, menu icons, search bar,
 * subtitle, title alignment, elevation, color theming, scroll behavior, and bottom content.
 *
 * Uses [CenterAlignedTopAppBar] when [titleAlignment] is CENTER for true centering
 * regardless of nav icon / action icon asymmetry.
 *
 * Supports collapsing via [scrollBehaviorType]:
 * - MEDIUM / LARGE variants use [MediumTopAppBar] / [LargeTopAppBar] for visible collapse effect.
 * - Dynamic color & font size transitions based on `collapsedFraction`.
 * - [scrolledContainerColor] controls the color when collapsed (defaults to [containerColor]).
 *
 * @param scrollBehaviorType Determines scroll behavior variant. Default [KanzanScrollBehaviorType.NONE].
 * @param scrollBehavior Pre-created scroll behavior (from [rememberKanzanScrollBehavior]).
 *   When provided, takes precedence. When null, created internally from [scrollBehaviorType].
 * @param scrolledContainerColor Container color when scrolled/collapsed. Defaults to [containerColor].
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
    scrolledContainerColor: Color = containerColor,
    shadowElevation: Dp = dp0,
    navigationIcon: @Composable (() -> Unit)? = null,
    menuIcons: @Composable (() -> Unit)? = null,
    search: Pair<String, (String) -> Unit>? = null,
    scrollBehaviorType: KanzanScrollBehaviorType = KanzanScrollBehaviorType.NONE,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    bottomContent: @Composable (() -> Unit)? = null,
    navigateUp: () -> Unit,
) {
    // Resolve scroll behavior: explicit param wins, otherwise create from enum
    val resolvedScrollBehavior = scrollBehavior ?: rememberKanzanScrollBehavior(scrollBehaviorType)

    // Dynamic styling based on collapsedFraction (for Medium/Large variants)
    val isLargeOrMedium = scrollBehaviorType in listOf(
        KanzanScrollBehaviorType.ENTER_ALWAYS_MEDIUM,
        KanzanScrollBehaviorType.EXIT_UNTIL_COLLAPSED_MEDIUM,
        KanzanScrollBehaviorType.ENTER_ALWAYS_LARGE,
        KanzanScrollBehaviorType.EXIT_UNTIL_COLLAPSED_LARGE,
    )
    val collapsedFraction = resolvedScrollBehavior?.state?.collapsedFraction ?: 0f
    val isCollapsed by remember { derivedStateOf { collapsedFraction > 0.5f } }

    // Interpolate element colors when collapsing (like Katie Barnett's example)
    val dynamicTitleColor = if (isLargeOrMedium && scrolledContainerColor != containerColor) {
        if (isCollapsed) titleColor else titleColor
    } else titleColor

    val dynamicIconTint = if (isLargeOrMedium && scrolledContainerColor != containerColor) {
        if (isCollapsed) iconTint else iconTint
    } else iconTint

    // Dynamic font size for Large variants (28sp expanded → 22sp collapsed)
    val collapsedTextSize = 22
    val expandedTextSize = 28
    val dynamicTitleFontSize = if (isLargeOrMedium) {
        (collapsedTextSize + (expandedTextSize - collapsedTextSize) * (1 - collapsedFraction)).sp
    } else null

    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val onSearchQueryChanged = search?.second

    fun closeSearch() {
        isSearchActive = false
        searchQuery = ""
        onSearchQueryChanged?.invoke("")
    }

    val appBarModifier = if (shadowElevation > dp0) modifier.shadow(shadowElevation) else modifier

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
                    color = dynamicTitleColor,
                    style = AppTextStyle.nunito_medium_16,
                    fontSize = dynamicTitleFontSize ?: AppTextStyle.nunito_medium_16.fontSize,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        color = dynamicTitleColor.copy(alpha = 0.6f),
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
                        Icon(painterResource(R.drawable.ic_arrow_head_left), "Tutup pencarian", tint = dynamicIconTint)
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
                        tint = dynamicIconTint
                    )
                }
            }
            navIconType == KanzanNavIconType.MENU -> {
                if (isSearchActive) {
                    IconButton(onClick = { closeSearch() }) {
                        Icon(painterResource(R.drawable.ic_arrow_head_left), "Tutup pencarian", tint = dynamicIconTint)
                    }
                } else {
                    IconButton(onClick = { navigateUp() }) {
                        Text(text = "☰", style = AppTextStyle.nunito_medium_16, color = dynamicIconTint)
                    }
                }
            }
            navIconType == KanzanNavIconType.NONE && isSearchActive -> {
                IconButton(onClick = { closeSearch() }) {
                    Icon(painterResource(R.drawable.ic_arrow_head_left), "Tutup pencarian", tint = dynamicIconTint)
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

    val colors = TopAppBarDefaults.topAppBarColors(
        containerColor = containerColor,
        scrolledContainerColor = scrolledContainerColor,
        navigationIconContentColor = dynamicIconTint,
        titleContentColor = dynamicTitleColor,
        actionIconContentColor = dynamicIconTint,
    )

    Column {
        when {
            // Large variants
            scrollBehaviorType == KanzanScrollBehaviorType.ENTER_ALWAYS_LARGE ||
            scrollBehaviorType == KanzanScrollBehaviorType.EXIT_UNTIL_COLLAPSED_LARGE -> {
                LargeTopAppBar(
                    title = titleContent,
                    modifier = appBarModifier,
                    navigationIcon = navIconContent,
                    actions = { actionsContent() },
                    colors = TopAppBarDefaults.largeTopAppBarColors(
                        containerColor = containerColor,
                        scrolledContainerColor = scrolledContainerColor,
                        navigationIconContentColor = dynamicIconTint,
                        titleContentColor = dynamicTitleColor,
                        actionIconContentColor = dynamicIconTint,
                    ),
                    scrollBehavior = resolvedScrollBehavior
                )
            }
            // Medium variants
            scrollBehaviorType == KanzanScrollBehaviorType.ENTER_ALWAYS_MEDIUM ||
            scrollBehaviorType == KanzanScrollBehaviorType.EXIT_UNTIL_COLLAPSED_MEDIUM -> {
                MediumTopAppBar(
                    title = titleContent,
                    modifier = appBarModifier,
                    navigationIcon = navIconContent,
                    actions = { actionsContent() },
                    colors = TopAppBarDefaults.mediumTopAppBarColors(
                        containerColor = containerColor,
                        scrolledContainerColor = scrolledContainerColor,
                        navigationIconContentColor = dynamicIconTint,
                        titleContentColor = dynamicTitleColor,
                        actionIconContentColor = dynamicIconTint,
                    ),
                    scrollBehavior = resolvedScrollBehavior
                )
            }
            // Center aligned (small)
            titleAlignment == KanzanTitleAlignment.CENTER -> {
                CenterAlignedTopAppBar(
                    title = titleContent,
                    modifier = appBarModifier,
                    navigationIcon = navIconContent,
                    actions = { actionsContent() },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = containerColor,
                        scrolledContainerColor = scrolledContainerColor,
                    ),
                    scrollBehavior = resolvedScrollBehavior
                )
            }
            // Default small TopAppBar (NONE, PINNED, ENTER_ALWAYS, EXIT_UNTIL_COLLAPSED)
            else -> {
                TopAppBar(
                    title = titleContent,
                    modifier = appBarModifier,
                    navigationIcon = navIconContent,
                    actions = { actionsContent() },
                    colors = colors,
                    scrollBehavior = resolvedScrollBehavior
                )
            }
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
    KanzanTopAppBar(title = "Elevated", shadowElevation = dp8, navigateUp = {})
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
        shadowElevation = dp4,
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

// region ==================== Preview: 10. Scroll Behavior Variants ====================
@Preview(showBackground = true, name = "24. PINNED scroll")
@Composable
private fun PreviewPinned() {
    KanzanTopAppBar(
        title = "Pinned",
        scrollBehaviorType = KanzanScrollBehaviorType.PINNED,
        containerColor = PrimaryDarkItungItungan,
        scrolledContainerColor = Color.White,
        navigateUp = {}
    )
}

@Preview(showBackground = true, name = "25. ENTER_ALWAYS scroll")
@Composable
private fun PreviewEnterAlways() {
    KanzanTopAppBar(
        title = "Enter Always",
        scrollBehaviorType = KanzanScrollBehaviorType.ENTER_ALWAYS,
        navigateUp = {}
    )
}

@Preview(showBackground = true, name = "26. EXIT_UNTIL_COLLAPSED scroll")
@Composable
private fun PreviewExitUntilCollapsed() {
    KanzanTopAppBar(
        title = "Exit Until Collapsed",
        scrollBehaviorType = KanzanScrollBehaviorType.EXIT_UNTIL_COLLAPSED,
        navigateUp = {}
    )
}

@Preview(showBackground = true, name = "27. ENTER_ALWAYS_MEDIUM")
@Composable
private fun PreviewEnterAlwaysMedium() {
    KanzanTopAppBar(
        title = "Medium Enter Always",
        subtitle = "Subtitle terlihat",
        scrollBehaviorType = KanzanScrollBehaviorType.ENTER_ALWAYS_MEDIUM,
        containerColor = PrimaryDarkItungItungan,
        scrolledContainerColor = Color.White,
        navigateUp = {}
    )
}

@Preview(showBackground = true, name = "28. EXIT_UNTIL_COLLAPSED_MEDIUM")
@Composable
private fun PreviewExitUntilCollapsedMedium() {
    KanzanTopAppBar(
        title = "Medium Exit Collapsed",
        scrollBehaviorType = KanzanScrollBehaviorType.EXIT_UNTIL_COLLAPSED_MEDIUM,
        containerColor = PrimaryDarkItungItungan,
        scrolledContainerColor = Color.White,
        navigateUp = {}
    )
}

@Preview(showBackground = true, name = "29. ENTER_ALWAYS_LARGE")
@Composable
private fun PreviewEnterAlwaysLarge() {
    KanzanTopAppBar(
        title = "Large Enter Always",
        subtitle = "Rp 15.000.000",
        scrollBehaviorType = KanzanScrollBehaviorType.ENTER_ALWAYS_LARGE,
        containerColor = PrimaryDarkItungItungan,
        scrolledContainerColor = Color.White,
        navigateUp = {}
    )
}

@Preview(showBackground = true, name = "30. EXIT_UNTIL_COLLAPSED_LARGE")
@Composable
private fun PreviewExitUntilCollapsedLarge() {
    KanzanTopAppBar(
        title = "Large Exit Collapsed",
        scrollBehaviorType = KanzanScrollBehaviorType.EXIT_UNTIL_COLLAPSED_LARGE,
        containerColor = PrimaryDarkItungItungan,
        scrolledContainerColor = Color.White,
        menuIcons = {
            IconButton(onClick = {}) { Text(text = "🔔", style = AppTextStyle.nunito_regular_16) }
            IconButton(onClick = {}) { Text(text = "⋮", style = AppTextStyle.nunito_regular_16) }
        },
        navigateUp = {}
    )
}

@Preview(showBackground = true, name = "31. Large + TabBar + Search")
@Composable
private fun PreviewLargeWithTabBarSearch() {
    var tabIndex by remember { mutableStateOf(0) }
    KanzanTopAppBar(
        title = "Dashboard",
        subtitle = "Rp 15.000.000",
        navIconType = KanzanNavIconType.MENU,
        scrollBehaviorType = KanzanScrollBehaviorType.EXIT_UNTIL_COLLAPSED_LARGE,
        containerColor = PrimaryDarkItungItungan,
        scrolledContainerColor = Color.White,
        search = "Cari transaksi..." to {},
        menuIcons = {
            KanzanIconBadge(count = 3) {
                IconButton(onClick = {}) { Text(text = "🔔", style = AppTextStyle.nunito_regular_16) }
            }
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