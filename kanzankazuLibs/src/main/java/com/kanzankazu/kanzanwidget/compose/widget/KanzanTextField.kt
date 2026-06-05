package com.kanzankazu.kanzanwidget.compose.widget

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.kanzankazu.kanzanwidget.compose.ui.AppTextStyle
import com.kanzankazu.kanzanwidget.compose.ui.Shapes

/**
 * Data class untuk mengatur warna KanzanTextField secara granular.
 * Gunakan [Color.Unspecified] untuk menggunakan default dari MaterialTheme.
 *
 * Contoh penggunaan:
 * ```
 * KanzanTextField(
 *     kanzanColors = KanzanTextFieldColors(
 *         focusedBorderColor = Color.Blue,
 *         unfocusedBorderColor = Color.Gray,
 *     )
 * )
 * ```
 *
 * Atau pakai preset:
 * ```
 * KanzanTextField(kanzanColors = KanzanTextFieldColors.error())
 * KanzanTextField(kanzanColors = KanzanTextFieldColors.success())
 * ```
 */
data class KanzanTextFieldColors(
    // Text colors
    val textColor: Color = Color.Unspecified,
    val disabledTextColor: Color = Color.Unspecified,
    val errorTextColor: Color = Color.Unspecified,

    // Cursor
    val cursorColor: Color = Color.Unspecified,
    val errorCursorColor: Color = Color.Unspecified,

    // Border colors
    val focusedBorderColor: Color = Color.Unspecified,
    val unfocusedBorderColor: Color = Color.Unspecified,
    val disabledBorderColor: Color = Color.Unspecified,
    val errorBorderColor: Color = Color.Unspecified,

    // Label colors
    val focusedLabelColor: Color = Color.Unspecified,
    val unfocusedLabelColor: Color = Color.Unspecified,
    val disabledLabelColor: Color = Color.Unspecified,
    val errorLabelColor: Color = Color.Unspecified,

    // Placeholder
    val placeholderColor: Color = Color.Unspecified,
    val disabledPlaceholderColor: Color = Color.Unspecified,

    // Icon colors
    val leadingIconColor: Color = Color.Unspecified,
    val disabledLeadingIconColor: Color = Color.Unspecified,
    val errorLeadingIconColor: Color = Color.Unspecified,
    val trailingIconColor: Color = Color.Unspecified,
    val disabledTrailingIconColor: Color = Color.Unspecified,
    val errorTrailingIconColor: Color = Color.Unspecified,

    // Container / background
    val focusedContainerColor: Color = Color.Unspecified,
    val unfocusedContainerColor: Color = Color.Unspecified,
    val disabledContainerColor: Color = Color.Unspecified,
    val errorContainerColor: Color = Color.Unspecified,

    // Supporting text
    val supportingTextColor: Color = Color.Unspecified,
    val disabledSupportingTextColor: Color = Color.Unspecified,
    val errorSupportingTextColor: Color = Color.Unspecified,
) {
    companion object {
        /** Default — semua pakai MaterialTheme */
        fun defaults() = KanzanTextFieldColors()

        /** Preset untuk state error — border & label merah */
        fun error(
            borderColor: Color = Color.Red,
            labelColor: Color = Color.Red,
            supportingColor: Color = Color.Red,
        ) = KanzanTextFieldColors(
            focusedBorderColor = borderColor,
            unfocusedBorderColor = borderColor,
            errorBorderColor = borderColor,
            focusedLabelColor = labelColor,
            unfocusedLabelColor = labelColor,
            errorLabelColor = labelColor,
            errorSupportingTextColor = supportingColor,
        )

        /** Preset untuk state success — border & label hijau */
        fun success(
            borderColor: Color = Color(0xFF4CAF50),
            labelColor: Color = Color(0xFF4CAF50),
        ) = KanzanTextFieldColors(
            focusedBorderColor = borderColor,
            unfocusedBorderColor = borderColor,
            focusedLabelColor = labelColor,
            unfocusedLabelColor = labelColor,
        )

        /** Preset untuk dark mode manual */
        fun dark(
            textColor: Color = Color.White,
            borderColor: Color = Color(0xFF90CAF9),
            labelColor: Color = Color(0xFFBBDEFB),
            containerColor: Color = Color(0xFF1E1E1E),
            placeholderColor: Color = Color(0xFF757575),
        ) = KanzanTextFieldColors(
            textColor = textColor,
            focusedBorderColor = borderColor,
            unfocusedBorderColor = borderColor.copy(alpha = 0.5f),
            focusedLabelColor = labelColor,
            unfocusedLabelColor = labelColor.copy(alpha = 0.7f),
            focusedContainerColor = containerColor,
            unfocusedContainerColor = containerColor,
            placeholderColor = placeholderColor,
        )
    }
}

