package com.kanzankazu.kanzanwidget.compose.widget

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kanzankazu.kanzanwidget.compose.ui.AppTextStyle
import com.kanzankazu.kanzanwidget.compose.ui.dp4
import com.kanzankazu.kanzanwidget.compose.ui.dp8
import com.kanzankazu.kanzanwidget.compose.ui.dp16
import com.kanzankazu.kanzanwidget.compose.ui.dp64
import com.kanzankazu.kanzanwidget.compose.ui.dp80
import com.kanzankazu.kanzanwidget.compose.ui.dp96

// region ==================== KanzanCircularProgress ====================

/**
 * Circular progress indicator dengan label di tengah.
 *
 * @param progress nilai progress (0f - 1f). Null = indeterminate.
 * @param modifier Modifier.
 * @param size ukuran lingkaran.
 * @param strokeWidth ketebalan garis.
 * @param progressColor warna progress.
 * @param trackColor warna track.
 * @param strokeCap bentuk ujung garis (Round/Butt/Square).
 * @param label teks di tengah (null = auto percentage).
 * @param labelStyle style teks label.
 * @param labelColor warna label.
 * @param animationDurationMs durasi animasi progress.
 */
@Composable
fun KanzanCircularProgress(
    progress: Float? = null,
    modifier: Modifier = Modifier,
    size: Dp = dp80,
    strokeWidth: Dp = dp8,
    progressColor: Color = Color.Black,
    trackColor: Color = Color.LightGray.copy(alpha = 0.3f),
    strokeCap: StrokeCap = StrokeCap.Round,
    label: String? = null,
    labelStyle: TextStyle = AppTextStyle.nunito_bold_16,
    labelColor: Color = Color.Black,
    animationDurationMs: Int = 600,
) {
    var animStarted by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { animStarted = true }

    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        if (progress == null) {
            CircularProgressIndicator(
                modifier = Modifier.size(size),
                color = progressColor,
                strokeWidth = strokeWidth,
            )
        } else {
            val animatedProgress by animateFloatAsState(
                targetValue = if (animStarted) progress.coerceIn(0f, 1f) else 0f,
                animationSpec = tween(animationDurationMs),
                label = "circularProgress",
            )
            Canvas(modifier = Modifier.size(size)) {
                val stroke = Stroke(width = strokeWidth.toPx(), cap = strokeCap, join = StrokeJoin.Round)
                drawArc(color = trackColor, startAngle = 0f, sweepAngle = 360f, useCenter = false, style = stroke)
                drawArc(color = progressColor, startAngle = -90f, sweepAngle = animatedProgress * 360f, useCenter = false, style = stroke)
            }
        }
        if (label != null) {
            Text(text = label, style = labelStyle, color = labelColor)
        } else if (progress != null) {
            Text(text = "${(progress * 100).toInt()}%", style = labelStyle, color = labelColor)
        }
    }
}

// endregion

// region ==================== KanzanGradientCircularProgress ====================

/**
 * Circular progress dengan gradient warna.
 *
 * @param progress nilai progress (0f - 1f).
 * @param gradientColors daftar warna gradient.
 * @param modifier Modifier.
 * @param size ukuran lingkaran.
 * @param strokeWidth ketebalan garis.
 * @param trackColor warna track.
 * @param strokeCap bentuk ujung garis.
 * @param label teks di tengah.
 * @param labelStyle style teks label.
 * @param labelColor warna label.
 * @param animationDurationMs durasi animasi.
 */
