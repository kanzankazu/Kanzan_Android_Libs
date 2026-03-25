@file:OptIn(ExperimentalMaterial3Api::class)

package com.kanzankazu.kanzanwidget.compose.widget

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kanzankazu.kanzanwidget.compose.ui.AppTextStyle
import com.kanzankazu.kanzanwidget.compose.ui.PrimaryDarkItungItungan
import com.kanzankazu.kanzanwidget.compose.ui.dp4
import com.kanzankazu.kanzanwidget.compose.ui.dp16

// region ==================== KanzanBaseScreen ====================

/**
 * Reusable base screen scaffold. All slots are pure `@Composable (() -> Unit)?`
 * so callers decide what goes in each area — no internal dependency on
 * [KanzanTopAppBar] or any other custom component.
 *
 * @param modifier Modifier for the Scaffold
 * @param topBar Top bar composable (e.g. KanzanTopAppBar, custom header, or null)
 * @param bottomBar Bottom bar composable (e.g. BottomNavigation, KanzanBottomNav)
 * @param floatingActionButton FAB composable
 * @param floatingActionButtonPosition FAB position (default: End)
 * @param snackbarHostState SnackbarHostState for showing snackbars
 * @param isLoading Show loading overlay (animated fade in/out)
 * @param loadingContent Custom loading composable (default: CircularProgressIndicator)
 * @param emptyContent When non-null, shown instead of [content] (empty state)
 * @param scrollBehaviorType Enum to create scroll behavior internally. Default [KanzanScrollBehaviorType.NONE].
 * @param scrollBehavior Pre-created scroll behavior. When provided, takes precedence over [scrollBehaviorType].
 * @param containerColor Scaffold background color
 * @param scrollable When true, wraps content in a vertical scroll. Default false. Jangan aktifkan jika content sudah pakai LazyColumn/LazyRow.
 * @param content Main screen content receiving PaddingValues from Scaffold
 */
@Composable
fun KanzanBaseScreen(
    modifier: Modifier = Modifier,
    topBar: @Composable (() -> Unit)? = null,
    bottomBar: @Composable (() -> Unit)? = null,
    floatingActionButton: @Composable (() -> Unit)? = null,
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    isLoading: Boolean = false,
    loadingContent: @Composable (() -> Unit)? = null,
    emptyContent: @Composable (() -> Unit)? = null,
    scrollBehaviorType: KanzanScrollBehaviorType = KanzanScrollBehaviorType.NONE,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    containerColor: Color = MaterialTheme.colorScheme.background,
    scrollable: Boolean = false,
    content: @Composable (PaddingValues) -> Unit,
) {
    val resolvedScrollBehavior = scrollBehavior ?: rememberKanzanScrollBehavior(scrollBehaviorType)

    val scaffoldModifier = if (resolvedScrollBehavior != null) {
        modifier.nestedScroll(resolvedScrollBehavior.nestedScrollConnection)
    } else {
        modifier
    }

    Scaffold(
        modifier = scaffoldModifier,
        topBar = { topBar?.invoke() },
        bottomBar = { bottomBar?.invoke() },
        floatingActionButton = { floatingActionButton?.invoke() },
        floatingActionButtonPosition = floatingActionButtonPosition,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(snackbarData = data)
            }
        },
        containerColor = containerColor,
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Main content or empty state
            if (emptyContent != null) {
                emptyContent()
            } else {
                val contentModifier = if (scrollable) {
                    Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                } else {
                    Modifier.fillMaxSize()
                }
                Box(modifier = contentModifier) {
                    content(paddingValues)
                }
            }

            // Loading overlay
            AnimatedVisibility(
                visible = isLoading,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (loadingContent != null) {
                        loadingContent()
                    } else {
                        androidx.compose.material3.CircularProgressIndicator()
                    }
                }
            }
        }
    }
}
// endregion

