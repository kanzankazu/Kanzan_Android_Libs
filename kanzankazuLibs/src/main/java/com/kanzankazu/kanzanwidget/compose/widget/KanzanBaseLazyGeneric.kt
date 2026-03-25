package com.kanzankazu.kanzanwidget.compose.widget

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kanzankazu.kanzanwidget.compose.ui.AppTextStyle
import com.kanzankazu.kanzanwidget.compose.ui.dp2
import com.kanzankazu.kanzanwidget.compose.ui.dp4
import com.kanzankazu.kanzanwidget.compose.ui.dp8
import com.kanzankazu.kanzanwidget.compose.ui.dp12
import com.kanzankazu.kanzanwidget.compose.ui.dp16
import com.kanzankazu.kanzanwidget.compose.ui.dp48
import com.kanzankazu.kanzanwidget.compose.ui.dp64
import com.kanzankazu.kanzanwidget.compose.ui.dp80
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged

// region ==================== Enums & Models ====================

enum class KanzanLazyOrientation { VERTICAL, HORIZONTAL }

enum class KanzanSelectionMode { NONE, SINGLE, MULTI }

/**
 * Konfigurasi swipe action (kiri atau kanan).
 *
 * @param backgroundColor warna background saat swipe.
 * @param icon composable icon yang tampil di belakang item.
 * @param threshold persentase lebar item untuk trigger action (0f-1f).
 * @param onAction callback saat swipe melewati threshold.
 */
data class KanzanSwipeAction(
    val backgroundColor: Color,
    val icon: @Composable () -> Unit,
    val threshold: Float = 0.3f,
    val onAction: (index: Int) -> Unit,
)

/**
 * State holder untuk KanzanBaseLazy.
 * Mirip RecyclerView state management.
 */
data class KanzanLazyState<T>(
    val items: List<T> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val isRefreshing: Boolean = false,
    val isEmpty: Boolean = false,
    val errorMessage: String? = null,
    val hasMoreData: Boolean = true,
)

// endregion

// region ==================== KanzanBaseLazy ====================

/**
 * Komponen LazyColumn/LazyRow generic dengan kekuatan RecyclerView.
 * Support: header/footer, empty state, loading, load more (pagination),
 * skeleton, divider, sticky header, error state, scroll listener,
 * swipe left/right actions, drag-to-reorder, single/multi selection.
 *
 * @param T tipe data item.
 * @param state state holder (items, loading, error, dll).
 * @param itemContent composable per item. Menerima index, item, dan isSelected.
 * @param modifier Modifier.
 * @param orientation VERTICAL (LazyColumn) atau HORIZONTAL (LazyRow).
 * @param listState LazyListState untuk kontrol scroll.
 * @param contentPadding padding konten.
 * @param verticalArrangement arrangement vertikal (Column).
 * @param horizontalArrangement arrangement horizontal (Row).
 * @param reverseLayout reverse layout.
 * @param headerContent composable header di atas list.
 * @param footerContent composable footer di bawah list.
 * @param emptyContent composable empty state.
 * @param loadingContent composable loading state (awal).
 * @param loadMoreContent composable loading more (pagination).
 * @param errorContent composable error state.
 * @param skeletonCount jumlah skeleton item saat loading.
 * @param skeletonContent composable skeleton item.
 * @param showDivider tampilkan divider antar item.
 * @param dividerColor warna divider.
 * @param dividerThickness ketebalan divider.
 * @param onLoadMore callback saat scroll mendekati akhir (pagination).
 * @param loadMoreThreshold jumlah item dari akhir untuk trigger load more.
 * @param onScrollStateChanged callback saat scroll state berubah.
 * @param itemKey key factory untuk item (performa diff).
 * @param itemContentType content type factory (performa reuse).
 * @param stickyHeaderContent composable sticky header (hanya Column).
 * @param swipeLeftAction konfigurasi swipe ke kiri (misal: hapus).
 * @param swipeRightAction konfigurasi swipe ke kanan (misal: arsip).
 * @param enableDragReorder aktifkan drag-to-reorder.
 * @param onReorder callback saat item di-reorder (fromIndex, toIndex).
 * @param dragHandleContent composable drag handle (tampil di kiri item saat enableDragReorder).
 * @param selectionMode mode seleksi (NONE, SINGLE, MULTI).
 * @param selectedIndices set index item yang terpilih.
 * @param onSelectionChanged callback saat seleksi berubah.
 * @param selectedColor warna background item terpilih.
 * @param showSelectionIndicator tampilkan radio/checkbox di kiri item.
 */
