package com.kanzankazu.kanzanwidget.compose.ui

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

/**
 * Extension function untuk mengkonversi Dp ke TextUnit (sp)
 */
fun androidx.compose.ui.unit.Dp.toSp(): TextUnit = this.value.sp

/**
 * Kumpulan text style yang telah didefinisikan untuk penggunaan umum.
 *
 * Cara penggunaan:
 * ```kotlin
 * @Composable
 * fun MyComponent() {
 *     // Pakai preset yang sudah ada
 *     Text(
 *         text = "Hello",
 *         style = AppTextStyle.nunito_regular_16
 *     )
 *
 *     // Atau pakai factory function untuk custom size
 *     Text(
 *         text = "Custom",
 *         style = AppTextStyle.nunito(FontWeight.Bold, 22.sp)
 *     )
 * }
 * ```
 */
object AppTextStyle {

    // region Factory Functions

    /** Factory function untuk membuat TextStyle dengan font Nunito */
    fun nunito(weight: FontWeight, size: TextUnit) = TextStyle(fontFamily = Nunito, fontWeight = weight, fontSize = size)

    /** Factory function untuk membuat TextStyle dengan font Avenir Next */
    fun avenirNext(weight: FontWeight, size: TextUnit) = TextStyle(fontFamily = AvenirNext, fontWeight = weight, fontSize = size)

    /** Factory function untuk membuat TextStyle dengan font Avenir Next Italic */
    fun avenirNextItalic(size: TextUnit) = TextStyle(fontFamily = AvenirNextItalic, fontWeight = FontWeight.Normal, fontSize = size)

    /** Factory function untuk membuat TextStyle dengan font Orator */
    fun orator(size: TextUnit) = TextStyle(fontFamily = Orator, fontWeight = FontWeight.Normal, fontSize = size)

    /** Generic factory function */
    fun custom(family: FontFamily, weight: FontWeight, size: TextUnit) = TextStyle(fontFamily = family, fontWeight = weight, fontSize = size)

    // endregion

    // region Nunito - Light
    val nunito_light_12 = nunito(FontWeight.Light, dp12.toSp())
    val nunito_light_14 = nunito(FontWeight.Light, dp14.toSp())
    val nunito_light_16 = nunito(FontWeight.Light, dp16.toSp())
    val nunito_light_18 = nunito(FontWeight.Light, dp18.toSp())
    val nunito_light_20 = nunito(FontWeight.Light, dp20.toSp())
    val nunito_light_24 = nunito(FontWeight.Light, dp24.toSp())
    val nunito_light_28 = nunito(FontWeight.Light, dp28.toSp())
    val nunito_light_32 = nunito(FontWeight.Light, dp32.toSp())
    val nunito_light_36 = nunito(FontWeight.Light, dp36.toSp())
    val nunito_light_40 = nunito(FontWeight.Light, dp40.toSp())
    val nunito_light_48 = nunito(FontWeight.Light, dp48.toSp())
    val nunito_light_56 = nunito(FontWeight.Light, dp56.toSp())
    val nunito_light_64 = nunito(FontWeight.Light, dp64.toSp())
    val nunito_light_72 = nunito(FontWeight.Light, dp72.toSp())
    val nunito_light_80 = nunito(FontWeight.Light, dp80.toSp())
    val nunito_light_96 = nunito(FontWeight.Light, dp96.toSp())
    val nunito_light_100 = nunito(FontWeight.Light, dp100.toSp())
    // endregion

    // region Nunito - Regular
    val nunito_regular_10 = nunito(FontWeight.Normal, dp10.toSp())
    val nunito_regular_12 = nunito(FontWeight.Normal, dp12.toSp())
    val nunito_regular_14 = nunito(FontWeight.Normal, dp14.toSp())
    val nunito_regular_16 = nunito(FontWeight.Normal, dp16.toSp())
    val nunito_regular_18 = nunito(FontWeight.Normal, dp18.toSp())
    val nunito_regular_20 = nunito(FontWeight.Normal, dp20.toSp())
    val nunito_regular_24 = nunito(FontWeight.Normal, dp24.toSp())
    val nunito_regular_28 = nunito(FontWeight.Normal, dp28.toSp())
    val nunito_regular_32 = nunito(FontWeight.Normal, dp32.toSp())
    val nunito_regular_36 = nunito(FontWeight.Normal, dp36.toSp())
    val nunito_regular_40 = nunito(FontWeight.Normal, dp40.toSp())
    val nunito_regular_48 = nunito(FontWeight.Normal, dp48.toSp())
    val nunito_regular_56 = nunito(FontWeight.Normal, dp56.toSp())
    val nunito_regular_64 = nunito(FontWeight.Normal, dp64.toSp())
    val nunito_regular_72 = nunito(FontWeight.Normal, dp72.toSp())
    val nunito_regular_80 = nunito(FontWeight.Normal, dp80.toSp())
    val nunito_regular_96 = nunito(FontWeight.Normal, dp96.toSp())
    val nunito_regular_100 = nunito(FontWeight.Normal, dp100.toSp())
    // endregion

