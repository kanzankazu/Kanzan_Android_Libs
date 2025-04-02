package com.kanzankazu.kanzanwidget.compose.animation

import android.graphics.BlurMaskFilter
import android.graphics.Paint
import android.graphics.Rect
import android.os.Build
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.StartOffset
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import kotlin.math.roundToInt

/**
 * The duration of animations in milliseconds. This constant is used across multiple composable animations
 * to standardize the animation timing for consistent visual behavior.
 *
 * Example usages include specifying the duration for infinite animations like fade-ins, rotations, or
 * custom animations in composables such as `BlurredAnimatedText`, `PulseAnimation`,
 * and `TripleOrbitLoadingAnimation`.
 */
private const val ANIMATION_DURATION = 1000

/**
 * Displays a text string with an animated blur effect for non-space characters.
 * The characters of the text animate independently to create a blurring and unblurring effect,
 * cycling through the animation over time.
 *
 * @param text The string to be displayed with the animated blur effect. Non-space characters will
 *             have a blur animation applied, while spaces remain unaffected.
 * @param modifier The modifier to be applied to the text layout. Defaults to an empty Modifier.
 *
 * Example:
 * ```kotlin
 * BlurredAnimatedText(
 *     text = "Blur Effect",
 *     modifier = Modifier.padding(16.dp)
 * )
 * ```
 */
@Composable
fun BlurredAnimatedText(
    text: String,
    modifier: Modifier = Modifier
) {
    val blurList = text.mapIndexed { index, character ->
        if(character == ' ') {
            remember {
                mutableStateOf(0f)
            }
        } else {
            val infiniteTransition = rememberInfiniteTransition(label = "infinite transition $index")
            infiniteTransition.animateFloat(
                initialValue = 10f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = ANIMATION_DURATION,
                        easing = LinearEasing
                    ),
                    repeatMode = RepeatMode.Reverse,
                    initialStartOffset = StartOffset(
                        offsetMillis = (ANIMATION_DURATION / text.length) * index
                    )
                ),
                label = "blur animation"
            )
        }
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        text.forEachIndexed { index, character ->
            Text(
                text = character.toString(),
                color = Color.White,
                modifier = Modifier
                    .graphicsLayer {
                        if(character != ' ') {
                            val blurAmount = blurList[index].value
                            renderEffect = BlurEffect(
                                radiusX = blurAmount,
                                radiusY = blurAmount
                            )
                        }
                    }
                    .then(
                        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
                            Modifier.fullContentBlur(
                                blurRadius = { blurList[index].value.roundToInt() }
                            )
                        } else {
                            Modifier
                        }
                    )
            )
        }
    }
}

/**
 * Applies a blur effect to the entire content drawn within the modifier.
 * The blur intensity is determined dynamically via a lambda function for the radius.
 * An optional color is also applied over the blurred content.
 *
 * @param blurRadius A lambda returning the blur radius as an integer. A radius of 0 disables the blur.
 * @param color The color to overlay on the blurred content. Defaults to black.
 * @return A Modifier that applies the full content blur with the specified radius and color.
 *
 * Example:
 * ```kotlin
 * Text(
 *     text = "Blurred Text",
 *     modifier = Modifier
 *         .fullContentBlur(blurRadius = { 15 }, color = Color.Gray)
 * )
 * ```
 */
private fun Modifier.fullContentBlur(
    blurRadius: () -> Int,
    color: Color = Color.Black
): Modifier {
    return drawWithCache {
        val radius = blurRadius()
        val nativePaint = Paint().apply {
            isAntiAlias = true
            this.color = color.toArgb()

            if(radius > 0) {
                maskFilter = BlurMaskFilter(
                    radius.toFloat(),
                    BlurMaskFilter.Blur.NORMAL
                )
            }
        }

        onDrawWithContent {
            drawContent()

            drawIntoCanvas { canvas ->
                canvas.save()

                val rect = Rect(0, 0, size.width.toInt(), size.height.toInt())
                canvas.nativeCanvas.drawRect(rect, nativePaint)

                canvas.restore()
            }
        }
    }
}