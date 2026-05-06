package com.kanzankazu.kanzanwidget.compose.widget

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.unit.dp
import com.kanzankazu.kanzanwidget.compose.ui.AppTextStyle
import com.kanzankazu.kanzanwidget.compose.ui.dp4
import com.kanzankazu.kanzanwidget.compose.ui.dp8
import com.kanzankazu.kanzanwidget.compose.ui.dp12
import com.kanzankazu.kanzanwidget.compose.ui.dp16
import com.kanzankazu.kanzanwidget.compose.ui.dp24

// region ==================== Data Model ====================

data class KanzanShowCaseStep(
    val title: String,
    val description: String,
    val icon: String? = null,
    val leadingIcon: @Composable (() -> Unit)? = null,
    val trailingIcon: @Composable (() -> Unit)? = null,
)

// endregion

// region ==================== KanzanOnboardingShowCase ====================

/**
 * Onboarding bubble showcase overlay.
 * Menampilkan step-by-step bubble guide di atas konten.
 *
 * @param steps daftar step onboarding.
 * @param isVisible kontrol visibilitas.
 * @param currentStep step saat ini (0-indexed).
 * @param onStepChanged callback saat step berubah.
 * @param onDismiss callback saat showcase ditutup.
 * @param modifier Modifier.
 * @param overlayColor warna overlay background.
 * @param bubbleColor warna bubble.
 * @param bubbleTextColor warna teks bubble.
 * @param titleStyle style teks title.
 * @param descriptionStyle style teks description.
 * @param bubbleShape bentuk bubble.
 * @param bubblePadding padding internal bubble.
 * @param nextText label tombol next.
 * @param prevText label tombol prev.
 * @param doneText label tombol done (step terakhir).
 * @param skipText label tombol skip (null = tidak tampil).
 * @param showStepCounter tampilkan counter "1/3".
 * @param bubbleAlignment posisi bubble.
 */
@Composable
fun KanzanOnboardingShowCase(
    steps: List<KanzanShowCaseStep>,
    isVisible: Boolean,
    currentStep: Int,
    onStepChanged: (Int) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    overlayColor: Color = Color.Black.copy(alpha = 0.6f),
    bubbleColor: Color = Color.White,
    bubbleTextColor: Color = Color.Black,
    titleStyle: TextStyle = AppTextStyle.nunito_bold_16,
    descriptionStyle: TextStyle = AppTextStyle.nunito_regular_14,
    bubbleShape: RoundedCornerShape = RoundedCornerShape(dp16),
    bubblePadding: Dp = dp24,
    nextText: String = "Lanjut",
    prevText: String = "Kembali",
    doneText: String = "Selesai",
    skipText: String? = "Lewati",
    showStepCounter: Boolean = true,
    bubbleAlignment: Alignment = Alignment.Center,
) {
    AnimatedVisibility(visible = isVisible, enter = fadeIn(), exit = fadeOut()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(overlayColor)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                ) { /* consume tap on overlay */ },
            contentAlignment = bubbleAlignment,
        ) {
            if (steps.isNotEmpty() && currentStep in steps.indices) {
                val step = steps[currentStep]
                val isFirst = currentStep == 0
                val isLast = currentStep == steps.lastIndex

                Column(
                    modifier = Modifier
                        .padding(horizontal = dp24)
                        .background(bubbleColor, bubbleShape)
                        .padding(bubblePadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    // Icon
                    step.icon?.let {
                        Text(text = it, style = AppTextStyle.nunito_regular_36)
                        Spacer(modifier = Modifier.height(dp12))
                    }

                    // Title with leading/trailing icons
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        step.leadingIcon?.invoke()
                        if (step.leadingIcon != null) KanzanSpacerHorizontal(width = dp8)
                        Text(text = step.title, style = titleStyle, color = bubbleTextColor)
                        if (step.trailingIcon != null) KanzanSpacerHorizontal(width = dp8)
                        step.trailingIcon?.invoke()
                    }
                    Spacer(modifier = Modifier.height(dp8))

                    // Description
                    Text(text = step.description, style = descriptionStyle, color = Color.Gray)
                    Spacer(modifier = Modifier.height(dp16))

                    // Step counter
                    if (showStepCounter) {
                        Text(
                            text = "${currentStep + 1} / ${steps.size}",
                            style = AppTextStyle.nunito_regular_12,
                            color = Color.Gray,
                        )
                        Spacer(modifier = Modifier.height(dp8))
                    }

                    // Navigation buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        // Skip / Prev
                        if (isFirst && skipText != null) {
                            KanzanBaseButton(
                                title = skipText,
                                onClick = onDismiss,
                                buttonType = KanzanButtonType.TEXT,
                                buttonSize = KanzanButtonSize.SMALL,
                                containerColor = Color.Gray,
                            )
                        } else if (!isFirst) {
                            KanzanBaseButton(
                                title = prevText,
                                onClick = { onStepChanged(currentStep - 1) },
                                buttonType = KanzanButtonType.TEXT,
                                buttonSize = KanzanButtonSize.SMALL,
                            )
                        } else {
                            KanzanSpacerHorizontal(width = dp4)
                        }

                        // Next / Done
                        KanzanBaseButton(
                            title = if (isLast) doneText else nextText,
                            onClick = {
                                if (isLast) onDismiss()
                                else onStepChanged(currentStep + 1)
                            },
                            buttonSize = KanzanButtonSize.SMALL,
                        )
                    }
                }
            }
        }
    }
}

// endregion


// region ==================== Preview ====================

private val sampleSteps = listOf(
    KanzanShowCaseStep("Selamat Datang", "Ini adalah aplikasi pencatat hutang piutang.", "👋"),
    KanzanShowCaseStep("Tambah Hutang", "Tap tombol + untuk menambah hutang baru.", "➕"),
    KanzanShowCaseStep("Lihat Riwayat", "Swipe ke tab Riwayat untuk melihat semua transaksi.", "📋"),
    KanzanShowCaseStep("Selesai", "Kamu siap menggunakan aplikasi ini.", "🎉"),
)

@Preview(showBackground = true, name = "Onboarding 1. Step 0")
@Composable
private fun PreviewOnboarding0() {
    KanzanOnboardingShowCase(
        steps = sampleSteps,
        isVisible = true,
        currentStep = 0,
        onStepChanged = {},
        onDismiss = {},
    )
}

@Preview(showBackground = true, name = "Onboarding 2. Step 2")
@Composable
private fun PreviewOnboarding2() {
    KanzanOnboardingShowCase(
        steps = sampleSteps,
        isVisible = true,
        currentStep = 2,
        onStepChanged = {},
        onDismiss = {},
    )
}

@Preview(showBackground = true, name = "Onboarding 3. Last step")
@Composable
private fun PreviewOnboardingLast() {
    KanzanOnboardingShowCase(
        steps = sampleSteps,
        isVisible = true,
        currentStep = 3,
        onStepChanged = {},
        onDismiss = {},
    )
}

@Preview(showBackground = true, name = "Onboarding 4. Interactive")
@Composable
private fun PreviewOnboardingInteractive() {
    var step by remember { mutableStateOf(0) }
    var visible by remember { mutableStateOf(true) }
    Box(modifier = Modifier.fillMaxSize()) {
        Text(text = "Background content", modifier = Modifier.padding(dp16), style = AppTextStyle.nunito_regular_14)
        KanzanOnboardingShowCase(
            steps = sampleSteps,
            isVisible = visible,
            currentStep = step,
            onStepChanged = { step = it },
            onDismiss = { visible = false },
        )
    }
}

// endregion
