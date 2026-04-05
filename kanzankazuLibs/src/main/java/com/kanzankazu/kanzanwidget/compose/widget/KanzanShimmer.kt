package com.kanzankazu.kanzanwidget.compose.widget

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kanzankazu.kanzanwidget.compose.ui.dp4
import com.kanzankazu.kanzanwidget.compose.ui.dp8
import com.kanzankazu.kanzanwidget.compose.ui.dp12
import com.kanzankazu.kanzanwidget.compose.ui.dp16
import com.kanzankazu.kanzanwidget.compose.ui.dp48
import com.kanzankazu.kanzanwidget.compose.ui.dp64

// region ==================== KanzanShimmer ====================

/**
 * Shimmer placeholder composable. Bisa dipakai untuk loading skeleton apapun.
 *
 * @param modifier Modifier.
 * @param width lebar shimmer (null = fillMaxWidth).
 * @param height tinggi shimmer.
 * @param shape bentuk shimmer.
 * @param baseColor warna dasar shimmer.
 * @param highlightColor warna highlight shimmer.
 * @param durationMs durasi animasi shimmer.
 */
@Composable
fun KanzanShimmer(
    modifier: Modifier = Modifier,
    width: Dp? = null,
    height: Dp = dp16,
    shape: Shape = RoundedCornerShape(dp4),
    baseColor: Color = Color(0xFFE0E0E0),
    highlightColor: Color = Color(0xFFF5F5F5),
    durationMs: Int = 1200,
) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val shimmerX by transition.animateFloat(
        initialValue = -300f,
        targetValue = 900f,
        animationSpec = infiniteRepeatable(tween(durationMs), RepeatMode.Restart),
        label = "shimmerX",
    )
    val brush = Brush.linearGradient(
        colors = listOf(baseColor, highlightColor, baseColor),
        start = Offset(shimmerX, 0f),
        end = Offset(shimmerX + 300f, 0f),
    )
    val sizeModifier = if (width != null) modifier.width(width) else modifier.fillMaxWidth()
    Box(
        modifier = sizeModifier
            .height(height)
            .clip(shape)
            .background(brush),
    )
}

// endregion

// region ==================== Preset Shimmer Layouts ====================

/**
 * Shimmer layout untuk card list item (avatar + 2 baris teks).
 */
@Composable
fun KanzanShimmerListItem(
    modifier: Modifier = Modifier,
    showAvatar: Boolean = true,
    lineCount: Int = 2,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = dp16, vertical = dp12),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (showAvatar) {
            KanzanShimmer(
                width = dp48,
                height = dp48,
                shape = CircleShape,
            )
            Spacer(modifier = Modifier.width(dp12))
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(dp8)) {
            repeat(lineCount) { index ->
                KanzanShimmer(
                    modifier = Modifier.fillMaxWidth(if (index == 0) 0.7f else 0.4f),
                    height = if (index == 0) dp16 else dp12,
                )
            }
        }
    }
}

/**
 * Shimmer layout untuk card.
 */
@Composable
fun KanzanShimmerCard(
    modifier: Modifier = Modifier,
    imageHeight: Dp = 120.dp,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(dp16),
        verticalArrangement = Arrangement.spacedBy(dp8),
    ) {
        KanzanShimmer(height = imageHeight, shape = RoundedCornerShape(dp8))
        KanzanShimmer(modifier = Modifier.fillMaxWidth(0.6f), height = dp16)
        KanzanShimmer(modifier = Modifier.fillMaxWidth(0.9f), height = dp12)
        KanzanShimmer(modifier = Modifier.fillMaxWidth(0.4f), height = dp12)
    }
}

/**
 * Shimmer layout untuk profile header.
 */
@Composable
fun KanzanShimmerProfile(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(dp16),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(dp8),
    ) {
        KanzanShimmer(width = dp64, height = dp64, shape = CircleShape)
        KanzanShimmer(width = 120.dp, height = dp16)
        KanzanShimmer(width = 80.dp, height = dp12)
    }
}

// endregion

// region ==================== Preview ====================

@Preview(showBackground = true, name = "Shimmer 1. Basic shapes")
@Composable
private fun PreviewShimmerBasic() {
    Column(modifier = Modifier.padding(dp16), verticalArrangement = Arrangement.spacedBy(dp8)) {
        KanzanShimmer(height = dp16)
        KanzanShimmer(modifier = Modifier.fillMaxWidth(0.6f), height = dp12)
        KanzanShimmer(width = dp48, height = dp48, shape = CircleShape)
        KanzanShimmer(height = dp48, shape = RoundedCornerShape(dp8))
    }
}

@Preview(showBackground = true, name = "Shimmer 2. List items")
@Composable
private fun PreviewShimmerList() {
    Column {
        repeat(4) { KanzanShimmerListItem() }
    }
}

@Preview(showBackground = true, name = "Shimmer 3. Card")
@Composable
private fun PreviewShimmerCard() {
    KanzanShimmerCard()
}

@Preview(showBackground = true, name = "Shimmer 4. Profile")
@Composable
private fun PreviewShimmerProfile() {
    KanzanShimmerProfile()
}

@Preview(showBackground = true, name = "Shimmer 5. No avatar list")
@Composable
private fun PreviewShimmerNoAvatar() {
    Column {
        repeat(3) { KanzanShimmerListItem(showAvatar = false, lineCount = 3) }
    }
}

// endregion