@Composable
fun <T> KanzanBaseLazy(
    state: KanzanLazyState<T>,
    itemContent: @Composable (index: Int, item: T, isSelected: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    orientation: KanzanLazyOrientation = KanzanLazyOrientation.VERTICAL,
    listState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    reverseLayout: Boolean = false,
    headerContent: @Composable (() -> Unit)? = null,
    footerContent: @Composable (() -> Unit)? = null,
    emptyContent: @Composable (() -> Unit)? = null,
    loadingContent: @Composable (() -> Unit)? = null,
    loadMoreContent: @Composable (() -> Unit)? = null,
    errorContent: @Composable ((String) -> Unit)? = null,
    skeletonCount: Int = 5,
    skeletonContent: @Composable ((Int) -> Unit)? = null,
    showDivider: Boolean = false,
    dividerColor: Color = Color.LightGray.copy(alpha = 0.3f),
    dividerThickness: Dp = 1.dp,
    onLoadMore: (() -> Unit)? = null,
    loadMoreThreshold: Int = 3,
    onScrollStateChanged: ((firstVisibleIndex: Int, isScrolling: Boolean) -> Unit)? = null,
    itemKey: ((index: Int, item: T) -> Any)? = null,
    itemContentType: ((index: Int, item: T) -> Any?)? = null,
    stickyHeaderContent: (LazyListScope.() -> Unit)? = null,
    // Swipe actions
    swipeLeftAction: KanzanSwipeAction? = null,
    swipeRightAction: KanzanSwipeAction? = null,
    // Drag reorder
    enableDragReorder: Boolean = false,
    onReorder: ((fromIndex: Int, toIndex: Int) -> Unit)? = null,
    dragHandleContent: @Composable (() -> Unit)? = null,
    // Selection
    selectionMode: KanzanSelectionMode = KanzanSelectionMode.NONE,
    selectedIndices: Set<Int> = emptySet(),
    onSelectionChanged: ((Set<Int>) -> Unit)? = null,
    selectedColor: Color = Color(0xFFE3F2FD),
    showSelectionIndicator: Boolean = true,
) {
    // Scroll listener
    if (onScrollStateChanged != null) {
        LaunchedEffect(listState) {
            snapshotFlow { listState.firstVisibleItemIndex to listState.isScrollInProgress }
                .distinctUntilChanged()
                .collect { (index, scrolling) -> onScrollStateChanged(index, scrolling) }
        }
    }

    // Load more detection
    if (onLoadMore != null && state.hasMoreData && !state.isLoadingMore) {
        val shouldLoadMore by remember {
            derivedStateOf {
                val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                val totalItems = listState.layoutInfo.totalItemsCount
                lastVisible >= totalItems - loadMoreThreshold && totalItems > 0
            }
        }
        LaunchedEffect(shouldLoadMore) {
            if (shouldLoadMore) onLoadMore()
        }
    }

    // Error state
    if (state.errorMessage != null && state.items.isEmpty()) {
        errorContent?.invoke(state.errorMessage) ?: DefaultErrorContent(state.errorMessage)
        return
    }

    // Initial loading with skeleton
    if (state.isLoading && state.items.isEmpty()) {
        if (skeletonContent != null) {
            if (orientation == KanzanLazyOrientation.VERTICAL) {
                LazyColumn(modifier = modifier, contentPadding = contentPadding, verticalArrangement = verticalArrangement) {
                    items(skeletonCount) { index -> skeletonContent(index) }
                }
            } else {
                LazyRow(modifier = modifier, contentPadding = contentPadding, horizontalArrangement = horizontalArrangement) {
                    items(skeletonCount) { index -> skeletonContent(index) }
                }
            }
        } else {
            loadingContent?.invoke() ?: DefaultLoadingContent()
        }
        return
    }

    // Empty state
    if (state.isEmpty || (state.items.isEmpty() && !state.isLoading)) {
        emptyContent?.invoke() ?: DefaultEmptyContent()
        return
    }

    val hasSwipe = swipeLeftAction != null || swipeRightAction != null

    // Main list
    val lazyContent: LazyListScope.() -> Unit = {
        // Header
        if (headerContent != null) {
            item(key = "__header__") { headerContent() }
        }

        // Sticky header (Column only)
        stickyHeaderContent?.invoke(this)

        // Items
        itemsIndexed(
            items = state.items,
            key = if (itemKey != null) { index, item -> itemKey(index, item) } else null,
            contentType = if (itemContentType != null) { index, item -> itemContentType(index, item) } else { _, _ -> null },
        ) { index, item ->
            val isSelected = index in selectedIndices

            val itemRow: @Composable () -> Unit = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (isSelected) selectedColor else Color.Transparent)
                        .then(
                            if (selectionMode != KanzanSelectionMode.NONE) {
                                Modifier.clickable { handleSelection(selectionMode, index, selectedIndices, onSelectionChanged) }
                            } else Modifier
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Selection indicator
                    if (selectionMode != KanzanSelectionMode.NONE && showSelectionIndicator) {
                        when (selectionMode) {
                            KanzanSelectionMode.SINGLE -> RadioButton(
                                selected = isSelected,
                                onClick = { handleSelection(selectionMode, index, selectedIndices, onSelectionChanged) },
                                modifier = Modifier.padding(start = dp8)
                            )
                            KanzanSelectionMode.MULTI -> Checkbox(
                                checked = isSelected,
                                onCheckedChange = { handleSelection(selectionMode, index, selectedIndices, onSelectionChanged) },
                                modifier = Modifier.padding(start = dp8)
                            )
                            else -> {}
                        }
                    }

                    // Drag handle
                    if (enableDragReorder) {
                        dragHandleContent?.invoke() ?: DefaultDragHandle()
                    }

                    // Item content
                    Box(modifier = Modifier.weight(1f)) {
                        itemContent(index, item, isSelected)
                    }
                }
            }

            // Wrap with swipe if needed
            if (hasSwipe) {
                KanzanSwipeableItem(
                    index = index,
                    swipeLeftAction = swipeLeftAction,
                    swipeRightAction = swipeRightAction,
                    content = itemRow,
                )
            } else {
                itemRow()
            }

            if (showDivider && index < state.items.lastIndex) {
                Divider(color = dividerColor, thickness = dividerThickness)
            }
        }

        // Load more indicator
        if (state.isLoadingMore) {
            item(key = "__load_more__") {
                loadMoreContent?.invoke() ?: DefaultLoadMoreContent()
            }
        }

        // Footer
        if (footerContent != null) {
            item(key = "__footer__") { footerContent() }
        }
    }

    if (orientation == KanzanLazyOrientation.VERTICAL) {
        LazyColumn(
            modifier = modifier,
            state = listState,
            contentPadding = contentPadding,
            verticalArrangement = verticalArrangement,
            reverseLayout = reverseLayout,
            content = lazyContent,
        )
    } else {
        LazyRow(
            modifier = modifier,
            state = listState,
            contentPadding = contentPadding,
            horizontalArrangement = horizontalArrangement,
            reverseLayout = reverseLayout,
            content = lazyContent,
        )
    }
}

