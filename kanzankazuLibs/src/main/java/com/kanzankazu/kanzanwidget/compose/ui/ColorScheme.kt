package com.kanzankazu.kanzanwidget.compose.ui

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme

val BaseDarkColorScheme = darkColorScheme(
    // Primary colors - warna utama aplikasi
    primary = md_theme_dark_primary,                    // `MaterialTheme.colorScheme.primary` - Tombol utama, FAB, selected tabs
    onPrimary = md_theme_dark_onPrimary,                // `MaterialTheme.colorScheme.onPrimary` - Teks/icon di atas tombol primary
    primaryContainer = md_theme_dark_primaryContainer,        // `MaterialTheme.colorScheme.primaryContainer` - Background untuk selected items, chips
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,       // `MaterialTheme.colorScheme.onPrimaryContainer` - Teks di atas primary container

    // Secondary colors - warna aksen sekunder
    secondary = md_theme_dark_secondary,               // `MaterialTheme.colorScheme.secondary` - Tombol sekunder, filter chips, badges
    onSecondary = md_theme_dark_onSecondary,              // `MaterialTheme.colorScheme.onSecondary` - Teks/icon di atas secondary
    secondaryContainer = md_theme_dark_secondaryContainer,             // `MaterialTheme.colorScheme.secondaryContainer` - Background secondary containers
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,      // `MaterialTheme.colorScheme.onSecondaryContainer` - Teks di atas secondary container

    // Tertiary colors - warna aksen tersier
    tertiary = md_theme_dark_tertiary,                       // `MaterialTheme.colorScheme.tertiary` - Aksen warna ketiga, special buttons
    onTertiary = md_theme_dark_onTertiary,               // `MaterialTheme.colorScheme.onTertiary` - Teks/icon di atas tertiary
    tertiaryContainer = md_theme_dark_tertiaryContainer,            // `MaterialTheme.colorScheme.tertiaryContainer` - Background tertiary containers
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,      // `MaterialTheme.colorScheme.onTertiaryContainer` - Teks di atas tertiary container

    // Error colors - untuk error states
    error = md_theme_dark_error,              // `MaterialTheme.colorScheme.error` - Error text, error icons, delete buttons
    onError = md_theme_dark_onError,            // `MaterialTheme.colorScheme.onError` - Teks di atas error background
    errorContainer = md_theme_dark_errorContainer,     // `MaterialTheme.colorScheme.errorContainer` - Background error messages/snackbars
    onErrorContainer = md_theme_dark_onErrorContainer,    // `MaterialTheme.colorScheme.onErrorContainer` - Teks error di dalam container

    // Surface colors - untuk cards, sheets, dialogs
    surface = md_theme_dark_surface,             // `MaterialTheme.colorScheme.surface` - Cards, sheets, dialogs, bottom sheets
    onSurface = md_theme_dark_onSurface,           // `MaterialTheme.colorScheme.onSurface` - Teks utama di atas surface
    surfaceVariant = md_theme_dark_surfaceVariant,      // `MaterialTheme.colorScheme.surfaceVariant` - Variant surfaces, outlined fields
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,    // `MaterialTheme.colorScheme.onSurfaceVariant` - Teks di atas surface variant

    // Background colors - background aplikasi
    background = md_theme_dark_background,          // `MaterialTheme.colorScheme.background` - Background utama aplikasi
    onBackground = md_theme_dark_onBackground,        // `MaterialTheme.colorScheme.onBackground` - Teks utama di background

    // Outline colors - untuk borders dan dividers
    outline = md_theme_dark_outline,            // `MaterialTheme.colorScheme.outline` - Borders, dividers, input field outlines
    outlineVariant = md_theme_dark_outlineVariant,      // `MaterialTheme.colorScheme.outlineVariant` - Disabled outlines, subtle borders

    // Inverse colors - untuk inverted themes
    inverseSurface = md_theme_dark_inverseSurface,      // `MaterialTheme.colorScheme.inverseSurface` - Background untuk inverted UI
    inverseOnSurface = md_theme_dark_inverseOnSurface,    // `MaterialTheme.colorScheme.inverseOnSurface` - Teks di atas inverse surface
    inversePrimary = md_theme_dark_inversePrimary,      // `MaterialTheme.colorScheme.inversePrimary` - Primary color untuk inverted theme

    // Scrim & Shadow - untuk overlays
    scrim = md_theme_dark_scrim,               // `MaterialTheme.colorScheme.scrim` - Overlay untuk dialogs, drawers, bottom sheets
    surfaceTint = md_theme_dark_surfaceTint,         // `MaterialTheme.colorScheme.surfaceTint` - Tint effect untuk surfaces
)