enum class KanzanInputType(val value: String, val keyboardType: KeyboardType) {
    TEXT("Text", KeyboardType.Text),
    EMAIL("Email", KeyboardType.Email),
    PASSWORD("Password", KeyboardType.Password),
    NUMBER("Number", KeyboardType.Number),
    PHONE("Phone", KeyboardType.Phone),
    PHONE_FORMATTED("Phone Formatted", KeyboardType.Number),
    URI("URI", KeyboardType.Uri),
    DECIMAL_NUMBER("Decimal", KeyboardType.Decimal),
    NOMINAL("Nominal", KeyboardType.Number),
    PATTERNED("Patterned", KeyboardType.Number),
    MULTILINE("Multiline", KeyboardType.Text),
    SEARCH("Search", KeyboardType.Text),
    PIN("PIN", KeyboardType.NumberPassword),
    DISABLED("Disabled", KeyboardType.Text),
    CLICKABLE("Clickable", KeyboardType.Text)
}

/**
 * Provides default leadingIcon, trailingIcon, prefix, suffix per [KanzanInputType].
 * User-supplied values take priority (via `?:` at call-site).
 */
@Composable
private fun KanzanInputType.defaultLeadingIcon(): (@Composable () -> Unit)? = when (this) {
    KanzanInputType.EMAIL -> ({ Text(text = "✉️", style = AppTextStyle.nunito_regular_16) })
    KanzanInputType.PASSWORD -> ({ Text(text = "🔒", style = AppTextStyle.nunito_regular_16) })
    KanzanInputType.PHONE -> ({ Text(text = "📞", style = AppTextStyle.nunito_regular_16) })
    KanzanInputType.PHONE_FORMATTED -> ({ Text(text = "📞", style = AppTextStyle.nunito_regular_16) })
    KanzanInputType.URI -> ({ Text(text = "🌐", style = AppTextStyle.nunito_regular_16) })
    KanzanInputType.NOMINAL -> ({ Text(text = "Rp", style = AppTextStyle.nunito_regular_16) })
    KanzanInputType.DECIMAL_NUMBER -> ({ Text(text = "$", style = AppTextStyle.nunito_regular_16) })
    KanzanInputType.SEARCH -> ({ Text(text = "🔍", style = AppTextStyle.nunito_regular_16) })
    KanzanInputType.PIN -> ({ Text(text = "🔑", style = AppTextStyle.nunito_regular_16) })
    else -> null
}

@Composable
private fun KanzanInputType.defaultTrailingIcon(): (@Composable () -> Unit)? = when (this) {
    KanzanInputType.NOMINAL -> ({ Text(text = "💰", style = AppTextStyle.nunito_regular_16) })
    KanzanInputType.CLICKABLE -> ({ Text(text = "▼", style = AppTextStyle.nunito_regular_16) })
    else -> null
}

@Composable
private fun KanzanInputType.defaultPrefix(): (@Composable () -> Unit)? = null

