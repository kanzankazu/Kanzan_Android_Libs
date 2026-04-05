package com.kanzankazu.kanzanwidget.compose.widget

import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.kanzankazu.kanzanwidget.compose.ui.AppTextStyle
import com.kanzankazu.kanzanwidget.compose.ui.dp4
import com.kanzankazu.kanzanwidget.compose.ui.dp8
import com.kanzankazu.kanzanwidget.compose.ui.dp16
import com.kanzankazu.kanzanwidget.compose.ui.dp24
import com.kanzankazu.kanzanwidget.compose.ui.dp32

// region ==================== KanzanRatingBar ====================

/**
 * Star rating bar composable.
 *
 * @param rating nilai rating saat ini (0f - [maxStars]).
 * @param onRatingChanged callback saat rating berubah (null = read-only).
 * @param modifier Modifier.
 * @param maxStars jumlah bintang maksimal.
 * @param starSize ukuran per bintang.
 * @param filledStar emoji/composable bintang terisi.
 * @param halfStar emoji/composable bintang setengah.
 * @param emptyStar emoji/composable bintang kosong.
 * @param allowHalf izinkan rating setengah bintang.
 * @param showLabel tampilkan label rating (misal "4.5/5").
 * @param labelStyle style teks label.
 * @param labelColor warna label.
 */
@Composable
fun KanzanRatingBar(
    rating: Float,
    onRatingChanged: ((Float) -> Unit)? = null,
    modifier: Modifier = Modifier,
    maxStars: Int = 5,
    starSize: Dp = dp32,
    filledStar: String = "⭐",
    halfStar: String = "⭐",
    emptyStar: String = "☆",
    allowHalf: Boolean = false,
    showLabel: Boolean = false,
    labelStyle: TextStyle = AppTextStyle.nunito_medium_14,
    labelColor: Color = Color.DarkGray,
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Row(horizontalArrangement = Arrangement.spacedBy(dp4)) {
            for (i in 1..maxStars) {
                val starText = when {
                    rating >= i -> filledStar
                    allowHalf && rating >= i - 0.5f -> halfStar
                    else -> emptyStar
                }
                Text(
                    text = starText,
                    style = AppTextStyle.nunito_regular_16,
                    modifier = Modifier
                        .size(starSize)
                        .then(
                            if (onRatingChanged != null) {
                                Modifier.clickable {
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
    var rating by remember { mutableStateOf(3f) }
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

// endregion
