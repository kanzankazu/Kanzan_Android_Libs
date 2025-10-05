/*
 * Copyright 2017 Chaos Leong
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kanzankazu.kanzanwidget.pinview

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.InputFilter
import android.text.TextPaint
import android.util.AttributeSet
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.EditorInfo
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.IntDef
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import com.kanzankazu.R

/**
 * Provides a widget for entering PIN/OTP/password etc.
 *
 * @author Chaos Leong
 * 01/04/2017
 */
class PinView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.pinViewStyle,
) : AppCompatEditText(context, attrs, defStyleAttr) {

    companion object {
        private const val TAG = "PinView"
        private const val DBG = false
        private const val BLINK = 500
        private const val DEFAULT_COUNT = 4
        private val NO_FILTERS = emptyArray<InputFilter>()
        private val HIGHLIGHT_STATES = intArrayOf(android.R.attr.state_selected)
        private const val VIEW_TYPE_RECTANGLE = 0
        private const val VIEW_TYPE_LINE = 1
        private const val VIEW_TYPE_NONE = 2
    }

    private var viewType: Int = 0
    private var pinItemCount: Int = 0
    private var pinItemWidth: Int = 0
    private var pinItemHeight: Int = 0
    private var pinItemRadius: Int = 0
    private var pinItemSpacing: Int = 0

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val animatorTextPaint = TextPaint()
    private var lineColor: ColorStateList? = null
    private var curLineColor = Color.BLACK
    private var lineWidth: Int = 0

    private val textRect = Rect()
    private val itemBorderRect = RectF()
    private val itemLineRect = RectF()
    private val path = Path()
    private val itemCenterPoint = PointF()

    private var defaultAddAnimator: ValueAnimator? = null
    private var isAnimationEnable = false
    private var isPasswordHidden: Boolean = false

    private var blink: Blink? = null
    private var isCursorVisible: Boolean = false
    private var drawCursor: Boolean = false
    private var cursorHeight: Float = 0f
    private var cursorWidth: Int = 0
    private var cursorColor: Int = 0

    private var itemBackgroundResource: Int = 0
    private var itemBackground: Drawable? = null

    private var hideLineWhenFilled: Boolean = false
    private var transformed: String? = null

    init {
        val res = resources
        paint.style = Paint.Style.STROKE
        animatorTextPaint.set(paint)

        val theme = context.theme
        val a = theme.obtainStyledAttributes(attrs, R.styleable.PinView, defStyleAttr, 0)

        try {
            viewType = a.getInt(R.styleable.PinView_viewType, VIEW_TYPE_RECTANGLE)
            pinItemCount = a.getInt(R.styleable.PinView_itemCount, DEFAULT_COUNT)
            pinItemHeight = a.getDimensionPixelSize(
                R.styleable.PinView_itemHeight,
                res.getDimensionPixelSize(R.dimen.pv_pin_view_item_size)
            )
            pinItemWidth = a.getDimensionPixelSize(
                R.styleable.PinView_itemWidth,
                res.getDimensionPixelSize(R.dimen.pv_pin_view_item_size)
            )
            pinItemSpacing = a.getDimensionPixelSize(
                R.styleable.PinView_itemSpacing,
                res.getDimensionPixelSize(R.dimen.pv_pin_view_item_spacing)
            )
            pinItemRadius = a.getDimensionPixelSize(
                R.styleable.PinView_itemRadius,
                0
            )
            lineWidth = a.getDimensionPixelSize(
                R.styleable.PinView_lineWidth,
                res.getDimensionPixelSize(R.dimen.pv_pin_view_item_line_width)
            )
            lineColor = a.getColorStateList(R.styleable.PinView_lineColor)
            isCursorVisible = a.getBoolean(R.styleable.PinView_android_cursorVisible, true)
            cursorColor = a.getColor(R.styleable.PinView_cursorColor, currentTextColor)
            cursorWidth = a.getDimensionPixelSize(
                R.styleable.PinView_cursorWidth,
                res.getDimensionPixelSize(R.dimen.pv_pin_view_cursor_width)
            )
            itemBackground = a.getDrawable(R.styleable.PinView_android_itemBackground)
            hideLineWhenFilled = a.getBoolean(R.styleable.PinView_hideLineWhenFilled, false)
        } finally {
            a.recycle()
        }

        lineColor?.let {
            curLineColor = it.defaultColor
        }
        updateCursorHeight()
        checkItemRadius()
        setMaxLength(pinItemCount)
        paint.strokeWidth = lineWidth.toFloat()
        setupAnimator()
        transformationMethod = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) disableSelectionMenu()
        isPasswordHidden = isPasswordInputType(inputType)
    }

    override fun setInputType(type: Int) {
        super.setInputType(type)
        isPasswordHidden = isPasswordInputType(inputType)
    }

    fun setPasswordHidden(hidden: Boolean) {
        isPasswordHidden = hidden
        requestLayout()
    }

    fun isPasswordHidden(): Boolean = isPasswordHidden

    override fun setTypeface(tf: Typeface?, style: Int) {
        super.setTypeface(tf, style)
        animatorTextPaint.set(paint)
    }

    override fun setTypeface(tf: Typeface?) {
        super.setTypeface(tf)
        animatorTextPaint.set(paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val width: Int
        val height: Int
        val boxHeight = pinItemHeight

        width = if (widthMode == MeasureSpec.EXACTLY) {
            widthSize
        } else {
            var boxesWidth = (pinItemCount - 1) * pinItemSpacing + pinItemCount * pinItemWidth
            boxesWidth += ViewCompat.getPaddingEnd(this) + ViewCompat.getPaddingStart(this)
            if (pinItemSpacing == 0) {
                boxesWidth -= (pinItemCount - 1) * lineWidth
            }
            boxesWidth
        }

        height = if (heightMode == MeasureSpec.EXACTLY) {
            heightSize
        } else {
            boxHeight + paddingTop + paddingBottom
        }

        setMeasuredDimension(width, height)
    }

    override fun onTextChanged(
        text: CharSequence?,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int,
    ) {
        if (start != text?.length) {
            moveSelectionToEnd()
        }

        makeBlink()

        if (isAnimationEnable && lengthAfter > lengthBefore) {
            defaultAddAnimator?.let {
                it.end()
                it.start()
            }
        }

        transformed = transformationMethod?.getTransformation(text, this)?.toString() ?: text?.toString()
    }

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect?) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect)
        if (focused) {
            moveSelectionToEnd()
            makeBlink()
        }
    }

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        super.onSelectionChanged(selStart, selEnd)
        if (selEnd != text?.length) {
            moveSelectionToEnd()
        }
    }

    private fun moveSelectionToEnd() {
        text?.length?.let { setSelection(it) }
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        if (lineColor == null || lineColor?.isStateful == true) {
            updateColors()
        }
    }

    override fun onDraw(canvas: Canvas) {
        canvas.save()
        updatePaints()
        drawPinView(canvas)
        canvas.restore()
    }

    private fun updatePaints() {
        paint.color = curLineColor
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = lineWidth.toFloat()
        paint.color = currentTextColor
    }

    private fun drawPinView(canvas: Canvas) {
        val highlightIdx = text?.length ?: 0
        for (i in 0 until pinItemCount) {
            val highlight = isFocused && highlightIdx == i
            paint.color = if (highlight) getLineColorForState(HIGHLIGHT_STATES) else curLineColor

            updateItemRectF(i)
            updateCenterPoint()

            canvas.save()
            if (viewType == VIEW_TYPE_RECTANGLE) {
                updatePinBoxPath(i)
                canvas.clipPath(path)
            }
            drawItemBackground(canvas, highlight)
            canvas.restore()

            if (highlight) {
                drawCursor(canvas)
            }

            when (viewType) {
                VIEW_TYPE_RECTANGLE -> drawPinBox(canvas, i)
                VIEW_TYPE_LINE -> drawPinLine(canvas, i)
            }

            drawText(canvas, i, highlight)
        }
    }

    private fun updateItemRectF(i: Int) {
        val halfLineWidth = lineWidth / 2f
        val startX = paddingLeft + i * (pinItemWidth + pinItemSpacing)
        val endX = startX + pinItemWidth
        val endY = pinItemHeight.toFloat()

        itemBorderRect.set(startX.toFloat(), 0f, endX.toFloat(), endY)
        itemLineRect.set(
            startX.toFloat(),
            endY - lineWidth,
            endX.toFloat(),
            endY
        )
    }

    private fun updateCenterPoint() {
        itemCenterPoint.set(
            itemBorderRect.centerX(),
            itemBorderRect.centerY()
        )
    }

    private fun updatePinBoxPath(i: Int) {
        path.reset()
        path.addRoundRect(
            itemBorderRect,
            pinItemRadius.toFloat(),
            pinItemRadius.toFloat(),
            Path.Direction.CCW
        )
    }

    private fun drawItemBackground(canvas: Canvas, highlight: Boolean) {
        itemBackground?.let { background ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                background.setHotspotBounds(
                    itemBorderRect.left.toInt(),
                    itemBorderRect.top.toInt(),
                    itemBorderRect.right.toInt(),
                    itemBorderRect.bottom.toInt()
                )
            }
            background.state = if (highlight) HIGHLIGHT_STATES else intArrayOf()
            background.bounds = Rect(
                itemBorderRect.left.toInt(),
                itemBorderRect.top.toInt(),
                itemBorderRect.right.toInt(),
                itemBorderRect.bottom.toInt()
            )
            background.draw(canvas)
        }
    }

    private fun drawCursor(canvas: Canvas) {
        if (isCursorVisible && drawCursor) {
            paint.style = Paint.Style.FILL
            paint.color = cursorColor
            canvas.drawRect(
                itemCenterPoint.x - cursorWidth / 2f,
                itemCenterPoint.y - cursorHeight / 2f,
                itemCenterPoint.x + cursorWidth / 2f,
                itemCenterPoint.y + cursorHeight / 2f,
                paint
            )
            paint.style = Paint.Style.STROKE
        }
    }

    private fun drawPinBox(canvas: Canvas, i: Int) {
        if (hideLineWhenFilled && i < (text?.length ?: 0)) {
            return
        }
        canvas.drawRoundRect(
            itemBorderRect,
            pinItemRadius.toFloat(),
            pinItemRadius.toFloat(),
            paint
        )
    }

    private fun drawPinLine(canvas: Canvas, i: Int) {
        if (hideLineWhenFilled && i < (text?.length ?: 0)) {
            return
        }
        canvas.drawRoundRect(
            itemLineRect,
            pinItemRadius.toFloat(),
            pinItemRadius.toFloat(),
            paint
        )
    }

    private fun drawText(canvas: Canvas, i: Int, highlight: Boolean) {
        val text = this.text
        if (text != null && i < text.length) {
            val char = if (isPasswordHidden) '•' else text[i]
            val charStr = char.toString()
            val textPaint = if (isAnimationEnable && highlight) animatorTextPaint else paint

            textPaint.getTextBounds(charStr, 0, 1, textRect)

            canvas.drawText(
                charStr,
                0,
                1,
                itemCenterPoint.x - textRect.width() / 2f - textRect.left,
                itemCenterPoint.y + textRect.height() / 2f - textRect.bottom,
                textPaint
            )
        }
    }

    private fun drawAnchorLine(canvas: Canvas) {
        val paint = Paint().apply {
            color = Color.RED
            strokeWidth = 4f
        }
        canvas.drawLine(
            itemCenterPoint.x,
            0f,
            itemCenterPoint.x,
            height.toFloat(),
            paint
        )
    }

    fun setViewType(@ViewType viewType: Int) {
        this.viewType = viewType
        checkItemRadius()
        invalidate()
    }

    @ViewType
    fun getViewType(): Int = viewType

    fun setPinItemCount(count: Int) {
        require(count >= 0) { "PinView ItemCount can't be negative value" }
        pinItemCount = count
        setMaxLength(count)
        requestLayout()
    }

    fun getPinItemCount(): Int = pinItemCount

    fun setPinItemWidth(width: Int) {
        pinItemWidth = width
        checkItemRadius()
        requestLayout()
    }

    fun getPinItemWidth(): Int = pinItemWidth

    fun setPinItemHeight(height: Int) {
        pinItemHeight = height
        updateCursorHeight()
        requestLayout()
    }

    fun getPinItemHeight(): Int = pinItemHeight

    fun setPinItemRadius(radius: Int) {
        pinItemRadius = radius
        checkItemRadius()
        invalidate()
    }

    fun getPinItemRadius(): Int = pinItemRadius

    fun setPinItemSpacing(spacing: Int) {
        pinItemSpacing = spacing
        requestLayout()
    }

    fun getPinItemSpacing(): Int = pinItemSpacing

    fun setLineWidth(width: Int) {
        lineWidth = width
        paint.strokeWidth = width.toFloat()
        checkItemRadius()
        invalidate()
    }

    fun getLineWidth(): Int = lineWidth

    fun setLineColor(color: ColorStateList?) {
        lineColor = color
        updateColors()
        invalidate()
    }

    fun setLineColor(@ColorInt color: Int) {
        lineColor = ColorStateList.valueOf(color)
        updateColors()
        invalidate()
    }

    fun getLineColors(): ColorStateList? = lineColor

    override fun setCursorVisible(visible: Boolean) {
        if (isCursorVisible != visible) {
            isCursorVisible = visible
            makeBlink()
            invalidate()
        }
    }

    fun setCursorWidth(width: Int) {
        cursorWidth = width
        invalidate()
    }

    fun getCursorWidth(): Int = cursorWidth

    fun setCursorColor(@ColorInt color: Int) {
        cursorColor = color
        invalidate()
    }

    fun getCursorColor(): Int = cursorColor

    fun setItemBackground(background: Drawable?) {
        itemBackground = background
        invalidate()
    }

    fun setItemBackgroundResource(@DrawableRes resId: Int) {
        itemBackground = ResourcesCompat.getDrawable(resources, resId, context.theme)
        invalidate()
    }

    fun getItemBackground(): Drawable? = itemBackground

    fun setHideLineWhenFilled(hide: Boolean) {
        hideLineWhenFilled = hide
        invalidate()
    }

    fun isHideLineWhenFilled(): Boolean = hideLineWhenFilled

    fun setAnimationEnable(enable: Boolean) {
        isAnimationEnable = enable
    }

    fun isAnimationEnable(): Boolean = isAnimationEnable

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(VIEW_TYPE_RECTANGLE, VIEW_TYPE_LINE, VIEW_TYPE_NONE)
    annotation class ViewType

    private fun setMaxLength(maxLength: Int) {
        filters = if (maxLength >= 0) {
            arrayOf(InputFilter.LengthFilter(maxLength))
        } else {
            NO_FILTERS
        }
    }

    private fun setupAnimator() {
        defaultAddAnimator = ValueAnimator.ofFloat(0.5f, 1f).apply {
            duration = 150
            interpolator = DecelerateInterpolator()
            addUpdateListener { animation ->
                val scale = animation.animatedValue as Float
                val alpha = (255 * scale).toInt()
                animatorTextPaint.textSize = textSize * scale
                animatorTextPaint.alpha = alpha
                postInvalidate()
            }
        }
    }

    private fun checkItemRadius() {
        when (viewType) {
            VIEW_TYPE_LINE -> {
                val halfOfLineWidth = lineWidth / 2f
                if (pinItemRadius > halfOfLineWidth) {
                    throw IllegalArgumentException(
                        "The itemRadius can not be greater than lineWidth when viewType is line"
                    )
                }
            }

            VIEW_TYPE_RECTANGLE -> {
                val halfOfItemWidth = pinItemWidth / 2f
                if (pinItemRadius > halfOfItemWidth) {
                    throw IllegalArgumentException("The itemRadius can not be greater than itemWidth")
                }
            }
        }
    }

    private fun updateCursorHeight() {
        cursorHeight = textSize * 1.1f
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun disableSelectionMenu() {
        setTextIsSelectable(false)
        importantForAutofill = View.IMPORTANT_FOR_AUTOFILL_NO
        isLongClickable = false
        setTextIsSelectable(false)
        customSelectionActionModeCallback = object : ActionMode.Callback {
            override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean = false
            override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean = false
            override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean = false
            override fun onDestroyActionMode(mode: ActionMode) {}
        }
    }

    private fun isPasswordInputType(type: Int): Boolean {
        val variation = type and EditorInfo.TYPE_MASK_VARIATION
        val klass = type and EditorInfo.TYPE_MASK_CLASS
        return klass == EditorInfo.TYPE_TEXT_VARIATION_PASSWORD ||
                klass == EditorInfo.TYPE_NUMBER_VARIATION_PASSWORD ||
                variation == EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD ||
                variation == EditorInfo.TYPE_TEXT_VARIATION_WEB_PASSWORD
    }

    private fun getLineColorForState(states: IntArray): Int {
        return lineColor?.getColorForState(states, curLineColor) ?: curLineColor
    }

    private fun updateColors() {
        curLineColor = getLineColorForState(drawableState)
    }

    private fun makeBlink() {
        if (!isCursorVisible) {
            if (blink != null) {
                removeCallbacks(blink)
                blink = null
            }
            return
        }

        if (blink == null) {
            blink = Blink()
        }

        removeCallbacks(blink)
        drawCursor = false
        postDelayed(blink!!, BLINK.toLong())
    }

    private inner class Blink : Runnable {
        private var cancelled = false

        override fun run() {
            if (cancelled) {
                return
            }

            removeCallbacks(this)

            if (isCursorVisible) {
                drawCursor = !drawCursor
                postDelayed(this, BLINK.toLong())
            }

            invalidate()
        }

        fun cancel() {
            if (!cancelled) {
                removeCallbacks(this)
                cancelled = true
            }
        }
    }
}
