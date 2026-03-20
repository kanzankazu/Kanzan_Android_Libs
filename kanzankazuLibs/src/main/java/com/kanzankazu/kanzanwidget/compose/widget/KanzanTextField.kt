package com.kanzankazu.kanzanwidget.compose.widget

import android.annotation.SuppressLint
import androidx.annotation.StringRes
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.kanzankazu.kanzanwidget.compose.ui.AppTextStyle
import com.kanzankazu.kanzanwidget.compose.ui.Shapes

enum class KanzanInputType(val value: String, val keyboardType: KeyboardType) {
    TEXT("Text", KeyboardType.Text),
    EMAIL("Email", KeyboardType.Email),
    PASSWORD("Password", KeyboardType.Password),
    NUMBER("Number", KeyboardType.Number),
    PHONE("Phone", KeyboardType.Phone),
    URI("URI", KeyboardType.Uri),
    DECIMAL_NUMBER("Decimal", KeyboardType.Number),
    NOMINAL("Nominal", KeyboardType.Number)
}

@Composable
fun KanzanTextField(
    @SuppressLint("SupportAnnotationUsage") @StringRes label: String,
    value: String,
    onValueChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    kanzanInputType: KanzanInputType? = null,
    imeAction: ImeAction = ImeAction.Next,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    placeholder: String = "",
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    supportingText: String? = null,
    errorMessage: String? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    textStyle: TextStyle = AppTextStyle.nunito_regular_16,
    labelStyle: TextStyle = AppTextStyle.nunito_regular_14,
    placeholderStyle: TextStyle = AppTextStyle.nunito_regular_14,
    supportingTextStyle: TextStyle = AppTextStyle.nunito_regular_12,
    errorTextStyle: TextStyle = AppTextStyle.nunito_regular_12,
    shape: Shape? = null,
    colors: TextFieldColors? = null,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    onFocusChange: (Boolean) -> Unit = {},
    isError: Boolean = false,
    isPassword: Boolean = false
) {
    val finalVisualTransformation = if (isPassword) PasswordVisualTransformation() else visualTransformation
    val finalIsError = isError || errorMessage != null
    val finalKeyboardType = kanzanInputType?.keyboardType ?: KanzanInputType.TEXT.keyboardType
    val isEmailInput = finalKeyboardType == KeyboardType.Email // Otomatis dari keyboard type
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    LaunchedEffect(isFocused) {
        onFocusChange(isFocused)
    }

    OutlinedTextField(
        value = value,
        onValueChange = { newValue ->
            when {
                isEmailInput -> {
                    // Process email input: convert spaces to @ and .
                    val processedValue = processEmailInput(newValue, value)
                    onValueChanged(processedValue)
                }
                kanzanInputType == KanzanInputType.NOMINAL -> {
                    // Process nominal input: format as Indonesian currency
                    val processedValue = processNominalInput(newValue, value)
                    onValueChanged(processedValue)
                }
                else -> {
                    onValueChanged(newValue)
                }
            }
        },
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        label = {
            Text(
                text = label,
                style = labelStyle
            )
        },
        placeholder = {
            Text(
                text = placeholder.ifEmpty { "" },
                style = placeholderStyle
            )
        },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        prefix = prefix,
        suffix = suffix,
        supportingText = if (errorMessage != null) {
            {
                Text(
                    text = errorMessage,
                    style = errorTextStyle,
                    color = Color.Red
                )
            }
        } else supportingText?.let { text ->
            {
                Text(
                    text = text,
                    style = supportingTextStyle
                )
            }
        },
        isError = finalIsError,
        visualTransformation = finalVisualTransformation,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = finalKeyboardType,
            imeAction = imeAction
        ),
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
        shape = shape ?: Shapes.medium,
        colors = colors ?: OutlinedTextFieldDefaults.colors(),
        interactionSource = interactionSource
    )
}

/**
 * Process email input by converting spaces to @ and . characters
 * Only processes new input, doesn't modify existing email format
 */
private fun processEmailInput(newValue: String, oldValue: String): String {
    if (newValue.length <= oldValue.length) {
        // User is deleting, return as-is
        return newValue
    }
    
    // Check if the new character is a space
    val addedChar = newValue.lastOrNull()
    if (addedChar != ' ') {
        // Not a space, return as-is
        return newValue
    }
    
    // Process the space based on current email structure
    val baseValue = newValue.dropLast(1) // Remove the space
    val hasAtSymbol = baseValue.contains('@')
    val hasDotSymbol = baseValue.contains('.')
    
    return when {
        !hasAtSymbol -> "$baseValue@" // First space becomes @
        hasAtSymbol && !baseValue.substringAfter('@').contains('.') -> "$baseValue." // Second space becomes .
        else -> baseValue // Additional spaces are removed
    }
}

