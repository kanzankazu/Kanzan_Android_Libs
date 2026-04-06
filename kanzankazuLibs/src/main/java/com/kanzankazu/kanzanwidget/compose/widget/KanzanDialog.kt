package com.kanzankazu.kanzanwidget.compose.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kanzankazu.kanzanwidget.compose.ui.AppTextStyle
import com.kanzankazu.kanzanwidget.compose.ui.dp8
import com.kanzankazu.kanzanwidget.compose.ui.dp12
import com.kanzankazu.kanzanwidget.compose.ui.dp16
import com.kanzankazu.kanzanwidget.compose.ui.dp24

// region ==================== Enums ====================

enum class KanzanDialogType { ALERT, CONFIRM, CUSTOM }

// endregion

// region ==================== KanzanDialog ====================

/**
 * Dialog generic yang support alert, confirm, dan custom content.
 *
 * @param isVisible kontrol visibilitas dialog.
 * @param onDismiss callback saat dialog ditutup.
 * @param dialogType tipe dialog (ALERT, CONFIRM, CUSTOM).
 * @param title judul dialog.
 * @param message pesan dialog (untuk ALERT dan CONFIRM).
 * @param icon composable icon di atas title.
 * @param confirmText label tombol konfirmasi.
 * @param cancelText label tombol batal.
 * @param onConfirm callback tombol konfirmasi.
 * @param onCancel callback tombol batal (null = pakai onDismiss).
 * @param confirmColor warna tombol konfirmasi.
 * @param cancelColor warna tombol batal.
 * @param titleStyle style teks title.
 * @param messageStyle style teks message.
 * @param dismissOnOutsideClick bisa tutup dengan tap di luar.
 * @param dismissOnBackPress bisa tutup dengan back press.
 * @param cornerRadius radius sudut dialog.
 * @param customContent composable kustom (untuk CUSTOM type).
 */
@Composable
fun KanzanDialog(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    dialogType: KanzanDialogType = KanzanDialogType.ALERT,
    title: String? = null,
    message: String? = null,
    icon: @Composable (() -> Unit)? = null,
    confirmText: String = "OK",
    cancelText: String = "Batal",
    onConfirm: (() -> Unit)? = null,
    onCancel: (() -> Unit)? = null,
    confirmColor: Color = Color.Black,
    cancelColor: Color = Color.Gray,
    titleStyle: TextStyle = AppTextStyle.nunito_bold_16,
    messageStyle: TextStyle = AppTextStyle.nunito_regular_14,
    dismissOnOutsideClick: Boolean = true,
    dismissOnBackPress: Boolean = true,
    cornerRadius: Dp = dp16,
    customContent: @Composable (() -> Unit)? = null,
) {
    if (!isVisible) return

    Dialog(
        onDismissRequest = { if (dismissOnOutsideClick) onDismiss() },
        properties = DialogProperties(
            dismissOnBackPress = dismissOnBackPress,
            dismissOnClickOutside = dismissOnOutsideClick,
        ),
    ) {
        Surface(
            modifier = modifier,
            shape = RoundedCornerShape(cornerRadius),
            color = Color.White,
            tonalElevation = dp8,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dp24),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Icon
                icon?.let {
                    it()
                    Spacer(modifier = Modifier.height(dp16))
                }

                // Title
                if (title != null) {
                    Text(
                        text = title,
                        style = titleStyle,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(modifier = Modifier.height(dp8))
                }

                // Body
                when (dialogType) {
                    KanzanDialogType.ALERT -> {
                        if (message != null) {
                            Text(
                                text = message,
                                style = messageStyle,
                                color = Color.DarkGray,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                        Spacer(modifier = Modifier.height(dp24))
                        KanzanBaseButton(
                            title = confirmText,
                            onClick = { (onConfirm ?: onDismiss).invoke() },
                            fullWidth = true,
                            containerColor = confirmColor,
                        )
                    }

                    KanzanDialogType.CONFIRM -> {
                        if (message != null) {
                            Text(
                                text = message,
                                style = messageStyle,
                                color = Color.DarkGray,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                        Spacer(modifier = Modifier.height(dp24))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(dp8),
                        ) {
                            KanzanBaseButton(
                                title = cancelText,
                                onClick = { (onCancel ?: onDismiss).invoke() },
                                modifier = Modifier.weight(1f),
                                buttonType = KanzanButtonType.OUTLINED,
                                containerColor = cancelColor,
                                borderColor = cancelColor,
                                contentColor = cancelColor,
                            )
                            KanzanBaseButton(
                                title = confirmText,
                                onClick = {
                                    onConfirm?.invoke()
                                    onDismiss()
                                },
                                modifier = Modifier.weight(1f),
                                containerColor = confirmColor,
                            )
                        }
                    }

                    KanzanDialogType.CUSTOM -> {
                        customContent?.invoke()
                    }
                }
            }
        }
    }
}

// endregion

// region ==================== Preview ====================

@Preview(showBackground = true, name = "Dialog 1. Alert")
@Composable
private fun PreviewDialogAlert() {
    Surface {
        KanzanDialog(
            isVisible = true,
            onDismiss = {},
            dialogType = KanzanDialogType.ALERT,
            title = "Berhasil",
            message = "Data hutang berhasil disimpan.",
            icon = { Text(text = "✅", style = AppTextStyle.nunito_regular_36) },
        )
    }
}

@Preview(showBackground = true, name = "Dialog 2. Confirm")
@Composable
private fun PreviewDialogConfirm() {
    Surface {
        KanzanDialog(
            isVisible = true,
            onDismiss = {},
            dialogType = KanzanDialogType.CONFIRM,
            title = "Hapus Data?",
            message = "Apakah kamu yakin ingin menghapus hutang ini? Tindakan ini tidak bisa dibatalkan.",
            icon = { Text(text = "🗑️", style = AppTextStyle.nunito_regular_36) },
            confirmText = "Hapus",
            confirmColor = Color.Red,
            onConfirm = {},
        )
    }
}

@Preview(showBackground = true, name = "Dialog 3. Custom")
@Composable
private fun PreviewDialogCustom() {
    Surface {
        KanzanDialog(
            isVisible = true,
            onDismiss = {},
            dialogType = KanzanDialogType.CUSTOM,
            title = "Pilih Metode",
            customContent = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(dp8),
                ) {
                    KanzanBaseButton(title = "Transfer Bank", onClick = {}, fullWidth = true, buttonType = KanzanButtonType.OUTLINED)
                    KanzanBaseButton(title = "E-Wallet", onClick = {}, fullWidth = true, buttonType = KanzanButtonType.OUTLINED)
                    KanzanBaseButton(title = "Tunai", onClick = {}, fullWidth = true, buttonType = KanzanButtonType.OUTLINED)
                }
            },
        )
    }
}

// endregion
