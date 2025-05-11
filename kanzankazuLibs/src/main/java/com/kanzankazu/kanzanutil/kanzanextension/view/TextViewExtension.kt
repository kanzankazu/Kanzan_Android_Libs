@file:Suppress("DEPRECATION", "VARIABLE_WITH_REDUNDANT_INITIALIZER")

package com.kanzankazu.kanzanutil.kanzanextension.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Handler
import android.text.Selection
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.ScrollView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.kanzankazu.kanzanutil.kanzanextension.toDigits
import com.kanzankazu.kanzanutil.kanzanextension.type.getRibuan
import com.kanzankazu.kanzanutil.kanzanextension.type.getRupiah
import com.kanzankazu.kanzanutil.kanzanextension.type.setRibuan
import com.kanzankazu.kanzanutil.kanzanextension.type.setRupiah
import com.kanzankazu.kanzanutil.kanzanextension.type.toIntOrDefault
import java.lang.Double.parseDouble
import java.text.DecimalFormat
import androidx.core.graphics.drawable.toDrawable

/**
 * Retrieves the trimmed text content of the TextView. If the text content is empty or contains only whitespace,
 * it returns a default value provided by the caller.
 *
 * @param defaultValue The value to return if the text content is empty or contains only whitespace.
 *                     Defaults to an empty string.
 * @return The trimmed text content of the TextView, or the specified default value if the text is empty or blank.
 *
 * Example:
 * ```kotlin
 * val textView: TextView = findViewById(R.id.my_text_view)
 * textView.text = "  Hello World  "
 * val result = textView.string("Default Text") // result: "Hello World"
 *
 * textView.text = "  "
 * val emptyResult = textView.string("Default Text") // emptyResult: "Default Text"
 * ```
 */
fun TextView.string(defaultValue: String = ""): String = if (text.toString().trim().isNotEmpty()) text.toString().trim { it <= ' ' } else defaultValue

/**
 * Converts the text of a TextView into an integer. If the text is empty or cannot be converted to a valid integer,
 * the specified default value is returned.
 *
 * @param defaultValue The integer value to return if the text could not be converted to an integer. Defaults to 0.
 * @return The integer representation of the TextView's text, or the default value if the text is empty or invalid.
 *
 * Example:
 * ```kotlin
 * val textView: TextView = findViewById(R.id.textView)
 * textView.text = "123"
 * val value = textView.int(0) // Result: 123
 *
 * textView.text = "abc"
 * val fallbackValue = textView.int(10) // Result: 10
 *
 * textView.text = ""
 * val emptyValue = textView.int(5) // Result: 5
 * ```
 */
fun TextView.int(defaultValue: Int = 0): Int = if (string().isNotEmpty()) string().toDigits().toIntOrDefault() else defaultValue

/**
 * Converts the text of a TextView to a long value. If the text is not a valid numeric value or is empty,
 * the provided default value is returned.
 *
 * @param defaultValue The fallback value to return if the TextView is empty or cannot be converted to a long. Defaults to `0`.
 * @return The long representation of the TextView's text, or the specified default value if the text is empty or invalid.
 *
 * Example:
 * ```kotlin
 * val textView = TextView(context)
 * textView.text = "12345"
 * val longValue = textView.long() // Returns 12345
 *
 * textView.text = ""
 * val defaultLongValue = textView.long(10) // Returns 10
 * ```
 */
fun TextView.long(defaultValue: Long = 0): Long = if (string().isNotEmpty()) string().toDigits().toLong() else defaultValue

/**
 * Adjusts the gravity of the `TextView` to the specified value.
 *
 * @param gravity The gravity constant to apply to the `TextView`.
 *                Common values include `Gravity.CENTER`, `Gravity.START`, `Gravity.END`,
 *                `Gravity.TOP`, or `Gravity.BOTTOM`.
 *
 * Example:
 * ```kotlin
 * val textView: TextView = findViewById(R.id.myTextView)
 * textView.gravity(Gravity.CENTER) // Centers the text within the TextView
 * ```
 */
fun TextView.gravity(gravity: Int) {
    //sample this.gravity = Gravity.CENTER or Gravity.BOTTOM
    this.gravity = gravity
}

