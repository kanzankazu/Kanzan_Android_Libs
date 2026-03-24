package com.kanzankazu.kanzanwidget.compose.widget

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kanzankazu.kanzanwidget.compose.ui.AppTextStyle
import com.kanzankazu.kanzanwidget.compose.ui.PrimaryDarkItungItungan
import com.kanzankazu.kanzanwidget.compose.ui.dp0
import com.kanzankazu.kanzanwidget.compose.ui.dp3

// region ==================== KanzanBottomAppBar ====================

/**
 * Reusable Bottom App Bar yang membungkus Material 3 [BottomAppBar].
 *
 * Mendukung dua mode:
 * 1. **Action mode** — ikon aksi di kiri, FAB di kanan (parameter [actions] + [floatingActionButton])
 * 2. **Custom content mode** — konten bebas via [content] (jika [content] disediakan, [actions] dan [floatingActionButton] diabaikan)
 *
 * @param modifier Modifier untuk BottomAppBar
 * @param containerColor Warna background bar
 * @param contentColor Warna konten (ikon, teks) di dalam bar
 * @param tonalElevation Elevasi tonal untuk surface tinting
 * @param contentPadding Padding internal konten bar
 * @param windowInsets Window insets yang diterapkan pada bar
 * @param actions Composable slot untuk ikon aksi (mode action)
 * @param floatingActionButton Composable slot untuk FAB (mode action)
 * @param content Composable slot untuk konten kustom (menggantikan actions + FAB)
 */
@Composable
fun KanzanBottomAppBar(
    modifier: Modifier = Modifier,
    containerColor: Color = Color.White,
    contentColor: Color = contentColorFor(containerColor),
    tonalElevation: Dp = dp3,
    contentPadding: PaddingValues = BottomAppBarDefaults.ContentPadding,
    windowInsets: WindowInsets = BottomAppBarDefaults.windowInsets,
    actions: @Composable (RowScope.() -> Unit)? = null,
    floatingActionButton: @Composable (() -> Unit)? = null,
    content: @Composable (RowScope.() -> Unit)? = null,
) {
    if (content != null) {
        // Custom content mode — konten bebas, tanpa actions/FAB layout
        BottomAppBar(
            modifier = modifier,
            containerColor = containerColor,
            contentColor = contentColor,
            tonalElevation = tonalElevation,
            contentPadding = contentPadding,
            windowInsets = windowInsets,
            content = content,
        )
    } else {
        // Action mode — actions di kiri, FAB di kanan
        BottomAppBar(
            actions = actions ?: {},
            modifier = modifier,
            floatingActionButton = floatingActionButton,
            containerColor = containerColor,
            contentColor = contentColor,
            tonalElevation = tonalElevation,
            contentPadding = contentPadding,
            windowInsets = windowInsets,
        )
    }
}
// endregion

// region ==================== Preview ====================

// region ── 1. Action mode ──

@Preview(showBackground = true, name = "BAB 1. Actions only")
@Composable
private fun PreviewActionsOnly() {
    KanzanBottomAppBar(
        actions = {
            IconButton(onClick = {}) { Text(text = "🏠", style = AppTextStyle.nunito_regular_16) }
            IconButton(onClick = {}) { Text(text = "📋", style = AppTextStyle.nunito_regular_16) }
            IconButton(onClick = {}) { Text(text = "⚙️", style = AppTextStyle.nunito_regular_16) }
        }
    )
}

@Preview(showBackground = true, name = "BAB 2. Actions + FAB")
@Composable
private fun PreviewActionsWithFab() {
    KanzanBottomAppBar(
        actions = {
            IconButton(onClick = {}) { Text(text = "🏠", style = AppTextStyle.nunito_regular_16) }
            IconButton(onClick = {}) { Text(text = "📋", style = AppTextStyle.nunito_regular_16) }
            IconButton(onClick = {}) { Text(text = "💰", style = AppTextStyle.nunito_regular_16) }
            IconButton(onClick = {}) { Text(text = "👤", style = AppTextStyle.nunito_regular_16) }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {},
                containerColor = PrimaryDarkItungItungan,
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
            ) {
                Text(text = "➕", style = AppTextStyle.nunito_medium_16)
            }
        }
    )
}

@Preview(showBackground = true, name = "BAB 3. FAB only (no actions)")
@Composable
private fun PreviewFabOnly() {
    KanzanBottomAppBar(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {},
                containerColor = PrimaryDarkItungItungan,
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
            ) {
                Text(text = "➕", style = AppTextStyle.nunito_medium_16)
            }
        }
    )
}
// endregion

// region ── 2. Custom content mode ──

@Preview(showBackground = true, name = "BAB 4. Custom content")
@Composable
private fun PreviewCustomContent() {
    KanzanBottomAppBar(
        content = {
            IconButton(onClick = {}, modifier = Modifier.weight(1f)) { Text(text = "🏠", style = AppTextStyle.nunito_regular_16) }
            IconButton(onClick = {}, modifier = Modifier.weight(1f)) { Text(text = "📊", style = AppTextStyle.nunito_regular_16) }
            IconButton(onClick = {}, modifier = Modifier.weight(1f)) { Text(text = "💰", style = AppTextStyle.nunito_regular_16) }
            IconButton(onClick = {}, modifier = Modifier.weight(1f)) { Text(text = "👤", style = AppTextStyle.nunito_regular_16) }
        }
    )
}
// endregion

// region ── 3. Color & elevation variants ──

@Preview(showBackground = true, name = "BAB 5. Primary color")
@Composable
private fun PreviewPrimaryColor() {
    KanzanBottomAppBar(
        containerColor = PrimaryDarkItungItungan,
        actions = {
            IconButton(onClick = {}) { Text(text = "🏠", style = AppTextStyle.nunito_regular_16) }
            IconButton(onClick = {}) { Text(text = "📋", style = AppTextStyle.nunito_regular_16) }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {},
                containerColor = Color.White,
                elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(),
            ) {
                Text(text = "➕", style = AppTextStyle.nunito_medium_16)
            }
        }
    )
}

@Preview(showBackground = true, name = "BAB 6. Dark color")
@Composable
private fun PreviewDarkColor() {
    KanzanBottomAppBar(
        containerColor = Color.DarkGray,
        contentColor = Color.White,
        actions = {
            IconButton(onClick = {}) { Text(text = "🏠", style = AppTextStyle.nunito_regular_16) }
            IconButton(onClick = {}) { Text(text = "📋", style = AppTextStyle.nunito_regular_16) }
            IconButton(onClick = {}) { Text(text = "⚙️", style = AppTextStyle.nunito_regular_16) }
        }
    )
}

@Preview(showBackground = true, name = "BAB 7. No elevation (flat)")
@Composable
private fun PreviewNoElevation() {
    KanzanBottomAppBar(
        tonalElevation = dp0,
        actions = {
            IconButton(onClick = {}) { Text(text = "🏠", style = AppTextStyle.nunito_regular_16) }
            IconButton(onClick = {}) { Text(text = "📋", style = AppTextStyle.nunito_regular_16) }
        }
    )
}
// endregion

// endregion
