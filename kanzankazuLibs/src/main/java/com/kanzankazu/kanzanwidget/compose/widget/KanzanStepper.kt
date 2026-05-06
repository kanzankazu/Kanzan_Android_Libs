package com.kanzankazu.kanzanwidget.compose.widget

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import com.kanzankazu.kanzanwidget.compose.ui.AppTextStyle
import com.kanzankazu.kanzanwidget.compose.ui.dp2
import com.kanzankazu.kanzanwidget.compose.ui.dp4
import com.kanzankazu.kanzanwidget.compose.ui.dp8
import com.kanzankazu.kanzanwidget.compose.ui.dp16
import com.kanzankazu.kanzanwidget.compose.ui.dp24
import com.kanzankazu.kanzanwidget.compose.ui.dp32

// region ==================== KanzanStepper ====================

/**
 * Step indicator / progress stepper horizontal.
 *
 * @param totalSteps jumlah total step.
 * @param currentStep step saat ini (0-indexed).
 * @param modifier Modifier.
 * @param labels daftar label per step (opsional).
 * @param activeColor warna step aktif.
 * @param inactiveColor warna step belum aktif.
 * @param completedColor warna step yang sudah selesai.
 * @param stepSize ukuran lingkaran step.
 * @param lineHeight tinggi garis penghubung.
 * @param labelStyle style teks label.
 * @param showNumbers tampilkan nomor di dalam lingkaran.
 */
