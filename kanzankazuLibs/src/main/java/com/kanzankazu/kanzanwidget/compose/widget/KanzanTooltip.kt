package com.kanzankazu.kanzanwidget.compose.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.kanzankazu.kanzanwidget.compose.ui.AppTextStyle
import com.kanzankazu.kanzanwidget.compose.ui.dp4
import com.kanzankazu.kanzanwidget.compose.ui.dp8
import com.kanzankazu.kanzanwidget.compose.ui.dp12
import com.kanzankazu.kanzanwidget.compose.ui.dp16

// region ==================== KanzanTooltip ====================

/**
 * Tooltip yang muncul saat content di-tap.
 *
 * @param tooltipText teks tooltip.
 * @param modifier Modifier.
 * @param isVisible kontrol visibilitas (null = internal toggle via tap).
 * @param onDismiss callback saat tooltip ditutup.
 * @param backgroundColor warna background tooltip.
 * @param textColor warna teks tooltip.
 * @param textStyle style teks tooltip.
 * @param shape bentuk tooltip.
 * @param padding padding internal tooltip.
 * @param alignment posisi tooltip relatif terhadap content.
 * @param content composable yang di-wrap tooltip.
 */
@Composable
fun KanzanTooltip(
    tooltipText: String,
    modifier: Modifier = Modifier,
    isVisible: Boolean? = null,
    onDismiss: (() -> Unit)? = null,
    backgroundColor: Color = Color(0xFF323232),
    textColor: Color = Color.White,
    textStyle: TextStyle = AppTextStyle.nunito_regular_12,
    shape: Shape = RoundedCornerShape(dp8),
    padding: Dp = dp8,
    alignment: Alignment = Alignment.TopCenter,
    content: @Composable () -> Unit,
) {
    var internalVisible by remember { mutableStateOf(false) }
    val showTooltip = isVisible ?: internalVisible

    Box(modifier = modifier) {
        Box(
            modifier = Modifier.clickable {
                if (isVisible == null) internalVisible = !internalVisible
            },
        ) {
            content()
        }

        if (showTooltip) {
            Popup(
                alignment = alignment,
                onDismissRequest = {
                    if (isVisible == null) internalVisible = false
                    onDismiss?.invoke()
                },
                properties = PopupProperties(focusable = true),
            ) {
                Box(
                    modifier = Modifier
                        .background(backgroundColor, shape)
                        .padding(horizontal = dp12, vertical = padding),
                ) {
                    Text(text = tooltipText, style = textStyle, color = textColor)
                }
            }
        }
    }
}

// endregion

// region ==================== Preview ====================

@Preview(showBackground = true, name = "Tooltip 1. Tap to show")
@Composable
private fun PreviewTooltipTap() {
    Box(modifier = Modifier.padding(dp16)) {
        KanzanTooltip(tooltipText = "Ini adalah tooltip") {
            Text(text = "Tap saya", style = AppTextStyle.nunito_medium_14, color = Color(0xFF2196F3))
        }
    }
}

@Preview(showBackground = true, name = "Tooltip 2. Always visible")
@Composable
private fun PreviewTooltipVisible() {
    Box(modifier = Modifier.padding(dp16).padding(top = 40.dp)) {
        KanzanTooltip(tooltipText = "Info penting", isVisible = true) {
            Text(text = "Label", style = AppTextStyle.nunito_regular_14)
        }
    }
}

@Preview(showBackground = true, name = "Tooltip 3. Custom style")
@Composable
private fun PreviewTooltipCustom() {
    Box(modifier = Modifier.padding(dp16).padding(top = 40.dp)) {
        KanzanTooltip(
            tooltipText = "Klik untuk detail",
            isVisible = true,
            backgroundColor = Color(0xFF4CAF50),
            shape = RoundedCornerShape(dp4),
        ) {
            Text(text = "💡 Hint", style = AppTextStyle.nunito_medium_14)
        }
    }
}

// endregion