    // region Nunito - Medium
    val nunito_medium_12 = nunito(FontWeight.Medium, dp12.toSp())
    val nunito_medium_14 = nunito(FontWeight.Medium, dp14.toSp())
    val nunito_medium_16 = nunito(FontWeight.Medium, dp16.toSp())
    val nunito_medium_18 = nunito(FontWeight.Medium, dp18.toSp())
    val nunito_medium_20 = nunito(FontWeight.Medium, dp20.toSp())
    val nunito_medium_24 = nunito(FontWeight.Medium, dp24.toSp())
    val nunito_medium_28 = nunito(FontWeight.Medium, dp28.toSp())
    val nunito_medium_32 = nunito(FontWeight.Medium, dp32.toSp())
    val nunito_medium_36 = nunito(FontWeight.Medium, dp36.toSp())
    val nunito_medium_40 = nunito(FontWeight.Medium, dp40.toSp())
    val nunito_medium_48 = nunito(FontWeight.Medium, dp48.toSp())
    val nunito_medium_56 = nunito(FontWeight.Medium, dp56.toSp())
    val nunito_medium_64 = nunito(FontWeight.Medium, dp64.toSp())
    val nunito_medium_72 = nunito(FontWeight.Medium, dp72.toSp())
    val nunito_medium_80 = nunito(FontWeight.Medium, dp80.toSp())
    val nunito_medium_96 = nunito(FontWeight.Medium, dp96.toSp())
    val nunito_medium_100 = nunito(FontWeight.Medium, dp100.toSp())
    // endregion

    // region Nunito - SemiBold
    val nunito_semi_bold_12 = nunito(FontWeight.SemiBold, dp12.toSp())
    val nunito_semi_bold_14 = nunito(FontWeight.SemiBold, dp14.toSp())
    val nunito_semi_bold_16 = nunito(FontWeight.SemiBold, dp16.toSp())
    val nunito_semi_bold_18 = nunito(FontWeight.SemiBold, dp18.toSp())
    val nunito_semi_bold_20 = nunito(FontWeight.SemiBold, dp20.toSp())
    val nunito_semi_bold_24 = nunito(FontWeight.SemiBold, dp24.toSp())
    val nunito_semi_bold_28 = nunito(FontWeight.SemiBold, dp28.toSp())
    val nunito_semi_bold_32 = nunito(FontWeight.SemiBold, dp32.toSp())
    val nunito_semi_bold_36 = nunito(FontWeight.SemiBold, dp36.toSp())
    val nunito_semi_bold_40 = nunito(FontWeight.SemiBold, dp40.toSp())
    val nunito_semi_bold_48 = nunito(FontWeight.SemiBold, dp48.toSp())
    val nunito_semi_bold_56 = nunito(FontWeight.SemiBold, dp56.toSp())
    val nunito_semi_bold_64 = nunito(FontWeight.SemiBold, dp64.toSp())
    val nunito_semi_bold_72 = nunito(FontWeight.SemiBold, dp72.toSp())
    val nunito_semi_bold_80 = nunito(FontWeight.SemiBold, dp80.toSp())
    val nunito_semi_bold_96 = nunito(FontWeight.SemiBold, dp96.toSp())
    val nunito_semi_bold_100 = nunito(FontWeight.SemiBold, dp100.toSp())
    // endregion

    // region Nunito - Bold
    val nunito_bold_12 = nunito(FontWeight.Bold, dp12.toSp())
    val nunito_bold_14 = nunito(FontWeight.Bold, dp14.toSp())
    val nunito_bold_16 = nunito(FontWeight.Bold, dp16.toSp())
    val nunito_bold_18 = nunito(FontWeight.Bold, dp18.toSp())
    val nunito_bold_20 = nunito(FontWeight.Bold, dp20.toSp())
    val nunito_bold_24 = nunito(FontWeight.Bold, dp24.toSp())
    val nunito_bold_28 = nunito(FontWeight.Bold, dp28.toSp())
    val nunito_bold_32 = nunito(FontWeight.Bold, dp32.toSp())
    val nunito_bold_36 = nunito(FontWeight.Bold, dp36.toSp())
    val nunito_bold_40 = nunito(FontWeight.Bold, dp40.toSp())
    val nunito_bold_48 = nunito(FontWeight.Bold, dp48.toSp())
    val nunito_bold_56 = nunito(FontWeight.Bold, dp56.toSp())
    val nunito_bold_64 = nunito(FontWeight.Bold, dp64.toSp())
    val nunito_bold_72 = nunito(FontWeight.Bold, dp72.toSp())
    val nunito_bold_80 = nunito(FontWeight.Bold, dp80.toSp())
    val nunito_bold_96 = nunito(FontWeight.Bold, dp96.toSp())
    val nunito_bold_100 = nunito(FontWeight.Bold, dp100.toSp())
    // endregion

