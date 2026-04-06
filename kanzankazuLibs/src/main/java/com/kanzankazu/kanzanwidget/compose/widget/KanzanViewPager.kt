package com.kanzankazu.kanzanwidget.compose.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kanzankazu.kanzanwidget.compose.ui.AppTextStyle
import com.kanzankazu.kanzanwidget.compose.ui.dp4
import com.kanzankazu.kanzanwidget.compose.ui.dp8
import com.kanzankazu.kanzanwidget.compose.ui.dp16
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlin.math.abs

// region ==================== KanzanViewPager ====================

/**
 * ViewPager-like composable menggunakan LazyRow dengan snap-to-page behavior.
 * Compatible dengan foundation 1.1.1+.
 *
 * @param pageCount jumlah halaman.
 * @param modifier Modifier.
 * @param initialPage halaman awal.
 * @param onPageChanged callback saat halaman berubah.
 * @param showIndicator tampilkan page indicator dots.
 * @param indicatorActiveColor warna dot aktif.
 * @param indicatorInactiveColor warna dot tidak aktif.
 * @param indicatorSize ukuran dot.
 * @param indicatorSpacing jarak antar dot.
 * @param pageContent composable per halaman.
 */
@Composable
fun KanzanViewPager(
    pageCount: Int,
    modifier: Modifier = Modifier,
    initialPage: Int = 0,
    onPageChanged: ((Int) -> Unit)? = null,
    showIndicator: Boolean = true,
    indicatorActiveColor: Color = Color.Black,
    indicatorInactiveColor: Color = Color.LightGray,
    indicatorSize: Dp = dp8,
    indicatorSpacing: Dp = dp4,
    pageContent: @Composable (page: Int) -> Unit,
) {
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialPage)
    val scope = rememberCoroutineScope()

    val currentPage by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val visibleItems = layoutInfo.visibleItemsInfo
            if (visibleItems.isEmpty()) initialPage
            else {
                val viewportCenter = layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset / 2
                visibleItems.minByOrNull { abs((it.offset + it.size / 2) - viewportCenter) }?.index ?: initialPage
            }
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { currentPage }
            .distinctUntilChanged()
            .collect { page -> onPageChanged?.invoke(page) }
    }

    // Snap to nearest page when scroll settles
    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            val layoutInfo = listState.layoutInfo
            val visibleItems = layoutInfo.visibleItemsInfo
            if (visibleItems.isNotEmpty()) {
                val viewportCenter = layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset / 2
                val closest = visibleItems.minByOrNull { abs((it.offset + it.size / 2) - viewportCenter) }
                if (closest != null) {
                    listState.animateScrollBy(closest.offset.toFloat())
                }
            }
        }
    }

    Column(modifier = modifier) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth().weight(1f)) {
            val pageWidth = maxWidth
            LazyRow(state = listState, modifier = Modifier.fillMaxSize()) {
                items(pageCount) { page ->
                    Box(modifier = Modifier.width(pageWidth).fillParentMaxHeight()) {
                        pageContent(page)
                    }
                }
            }
        }

        if (showIndicator && pageCount > 1) {
            Spacer(modifier = Modifier.height(dp8))
            KanzanPageIndicator(
                pageCount = pageCount,
                currentPage = currentPage,
                activeColor = indicatorActiveColor,
                inactiveColor = indicatorInactiveColor,
                dotSize = indicatorSize,
                spacing = indicatorSpacing,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
        }
    }
}

// endregion


// region ==================== KanzanTabViewPager ====================

/**
 * ViewPager dengan TabLayout di atas.
 * Kombinasi KanzanTabBar + KanzanViewPager.
 *
 * @param tabs daftar label tab.
 * @param modifier Modifier.
 * @param scrollableTabs tab scrollable.
 * @param tabContainerColor warna background tab.
 * @param tabSelectedColor warna tab aktif.
 * @param tabUnselectedColor warna tab tidak aktif.
 * @param showIndicator tampilkan page indicator dots.
 * @param pageContent composable per halaman.
 */
@Composable
fun KanzanTabViewPager(
    tabs: List<String>,
    modifier: Modifier = Modifier,
    scrollableTabs: Boolean = false,
    tabContainerColor: Color = Color.White,
    tabSelectedColor: Color = Color.Black,
    tabUnselectedColor: Color = Color.Gray,
    showIndicator: Boolean = false,
    pageContent: @Composable (page: Int) -> Unit,
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val currentPage by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val visibleItems = layoutInfo.visibleItemsInfo
            if (visibleItems.isEmpty()) 0
            else {
                val viewportCenter = layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset / 2
                visibleItems.minByOrNull { abs((it.offset + it.size / 2) - viewportCenter) }?.index ?: 0
            }
        }
    }

    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            val layoutInfo = listState.layoutInfo
            val visibleItems = layoutInfo.visibleItemsInfo
            if (visibleItems.isNotEmpty()) {
                val viewportCenter = layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset / 2
                val closest = visibleItems.minByOrNull { abs((it.offset + it.size / 2) - viewportCenter) }
                if (closest != null) {
                    listState.animateScrollBy(closest.offset.toFloat())
                }
            }
        }
    }

    Column(modifier = modifier) {
        KanzanTabBar(
            items = tabs,
            selectedIndex = currentPage,
            onTabSelected = { scope.launch { listState.animateScrollToItem(it) } },
            alignment = if (scrollableTabs) KanzanBarAlignment.SCROLL else KanzanBarAlignment.CENTER,
            containerColor = tabContainerColor,
            selectedColor = tabSelectedColor,
            unselectedColor = tabUnselectedColor,
        )

        BoxWithConstraints(modifier = Modifier.fillMaxWidth().weight(1f)) {
            val pageWidth = maxWidth
            LazyRow(state = listState, modifier = Modifier.fillMaxSize()) {
                items(tabs.size) { page ->
                    Box(modifier = Modifier.width(pageWidth).fillParentMaxHeight()) {
                        pageContent(page)
                    }
                }
            }
        }

        if (showIndicator && tabs.size > 1) {
            Spacer(modifier = Modifier.height(dp8))
            KanzanPageIndicator(
                pageCount = tabs.size,
                currentPage = currentPage,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
        }
    }
}