/**
 * Applies a strikethrough effect to the text displayed in a `TextView`.
 * This is achieved by modifying the `paintFlags` property of the `TextView`
 * to include the `STRIKE_THRU_TEXT_FLAG`, which renders the text with a line through it.
 *
 * This method allows developers to visually convey that the text is no longer relevant or has been "crossed out".
 *
 * Usage should be limited to enhancing the user interface for situations where such an effect is suitable.
 *
 * Example:
 * ```kotlin
 * val textView: TextView = findViewById(R.id.textView)
 * textView.strikeLine()
 * ```
 */
fun TextView.strikeLine() {
    this.paintFlags = this.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
}

/**
 * Sets the color of the `TextView`'s text based on the provided parameters.
 * By default, it applies `defaultColor`, but if `isElse` is set to `true`,
 * it applies `elseColor` instead.
 *
 * @param defaultColor The default color resource ID to apply. This is used when `isElse` is `false`.
 * @param isElse A boolean flag indicating whether to use the alternate color (`elseColor`). Defaults to `false`.
 * @param elseColor The alternate color resource ID to apply if `isElse` is `true`. Defaults to `-1`.
 *
 * Example:
 * ```kotlin
 * // Use default color
 * textView.color(R.color.primaryColor)
 *
 * // Use alternate color when isElse is true
 * textView.color(
 *     defaultColor = R.color.primaryColor,
 *     isElse = true,
 *     elseColor = R.color.secondaryColor
 * )
 * ```
 */
fun TextView.color(@ColorRes defaultColor: Int, isElse: Boolean = false, @ColorRes elseColor: Int = -1) {
    if (!isElse) {
        setTextColor(ContextCompat.getColor(context, defaultColor))
    } else {
        setTextColor(ContextCompat.getColor(context, elseColor))
    }
}

/**
 * Applies two different text styles to a TextView's content. Specifically, it changes the color of a specified text range
 * and applies bold styling to the remainder of the text starting from the given range's start position.
 *
 * @param value The complete string content to be styled.
 * @param startPosition The starting position of the range to apply the color and bold styling. Should be between 0 and the string length.
 * @param endPosition The ending position of the range to apply the color. Should be between 0 and the string length and greater than or equal to `start
 * Position`.
 *
 * This method highlights the specified range of text (from `startPosition` to `endPosition`) with a predefined color
 * (`#0081e8`) and sets the text from the starting position to the end of the text to bold.
 * The styled text is then applied to the TextView.
 *
 * Example:
 * ```kotlin
 * val textView: TextView = findViewById(R.id.textView)
 * textView.formatTwoColor(
 *     value = "Hello, World!",
 *     startPosition = 7,
 *     endPosition = 12
 * )
 * // The text "World" will be colored (blue) and bold, and the rest of the text starting
 * // from "World" will be bold.
 * ```
 */
fun TextView.formatTwoColor(value: String, startPosition: Int, endPosition: Int) {

    val spannable = SpannableString(value)
    spannable.setSpan(
        ForegroundColorSpan(Color.parseColor("#0081e8")),
        startPosition, endPosition,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )

    spannable.setSpan(
        StyleSpan(Typeface.BOLD),
        startPosition, spannable.length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )

    this.text = spannable

}

/**
 * Configures the `TextView` to display a specified maximum number of lines, with overflowing text truncated at the end.
 * This method also ensures that the `TextView` can display multiple lines by setting `isSingleLine` to `false`.
 *
 * @param n The maximum number of lines to display in the `TextView`. Defaults to 2 if not specified.
 *
 * Example:
 * ```kotlin
 * val textView: TextView = findViewById(R.id.myTextView)
 * textView.line(3) // Allows the TextView to display up to 3 lines, truncating additional content.
 * ```
 */
fun TextView.line(n: Int = 2) {
    isSingleLine = false
    ellipsize = TextUtils.TruncateAt.END
    setLines(n)
}

/**
 * Extracts the `String` text from the `TextView`, converts it into a numeric representation of Rupiah (Indonesian currency)
 * by removing unnecessary formatting or symbols (like "Rp " or dots), and ensures the result contains only numeric digits.
 *
 * @return A `String` representing the numeric value of the Rupiah amount without any formatting or symbols.
 *         If the `TextView` is empty, returns "0".
 *
 * Example:
 * ```kotlin
 * val textView: TextView = findViewById(R.id.amountTextView)
 * textView.text = "Rp 1.234.567"
 * val cleanRupiah = textView.getRupiah() // Returns: "1234567"
 *
 * val emptyTextView: TextView = findViewById(R.id.emptyTextView)
 * emptyTextView.text = ""
 * val cleanEmptyRupiah = emptyTextView.getRupiah() // Returns: "0"
 * ```
 */
