package com.kanzankazu.kanzanwidget.compose.extension

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun initMod() = Modifier

@Composable
fun VerticalSpacer(height: Dp) {
    Spacer(modifier = Modifier.height(height))
}

@Composable
fun HorizontalSpacer(width: Dp) {
    Spacer(modifier = Modifier.width(width))
}

/**
 * Menambahkan background dengan rounded corners
 */
fun Modifier.roundedBackground(
    colorBackground: Color,
    radius: Dp = 8.dp,
) =
    this
        .clip(RoundedCornerShape(radius))
        .background(colorBackground)

/**
 * Menambahkan border dengan rounded corners
 */
fun Modifier.roundedBorder(
    colorBackground: Color,
    colorBorder: Color,
    width: Dp = 1.dp,
    radius: Dp = 8.dp,
) =
    this
        .clip(RoundedCornerShape(radius))
        .border(width, colorBorder, RoundedCornerShape(radius))
        .background(colorBackground)

/**
 * Menambahkan shadow dengan rounded corners
 */
fun Modifier.roundedShadow(
    elevation: Dp = 4.dp,
    radius: Dp = 8.dp,
) = this
    .shadow(elevation, RoundedCornerShape(radius))
    .clip(RoundedCornerShape(radius))

/**
 * Membuat modifier clickable dengan opacity effect
 */
fun Modifier.clickableWithRipple(onClick: () -> Unit) =
    this.clickable { onClick() }

/**
 * Membuat conditional composable dengan else
 */
@Composable
fun Boolean.Conditional(
    ifFalse: @Composable () -> Unit = {},
    ifTrue: @Composable () -> Unit,
) = if (this) ifTrue() else ifFalse()

@Composable
fun SmoothVisibility(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        // Animasi masuk: Muncul perlahan + Meluas ke bawah
        enter = fadeIn(animationSpec = tween(300)) + expandVertically(),
        // Animasi keluar: Hilang perlahan + Menyusut ke atas
        exit = fadeOut(animationSpec = tween(300)) + shrinkVertically(),
        content = content
    )
}

@Composable
fun SmoothVisibilitySpring(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn() + expandVertically(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ),
        exit = fadeOut() + shrinkVertically(),
        content = content
    )
}