/**
 * Backward-compatible overload tanpa isSelected di itemContent.
 */
@Composable
fun <T> KanzanBaseLazy(
    state: KanzanLazyState<T>,
    itemContent: @Composable (index: Int, item: T) -> Unit,
    modifier: Modifier = Modifier,
    orientation: KanzanLazyOrientation = KanzanLazyOrientation.VERTICAL,
    listState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    reverseLayout: Boolean = false,
    headerContent: @Composable (() -> Unit)? = null,
    footerContent: @Composable (() -> Unit)? = null,
    emptyContent: @Composable (() -> Unit)? = null,
    loadingContent: @Composable (() -> Unit)? = null,
    loadMoreContent: @Composable (() -> Unit)? = null,
    errorContent: @Composable ((String) -> Unit)? = null,
    skeletonCount: Int = 5,
    skeletonContent: @Composable ((Int) -> Unit)? = null,
    showDivider: Boolean = false,
    dividerColor: Color = Color.LightGray.copy(alpha = 0.3f),
    dividerThickness: Dp = 1.dp,
    onLoadMore: (() -> Unit)? = null,
    loadMoreThreshold: Int = 3,
    onScrollStateChanged: ((firstVisibleIndex: Int, isScrolling: Boolean) -> Unit)? = null,
    itemKey: ((index: Int, item: T) -> Any)? = null,
    itemContentType: ((index: Int, item: T) -> Any?)? = null,
    stickyHeaderContent: (LazyListScope.() -> Unit)? = null,
) {
    KanzanBaseLazy(
        state = state,
        itemContent = { index, item, _ -> itemContent(index, item) },
        modifier = modifier,
        orientation = orientation,
        listState = listState,
        contentPadding = contentPadding,
        verticalArrangement = verticalArrangement,
        horizontalArrangement = horizontalArrangement,
        reverseLayout = reverseLayout,
        headerContent = headerContent,
        footerContent = footerContent,
        emptyContent = emptyContent,
        loadingContent = loadingContent,
        loadMoreContent = loadMoreContent,
        errorContent = errorContent,
        skeletonCount = skeletonCount,
        skeletonContent = skeletonContent,
        showDivider = showDivider,
        dividerColor = dividerColor,
        dividerThickness = dividerThickness,
        onLoadMore = onLoadMore,
        loadMoreThreshold = loadMoreThreshold,
        onScrollStateChanged = onScrollStateChanged,
        itemKey = itemKey,
        itemContentType = itemContentType,
        stickyHeaderContent = stickyHeaderContent,
    )
}
// endregion

