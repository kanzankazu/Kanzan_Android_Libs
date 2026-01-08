package com.kanzankazu.kanzanutil.kanzanextension

import android.annotation.SuppressLint
import com.kanzankazu.kanzanutil.BaseConst.locale
import java.text.NumberFormat
import java.util.Locale

/**
 * Converts a `Number` object into a formatted string for Indonesian Rupiah currency.
 *
 * @param useCurrencySymbol Determines whether to include the "Rp" currency symbol in the output.
 *                          Defaults to `true`. If `true`, the output will include "Rp" as a prefix.
 * @param short Determines whether to use the short format (e.g., "Triliun", "M", "Jt", "Rb") or not.
 *              Defaults to `false`. If `true`, the output will use the short format.
 * @param shortLong Determines whether to use the long format for the short format (e.g., "Triliun", "Miliar", "Juta", "Ribu") or not.
 *              Defaults to `false`. If `true`, the output will use the long format for the short format.
 * @param showUnit Determines whether to display the unit (e.g., "T", "M", "Jt", "Rb") or not.
 *              Defaults to `true`. If `true`, the output will include the unit.
 * @param locale The locale to use for formatting. Defaults to `Locale("id", "ID")`.
 * @return A formatted string representing the Indonesian Rupiah currency value.
 */
@SuppressLint("DefaultLocale")
@JvmOverloads
fun Number?.toRupiahFormat(
    useCurrencySymbol: Boolean = true,    // Menentukan apakah menampilkan simbol Rp
    short: Boolean = false,               // Menggunakan format pendek (Jt, Rb, dll)
    shortLong: Boolean = false,           // Menggunakan nama lengkap untuk format pendek
    showUnit: Boolean = true,             // Menampilkan unit (T, M, Jt, dll)
    locale: Locale = Locale("id", "ID")   // Locale untuk formatting (Indonesia)
): String {
    // ==================== NULL HANDLING ====================
    if (this == null) {
        return if (useCurrencySymbol) "Rp -" else ""  // Return Rp - untuk null jika simbol aktif
    }

    // ==================== SETUP FORMATTER ====================
    val formatRupiah = NumberFormat.getCurrencyInstance(locale)  // Currency formatter dengan locale Indonesia

    // ==================== MAIN FORMATTING LOGIC ====================
    val formattedValue = if (short) {
        // ==================== SHORT FORMAT (UNTUK NILAI BESAR) ====================
        val value = this.toDouble()  // Konversi ke Double untuk perhitungan

        when {
            // ==================== TRILIUN FORMAT ====================
            value >= 1_000_000_000_000 -> String.format(
                "%s%.2f %s",                      // Format: Rp/Triliun
                if (useCurrencySymbol) "Rp" else "",  // Tambah Rp jika diperlukan
                value / 1_000_000_000_000,       // Bagi dengan 1 triliun
                if (showUnit) {
                    if (shortLong) "Triliun" else "T"  // Unit display
                } else ""
            ).replace(",00", "")  // Hapus desimal .00 jika tidak diperlukan

            // ==================== MILIAR FORMAT ====================
            value >= 1_000_000_000 -> String.format(
                "%s%.2f %s",
                if (useCurrencySymbol) "Rp" else "",
                value / 1_000_000_000,           // Bagi dengan 1 miliar
                if (showUnit) {
                    if (shortLong) "Miliar" else "M"
                } else ""
            ).replace(",00", "")

            // ==================== JUTA FORMAT ====================
            value >= 1_000_000 -> String.format(
                "%s%.2f %s",
                if (useCurrencySymbol) "Rp" else "",
                value / 1_000_000,               // Bagi dengan 1 juta
                if (showUnit) {
                    if (shortLong) "Juta" else "Jt"
                } else ""
            ).replace(",00", "")

            // ==================== RIBU FORMAT ====================
            value >= 1_000 -> String.format(
                "%s%.2f %s",
                if (useCurrencySymbol) "Rp" else "",
                value / 1_000,                   // Bagi dengan 1 ribu
                if (showUnit) {
                    if (shortLong) "Ribu" else "Rb"
                } else ""
            ).replace(",00", "")

            // ==================== NORMAL FORMAT (UNTUK NILAI KECIL) ====================
            else -> {
                // Gunakan NumberFormat untuk nilai kecil, tapi perbaiki double prefix issue
                val result = formatRupiah.format(this)
                    .replace(",00", "")              // Hapus desimal .00
                    .replace("Rp", "")               // HAPUS prefix Rp yang sudah ada (FIX)
                val cleanResult = result.trim()      // Bersihkan spasi
                if (useCurrencySymbol) "Rp $cleanResult" else cleanResult  // Tambah Rp jika diperlukan
            }
        }
    } else {
        // ==================== NORMAL FORMAT (STANDARD) ====================
        // Gunakan NumberFormat untuk formatting standar, tapi perbaiki double prefix issue
        val result = formatRupiah.format(this)
            .replace(",00", "")                  // Hapus desimal .00
            .replace("Rp", "")                   // HAPUS prefix Rp yang sudah ada (FIX)
        val cleanResult = result.trim()          // Bersihkan spasi
        if (useCurrencySymbol) "Rp $cleanResult" else cleanResult  // Tambah Rp jika diperlukan
    }

    return formattedValue  // Kembalikan hasil formatting
}