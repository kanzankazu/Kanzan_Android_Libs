package com.kanzankazu.kanzanwidget.compose.widget

import android.annotation.SuppressLint
import androidx.annotation.StringRes
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun KanzanTextField(
    @SuppressLint("SupportAnnotationUsage") @StringRes label: String,
    value: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    modifier: Modifier = Modifier,
    onValueChanged: (String) -> Unit,
) {
    OutlinedTextField(
        value = value,
        modifier = modifier,
        onValueChange = onValueChanged,
        singleLine = true,
        label = { Text(label) },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = keyboardType,
            imeAction = imeAction
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun KanzanTextFieldPreview() {
    KanzanTextField(
        label = "TextField",
        value = "qwerty",
        onValueChanged = {},
    )
}