// region ==================== Swipe Item ====================

/**
 * Item wrapper menggunakan Material [SwipeToDismiss] + [AnimatedVisibility]
 * untuk smooth swipe-to-dismiss animation.
 * Ref: https://proandroiddev.com/swipe-to-dismiss-with-compose-material-3-38445e0143f7
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun KanzanSwipeableItem(
    index: Int,
    swipeLeftAction: KanzanSwipeAction?,
    swipeRightAction: KanzanSwipeAction?,
    content: @Composable () -> Unit,
) {
    var isRemoved by remember { mutableStateOf(false) }

    val directions = mutableSetOf<DismissDirection>()
    if (swipeLeftAction != null) directions.add(DismissDirection.EndToStart)
    if (swipeRightAction != null) directions.add(DismissDirection.StartToEnd)

    val dismissState = rememberDismissState(
        confirmStateChange = { dismissValue ->
            when (dismissValue) {
                DismissValue.DismissedToStart -> {
                    // Swipe left (end-to-start)
                    if (swipeLeftAction != null) {
                        isRemoved = true
                        true
                    } else false
                }
                DismissValue.DismissedToEnd -> {
                    // Swipe right (start-to-end)
                    if (swipeRightAction != null) {
                        isRemoved = true
                        true
                    } else false
                }
                else -> false
            }
        }
    )

    // Trigger action after dismiss animation completes + shrink animation
    LaunchedEffect(isRemoved) {
        if (isRemoved) {
            delay(300) // Wait for AnimatedVisibility shrink
            when (dismissState.currentValue) {
                DismissValue.DismissedToStart -> swipeLeftAction?.onAction?.invoke(index)
                DismissValue.DismissedToEnd -> swipeRightAction?.onAction?.invoke(index)
                else -> {}
            }
        }
    }

    AnimatedVisibility(
        visible = !isRemoved,
        exit = shrinkVertically(animationSpec = tween(300)) + fadeOut(tween(300))
    ) {
        SwipeToDismiss(
            state = dismissState,
            directions = directions,
            dismissThresholds = { direction ->
                val threshold = when (direction) {
                    DismissDirection.EndToStart -> swipeLeftAction?.threshold ?: 0.3f
                    DismissDirection.StartToEnd -> swipeRightAction?.threshold ?: 0.3f
                }
                FractionalThreshold(threshold)
            },
            background = {
                SwipeDismissBackground(
                    dismissState = dismissState,
                    swipeLeftAction = swipeLeftAction,
                    swipeRightAction = swipeRightAction,
                )
            },
            dismissContent = {
                content()
            }
        )
    }
}

/**
 * Background yang tampil di belakang item saat di-swipe.
 * Icon scale up saat mendekati threshold.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun SwipeDismissBackground(
    dismissState: androidx.compose.material.DismissState,
    swipeLeftAction: KanzanSwipeAction?,
    swipeRightAction: KanzanSwipeAction?,
) {
    val direction = dismissState.dismissDirection
    val bgColor by animateColorAsState(
        when (dismissState.targetValue) {
            DismissValue.DismissedToStart -> swipeLeftAction?.backgroundColor ?: Color.Transparent
            DismissValue.DismissedToEnd -> swipeRightAction?.backgroundColor ?: Color.Transparent
            else -> Color.LightGray.copy(alpha = 0.2f)
        },
        tween(200),
        label = "swipeBg"
    )

    val iconScale by animateFloatAsState(
        if (dismissState.targetValue == DismissValue.Default) 0.75f else 1.2f,
        tween(200),
        label = "iconScale"
    )

    val alignment = when (direction) {
        DismissDirection.EndToStart -> Alignment.CenterEnd
        DismissDirection.StartToEnd -> Alignment.CenterStart
        else -> Alignment.Center
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .padding(horizontal = dp16),
        contentAlignment = alignment
    ) {
        Box(modifier = Modifier.scale(iconScale)) {
            when (direction) {
                DismissDirection.EndToStart -> swipeLeftAction?.icon?.invoke()
                DismissDirection.StartToEnd -> swipeRightAction?.icon?.invoke()
                else -> {}
            }
        }
    }
}
// endregion

// region ==================== Selection Helper ====================

private fun handleSelection(
    mode: KanzanSelectionMode,
    index: Int,
    currentSelection: Set<Int>,
    onSelectionChanged: ((Set<Int>) -> Unit)?,
) {
    if (onSelectionChanged == null) return
    when (mode) {
        KanzanSelectionMode.SINGLE -> {
            onSelectionChanged(if (index in currentSelection) emptySet() else setOf(index))
        }
        KanzanSelectionMode.MULTI -> {
            val newSelection = currentSelection.toMutableSet()
            if (index in newSelection) newSelection.remove(index) else newSelection.add(index)
            onSelectionChanged(newSelection)
        }
        KanzanSelectionMode.NONE -> {}
    }
}
// endregion

// region ==================== Default Content ====================

@Composable
private fun DefaultDragHandle() {
    Text(
        text = "☰",
        style = AppTextStyle.nunito_regular_16,
        color = Color.Gray,
        modifier = Modifier.padding(horizontal = dp8)
    )
}

@Composable
private fun DefaultLoadingContent() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(strokeWidth = dp2)
    }
}

@Composable
private fun DefaultEmptyContent() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "📭", style = AppTextStyle.nunito_regular_36)
            Spacer(modifier = Modifier.height(dp8))
            Text(text = "Belum ada data", style = AppTextStyle.nunito_medium_14, color = Color.Gray)
        }
    }
}

@Composable
private fun DefaultErrorContent(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "⚠️", style = AppTextStyle.nunito_regular_36)
            Spacer(modifier = Modifier.height(dp8))
            Text(text = message, style = AppTextStyle.nunito_regular_14, color = Color.Red)
        }
    }
}

@Composable
private fun DefaultLoadMoreContent() {
    Box(modifier = Modifier.fillMaxWidth().padding(dp16), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(modifier = Modifier.height(dp16).width(dp16), strokeWidth = dp2)
    }
}

/**
 * Default skeleton item shimmer.
 */