/**
 * Process nominal input by formatting as Indonesian currency format
 * Converts numbers to format like "1.000.000"
 */
private fun processNominalInput(newValue: String, oldValue: String): String {
    if (newValue.length <= oldValue.length) {
        // User is deleting, return as-is
        return newValue
    }
    
    // Remove all non-digit characters
    val digitsOnly = newValue.filter { it.isDigit() }
    
    // Don't process if no digits or same as old value
    if (digitsOnly.isEmpty() || digitsOnly == oldValue.filter { it.isDigit() }) {
        return newValue
    }
    
    // Format as Indonesian currency
    val number = digitsOnly.toLongOrNull() ?: return newValue
    return when {
        number >= 1000000 -> String.format("%,d", number).replace(",", ".")
        number >= 1000 -> String.format("%,d", number).replace(",", ".")
        else -> number.toString()
    }
}

@Preview(showBackground = true)
@Composable
private fun KanzanTextFieldPreview() {
    var textValue by remember { mutableStateOf("qwerty") }
    KanzanTextField(
        label = "TextField",
        value = textValue,
        onValueChanged = { textValue = it },
    )
}

@Preview(showBackground = true, name = "With Placeholder")
@Composable
private fun KanzanTextFieldWithPlaceholderPreview() {
    var textValue by remember { mutableStateOf("") }
    KanzanTextField(
        label = "Email",
        value = textValue,
        onValueChanged = { textValue = it },
        placeholder = "Masukkan email Anda",
        kanzanInputType = KanzanInputType.EMAIL
    )
}

@Preview(showBackground = true, name = "Email Input Demo")
@Composable
private fun KanzanTextFieldEmailDemoPreview() {
    var emailValue by remember { mutableStateOf("user@domain.com") }
    KanzanTextField(
        label = "Email (Ketik spasi untuk @ dan .)",
        value = emailValue,
        onValueChanged = { emailValue = it },
        placeholder = "Ketik: user[spasi]domain[spasi]com",
        kanzanInputType = KanzanInputType.EMAIL,
        supportingText = "Spasi pertama = @, Spasi kedua = ., Spasi lainnya = dihapus"
    )
}

@Preview(showBackground = true, name = "Password Field")
@Composable
private fun KanzanTextFieldPasswordPreview() {
    var textValue by remember { mutableStateOf("password123") }
    KanzanTextField(
        label = "Password",
        value = textValue,
        onValueChanged = { textValue = it },
        placeholder = "Masukkan password",
        isPassword = true,
        kanzanInputType = KanzanInputType.PASSWORD
    )
}

@Preview(showBackground = true, name = "With Error")
@Composable
private fun KanzanTextFieldErrorPreview() {
    var textValue by remember { mutableStateOf("invalid-email") }
    KanzanTextField(
        label = "Email",
        value = textValue,
        onValueChanged = { textValue = it },
        placeholder = "Contoh: user@example.com",
        errorMessage = "Format email tidak valid",
        isError = true
    )
}

@Preview(showBackground = true, name = "Disabled Field")
@Composable
private fun KanzanTextFieldDisabledPreview() {
    KanzanTextField(
        label = "Username",
        value = "john_doe",
        onValueChanged = { },
        enabled = false,
        readOnly = true
    )
}

@Preview(showBackground = true, name = "Multiline Field")
@Composable
private fun KanzanTextFieldMultilinePreview() {
    var textValue by remember { mutableStateOf("Ini adalah contoh text\nyang memiliki beberapa baris\ndalam multiline TextField.\nBaris keempat\ndan baris kelima\nserta baris keenam.") }
    KanzanTextField(
        label = "Deskripsi",
        value = textValue,
        onValueChanged = { textValue = it },
        placeholder = "Tulis deskripsi di sini...",
        singleLine = false,
        maxLines = 4,
        kanzanInputType = KanzanInputType.TEXT,
    )
}

