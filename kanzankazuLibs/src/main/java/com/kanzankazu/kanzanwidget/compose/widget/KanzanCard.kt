package com.kanzankazu.kanzanwidget.compose.widget

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kanzankazu.kanzanwidget.compose.ui.AppTextStyle
import com.kanzankazu.kanzanwidget.compose.ui.Shapes
import com.kanzankazu.kanzanwidget.compose.ui.dp12
import com.kanzankazu.kanzanwidget.compose.ui.dp16
import com.kanzankazu.kanzanwidget.compose.ui.dp4
import com.kanzankazu.kanzanwidget.compose.ui.dp48
import com.kanzankazu.kanzanwidget.compose.ui.dp8

// region ==================== Enums ====================

enum class KanzanCardType { FILLED, OUTLINED, ELEVATED, FILLED_ELEVATED, OUTLINED_ELEVATED }

enum class KanzanCardLayout { VERTICAL, HORIZONTAL }

// endregion

// region ==================== KanzanCard ====================

/**
 * Card generic yang support berbagai tipe dan layout.
 *
 * @param modifier Modifier.
 * @param onClick callback klik (null = tidak clickable).
 * @param cardType tipe card (FILLED, OUTLINED, ELEVATED).
 * @param layout layout card (VERTICAL, HORIZONTAL).
 * @param shape bentuk card.
 * @param containerColor warna background card.
 * @param contentColor warna konten card.
 * @param borderColor warna border (untuk OUTLINED).
 * @param borderWidth lebar border (untuk OUTLINED).
 * @param elevation elevasi card.
 * @param shadowColor warna shadow card (default: Color.Black).
 * @param contentPadding padding internal konten.
 * @param isSkeleton tampilkan skeleton shimmer.
 * @param skeletonHeight tinggi skeleton.
 * @param title teks judul.
 * @param subtitle teks sub-judul.
 * @param description teks deskripsi.
 * @param titleStyle style teks title.
 * @param subtitleStyle style teks subtitle.
 * @param descriptionStyle style teks description.
 * @param titleColor warna title.
 * @param subtitleColor warna subtitle.
 * @param descriptionColor warna description.
 * @param titleMaxLines max baris title.
 * @param descriptionMaxLines max baris description.
 * @param leadingIcon composable icon di kiri (HORIZONTAL) atau atas (VERTICAL).
 * @param trailingContent composable di kanan (HORIZONTAL) atau bawah title area.
 * @param topContent composable di atas title (misal: image banner).
 * @param bottomContent composable di bawah semua konten (misal: action buttons).
 * @param showDividerBeforeBottom tampilkan divider sebelum bottomContent.
 * @param badge composable badge (misal: status label).
 * @param customContent composable kustom (menggantikan seluruh konten default).
 */
