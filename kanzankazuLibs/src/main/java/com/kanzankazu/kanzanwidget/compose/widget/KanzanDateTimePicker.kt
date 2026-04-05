@file:OptIn(ExperimentalMaterial3Api::class)

package com.kanzankazu.kanzanwidget.compose.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.kanzankazu.kanzanwidget.compose.ui.AppTextStyle
import com.kanzankazu.kanzanwidget.compose.ui.dp8
import com.kanzankazu.kanzanwidget.compose.ui.dp16
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// region ==================== KanzanDatePicker ====================

/**
 * Date picker dialog wrapper.
 *
 * @param isVisible kontrol visibilitas.
 * @param onDismiss callback saat dialog ditutup.
 * @param onDateSelected callback saat tanggal dipilih (millis).
 * @param initialDateMillis tanggal awal (millis, null = hari ini).
 * @param confirmText label tombol konfirmasi.
 * @param cancelText label tombol batal.
 */
@Composable
fun KanzanDatePicker(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onDateSelected: (Long) -> Unit,
    initialDateMillis: Long? = null,
    confirmText: String = "Pilih",
    cancelText: String = "Batal",
) {
    if (!isVisible) return

    val state = rememberDatePickerState(initialSelectedDateMillis = initialDateMillis)

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                state.selectedDateMillis?.let { onDateSelected(it) }
                onDismiss()
            }) {
                Text(text = confirmText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = cancelText)
            }
        },
    ) {
        DatePicker(state = state)
    }
}

// endregion

// region ==================== KanzanTimePicker ====================

/**
 * Time picker dialog wrapper.
 *
 * @param isVisible kontrol visibilitas.
 * @param onDismiss callback saat dialog ditutup.
 * @param onTimeSelected callback saat waktu dipilih (hour, minute).
 * @param initialHour jam awal.
 * @param initialMinute menit awal.
 * @param is24Hour format 24 jam.
 * @param confirmText label tombol konfirmasi.
 * @param cancelText label tombol batal.
 */
@Composable
fun KanzanTimePicker(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onTimeSelected: (hour: Int, minute: Int) -> Unit,
    initialHour: Int = 0,
    initialMinute: Int = 0,
    is24Hour: Boolean = true,
    confirmText: String = "Pilih",
    cancelText: String = "Batal",
) {
    if (!isVisible) return

    val state = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = is24Hour,
    )

    KanzanDialog(
        isVisible = true,
        onDismiss = onDismiss,
        dialogType = KanzanDialogType.CUSTOM,
        title = "Pilih Waktu",
        customContent = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                TimePicker(state = state)
                Spacer(modifier = Modifier.height(dp16))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(dp8),
                ) {
                    KanzanBaseButton(
                        title = cancelText,
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        buttonType = KanzanButtonType.OUTLINED,
                        containerColor = androidx.compose.ui.graphics.Color.Gray,
                        borderColor = androidx.compose.ui.graphics.Color.Gray,
                        contentColor = androidx.compose.ui.graphics.Color.Gray,
                    )
                    KanzanBaseButton(
                        title = confirmText,
                        onClick = {
                            onTimeSelected(state.hour, state.minute)
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        },
    )
}

// endregion

// region ==================== Preview ====================

@Preview(showBackground = true, name = "DatePicker 1. Trigger button")
@Composable
private fun PreviewDatePickerTrigger() {
    var showPicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf("Belum dipilih") }
    val sdf = remember { SimpleDateFormat("dd MMM yyyy", Locale("id")) }

    Column(modifier = Modifier.padding(dp16)) {
        Text(text = "Tanggal: $selectedDate", style = AppTextStyle.nunito_regular_14)
        Spacer(modifier = Modifier.height(dp8))
        KanzanBaseButton(
            title = "Pilih Tanggal",
            onClick = { showPicker = true },
            buttonType = KanzanButtonType.OUTLINED,
        )
        KanzanDatePicker(
            isVisible = showPicker,
            onDismiss = { showPicker = false },
            onDateSelected = { millis ->
                selectedDate = sdf.format(Date(millis))
            },
        )
    }
}

@Preview(showBackground = true, name = "TimePicker 1. Trigger button")
@Composable
private fun PreviewTimePickerTrigger() {
    var showPicker by remember { mutableStateOf(false) }
    var selectedTime by remember { mutableStateOf("Belum dipilih") }

    Column(modifier = Modifier.padding(dp16)) {
        Text(text = "Waktu: $selectedTime", style = AppTextStyle.nunito_regular_14)
        Spacer(modifier = Modifier.height(dp8))
        KanzanBaseButton(
            title = "Pilih Waktu",
            onClick = { showPicker = true },
            buttonType = KanzanButtonType.OUTLINED,
        )
        KanzanTimePicker(
            isVisible = showPicker,
            onDismiss = { showPicker = false },
            onTimeSelected = { h, m ->
                selectedTime = "%02d:%02d".format(h, m)
            },
        )
    }
}

// endregion