fun TextView.getRupiah(): String {
    return string().getRupiah().toDigits()
}

/**
 * Sets the text of a TextView to a formatted currency string in Indonesian Rupiah format.
 * The method internally converts a numeric string into a formatted Rupiah string using the `setRupiah` extension of String,
 * and applies the formatted result to the TextView.
 *
 * @param string The numeric string to be converted into Rupiah format. The string should represent a valid number (e.g., "1500000").
 *               If the string is invalid or conversion fails, the result may be an empty string.
 *
 * Example:
 * ```kotlin
 * val textView: TextView = findViewById(R.id.textView)
 * textView.setRupiah("1500000") // Sets the text as "Rp 1.500.000"
 * ```
 */
fun TextView.setRupiah(string: String) {
    text = string.setRupiah()
}

/**
 * Retrieves the text content of a `TextView`, processes it to remove dot (`.`) characters,
 * and converts any numeric content to a standard digit-only string format (e.g. removing any non-numeric characters).
 * If the `TextView` contains an empty or blank string, it will default to the value provided.
 *
 * @return A processed string that contains only numeric digits, excluding dot (`.`) characters.
 *         Returns "0" if the string contains no numeric characters.
 *
 * Example:
 * ```kotlin
 * val myTextView: TextView = findViewById(R.id.textView)
 * myTextView.text = "12.345.67"
 * val result = myTextView.getRibuan() // Result: "1234567"
 *
 * myTextView.text = "abc.def"
 * val result2 = myTextView.getRibuan() // Result: "0"
 * ```
 */
fun TextView.getRibuan(): String {
    return string().getRibuan().toDigits()
}

/**
 * Sets the text of a TextView by formatting the provided numeric string into "ribuan" (thousands) format.
 * The formatting applies thousand separators with dots (".") for improved readability.
 *
 * @param string A numeric string to be formatted into the "ribuan" format.
 *               If the string contains a decimal point (e.g., "123456.78"), only the integer part will be formatted.
 *               For invalid or non-numeric input, an empty string is displayed.
 *
 * Example:
 * ```kotlin
 * val textView: TextView = findViewById(R.id.textView)
 * textView.setRibuan("1234567") // The TextView will display "1.234.567".
 * textView.setRibuan("1234567.89") // The TextView will display "1.234.567".
 * ```
 */
fun TextView.setRibuan(string: String) {
    text = string.setRibuan()
}

/**
 * Formats and sets the price on a `TextView` by converting the string value into a currency format.
 * This function parses the string value into a double, formats it as a currency, and displays it as text on the `TextView`.
 *
 * @param value The price value as a string. It should represent a number (e.g., "1234.56").
 *
 * Example:
 * ```kotlin
 * val textView: TextView = findViewById(R.id.priceTextView)
 * textView.formatPrice("1234.56") // Displays "Rp 1,234.56" (assuming a specific currency format)
 * ```
 */
fun TextView.formatPrice(value: String) {
    this.text = formatCurrency(parseDouble(value))
}

/**
 * Formats a given price string as a negative currency value and sets it as the text of the TextView.
 * The formatted string is prefixed with a minus sign (-) followed by the properly formatted currency.
 *
 * @param value A string representing the price to be formatted. The method parses this string into a double
 * and applies the currency formatting.
 *
 * Example:
 * ```kotlin
 * val textView: TextView = findViewById(R.id.text_view)
 * textView.formatPriceNegative("1234.56") // TextView displays: "- Rp1.234,56"
 * ```
 */
@SuppressLint("SetTextI18n")
fun TextView.formatPriceNegative(value: String) {
    this.text = "- ${formatCurrency(parseDouble(value))}"
}

/**
 * Sets the text of multiple `TextView` objects to an empty string.
 * This method is useful for quickly clearing the content of several `TextView` elements at once.
 *
 * @param textViews A vararg parameter of `TextView` objects whose text content should be cleared.
 *
 * Example:
 * ```kotlin
 * val textView1: TextView = findViewById(R.id.textView1)
 * val textView2: TextView = findViewById(R.id.textView2)
 * textViewsSetEmpty(textView1, textView2)
 * // Both textView1 and textView2 will now have empty text
 * ```
 */
fun textViewsSetEmpty(vararg textViews: TextView) {
    textViews.forEach { it.text = "" }
}

