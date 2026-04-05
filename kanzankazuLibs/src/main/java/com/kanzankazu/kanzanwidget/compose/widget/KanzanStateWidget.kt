package com.kanzankazu.kanzanwidget.compose.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kanzankazu.kanzanwidget.compose.ui.AppTextStyle
import com.kanzankazu.kanzanwidget.compose.ui.dp8
import com.kanzankazu.kanzanwidget.compose.ui.dp12
import com.kanzankazu.kanzanwidget.compose.ui.dp16
import com.kanzankazu.kanzanwidget.compose.ui.dp24
import com.kanzankazu.kanzanwidget.compose.ui.dp64

// region ==================== Enums ====================

enum class KanzanStateType { EMPTY, ERROR, NO_CONNECTION, MAINTENANCE, CUSTOM }

// endregion

// region ==================== KanzanStateWidget ====================

/**
 * Widget untuk menampilkan state kosong, error, no connection, maintenance, dll.
 *
 * @param stateType tipe state.
 * @param modifier Modifier.
 * @param title judul state (null = auto dari stateType).
 * @param message pesan state (null = auto dari stateType).
 * @param icon composable icon (null = auto emoji dari stateType).
 * @param iconSize ukuran icon default.
 * @param actionText label tombol aksi (null = tidak tampil).
 * @param onAction callback tombol aksi.
 * @param titleStyle style teks title.
 * @param messageStyle style teks message.
 * @param titleColor warna title.
 * @param messageColor warna message.
 * @param fillMaxSize apakah mengisi seluruh layar.
 * @param customContent composable kustom (untuk CUSTOM type).
 */
@Composable
fun KanzanStateWidget(
    stateType: KanzanStateType,
    modifier: Modifier = Modifier,
    title: String? = null,
    message: String? = null,
    icon: @Composable (() -> Unit)? = null,
    iconSize: Dp = dp64,
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
    titleStyle: TextStyle = AppTextStyle.nunito_medium_16,
    messageStyle: TextStyle = AppTextStyle.nunito_regular_14,
    titleColor: Color = Color.Black,
    messageColor: Color = Color.Gray,
    fillMaxSize: Boolean = true,
    customContent: @Composable (() -> Unit)? = null,
) {
    val resolvedTitle = title ?: when (stateType) {
        KanzanStateType.EMPTY -> "Belum Ada Data"
        KanzanStateType.ERROR -> "Terjadi Kesalahan"
        KanzanStateType.NO_CONNECTION -> "Tidak Ada Koneksi"
        KanzanStateType.MAINTENANCE -> "Sedang Maintenance"
        KanzanStateType.CUSTOM -> ""
    }
    val resolvedMessage = message ?: when (stateType) {
        KanzanStateType.EMPTY -> "Data yang kamu cari belum tersedia."
        KanzanStateType.ERROR -> "Terjadi kesalahan saat memuat data. Silakan coba lagi."
        KanzanStateType.NO_CONNECTION -> "Periksa koneksi internet kamu dan coba lagi."
        KanzanStateType.MAINTENANCE -> "Sistem sedang dalam perbaikan. Silakan kembali nanti."
        KanzanStateType.CUSTOM -> ""
    }
    val resolvedIcon: @Composable () -> Unit = icon ?: {
        Text(
            text = when (stateType) {
                KanzanStateType.EMPTY -> "📭"
                KanzanStateType.ERROR -> "⚠️"
                KanzanStateType.NO_CONNECTION -> "📡"
                KanzanStateType.MAINTENANCE -> "🔧"
                KanzanStateType.CUSTOM -> ""
            },
            style = AppTextStyle.nunito_regular_36,
        )
    }
    val resolvedActionText = actionText ?: when (stateType) {
        KanzanStateType.ERROR, KanzanStateType.NO_CONNECTION -> "Coba Lagi"
        else -> null
    }

    val boxModifier = if (fillMaxSize) modifier.fillMaxSize() else modifier.fillMaxWidth()

    Box(modifier = boxModifier, contentAlignment = Alignment.Center) {
        if (stateType == KanzanStateType.CUSTOM && customContent != null) {
            customContent()
        } else {
            Column(
                modifier = Modifier.padding(dp24),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                resolvedIcon()
                Spacer(modifier = Modifier.height(dp16))
                if (resolvedTitle.isNotBlank()) {
                    Text(
                        text = resolvedTitle,
                        style = titleStyle,
                        color = titleColor,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.height(dp8))
                }
                if (resolvedMessage.isNotBlank()) {
                    Text(
                        text = resolvedMessage,
                        style = messageStyle,
                        color = messageColor,
                        textAlign = TextAlign.Center,
                    )
                }
                if (resolvedActionText != null && onAction != null) {
                    Spacer(modifier = Modifier.height(dp24))
                    KanzanBaseButton(
                        title = resolvedActionText,
                        onClick = onAction,
                        buttonType = KanzanButtonType.OUTLINED,
                    )
                }
            }
        }
    }
}

// endregion

// region ==================== Preview ====================

@Preview(showBackground = true, name = "State 1. Empty")
@Composable
private fun PreviewStateEmpty() {
    KanzanStateWidget(stateType = KanzanStateType.EMPTY, modifier = Modifier.height(300.dp))
}

@Preview(showBackground = true, name = "State 2. Error")
@Composable
private fun PreviewStateError() {
    KanzanStateWidget(
        stateType = KanzanStateType.ERROR,
        modifier = Modifier.height(300.dp),
        onAction = {},
    )
}

@Preview(showBackground = true, name = "State 3. No Connection")
@Composable
private fun PreviewStateNoConnection() {
    KanzanStateWidget(
        stateType = KanzanStateType.NO_CONNECTION,
        modifier = Modifier.height(300.dp),
        onAction = {},
    )
}

@Preview(showBackground = true, name = "State 4. Maintenance")
@Composable
private fun PreviewStateMaintenance() {
    KanzanStateWidget(stateType = KanzanStateType.MAINTENANCE, modifier = Modifier.height(300.dp))
}

@Preview(showBackground = true, name = "State 5. Custom")
@Composable
private fun PreviewStateCustom() {
    KanzanStateWidget(
        stateType = KanzanStateType.CUSTOM,
        modifier = Modifier.height(300.dp),
        customContent = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "🎉", style = AppTextStyle.nunito_regular_36)
                Spacer(modifier = Modifier.height(dp8))
                Text(text = "Semua hutang lunas!", style = AppTextStyle.nunito_bold_16)
            }
        },
    )
}

// endregion