@Composable
fun KanzanGradientCircularProgress(
    progress: Float,
    gradientColors: List<Color>,
    modifier: Modifier = Modifier,
    size: Dp = dp80,
    strokeWidth: Dp = dp8,
    trackColor: Color = Color.LightGray.copy(alpha = 0.3f),
    strokeCap: StrokeCap = StrokeCap.Round,
    label: String? = null,
    labelStyle: TextStyle = AppTextStyle.nunito_bold_16,
    labelColor: Color = Color.Black,
    animationDurationMs: Int = 600,
) {
    var animStarted by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { animStarted = true }

    val animatedProgress by animateFloatAsState(
        targetValue = if (animStarted) progress.coerceIn(0f, 1f) else 0f,
        animationSpec = tween(animationDurationMs),
        label = "gradientProgress",
    )

    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(size)) {
            val stroke = Stroke(width = strokeWidth.toPx(), cap = strokeCap, join = StrokeJoin.Round)
            drawArc(color = trackColor, startAngle = 0f, sweepAngle = 360f, useCenter = false, style = stroke)
            drawArc(
                brush = Brush.sweepGradient(colors = gradientColors),
                startAngle = -90f,
                sweepAngle = animatedProgress * 360f,
                useCenter = false,
                style = stroke,
            )
        }
        if (label != null) {
            Text(text = label, style = labelStyle, color = labelColor)
        } else {
            Text(text = "${(progress * 100).toInt()}%", style = labelStyle, color = labelColor)
        }
    }
}

// endregion

// region ==================== KanzanPulsingProgress ====================

/**
 * Pulsing circular progress — animasi pulse berulang.
 * Cocok untuk loading state yang eye-catching.
 *
 * @param modifier Modifier.
 * @param size ukuran lingkaran.
 * @param color warna utama.
 * @param strokeWidth ketebalan garis.
 * @param pulseDurationMs durasi satu siklus pulse.
 */
@Composable
fun KanzanPulsingProgress(
    modifier: Modifier = Modifier,
    size: Dp = dp80,
    color: Color = Color.Black,
    strokeWidth: Dp = dp4,
    pulseDurationMs: Int = 1000,
) {
    val transition = rememberInfiniteTransition(label = "pulse")
    val scale by transition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(pulseDurationMs, easing = LinearEasing), RepeatMode.Reverse),
        label = "pulseScale",
    )
    val alpha by transition.animateFloat(
        initialValue = 1f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(tween(pulseDurationMs, easing = LinearEasing), RepeatMode.Reverse),
        label = "pulseAlpha",
    )

    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(size)) {
            val scaledRadius = (this.size.minDimension / 2) * scale
            drawCircle(
                color = color.copy(alpha = alpha * 0.3f),
                radius = scaledRadius,
            )
            drawCircle(
                color = color.copy(alpha = alpha),
                radius = scaledRadius * 0.6f,
                style = Stroke(width = strokeWidth.toPx()),
            )
        }
    }
}

// endregion

// region ==================== KanzanSpinnerProgress ====================

/**
 * Spinning arc progress — arc yang berputar terus.
 *
 * @param modifier Modifier.
 * @param size ukuran.
 * @param color warna arc.
 * @param strokeWidth ketebalan.
 * @param arcAngle sudut arc (derajat).
 * @param spinDurationMs durasi satu putaran penuh.
 */
@Composable
fun KanzanSpinnerProgress(
    modifier: Modifier = Modifier,
    size: Dp = dp64,
    color: Color = Color.Black,
    strokeWidth: Dp = dp4,
    arcAngle: Float = 270f,
    spinDurationMs: Int = 1200,
) {
    val transition = rememberInfiniteTransition(label = "spinner")
    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(spinDurationMs, easing = LinearEasing)),
        label = "spinRotation",
    )

    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(size)) {
            val stroke = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            rotate(rotation) {
                drawArc(
                    color = color,
                    startAngle = 0f,
                    sweepAngle = arcAngle,
                    useCenter = false,
                    style = stroke,
                )
            }
        }
    }
}

// endregion

// region ==================== KanzanMultiSegmentProgress ====================

/**
 * Multi-segment circular progress — beberapa segment warna berbeda.
 * Cocok untuk breakdown kategori (hutang, piutang, tabungan).
 *
 * @param segments daftar pair (value, color).
 * @param modifier Modifier.
 * @param size ukuran lingkaran.
 * @param strokeWidth ketebalan garis.
 * @param trackColor warna track.
 * @param gapAngle jarak (derajat) antar segment.
 * @param strokeCap bentuk ujung garis.
 * @param label teks di tengah.
 * @param labelStyle style teks label.
 * @param labelColor warna label.
 * @param animationDurationMs durasi animasi.
 */
