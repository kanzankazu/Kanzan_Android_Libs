package com.kanzankazu.kanzanwidget.compose.widget.otp

sealed interface OtpAction {
    data class OnEnterNumber(val number: Int?, val index: Int) : OtpAction
    data class OnChangeFieldFocused(val index: Int) : OtpAction
    object OnKeyboardBack : OtpAction
}