@Composable
fun KanzanStepper(
    totalSteps: Int,
    currentStep: Int,
    modifier: Modifier = Modifier,
    labels: List<String>? = null,
    activeColor: Color = Color.Black,
    inactiveColor: Color = Color.LightGray,
    completedColor: Color = Color(0xFF4CAF50),
    stepSize: Dp = dp32,
    lineHeight: Dp = dp2,
    labelStyle: TextStyle = AppTextStyle.nunito_regular_12,
    showNumbers: Boolean = true,
) {
    val stepSizePx = with(LocalDensity.current) { stepSize.toPx() }
    val lineHeightPx = with(LocalDensity.current) { lineHeight.toPx() }

    Column(modifier = modifier.fillMaxWidth()) {
        // Step circles + connector lines drawn via Canvas overlay
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val totalWidth = constraints.maxWidth.toFloat()
            // Each step is equally spaced; center of step i = stepSizePx/2 + i * spacing
            val spacing = if (totalSteps > 1) (totalWidth - stepSizePx) / (totalSteps - 1) else 0f

            // Draw connector lines behind circles
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(stepSize)
            ) {
                val centerY = size.height / 2
                for (i in 0 until totalSteps - 1) {
                    val startX = stepSizePx / 2 + i * spacing + stepSizePx / 2
                    val endX = stepSizePx / 2 + (i + 1) * spacing - stepSizePx / 2
                    val lineColor = if (i < currentStep) completedColor else inactiveColor
                    drawLine(
                        color = lineColor,
                        start = Offset(startX, centerY),
                        end = Offset(endX, centerY),
                        strokeWidth = lineHeightPx,
                    )
                }
            }

            // Draw circles on top
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                for (i in 0 until totalSteps) {
                    val color = when {
                        i < currentStep -> completedColor
                        i == currentStep -> activeColor
                        else -> inactiveColor
                    }
                    Box(
                        modifier = Modifier
                            .size(stepSize)
                            .clip(CircleShape)
                            .background(color),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (i < currentStep) {
                            Text(text = "✓", style = AppTextStyle.nunito_bold_12, color = Color.White)
                        } else if (showNumbers) {
                            Text(
                                text = "${i + 1}",
                                style = AppTextStyle.nunito_bold_12,
                                color = if (i == currentStep) Color.White else Color.DarkGray,
                            )
                        }
                    }
                }
            }
        }

        // Labels row — equal weight per step
        if (labels != null && labels.size == totalSteps) {
            Spacer(modifier = Modifier.height(dp4))
            Row(modifier = Modifier.fillMaxWidth()) {
                labels.forEachIndexed { index, label ->
                    Text(
                        text = label,
                        style = labelStyle,
                        color = when {
                            index < currentStep -> completedColor
                            index == currentStep -> activeColor
                            else -> inactiveColor
                        },
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

// endregion

// region ==================== KanzanVerticalStepper ====================

/**
 * Vertical step indicator / timeline stepper.
 * Cocok untuk timeline, wizard vertikal, atau tracking status.
 *
 * @param totalSteps jumlah total step.
 * @param currentStep step saat ini (0-indexed).
 * @param modifier Modifier.
 * @param labels daftar label per step (opsional).
 * @param descriptions daftar deskripsi per step (opsional).
 * @param activeColor warna step aktif.
 * @param inactiveColor warna step belum aktif.
 * @param completedColor warna step yang sudah selesai.
 * @param stepSize ukuran lingkaran step.
 * @param lineWidth lebar garis penghubung vertikal.
 * @param lineMinHeight tinggi minimum garis penghubung.
 * @param labelStyle style teks label.
 * @param descriptionStyle style teks deskripsi.
 * @param showNumbers tampilkan nomor di dalam lingkaran.
 * @param stepContent composable kustom per step (index, isCompleted, isActive).
 */
@Composable
fun KanzanVerticalStepper(
    totalSteps: Int,
    currentStep: Int,
    modifier: Modifier = Modifier,
    labels: List<String>? = null,
    descriptions: List<String>? = null,
    activeColor: Color = Color.Black,
    inactiveColor: Color = Color.LightGray,
    completedColor: Color = Color(0xFF4CAF50),
    stepSize: Dp = dp32,
    lineWidth: Dp = dp2,
    lineMinHeight: Dp = dp24,
    labelStyle: TextStyle = AppTextStyle.nunito_medium_14,
    descriptionStyle: TextStyle = AppTextStyle.nunito_regular_12,
    showNumbers: Boolean = true,
    stepContent: @Composable ((index: Int, isCompleted: Boolean, isActive: Boolean) -> Unit)? = null,
) {
    Column(modifier = modifier) {
        for (i in 0 until totalSteps) {
            val isCompleted = i < currentStep
            val isActive = i == currentStep

            val color by animateColorAsState(
                targetValue = when {
                    isCompleted -> completedColor
                    isActive -> activeColor
                    else -> inactiveColor
                },
                animationSpec = tween(300),
                label = "vStepColor$i",
            )

            Row(modifier = Modifier.fillMaxWidth()) {
                // Left: circle + line
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Step circle
                    Box(
                        modifier = Modifier
                            .size(stepSize)
                            .clip(CircleShape)
                            .background(color),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (isCompleted) {
                            Text(text = "✓", style = AppTextStyle.nunito_bold_12, color = Color.White)
                        } else if (showNumbers) {
                            Text(
                                text = "${i + 1}",
                                style = AppTextStyle.nunito_bold_12,
                                color = if (isActive) Color.White else Color.DarkGray,
                            )
                        }
                    }

                    // Vertical line (except last step)
                    if (i < totalSteps - 1) {
                        val lineColor by animateColorAsState(
                            targetValue = if (isCompleted) completedColor else inactiveColor,
                            animationSpec = tween(300),
                            label = "vLineColor$i",
                        )
                        Box(
                            modifier = Modifier
                                .width(lineWidth)
                                .height(lineMinHeight)
                                .background(lineColor),
                        )
                    }
                }

                KanzanSpacerHorizontal(width = dp8)

                // Right: label + description + custom content
                Column(modifier = Modifier.weight(1f).padding(top = dp4)) {
                    if (labels != null && i < labels.size) {
                        Text(
                            text = labels[i],
                            style = labelStyle,
                            color = when {
                                isCompleted -> completedColor
                                isActive -> activeColor
                                else -> inactiveColor
                            },
                        )
                    }
                    if (descriptions != null && i < descriptions.size) {
                        Text(
                            text = descriptions[i],
                            style = descriptionStyle,
                            color = Color.Gray,
                        )
                    }
                    stepContent?.invoke(i, isCompleted, isActive)
                }
            }
        }
    }
}

// endregion

// region ==================== KanzanProgressBar ====================

/**
 * Linear progress bar dengan label.
 *
 * @param progress nilai progress (0f - 1f).
 * @param modifier Modifier.
 * @param label label di atas progress bar.
 * @param showPercentage tampilkan persentase.
 * @param progressColor warna progress.
 * @param trackColor warna track.
 * @param height tinggi progress bar.
 * @param labelStyle style teks label.
 * @param percentageStyle style teks persentase.
 */
@Composable
fun KanzanProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    label: String? = null,
    showPercentage: Boolean = true,
    progressColor: Color = Color.Black,
    trackColor: Color = Color.LightGray,
    height: Dp = dp8,
    labelStyle: TextStyle = AppTextStyle.nunito_medium_14,
    percentageStyle: TextStyle = AppTextStyle.nunito_regular_12,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (label != null || showPercentage) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (label != null) {
                    Text(text = label, style = labelStyle, color = Color.Black)
                }
                if (showPercentage) {
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = percentageStyle,
                        color = Color.Gray,
                    )
                }
            }
            Spacer(modifier = Modifier.height(dp4))
        }
        LinearProgressIndicator(
            progress = progress.coerceIn(0f, 1f),
            modifier = Modifier.fillMaxWidth().height(height).clip(CircleShape),
            color = progressColor,
            trackColor = trackColor,
        )
    }
}

// endregion