/**
 * Formats a given price value into a currency string representation in Indonesian Rupiah (IDR).
 * The formatted string includes currency symbol "Rp" and uses "." as the thousand separator.
 *
 * @param yourPrice The price value to be formatted as a Double.
 * @return A formatted currency string in the format of Indonesian Rupiah (e.g., "Rp 1.000.000").
 *
 * Example:
 * ```kotlin
 * val price = 2500000.0
 * val formattedPrice = formatCurrency(price) // Output: "Rp 2.500.000"
 * ```
 */
private fun formatCurrency(yourPrice: Double): String = getCurrencyIdr(yourPrice)

/**
 * Formats a given price into Indonesian Rupiah (IDR) currency format.
 * The formatted string includes the "Rp" prefix and uses a period (.) as the thousands separator.
 *
 * @param price The price value to be formatted. This should be a `Double` representing the amount in IDR.
 * @return A formatted string representing the price in Indonesian Rupiah currency format, prefixed with "Rp".
 *
 * Example:
 * ```kotlin
 * val formattedPrice = getCurrencyIdr(1234567.89)
 * // Result: "Rp 1.234.567"
 * ```
 */
private fun getCurrencyIdr(price: Double): String {
    val format = DecimalFormat("#,###,###")
    return "Rp " + format.format(price).replace(",".toRegex(), ".")
}

/**
 * Checks if the `TextView` has reached its ellipsis state (i.e., when the content overflows and is truncated)
 * and triggers a callback with the result.
 *
 * @param listener A lambda function that receives a Boolean value indicating whether the TextView has reached its ellipsis.
 *                 - `true`: The content is truncated with an ellipsis.
 *                 - `false`: The content is fully visible without truncation.
 *
 * Example:
 * ```kotlin
 * textView.hasReachEllipsis { hasEllipsis ->
 *     if (hasEllipsis) {
 *         println("Text is truncated with ellipsis.")
 *     } else {
 *         println("Text is fully visible.")
 *     }
 * }
 * ```
 */
fun TextView.hasReachEllipsis(listener: (Boolean) -> Unit) {
    var hasReachEllipsize = false
    val vto = this.viewTreeObserver
    vto.addOnGlobalLayoutListener {
        val layout = this.layout
        if (layout != null) {
            val lines = layout.lineCount
            if (lines > 0) {
                val ellipsisCount = layout.getEllipsisCount(lines - 1)
                if (ellipsisCount > 0) {
                    hasReachEllipsize = true
                    listener(hasReachEllipsize)
                }
            }
        }
    }
}

/**
 * Toggles the visibility of additional text in a `TextView` with a "Read More/Less" button.
 * This method allows the user to expand or collapse a text description up to a specified maximum number of lines.
 *
 * @param textViewReadMoreBtn The `TextView` button used to toggle between "Read More" and "Read Less".
 * @param textViewDesc The `TextView` containing the description text that will be expanded or collapsed.
 * @param stringReadMore The text to display on the toggle button when the description is collapsed (e.g., "Read More").
 * @param stringReadLess The text to display on the toggle button when the description is expanded (e.g., "Read Less").
 * @param stringDesc The full description text to be displayed in the `TextView`.
 * @param nsvDB (Optional) A `ScrollView` to ensure smooth scrolling to the expanded text. Defaults to `null`.
 * @param maxLine The maximum number of lines to show in the collapsed state. Defaults to 3.
 *
 * Example:
 * ```kotlin
 * val textView = findViewById<TextView>(R.id.description)
 * val readMoreButton = findViewById<TextView>(R.id.readMoreButton)
 * val scrollView = findViewById<ScrollView>(R.id.scrollView)
 *
 * readMoreLess(
 *     textViewReadMoreBtn = readMoreButton,
 *     textViewDesc = textView,
 *     stringReadMore = "Show more",
 *     stringReadLess = "Show less",
 *     stringDesc = "This is a very long description text that can be expanded or collapsed...",
 *     nsvDB = scrollView,
 *     maxLine = 5
 * )
 * ```
 */
fun readMoreLess(textViewReadMoreBtn: TextView, textViewDesc: TextView, stringReadMore: String, stringReadLess: String, stringDesc: String, nsvDB: ScrollView? = null, maxLine: Int = 3) {
    if (textViewReadMoreBtn.text == stringReadMore) {
        textViewDesc.maxLines = 100
        textViewDesc.text = stringDesc
        textViewReadMoreBtn.text = stringReadLess
        if (nsvDB != null) Handler().postDelayed({ nsvDB.fullScroll(View.FOCUS_DOWN) }, 100)
    } else {
        textViewDesc.maxLines = maxLine
        textViewDesc.text = stringDesc
        textViewReadMoreBtn.text = stringReadMore
    }
}

