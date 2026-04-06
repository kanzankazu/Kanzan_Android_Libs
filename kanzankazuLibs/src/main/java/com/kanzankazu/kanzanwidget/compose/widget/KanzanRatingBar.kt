package com.kanzankazu.kanzanwidget.compose.widget

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kanzankazu.R
import com.kanzankazu.kanzanwidget.compose.ui.AppTextStyle
import com.kanzankazu.kanzanwidget.compose.ui.dp4
import com.kanzankazu.kanzanwidget.compose.ui.dp8
import com.kanzankazu.kanzanwidget.compose.ui.dp16
import com.kanzankazu.kanzanwidget.compose.ui.dp24
import com.kanzankazu.kanzanwidget.compose.ui.dp32

// region ==================== KanzanRatingBar ====================

/**
 * Star rating bar composable menggunakan drawable resource.
 *
 * @param rating nilai rating saat ini (0f - [maxStars]).
 * @param onRatingChanged callback saat rating berubah (null = read-only).
 * @param modifier Modifier.
 * @param maxStars jumlah bintang maksimal.
 * @param starSize ukuran per bintang.
 * @param filledStarPainter painter untuk bintang terisi (default: ic_rating_star_active).
 * @param emptyStarPainter painter untuk bintang kosong (default: ic_rating_star_inactive).
 * @param halfStarPainter painter untuk bintang setengah (default: sama dengan filled).
 * @param starTint opsional color filter untuk tint bintang aktif (null = pakai warna asli drawable).
 * @param starTintInactive opsional color filter untuk tint bintang inactive (null = pakai warna asli drawable).
 * @param allowHalf izinkan rating setengah bintang.
 * @param showLabel tampilkan label rating (misal "4.5/5").
 * @param labelStyle style teks label.
 * @param labelColor warna label.
 * @param spacing jarak antar bintang.
 */
@Composable
fun KanzanRatingBar(
    rating: Float,
    onRatingChanged: ((Float) -> Unit)? = null,
    modifier: Modifier = Modifier,
    maxStars: Int = 5,
    starSize: Dp = dp32,
    filledStarPainter: Painter = painterResource(id = R.drawable.ic_rating_star_active),
    emptyStarPainter: Painter = painterResource(id = R.drawable.ic_rating_star_inactive),
    halfStarPainter: Painter = filledStarPainter,
    starTint: ColorFilter? = null,
    starTintInactive: ColorFilter? = null,
    allowHalf: Boolean = false,
    showLabel: Boolean = false,
    labelStyle: TextStyle = AppTextStyle.nunito_medium_14,
    labelColor: Color = Color.DarkGray,
    spacing: Dp = dp4,
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Row(horizontalArrangement = Arrangement.spacedBy(spacing)) {
            for (i in 1..maxStars) {
                val painter = when {
                    rating >= i -> filledStarPainter
                    allowHalf && rating >= i - 0.5f -> halfStarPainter
                    else -> emptyStarPainter
                }
                val tint = when {
                    rating >= i -> starTint
                    allowHalf && rating >= i - 0.5f -> starTint
                    else -> starTintInactive
                }
                Image(
                    painter = painter,
                    contentDescription = "Star $i",
                    colorFilter = tint,
                    modifier = Modifier
                        .size(starSize)
                        .then(
                            if (onRatingChanged != null) {
                                Modifier.clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                ) {
                                    val newRating = if (allowHalf && rating == i.toFloat()) {
                                        i - 0.5f
                                    } else {
                                        i.toFloat()
                                    }
                                    onRatingChanged(newRating)
                                }
                            } else Modifier
                        ),
                )
            }
        }
        if (showLabel) {
            Text(
                text = "${"%.1f".format(rating)}/$maxStars",
                style = labelStyle,
                color = labelColor,
                modifier = Modifier.padding(start = dp8),
            )
        }
    }
}

// endregion

// region ==================== Preview ====================

@Preview(showBackground = true, name = "Rating 1. Read-only")
@Composable
private fun PreviewRatingReadOnly() {
    Column(modifier = Modifier.padding(dp16), verticalArrangement = Arrangement.spacedBy(dp8)) {
        KanzanRatingBar(rating = 4.5f, showLabel = true)
        KanzanRatingBar(rating = 3f, showLabel = true)
        KanzanRatingBar(rating = 0f, showLabel = true)
    }
}

@Preview(showBackground = true, name = "Rating 2. Interactive")
@Composable
private fun PreviewRatingInteractive() {
    var rating by remember { mutableStateOf(3.5f) }
    KanzanRatingBar(
        rating = rating,
        onRatingChanged = { rating = it },
        modifier = Modifier.padding(dp16),
        showLabel = true,
    )
}

@Preview(showBackground = true, name = "Rating 3. Small")
@Composable
private fun PreviewRatingSmall() {
    KanzanRatingBar(
        rating = 4f,
        modifier = Modifier.padding(dp16),
        starSize = dp24,
    )
}

@Preview(showBackground = true, name = "Rating 4. Custom tint")
@Composable
private fun PreviewRatingTint() {
    var rating by remember { mutableStateOf(4f) }
    KanzanRatingBar(
        rating = rating,
        onRatingChanged = { rating = it },
        modifier = Modifier.padding(dp16),
        starTint = ColorFilter.tint(Color(0xFFFFD700)),
        showLabel = true,
    )
}

@Preview(showBackground = true, name = "Rating 5. Large")
@Composable
private fun PreviewRatingLarge() {
    var rating by remember { mutableStateOf(2.5f) }
    KanzanRatingBar(
        rating = rating,
        onRatingChanged = { rating = it },
        modifier = Modifier.padding(dp16),
        starSize = 48.dp,
        spacing = dp8,
        showLabel = true,
    )
}

// endregion