val BaseLightColorScheme = lightColorScheme(
    // Primary colors - warna utama aplikasi
    primary = md_theme_light_primary,                      // `MaterialTheme.colorScheme.primary` - Tombol utama, FAB, selected tabs
    onPrimary = md_theme_light_onPrimary,                 // `MaterialTheme.colorScheme.onPrimary` - Teks/icon di atas tombol primary
    primaryContainer = md_theme_light_primaryContainer,         // `MaterialTheme.colorScheme.primaryContainer` - Background untuk selected items, chips
    onPrimaryContainer = md_theme_light_onPrimaryContainer,        // `MaterialTheme.colorScheme.onPrimaryContainer` - Teks di atas primary container

    // Secondary colors - warna aksen sekunder
    secondary = md_theme_light_secondary,                // `MaterialTheme.colorScheme.secondary` - Tombol sekunder, filter chips, badges
    onSecondary = md_theme_light_onSecondary,               // `MaterialTheme.colorScheme.onSecondary` - Teks/icon di atas secondary
    secondaryContainer = md_theme_light_secondaryContainer,             // `MaterialTheme.colorScheme.secondaryContainer` - Background secondary containers
    onSecondaryContainer = md_theme_light_onSecondaryContainer,      // `MaterialTheme.colorScheme.onSecondaryContainer` - Teks di atas secondary container

    // Tertiary colors - warna aksen tersier
    tertiary = md_theme_light_tertiary,                        // `MaterialTheme.colorScheme.tertiary` - Aksen warna ketiga, special buttons
    onTertiary = md_theme_light_onTertiary,                // `MaterialTheme.colorScheme.onTertiary` - Teks/icon di atas tertiary
    tertiaryContainer = md_theme_light_tertiaryContainer,             // `MaterialTheme.colorScheme.tertiaryContainer` - Background tertiary containers
    onTertiaryContainer = md_theme_light_onTertiaryContainer,       // `MaterialTheme.colorScheme.onTertiaryContainer` - Teks di atas tertiary container

    // Error colors - untuk error states
    error = md_theme_light_error,               // `MaterialTheme.colorScheme.error` - Error text, error icons, delete buttons
    onError = md_theme_light_onError,                   // `MaterialTheme.colorScheme.onError` - Teks di atas error background
    errorContainer = md_theme_light_errorContainer,       // `MaterialTheme.colorScheme.errorContainer` - Background error messages/snackbars
    onErrorContainer = md_theme_light_onErrorContainer,     // `MaterialTheme.colorScheme.onErrorContainer` - Teks error di dalam container

    // Surface colors - untuk cards, sheets, dialogs
    surface = md_theme_light_surface,             // `MaterialTheme.colorScheme.surface` - Cards, sheets, dialogs, bottom sheets
    onSurface = md_theme_light_onSurface,           // `MaterialTheme.colorScheme.onSurface` - Teks utama di atas surface
    surfaceVariant = md_theme_light_surfaceVariant,       // `MaterialTheme.colorScheme.surfaceVariant` - Variant surfaces, outlined fields
    onSurfaceVariant = md_theme_light_onSurfaceVariant,     // `MaterialTheme.colorScheme.onSurfaceVariant` - Teks di atas surface variant

    // Background colors - background aplikasi
    background = md_theme_light_background,           // `MaterialTheme.colorScheme.background` - Background utama aplikasi
    onBackground = md_theme_light_onBackground,         // `MaterialTheme.colorScheme.onBackground` - Teks utama di background

    // Outline colors - untuk borders dan dividers
    outline = md_theme_light_outline,             // `MaterialTheme.colorScheme.outline` - Borders, dividers, input field outlines
    outlineVariant = md_theme_light_outlineVariant,      // `MaterialTheme.colorScheme.outlineVariant` - Disabled outlines, subtle borders

    // Inverse colors - untuk inverted themes
    inverseSurface = md_theme_light_inverseSurface,       // `MaterialTheme.colorScheme.inverseSurface` - Background untuk inverted UI
    inverseOnSurface = md_theme_light_inverseOnSurface,     // `MaterialTheme.colorScheme.inverseOnSurface` - Teks di atas inverse surface
    inversePrimary = md_theme_light_inversePrimary,       // `MaterialTheme.colorScheme.inversePrimary` - Primary color untuk inverted theme

    // Scrim & Shadow - untuk overlays
    scrim = md_theme_light_scrim,                // `MaterialTheme.colorScheme.scrim` - Overlay untuk dialogs, drawers, bottom sheets
    surfaceTint = md_theme_light_surfaceTint,          // `MaterialTheme.colorScheme.surfaceTint` - Tint effect untuk surfaces
)