@Composable
fun KanzanSkeletonItem(
    modifier: Modifier = Modifier,
    height: Dp = dp64,
) {
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
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = dp16, vertical = dp8),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.height(dp48).width(dp48).clip(RoundedCornerShape(dp8)).background(brush))
        Spacer(modifier = Modifier.width(dp12))
        Column(modifier = Modifier.weight(1f)) {
            Box(modifier = Modifier.fillMaxWidth(0.7f).height(dp16).clip(RoundedCornerShape(dp4)).background(brush))
            Spacer(modifier = Modifier.height(dp8))
            Box(modifier = Modifier.fillMaxWidth(0.4f).height(dp12).clip(RoundedCornerShape(dp4)).background(brush))
        }
    }
}
// endregion

// region ==================== Preview ====================

@Preview(showBackground = true, name = "Lazy 1. Vertical list")
@Composable
private fun PreviewLazyVertical() {
    val items = (1..10).map { "Item $it" }
    KanzanBaseLazy(
        state = KanzanLazyState(items = items),
        itemContent = { index, item ->
            Text(
                text = "$item (index: $index)",
                style = AppTextStyle.nunito_regular_14,
                modifier = Modifier.fillMaxWidth().padding(dp16)
            )
        },
        showDivider = true,
        modifier = Modifier.height(300.dp)
    )
}