@Composable
private fun KanzanInputType.defaultSuffix(): (@Composable () -> Unit)? = when (this) {
    KanzanInputType.NOMINAL -> ({ Text(text = "IDR", style = AppTextStyle.nunito_regular_12, color = Color.Green) })
    KanzanInputType.DECIMAL_NUMBER -> ({ Text(text = "USD", style = AppTextStyle.nunito_regular_12, color = Color.Blue) })
    KanzanInputType.URI -> ({ Text(text = ".com", style = AppTextStyle.nunito_regular_12, color = Color.Gray) })
    else -> null
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun KanzanTextField(
    label: String,
    value: String,
    onValueChanged: (String) -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth(),
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
    kanzanColors: KanzanTextFieldColors = KanzanTextFieldColors.defaults(),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    onFocusChange: (Boolean) -> Unit = {},
    onClick: (() -> Unit)? = null,
    isError: Boolean = false,
    separator: String = if (kanzanInputType == KanzanInputType.PHONE_FORMATTED) "-" else "*",
    chunkSize: Int = 4,
    phoneChunkSizes: List<Int> = listOf(4, 4, 4)
) {
    val finalIsError = isError || errorMessage != null
    val finalKeyboardType = kanzanInputType?.keyboardType ?: KanzanInputType.TEXT.keyboardType
    val isPasswordInput = kanzanInputType == KanzanInputType.PASSWORD || kanzanInputType == KanzanInputType.PIN
    var passwordVisible by remember { mutableStateOf(false) }
    val finalVisualTransformation = when {
        isPasswordInput && !passwordVisible -> PasswordVisualTransformation()
        isPasswordInput && passwordVisible -> VisualTransformation.None
        kanzanInputType == KanzanInputType.PATTERNED -> ChunkedVisualTransformation(separator.firstOrNull() ?: '*', chunkSize)
        kanzanInputType == KanzanInputType.PHONE_FORMATTED -> VariableChunkedVisualTransformation(separator.firstOrNull() ?: '-', phoneChunkSizes)
        kanzanInputType == KanzanInputType.NOMINAL -> NominalVisualTransformation()
        else -> visualTransformation
    }
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    // Resolve final TextFieldColors: user-supplied `colors` param > kanzanColors > default
    val finalColors: TextFieldColors = colors ?: kanzanColors.toTextFieldColors()

    // Auto-resolve behavior per input type — user-supplied params still take priority via function signature defaults
    val finalEnabled = if (kanzanInputType == KanzanInputType.DISABLED) false else enabled
    val finalReadOnly = if (kanzanInputType == KanzanInputType.CLICKABLE || kanzanInputType == KanzanInputType.DISABLED) true else readOnly
    val finalSingleLine = if (kanzanInputType == KanzanInputType.MULTILINE) false else singleLine
    val finalMaxLines = if (kanzanInputType == KanzanInputType.MULTILINE && maxLines == 1) Int.MAX_VALUE else maxLines
    val finalImeAction = when (kanzanInputType) {
        KanzanInputType.SEARCH -> ImeAction.Search
        KanzanInputType.MULTILINE -> ImeAction.Default
        else -> imeAction
    }

    // Resolve defaults per input type — user-supplied values take priority
    val finalLeadingIcon = leadingIcon ?: kanzanInputType?.defaultLeadingIcon()
    val finalPrefix = prefix ?: kanzanInputType?.defaultPrefix()
    val finalSuffix = suffix ?: kanzanInputType?.defaultSuffix()

    // Build trailing icon: user-supplied > auto (password toggle + clear text) > default per type
    val showClearButton = value.isNotEmpty() && finalEnabled && !finalReadOnly
    val finalTrailingIcon: @Composable (() -> Unit)? = when {
        // User-supplied trailing icon always wins
        trailingIcon != null -> trailingIcon
        // Password/PIN: show toggle eye, optionally with clear button
        isPasswordInput -> {
            {
                Row {
                    AnimatedVisibility(
                        visible = showClearButton,
                        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(animationSpec = tween(300)),
                        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(animationSpec = tween(300))
                    ) {
                        IconButton(onClick = { onValueChanged("") }) {
                            Text(text = "✕", style = AppTextStyle.nunito_regular_16)
                        }
                    }
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        AnimatedContent(
                            targetState = passwordVisible,
                            transitionSpec = {
                                (fadeIn(animationSpec = tween(300)) + scaleIn(initialScale = 0.8f, animationSpec = tween(150)))
                                    .with(fadeOut(animationSpec = tween(300)) + scaleOut(targetScale = 0.8f, animationSpec = tween(150)))
                            },
                            label = "PasswordToggle"
                        ) { visible ->
                            Text(
                                text = if (visible) "🙈" else "🐵",
                                style = AppTextStyle.nunito_regular_16
                            )
                        }
                    }
                }
            }
        }
        // Non-password with text: show clear button
        showClearButton -> {
            {
                AnimatedVisibility(
                    visible = true,
                    enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(animationSpec = tween(300)),
                    exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut(animationSpec = tween(300))
                ) {
                    IconButton(onClick = { onValueChanged("") }) {
                        Text(text = "✕", style = AppTextStyle.nunito_regular_16)
                    }
                }
            }
        }
        // Fallback to default per input type
        else -> kanzanInputType?.defaultTrailingIcon()
    }

    // Apply click handler for CLICKABLE type
    // For CLICKABLE, we disable the TextField and wrap click on the Box instead
    // to prevent OutlinedTextField from consuming touch events
    val finalModifier = modifier
    val isClickableType = kanzanInputType == KanzanInputType.CLICKABLE && onClick != null

    LaunchedEffect(isFocused) {
        onFocusChange(isFocused)
    }

    // For CLICKABLE type: wrap in Box with clickable overlay so touch events
    // are not consumed by the OutlinedTextField's internal focus handling
    if (isClickableType) {
        Box(modifier = finalModifier) {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
                readOnly = true,
                textStyle = textStyle,
                label = {
                    Text(
                        text = label,
                        style = labelStyle
                    )
                },
                placeholder = {
                    Text(
                        text = placeholder,
                        style = placeholderStyle
                    )
                },
                leadingIcon = finalLeadingIcon,
                trailingIcon = finalTrailingIcon,
                prefix = finalPrefix,
                suffix = finalSuffix,
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
                singleLine = finalSingleLine,
                maxLines = finalMaxLines,
                minLines = minLines,
                shape = shape ?: Shapes.medium,
                colors = if (colors != null) colors else kanzanColors.toClickableTextFieldColors(),
            )
            // Invisible clickable overlay
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { onClick!!() }
            )
        }
    } else {
        OutlinedTextField(
            value = value,
            onValueChange = { newValue ->
                when (kanzanInputType) {
                    KanzanInputType.EMAIL -> onValueChanged(processEmailInput(newValue, value))
                    KanzanInputType.NOMINAL -> onValueChanged(newValue.filter { it.isDigit() })
                    KanzanInputType.PATTERNED -> onValueChanged(newValue.filter { it.isDigit() })
                    KanzanInputType.PHONE_FORMATTED -> onValueChanged(newValue.filter { it.isDigit() })
                    else -> onValueChanged(newValue)
                }
            },
            modifier = finalModifier,
            enabled = finalEnabled,
            readOnly = finalReadOnly,
            textStyle = textStyle,
            label = {
                Text(
                    text = label,
                    style = labelStyle
                )
            },
            placeholder = {
                Text(
                    text = placeholder,
                    style = placeholderStyle
                )
            },
            leadingIcon = finalLeadingIcon,
            trailingIcon = finalTrailingIcon,
            prefix = finalPrefix,
            suffix = finalSuffix,
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
                imeAction = finalImeAction
            ),
            keyboardActions = keyboardActions,
            singleLine = finalSingleLine,
            maxLines = finalMaxLines,
            minLines = minLines,
            shape = shape ?: Shapes.medium,
            colors = finalColors,
            interactionSource = interactionSource
        )
    }
}