@Composable
fun KanzanMultiSegmentProgress(
    segments: List<Pair<Float, Color>>,
    modifier: Modifier = Modifier,
    size: Dp = dp80,
    strokeWidth: Dp = dp8,
    trackColor: Color = Color.LightGray.copy(alpha = 0.3f),
    gapAngle: Float = 3f,
    strokeCap: StrokeCap = StrokeCap.Round,
    label: String? = null,
    labelStyle: TextStyle = AppTextStyle.nunito_bold_16,
    labelColor: Color = Color.Black,
    animationDurationMs: Int = 600,
) {
    val total = segments.sumOf { it.first.toDouble() }.toFloat()
    var animStarted by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { animStarted = true }

    val animatedFraction by animateFloatAsState(
        targetValue = if (animStarted) 1f else 0f,
        animationSpec = tween(animationDurationMs),
        label = "multiSegment",
    )

    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(size)) {
            val stroke = Stroke(width = strokeWidth.toPx(), cap = strokeCap, join = StrokeJoin.Round)
            // Track
            drawArc(color = trackColor, startAngle = 0f, sweepAngle = 360f, useCenter = false, style = stroke)
            // Segments
            val totalGap = gapAngle * segments.size
            val availableSweep = (360f - totalGap) * animatedFraction
            var startAngle = -90f
            segments.forEach { (value, color) ->
                val sweep = if (total > 0) (value / total) * availableSweep else 0f
                drawArc(color = color, startAngle = startAngle, sweepAngle = sweep, useCenter = false, style = stroke)
                startAngle += sweep + gapAngle
            }
        }
        label?.let { Text(text = it, style = labelStyle, color = labelColor) }
    }
}

// endregion


// region ==================== Preview ====================

@Preview(showBackground = true, name = "Progress 1. Basic 65%")
@Composable
private fun PreviewBasic65() {
    KanzanCircularProgress(progress = 0.65f, modifier = Modifier.padding(dp16))
}

@Preview(showBackground = true, name = "Progress 2. Various sizes")
@Composable
private fun PreviewVariousSizes() {
    Row(modifier = Modifier.padding(dp16), horizontalArrangement = Arrangement.spacedBy(dp16)) {
        KanzanCircularProgress(progress = 0.25f, size = dp64, progressColor = Color(0xFFF44336))
        KanzanCircularProgress(progress = 0.5f, size = dp64, progressColor = Color(0xFFFF9800))
        KanzanCircularProgress(progress = 0.75f, size = dp64, progressColor = Color(0xFF4CAF50))
        KanzanCircularProgress(progress = 1f, size = dp64, progressColor = Color(0xFF2196F3))
    }
}

@Preview(showBackground = true, name = "Progress 3. Custom label")
@Composable
private fun PreviewCustomLabel() {
    KanzanCircularProgress(
        progress = 0.8f,
        modifier = Modifier.padding(dp16),
        size = dp96,
        label = "Rp 8jt",
        labelStyle = AppTextStyle.nunito_bold_14,
        progressColor = Color(0xFF4CAF50),
    )
}

@Preview(showBackground = true, name = "Progress 4. Indeterminate")
@Composable
private fun PreviewIndeterminate() {
    KanzanCircularProgress(progress = null, modifier = Modifier.padding(dp16))
}

@Preview(showBackground = true, name = "Progress 5. Flat cap")
@Composable
private fun PreviewFlatCap() {
    KanzanCircularProgress(
        progress = 0.7f,
        modifier = Modifier.padding(dp16),
        strokeCap = StrokeCap.Butt,
        strokeWidth = 12.dp,
        progressColor = Color(0xFF9C27B0),
    )
}

