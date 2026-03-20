package com.kanzankazu.kanzanwidget.compose.ui

import androidx.compose.ui.text.TextStyle
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
 *     Text(
 *         text = "Hello",
 *         style = AppTextStyle.nunito_regular_16
 *     )
 *
 *     Text(
 *         text = "Title",
 *         style = AppTextStyle.avenir_next_bold_24
 *     )
 * }
 * ```
 */

object AppTextStyle {
    // Nunito Styles
    val nunito_light_12 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Light,
        fontSize = dp12.toSp()
    )
    val nunito_light_14 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Light,
        fontSize = dp14.toSp()
    )
    val nunito_light_16 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Light,
        fontSize = dp16.toSp()
    )
    val nunito_light_18 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Light,
        fontSize = dp18.toSp()
    )
    val nunito_light_20 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Light,
        fontSize = dp20.toSp()
    )
    val nunito_light_24 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Light,
        fontSize = dp24.toSp()
    )
    val nunito_light_28 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Light,
        fontSize = dp28.toSp()
    )
    val nunito_light_32 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Light,
        fontSize = dp32.toSp()
    )
    val nunito_light_36 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Light,
        fontSize = dp36.toSp()
    )
    val nunito_light_40 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Light,
        fontSize = dp40.toSp()
    )
    val nunito_light_48 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Light,
        fontSize = dp48.toSp()
    )
    val nunito_light_56 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Light,
        fontSize = dp56.toSp()
    )
    val nunito_light_64 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Light,
        fontSize = dp64.toSp()
    )
    val nunito_light_72 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Light,
        fontSize = dp72.toSp()
    )
    val nunito_light_80 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Light,
        fontSize = dp80.toSp()
    )
    val nunito_light_96 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Light,
        fontSize = dp96.toSp()
    )
    val nunito_light_100 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Light,
        fontSize = dp100.toSp()
    )

    val nunito_regular_12 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Normal,
        fontSize = dp12.toSp()
    )
    val nunito_regular_14 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Normal,
        fontSize = dp14.toSp()
    )
    val nunito_regular_16 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Normal,
        fontSize = dp16.toSp()
    )
    val nunito_regular_18 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Normal,
        fontSize = dp18.toSp()
    )
    val nunito_regular_20 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Normal,
        fontSize = dp20.toSp()
    )
    val nunito_regular_24 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Normal,
        fontSize = dp24.toSp()
    )
    val nunito_regular_28 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Normal,
        fontSize = dp28.toSp()
    )
    val nunito_regular_32 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Normal,
        fontSize = dp32.toSp()
    )
    val nunito_regular_36 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Normal,
        fontSize = dp36.toSp()
    )
    val nunito_regular_40 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Normal,
        fontSize = dp40.toSp()
    )
    val nunito_regular_48 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Normal,
        fontSize = dp48.toSp()
    )
    val nunito_regular_56 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Normal,
        fontSize = dp56.toSp()
    )
    val nunito_regular_64 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Normal,
        fontSize = dp64.toSp()
    )
    val nunito_regular_72 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Normal,
        fontSize = dp72.toSp()
    )
    val nunito_regular_80 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Normal,
        fontSize = dp80.toSp()
    )
    val nunito_regular_96 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Normal,
        fontSize = dp96.toSp()
    )
    val nunito_regular_100 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Normal,
        fontSize = dp100.toSp()
    )

    val nunito_medium_12 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Medium,
        fontSize = dp12.toSp()
    )
    val nunito_medium_14 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Medium,
        fontSize = dp14.toSp()
    )
    val nunito_medium_16 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Medium,
        fontSize = dp16.toSp()
    )
    val nunito_medium_18 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Medium,
        fontSize = dp18.toSp()
    )
    val nunito_medium_20 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Medium,
        fontSize = dp20.toSp()
    )
    val nunito_medium_24 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Medium,
        fontSize = dp24.toSp()
    )
    val nunito_medium_28 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Medium,
        fontSize = dp28.toSp()
    )
    val nunito_medium_32 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Medium,
        fontSize = dp32.toSp()
    )
    val nunito_medium_36 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Medium,
        fontSize = dp36.toSp()
    )
    val nunito_medium_40 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Medium,
        fontSize = dp40.toSp()
    )
    val nunito_medium_48 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Medium,
        fontSize = dp48.toSp()
    )
    val nunito_medium_56 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Medium,
        fontSize = dp56.toSp()
    )
    val nunito_medium_64 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Medium,
        fontSize = dp64.toSp()
    )
    val nunito_medium_72 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Medium,
        fontSize = dp72.toSp()
    )
    val nunito_medium_80 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Medium,
        fontSize = dp80.toSp()
    )
    val nunito_medium_96 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Medium,
        fontSize = dp96.toSp()
    )
    val nunito_medium_100 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Medium,
        fontSize = dp100.toSp()
    )

    val nunito_semi_bold_12 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.SemiBold,
        fontSize = dp12.toSp()
    )
    val nunito_semi_bold_14 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.SemiBold,
        fontSize = dp14.toSp()
    )
    val nunito_semi_bold_16 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.SemiBold,
        fontSize = dp16.toSp()
    )
    val nunito_semi_bold_18 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.SemiBold,
        fontSize = dp18.toSp()
    )
    val nunito_semi_bold_20 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.SemiBold,
        fontSize = dp20.toSp()
    )
    val nunito_semi_bold_24 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.SemiBold,
        fontSize = dp24.toSp()
    )
    val nunito_semi_bold_28 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.SemiBold,
        fontSize = dp28.toSp()
    )
    val nunito_semi_bold_32 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.SemiBold,
        fontSize = dp32.toSp()
    )
    val nunito_semi_bold_36 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.SemiBold,
        fontSize = dp36.toSp()
    )
    val nunito_semi_bold_40 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.SemiBold,
        fontSize = dp40.toSp()
    )
    val nunito_semi_bold_48 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.SemiBold,
        fontSize = dp48.toSp()
    )
    val nunito_semi_bold_56 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.SemiBold,
        fontSize = dp56.toSp()
    )
    val nunito_semi_bold_64 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.SemiBold,
        fontSize = dp64.toSp()
    )
    val nunito_semi_bold_72 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.SemiBold,
        fontSize = dp72.toSp()
    )
    val nunito_semi_bold_80 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.SemiBold,
        fontSize = dp80.toSp()
    )
    val nunito_semi_bold_96 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.SemiBold,
        fontSize = dp96.toSp()
    )
    val nunito_semi_bold_100 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.SemiBold,
        fontSize = dp100.toSp()
    )

    val nunito_bold_12 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Bold,
        fontSize = dp12.toSp()
    )
    val nunito_bold_14 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Bold,
        fontSize = dp14.toSp()
    )
    val nunito_bold_16 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Bold,
        fontSize = dp16.toSp()
    )
    val nunito_bold_18 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Bold,
        fontSize = dp18.toSp()
    )
    val nunito_bold_20 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Bold,
        fontSize = dp20.toSp()
    )
    val nunito_bold_24 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Bold,
        fontSize = dp24.toSp()
    )
    val nunito_bold_28 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Bold,
        fontSize = dp28.toSp()
    )
    val nunito_bold_32 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Bold,
        fontSize = dp32.toSp()
    )
    val nunito_bold_36 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Bold,
        fontSize = dp36.toSp()
    )
    val nunito_bold_40 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Bold,
        fontSize = dp40.toSp()
    )
    val nunito_bold_48 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Bold,
        fontSize = dp48.toSp()
    )
    val nunito_bold_56 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Bold,
        fontSize = dp56.toSp()
    )
    val nunito_bold_64 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Bold,
        fontSize = dp64.toSp()
    )
    val nunito_bold_72 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Bold,
        fontSize = dp72.toSp()
    )
    val nunito_bold_80 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Bold,
        fontSize = dp80.toSp()
    )
    val nunito_bold_96 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Bold,
        fontSize = dp96.toSp()
    )
    val nunito_bold_100 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Bold,
        fontSize = dp100.toSp()
    )

    val nunito_extra_bold_12 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.ExtraBold,
        fontSize = dp12.toSp()
    )
    val nunito_extra_bold_14 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.ExtraBold,
        fontSize = dp14.toSp()
    )
    val nunito_extra_bold_16 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.ExtraBold,
        fontSize = dp16.toSp()
    )
    val nunito_extra_bold_18 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.ExtraBold,
        fontSize = dp18.toSp()
    )
    val nunito_extra_bold_20 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.ExtraBold,
        fontSize = dp20.toSp()
    )
    val nunito_extra_bold_24 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.ExtraBold,
        fontSize = dp24.toSp()
    )
    val nunito_extra_bold_28 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.ExtraBold,
        fontSize = dp28.toSp()
    )
    val nunito_extra_bold_32 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.ExtraBold,
        fontSize = dp32.toSp()
    )
    val nunito_extra_bold_36 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.ExtraBold,
        fontSize = dp36.toSp()
    )
    val nunito_extra_bold_40 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.ExtraBold,
        fontSize = dp40.toSp()
    )
    val nunito_extra_bold_48 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.ExtraBold,
        fontSize = dp48.toSp()
    )
    val nunito_extra_bold_56 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.ExtraBold,
        fontSize = dp56.toSp()
    )
    val nunito_extra_bold_64 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.ExtraBold,
        fontSize = dp64.toSp()
    )
    val nunito_extra_bold_72 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.ExtraBold,
        fontSize = dp72.toSp()
    )
    val nunito_extra_bold_80 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.ExtraBold,
        fontSize = dp80.toSp()
    )
    val nunito_extra_bold_96 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.ExtraBold,
        fontSize = dp96.toSp()
    )
    val nunito_extra_bold_100 = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.ExtraBold,
        fontSize = dp100.toSp()
    )

    // Avenir Next Styles
    val avenir_next_light_12 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.Light,
        fontSize = dp12.toSp()
    )
    val avenir_next_light_14 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.Light,
        fontSize = dp14.toSp()
    )
    val avenir_next_light_16 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.Light,
        fontSize = dp16.toSp()
    )
    val avenir_next_light_18 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.Light,
        fontSize = dp18.toSp()
    )
    val avenir_next_light_20 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.Light,
        fontSize = dp20.toSp()
    )
    val avenir_next_light_24 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.Light,
        fontSize = dp24.toSp()
    )
    val avenir_next_light_28 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.Light,
        fontSize = dp28.toSp()
    )
    val avenir_next_light_32 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.Light,
        fontSize = dp32.toSp()
    )
    val avenir_next_light_36 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.Light,
        fontSize = dp36.toSp()
    )
    val avenir_next_light_40 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.Light,
        fontSize = dp40.toSp()
    )
    val avenir_next_light_48 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.Light,
        fontSize = dp48.toSp()
    )
    val avenir_next_light_56 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.Light,
        fontSize = dp56.toSp()
    )
    val avenir_next_light_64 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.Light,
        fontSize = dp64.toSp()
    )
    val avenir_next_light_72 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.Light,
        fontSize = dp72.toSp()
    )
    val avenir_next_light_80 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.Light,
        fontSize = dp80.toSp()
    )
    val avenir_next_light_96 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.Light,
        fontSize = dp96.toSp()
    )
    val avenir_next_light_100 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.Light,
        fontSize = dp100.toSp()
    )

    val avenir_next_regular_12 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.Normal,
        fontSize = dp12.toSp()
    )
    val avenir_next_regular_14 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.Normal,
        fontSize = dp14.toSp()
    )
    val avenir_next_regular_16 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.Normal,
        fontSize = dp16.toSp()
    )
    val avenir_next_regular_18 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.Normal,
        fontSize = dp18.toSp()
    )
    val avenir_next_regular_20 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.Normal,
        fontSize = dp20.toSp()
    )
    val avenir_next_regular_24 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.Normal,
        fontSize = dp24.toSp()
    )
    val avenir_next_regular_28 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.Normal,
        fontSize = dp28.toSp()
    )
    val avenir_next_regular_32 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.Normal,
        fontSize = dp32.toSp()
    )
    val avenir_next_regular_36 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.Normal,
        fontSize = dp36.toSp()
    )
    val avenir_next_regular_40 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.Normal,
        fontSize = dp40.toSp()
    )
    val avenir_next_regular_48 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.Normal,
        fontSize = dp48.toSp()
    )
    val avenir_next_regular_56 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.Normal,
        fontSize = dp56.toSp()
    )
    val avenir_next_regular_64 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.Normal,
        fontSize = dp64.toSp()
    )
    val avenir_next_regular_72 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.Normal,
        fontSize = dp72.toSp()
    )
    val avenir_next_regular_80 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.Normal,
        fontSize = dp80.toSp()
    )
    val avenir_next_regular_96 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.Normal,
        fontSize = dp96.toSp()
    )
    val avenir_next_regular_100 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.Normal,
        fontSize = dp100.toSp()
    )

    val avenir_next_medium_12 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.Medium,
        fontSize = dp12.toSp()
    )
    val avenir_next_medium_14 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.Medium,
        fontSize = dp14.toSp()
    )
    val avenir_next_medium_16 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.Medium,
        fontSize = dp16.toSp()
    )
    val avenir_next_medium_18 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.Medium,
        fontSize = dp18.toSp()
    )
    val avenir_next_medium_20 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.Medium,
        fontSize = dp20.toSp()
    )
    val avenir_next_medium_24 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.Medium,
        fontSize = dp24.toSp()
    )
    val avenir_next_medium_28 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.Medium,
        fontSize = dp28.toSp()
    )
    val avenir_next_medium_32 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.Medium,
        fontSize = dp32.toSp()
    )
    val avenir_next_medium_36 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.Medium,
        fontSize = dp36.toSp()
    )
    val avenir_next_medium_40 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.Medium,
        fontSize = dp40.toSp()
    )
    val avenir_next_medium_48 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.Medium,
        fontSize = dp48.toSp()
    )
    val avenir_next_medium_56 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.Medium,
        fontSize = dp56.toSp()
    )
    val avenir_next_medium_64 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.Medium,
        fontSize = dp64.toSp()
    )
    val avenir_next_medium_72 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.Medium,
        fontSize = dp72.toSp()
    )
    val avenir_next_medium_80 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.Medium,
        fontSize = dp80.toSp()
    )
    val avenir_next_medium_96 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.Medium,
        fontSize = dp96.toSp()
    )
    val avenir_next_medium_100 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.Medium,
        fontSize = dp100.toSp()
    )

    val avenir_next_semi_bold_12 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.SemiBold,
        fontSize = dp12.toSp()
    )
    val avenir_next_semi_bold_14 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.SemiBold,
        fontSize = dp14.toSp()
    )
    val avenir_next_semi_bold_16 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.SemiBold,
        fontSize = dp16.toSp()
    )
    val avenir_next_semi_bold_18 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.SemiBold,
        fontSize = dp18.toSp()
    )
    val avenir_next_semi_bold_20 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.SemiBold,
        fontSize = dp20.toSp()
    )
    val avenir_next_semi_bold_24 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.SemiBold,
        fontSize = dp24.toSp()
    )
    val avenir_next_semi_bold_28 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.SemiBold,
        fontSize = dp28.toSp()
    )
    val avenir_next_semi_bold_32 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.SemiBold,
        fontSize = dp32.toSp()
    )
    val avenir_next_semi_bold_36 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.SemiBold,
        fontSize = dp36.toSp()
    )
    val avenir_next_semi_bold_40 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.SemiBold,
        fontSize = dp40.toSp()
    )
    val avenir_next_semi_bold_48 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.SemiBold,
        fontSize = dp48.toSp()
    )
    val avenir_next_semi_bold_56 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.SemiBold,
        fontSize = dp56.toSp()
    )
    val avenir_next_semi_bold_64 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.SemiBold,
        fontSize = dp64.toSp()
    )
    val avenir_next_semi_bold_72 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.SemiBold,
        fontSize = dp72.toSp()
    )
    val avenir_next_semi_bold_80 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.SemiBold,
        fontSize = dp80.toSp()
    )
    val avenir_next_semi_bold_96 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.SemiBold,
        fontSize = dp96.toSp()
    )
    val avenir_next_semi_bold_100 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.SemiBold,
        fontSize = dp100.toSp()
    )

    val avenir_next_bold_12 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.Bold,
        fontSize = dp12.toSp()
    )
    val avenir_next_bold_14 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.Bold,
        fontSize = dp14.toSp()
    )
    val avenir_next_bold_16 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.Bold,
        fontSize = dp16.toSp()
    )
    val avenir_next_bold_18 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.Bold,
        fontSize = dp18.toSp()
    )
    val avenir_next_bold_20 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.Bold,
        fontSize = dp20.toSp()
    )
    val avenir_next_bold_24 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.Bold,
        fontSize = dp24.toSp()
    )
    val avenir_next_bold_28 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.Bold,
        fontSize = dp28.toSp()
    )
    val avenir_next_bold_32 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.Bold,
        fontSize = dp32.toSp()
    )
    val avenir_next_bold_36 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.Bold,
        fontSize = dp36.toSp()
    )
    val avenir_next_bold_40 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.Bold,
        fontSize = dp40.toSp()
    )
    val avenir_next_bold_48 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.Bold,
        fontSize = dp48.toSp()
    )
    val avenir_next_bold_56 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.Bold,
        fontSize = dp56.toSp()
    )
    val avenir_next_bold_64 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.Bold,
        fontSize = dp64.toSp()
    )
    val avenir_next_bold_72 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.Bold,
        fontSize = dp72.toSp()
    )
    val avenir_next_bold_80 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.Bold,
        fontSize = dp80.toSp()
    )
    val avenir_next_bold_96 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.Bold,
        fontSize = dp96.toSp()
    )
    val avenir_next_bold_100 = TextStyle(
        fontFamily = AvenirNext,
        fontWeight = FontWeight.Bold,
        fontSize = dp100.toSp()
    )

    // Avenir Next Italic Styles
    val avenir_next_italic_12 = TextStyle(
        fontFamily = AvenirNextItalic,
        fontWeight = FontWeight.Normal,
        fontSize = dp12.toSp()
    )
    val avenir_next_italic_14 = TextStyle(
        fontFamily = AvenirNextItalic,
        fontWeight = FontWeight.Normal,
        fontSize = dp14.toSp()
    )
    val avenir_next_italic_16 = TextStyle(
        fontFamily = AvenirNextItalic,
        fontWeight = FontWeight.Normal,
        fontSize = dp16.toSp()
    )
    val avenir_next_italic_18 = TextStyle(
        fontFamily = AvenirNextItalic,
        fontWeight = FontWeight.Normal,
        fontSize = dp18.toSp()
    )
    val avenir_next_italic_20 = TextStyle(
        fontFamily = AvenirNextItalic,
        fontWeight = FontWeight.Normal,
        fontSize = dp20.toSp()
    )
    val avenir_next_italic_24 = TextStyle(
        fontFamily = AvenirNextItalic,
        fontWeight = FontWeight.Normal,
        fontSize = dp24.toSp()
    )
    val avenir_next_italic_28 = TextStyle(
        fontFamily = AvenirNextItalic,
        fontWeight = FontWeight.Normal,
        fontSize = dp28.toSp()
    )
    val avenir_next_italic_32 = TextStyle(
        fontFamily = AvenirNextItalic,
        fontWeight = FontWeight.Normal,
        fontSize = dp32.toSp()
    )
    val avenir_next_italic_36 = TextStyle(
        fontFamily = AvenirNextItalic,
        fontWeight = FontWeight.Normal,
        fontSize = dp36.toSp()
    )
    val avenir_next_italic_40 = TextStyle(
        fontFamily = AvenirNextItalic,
        fontWeight = FontWeight.Normal,
        fontSize = dp40.toSp()
    )
    val avenir_next_italic_48 = TextStyle(
        fontFamily = AvenirNextItalic,
        fontWeight = FontWeight.Normal,
        fontSize = dp48.toSp()
    )
    val avenir_next_italic_56 = TextStyle(
        fontFamily = AvenirNextItalic,
        fontWeight = FontWeight.Normal,
        fontSize = dp56.toSp()
    )
    val avenir_next_italic_64 = TextStyle(
        fontFamily = AvenirNextItalic,
        fontWeight = FontWeight.Normal,
        fontSize = dp64.toSp()
    )
    val avenir_next_italic_72 = TextStyle(
        fontFamily = AvenirNextItalic,
        fontWeight = FontWeight.Normal,
        fontSize = dp72.toSp()
    )
    val avenir_next_italic_80 = TextStyle(
        fontFamily = AvenirNextItalic,
        fontWeight = FontWeight.Normal,
        fontSize = dp80.toSp()
    )
    val avenir_next_italic_96 = TextStyle(
        fontFamily = AvenirNextItalic,
        fontWeight = FontWeight.Normal,
        fontSize = dp96.toSp()
    )
    val avenir_next_italic_100 = TextStyle(
        fontFamily = AvenirNextItalic,
        fontWeight = FontWeight.Normal,
        fontSize = dp100.toSp()
    )

    // Orator Style (Monospace)
    val orator_regular_12 = TextStyle(
        fontFamily = Orator,
        fontWeight = FontWeight.Normal,
        fontSize = dp12.toSp()
    )
    val orator_regular_14 = TextStyle(
        fontFamily = Orator,
        fontWeight = FontWeight.Normal,
        fontSize = dp14.toSp()
    )
    val orator_regular_16 = TextStyle(
        fontFamily = Orator,
        fontWeight = FontWeight.Normal,
        fontSize = dp16.toSp()
    )
    val orator_regular_18 = TextStyle(
        fontFamily = Orator,
        fontWeight = FontWeight.Normal,
        fontSize = dp18.toSp()
    )
    val orator_regular_20 = TextStyle(
        fontFamily = Orator,
        fontWeight = FontWeight.Normal,
        fontSize = dp20.toSp()
    )
    val orator_regular_24 = TextStyle(
        fontFamily = Orator,
        fontWeight = FontWeight.Normal,
        fontSize = dp24.toSp()
    )
    val orator_regular_28 = TextStyle(
        fontFamily = Orator,
        fontWeight = FontWeight.Normal,
        fontSize = dp28.toSp()
    )
    val orator_regular_32 = TextStyle(
        fontFamily = Orator,
        fontWeight = FontWeight.Normal,
        fontSize = dp32.toSp()
    )
    val orator_regular_36 = TextStyle(
        fontFamily = Orator,
        fontWeight = FontWeight.Normal,
        fontSize = dp36.toSp()
    )
    val orator_regular_40 = TextStyle(
        fontFamily = Orator,
        fontWeight = FontWeight.Normal,
        fontSize = dp40.toSp()
    )
    val orator_regular_48 = TextStyle(
        fontFamily = Orator,
        fontWeight = FontWeight.Normal,
        fontSize = dp48.toSp()
    )
    val orator_regular_56 = TextStyle(
        fontFamily = Orator,
        fontWeight = FontWeight.Normal,
        fontSize = dp56.toSp()
    )
    val orator_regular_64 = TextStyle(
        fontFamily = Orator,
        fontWeight = FontWeight.Normal,
        fontSize = dp64.toSp()
    )
    val orator_regular_72 = TextStyle(
        fontFamily = Orator,
        fontWeight = FontWeight.Normal,
        fontSize = dp72.toSp()
    )
    val orator_regular_80 = TextStyle(
        fontFamily = Orator,
        fontWeight = FontWeight.Normal,
        fontSize = dp80.toSp()
    )
    val orator_regular_96 = TextStyle(
        fontFamily = Orator,
        fontWeight = FontWeight.Normal,
        fontSize = dp96.toSp()
    )
    val orator_regular_100 = TextStyle(
        fontFamily = Orator,
        fontWeight = FontWeight.Normal,
        fontSize = dp100.toSp()
    )
}