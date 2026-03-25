package com.kanzankazu.kanzanwidget.compose.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kanzankazu.kanzanwidget.compose.ui.AppTextStyle
import com.kanzankazu.kanzanwidget.compose.ui.dp1
import com.kanzankazu.kanzanwidget.compose.ui.dp4
import com.kanzankazu.kanzanwidget.compose.ui.dp8
import com.kanzankazu.kanzanwidget.compose.ui.dp16
import com.kanzankazu.kanzanwidget.compose.ui.dp40
import com.kanzankazu.kanzanwidget.compose.ui.dp48
import kotlin.math.absoluteValue

// region ==================== KanzanTextDrawable ====================

/**
 * Komponen avatar/inisial mirip TextDrawable di Android View.
 * Menampilkan 1-2 huruf inisial di dalam shape berwarna.
 *
 * @param text teks sumber (akan diambil inisialnya).
 * @param modifier Modifier.
 * @param maxInitials jumlah huruf inisial maksimal (1 atau 2).
 * @param size ukuran komponen.
 * @param shape bentuk (CircleShape, RoundedCornerShape, dll).
 * @param backgroundColor warna background (null = auto dari hash teks).
 * @param textColor warna teks.
 * @param textStyle style teks.
 * @param borderWidth lebar border (0 = tanpa border).
 * @param borderColor warna border.
 * @param colorPalette palet warna untuk auto-color.
 * @param overlay composable overlay di atas inisial (badge, icon, dll).
 */
@Composable
fun KanzanTextDrawable(
    text: String,
    modifier: Modifier = Modifier,
    maxInitials: Int = 2,
    size: Dp = dp48,
    shape: Shape = CircleShape,
    backgroundColor: Color? = null,
    textColor: Color = Color.White,
    textStyle: TextStyle = AppTextStyle.nunito_bold_20,
    borderWidth: Dp = 0.dp,
    borderColor: Color = Color.White,
    colorPalette: List<Color> = defaultTextDrawableColors,
    overlay: @Composable (() -> Unit)? = null,
) {
    val initials = remember(text, maxInitials) { extractInitials(text, maxInitials) }
    val bgColor = backgroundColor ?: remember(text) {
        colorPalette[text.hashCode().absoluteValue % colorPalette.size]
    }

    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(bgColor, shape)
            .then(if (borderWidth > 0.dp) Modifier.border(borderWidth, borderColor, shape) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            style = textStyle,
            color = textColor,
            textAlign = TextAlign.Center
        )
        overlay?.invoke()
    }
}

private fun extractInitials(text: String, max: Int): String {
    val words = text.trim().split("\\s+".toRegex()).filter { it.isNotBlank() }
    return when {
        words.isEmpty() -> "?"
        max == 1 || words.size == 1 -> words.first().first().uppercaseChar().toString()
        else -> "${words.first().first().uppercaseChar()}${words.last().first().uppercaseChar()}"
    }
}

private val defaultTextDrawableColors = listOf(
    Color(0xFFE91E63), Color(0xFF9C27B0), Color(0xFF673AB7),
    Color(0xFF3F51B5), Color(0xFF2196F3), Color(0xFF03A9F4),
    Color(0xFF00BCD4), Color(0xFF009688), Color(0xFF4CAF50),
    Color(0xFF8BC34A), Color(0xFFFF9800), Color(0xFFFF5722),
    Color(0xFF795548), Color(0xFF607D8B),
)
// endregion

// region ==================== Preview ====================

@Preview(showBackground = true, name = "TextDrawable 1. Circle")
@Composable
private fun PreviewTextDrawableCircle() {
    Row(horizontalArrangement = Arrangement.spacedBy(dp8)) {
        KanzanTextDrawable(text = "Budi Santoso")
        KanzanTextDrawable(text = "Ani Wijaya")
        KanzanTextDrawable(text = "Citra")
    }
}

@Preview(showBackground = true, name = "TextDrawable 2. Rounded rect")
@Composable
private fun PreviewTextDrawableRounded() {
    KanzanTextDrawable(
        text = "Money Manager",
        shape = RoundedCornerShape(dp8),
        size = dp48
    )
}

@Preview(showBackground = true, name = "TextDrawable 3. Single initial")
@Composable
private fun PreviewTextDrawableSingle() {
    KanzanTextDrawable(text = "Admin", maxInitials = 1, size = dp40)
}

@Preview(showBackground = true, name = "TextDrawable 4. Custom color + border")
@Composable
private fun PreviewTextDrawableCustom() {
    KanzanTextDrawable(
        text = "VIP",
        backgroundColor = Color(0xFF1E88E5),
        borderWidth = dp1 * 2,
        borderColor = Color(0xFFBBDEFB),
        size = dp48
    )
}

@Preview(showBackground = true, name = "TextDrawable 5. With badge overlay")
@Composable
private fun PreviewTextDrawableOverlay() {
    KanzanTextDrawable(
        text = "Notif",
        size = dp48,
        overlay = {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(dp16)
                    .background(Color.Red, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "3", style = AppTextStyle.nunito_bold_12, color = Color.White)
            }
        }
    )
}

@Composable
private fun Modifier.align(alignment: Alignment): Modifier = this

@Preview(showBackground = true, name = "TextDrawable 6. Sizes")
@Composable
private fun PreviewTextDrawableSizes() {
    Row(horizontalArrangement = Arrangement.spacedBy(dp8), verticalAlignment = Alignment.CenterVertically) {
        KanzanTextDrawable(text = "S", size = 32.dp, textStyle = AppTextStyle.nunito_bold_14)
        KanzanTextDrawable(text = "M", size = dp40, textStyle = AppTextStyle.nunito_bold_16)
        KanzanTextDrawable(text = "L", size = dp48, textStyle = AppTextStyle.nunito_bold_20)
        KanzanTextDrawable(text = "XL", size = 64.dp, textStyle = AppTextStyle.nunito_bold_24)
    }
}

// endregion