@Preview(showBackground = true, name = "Progress 6. Gradient")
@Composable
private fun PreviewGradient() {
    KanzanGradientCircularProgress(
        progress = 0.75f,
        gradientColors = listOf(Color(0xFF1A3194), Color(0xFF1D41C5), Color(0xFF7F98FF)),
        modifier = Modifier.padding(dp16),
        size = dp96,
    )
}

@Preview(showBackground = true, name = "Progress 7. Gradient warm")
@Composable
private fun PreviewGradientWarm() {
    KanzanGradientCircularProgress(
        progress = 0.6f,
        gradientColors = listOf(Color(0xFFFF5722), Color(0xFFFF9800), Color(0xFFFFEB3B)),
        modifier = Modifier.padding(dp16),
        size = dp96,
        strokeWidth = 12.dp,
    )
}

@Preview(showBackground = true, name = "Progress 8. Pulsing")
@Composable
private fun PreviewPulsing() {
    KanzanPulsingProgress(modifier = Modifier.padding(dp16), color = Color(0xFF2196F3))
}

@Preview(showBackground = true, name = "Progress 9. Pulsing red")
@Composable
private fun PreviewPulsingRed() {
    KanzanPulsingProgress(modifier = Modifier.padding(dp16), color = Color(0xFFF44336), size = dp64)
}

@Preview(showBackground = true, name = "Progress 10. Spinner")
@Composable
private fun PreviewSpinner() {
    KanzanSpinnerProgress(modifier = Modifier.padding(dp16), color = Color.Black)
}

@Preview(showBackground = true, name = "Progress 11. Spinner colored")
@Composable
private fun PreviewSpinnerColored() {
    Row(modifier = Modifier.padding(dp16), horizontalArrangement = Arrangement.spacedBy(dp16)) {
        KanzanSpinnerProgress(color = Color(0xFF4CAF50), size = 40.dp, strokeWidth = 3.dp)
        KanzanSpinnerProgress(color = Color(0xFF2196F3), size = 40.dp, arcAngle = 180f)
        KanzanSpinnerProgress(color = Color(0xFFF44336), size = 40.dp, arcAngle = 90f, spinDurationMs = 800)
    }
}

@Preview(showBackground = true, name = "Progress 12. Multi-segment")
@Composable
private fun PreviewMultiSegment() {
    KanzanMultiSegmentProgress(
        segments = listOf(
            5000f to Color(0xFFF44336),
            3000f to Color(0xFF4CAF50),
            2000f to Color(0xFF2196F3),
        ),
        modifier = Modifier.padding(dp16),
        size = dp96,
        label = "10jt",
        labelStyle = AppTextStyle.nunito_bold_14,
    )
}

@Preview(showBackground = true, name = "Progress 13. Multi-segment thin")
@Composable
private fun PreviewMultiSegmentThin() {
    KanzanMultiSegmentProgress(
        segments = listOf(
            40f to Color(0xFFFFEB3B),
            30f to Color(0xFF4CAF50),
            20f to Color(0xFF2196F3),
            10f to Color(0xFF9C27B0),
        ),
        modifier = Modifier.padding(dp16),
        size = dp80,
        strokeWidth = dp4,
        gapAngle = 6f,
    )
}

@Preview(showBackground = true, name = "Progress 14. All variants row")
@Composable
private fun PreviewAllVariants() {
    Row(modifier = Modifier.padding(dp16), horizontalArrangement = Arrangement.spacedBy(dp8)) {
        KanzanCircularProgress(progress = 0.7f, size = 50.dp, strokeWidth = dp4)
        KanzanGradientCircularProgress(
            progress = 0.7f,
            gradientColors = listOf(Color(0xFF4CAF50), Color(0xFFCDDC39)),
            size = 50.dp,
            strokeWidth = dp4,
        )
        KanzanSpinnerProgress(size = 50.dp, strokeWidth = 3.dp, color = Color(0xFF2196F3))
        KanzanPulsingProgress(size = 50.dp, color = Color(0xFFF44336))
    }
}

// endregion