    // region Nunito - ExtraBold
    val nunito_extra_bold_12 = nunito(FontWeight.ExtraBold, dp12.toSp())
    val nunito_extra_bold_14 = nunito(FontWeight.ExtraBold, dp14.toSp())
    val nunito_extra_bold_16 = nunito(FontWeight.ExtraBold, dp16.toSp())
    val nunito_extra_bold_18 = nunito(FontWeight.ExtraBold, dp18.toSp())
    val nunito_extra_bold_20 = nunito(FontWeight.ExtraBold, dp20.toSp())
    val nunito_extra_bold_24 = nunito(FontWeight.ExtraBold, dp24.toSp())
    val nunito_extra_bold_28 = nunito(FontWeight.ExtraBold, dp28.toSp())
    val nunito_extra_bold_32 = nunito(FontWeight.ExtraBold, dp32.toSp())
    val nunito_extra_bold_36 = nunito(FontWeight.ExtraBold, dp36.toSp())
    val nunito_extra_bold_40 = nunito(FontWeight.ExtraBold, dp40.toSp())
    val nunito_extra_bold_48 = nunito(FontWeight.ExtraBold, dp48.toSp())
    val nunito_extra_bold_56 = nunito(FontWeight.ExtraBold, dp56.toSp())
    val nunito_extra_bold_64 = nunito(FontWeight.ExtraBold, dp64.toSp())
    val nunito_extra_bold_72 = nunito(FontWeight.ExtraBold, dp72.toSp())
    val nunito_extra_bold_80 = nunito(FontWeight.ExtraBold, dp80.toSp())
    val nunito_extra_bold_96 = nunito(FontWeight.ExtraBold, dp96.toSp())
    val nunito_extra_bold_100 = nunito(FontWeight.ExtraBold, dp100.toSp())
    // endregion

    // region Avenir Next - Light
    val avenir_next_light_12 = avenirNext(FontWeight.Light, dp12.toSp())
    val avenir_next_light_14 = avenirNext(FontWeight.Light, dp14.toSp())
    val avenir_next_light_16 = avenirNext(FontWeight.Light, dp16.toSp())
    val avenir_next_light_18 = avenirNext(FontWeight.Light, dp18.toSp())
    val avenir_next_light_20 = avenirNext(FontWeight.Light, dp20.toSp())
    val avenir_next_light_24 = avenirNext(FontWeight.Light, dp24.toSp())
    val avenir_next_light_28 = avenirNext(FontWeight.Light, dp28.toSp())
    val avenir_next_light_32 = avenirNext(FontWeight.Light, dp32.toSp())
    val avenir_next_light_36 = avenirNext(FontWeight.Light, dp36.toSp())
    val avenir_next_light_40 = avenirNext(FontWeight.Light, dp40.toSp())
    val avenir_next_light_48 = avenirNext(FontWeight.Light, dp48.toSp())
    val avenir_next_light_56 = avenirNext(FontWeight.Light, dp56.toSp())
    val avenir_next_light_64 = avenirNext(FontWeight.Light, dp64.toSp())
    val avenir_next_light_72 = avenirNext(FontWeight.Light, dp72.toSp())
    val avenir_next_light_80 = avenirNext(FontWeight.Light, dp80.toSp())
    val avenir_next_light_96 = avenirNext(FontWeight.Light, dp96.toSp())
    val avenir_next_light_100 = avenirNext(FontWeight.Light, dp100.toSp())
    // endregion