@Preview(showBackground = true, name = "Lazy 2. Horizontal list")
@Composable
private fun PreviewLazyHorizontal() {
    val items = (1..8).map { "Tag $it" }
    KanzanBaseLazy(
        state = KanzanLazyState(items = items),
        orientation = KanzanLazyOrientation.HORIZONTAL,
        itemContent = { _, item ->
            Box(
                modifier = Modifier
                    .padding(dp4)
                    .clip(RoundedCornerShape(dp16))
                    .background(Color(0xFFE3F2FD))
                    .padding(horizontal = dp12, vertical = dp8)
            ) {
                Text(text = item, style = AppTextStyle.nunito_regular_12)
            }
        },
        horizontalArrangement = Arrangement.spacedBy(dp4),
        contentPadding = PaddingValues(horizontal = dp16)
    )
}

@Preview(showBackground = true, name = "Lazy 3. Empty state")
@Composable
private fun PreviewLazyEmpty() {
    KanzanBaseLazy<String>(
        state = KanzanLazyState(items = emptyList(), isEmpty = true),
        itemContent = { _, _ -> },
        modifier = Modifier.height(200.dp)
    )
}

@Preview(showBackground = true, name = "Lazy 4. Skeleton loading")
@Composable
private fun PreviewLazySkeleton() {
    KanzanBaseLazy<String>(
        state = KanzanLazyState(items = emptyList(), isLoading = true),
        itemContent = { _, _ -> },
        skeletonCount = 4,
        skeletonContent = { KanzanSkeletonItem() },
        modifier = Modifier.height(350.dp)
    )
}

@Preview(showBackground = true, name = "Lazy 5. Error state")
@Composable
private fun PreviewLazyError() {
    KanzanBaseLazy<String>(
        state = KanzanLazyState(items = emptyList(), errorMessage = "Gagal memuat data. Coba lagi."),
        itemContent = { _, _ -> },
        modifier = Modifier.height(200.dp)
    )
}