// region ==================== Preview ====================

@Preview(showBackground = true, name = "Stepper 1. Step 0 of 4")
@Composable
private fun PreviewStepper0() {
    KanzanStepper(
        totalSteps = 4,
        currentStep = 0,
        modifier = Modifier.padding(dp16),
        labels = listOf("Data", "Verifikasi", "Pembayaran", "Selesai"),
    )
}

@Preview(showBackground = true, name = "Stepper 2. Step 2 of 4")
@Composable
private fun PreviewStepper2() {
    KanzanStepper(
        totalSteps = 4,
        currentStep = 2,
        modifier = Modifier.padding(dp16),
        labels = listOf("Data", "Verifikasi", "Pembayaran", "Selesai"),
    )
}

@Preview(showBackground = true, name = "Stepper 3. All completed")
@Composable
private fun PreviewStepperDone() {
    KanzanStepper(
        totalSteps = 3,
        currentStep = 3,
        modifier = Modifier.padding(dp16),
        labels = listOf("Input", "Review", "Done"),
    )
}

@Preview(showBackground = true, name = "Progress 1. 60%")
@Composable
private fun PreviewProgress60() {
    KanzanProgressBar(
        progress = 0.6f,
        modifier = Modifier.padding(dp16),
        label = "Pelunasan Hutang",
    )
}

@Preview(showBackground = true, name = "Progress 2. Various")
@Composable
private fun PreviewProgressVarious() {
    Column(modifier = Modifier.padding(dp16), verticalArrangement = Arrangement.spacedBy(dp16)) {
        KanzanProgressBar(progress = 0.25f, label = "Target Tabungan", progressColor = Color(0xFF2196F3))
        KanzanProgressBar(progress = 0.75f, label = "Pelunasan", progressColor = Color(0xFF4CAF50))
        KanzanProgressBar(progress = 1f, label = "Selesai", progressColor = Color(0xFF4CAF50))
    }
}

@Preview(showBackground = true, name = "Stepper 4. Interactive")
@Composable
private fun PreviewStepperInteractive() {
    var step by remember { mutableStateOf(1) }
    Column(modifier = Modifier.padding(dp16), verticalArrangement = Arrangement.spacedBy(dp16)) {
        KanzanStepper(
            totalSteps = 4,
            currentStep = step,
            labels = listOf("Data", "Verifikasi", "Bayar", "Selesai"),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(dp8)) {
            KanzanBaseButton(
                title = "Prev",
                onClick = { if (step > 0) step-- },
                buttonType = KanzanButtonType.OUTLINED,
                buttonSize = KanzanButtonSize.SMALL,
            )
            KanzanBaseButton(
                title = "Next",
                onClick = { if (step < 4) step++ },
                buttonSize = KanzanButtonSize.SMALL,
            )
        }
    }
}

@Preview(showBackground = true, name = "VerticalStepper 1. Step 1 of 4")
@Composable
private fun PreviewVerticalStepper1() {
    KanzanVerticalStepper(
        totalSteps = 4,
        currentStep = 1,
        modifier = Modifier.padding(dp16),
        labels = listOf("Pesanan Dibuat", "Diproses", "Dikirim", "Selesai"),
        descriptions = listOf("01 Jan 2026, 10:00", "Menunggu konfirmasi", "Belum dikirim", "Belum selesai"),
    )
}

@Preview(showBackground = true, name = "VerticalStepper 2. All completed")
@Composable
private fun PreviewVerticalStepperDone() {
    KanzanVerticalStepper(
        totalSteps = 3,
        currentStep = 3,
        modifier = Modifier.padding(dp16),
        labels = listOf("Input Data", "Review", "Selesai"),
        descriptions = listOf("Data hutang diisi", "Diverifikasi admin", "Hutang tercatat"),
    )
}

@Preview(showBackground = true, name = "VerticalStepper 3. Interactive")
@Composable
private fun PreviewVerticalStepperInteractive() {
    var step by remember { mutableStateOf(2) }
    Column(modifier = Modifier.padding(dp16)) {
        KanzanVerticalStepper(
            totalSteps = 5,
            currentStep = step,
            labels = listOf("Pengajuan", "Verifikasi", "Persetujuan", "Pencairan", "Selesai"),
        )
        Spacer(modifier = Modifier.height(dp16))
        Row(horizontalArrangement = Arrangement.spacedBy(dp8)) {
            KanzanBaseButton(
                title = "Prev",
                onClick = { if (step > 0) step-- },
                buttonType = KanzanButtonType.OUTLINED,
                buttonSize = KanzanButtonSize.SMALL,
            )
            KanzanBaseButton(
                title = "Next",
                onClick = { if (step < 5) step++ },
                buttonSize = KanzanButtonSize.SMALL,
            )
        }
    }
}

// endregion