    // region Avenir Next - Regular
    val avenir_next_regular_12 = avenirNext(FontWeight.Normal, dp12.toSp())
    val avenir_next_regular_14 = avenirNext(FontWeight.Normal, dp14.toSp())
    val avenir_next_regular_16 = avenirNext(FontWeight.Normal, dp16.toSp())
    val avenir_next_regular_18 = avenirNext(FontWeight.Normal, dp18.toSp())
    val avenir_next_regular_20 = avenirNext(FontWeight.Normal, dp20.toSp())
    val avenir_next_regular_24 = avenirNext(FontWeight.Normal, dp24.toSp())
    val avenir_next_regular_28 = avenirNext(FontWeight.Normal, dp28.toSp())
    val avenir_next_regular_32 = avenirNext(FontWeight.Normal, dp32.toSp())
    val avenir_next_regular_36 = avenirNext(FontWeight.Normal, dp36.toSp())
    val avenir_next_regular_40 = avenirNext(FontWeight.Normal, dp40.toSp())
    val avenir_next_regular_48 = avenirNext(FontWeight.Normal, dp48.toSp())
    val avenir_next_regular_56 = avenirNext(FontWeight.Normal, dp56.toSp())
    val avenir_next_regular_64 = avenirNext(FontWeight.Normal, dp64.toSp())
    val avenir_next_regular_72 = avenirNext(FontWeight.Normal, dp72.toSp())
    val avenir_next_regular_80 = avenirNext(FontWeight.Normal, dp80.toSp())
    val avenir_next_regular_96 = avenirNext(FontWeight.Normal, dp96.toSp())
    val avenir_next_regular_100 = avenirNext(FontWeight.Normal, dp100.toSp())
    // endregion

    // region Avenir Next - Medium
    val avenir_next_medium_12 = avenirNext(FontWeight.Medium, dp12.toSp())
    val avenir_next_medium_14 = avenirNext(FontWeight.Medium, dp14.toSp())
    val avenir_next_medium_16 = avenirNext(FontWeight.Medium, dp16.toSp())
    val avenir_next_medium_18 = avenirNext(FontWeight.Medium, dp18.toSp())
    val avenir_next_medium_20 = avenirNext(FontWeight.Medium, dp20.toSp())
    val avenir_next_medium_24 = avenirNext(FontWeight.Medium, dp24.toSp())
    val avenir_next_medium_28 = avenirNext(FontWeight.Medium, dp28.toSp())
    val avenir_next_medium_32 = avenirNext(FontWeight.Medium, dp32.toSp())
    val avenir_next_medium_36 = avenirNext(FontWeight.Medium, dp36.toSp())
    val avenir_next_medium_40 = avenirNext(FontWeight.Medium, dp40.toSp())
    val avenir_next_medium_48 = avenirNext(FontWeight.Medium, dp48.toSp())
    val avenir_next_medium_56 = avenirNext(FontWeight.Medium, dp56.toSp())
    val avenir_next_medium_64 = avenirNext(FontWeight.Medium, dp64.toSp())
    val avenir_next_medium_72 = avenirNext(FontWeight.Medium, dp72.toSp())
    val avenir_next_medium_80 = avenirNext(FontWeight.Medium, dp80.toSp())
    val avenir_next_medium_96 = avenirNext(FontWeight.Medium, dp96.toSp())
    val avenir_next_medium_100 = avenirNext(FontWeight.Medium, dp100.toSp())
    // endregion

    // region Avenir Next - SemiBold
    val avenir_next_semi_bold_12 = avenirNext(FontWeight.SemiBold, dp12.toSp())
    val avenir_next_semi_bold_14 = avenirNext(FontWeight.SemiBold, dp14.toSp())
    val avenir_next_semi_bold_16 = avenirNext(FontWeight.SemiBold, dp16.toSp())
    val avenir_next_semi_bold_18 = avenirNext(FontWeight.SemiBold, dp18.toSp())
    val avenir_next_semi_bold_20 = avenirNext(FontWeight.SemiBold, dp20.toSp())
    val avenir_next_semi_bold_24 = avenirNext(FontWeight.SemiBold, dp24.toSp())
    val avenir_next_semi_bold_28 = avenirNext(FontWeight.SemiBold, dp28.toSp())
    val avenir_next_semi_bold_32 = avenirNext(FontWeight.SemiBold, dp32.toSp())
    val avenir_next_semi_bold_36 = avenirNext(FontWeight.SemiBold, dp36.toSp())
    val avenir_next_semi_bold_40 = avenirNext(FontWeight.SemiBold, dp40.toSp())
    val avenir_next_semi_bold_48 = avenirNext(FontWeight.SemiBold, dp48.toSp())
    val avenir_next_semi_bold_56 = avenirNext(FontWeight.SemiBold, dp56.toSp())
    val avenir_next_semi_bold_64 = avenirNext(FontWeight.SemiBold, dp64.toSp())
    val avenir_next_semi_bold_72 = avenirNext(FontWeight.SemiBold, dp72.toSp())
    val avenir_next_semi_bold_80 = avenirNext(FontWeight.SemiBold, dp80.toSp())
    val avenir_next_semi_bold_96 = avenirNext(FontWeight.SemiBold, dp96.toSp())
    val avenir_next_semi_bold_100 = avenirNext(FontWeight.SemiBold, dp100.toSp())
    // endregion