// region ==================== Preview: 9. Scroll Behavior Variants ====================
@Preview(showBackground = true, name = "BaseScreen 18. Pinned scroll")
@Composable
private fun PreviewBaseScreenPinned() {
    val scrollBehavior = rememberKanzanScrollBehavior(KanzanScrollBehaviorType.PINNED)
    KanzanBaseScreen(
        scrollBehavior = scrollBehavior,
        topBar = {
            KanzanTopAppBar(
                title = "Pinned",
                containerColor = PrimaryDarkItungItungan,
                scrolledContainerColor = Color.White,
                scrollBehavior = scrollBehavior,
                navigateUp = {}
            )
        }
    ) { padding ->
        Text(
            text = "Scroll untuk lihat perubahan warna",
            modifier = Modifier.padding(padding).padding(dp16),
            style = AppTextStyle.nunito_regular_16
        )
    }
}

@Preview(showBackground = true, name = "BaseScreen 19. Enter Always")
@Composable
private fun PreviewBaseScreenEnterAlways() {
    val scrollBehavior = rememberKanzanScrollBehavior(KanzanScrollBehaviorType.ENTER_ALWAYS)
    KanzanBaseScreen(
        scrollBehavior = scrollBehavior,
        topBar = {
            KanzanTopAppBar(
                title = "Enter Always",
                scrollBehavior = scrollBehavior,
                navigateUp = {}
            )
        }
    ) { padding ->
        Text(
            text = "Scroll down untuk collapse, scroll up untuk muncul",
            modifier = Modifier.padding(padding).padding(dp16),
            style = AppTextStyle.nunito_regular_16
        )
    }
}

@Preview(showBackground = true, name = "BaseScreen 20. Exit Until Collapsed Large")
@Composable
private fun PreviewBaseScreenExitUntilCollapsedLarge() {
    val scrollBehavior = rememberKanzanScrollBehavior(KanzanScrollBehaviorType.EXIT_UNTIL_COLLAPSED_LARGE)
    KanzanBaseScreen(
        scrollBehavior = scrollBehavior,
        topBar = {
            KanzanTopAppBar(
                title = "Large Collapsing",
                subtitle = "Rp 15.000.000",
                scrollBehaviorType = KanzanScrollBehaviorType.EXIT_UNTIL_COLLAPSED_LARGE,
                scrollBehavior = scrollBehavior,
                containerColor = PrimaryDarkItungItungan,
                scrolledContainerColor = Color.White,
                navigateUp = {}
            )
        }
    ) { padding ->
        Text(
            text = "Large TopAppBar collapse ke ukuran kecil",
            modifier = Modifier.padding(padding).padding(dp16),
            style = AppTextStyle.nunito_regular_16
        )
    }
}

