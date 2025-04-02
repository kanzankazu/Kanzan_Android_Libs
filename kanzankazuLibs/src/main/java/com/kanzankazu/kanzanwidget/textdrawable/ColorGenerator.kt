package com.kanzankazu.kanzanwidget.textdrawable

import java.util.*

/**
 * A class used to generate colors either randomly or based on a specific key.
 * The color generator is initialized with a list of colors and provides functionalities
 * to retrieve colors using randomization or by associating a key to a color.
 */
class ColorGenerator private constructor(private val mColors: List<Int>) {
    private val mRandom: Random = Random(System.currentTimeMillis())

    /**
     * Generates and returns a random color from a predefined list of colors.
     *
     * @return An integer representing the randomly selected color.
     *
     * Example:
     * ```kotlin
     * val colorGenerator = ColorGenerator.create(listOf(Color.RED, Color.BLUE, Color.GREEN))
     * val randomColor = colorGenerator.getRandomColor() // e.g., Color.RED
     * ```
     */
    fun getRandomColor(): Int {
        return mColors[mRandom.nextInt(mColors.size)]
    }

    /**
     * Returns an integer color value from a predefined palette based on the hash code of the provided key.
     * The color is determined by taking the absolute value of the key's hash code modulo the size of the palette.
     *
     * @param key The input object whose hash code is used to determine the color. Must not be null.
     * @return An integer representing the color corresponding to the given key from the color palette.
     *
     * Example:
     * ```kotlin
     * val generator = ColorGenerator.MATERIAL
     * val color = generator.getColor("exampleKey")
     * // color is an integer corresponding to one of the predefined colors in the palette
     * ```
     */
    fun getColor(key: Any): Int {
        return mColors[Math.abs(key.hashCode()) % mColors.size]
    }

    /**
     * Provides a set of predefined ColorGenerator instances and methods to create custom ones.
     */
    companion object {
        /**
         * Provides a default instance of the ColorGenerator class, pre-configured with a fixed set of colors.
         * This instance can be used to generate random or specific colors with ease.
         *
         * Example:
         * ```kotlin
         * val color = DEFAULT.getRandomColor() // Generates a random color from the default palette
         * ```
         */
        var DEFAULT: ColorGenerator
        /**
         * A predefined instance of the `ColorGenerator` class that provides a set of material design colors.
         * This instance can be used to dynamically generate colors associated with a key or to retrieve a random color
         * from the material design palette.
         *
         * Example:
         * ```kotlin
         * val generator = ColorGenerator.MATERIAL
         * val color = generator.getColor("exampleKey")
         * val randomColor = generator.getRandomColor()
         * ```
         */
        var MATERIAL: ColorGenerator

        init {
            DEFAULT = create(
                listOf(
                    0xfff16364.toInt(),
                    0xfff58559.toInt(),
                    0xfff9a43e.toInt(),
                    0xffe4c62e.toInt(),
                    0xff67bf74.toInt(),
                    0xff59a2be.toInt(),
                    0xff2093cd.toInt(),
                    0xffad62a7.toInt(),
                    0xff805781.toInt()
                )
            )
            MATERIAL = create(
                listOf(
                    0xffe57373.toInt(),
                    0xfff06292.toInt(),
                    0xffba68c8.toInt(),
                    0xff9575cd.toInt(),
                    0xff7986cb.toInt(),
                    0xff64b5f6.toInt(),
                    0xff4fc3f7.toInt(),
                    0xff4dd0e1.toInt(),
                    0xff4db6ac.toInt(),
                    0xff81c784.toInt(),
                    0xffaed581.toInt(),
                    0xffff8a65.toInt(),
                    0xffd4e157.toInt(),
                    0xffffd54f.toInt(),
                    0xffffb74d.toInt(),
                    0xffa1887f.toInt(),
                    0xff90a4ae.toInt()
                )
            )
        }

        /**
         * Creates a new instance of the `ColorGenerator` class using the provided list of colors.
         *
         * @param colorList A list of integers representing colors to be used by the `ColorGenerator`.
         * @return An instance of `ColorGenerator` initialized with the specified color list.
         */
        fun create(colorList: List<Int>): ColorGenerator {
            return ColorGenerator(colorList)
        }
    }
}