    // region Avenir Next - Bold
    val avenir_next_bold_12 = avenirNext(FontWeight.Bold, dp12.toSp())
    val avenir_next_bold_14 = avenirNext(FontWeight.Bold, dp14.toSp())
    val avenir_next_bold_16 = avenirNext(FontWeight.Bold, dp16.toSp())
    val avenir_next_bold_18 = avenirNext(FontWeight.Bold, dp18.toSp())
    val avenir_next_bold_20 = avenirNext(FontWeight.Bold, dp20.toSp())
    val avenir_next_bold_24 = avenirNext(FontWeight.Bold, dp24.toSp())
    val avenir_next_bold_28 = avenirNext(FontWeight.Bold, dp28.toSp())
    val avenir_next_bold_32 = avenirNext(FontWeight.Bold, dp32.toSp())
    val avenir_next_bold_36 = avenirNext(FontWeight.Bold, dp36.toSp())
    val avenir_next_bold_40 = avenirNext(FontWeight.Bold, dp40.toSp())
    val avenir_next_bold_48 = avenirNext(FontWeight.Bold, dp48.toSp())
    val avenir_next_bold_56 = avenirNext(FontWeight.Bold, dp56.toSp())
    val avenir_next_bold_64 = avenirNext(FontWeight.Bold, dp64.toSp())
    val avenir_next_bold_72 = avenirNext(FontWeight.Bold, dp72.toSp())
    val avenir_next_bold_80 = avenirNext(FontWeight.Bold, dp80.toSp())
    val avenir_next_bold_96 = avenirNext(FontWeight.Bold, dp96.toSp())
    val avenir_next_bold_100 = avenirNext(FontWeight.Bold, dp100.toSp())
    // endregion

    // region Avenir Next - Italic
    val avenir_next_italic_12 = avenirNextItalic(dp12.toSp())
    val avenir_next_italic_14 = avenirNextItalic(dp14.toSp())
    val avenir_next_italic_16 = avenirNextItalic(dp16.toSp())
    val avenir_next_italic_18 = avenirNextItalic(dp18.toSp())
    val avenir_next_italic_20 = avenirNextItalic(dp20.toSp())
    val avenir_next_italic_24 = avenirNextItalic(dp24.toSp())
    val avenir_next_italic_28 = avenirNextItalic(dp28.toSp())
    val avenir_next_italic_32 = avenirNextItalic(dp32.toSp())
    val avenir_next_italic_36 = avenirNextItalic(dp36.toSp())
    val avenir_next_italic_40 = avenirNextItalic(dp40.toSp())
    val avenir_next_italic_48 = avenirNextItalic(dp48.toSp())
    val avenir_next_italic_56 = avenirNextItalic(dp56.toSp())
    val avenir_next_italic_64 = avenirNextItalic(dp64.toSp())
    val avenir_next_italic_72 = avenirNextItalic(dp72.toSp())
    val avenir_next_italic_80 = avenirNextItalic(dp80.toSp())
    val avenir_next_italic_96 = avenirNextItalic(dp96.toSp())
    val avenir_next_italic_100 = avenirNextItalic(dp100.toSp())
    // endregion

    // region Orator - Regular (Monospace)
    val orator_regular_12 = orator(dp12.toSp())
    val orator_regular_14 = orator(dp14.toSp())
    val orator_regular_16 = orator(dp16.toSp())
    val orator_regular_18 = orator(dp18.toSp())
    val orator_regular_20 = orator(dp20.toSp())
    val orator_regular_24 = orator(dp24.toSp())
    val orator_regular_28 = orator(dp28.toSp())
    val orator_regular_32 = orator(dp32.toSp())
    val orator_regular_36 = orator(dp36.toSp())
    val orator_regular_40 = orator(dp40.toSp())
    val orator_regular_48 = orator(dp48.toSp())
    val orator_regular_56 = orator(dp56.toSp())
    val orator_regular_64 = orator(dp64.toSp())
    val orator_regular_72 = orator(dp72.toSp())
    val orator_regular_80 = orator(dp80.toSp())
    val orator_regular_96 = orator(dp96.toSp())
    val orator_regular_100 = orator(dp100.toSp())
    // endregion
}