@Preview(showBackground = true, name = "BaseScreen 21. Medium Enter Always + TabBar")
@Composable
private fun PreviewBaseScreenMediumEnterAlwaysTabBar() {
    var tabIndex by remember { mutableStateOf(0) }
    val scrollBehavior = rememberKanzanScrollBehavior(KanzanScrollBehaviorType.ENTER_ALWAYS_MEDIUM)
    KanzanBaseScreen(
        scrollBehavior = scrollBehavior,
        topBar = {
            KanzanTopAppBar(
                title = "Money Manager",
                navIconType = KanzanNavIconType.MENU,
                scrollBehaviorType = KanzanScrollBehaviorType.ENTER_ALWAYS_MEDIUM,
                scrollBehavior = scrollBehavior,
                containerColor = PrimaryDarkItungItungan,
                scrolledContainerColor = Color.White,
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
    ) { padding ->
        Text(
            text = "Tab: $tabIndex",
            modifier = Modifier.padding(padding).padding(dp16),
            style = AppTextStyle.nunito_regular_16
        )
    }
}

@Preview(showBackground = true, name = "BaseScreen 22. Full combo + Large collapsing")
@Composable
private fun PreviewBaseScreenFullComboCollapsing() {
    var tabIndex by remember { mutableStateOf(0) }
    val scrollBehavior = rememberKanzanScrollBehavior(KanzanScrollBehaviorType.EXIT_UNTIL_COLLAPSED_LARGE)
    KanzanBaseScreen(
        scrollBehavior = scrollBehavior,
        topBar = {
            KanzanTopAppBar(
                title = "Dashboard",
                subtitle = "Rp 15.000.000",
                navIconType = KanzanNavIconType.MENU,
                scrollBehaviorType = KanzanScrollBehaviorType.EXIT_UNTIL_COLLAPSED_LARGE,
                scrollBehavior = scrollBehavior,
                containerColor = PrimaryDarkItungItungan,
                scrolledContainerColor = Color.White,
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
        },
        floatingActionButton = {
            androidx.compose.material3.FloatingActionButton(
                onClick = {},
                containerColor = PrimaryDarkItungItungan,
                contentColor = Color.Black,
            ) {
                Text(text = "➕", style = AppTextStyle.nunito_regular_16)
            }
        },
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(dp16)) {
            Text(text = "Tab aktif: $tabIndex", style = AppTextStyle.nunito_medium_14)
            Text(text = "Large collapsing + full combo", style = AppTextStyle.nunito_regular_14, color = Color.Gray)
        }
    }
}
// endregion// region ==================== Preview: 1. Basic ====================
@Preview(showBackground = true, name = "BaseScreen 1. Minimal (no topBar)")
@Composable
private fun PreviewBaseScreenMinimal() {
    KanzanBaseScreen { padding ->
        Text(
            text = "Konten tanpa topBar",
            modifier = Modifier.padding(padding).padding(dp16),
            style = AppTextStyle.nunito_regular_16
        )
    }
}

@Preview(showBackground = true, name = "BaseScreen 2. With KanzanTopAppBar")
@Composable
private fun PreviewBaseScreenWithTopAppBar() {
    KanzanBaseScreen(
        topBar = {
            KanzanTopAppBar(title = "Halaman Detail", navigateUp = {})
        }
    ) { padding ->
        Text(
            text = "Konten halaman",
            modifier = Modifier.padding(padding).padding(dp16),
            style = AppTextStyle.nunito_regular_16
        )
    }
}

@Preview(showBackground = true, name = "BaseScreen 3. MENU nav")
@Composable
private fun PreviewBaseScreenMenu() {
    KanzanBaseScreen(
        topBar = {
            KanzanTopAppBar(
                title = "Dashboard",
                navIconType = KanzanNavIconType.MENU,
                navigateUp = {}
            )
        }
    ) { padding ->
        Text(
            text = "Menu navigation",
            modifier = Modifier.padding(padding).padding(dp16),
            style = AppTextStyle.nunito_regular_16
        )
    }
}
// endregion

// region ==================== Preview: 2. Subtitle & Alignment ====================
@Preview(showBackground = true, name = "BaseScreen 4. Subtitle")
@Composable
private fun PreviewBaseScreenSubtitle() {
    KanzanBaseScreen(
        topBar = {
            KanzanTopAppBar(title = "Hutang", subtitle = "3 hutang aktif", navigateUp = {})
        }
    ) { padding ->
        Text(
            text = "Daftar hutang",
            modifier = Modifier.padding(padding).padding(dp16),
            style = AppTextStyle.nunito_regular_16
        )
    }
}

@Preview(showBackground = true, name = "BaseScreen 5. CENTER title")
@Composable
private fun PreviewBaseScreenCenter() {
    KanzanBaseScreen(
        topBar = {
            KanzanTopAppBar(
                title = "Pengaturan",
                titleAlignment = KanzanTitleAlignment.CENTER,
                navigateUp = {}
            )
        }
    ) { padding ->
        Text(
            text = "Konten pengaturan",
            modifier = Modifier.padding(padding).padding(dp16),
            style = AppTextStyle.nunito_regular_16
        )
    }
}
// endregion

// region ==================== Preview: 3. Search & Menu Icons ====================
@Preview(showBackground = true, name = "BaseScreen 6. Search")
@Composable
private fun PreviewBaseScreenSearch() {
    KanzanBaseScreen(
        topBar = {
            KanzanTopAppBar(title = "Daftar Hutang", search = "Cari hutang..." to {}, navigateUp = {})
        }
    ) { padding ->
        Text(
            text = "Hasil pencarian",
            modifier = Modifier.padding(padding).padding(dp16),
            style = AppTextStyle.nunito_regular_16
        )
    }
}

@Preview(showBackground = true, name = "BaseScreen 7. Menu icons + badge")
@Composable
private fun PreviewBaseScreenMenuIcons() {
    KanzanBaseScreen(
        topBar = {
            KanzanTopAppBar(
                title = "Arisan",
                menuIcons = {
                    KanzanIconBadge(count = 5) {
                        IconButton(onClick = {}) { Text(text = "🔔", style = AppTextStyle.nunito_regular_16) }
                    }
                    IconButton(onClick = {}) { Text(text = "⋮", style = AppTextStyle.nunito_regular_16) }
                },
                navigateUp = {}
            )
        }
    ) { padding ->
        Text(
            text = "Daftar arisan",
            modifier = Modifier.padding(padding).padding(dp16),
            style = AppTextStyle.nunito_regular_16
        )
    }
}
// endregion

// region ==================== Preview: 4. FAB ====================
@Preview(showBackground = true, name = "BaseScreen 8. FAB")
@Composable
private fun PreviewBaseScreenFab() {
    KanzanBaseScreen(
        topBar = {
            KanzanTopAppBar(title = "Transaksi", navigateUp = {})
        },
        floatingActionButton = {
            androidx.compose.material3.FloatingActionButton(onClick = {}) {
                Text(text = "➕", style = AppTextStyle.nunito_regular_16)
            }
        }
    ) { padding ->
        Text(
            text = "Daftar transaksi",
            modifier = Modifier.padding(padding).padding(dp16),
            style = AppTextStyle.nunito_regular_16
        )
    }
}

@Preview(showBackground = true, name = "BaseScreen 9. FAB custom color")
@Composable
private fun PreviewBaseScreenFabCustomColor() {
    KanzanBaseScreen(
        topBar = {
            KanzanTopAppBar(title = "Anggaran", navigateUp = {})
        },
        floatingActionButton = {
            androidx.compose.material3.FloatingActionButton(
                onClick = {},
                containerColor = PrimaryDarkItungItungan,
                contentColor = Color.Black,
            ) {
                Text(text = "➕", style = AppTextStyle.nunito_regular_16)
            }
        }
    ) { padding ->
        Text(
            text = "Daftar anggaran",
            modifier = Modifier.padding(padding).padding(dp16),
            style = AppTextStyle.nunito_regular_16
        )
    }
}
// endregion

// region ==================== Preview: 5. Loading & Empty ====================
@Preview(showBackground = true, name = "BaseScreen 10. Loading")
@Composable
private fun PreviewBaseScreenLoading() {
    KanzanBaseScreen(
        topBar = {
            KanzanTopAppBar(title = "Memuat...", navigateUp = {})
        },
        isLoading = true,
    ) { padding ->
        Text(
            text = "Konten di belakang loading",
            modifier = Modifier.padding(padding).padding(dp16),
            style = AppTextStyle.nunito_regular_16
        )
    }
}

@Preview(showBackground = true, name = "BaseScreen 11. Custom loading")
@Composable
private fun PreviewBaseScreenCustomLoading() {
    KanzanBaseScreen(
        topBar = {
            KanzanTopAppBar(title = "Memuat...", navigateUp = {})
        },
        isLoading = true,
        loadingContent = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "⏳", style = AppTextStyle.nunito_regular_16)
                Text(text = "Mohon tunggu...", style = AppTextStyle.nunito_regular_14, color = Color.Gray)
            }
        },
    ) { padding ->
        Text(
            text = "Konten",
            modifier = Modifier.padding(padding).padding(dp16),
            style = AppTextStyle.nunito_regular_16
        )
    }
}

