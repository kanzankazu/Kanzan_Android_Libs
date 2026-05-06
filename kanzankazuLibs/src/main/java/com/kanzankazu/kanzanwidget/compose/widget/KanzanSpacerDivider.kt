package com.kanzankazu.kanzanwidget.compose.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kanzankazu.kanzanwidget.compose.ui.AppTextStyle
import com.kanzankazu.kanzanwidget.compose.ui.dp1
import com.kanzankazu.kanzanwidget.compose.ui.dp4
import com.kanzankazu.kanzanwidget.compose.ui.dp8
import com.kanzankazu.kanzanwidget.compose.ui.dp16
import com.kanzankazu.kanzanwidget.compose.ui.dp24

// region ==================== KanzanSpacer ====================

/** Spacer vertikal (tinggi). */
@Composable
fun KanzanSpacerVertical(height: Dp = dp16) {
    Spacer(modifier = Modifier.height(height))
}

/** Spacer horizontal (lebar). */
@Composable
fun KanzanSpacerHorizontal(width: Dp = dp16) {
    KanzanSpacerHorizontal(width = width)
}

// endregion

// region ==================== KanzanDivider ====================

/**
 * Divider horizontal.
 *
 * @param modifier Modifier.
 * @param thickness ketebalan garis.
 * @param color warna garis.
 * @param startIndent indent dari kiri.
 * @param endIndent indent dari kanan.
 */
@Composable
fun KanzanDividerHorizontal(
    modifier: Modifier = Modifier,
    thickness: Dp = dp1,
    color: Color = Color.LightGray.copy(alpha = 0.5f),
    startIndent: Dp = 0.dp,
    endIndent: Dp = 0.dp,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = startIndent, end = endIndent)
            .height(thickness)
            .background(color),
    )
}

/**
 * Divider vertikal.
 *
 * @param modifier Modifier.
 * @param thickness ketebalan garis.
 * @param color warna garis.
 * @param height tinggi divider (null = fillMaxHeight).
 * @param topIndent indent dari atas.
 * @param bottomIndent indent dari bawah.
 */
@Composable
fun KanzanDividerVertical(
    modifier: Modifier = Modifier,
    thickness: Dp = dp1,
    color: Color = Color.LightGray.copy(alpha = 0.5f),
    height: Dp? = null,
    topIndent: Dp = 0.dp,
    bottomIndent: Dp = 0.dp,
) {
    val heightModifier = if (height != null) Modifier.height(height) else Modifier.fillMaxHeight()
    Box(
        modifier = modifier
            .then(heightModifier)
            .padding(top = topIndent, bottom = bottomIndent)
            .width(thickness)
            .background(color),
    )
}

// endregion

// region ==================== Preview ====================

@Preview(showBackground = true, name = "Spacer 1. Vertical sizes")
@Composable
private fun PreviewSpacerVertical() {
    Column(modifier = Modifier.padding(dp16)) {
        Text(text = "Above (4dp)", style = AppTextStyle.nunito_regular_14)
        KanzanSpacerVertical(dp4)
        Text(text = "Above (8dp)", style = AppTextStyle.nunito_regular_14)
        KanzanSpacerVertical(dp8)
        Text(text = "Above (16dp)", style = AppTextStyle.nunito_regular_14)
        KanzanSpacerVertical(dp16)
        Text(text = "Above (24dp)", style = AppTextStyle.nunito_regular_14)
        KanzanSpacerVertical(dp24)
        Text(text = "Bottom", style = AppTextStyle.nunito_regular_14)
    }
}

@Preview(showBackground = true, name = "Spacer 2. Horizontal sizes")
@Composable
private fun PreviewSpacerHorizontal() {
    Row(modifier = Modifier.padding(dp16)) {
        Text(text = "A", style = AppTextStyle.nunito_regular_14)
        KanzanSpacerHorizontal(dp4)
        Text(text = "B", style = AppTextStyle.nunito_regular_14)
        KanzanSpacerHorizontal(dp16)
        Text(text = "C", style = AppTextStyle.nunito_regular_14)
        KanzanSpacerHorizontal(dp24)
        Text(text = "D", style = AppTextStyle.nunito_regular_14)
    }
}

@Preview(showBackground = true, name = "Divider 1. Horizontal default")
@Composable
private fun PreviewDividerHorizontal() {
    Column(modifier = Modifier.padding(dp16), verticalArrangement = Arrangement.spacedBy(dp8)) {
        Text(text = "Item 1", style = AppTextStyle.nunito_regular_14)
        KanzanDividerHorizontal()
        Text(text = "Item 2", style = AppTextStyle.nunito_regular_14)
        KanzanDividerHorizontal(color = Color.Red, thickness = 2.dp)
        Text(text = "Item 3", style = AppTextStyle.nunito_regular_14)
    }
}

@Preview(showBackground = true, name = "Divider 2. Horizontal with indent")
@Composable
private fun PreviewDividerHorizontalIndent() {
    Column(modifier = Modifier.padding(dp16), verticalArrangement = Arrangement.spacedBy(dp8)) {
        Text(text = "Full width", style = AppTextStyle.nunito_regular_14)
        KanzanDividerHorizontal()
        Text(text = "Start indent 16dp", style = AppTextStyle.nunito_regular_14)
        KanzanDividerHorizontal(startIndent = dp16)
        Text(text = "Both indent 24dp", style = AppTextStyle.nunito_regular_14)
        KanzanDividerHorizontal(startIndent = dp24, endIndent = dp24)
    }
}

@Preview(showBackground = true, name = "Divider 3. Vertical")
@Composable
private fun PreviewDividerVertical() {
    Row(
        modifier = Modifier.padding(dp16).height(60.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = "Left", style = AppTextStyle.nunito_regular_14)
        KanzanSpacerHorizontal(dp8)
        KanzanDividerVertical()
        KanzanSpacerHorizontal(dp8)
        Text(text = "Center", style = AppTextStyle.nunito_regular_14)
        KanzanSpacerHorizontal(dp8)
        KanzanDividerVertical(color = Color.Red, thickness = 2.dp)
        KanzanSpacerHorizontal(dp8)
        Text(text = "Right", style = AppTextStyle.nunito_regular_14)
    }
}

@Preview(showBackground = true, name = "Divider 4. Vertical fixed height")
@Composable
private fun PreviewDividerVerticalFixed() {
    Row(modifier = Modifier.padding(dp16), verticalAlignment = Alignment.CenterVertically) {
        Text(text = "A", style = AppTextStyle.nunito_bold_16)
        KanzanSpacerHorizontal(dp8)
        KanzanDividerVertical(height = dp24)
        KanzanSpacerHorizontal(dp8)
        Text(text = "B", style = AppTextStyle.nunito_bold_16)
    }
}

// endregion