@Preview(showBackground = true, name = "Lazy 6. With header & footer")
@Composable
private fun PreviewLazyHeaderFooter() {
    val items = (1..5).map { "Transaksi $it" }
    KanzanBaseLazy(
        state = KanzanLazyState(items = items),
        itemContent = { _, item ->
            Text(text = item, style = AppTextStyle.nunito_regular_14, modifier = Modifier.fillMaxWidth().padding(dp16))
        },
        headerContent = {
            Text(
                text = "📋 Riwayat Transaksi",
                style = AppTextStyle.nunito_bold_16,
                modifier = Modifier.fillMaxWidth().background(Color(0xFFF5F5F5)).padding(dp16)
            )
        },
        footerContent = {
            Text(
                text = "— Akhir daftar —",
                style = AppTextStyle.nunito_regular_12,
                color = Color.Gray,
                modifier = Modifier.fillMaxWidth().padding(dp16)
            )
        },
        showDivider = true,
        modifier = Modifier.height(350.dp)
    )
}

@Preview(showBackground = true, name = "Lazy 7. Load more")
@Composable
private fun PreviewLazyLoadMore() {
    val items = (1..5).map { "Item $it" }
    KanzanBaseLazy(
        state = KanzanLazyState(items = items, isLoadingMore = true, hasMoreData = true),
        itemContent = { _, item ->
            Text(text = item, style = AppTextStyle.nunito_regular_14, modifier = Modifier.fillMaxWidth().padding(dp16))
        },
        showDivider = true,
        modifier = Modifier.height(350.dp)
    )
}

@Preview(showBackground = true, name = "Lazy 8. Swipe left (hapus)")
@Composable
private fun PreviewLazySwipeLeft() {
    var items by remember { mutableStateOf((1..5).map { "Hutang $it" }) }
    KanzanBaseLazy(
        state = KanzanLazyState(items = items),
        itemContent = { _, item, _ ->
            Text(text = item, style = AppTextStyle.nunito_regular_14, modifier = Modifier.fillMaxWidth().padding(dp16))
        },
        swipeLeftAction = KanzanSwipeAction(
            backgroundColor = Color(0xFFF44336),
            icon = { Text(text = "🗑️ Hapus", style = AppTextStyle.nunito_medium_14, color = Color.White) },
            onAction = { index -> items = items.toMutableList().also { it.removeAt(index) } }
        ),
        showDivider = true,
        modifier = Modifier.height(300.dp)
    )
}

@Preview(showBackground = true, name = "Lazy 9. Swipe right (arsip)")
@Composable
private fun PreviewLazySwipeRight() {
    val items = (1..5).map { "Arisan $it" }
    KanzanBaseLazy(
        state = KanzanLazyState(items = items),
        itemContent = { _, item, _ ->
            Text(text = item, style = AppTextStyle.nunito_regular_14, modifier = Modifier.fillMaxWidth().padding(dp16))
        },
        swipeRightAction = KanzanSwipeAction(
            backgroundColor = Color(0xFF4CAF50),
            icon = { Text(text = "✅ Arsip", style = AppTextStyle.nunito_medium_14, color = Color.White) },
            onAction = {}
        ),
        showDivider = true,
        modifier = Modifier.height(300.dp)
    )
}

@Preview(showBackground = true, name = "Lazy 10. Swipe both directions")
@Composable
private fun PreviewLazySwipeBoth() {
    val items = (1..5).map { "Transaksi $it" }
    KanzanBaseLazy(
        state = KanzanLazyState(items = items),
        itemContent = { _, item, _ ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(dp16),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "💰", style = AppTextStyle.nunito_regular_16)
                Spacer(modifier = Modifier.width(dp8))
                Text(text = item, style = AppTextStyle.nunito_regular_14)
            }
        },
        swipeLeftAction = KanzanSwipeAction(
            backgroundColor = Color(0xFFF44336),
            icon = { Text(text = "🗑️", style = AppTextStyle.nunito_regular_24) },
            onAction = {}
        ),
        swipeRightAction = KanzanSwipeAction(
            backgroundColor = Color(0xFF2196F3),
            icon = { Text(text = "📌", style = AppTextStyle.nunito_regular_24) },
            onAction = {}
        ),
        showDivider = true,
        modifier = Modifier.height(300.dp)
    )
}

