package com.kanzankazu.kanzanwidget.compose.widget

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kanzankazu.kanzanwidget.compose.ui.AppTextStyle
import com.kanzankazu.kanzanwidget.compose.ui.dp4
import com.kanzankazu.kanzanwidget.compose.ui.dp8
import com.kanzankazu.kanzanwidget.compose.ui.dp16

// region ==================== KanzanNumberStepper ====================

/**
 * Number stepper (increment/decrement) widget.
 *
 * @param value nilai saat ini.
 * @param onValueChanged callback saat nilai berubah.
 * @param modifier Modifier.
 * @param minValue nilai minimum.
 * @param maxValue nilai maksimum.
 * @param step langkah increment/decrement.
 * @param label label di atas stepper (opsional).
 * @param valueStyle style teks nilai.
 * @param labelStyle style teks label.
 * @param borderColor warna border.
 * @param cornerRadius radius sudut.
 * @param minWidth lebar minimum area nilai.
 */
@Composable
fun KanzanNumberStepper(
    value: Int,
    onValueChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
    minValue: Int = 0,
    maxValue: Int = Int.MAX_VALUE,
    step: Int = 1,
    label: String? = null,
    valueStyle: TextStyle = AppTextStyle.nunito_bold_16,
    labelStyle: TextStyle = AppTextStyle.nunito_regular_12,
    borderColor: Color = Color.LightGray,
    cornerRadius: Dp = dp8,
    minWidth: Dp = 48.dp,
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        if (label != null) {
            Text(
                text = label,
                style = labelStyle,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = dp4),
            )
        }
        Row(
            modifier = Modifier.border(1.dp, borderColor, RoundedCornerShape(cornerRadius)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            KanzanBaseButton(
                title = "−",
                onClick = {
                    val newValue = value - step
                    if (newValue >= minValue) onValueChanged(newValue)
                },
                enabled = value > minValue,
                buttonType = KanzanButtonType.TEXT,
                buttonSize = KanzanButtonSize.SMALL,
            )
            Text(
                text = value.toString(),
                style = valueStyle,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(min = minWidth),
            )
            KanzanBaseButton(
                title = "+",
                onClick = {
                    val newValue = value + step
                    if (newValue <= maxValue) onValueChanged(newValue)
                },
                enabled = value < maxValue,
                buttonType = KanzanButtonType.TEXT,
                buttonSize = KanzanButtonSize.SMALL,
            )
        }
    }
}

// endregion

// region ==================== Preview ====================

@Preview(showBackground = true, name = "NumberStepper 1. Basic")
@Composable
private fun PreviewNumberStepperBasic() {
    var value by remember { mutableStateOf(1) }
    KanzanNumberStepper(
        value = value,
        onValueChanged = { value = it },
        modifier = Modifier.padding(dp16),
        label = "Jumlah",
    )
}

@Preview(showBackground = true, name = "NumberStepper 2. Min/Max")
@Composable
private fun PreviewNumberStepperMinMax() {
    var value by remember { mutableStateOf(0) }
    KanzanNumberStepper(
        value = value,
        onValueChanged = { value = it },
        modifier = Modifier.padding(dp16),
        minValue = 0,
        maxValue = 10,
        label = "Cicilan (max 10)",
    )
}

@Preview(showBackground = true, name = "NumberStepper 3. Step 5")
@Composable
private fun PreviewNumberStepperStep5() {
    var value by remember { mutableStateOf(50) }
    KanzanNumberStepper(
        value = value,
        onValueChanged = { value = it },
        modifier = Modifier.padding(dp16),
        step = 5,
        minValue = 0,
        maxValue = 100,
        label = "Persentase",
    )
}

@Preview(showBackground = true, name = "NumberStepper 4. Multiple")
@Composable
private fun PreviewNumberStepperMultiple() {
    var qty by remember { mutableStateOf(1) }
    var months by remember { mutableStateOf(3) }
    Column(modifier = Modifier.padding(dp16), verticalArrangement = Arrangement.spacedBy(dp16)) {
        KanzanNumberStepper(value = qty, onValueChanged = { qty = it }, label = "Jumlah Item", minValue = 1, maxValue = 99)
        KanzanNumberStepper(value = months, onValueChanged = { months = it }, label = "Tenor (bulan)", minValue = 1, maxValue = 60)
    }
}

// endregion
