package com.kanzankazu.kanzanwidget.compose.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kanzankazu.kanzanwidget.compose.ui.AppTextStyle

/**
 * Badge wrapper — wraps a composable (typically an IconButton) with a badge count.
 */
@Composable
fun KanzanBadgeIcon(
    count: Int,
    content: @Composable () -> Unit
) {
    Box {
        content()
        if (count > 0) {
            val label = if (count > 99) "99+" else count.toString()
            Text(
                text = label,
                style = AppTextStyle.nunito_regular_12,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-4).dp, y = 4.dp)
                    .size(if (count > 99) 20.dp else 16.dp)
                    .clip(CircleShape)
                    .background(Color.Red)
                    .padding(1.dp),
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}
