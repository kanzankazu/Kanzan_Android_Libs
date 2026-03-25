package com.kanzankazu.kanzanwidget.compose.widget

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kanzankazu.kanzanwidget.compose.ui.AppTextStyle
import com.kanzankazu.kanzanwidget.compose.ui.dp4
import com.kanzankazu.kanzanwidget.compose.ui.dp8
import com.kanzankazu.kanzanwidget.compose.ui.dp12
import com.kanzankazu.kanzanwidget.compose.ui.dp16
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// region ==================== Enums ====================

enum class KanzanToastType {
    SUCCESS, ERROR, WARNING, INFO
}

enum class KanzanSnackbarPosition {
    TOP, BOTTOM
}

// endregion

// region ==================== Toast ====================

/**
 * Wrapper untuk Android Toast yang bisa dipanggil dari Composable.
 *
 * @param message pesan toast.
 * @param duration durasi (Toast.LENGTH_SHORT atau Toast.LENGTH_LONG).
 */
@Composable
fun KanzanToast(
    message: String,
    duration: Int = Toast.LENGTH_SHORT,
) {
    val context = LocalContext.current
    LaunchedEffect(message) {
        if (message.isNotBlank()) {
            Toast.makeText(context, message, duration).show()
        }
    }
}

/**
 * Custom toast composable dengan animasi dan styling.
 * Tampil di atas konten, auto-dismiss setelah durasi tertentu.
 *
 * @param message pesan toast.
 * @param isVisible state visibility.
 * @param onDismiss callback saat toast hilang.
 * @param type tipe toast (SUCCESS, ERROR, WARNING, INFO).
 * @param durationMs durasi tampil dalam milidetik.
 * @param position posisi (TOP atau BOTTOM).
 * @param leadingIcon icon di kiri pesan.
 * @param actionLabel label tombol aksi (opsional).
 * @param onAction callback tombol aksi.
 * @param textStyle style teks pesan.
 * @param containerColor warna background (null = auto dari type).
 * @param contentColor warna teks (null = auto dari type).
 */
@Composable
fun KanzanCustomToast(
    message: String,
    isVisible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    type: KanzanToastType = KanzanToastType.INFO,
    durationMs: Long = 3000L,
    position: KanzanSnackbarPosition = KanzanSnackbarPosition.BOTTOM,
    leadingIcon: @Composable (() -> Unit)? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    textStyle: TextStyle = AppTextStyle.nunito_regular_14,
    containerColor: Color? = null,
    contentColor: Color? = null,
) {
    val resolvedContainerColor = containerColor ?: when (type) {
        KanzanToastType.SUCCESS -> Color(0xFF4CAF50)
        KanzanToastType.ERROR -> Color(0xFFF44336)
        KanzanToastType.WARNING -> Color(0xFFFF9800)
        KanzanToastType.INFO -> Color(0xFF2196F3)
    }
    val resolvedContentColor = contentColor ?: Color.White
    val resolvedIcon = leadingIcon ?: {
        Text(
            text = when (type) {
                KanzanToastType.SUCCESS -> "✅"
                KanzanToastType.ERROR -> "❌"
                KanzanToastType.WARNING -> "⚠️"
                KanzanToastType.INFO -> "ℹ️"
            },
            style = AppTextStyle.nunito_regular_16
        )
    }

    // Auto dismiss
    LaunchedEffect(isVisible) {
        if (isVisible) {
            delay(durationMs)
            onDismiss()
        }
    }

    val enterAnim = if (position == KanzanSnackbarPosition.TOP)
        slideInVertically(tween(300)) { -it } + fadeIn(tween(300))
    else
        slideInVertically(tween(300)) { it } + fadeIn(tween(300))

    val exitAnim = if (position == KanzanSnackbarPosition.TOP)
        slideOutVertically(tween(300)) { -it } + fadeOut(tween(300))
    else
        slideOutVertically(tween(300)) { it } + fadeOut(tween(300))

    AnimatedVisibility(
        visible = isVisible,
        enter = enterAnim,
        exit = exitAnim,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dp16, vertical = dp8)
                .clip(RoundedCornerShape(dp12))
                .background(resolvedContainerColor)
                .padding(horizontal = dp16, vertical = dp12),
            verticalAlignment = Alignment.CenterVertically
        ) {
            resolvedIcon()
            Spacer(modifier = Modifier.width(dp12))
            Text(
                text = message,
                style = textStyle,
                color = resolvedContentColor,
                modifier = Modifier.weight(1f)
            )
            if (actionLabel != null && onAction != null) {
                Spacer(modifier = Modifier.width(dp8))
                Text(
                    text = actionLabel,
                    style = AppTextStyle.nunito_bold_14,
                    color = resolvedContentColor,
                    modifier = Modifier.clickable { onAction() }
                )
            }
        }
    }
}
// endregion

