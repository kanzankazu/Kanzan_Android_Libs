package com.kanzankazu.kanzanwidget.compose.widget

import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kanzankazu.kanzanwidget.compose.ui.AppTextStyle
import com.kanzankazu.kanzanwidget.compose.ui.dp4
import com.kanzankazu.kanzanwidget.compose.ui.dp8
import com.kanzankazu.kanzanwidget.compose.ui.dp16
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// region ==================== Enums ====================

enum class KanzanBarcodeType {
    QR_CODE,
    BARCODE_128,
    BARCODE_EAN13,
}

// endregion

// region ==================== QR/Barcode Generator ====================

/**
 * Komponen untuk generate dan menampilkan QR Code / Barcode.
 * Menggunakan library QRGenerator yang sudah ada di project.
 *
 * @param content data yang di-encode ke QR/barcode.
 * @param modifier Modifier.
 * @param type tipe barcode (QR_CODE, BARCODE_128, BARCODE_EAN13).
 * @param size ukuran gambar QR/barcode.
 * @param label teks label di bawah gambar.
 * @param labelStyle style teks label.
 * @param backgroundColor warna background QR.
 * @param foregroundColor warna foreground QR.
 * @param showBorder tampilkan border di sekitar gambar.
 * @param borderColor warna border.
 * @param cornerRadius radius sudut.
 * @param errorContent composable saat gagal generate.
 * @param loadingContent composable saat loading.
 */
@Composable
fun KanzanBarcodeGenerator(
    content: String,
    modifier: Modifier = Modifier,
    type: KanzanBarcodeType = KanzanBarcodeType.QR_CODE,
    size: Dp = 200.dp,
    label: String? = null,
    labelStyle: TextStyle = AppTextStyle.nunito_regular_12,
    backgroundColor: Int = AndroidColor.WHITE,
    foregroundColor: Int = AndroidColor.BLACK,
    showBorder: Boolean = true,
    borderColor: Color = Color.LightGray,
    cornerRadius: Dp = dp8,
    errorContent: @Composable ((String) -> Unit)? = null,
    loadingContent: @Composable (() -> Unit)? = null,
) {
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    val sizePx = with(LocalDensity.current) { size.toPx().toInt() }

    LaunchedEffect(content, type, sizePx) {
        if (content.isBlank()) {
            error = "Konten kosong"
            return@LaunchedEffect
        }
        withContext(Dispatchers.Default) {
            try {
                val qrgEncoder = QRGEncoder(content, null, resolveQRGType(type), sizePx)
                qrgEncoder.colorBlack = foregroundColor
                qrgEncoder.colorWhite = backgroundColor
                bitmap = qrgEncoder.bitmap
                error = null
            } catch (e: Exception) {
                error = e.message ?: "Gagal generate barcode"
                bitmap = null
            }
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when {
            error != null -> {
                errorContent?.invoke(error!!) ?: Box(
                    modifier = Modifier.size(size).clip(RoundedCornerShape(cornerRadius))
                        .background(Color(0xFFFFF3F3))
                        .then(if (showBorder) Modifier.border(1.dp, Color.Red.copy(alpha = 0.3f), RoundedCornerShape(cornerRadius)) else Modifier),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "⚠️ $error", style = AppTextStyle.nunito_regular_12, color = Color.Red)
                }
            }
            bitmap != null -> {
                Image(
                    bitmap = bitmap!!.asImageBitmap(),
                    contentDescription = "Barcode: $content",
                    modifier = Modifier
                        .size(size)
                        .clip(RoundedCornerShape(cornerRadius))
                        .then(if (showBorder) Modifier.border(1.dp, borderColor, RoundedCornerShape(cornerRadius)) else Modifier),
                    contentScale = ContentScale.Fit
                )
            }
            else -> {
                loadingContent?.invoke() ?: Box(
                    modifier = Modifier.size(size),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "⏳", style = AppTextStyle.nunito_regular_24)
                }
            }
        }

        if (label != null) {
            Spacer(modifier = Modifier.height(dp8))
            Text(text = label, style = labelStyle, color = Color.Gray)
        }
    }
}