@Preview(showBackground = true, name = "Lazy 11. Single selection")
@Composable
private fun PreviewLazySingleSelection() {
    val items = listOf("BCA", "Mandiri", "BRI", "BNI", "CIMB")
    var selected by remember { mutableStateOf(setOf(1)) }
    KanzanBaseLazy(
        state = KanzanLazyState(items = items),
        itemContent = { _, item, isSelected ->
            Text(
                text = item,
                style = if (isSelected) AppTextStyle.nunito_medium_14 else AppTextStyle.nunito_regular_14,
                modifier = Modifier.fillMaxWidth().padding(dp16)
            )
        },
        selectionMode = KanzanSelectionMode.SINGLE,
        selectedIndices = selected,
        onSelectionChanged = { selected = it },
        showDivider = true,
        modifier = Modifier.height(300.dp)
    )
}

@Preview(showBackground = true, name = "Lazy 12. Multi selection")
@Composable
private fun PreviewLazyMultiSelection() {
    val items = listOf("Makanan", "Transportasi", "Hiburan", "Belanja", "Tagihan", "Pendidikan")
    var selected by remember { mutableStateOf(setOf(0, 2, 4)) }
    KanzanBaseLazy(
        state = KanzanLazyState(items = items),
        itemContent = { _, item, isSelected ->
            Text(
                text = item,
                style = if (isSelected) AppTextStyle.nunito_medium_14 else AppTextStyle.nunito_regular_14,
                modifier = Modifier.fillMaxWidth().padding(dp16)
            )
        },
        selectionMode = KanzanSelectionMode.MULTI,
        selectedIndices = selected,
        onSelectionChanged = { selected = it },
        showDivider = true,
        modifier = Modifier.height(350.dp)
    )
}

@Preview(showBackground = true, name = "Lazy 13. Drag reorder")
@Composable
private fun PreviewLazyDragReorder() {
    var items by remember { mutableStateOf(listOf("Prioritas 1", "Prioritas 2", "Prioritas 3", "Prioritas 4", "Prioritas 5")) }
    KanzanBaseLazy(
        state = KanzanLazyState(items = items),
        itemContent = { index, item, _ ->
            Text(
                text = "${index + 1}. $item",
                style = AppTextStyle.nunito_regular_14,
                modifier = Modifier.fillMaxWidth().padding(dp16)
            )
        },
        enableDragReorder = true,
        onReorder = { from, to ->
            items = items.toMutableList().also {
                val moved = it.removeAt(from)
                it.add(to, moved)
            }
        },
        showDivider = true,
        modifier = Modifier.height(300.dp)
    )
}

@Preview(showBackground = true, name = "Lazy 14. Multi select + swipe")
@Composable
private fun PreviewLazyMultiSelectSwipe() {
    var items by remember { mutableStateOf((1..6).map { "Hutang ke-$it: Rp ${it * 100}.000" }) }
    var selected by remember { mutableStateOf(emptySet<Int>()) }
    KanzanBaseLazy(
        state = KanzanLazyState(items = items),
        itemContent = { _, item, isSelected ->
            Text(
                text = item,
                style = if (isSelected) AppTextStyle.nunito_bold_14 else AppTextStyle.nunito_regular_14,
                modifier = Modifier.fillMaxWidth().padding(dp16)
            )
        },
        selectionMode = KanzanSelectionMode.MULTI,
        selectedIndices = selected,
        onSelectionChanged = { selected = it },
        swipeLeftAction = KanzanSwipeAction(
            backgroundColor = Color(0xFFF44336),
            icon = { Text(text = "🗑️", style = AppTextStyle.nunito_regular_24) },
            onAction = { index ->
                items = items.toMutableList().also { it.removeAt(index) }
                selected = selected.filter { it != index }.map { if (it > index) it - 1 else it }.toSet()
            }
        ),
        showDivider = true,
        modifier = Modifier.height(350.dp)
    )
}

// endregion