/**
 * Convert [KanzanTextFieldColors] to Material3 [TextFieldColors].
 * Warna yang [Color.Unspecified] akan fallback ke default MaterialTheme.
 */
@Composable
private fun KanzanTextFieldColors.toTextFieldColors(): TextFieldColors {
    return OutlinedTextFieldDefaults.colors(
        // Text
        focusedTextColor = textColor.takeOrDefault(MaterialTheme.colorScheme.onSurface),
        unfocusedTextColor = textColor.takeOrDefault(MaterialTheme.colorScheme.onSurface),
        disabledTextColor = disabledTextColor.takeOrDefault(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)),
        errorTextColor = errorTextColor.takeOrDefault(MaterialTheme.colorScheme.onSurface),
        // Cursor
        cursorColor = cursorColor.takeOrDefault(MaterialTheme.colorScheme.primary),
        errorCursorColor = errorCursorColor.takeOrDefault(MaterialTheme.colorScheme.error),
        // Border
        focusedBorderColor = focusedBorderColor.takeOrDefault(MaterialTheme.colorScheme.primary),
        unfocusedBorderColor = unfocusedBorderColor.takeOrDefault(MaterialTheme.colorScheme.outline),
        disabledBorderColor = disabledBorderColor.takeOrDefault(MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)),
        errorBorderColor = errorBorderColor.takeOrDefault(MaterialTheme.colorScheme.error),
        // Label
        focusedLabelColor = focusedLabelColor.takeOrDefault(MaterialTheme.colorScheme.primary),
        unfocusedLabelColor = unfocusedLabelColor.takeOrDefault(MaterialTheme.colorScheme.onSurfaceVariant),
        disabledLabelColor = disabledLabelColor.takeOrDefault(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)),
        errorLabelColor = errorLabelColor.takeOrDefault(MaterialTheme.colorScheme.error),
        // Placeholder
        focusedPlaceholderColor = placeholderColor.takeOrDefault(MaterialTheme.colorScheme.onSurfaceVariant),
        unfocusedPlaceholderColor = placeholderColor.takeOrDefault(MaterialTheme.colorScheme.onSurfaceVariant),
        disabledPlaceholderColor = disabledPlaceholderColor.takeOrDefault(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)),
        errorPlaceholderColor = placeholderColor.takeOrDefault(MaterialTheme.colorScheme.onSurfaceVariant),
        // Leading icon
        focusedLeadingIconColor = leadingIconColor.takeOrDefault(MaterialTheme.colorScheme.onSurfaceVariant),
        unfocusedLeadingIconColor = leadingIconColor.takeOrDefault(MaterialTheme.colorScheme.onSurfaceVariant),
        disabledLeadingIconColor = disabledLeadingIconColor.takeOrDefault(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)),
        errorLeadingIconColor = errorLeadingIconColor.takeOrDefault(MaterialTheme.colorScheme.onSurfaceVariant),
        // Trailing icon
        focusedTrailingIconColor = trailingIconColor.takeOrDefault(MaterialTheme.colorScheme.onSurfaceVariant),
        unfocusedTrailingIconColor = trailingIconColor.takeOrDefault(MaterialTheme.colorScheme.onSurfaceVariant),
        disabledTrailingIconColor = disabledTrailingIconColor.takeOrDefault(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)),
        errorTrailingIconColor = errorTrailingIconColor.takeOrDefault(MaterialTheme.colorScheme.onSurfaceVariant),
        // Container
        focusedContainerColor = focusedContainerColor.takeOrDefault(Color.Transparent),
        unfocusedContainerColor = unfocusedContainerColor.takeOrDefault(Color.Transparent),
        disabledContainerColor = disabledContainerColor.takeOrDefault(Color.Transparent),
        errorContainerColor = errorContainerColor.takeOrDefault(Color.Transparent),
        // Supporting text
        focusedSupportingTextColor = supportingTextColor.takeOrDefault(MaterialTheme.colorScheme.onSurfaceVariant),
        unfocusedSupportingTextColor = supportingTextColor.takeOrDefault(MaterialTheme.colorScheme.onSurfaceVariant),
        disabledSupportingTextColor = disabledSupportingTextColor.takeOrDefault(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)),
        errorSupportingTextColor = errorSupportingTextColor.takeOrDefault(MaterialTheme.colorScheme.error),
    )
}

/**
 * Convert [KanzanTextFieldColors] to [TextFieldColors] untuk CLICKABLE type.
 * Disabled state tetap terlihat normal (tidak pudar).
 */