@Preview(showBackground = true, name = "BaseScreen 12. Empty state")
@Composable
private fun PreviewBaseScreenEmpty() {
    KanzanBaseScreen(
        topBar = {
            KanzanTopAppBar(title = "Hutang", navigateUp = {})
        },
        emptyContent = {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "📭", style = AppTextStyle.nunito_regular_16)
                    Text(text = "Belum ada hutang", style = AppTextStyle.nunito_medium_14, color = Color.Gray)
                }
            }
        },
    ) { _ -> }
}
// endregion

// region ==================== Preview: 6. TopBar Bottom Content (TabBar + ChipBar) ====================
@Preview(showBackground = true, name = "BaseScreen 13. TabBar")
@Composable
private fun PreviewBaseScreenTabBar() {
    var selected by remember { mutableStateOf(0) }
    KanzanBaseScreen(
        topBar = {
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
    ) { padding ->
        Text(
            text = "Tab: $selected",
            modifier = Modifier.padding(padding).padding(dp16),
            style = AppTextStyle.nunito_regular_16
        )
    }
}

@Preview(showBackground = true, name = "BaseScreen 14. ChipBar")
@Composable
private fun PreviewBaseScreenChipBar() {
    var selected by remember { mutableStateOf(0) }
    KanzanBaseScreen(
        topBar = {
            KanzanTopAppBar(
                title = "Hutang",
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
    ) { padding ->
        Text(
            text = "Filter: $selected",
            modifier = Modifier.padding(padding).padding(dp16),
            style = AppTextStyle.nunito_regular_16
        )
    }
}
// endregion

// region ==================== Preview: 7. Custom Colors & Elevation ====================
@Preview(showBackground = true, name = "BaseScreen 15. Custom colors")
@Composable
private fun PreviewBaseScreenCustomColors() {
    KanzanBaseScreen(
        topBar = {
            KanzanTopAppBar(
                title = "Dark Theme",
                containerColor = Color.DarkGray,
                titleColor = Color.White,
                iconTint = Color.White,
                navigateUp = {}
            )
        },
        containerColor = Color(0xFF1E1E1E),
    ) { padding ->
        Text(
            text = "Dark content",
            modifier = Modifier.padding(padding).padding(dp16),
            style = AppTextStyle.nunito_regular_16,
            color = Color.White
        )
    }
}

@Preview(showBackground = true, name = "BaseScreen 16. Primary + elevation")
@Composable
private fun PreviewBaseScreenPrimaryElevation() {
    KanzanBaseScreen(
        topBar = {
            KanzanTopAppBar(
                title = "Pengaturan",
                containerColor = PrimaryDarkItungItungan,
                shadowElevation = dp4,
                navigateUp = {}
            )
        }
    ) { padding ->
        Text(
            text = "Konten",
            modifier = Modifier.padding(padding).padding(dp16),
            style = AppTextStyle.nunito_regular_16
        )
    }
}
// endregion

// region ==================== Preview: 8. Full Combo ====================
@Preview(showBackground = true, name = "BaseScreen 17. Full combo")
@Composable
private fun PreviewBaseScreenFullCombo() {
    var tabIndex by remember { mutableStateOf(0) }
    KanzanBaseScreen(
        topBar = {
            KanzanTopAppBar(
                title = "Dashboard",
                subtitle = "Rp 15.000.000",
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
        },
        floatingActionButton = {
            androidx.compose.material3.FloatingActionButton(
                onClick = {},
                containerColor = PrimaryDarkItungItungan,
                contentColor = Color.Black,
            ) {
                Text(text = "➕", style = AppTextStyle.nunito_regular_16)
            }
        },
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(dp16)) {
            Text(text = "Tab aktif: $tabIndex", style = AppTextStyle.nunito_medium_14)
            Text(text = "Konten dashboard lengkap", style = AppTextStyle.nunito_regular_14, color = Color.Gray)
        }
    }
}
// endregion
