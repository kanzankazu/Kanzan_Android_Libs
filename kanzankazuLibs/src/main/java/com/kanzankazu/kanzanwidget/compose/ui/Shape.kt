package com.kanzankazu.kanzanwidget.compose.ui

import androidx.compose.foundation.shape.AbsoluteCutCornerShape
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Kumpulan bentuk (shapes) yang dapat digunakan di seluruh aplikasi Compose.
 * 
 * Cara penggunaan:
 * ```kotlin
 * @Composable
 * fun MyComponent() {
 *     Surface(
 *         modifier = Modifier.clip(Shapes.small),
 *         shape = Shapes.medium
 *     ) {
 *         // content
 *     }
 *     
 *     Card(
 *         shape = Shapes.small
 *     ) {
 *         // content
 *     }
 *     
 *     // Menggunakan shape custom
 *     Box(
 *         modifier = Modifier
 *             .clip(CustomShapes.fullyRounded)
 *             .background(MaterialTheme.colorScheme.primary)
 *     ) {
 *         // content
 *     }
 * }
 * ```
 */

// Material 3 Shapes - Standard shapes untuk aplikasi
val Shapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),      // Untuk chips, badges, small buttons
    small = RoundedCornerShape(8.dp),           // Untuk buttons, cards, text fields
    medium = RoundedCornerShape(12.dp),          // Untuk cards, sheets, dialogs
    large = RoundedCornerShape(16.dp),           // Untuk bottom sheets, large cards
    extraLarge = RoundedCornerShape(28.dp)       // Untuk large containers, special components
)

// Custom Shapes - Bentuk-bentuk khusus yang sering digunakan
object CustomShapes {
    
    // === Rounded Corner Shapes ===
    
    // Fully rounded - untuk avatar, circular buttons
    val fullyRounded = RoundedCornerShape(50.dp)
    
    // Slightly rounded - untuk subtle corners
    val slightlyRounded = RoundedCornerShape(4.dp)
    
    // Medium rounded - untuk cards dan buttons
    val mediumRounded = RoundedCornerShape(12.dp)
    
    // Large rounded - untuk large containers
    val largeRounded = RoundedCornerShape(20.dp)
    
    // Top rounded - untuk bottom sheets, modal dialogs
    val topRounded = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 16.dp,
        bottomStart = 0.dp,
        bottomEnd = 0.dp
    )
    
    // Bottom rounded - untuk top bars, headers
    val bottomRounded = RoundedCornerShape(
        topStart = 0.dp,
        topEnd = 0.dp,
        bottomStart = 16.dp,
        bottomEnd = 16.dp
    )
    
    // Left rounded - untuk side panels
    val leftRounded = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 0.dp,
        bottomStart = 16.dp,
        bottomEnd = 0.dp
    )
    
    // Right rounded - untuk side panels
    val rightRounded = RoundedCornerShape(
        topStart = 0.dp,
        topEnd = 16.dp,
        bottomStart = 0.dp,
        bottomEnd = 16.dp
    )
    
    // Diagonal rounded - untuk special cards
    val diagonalRounded = RoundedCornerShape(
        topStart = 0.dp,
        topEnd = 16.dp,
        bottomStart = 16.dp,
        bottomEnd = 0.dp
    )
    
    // Asymmetric rounded - untuk unique designs
    val asymmetricRounded = RoundedCornerShape(
        topStart = 8.dp,
        topEnd = 20.dp,
        bottomStart = 16.dp,
        bottomEnd = 4.dp
    )
    
    // === Cut Corner Shapes ===
    
    // Small cut corners - untuk retro/tech style
    val smallCut = CutCornerShape(4.dp)
    
    // Medium cut corners - untuk cards dengan sudut tumpul
    val mediumCut = CutCornerShape(8.dp)
    
    // Large cut corners - untuk bold designs
    val largeCut = CutCornerShape(12.dp)
    
    // Top cut - untuk bottom sheets dengan cut corners
    val topCut = CutCornerShape(
        topStart = 8.dp,
        topEnd = 8.dp,
        bottomStart = 0.dp,
        bottomEnd = 0.dp
    )
    
    // Bottom cut - untuk headers dengan cut corners
    val bottomCut = CutCornerShape(
        topStart = 0.dp,
        topEnd = 0.dp,
        bottomStart = 8.dp,
        bottomEnd = 8.dp
    )
    
    // Diagonal cut - untuk special effects
    val diagonalCut = CutCornerShape(
        topStart = 0.dp,
        topEnd = 12.dp,
        bottomStart = 12.dp,
        bottomEnd = 0.dp
    )
    
    // === Special Purpose Shapes ===
    
    // Pill shape - untuk tags, badges, pills
    val pillShape = RoundedCornerShape(50.dp)
    
    // Stadium shape - untuk buttons dengan rounded ends
    val stadiumShape = RoundedCornerShape(
        topStart = 50.dp,
        topEnd = 50.dp,
        bottomStart = 50.dp,
        bottomEnd = 50.dp
    )
    
    // Card shape - untuk standard cards
    val cardShape = RoundedCornerShape(12.dp)
    
    // Dialog shape - untuk modal dialogs
    val dialogShape = RoundedCornerShape(16.dp)
    
    // Bottom sheet shape - untuk bottom sheets
    val bottomSheetShape = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 16.dp,
        bottomStart = 0.dp,
        bottomEnd = 0.dp
    )
    
    // Chip shape - untuk chips, tags
    val chipShape = RoundedCornerShape(8.dp)
    
    // Button shape - untuk standard buttons
    val buttonShape = RoundedCornerShape(8.dp)
    
    // FAB shape - untuk Floating Action Buttons
    val fabShape = RoundedCornerShape(16.dp)
    
    // TextField shape - untuk text fields, search bars
    val textFieldShape = RoundedCornerShape(8.dp)
    
    // === Size-based Shapes ===
    
    // Extra small shapes - untuk tiny elements
    val extraSmallShape = RoundedCornerShape(2.dp)
    
    // Small shapes - untuk small buttons, badges
    val smallShape = RoundedCornerShape(4.dp)
    
    // Medium shapes - untuk cards, buttons
    val mediumShape = RoundedCornerShape(8.dp)
    
    // Large shapes - untuk large cards, containers
    val largeShape = RoundedCornerShape(16.dp)
    
    // Extra large shapes - untuk hero elements
    val extraLargeShape = RoundedCornerShape(24.dp)
    
    // === Absolute Shapes (untuk consistency) ===
    
    // Absolute rounded corners
    val absoluteSmallRounded = AbsoluteRoundedCornerShape(4.dp)
    val absoluteMediumRounded = AbsoluteRoundedCornerShape(8.dp)
    val absoluteLargeRounded = AbsoluteRoundedCornerShape(16.dp)
    
    // Absolute cut corners
    val absoluteSmallCut = AbsoluteCutCornerShape(4.dp)
    val absoluteMediumCut = AbsoluteCutCornerShape(8.dp)
    val absoluteLargeCut = AbsoluteCutCornerShape(12.dp)
}

// Shape constants untuk kemudahan penggunaan
object ShapeConstants {
    const val EXTRA_SMALL_CORNER = 2
    const val SMALL_CORNER = 4
    const val MEDIUM_CORNER = 8
    const val LARGE_CORNER = 16
    const val EXTRA_LARGE_CORNER = 24
    const val FULLY_ROUNDED_CORNER = 50
}