@Composable
private fun KanzanTextFieldColors.toClickableTextFieldColors(): TextFieldColors {
    return OutlinedTextFieldDefaults.colors(
        disabledTextColor = textColor.takeOrDefault(MaterialTheme.colorScheme.onSurface),
        disabledBorderColor = disabledBorderColor.takeOrDefault(MaterialTheme.colorScheme.outline),
        disabledLabelColor = disabledLabelColor.takeOrDefault(MaterialTheme.colorScheme.onSurfaceVariant),
        disabledPlaceholderColor = disabledPlaceholderColor.takeOrDefault(MaterialTheme.colorScheme.onSurfaceVariant),
        disabledLeadingIconColor = disabledLeadingIconColor.takeOrDefault(MaterialTheme.colorScheme.onSurfaceVariant),
        disabledTrailingIconColor = disabledTrailingIconColor.takeOrDefault(MaterialTheme.colorScheme.onSurfaceVariant),
        disabledSupportingTextColor = disabledSupportingTextColor.takeOrDefault(MaterialTheme.colorScheme.onSurfaceVariant),
        disabledContainerColor = disabledContainerColor.takeOrDefault(Color.Transparent),
    )
}

/** Helper: return this color if specified, otherwise fallback */
private fun Color.takeOrDefault(default: Color): Color =
    if (this != Color.Unspecified) this else default

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
    
    return when {
        !hasAtSymbol -> "$baseValue@" // First space becomes @
        hasAtSymbol && !baseValue.substringAfter('@').contains('.') -> "$baseValue." // Second space becomes .
        else -> baseValue // Additional spaces are removed
    }
}

/**
 * VisualTransformation for Indonesian currency format (e.g. "1.000.000").
 * State stores raw digits only; display shows formatted number with "." thousand separators.
 * Groups of 3 from the right.
 */
private class NominalVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val original = text.text
        if (original.isEmpty()) return TransformedText(text, OffsetMapping.Identity)

        // Build formatted string: insert "." every 3 digits from right
        val formatted = buildString {
            for (i in original.indices) {
                val posFromRight = original.length - 1 - i
                append(original[i])
                if (posFromRight > 0 && posFromRight % 3 == 0) append('.')
            }
        }

        // Pre-compute forward mapping: original index → formatted index
        val origToTrans = IntArray(original.length + 1)
        var fIdx = 0
        for (oIdx in original.indices) {
            origToTrans[oIdx] = fIdx
            fIdx++
            // If next position in formatted is a dot, skip it
            if (fIdx < formatted.length && formatted[fIdx] == '.') fIdx++
        }
        origToTrans[original.length] = formatted.length

        // Pre-compute reverse mapping: formatted index → original index
        val transToOrig = IntArray(formatted.length + 1)
        var oIdx = 0
        for (fi in formatted.indices) {
            if (formatted[fi] == '.') {
                transToOrig[fi] = oIdx
            } else {
                transToOrig[fi] = oIdx
                oIdx++
            }
        }
        transToOrig[formatted.length] = original.length

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return origToTrans[offset.coerceIn(0, original.length)]
            }

            override fun transformedToOriginal(offset: Int): Int {
                return transToOrig[offset.coerceIn(0, formatted.length)]
            }
        }
        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}

/**
 * VisualTransformation that inserts [separator] every [chunkSize] digits.
 * State stores raw digits only; display shows e.g. "1234*5678*9012".
 * OffsetMapping keeps cursor position accurate during insert/delete at any position.
 */
private class ChunkedVisualTransformation(
    private val separator: Char,
    private val chunkSize: Int
) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val original = text.text
        if (original.isEmpty()) return TransformedText(text, OffsetMapping.Identity)

        val sb = StringBuilder()
        for (i in original.indices) {
            if (i > 0 && i % chunkSize == 0) sb.append(separator)
            sb.append(original[i])
        }
        val transformed = sb.toString()

        // Pre-compute mapping: original index → transformed index
        val origToTrans = IntArray(original.length + 1)
        var tIdx = 0
        for (oIdx in original.indices) {
            if (oIdx > 0 && oIdx % chunkSize == 0) tIdx++ // skip separator
            origToTrans[oIdx] = tIdx
            tIdx++
        }
        origToTrans[original.length] = tIdx // end position

        // Pre-compute reverse mapping: transformed index → original index
        val transToOrig = IntArray(transformed.length + 1)
        var oIdx = 0
        for (ti in transformed.indices) {
            if (transformed[ti] == separator) {
                transToOrig[ti] = oIdx
            } else {
                transToOrig[ti] = oIdx
                oIdx++
            }
        }
        transToOrig[transformed.length] = original.length

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return origToTrans[offset.coerceIn(0, original.length)]
            }

            override fun transformedToOriginal(offset: Int): Int {
                return transToOrig[offset.coerceIn(0, transformed.length)]
            }
        }
        return TransformedText(AnnotatedString(transformed), offsetMapping)
    }
}

/**
 * VisualTransformation that inserts [separator] based on variable [chunkSizes].
 * State stores raw digits only; display shows e.g. "0812-3456-7890".
 * Supports arbitrary chunk patterns like [4,4,4] or [3,3,4].
 */