@Composable
fun KanzanCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    cardType: KanzanCardType = KanzanCardType.FILLED,
    layout: KanzanCardLayout = KanzanCardLayout.VERTICAL,
    shape: Shape = Shapes.medium,
    containerColor: Color = Color.White,
    contentColor: Color = Color.Black,
    borderColor: Color = Color.LightGray,
    borderWidth: Dp = 1.dp,
    elevation: Dp = dp4,
    shadowColor: Color = Color.Black,
    contentPadding: Dp = dp16,
    isSkeleton: Boolean = false,
    skeletonHeight: Dp = 120.dp,
    title: String? = null,
    subtitle: String? = null,
    description: String? = null,
    titleStyle: TextStyle = AppTextStyle.nunito_bold_16,
    subtitleStyle: TextStyle = AppTextStyle.nunito_medium_12,
    descriptionStyle: TextStyle = AppTextStyle.nunito_regular_14,
    titleColor: Color = contentColor,
    subtitleColor: Color = Color.Gray,
    descriptionColor: Color = Color.DarkGray,
    titleMaxLines: Int = 2,
    descriptionMaxLines: Int = 3,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    topContent: @Composable (() -> Unit)? = null,
    bottomContent: @Composable (() -> Unit)? = null,
    showDividerBeforeBottom: Boolean = false,
    badge: @Composable (() -> Unit)? = null,
    customContent: @Composable (() -> Unit)? = null,
) {
    val shadowModifier = if (elevation > 0.dp) {
        modifier.shadow(elevation = elevation, shape = shape, ambientColor = shadowColor, spotColor = shadowColor)
    } else {
        modifier
    }
    val cardModifier = if (onClick != null) shadowModifier.clickable { onClick() } else shadowModifier

    val colors = CardDefaults.cardColors(
        containerColor = containerColor,
        contentColor = contentColor,
    )
    val elevatedColors = CardDefaults.elevatedCardColors(
        containerColor = containerColor,
        contentColor = contentColor,
    )
    val outlinedColors = CardDefaults.outlinedCardColors(
        containerColor = containerColor,
        contentColor = contentColor,
    )

    val content: @Composable () -> Unit = {
        if (isSkeleton) {
            KanzanShimmerCard(modifier = Modifier.padding(contentPadding), imageHeight = skeletonHeight)
        } else if (customContent != null) {
            customContent()
        } else {
            KanzanCardContent(
                layout = layout,
                contentPadding = contentPadding,
                topContent = topContent,
                leadingIcon = leadingIcon,
                trailingContent = trailingContent,
                title = title,
                subtitle = subtitle,
                description = description,
                titleStyle = titleStyle,
                subtitleStyle = subtitleStyle,
                descriptionStyle = descriptionStyle,
                titleColor = titleColor,
                subtitleColor = subtitleColor,
                descriptionColor = descriptionColor,
                titleMaxLines = titleMaxLines,
                descriptionMaxLines = descriptionMaxLines,
                badge = badge,
                bottomContent = bottomContent,
                showDividerBeforeBottom = showDividerBeforeBottom,
            )
        }
    }

    when (cardType) {
        KanzanCardType.FILLED -> Card(
            modifier = cardModifier,
            shape = shape,
            colors = colors,
        ) { content() }

        KanzanCardType.OUTLINED -> OutlinedCard(
            modifier = cardModifier,
            shape = shape,
            colors = outlinedColors,
            border = BorderStroke(borderWidth, borderColor),
        ) { content() }

        KanzanCardType.ELEVATED -> ElevatedCard(
            modifier = cardModifier,
            shape = shape,
            colors = elevatedColors,
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = elevation),
        ) { content() }

        KanzanCardType.FILLED_ELEVATED -> ElevatedCard(
            modifier = cardModifier,
            shape = shape,
            colors = CardDefaults.elevatedCardColors(
                containerColor = containerColor,
                contentColor = contentColor,
            ),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = elevation),
        ) { content() }

        KanzanCardType.OUTLINED_ELEVATED -> Card(
            modifier = cardModifier,
            shape = shape,
            colors = CardDefaults.cardColors(
                containerColor = containerColor,
                contentColor = contentColor,
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = elevation),
            border = BorderStroke(borderWidth, borderColor),
        ) { content() }
    }
}

@Composable
private fun KanzanCardContent(
    layout: KanzanCardLayout,
    contentPadding: Dp,
    topContent: @Composable (() -> Unit)?,
    leadingIcon: @Composable (() -> Unit)?,
    trailingContent: @Composable (() -> Unit)?,
    title: String?,
    subtitle: String?,
    description: String?,
    titleStyle: TextStyle,
    subtitleStyle: TextStyle,
    descriptionStyle: TextStyle,
    titleColor: Color,
    subtitleColor: Color,
    descriptionColor: Color,
    titleMaxLines: Int,
    descriptionMaxLines: Int,
    badge: @Composable (() -> Unit)?,
    bottomContent: @Composable (() -> Unit)?,
    showDividerBeforeBottom: Boolean,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Top content (image banner, etc)
        topContent?.invoke()

        when (layout) {
            KanzanCardLayout.VERTICAL -> {
                Column(modifier = Modifier.fillMaxWidth().padding(contentPadding)) {
                    KanzanCardTextBlock(
                        title = title,
                        subtitle = subtitle,
                        description = description,
                        titleStyle = titleStyle,
                        subtitleStyle = subtitleStyle,
                        descriptionStyle = descriptionStyle,
                        titleColor = titleColor,
                        subtitleColor = subtitleColor,
                        descriptionColor = descriptionColor,
                        titleMaxLines = titleMaxLines,
                        descriptionMaxLines = descriptionMaxLines,
                        badge = badge,
                    )
                    trailingContent?.invoke()
                }
            }

            KanzanCardLayout.HORIZONTAL -> {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(contentPadding),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    leadingIcon?.let {
                        it()
                        KanzanSpacerHorizontal(width = dp12)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        KanzanCardTextBlock(
                            title = title,
                            subtitle = subtitle,
                            description = description,
                            titleStyle = titleStyle,
                            subtitleStyle = subtitleStyle,
                            descriptionStyle = descriptionStyle,
                            titleColor = titleColor,
                            subtitleColor = subtitleColor,
                            descriptionColor = descriptionColor,
                            titleMaxLines = titleMaxLines,
                            descriptionMaxLines = descriptionMaxLines,
                            badge = badge,
                        )
                    }
                    trailingContent?.let {
                        KanzanSpacerHorizontal(width = dp12)
                        it()
                    }
                }
            }
        }

        // Bottom content (action buttons, etc)
        if (bottomContent != null) {
            if (showDividerBeforeBottom) {
                Divider(color = Color.LightGray.copy(alpha = 0.5f))
            }
            bottomContent()
        }
    }
}