@Preview(showBackground = true, name = "Numeric Field")
@Composable
private fun KanzanTextFieldNumericPreview() {
    var textValue by remember { mutableStateOf("12345") }
    KanzanTextField(
        label = "Nomor Telepon",
        value = textValue,
        onValueChanged = { textValue = it },
        placeholder = "0812-3456-7890",
        kanzanInputType = KanzanInputType.PHONE,
        imeAction = ImeAction.Done
    )
}

@Preview(showBackground = true, name = "With Leading Icon")
@Composable
private fun KanzanTextFieldWithIconPreview() {
    var textValue by remember { mutableStateOf("search query") }
    KanzanTextField(
        label = "Pencarian",
        value = textValue,
        onValueChanged = { textValue = it },
        placeholder = "Cari sesuatu...",
        leadingIcon = {
            // Search icon placeholder
            Text("🔍")
        }
    )
}

@Preview(showBackground = true, name = "With Helper Text")
@Composable
private fun KanzanTextFieldWithHelperTextPreview() {
    var textValue by remember { mutableStateOf("") }
    KanzanTextField(
        label = "Username",
        value = textValue,
        onValueChanged = { textValue = it },
        placeholder = "Masukkan username",
        supportingText = "Username harus 3-20 karakter, hanya huruf dan angka"
    )
}

@Preview(showBackground = true, name = "With Helper Text and Error")
@Composable
private fun KanzanTextFieldWithHelperTextAndErrorPreview() {
    var textValue by remember { mutableStateOf("ab") }
    KanzanTextField(
        label = "Username",
        value = textValue,
        onValueChanged = { textValue = it },
        placeholder = "Masukkan username",
        errorMessage = "Username terlalu pendek, minimal 3 karakter",
        isError = true
    )
}