private class VariableChunkedVisualTransformation(
    private val separator: Char,
    private val chunkSizes: List<Int>
) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val original = text.text
        if (original.isEmpty()) return TransformedText(text, OffsetMapping.Identity)

        // Build transformed string
        val sb = StringBuilder()
        var chunkIdx = 0
        var posInChunk = 0
        for (ch in original) {
            val currentChunkSize = if (chunkIdx < chunkSizes.size) chunkSizes[chunkIdx] else chunkSizes.lastOrNull() ?: 4
            if (posInChunk >= currentChunkSize) {
                sb.append(separator)
                chunkIdx++
                posInChunk = 0
            }
            sb.append(ch)
            posInChunk++
        }
        val transformed = sb.toString()

        // Pre-compute forward mapping: original index → transformed index
        val origToTrans = IntArray(original.length + 1)
        var tIdx = 0
        var cIdx = 0
        var pInC = 0
        for (oIdx in original.indices) {
            val cSize = if (cIdx < chunkSizes.size) chunkSizes[cIdx] else chunkSizes.lastOrNull() ?: 4
            if (pInC >= cSize) {
                tIdx++ // skip separator
                cIdx++
                pInC = 0
            }
            origToTrans[oIdx] = tIdx
            tIdx++
            pInC++
        }
        origToTrans[original.length] = tIdx // end position

        // Pre-compute reverse mapping: transformed index → original index
        val transToOrig = IntArray(transformed.length + 1)
        var oIdx = 0
        for (ti in transformed.indices) {
            if (transformed[ti] == separator) {
                transToOrig[ti] = oIdx
            } else {
                transToOrig[ti] = oIdx
                oIdx++
            }
        }
        transToOrig[transformed.length] = original.length

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                return origToTrans[offset.coerceIn(0, original.length)]
            }

            override fun transformedToOriginal(offset: Int): Int {
                return transToOrig[offset.coerceIn(0, transformed.length)]
            }
        }
        return TransformedText(AnnotatedString(transformed), offsetMapping)
    }
}

// region ==================== Preview: 1. Basic Text Input ====================

@Preview(showBackground = true, name = "1a. Basic Text")
@Composable
private fun KanzanTextFieldPreview() {
    var textValue by remember { mutableStateOf("qwerty") }
    KanzanTextField(
        label = "TextField",
        value = textValue,
        onValueChanged = { textValue = it },
    )
}

@Preview(showBackground = true, name = "1b. Text Input Type")
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

@Preview(showBackground = true, name = "1c. Multiline")
@Composable
private fun KanzanMultilineInputPreview() {
    var textValue by remember { mutableStateOf("Baris pertama\nBaris kedua\nBaris ketiga") }
    KanzanTextField(
        label = "Catatan",
        value = textValue,
        onValueChanged = { textValue = it },
        placeholder = "Tulis catatan di sini...",
        kanzanInputType = KanzanInputType.MULTILINE,
        supportingText = "Otomatis multiline, maxLines unlimited"
    )
}

@Preview(showBackground = true, name = "1d. Multiline (maxLines=4)")
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

// endregion

// region ==================== Preview: 2. Credential Input (Email, Password, PIN) ====================

@Preview(showBackground = true, name = "2a. Email")
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

@Preview(showBackground = true, name = "2b. Email Demo (spasi → @ dan .)")
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

@Preview(showBackground = true, name = "2c. Password")
@Composable
private fun KanzanTextFieldPasswordPreview() {
    var textValue by remember { mutableStateOf("password123") }
    KanzanTextField(
        label = "Password",
        value = textValue,
        onValueChanged = { textValue = it },
        placeholder = "Masukkan password",
        kanzanInputType = KanzanInputType.PASSWORD
    )
}

@Preview(showBackground = true, name = "2d. PIN")
@Composable
private fun KanzanPinInputPreview() {
    var pinValue by remember { mutableStateOf("1234") }
    KanzanTextField(
        label = "PIN",
        value = pinValue,
        onValueChanged = { pinValue = it },
        placeholder = "Masukkan 4-6 digit PIN",
        kanzanInputType = KanzanInputType.PIN,
        supportingText = "Input tersembunyi, keyboard numerik"
    )
}

// endregion

// region ==================== Preview: 3. Numeric & Currency Input ====================

@Preview(showBackground = true, name = "3a. Number")
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

@Preview(showBackground = true, name = "3b. Nominal (Rp)")
@Composable
private fun KanzanNominalInputPreview() {
    var nominalValue by remember { mutableStateOf("1000000") }
    KanzanTextField(
        label = "Nominal",
        value = nominalValue,
        onValueChanged = { nominalValue = it },
        placeholder = "0",
        kanzanInputType = KanzanInputType.NOMINAL,
        leadingIcon = { Text(text = "Rp", style = AppTextStyle.nunito_regular_16) },
        suffix = { Text(text = "IDR", style = AppTextStyle.nunito_regular_12, color = Color.Green) },
        supportingText = "Input akan otomatis diformat: 1.000.000"
    )
}

@Preview(showBackground = true, name = "3c. Decimal ($)")
@Composable
private fun KanzanDecimalInputPreview() {
    var decimalValue by remember { mutableStateOf("99.99") }
    KanzanTextField(
        label = "Harga",
        value = decimalValue,
        onValueChanged = { decimalValue = it },
        placeholder = "0.00",
        kanzanInputType = KanzanInputType.DECIMAL_NUMBER,
        leadingIcon = { Text(text = "$", style = AppTextStyle.nunito_regular_16) },
        suffix = { Text(text = "USD", style = AppTextStyle.nunito_regular_12, color = Color.Blue) }
    )
}

