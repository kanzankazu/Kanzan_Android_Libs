@file:Suppress("PrivatePropertyName")

package com.kanzankazu.kanzanwidget.compose.ui

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Tema dasar aplikasi Compose dengan dukungan dark/light mode.
 *
 * Cara penggunaan:
 * ```kotlin
 * @Composable
 * fun MyApp() {
 *     BaseTheme {
 *         // Seluruh konten aplikasi di sini
 *         MaterialTheme {
 *             Surface {
 *                 Text(
 *                     text = "Hello World",
 *                     style = MaterialTheme.typography.bodyLarge
 *                 )
 *             }
 *         }
 *     }
 * }
 *
 * // Custom dark theme
 * @Composable
 * fun MyDarkApp() {
 *     BaseTheme(
 *         darkTheme = true,
 *         dynamicColor = false
 *     ) {
 *         // content
 *     }
 * }
 * ```
 */

@Composable
fun BaseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    schemeColor: Pair<ColorScheme, ColorScheme> = Pair(BaseDarkColorScheme, BaseLightColorScheme),
    typography: Typography = TypographyItungItungan,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> if (darkTheme) schemeColor.first else schemeColor.second
        darkTheme -> schemeColor.first
        else -> schemeColor.second
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = TypographyItungItungan,
        content = content
    )
}