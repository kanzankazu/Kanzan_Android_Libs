package com.kanzankazu.kanzanwidget.compose.widget

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kanzankazu.kanzanwidget.compose.ui.AppTextStyle
import com.kanzankazu.kanzanwidget.compose.ui.dp4
import com.kanzankazu.kanzanwidget.compose.ui.dp8
import com.kanzankazu.kanzanwidget.compose.ui.dp12
import com.kanzankazu.kanzanwidget.compose.ui.dp16
import com.kanzankazu.kanzanwidget.compose.ui.dp80

// region ==================== Data Models ====================

data class KanzanChartItem(
    val label: String,
    val value: Float,
    val color: Color,
)

// endregion

// region ==================== KanzanBarChart ====================

/**
 * Simple horizontal bar chart.
 *
 * @param items data chart.
 * @param modifier Modifier.
 * @param barHeight tinggi per bar.
 * @param barSpacing jarak antar bar.
 * @param showLabels tampilkan label.
 * @param showValues tampilkan nilai.
 * @param labelStyle style teks label.
 * @param valueStyle style teks value.
 * @param trackColor warna track background.
 * @param animationDurationMs durasi animasi.
 */
@Composable
fun KanzanBarChart(
    items: List<KanzanChartItem>,
    modifier: Modifier = Modifier,
    barHeight: Dp = dp16,
    barSpacing: Dp = dp12,
    showLabels: Boolean = true,
    showValues: Boolean = true,
    labelStyle: TextStyle = AppTextStyle.nunito_regular_12,
    valueStyle: TextStyle = AppTextStyle.nunito_medium_12,
    trackColor: Color = Color.LightGray.copy(alpha = 0.2f),
    animationDurationMs: Int = 600,
) {
    val maxValue = items.maxOfOrNull { it.value } ?: 1f
    var animate by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { animate = true }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(barSpacing)) {
        items.forEach { item ->
            val fraction = if (maxValue > 0) item.value / maxValue else 0f
            val animatedFraction by animateFloatAsState(
                targetValue = if (animate) fraction else 0f,
                animationSpec = tween(animationDurationMs),
                label = "bar_${item.label}",
            )
            if (showLabels) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(text = item.label, style = labelStyle, color = Color.DarkGray)
                    if (showValues) Text(text = item.value.toLong().toString(), style = valueStyle, color = Color.Black)
                }
                Spacer(modifier = Modifier.height(dp4))
            }
            Box(modifier = Modifier.fillMaxWidth().height(barHeight)) {
                // Track
                Box(modifier = Modifier.matchParentSize().background(trackColor, CircleShape))
                // Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedFraction)
                        .height(barHeight)
                        .background(item.color, CircleShape),
                )
            }
        }
    }
}

// endregion

// region ==================== KanzanPieChart ====================

/**
 * Simple pie/donut chart.
 *
 * @param items data chart.
 * @param modifier Modifier.
 * @param size ukuran chart.
 * @param strokeWidth ketebalan donut (0 = filled pie).
 * @param showLegend tampilkan legend.
 * @param legendStyle style teks legend.
 * @param animationDurationMs durasi animasi.
 */
@Composable
fun KanzanPieChart(
    items: List<KanzanChartItem>,
    modifier: Modifier = Modifier,
    size: Dp = 150.dp,
    strokeWidth: Dp = 24.dp,
    showLegend: Boolean = true,
    legendStyle: TextStyle = AppTextStyle.nunito_regular_12,
    animationDurationMs: Int = 800,
) {
    val total = items.sumOf { it.value.toDouble() }.toFloat()
    var animate by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { animate = true }
    val animatedSweep by animateFloatAsState(
        targetValue = if (animate) 360f else 0f,
        animationSpec = tween(animationDurationMs),
        label = "pieSweep",
    )

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Canvas(modifier = Modifier.size(size)) {
            val canvasSize = this.size
            val stroke = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Butt)
            val padding = strokeWidth.toPx() / 2
            val arcSize = Size(canvasSize.width - strokeWidth.toPx(), canvasSize.height - strokeWidth.toPx())
            val topLeft = Offset(padding, padding)

            var startAngle = -90f
            items.forEach { item ->
                val sweep = if (total > 0) (item.value / total) * animatedSweep else 0f
                if (strokeWidth.value > 0) {
                    drawArc(color = item.color, startAngle = startAngle, sweepAngle = sweep, useCenter = false, topLeft = topLeft, size = arcSize, style = stroke)
                } else {
                    drawArc(color = item.color, startAngle = startAngle, sweepAngle = sweep, useCenter = true)
                }
                startAngle += sweep
            }
        }

        if (showLegend && items.isNotEmpty()) {
            Spacer(modifier = Modifier.height(dp12))
            items.forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = dp4),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(modifier = Modifier.size(dp12).background(item.color, CircleShape))
                    Spacer(modifier = Modifier.width(dp8))
                    Text(text = item.label, style = legendStyle, color = Color.DarkGray, modifier = Modifier.weight(1f))
                    Text(text = item.value.toLong().toString(), style = legendStyle, color = Color.Black)
                }
            }
        }
    }
}

// endregion


// region ==================== Preview ====================

private val sampleChartItems = listOf(
    KanzanChartItem("Hutang", 5000000f, Color(0xFFF44336)),
    KanzanChartItem("Piutang", 3000000f, Color(0xFF4CAF50)),
    KanzanChartItem("Cicilan", 2000000f, Color(0xFF2196F3)),
    KanzanChartItem("Tabungan", 8000000f, Color(0xFFFF9800)),
)

@Preview(showBackground = true, name = "BarChart 1. Basic")
@Composable
private fun PreviewBarChart() {
    KanzanBarChart(items = sampleChartItems, modifier = Modifier.padding(dp16))
}

@Preview(showBackground = true, name = "BarChart 2. No labels")
@Composable
private fun PreviewBarChartNoLabels() {
    KanzanBarChart(items = sampleChartItems, modifier = Modifier.padding(dp16), showLabels = false)
}

@Preview(showBackground = true, name = "PieChart 1. Donut")
@Composable
private fun PreviewPieDonut() {
    KanzanPieChart(items = sampleChartItems, modifier = Modifier.padding(dp16))
}

@Preview(showBackground = true, name = "PieChart 2. Filled")
@Composable
private fun PreviewPieFilled() {
    KanzanPieChart(items = sampleChartItems, modifier = Modifier.padding(dp16), strokeWidth = 0.dp)
}

@Preview(showBackground = true, name = "PieChart 3. No legend")
@Composable
private fun PreviewPieNoLegend() {
    KanzanPieChart(items = sampleChartItems, modifier = Modifier.padding(dp16), showLegend = false, size = dp80)
}

// endregion