@Preview(showBackground = true, name = "3d. Amount (custom icon)")
@Composable
private fun KanzanTextFieldAmountPreview() {
    var amount by remember { mutableStateOf("1.000.000") }
    KanzanTextField(
        label = "Jumlah",
        value = amount,
        onValueChanged = { amount = it },
        placeholder = "0",
        kanzanInputType = KanzanInputType.NUMBER,
        leadingIcon = { Text(text = "Rp", style = AppTextStyle.nunito_regular_16) },
        trailingIcon = { Text(text = "💰", style = AppTextStyle.nunito_regular_16) }
    )
}

@Preview(showBackground = true, name = "3e. Percentage")
@Composable
private fun KanzanTextFieldPercentagePreview() {
    var percentage by remember { mutableStateOf("85") }
    KanzanTextField(
        label = "Diskon",
        value = percentage,
        onValueChanged = { percentage = it },
        placeholder = "0",
        kanzanInputType = KanzanInputType.NUMBER,
        suffix = { Text(text = "%", style = AppTextStyle.nunito_regular_14, color = Color.Blue) },
        trailingIcon = { Text(text = "🏷️", style = AppTextStyle.nunito_regular_16) }
    )
}

@Preview(showBackground = true, name = "3f. Weight (suffix)")
@Composable
private fun KanzanTextFieldWithSuffixPreview() {
    var weight by remember { mutableStateOf("75.5") }
    KanzanTextField(
        label = "Berat Badan",
        value = weight,
        onValueChanged = { weight = it },
        placeholder = "0.0",
        kanzanInputType = KanzanInputType.NUMBER,
        suffix = { Text(text = "kg", style = AppTextStyle.nunito_regular_14) },
        supportingText = "Masukkan berat badan dalam kilogram"
    )
}

// endregion

// region ==================== Preview: 4. Formatted Input (Patterned, Phone) ====================

@Preview(showBackground = true, name = "4a. Patterned (serial code)")
@Composable
private fun KanzanPatternedInputPreview() {
    var patternedValue by remember { mutableStateOf("1234123412341234") }
    KanzanTextField(
        label = "Kode Serial",
        value = patternedValue,
        onValueChanged = { patternedValue = it },
        placeholder = "Masukkan kode serial",
        kanzanInputType = KanzanInputType.PATTERNED,
        supportingText = "Display: 1234*1234*1234*1234 (separator: *, chunk: 4)"
    )
}

@Preview(showBackground = true, name = "4b. Phone Formatted")
@Composable
private fun KanzanPhoneFormattedInputPreview() {
    var phoneValue by remember { mutableStateOf("") }
    KanzanTextField(
        label = "Nomor Telepon",
        value = phoneValue,
        onValueChanged = { phoneValue = it },
        //placeholder = "0812-3456-7890",
        kanzanInputType = KanzanInputType.PHONE_FORMATTED,
        leadingIcon = { Text(text = "📞", style = AppTextStyle.nunito_regular_16) },
        supportingText = null
    )
}

@Preview(showBackground = true, name = "4c. URI")
@Composable
private fun KanzanTextFieldUrlPreview() {
    var url by remember { mutableStateOf("https://example") }
    KanzanTextField(
        label = "Website URL",
        value = url,
        onValueChanged = { url = it },
        placeholder = "https://",
        kanzanInputType = KanzanInputType.URI,
        leadingIcon = { Text(text = "🌐", style = AppTextStyle.nunito_regular_16) },
        suffix = { Text(text = ".com", style = AppTextStyle.nunito_regular_12, color = Color.Gray) },
        supportingText = "Masukkan alamat website lengkap"
    )
}

// endregion

// region ==================== Preview: 5. Search ====================

@Preview(showBackground = true, name = "5a. Search Input Type")
@Composable
private fun KanzanSearchInputPreview() {
    var searchValue by remember { mutableStateOf("hutang") }
    KanzanTextField(
        label = "Cari",
        value = searchValue,
        onValueChanged = { searchValue = it },
        placeholder = "Cari transaksi...",
        kanzanInputType = KanzanInputType.SEARCH,
        supportingText = "IME action otomatis Search"
    )
}

