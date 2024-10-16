package com.kanzankazu.kanzanwidget

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import com.kanzankazu.kanzanutil.kanzanextension.type.debugMessageDebug

class TextViewNoPadding : AppCompatTextView {
    private var mAdditionalPadding = 0

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    constructor(context: Context, attributeSet: AttributeSet, defStyle: Int) : super(context, attributeSet, defStyle)

    init {
        includeFontPadding = false
    }

    override fun onDraw(canvas: Canvas) {
        val yOff = -mAdditionalPadding / 6
        canvas.translate(0F, yOff.toFloat())
        super.onDraw(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var heightMeasureSpecNew = heightMeasureSpec
        getAdditionalPadding()
        val mode = MeasureSpec.getMode(heightMeasureSpecNew)
        if (mode != MeasureSpec.EXACTLY) {
            val measureHeight = measureHeight(text.toString(), widthMeasureSpec)
            var height = measureHeight - mAdditionalPadding
            height += paddingTop + paddingBottom
            heightMeasureSpecNew = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpecNew)
    }

    private fun measureHeight(text: String, widthMeasureSpec: Int): Int {
        val textSize = textSize
        val textView = TextView(context)
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
        textView.text = text
        textView.measure(widthMeasureSpec, 0)
        return textView.measuredHeight
    }

    private fun getAdditionalPadding(): Int {
        val textSize = textSize
        val textView = TextView(context)
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
        textView.setLines(1)
        textView.measure(0, 0)
        val measuredHeight = textView.measuredHeight
        if (measuredHeight - textSize > 0) {
            mAdditionalPadding = (measuredHeight - textSize).toInt()
            "onMeasure: height=$measuredHeight textSize=$textSize mAdditionalPadding=$mAdditionalPadding".debugMessageDebug()
        }
        return mAdditionalPadding
    }
}