// region ==================== Snackbar ====================

/**
 * Custom snackbar host dengan styling konsisten.
 * Wraps Material3 SnackbarHost dengan custom snackbar appearance.
 *
 * @param hostState SnackbarHostState.
 * @param modifier Modifier.
 * @param containerColor warna background snackbar.
 * @param contentColor warna teks snackbar.
 * @param actionColor warna tombol aksi.
 * @param shape bentuk snackbar.
 */
@Composable
fun KanzanSnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    containerColor: Color = Color(0xFF323232),
    contentColor: Color = Color.White,
    actionColor: Color = Color(0xFF82B1FF),
    shape: RoundedCornerShape = RoundedCornerShape(dp12),
) {
    SnackbarHost(
        hostState = hostState,
        modifier = modifier,
    ) { data ->
        Snackbar(
            snackbarData = data,
            containerColor = containerColor,
            contentColor = contentColor,
            actionColor = actionColor,
            shape = shape,
            modifier = Modifier.padding(horizontal = dp16, vertical = dp8)
        )
    }
}

/**
 * Helper function untuk menampilkan snackbar dengan mudah.
 */
suspend fun SnackbarHostState.showKanzanSnackbar(
    message: String,
    actionLabel: String? = null,
    duration: SnackbarDuration = SnackbarDuration.Short,
    onAction: (() -> Unit)? = null,
) {
    val result = showSnackbar(
        message = message,
        actionLabel = actionLabel,
        duration = duration,
    )
    if (result == SnackbarResult.ActionPerformed) {
        onAction?.invoke()
    }
}
// endregion

// region ==================== Preview ====================

@Preview(showBackground = true, name = "Toast 1. Success")
@Composable
private fun PreviewToastSuccess() {
    Box(modifier = Modifier.fillMaxWidth().padding(dp8)) {
        KanzanCustomToast(
            message = "Data berhasil disimpan",
            isVisible = true,
            onDismiss = {},
            type = KanzanToastType.SUCCESS
        )
    }
}

@Preview(showBackground = true, name = "Toast 2. Error")
@Composable
private fun PreviewToastError() {
    Box(modifier = Modifier.fillMaxWidth().padding(dp8)) {
        KanzanCustomToast(
            message = "Gagal menyimpan data. Coba lagi.",
            isVisible = true,
            onDismiss = {},
            type = KanzanToastType.ERROR
        )
    }
}

@Preview(showBackground = true, name = "Toast 3. Warning")
@Composable
private fun PreviewToastWarning() {
    Box(modifier = Modifier.fillMaxWidth().padding(dp8)) {
        KanzanCustomToast(
            message = "Koneksi internet lambat",
            isVisible = true,
            onDismiss = {},
            type = KanzanToastType.WARNING
        )
    }
}

@Preview(showBackground = true, name = "Toast 4. Info")
@Composable
private fun PreviewToastInfo() {
    Box(modifier = Modifier.fillMaxWidth().padding(dp8)) {
        KanzanCustomToast(
            message = "Hutang baru telah ditambahkan",
            isVisible = true,
            onDismiss = {},
            type = KanzanToastType.INFO
        )
    }
}

@Preview(showBackground = true, name = "Toast 5. With action")
@Composable
private fun PreviewToastAction() {
    Box(modifier = Modifier.fillMaxWidth().padding(dp8)) {
        KanzanCustomToast(
            message = "Item dihapus",
            isVisible = true,
            onDismiss = {},
            type = KanzanToastType.INFO,
            actionLabel = "UNDO",
            onAction = {}
        )
    }
}

@Preview(showBackground = true, name = "Toast 6. All types")
@Composable
private fun PreviewToastAllTypes() {
    Column(modifier = Modifier.padding(dp8), verticalArrangement = Arrangement.spacedBy(dp4)) {
        KanzanCustomToast(message = "Sukses", isVisible = true, onDismiss = {}, type = KanzanToastType.SUCCESS)
        KanzanCustomToast(message = "Error", isVisible = true, onDismiss = {}, type = KanzanToastType.ERROR)
        KanzanCustomToast(message = "Warning", isVisible = true, onDismiss = {}, type = KanzanToastType.WARNING)
        KanzanCustomToast(message = "Info", isVisible = true, onDismiss = {}, type = KanzanToastType.INFO)
    }
}

@Preview(showBackground = true, name = "Snackbar 1. Custom host")
@Composable
private fun PreviewSnackbarHost() {
    val hostState = remember { SnackbarHostState() }
    Box(modifier = Modifier.fillMaxWidth().height(80.dp)) {
        KanzanSnackbarHost(hostState = hostState)
    }
}

// endregion
