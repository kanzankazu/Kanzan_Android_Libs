package com.kanzankazu.kanzanwidget.compose.ui

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.kanzankazu.R

/**
 * Kumpulan font family yang dapat digunakan di seluruh aplikasi Compose.
 *
 * Cara penggunaan:
 * ```kotlin
 * @Composable
 * fun MyComponent() {
 *     Text(
 *         text = "Hello",
 *         fontFamily = Nunito,
 *         fontWeight = FontWeight.Bold
 *     )
 *
 *     Text(
 *         text = "Custom Style",
 *         style = TextStyle(
 *             fontFamily = AvenirNext,
 *             fontWeight = FontWeight.Bold
 *         )
 *     )
 * }
 * ```
 */

// Font family Nunito - untuk teks yang lebih friendly dan modern
val Nunito = FontFamily(
    Font(R.font.nunito_light, FontWeight.Light),
    Font(R.font.nunito_regular, FontWeight.Normal),
    Font(R.font.nunito_semi_bold, FontWeight.Medium),
    Font(R.font.nunito_semi_bold, FontWeight.SemiBold),
    Font(R.font.nunito_bold, FontWeight.Bold),
    Font(R.font.nunito_extra_bold, FontWeight.ExtraBold),
    Font(R.font.nunito_black, FontWeight.Black)
)

// Font family Avenir Next - untuk teks yang elegant dan professional
val AvenirNext = FontFamily(
    Font(R.font.avenir_next_ltpro_regular, FontWeight.Normal),
    Font(R.font.avenir_next_ltpro_medium, FontWeight.Medium),
    Font(R.font.avenir_next_ltpro_demi, FontWeight.SemiBold),
    Font(R.font.avenir_next_ltpro_bold, FontWeight.Bold)
)

// Font family Avenir Next Italic - untuk italic text
val AvenirNextItalic = FontFamily(
    Font(R.font.avenir_next_ltpro_it, FontWeight.Normal),
    Font(R.font.avenir_next_ltpro_demiit, FontWeight.SemiBold)
)

// Font family Orator - untuk monospace style
val Orator = FontFamily(
    Font(R.font.orator_std, FontWeight.Normal)
)