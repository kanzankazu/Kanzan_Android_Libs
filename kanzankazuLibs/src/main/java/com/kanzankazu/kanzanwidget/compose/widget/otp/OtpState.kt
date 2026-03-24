package com.kanzankazu.kanzanwidget.compose.widget.otp

/**
 * State untuk OTP input.
 * @param code list digit OTP, null berarti belum diisi. Panjang list = jumlah digit.
 * @param focusedIndex index field yang sedang fokus.
 * @param isValid null = belum lengkap, true/false = hasil validasi dari caller.
 */
data class OtpState(
    val code: List<Int?> = emptyList(),
    val focusedIndex: Int? = null,
    val isValid: Boolean? = null
) {
    companion object {
        fun create(digitCount: Int = 4): OtpState =
            OtpState(code = List(digitCount) { null })
    }

    val isFilled: Boolean get() = code.none { it == null }
    val otpText: String get() = code.joinToString("")
}
