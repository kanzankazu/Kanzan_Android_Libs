@file:Suppress("DEPRECATION", "VARIABLE_WITH_REDUNDANT_INITIALIZER")

package com.kanzankazu.kanzanutil.kanzanextension.view

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
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
import androidx.core.content.ContextCompat
import com.kanzankazu.kanzanutil.kanzanextension.toDigits
import com.kanzankazu.kanzanutil.kanzanextension.type.getRibuan
import com.kanzankazu.kanzanutil.kanzanextension.type.getRupiah
import com.kanzankazu.kanzanutil.kanzanextension.type.setRibuan
import com.kanzankazu.kanzanutil.kanzanextension.type.setRupiah
import com.kanzankazu.kanzanutil.kanzanextension.type.toIntOrDefault
import java.lang.Double.parseDouble
import java.text.DecimalFormat

fun TextView.string(defaultValue: String = ""): String = if (text.toString().trim().isNotEmpty()) text.toString().trim { it <= ' ' } else defaultValue

fun TextView.int(defaultValue: Int = 0): Int = if (string().isNotEmpty()) string().toDigits().toIntOrDefault() else defaultValue

fun TextView.long(defaultValue: Long = 0): Long = if (string().isNotEmpty()) string().toDigits().toLong() else defaultValue

fun TextView.gravity(gravity: Int) {
    //sample this.gravity = Gravity.CENTER or Gravity.BOTTOM
    this.gravity = gravity
}

fun TextView.strikeLine() {
    this.paintFlags = this.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
}

fun TextView.color(@ColorRes defaultColor: Int, isElse: Boolean = false, @ColorRes elseColor: Int = -1) {
    if (!isElse) {
        setTextColor(ContextCompat.getColor(context, defaultColor))
    } else {
        setTextColor(ContextCompat.getColor(context, elseColor))
    }
}

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

fun TextView.line(n: Int = 2) {
    isSingleLine = false
    ellipsize = TextUtils.TruncateAt.END
    setLines(n)
}

fun TextView.getRupiah(): String {
    return string().getRupiah().toDigits()
}

fun TextView.setRupiah(string: String) {
    text = string.setRupiah()
}

fun TextView.getRibuan(): String {
    return string().getRibuan().toDigits()
}

fun TextView.setRibuan(string: String) {
    text = string.setRibuan()
}

fun TextView.formatPrice(value: String) {
    this.text = formatCurrency(parseDouble(value))
}

@SuppressLint("SetTextI18n")
fun TextView.formatPriceNegative(value: String) {
    this.text = "- ${formatCurrency(parseDouble(value))}"
}

fun textViewsSetEmpty(vararg textViews: TextView) {
    textViews.forEach { it.text = "" }
}

private fun formatCurrency(yourPrice: Double): String = getCurrencyIdr(yourPrice)

private fun getCurrencyIdr(price: Double): String {
    val format = DecimalFormat("#,###,###")
    return "Rp " + format.format(price).replace(",".toRegex(), ".")
}

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

fun TextView.makeLinks(vararg links: Pair<String, View.OnClickListener>) {
    val spannableString = SpannableString(this.text)
    var startIndexOfLink = -1
    for (link in links) {
        val clickableSpan = object : ClickableSpan() {
            override fun updateDrawState(textPaint: TextPaint) {
                // use this to change the link color
                textPaint.color = textPaint.linkColor
                // toggle below value to enable/disable
                // the underline shown below the clickable text
                textPaint.isUnderlineText = true
            }

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