@Preview(showBackground = true, name = "5b. Search (custom trailing)")
@Composable
private fun KanzanTextFieldSearchWithClearPreview() {
    var searchQuery by remember { mutableStateOf("search text") }
    KanzanTextField(
        label = "Pencarian",
        value = searchQuery,
        onValueChanged = { searchQuery = it },
        placeholder = "Cari produk...",
        leadingIcon = { Text(text = "🔍", style = AppTextStyle.nunito_regular_16) },
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

// endregion

// region ==================== Preview: 6. State Variants (Disabled, Clickable, Error, Helper) ====================

@Preview(showBackground = true, name = "6a. Disabled")
@Composable
private fun KanzanDisabledInputPreview() {
    KanzanTextField(
        label = "Username",
        value = "john_doe",
        onValueChanged = { },
        kanzanInputType = KanzanInputType.DISABLED,
        supportingText = "Otomatis disabled & readOnly"
    )
}

@Preview(showBackground = true, name = "6b. Disabled (manual)")
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

@Preview(showBackground = true, name = "6c. Clickable")
@Composable
private fun KanzanClickableInputPreview() {
    var selectedValue by remember { mutableStateOf("Pilih tanggal...") }
    KanzanTextField(
        label = "Tanggal",
        value = selectedValue,
        onValueChanged = { selectedValue = it },
        placeholder = "Tap untuk memilih",
        kanzanInputType = KanzanInputType.CLICKABLE,
        onClick = { /* showDatePicker = true */ },
        supportingText = "Otomatis readOnly + trailing ▼ + onClick"
    )
}

@Preview(showBackground = true, name = "6c2. Clickable Disabled (with value)")
@Composable
private fun KanzanClickableDisabledWithValuePreview() {
    KanzanTextField(
        label = "Dompet / Akun",
        value = "💰 Dompet Tunai",
        onValueChanged = { },
        placeholder = "Pilih dompet",
        kanzanInputType = KanzanInputType.CLICKABLE,
        enabled = false,
        onClick = null,
        supportingText = "Clickable + disabled (ada value, tidak bisa klik)"
    )
}

@Preview(showBackground = true, name = "6c3. Clickable Disabled (empty)")
@Composable
private fun KanzanClickableDisabledEmptyPreview() {
    KanzanTextField(
        label = "Kategori",
        value = "",
        onValueChanged = { },
        placeholder = "Pilih kategori",
        kanzanInputType = KanzanInputType.CLICKABLE,
        enabled = false,
        onClick = null,
        supportingText = "Clickable + disabled (kosong, tidak bisa klik)"
    )
}

@Preview(showBackground = true, name = "6c4. Clickable Disabled (with error)")
@Composable
private fun KanzanClickableDisabledWithErrorPreview() {
    KanzanTextField(
        label = "Tanggal",
        value = "",
        onValueChanged = { },
        placeholder = "Pilih tanggal",
        kanzanInputType = KanzanInputType.CLICKABLE,
        enabled = false,
        onClick = null,
        isError = true,
        errorMessage = "Tanggal wajib diisi"
    )
}

@Preview(showBackground = true, name = "6d. Dropdown (Clickable)")
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
            kanzanInputType = KanzanInputType.CLICKABLE,
            onClick = { isDropdownExpanded = true }
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

@Preview(showBackground = true, name = "6e. Date Picker (Clickable)")
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
            kanzanInputType = KanzanInputType.CLICKABLE,
            onClick = { showDatePicker = true },
            trailingIcon = { Text(text = "📅", style = AppTextStyle.nunito_regular_16) }
        )
        if (showDatePicker) {
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

@Preview(showBackground = true, name = "6f. Error")
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

@Preview(showBackground = true, name = "6g. Helper Text")
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

@Preview(showBackground = true, name = "6h. Helper Text + Error")
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

@Preview(showBackground = true, name = "6i. Leading Icon")
@Composable
private fun KanzanTextFieldWithIconPreview() {
    var textValue by remember { mutableStateOf("search query") }
    KanzanTextField(
        label = "Pencarian",
        value = textValue,
        onValueChanged = { textValue = it },
        placeholder = "Cari sesuatu...",
        leadingIcon = { Text("🔍") }
    )
}

// endregion

// region ==================== Preview: 7. Custom Colors (KanzanTextFieldColors) ====================

@Preview(showBackground = true, name = "7a. Custom Colors (Blue theme)")
@Composable
private fun KanzanTextFieldCustomColorsPreview() {
    var textValue by remember { mutableStateOf("Hello World") }
    KanzanTextField(
        label = "Custom Blue",
        value = textValue,
        onValueChanged = { textValue = it },
        placeholder = "Ketik sesuatu...",
        kanzanColors = KanzanTextFieldColors(
            focusedBorderColor = Color.Blue,
            unfocusedBorderColor = Color.Blue.copy(alpha = 0.5f),
            focusedLabelColor = Color.Blue,
            unfocusedLabelColor = Color.Blue.copy(alpha = 0.7f),
            cursorColor = Color.Blue,
        ),
        supportingText = "Border & label biru custom"
    )
}

@Preview(showBackground = true, name = "7b. Preset Success")
@Composable
private fun KanzanTextFieldSuccessPreview() {
    var textValue by remember { mutableStateOf("Valid input") }
    KanzanTextField(
        label = "Username",
        value = textValue,
        onValueChanged = { textValue = it },
        kanzanColors = KanzanTextFieldColors.success(),
        supportingText = "Username tersedia ✓"
    )
}

@Preview(showBackground = true, name = "7c. Preset Error")
@Composable
private fun KanzanTextFieldErrorColorsPreview() {
    var textValue by remember { mutableStateOf("bad") }
    KanzanTextField(
        label = "Password",
        value = textValue,
        onValueChanged = { textValue = it },
        kanzanColors = KanzanTextFieldColors.error(),
        errorMessage = "Password terlalu pendek",
        isError = true
    )
}

@Preview(showBackground = true, name = "7d. Preset Dark")
@Composable
private fun KanzanTextFieldDarkPreview() {
    var textValue by remember { mutableStateOf("Dark mode text") }
    KanzanTextField(
        label = "Dark Theme",
        value = textValue,
        onValueChanged = { textValue = it },
        kanzanColors = KanzanTextFieldColors.dark(),
        supportingText = "Manual dark mode colors"
    )
}

// endregion
