package com.kanzankazu.kanzanwidget.compose.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kanzankazu.kanzanwidget.compose.ui.AppTextStyle

/**
 * Badge wrapper — wraps a composable (typically an IconButton) with a badge count.
 */
@Composable
fun KanzanIconBadge(
    count: Int,
    content: @Composable () -> Unit
) {
    Box {
        content()
        if (count > 0) {
            val label = if (count > 99) "99+" else count.toString()
            val isChip = label.length >= 3
            val badgeHeight = 16.dp
            Text(
                text = label,
                style = AppTextStyle.nunito_regular_10,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = if (isChip) (-2).dp else (-10).dp, y = 2.dp)
                    .then(
                        if (isChip) {
                            Modifier
                                .height(badgeHeight)
                                .widthIn(min = 24.dp)
                                .clip(RoundedCornerShape(badgeHeight / 2))
                        } else {
                            Modifier
                                .size(badgeHeight)
                                .clip(CircleShape)
                        }
                    )
                    .background(Color.Red)
                    .padding(horizontal = if (isChip) 4.dp else 1.dp, vertical = 1.dp),
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}


// region ==================== Preview ====================
@Preview(showBackground = true, name = "1. Badge count = 0 (hidden)")
@Composable
private fun PreviewZeroBadge() {
    KanzanIconBadge(count = 0) {
        IconButton(onClick = {}) { Text(text = "🔔", style = AppTextStyle.nunito_regular_16) }
    }
}

@Preview(showBackground = true, name = "2. Badge count = 5")
@Composable
private fun PreviewSingleBadge() {
    KanzanIconBadge(count = 5) {
        IconButton(onClick = {}) { Text(text = "🔔", style = AppTextStyle.nunito_regular_16) }
    }
}

@Preview(showBackground = true, name = "3. Badge count = 99")
@Composable
private fun Preview99Badge() {
    KanzanIconBadge(count = 99) {
        IconButton(onClick = {}) { Text(text = "🔔", style = AppTextStyle.nunito_regular_16) }
    }
}

@Preview(showBackground = true, name = "4. Badge count = 150 (99+)")
@Composable
private fun PreviewOver99Badge() {
    KanzanIconBadge(count = 150) {
        IconButton(onClick = {}) { Text(text = "🔔", style = AppTextStyle.nunito_regular_16) }
    }
}
// endregion