// endregion

// region ==================== KanzanPageIndicator ====================

/**
 * Page indicator dots.
 *
 * @param pageCount jumlah halaman.
 * @param currentPage halaman aktif.
 * @param modifier Modifier.
 * @param activeColor warna dot aktif.
 * @param inactiveColor warna dot tidak aktif.
 * @param dotSize ukuran dot.
 * @param spacing jarak antar dot.
 */
@Composable
fun KanzanPageIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier,
    activeColor: Color = Color.Black,
    inactiveColor: Color = Color.LightGray,
    dotSize: Dp = dp8,
    spacing: Dp = dp4,
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(spacing)) {
        for (i in 0 until pageCount) {
            Box(
                modifier = Modifier
                    .size(dotSize)
                    .background(
                        color = if (i == currentPage) activeColor else inactiveColor,
                        shape = CircleShape,
                    ),
            )
        }
    }
}

// endregion

// region ==================== Preview ====================

@Preview(showBackground = true, name = "ViewPager 1. Basic")
@Composable
private fun PreviewViewPagerBasic() {
    val colors = listOf(Color(0xFFE3F2FD), Color(0xFFFCE4EC), Color(0xFFF1F8E9))
    KanzanViewPager(
        pageCount = 3,
        modifier = Modifier.fillMaxWidth().height(200.dp),
    ) { page ->
        Box(
            modifier = Modifier.fillMaxSize().background(colors[page]),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "Page ${page + 1}", style = AppTextStyle.nunito_bold_16)
        }
    }
}

@Preview(showBackground = true, name = "ViewPager 2. No indicator")
@Composable
private fun PreviewViewPagerNoIndicator() {
    KanzanViewPager(
        pageCount = 4,
        modifier = Modifier.fillMaxWidth().height(150.dp),
        showIndicator = false,
    ) { page ->
        Box(modifier = Modifier.fillMaxSize().padding(dp16), contentAlignment = Alignment.Center) {
            Text(text = "Slide ${page + 1} of 4", style = AppTextStyle.nunito_medium_14)
        }
    }
}

@Preview(showBackground = true, name = "ViewPager 3. TabViewPager")
@Composable
private fun PreviewTabViewPager() {
    KanzanTabViewPager(
        tabs = listOf("Hutang", "Piutang", "Riwayat"),
        modifier = Modifier.fillMaxWidth().height(250.dp),
    ) { page ->
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = when (page) {
                    0 -> "Daftar Hutang"
                    1 -> "Daftar Piutang"
                    else -> "Riwayat Transaksi"
                },
                style = AppTextStyle.nunito_medium_16,
            )
        }
    }
}

@Preview(showBackground = true, name = "ViewPager 4. Scrollable tabs")
@Composable
private fun PreviewTabViewPagerScrollable() {
    KanzanTabViewPager(
        tabs = listOf("Semua", "Hutang", "Piutang", "Lunas", "Jatuh Tempo", "Arsip"),
        modifier = Modifier.fillMaxWidth().height(200.dp),
        scrollableTabs = true,
    ) { page ->
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Tab $page", style = AppTextStyle.nunito_regular_14)
        }
    }
}

@Preview(showBackground = true, name = "ViewPager 5. Custom indicator")
@Composable
private fun PreviewViewPagerCustomIndicator() {
    KanzanViewPager(
        pageCount = 5,
        modifier = Modifier.fillMaxWidth().height(180.dp),
        indicatorActiveColor = Color(0xFFFFE422),
        indicatorInactiveColor = Color(0xFFE0E0E0),
        indicatorSize = 10.dp,
        indicatorSpacing = dp8,
    ) { page ->
        Box(
            modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5)),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "Banner ${page + 1}", style = AppTextStyle.nunito_bold_16)
        }
    }
}

@Preview(showBackground = true, name = "PageIndicator 1. Standalone")
@Composable
private fun PreviewPageIndicator() {
    Column(modifier = Modifier.padding(dp16), verticalArrangement = Arrangement.spacedBy(dp16)) {
        KanzanPageIndicator(pageCount = 5, currentPage = 3)
        KanzanPageIndicator(pageCount = 5, currentPage = 2)
        KanzanPageIndicator(pageCount = 5, currentPage = 4, activeColor = Color.Red)
        KanzanPageIndicator(pageCount = 3, currentPage = 1, dotSize = 12.dp, spacing = dp8, activeColor = Color(0xFFFFE422))
    }
}

// endregion