@Composable
private fun KanzanCardTextBlock(
    title: String?,
    subtitle: String?,
    description: String?,
    titleStyle: TextStyle,
    subtitleStyle: TextStyle,
    descriptionStyle: TextStyle,
    titleColor: Color,
    subtitleColor: Color,
    descriptionColor: Color,
    titleMaxLines: Int,
    descriptionMaxLines: Int,
    badge: @Composable (() -> Unit)?,
) {
    if (title != null) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = title,
                style = titleStyle,
                color = titleColor,
                maxLines = titleMaxLines,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false),
            )
            badge?.let {
                KanzanSpacerHorizontal(width = dp8)
                it()
            }
        }
    }
    if (subtitle != null) {
        if (title != null) Spacer(modifier = Modifier.height(dp4))
        Text(
            text = subtitle,
            style = subtitleStyle,
            color = subtitleColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
    if (description != null) {
        Spacer(modifier = Modifier.height(dp8))
        Text(
            text = description,
            style = descriptionStyle,
            color = descriptionColor,
            maxLines = descriptionMaxLines,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

// endregion


// region ==================== Preview ====================

@Preview(showBackground = true, name = "Card 1. Basic filled")
@Composable
private fun PreviewCardBasic() {
    KanzanCard(
        modifier = Modifier.fillMaxWidth().padding(dp16),
        title = "Hutang Rumah",
        subtitle = "Jatuh tempo: 15 Jan 2026",
        description = "Cicilan bulanan untuk renovasi rumah. Sisa 12 bulan lagi.",
    )
}

@Preview(showBackground = true, name = "Card 2. Outlined")
@Composable
private fun PreviewCardOutlined() {
    KanzanCard(
        modifier = Modifier.fillMaxWidth().padding(dp16),
        cardType = KanzanCardType.OUTLINED,
        title = "Transfer Bank",
        subtitle = "BCA - 1234567890",
        description = "Rp 500.000",
    )
}

@Preview(showBackground = true, name = "Card 3. Elevated")
@Composable
private fun PreviewCardElevated() {
    KanzanCard(
        modifier = Modifier.fillMaxWidth().padding(dp16),
        cardType = KanzanCardType.ELEVATED,
        title = "Ringkasan Bulan Ini",
        description = "Total hutang: Rp 15.000.000\nTotal piutang: Rp 8.000.000",
    )
}

@Preview(showBackground = true, name = "Card 4. Horizontal with icon")
@Composable
private fun PreviewCardHorizontal() {
    KanzanCard(
        modifier = Modifier.fillMaxWidth().padding(dp16),
        cardType = KanzanCardType.OUTLINED,
        layout = KanzanCardLayout.HORIZONTAL,
        title = "Ahmad Fauzi",
        subtitle = "Hutang aktif: 3",
        leadingIcon = {
            KanzanTextDrawable(text = "AF", size = dp48)
        },
        trailingContent = {
            Text(text = "Rp 2.5jt", style = AppTextStyle.nunito_bold_14, color = Color.Red)
        },
    )
}

@Preview(showBackground = true, name = "Card 5. With badge")
@Composable
private fun PreviewCardBadge() {
    KanzanCard(
        modifier = Modifier.fillMaxWidth().padding(dp16),
        cardType = KanzanCardType.OUTLINED,
        layout = KanzanCardLayout.HORIZONTAL,
        title = "Cicilan Motor",
        subtitle = "Sisa 6 bulan",
        leadingIcon = {
            Text(text = "🏍️", style = AppTextStyle.nunito_regular_36)
        },
        badge = {
            Box(
                modifier = Modifier
                    .padding(dp4)
            ) {
                Text(text = "Aktif", style = AppTextStyle.nunito_bold_12, color = Color(0xFF4CAF50))
            }
        },
    )
}

@Preview(showBackground = true, name = "Card 6. With bottom actions")
@Composable
private fun PreviewCardActions() {
    KanzanCard(
        modifier = Modifier.fillMaxWidth().padding(dp16),
        cardType = KanzanCardType.ELEVATED,
        title = "Hutang ke Budi",
        description = "Pinjaman untuk modal usaha. Jatuh tempo minggu depan.",
        showDividerBeforeBottom = true,
        bottomContent = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(dp12),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                KanzanBaseButton(
                    title = "Bayar",
                    onClick = {},
                    buttonSize = KanzanButtonSize.SMALL,
                    buttonType = KanzanButtonType.TEXT,
                )
                KanzanSpacerHorizontal(width = dp8)
                KanzanBaseButton(
                    title = "Detail",
                    onClick = {},
                    buttonSize = KanzanButtonSize.SMALL,
                    buttonType = KanzanButtonType.OUTLINED,
                )
            }
        },
    )
}

@Preview(showBackground = true, name = "Card 7. With top image")
@Composable
private fun PreviewCardTopImage() {
    KanzanCard(
        modifier = Modifier.fillMaxWidth().padding(dp16),
        cardType = KanzanCardType.ELEVATED,
        title = "Promo Spesial",
        description = "Dapatkan cashback 10% untuk pembayaran hutang tepat waktu.",
        topContent = {
            KanzanShimmer(height = 120.dp)
        },
    )
}

@Preview(showBackground = true, name = "Card 8. Skeleton")
@Composable
private fun PreviewCardSkeleton() {
    KanzanCard(
        modifier = Modifier.fillMaxWidth().padding(dp16),
        isSkeleton = true,
    )
}

@Preview(showBackground = true, name = "Card 9. Custom content")
@Composable
private fun PreviewCardCustom() {
    KanzanCard(
        modifier = Modifier.fillMaxWidth().padding(dp16),
        cardType = KanzanCardType.OUTLINED,
        customContent = {
            Column(modifier = Modifier.padding(dp16)) {
                Text(text = "💰 Total Tabungan", style = AppTextStyle.nunito_medium_14, color = Color.Gray)
                Spacer(modifier = Modifier.height(dp4))
                Text(text = "Rp 25.000.000", style = AppTextStyle.nunito_bold_16)
                Spacer(modifier = Modifier.height(dp8))
                KanzanProgressBar(
                    progress = 0.65f,
                    label = "Target: Rp 40.000.000",
                    progressColor = Color(0xFF4CAF50),
                )
            }
        },
    )
}

@Preview(showBackground = true, name = "Card 10. List of cards")
@Composable
private fun PreviewCardList() {
    Column(modifier = Modifier.padding(dp16), verticalArrangement = Arrangement.spacedBy(dp8)) {
        listOf("Hutang A" to "Rp 1.000.000", "Hutang B" to "Rp 2.500.000", "Hutang C" to "Rp 750.000").forEach { (name, amount) ->
            KanzanCard(
                modifier = Modifier.fillMaxWidth(),
                cardType = KanzanCardType.OUTLINED,
                layout = KanzanCardLayout.HORIZONTAL,
                title = name,
                subtitle = amount,
                leadingIcon = {
                    Text(text = "💳", style = AppTextStyle.nunito_regular_16)
                },
                onClick = {},
            )
        }
    }
}

@Preview(showBackground = true, name = "Card 11. Filled Elevated")
@Composable
private fun PreviewCardFilledElevated() {
    KanzanCard(
        modifier = Modifier.fillMaxWidth().padding(dp16),
        cardType = KanzanCardType.FILLED_ELEVATED,
        title = "Tabungan Haji",
        subtitle = "Target: Rp 50.000.000",
        description = "Sudah terkumpul 60% dari target.",
        elevation = dp8,
    )
}

@Preview(showBackground = true, name = "Card 12. Outlined Elevated")
@Composable
private fun PreviewCardOutlinedElevated() {
    KanzanCard(
        modifier = Modifier.fillMaxWidth().padding(dp16),
        cardType = KanzanCardType.OUTLINED_ELEVATED,
        title = "Cicilan Laptop",
        subtitle = "Sisa 3 bulan",
        description = "Rp 1.500.000 / bulan",
        borderColor = Color(0xFF2196F3),
        elevation = dp8,
    )
}

@Preview(showBackground = true, name = "Card 13. Filled Elevated Horizontal")
@Composable
private fun PreviewCardFilledElevatedHorizontal() {
    KanzanCard(
        modifier = Modifier.fillMaxWidth().padding(dp16),
        cardType = KanzanCardType.FILLED_ELEVATED,
        layout = KanzanCardLayout.HORIZONTAL,
        title = "Saldo Utama",
        subtitle = "Rp 12.500.000",
        leadingIcon = {
            Text(text = "💰", style = AppTextStyle.nunito_regular_36)
        },
        elevation = dp8,
    )
}

@Preview(showBackground = true, name = "Card 14. Outlined Elevated with actions")
@Composable
private fun PreviewCardOutlinedElevatedActions() {
    KanzanCard(
        modifier = Modifier.fillMaxWidth().padding(dp16),
        cardType = KanzanCardType.OUTLINED_ELEVATED,
        title = "Tagihan Listrik",
        description = "Jatuh tempo: 20 Jan 2026",
        elevation = dp4,
        showDividerBeforeBottom = true,
        bottomContent = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(dp12),
                horizontalArrangement = Arrangement.End,
            ) {
                KanzanBaseButton(
                    title = "Bayar Sekarang",
                    onClick = {},
                    buttonSize = KanzanButtonSize.SMALL,
                )
            }
        },
    )
}

// endregion