@Preview(showBackground = true, name = "Dropdown Field")
@Composable
private fun KanzanTextFieldDropdownPreview() {
    var selectedValue by remember { mutableStateOf("Option 1") }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    val options = listOf("Option 1", "Option 2", "Option 3", "Option 4", "Option 5")
    
    Box(modifier = Modifier.fillMaxWidth()) {
        KanzanTextField(
            label = "Pilih Kategori",
            value = selectedValue,
            onValueChanged = { selectedValue = it },
            placeholder = "Pilih kategori",
            readOnly = true,
            modifier = Modifier.clickable { isDropdownExpanded = true },
            trailingIcon = {
                Text(
                    text = "▼",
                    style = AppTextStyle.nunito_regular_16
                )
            }
        )
        
        DropdownMenu(
            expanded = isDropdownExpanded,
            onDismissRequest = { isDropdownExpanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        selectedValue = option
                        isDropdownExpanded = false
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Date Picker Field")
@Composable
private fun KanzanTextFieldDatePickerPreview() {
    var selectedDate by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    
    Box(modifier = Modifier.fillMaxWidth()) {
        KanzanTextField(
            label = "Tanggal Lahir",
            value = selectedDate,
            onValueChanged = { selectedDate = it },
            placeholder = "DD/MM/YYYY",
            readOnly = true,
            modifier = Modifier.clickable { showDatePicker = true },
            trailingIcon = {
                Text(
                    text = "📅",
                    style = AppTextStyle.nunito_regular_16
                )
            }
        )
        
        // Simulasi date picker dialog
        if (showDatePicker) {
            // Dalam implementasi nyata, gunakan DatePickerDialog
            // Ini hanya simulasi dengan dropdown sederhana
            DropdownMenu(
                expanded = showDatePicker,
                onDismissRequest = { showDatePicker = false }
            ) {
                listOf("01/01/1990", "15/06/1995", "20/12/2000", "05/03/1985").forEach { date ->
                    DropdownMenuItem(
                        text = { Text(date) },
                        onClick = {
                            selectedDate = date
                            showDatePicker = false
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Search Field with Clear")
@Composable
private fun KanzanTextFieldSearchWithClearPreview() {
    var searchQuery by remember { mutableStateOf("search text") }
    
    KanzanTextField(
        label = "Pencarian",
        value = searchQuery,
        onValueChanged = { searchQuery = it },
        placeholder = "Cari produk...",
        leadingIcon = {
            Text(
                text = "🔍",
                style = AppTextStyle.nunito_regular_16
            )
        },
        trailingIcon = {
            if (searchQuery.isNotEmpty()) {
                Text(
                    text = "✕",
                    style = AppTextStyle.nunito_regular_16,
                    modifier = Modifier.clickable { searchQuery = "" }
                )
            }
        }
    )
}

@Preview(showBackground = true, name = "Amount Field")
@Composable
private fun KanzanTextFieldAmountPreview() {
    var amount by remember { mutableStateOf("1.000.000") }
    
    KanzanTextField(
        label = "Jumlah",
        value = amount,
        onValueChanged = { amount = it },
        placeholder = "0",
        kanzanInputType = KanzanInputType.NUMBER,
        leadingIcon = {
            Text(
                text = "Rp",
                style = AppTextStyle.nunito_regular_16
            )
        },
        trailingIcon = {
            Text(
                text = "💰",
                style = AppTextStyle.nunito_regular_16
            )
        }
    )
}

@Preview(showBackground = true, name = "With Suffix")
@Composable
private fun KanzanTextFieldWithSuffixPreview() {
    var weight by remember { mutableStateOf("75.5") }
    
    KanzanTextField(
        label = "Berat Badan",
        value = weight,
        onValueChanged = { weight = it },
        placeholder = "0.0",
        kanzanInputType = KanzanInputType.NUMBER,
        suffix = {
            Text(
                text = "kg",
                style = AppTextStyle.nunito_regular_14
            )
        },
        supportingText = "Masukkan berat badan dalam kilogram"
    )
}

@Preview(showBackground = true, name = "Percentage Field")
@Composable
private fun KanzanTextFieldPercentagePreview() {
    var percentage by remember { mutableStateOf("85") }
    
    KanzanTextField(
        label = "Diskon",
        value = percentage,
        onValueChanged = { percentage = it },
        placeholder = "0",
        kanzanInputType = KanzanInputType.NUMBER,
        suffix = {
            Text(
                text = "%",
                style = AppTextStyle.nunito_regular_14,
                color = Color.Blue
            )
        },
        trailingIcon = {
            Text(
                text = "🏷️",
                style = AppTextStyle.nunito_regular_16
            )
        }
    )
}

@Preview(showBackground = true, name = "URL Field")
@Composable
private fun KanzanTextFieldUrlPreview() {
    var url by remember { mutableStateOf("https://example") }
    
    KanzanTextField(
        label = "Website URL",
        value = url,
        onValueChanged = { url = it },
        placeholder = "https://",
        kanzanInputType = KanzanInputType.URI,
        leadingIcon = {
            Text(
                text = "🌐",
                style = AppTextStyle.nunito_regular_16
            )
        },
        suffix = {
            Text(
                text = ".com",
                style = AppTextStyle.nunito_regular_12,
                color = Color.Gray
            )
        },
        supportingText = "Masukkan alamat website lengkap"
    )
}

@Preview(showBackground = true, name = "KanzanInputType Demo")
@Composable
private fun KanzanInputTypeDemoPreview() {
    var textValue by remember { mutableStateOf("") }
    
    KanzanTextField(
        label = "Text Input",
        value = textValue,
        onValueChanged = { textValue = it },
        placeholder = "Ketik sesuatu...",
        kanzanInputType = KanzanInputType.TEXT,
        supportingText = "Input teks normal"
    )
}

@Preview(showBackground = true, name = "Decimal Input")
@Composable
private fun KanzanDecimalInputPreview() {
    var decimalValue by remember { mutableStateOf("99.99") }
    
    KanzanTextField(
        label = "Harga",
        value = decimalValue,
        onValueChanged = { decimalValue = it },
        placeholder = "0.00",
        kanzanInputType = KanzanInputType.DECIMAL_NUMBER,
        leadingIcon = {
            Text(
                text = "$",
                style = AppTextStyle.nunito_regular_16
            )
        },
        suffix = {
            Text(
                text = "USD",
                style = AppTextStyle.nunito_regular_12,
                color = Color.Blue
            )
        }
    )
}

@Preview(showBackground = true, name = "Nominal Input")
@Composable
private fun KanzanNominalInputPreview() {
    var nominalValue by remember { mutableStateOf("1.000.000") }
    
    KanzanTextField(
        label = "Nominal",
        value = nominalValue,
        onValueChanged = { nominalValue = it },
        placeholder = "0",
        kanzanInputType = KanzanInputType.NOMINAL,
        leadingIcon = {
            Text(
                text = "Rp",
                style = AppTextStyle.nunito_regular_16
            )
        },
        suffix = {
            Text(
                text = "IDR",
                style = AppTextStyle.nunito_regular_12,
                color = Color.Green
            )
        },
        supportingText = "Input akan otomatis diformat: 1.000.000"
    )
}