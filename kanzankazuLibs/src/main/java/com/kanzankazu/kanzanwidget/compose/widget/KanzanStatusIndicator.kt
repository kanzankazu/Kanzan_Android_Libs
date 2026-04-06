package com.kanzankazu.kanzanwidget.compose.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.kanzankazu.kanzanwidget.compose.ui.AppTextStyle
import com.kanzankazu.kanzanwidget.compose.ui.dp4
import com.kanzankazu.kanzanwidget.compose.ui.dp6
import com.kanzankazu.kanzanwidget.compose.ui.dp8
import com.kanzankazu.kanzanwidget.compose.ui.dp12
import com.kanzankazu.kanzanwidget.compose.ui.dp16

// region ==================== KanzanStatusIndicator ====================

/**
 * Status indicator — dot + label, atau chip badge.
 *
 * @param label teks status.
 * @param color warna status.
 * @param modifier Modifier.
 * @param showDot tampilkan dot di kiri label.
 * @param dotSize ukuran dot.
 * @param asChip tampilkan sebagai chip (background color).
 * @param chipBackgroundAlpha alpha background chip.
 * @param chipShape bentuk chip.
 * @param textStyle style teks.
 * @param textColor warna teks (null = auto dari color).
 * @param leadingIcon composable icon di kiri (sebelum dot).
 * @param trailingIcon composable icon di kanan (setelah label).
 */
@Composable
fun KanzanStatusIndicator(
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
    showDot: Boolean = true,
    dotSize: Dp = dp8,
    asChip: Boolean = false,
    chipBackgroundAlpha: Float = 0.15f,
    chipShape: Shape = RoundedCornerShape(dp12),
    textStyle: TextStyle = AppTextStyle.nunito_medium_12,
    textColor: Color? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    val resolvedTextColor = textColor ?: color

    if (asChip) {
        Row(
            modifier = modifier
                .background(color.copy(alpha = chipBackgroundAlpha), chipShape)
                .padding(horizontal = dp12, vertical = dp4),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            leadingIcon?.let {
                it()
                Spacer(modifier = Modifier.width(dp6))
            }
            if (showDot) {
                Box(modifier = Modifier.size(dotSize).background(color, CircleShape))
                Spacer(modifier = Modifier.width(dp6))
            }
            Text(text = label, style = textStyle, color = resolvedTextColor)
            trailingIcon?.let {
                Spacer(modifier = Modifier.width(dp6))
                it()
            }
        }
    } else {
        Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
            leadingIcon?.let {
                it()
                Spacer(modifier = Modifier.width(dp6))
            }
            if (showDot) {
                Box(modifier = Modifier.size(dotSize).background(color, CircleShape))
                Spacer(modifier = Modifier.width(dp6))
            }
            Text(text = label, style = textStyle, color = resolvedTextColor)
            trailingIcon?.let {
                Spacer(modifier = Modifier.width(dp6))
                it()
            }
        }
    }
}

// endregion

// region ==================== Preview ====================

@Preview(showBackground = true, name = "Status 1. Dot + label")
@Composable
private fun PreviewStatusDot() {
    Column(modifier = Modifier.padding(dp16), verticalArrangement = Arrangement.spacedBy(dp8)) {
        KanzanStatusIndicator(label = "Aktif", color = Color(0xFF4CAF50))
        KanzanStatusIndicator(label = "Pending", color = Color(0xFFFF9800))
        KanzanStatusIndicator(label = "Gagal", color = Color(0xFFF44336))
        KanzanStatusIndicator(label = "Draft", color = Color.Gray)
    }
}

@Preview(showBackground = true, name = "Status 2. Chip style")
@Composable
private fun PreviewStatusChip() {
    Column(modifier = Modifier.padding(dp16), verticalArrangement = Arrangement.spacedBy(dp8)) {
        KanzanStatusIndicator(label = "Lunas", color = Color(0xFF4CAF50), asChip = true)
        KanzanStatusIndicator(label = "Belum Lunas", color = Color(0xFFF44336), asChip = true)
        KanzanStatusIndicator(label = "Jatuh Tempo", color = Color(0xFFFF9800), asChip = true)
    }
}

@Preview(showBackground = true, name = "Status 3. Chip no dot")
@Composable
private fun PreviewStatusChipNoDot() {
    Row(modifier = Modifier.padding(dp16), horizontalArrangement = Arrangement.spacedBy(dp8)) {
        KanzanStatusIndicator(label = "Baru", color = Color(0xFF2196F3), asChip = true, showDot = false)
        KanzanStatusIndicator(label = "Proses", color = Color(0xFFFF9800), asChip = true, showDot = false)
        KanzanStatusIndicator(label = "Selesai", color = Color(0xFF4CAF50), asChip = true, showDot = false)
    }
}

@Preview(showBackground = true, name = "Status 4. With leading icon")
@Composable
private fun PreviewStatusLeadingIcon() {
    Column(modifier = Modifier.padding(dp16), verticalArrangement = Arrangement.spacedBy(dp8)) {
        KanzanStatusIndicator(
            label = "Terverifikasi",
            color = Color(0xFF4CAF50),
            asChip = true,
            showDot = false,
            leadingIcon = { Text(text = "✅", style = AppTextStyle.nunito_regular_12) },
        )
        KanzanStatusIndicator(
            label = "Menunggu",
            color = Color(0xFFFF9800),
            asChip = true,
            showDot = false,
            leadingIcon = { Text(text = "⏳", style = AppTextStyle.nunito_regular_12) },
        )
    }
}

@Preview(showBackground = true, name = "Status 5. With trailing icon")
@Composable
private fun PreviewStatusTrailingIcon() {
    Column(modifier = Modifier.padding(dp16), verticalArrangement = Arrangement.spacedBy(dp8)) {
        KanzanStatusIndicator(
            label = "Lunas",
            color = Color(0xFF4CAF50),
            asChip = true,
            trailingIcon = { Text(
                text = "→",
                style = AppTextStyle.nunito_regular_12,
                color = Color(0xFF4CAF50),
                textAlign = TextAlign.Center
            ) },
        )
        KanzanStatusIndicator(
            label = "Overdue",
            color = Color(0xFFF44336),
            trailingIcon = { Text(text = "⚠️", style = AppTextStyle.nunito_regular_12) },
        )
    }
}

@Preview(showBackground = true, name = "Status 6. Leading + trailing")
@Composable
private fun PreviewStatusBothIcons() {
    KanzanStatusIndicator(
        label = "Premium",
        color = Color(0xFFFFD700),
        modifier = Modifier.padding(dp16),
        asChip = true,
        showDot = false,
        leadingIcon = { Text(text = "⭐", style = AppTextStyle.nunito_regular_12) },
        trailingIcon = { Text(text = "✕", style = AppTextStyle.nunito_regular_12, color = Color(0xFFFFD700)) },
    )
}

// endregion
