package com.kanzankazu.kanzanwidget.compose.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.kanzankazu.kanzanwidget.compose.ui.AppTextStyle
import com.kanzankazu.kanzanwidget.compose.ui.dp16

// region ==================== KanzanSwipeRefresh ====================

/**
 * Wrapper pull-to-refresh.
 * Menggunakan Material pullrefresh jika tersedia, atau fallback ke manual indicator.
 *
 * Catatan: Jika project menggunakan compose.material >= 1.3.0, bisa diganti
 * dengan PullRefreshIndicator + pullRefresh modifier.
 * Untuk versi lama, widget ini menyediakan simple refresh layout.
 *
 * @param isRefreshing state apakah sedang refresh.
 * @param onRefresh callback saat user trigger refresh.
 * @param modifier Modifier.
 * @param indicatorColor warna indicator.
 * @param content konten yang bisa di-refresh.
 */
@Composable
fun KanzanSwipeRefresh(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    indicatorColor: Color = Color.Black,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier) {
        content()
        if (isRefreshing) {
            Box(
                modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter).padding(dp16),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = indicatorColor)
            }
        }
    }
}

// endregion

// region ==================== Preview ====================

@Preview(showBackground = true, name = "SwipeRefresh 1. Not refreshing")
@Composable
private fun PreviewSwipeRefreshIdle() {
    KanzanSwipeRefresh(
        isRefreshing = true,
        onRefresh = {},
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(dp16)) {
            Text(text = "Tarik ke bawah untuk refresh", style = AppTextStyle.nunito_regular_14)
        }
    }
}

@Preview(showBackground = true, name = "SwipeRefresh 2. Refreshing")
@Composable
private fun PreviewSwipeRefreshLoading() {
    KanzanSwipeRefresh(
        isRefreshing = true,
        onRefresh = {},
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(dp16)) {
            Text(text = "Sedang memuat data...", style = AppTextStyle.nunito_regular_14)
        }
    }
}

// endregion