/**
 * Adds clickable links to specified text segments within a `TextView`. Each segment of text
 * is associated with an `OnClickListener` to handle click events. It modifies the `TextView`
 * by turning specified text into clickable spans and enables link clicks.
 *
 * @param links A vararg of pairs where the first element is a string (text to be turned into a link),
 *              and the second is a `View.OnClickListener` defining the action to take when the link is clicked.
 *
 * Example:
 * ```kotlin
 * textView.makeLinks(
 *     "Google" to View.OnClickListener {
 *         // Handle click for "Google"
 *     },
 *     "Facebook" to View.OnClickListener {
 *         // Handle click for "Facebook"
 *     }
 * )
 * ```
 */
fun TextView.makeLinks(vararg links: Pair<String, View.OnClickListener>) {
    val spannableString = SpannableString(this.text)
    var startIndexOfLink = -1
    for (link in links) {
        val clickableSpan = object : ClickableSpan() {
            /**
             * Updates the appearance of the text in a `TextPaint` object used for rendering clickable spans.
             * This method sets the color of the text to the link color and enables underlining for styled text.
             *
             * @param textPaint The `TextPaint` object responsible for the appearance of the text.
             *                  This parameter allows customization of text attributes such as color, style, and decorations (e.g., underline or strike
             * -through).
             *
             * Example:
             * ```kotlin
             * val clickableSpan = object : ClickableSpan() {
             *     override fun updateDrawState(textPaint: TextPaint) {
             *         textPaint.color = textPaint.linkColor
             *         textPaint.isUnderlineText = true
             *     }
             *
             *     override fun onClick(view: View) {
             *         // Define what happens when the span is clicked
             *     }
             * }
             * ```
             */
            override fun updateDrawState(textPaint: TextPaint) {
                // use this to change the link color
                textPaint.color = textPaint.linkColor
                // toggle below value to enable/disable
                // the underline shown below the clickable text
                textPaint.isUnderlineText = true
            }

            /**
             * Handles the click event for a `View`. Specifically, it selects the entire content of a `TextView`
             * starting at index 0 and triggers the `onClick` method of the associated `View.OnClickListener`.
             *
             * @param view The view that was clicked. Expected to be a `TextView`, whose text content will be
             *             processed as a `Spannable`.
             *
             * Example:
             * ```kotlin
             * textView.makeLinks(
             *     "Clickable Text" to View.OnClickListener {
             *         // Handle click logic for the link
             *     }
             * )
             * ```
             */
            override fun onClick(view: View) {
                Selection.setSelection((view as TextView).text as Spannable, 0)
                view.invalidate()
                link.second.onClick(view)
            }
        }
        startIndexOfLink = this.text.toString().indexOf(link.first, startIndexOfLink + 1)
        //if(startIndexOfLink == -1) continue // todo if you want to verify your texts contains links text
        spannableString.setSpan(
            clickableSpan, startIndexOfLink, startIndexOfLink + link.first.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
    this.movementMethod =
        LinkMovementMethod.getInstance() // without LinkMovementMethod, link can not click
    this.setText(spannableString, TextView.BufferType.SPANNABLE)
}

fun TextView.rotateDrawable(@DrawableRes drawableResId: Int, angle: Float) {
    fun drawableToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }

        val width = drawable.intrinsicWidth.takeIf { it > 0 } ?: 1
        val height = drawable.intrinsicHeight.takeIf { it > 0 } ?: 1

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }

    fun rotateDrawableView(context: Context, @DrawableRes drawableResId: Int, angle: Float): Drawable? {
        val originalDrawable = ContextCompat.getDrawable(context, drawableResId) ?: return null
        val bitmap = drawableToBitmap(originalDrawable)

        val matrix = Matrix()
        matrix.postRotate(angle)

        val rotatedBitmap = Bitmap.createBitmap(
            bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
        )

        return rotatedBitmap.toDrawable(context.resources)
    }

    val rotatedDrawable = rotateDrawableView(context, drawableResId, angle)
    setCompoundDrawablesWithIntrinsicBounds(null, null, rotatedDrawable, null)
}