private fun resolveQRGType(type: KanzanBarcodeType): String = when (type) {
    KanzanBarcodeType.QR_CODE -> QRGContents.Type.TEXT
    KanzanBarcodeType.BARCODE_128 -> QRGContents.Type.TEXT
    KanzanBarcodeType.BARCODE_EAN13 -> QRGContents.Type.TEXT
}

// endregion

// region ==================== Scan Result Model ====================

/**
 * Model hasil scan barcode/QR.
 */
data class KanzanScanResult(
    val rawValue: String,
    val format: String = "UNKNOWN",
)

/**
 * Composable placeholder untuk scanner.
 * Karena CameraX belum ada di dependencies, komponen ini menyediakan
 * UI placeholder yang bisa di-integrate dengan Activity result / Intent scanner.
 *
 * Untuk integrasi penuh, tambahkan CameraX + ML Kit Barcode di build.gradle:
 * ```
 * implementation "androidx.camera:camera-camera2:1.3.0"
 * implementation "androidx.camera:camera-lifecycle:1.3.0"
 * implementation "androidx.camera:camera-view:1.3.0"
 * implementation "com.google.mlkit:barcode-scanning:17.2.0"
 * ```
 *
 * @param onScanResult callback saat scan berhasil.
 * @param modifier Modifier.
 * @param title judul area scanner.
 * @param subtitle instruksi.
 * @param scanButtonContent composable tombol scan (trigger intent/activity).
 */
@Composable
fun KanzanBarcodeScannerPlaceholder(
    onScanResult: (KanzanScanResult) -> Unit,
    modifier: Modifier = Modifier,
    title: String = "Scan QR / Barcode",
    subtitle: String = "Arahkan kamera ke QR code atau barcode",
    scanButtonContent: @Composable (() -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(dp16))
            .background(Color(0xFFF5F5F5))
            .border(1.dp, Color.LightGray, RoundedCornerShape(dp16))
            .padding(dp16),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "📷", style = AppTextStyle.nunito_regular_36)
        Spacer(modifier = Modifier.height(dp8))
        Text(text = title, style = AppTextStyle.nunito_medium_16)
        Spacer(modifier = Modifier.height(dp4))
        Text(text = subtitle, style = AppTextStyle.nunito_regular_12, color = Color.Gray)
        Spacer(modifier = Modifier.height(dp16))

        scanButtonContent?.invoke() ?: KanzanBaseButton(
            title = "Buka Scanner",
            onClick = { /* Trigger camera intent / activity */ },
            leadingIcon = { Text(text = "📸", style = AppTextStyle.nunito_regular_14) },
            fullWidth = true,
        )
    }
}
// endregion

// region ==================== Preview ====================

@Preview(showBackground = true, name = "Barcode 1. QR Code generator")
@Composable
private fun PreviewQRCode() {
    KanzanBarcodeGenerator(
        content = "https://itungitungan.app/hutang/12345",
        label = "Scan untuk lihat detail hutang",
        modifier = Modifier.padding(dp16)
    )
}

@Preview(showBackground = true, name = "Barcode 2. Small QR")
@Composable
private fun PreviewQRSmall() {
    KanzanBarcodeGenerator(
        content = "INV-2024-001",
        size = 120.dp,
        label = "Invoice",
        modifier = Modifier.padding(dp16)
    )
}

@Preview(showBackground = true, name = "Barcode 3. Error state")
@Composable
private fun PreviewQRError() {
    KanzanBarcodeGenerator(
        content = "",
        label = "QR Error",
        modifier = Modifier.padding(dp16)
    )
}

@Preview(showBackground = true, name = "Barcode 4. Scanner placeholder")
@Composable
private fun PreviewScanner() {
    KanzanBarcodeScannerPlaceholder(
        onScanResult = {},
        modifier = Modifier.padding(dp16)
    )
}

@Preview(showBackground = true, name = "Barcode 5. Multiple QR codes")
@Composable
private fun PreviewMultipleQR() {
    Row(
        modifier = Modifier.padding(dp16),
        horizontalArrangement = Arrangement.spacedBy(dp16)
    ) {
        KanzanBarcodeGenerator(content = "ARISAN-001", size = 100.dp, label = "Arisan")
        KanzanBarcodeGenerator(content = "HUTANG-002", size = 100.dp, label = "Hutang")
    }
}

// endregion
