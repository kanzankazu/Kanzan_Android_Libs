package com.kanzankazu.kanzanwidget.toolbar

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View.OnClickListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import com.kanzankazu.R

class KanzanToolbarComponent @JvmOverloads constructor(private val _context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : AppBarLayout(_context, attrs, defStyleAttr) {

    private var mNavListener: OnClickListener = OnClickListener { }
    private var mNavColor: Int = R.color.baseBlack
    private var mNavBackgroundColor: Int = R.color.baseWhite
    private var mNavigationIcon: Int = R.drawable.ic_arrow_head_left
    private var mSubTitleFontStyle: Int = R.style.TvStandard
    private var mTitleFontStyle: Int = R.style.TvStandard
    private var mSubTitleColor: Int = R.color.baseBlack
    private var mTitleColor: Int = R.color.baseBlack
    private var mIsSubtitleCentered: Boolean = false
    private var mIsTitleCentered: Boolean = false
    private var mSubTitle: CharSequence = ""
    private var mTitle: CharSequence = ""

    private lateinit var toolbar: MaterialToolbar

    init {
        attrs?.let { extractAttributes(it) }
        initView()
    }

    private fun extractAttributes(attrs: AttributeSet) {
        val typedArray = _context.obtainStyledAttributes(attrs, R.styleable.KanzanToolbarComponent)
        mNavColor = typedArray.getResourceId(R.styleable.KanzanToolbarComponent_ktc_navColor, mNavColor)
        mNavBackgroundColor = typedArray.getResourceId(R.styleable.KanzanToolbarComponent_ktc_navBackgroundColor, mNavBackgroundColor)
        mNavigationIcon = typedArray.getResourceId(R.styleable.KanzanToolbarComponent_ktc_navigationIcon, mNavigationIcon)
        mSubTitleFontStyle = typedArray.getResourceId(R.styleable.KanzanToolbarComponent_ktc_subTitleFontStyle, mSubTitleFontStyle)
        mTitleFontStyle = typedArray.getResourceId(R.styleable.KanzanToolbarComponent_ktc_subTitleFontStyle, mTitleFontStyle)
        mSubTitleColor = typedArray.getResourceId(R.styleable.KanzanToolbarComponent_ktc_subTitleColor, mSubTitleColor)
        mTitleColor = typedArray.getResourceId(R.styleable.KanzanToolbarComponent_ktc_titleColor, mTitleColor)
        mIsSubtitleCentered = typedArray.getBoolean(R.styleable.KanzanToolbarComponent_ktc_isSubtitleCentered, mIsSubtitleCentered)
        mIsTitleCentered = typedArray.getBoolean(R.styleable.KanzanToolbarComponent_ktc_isTitleCentered, mIsTitleCentered)
        mSubTitle = typedArray.getString(R.styleable.KanzanToolbarComponent_ktc_subTitle) ?: mSubTitle
        mTitle = typedArray.getString(R.styleable.KanzanToolbarComponent_ktc_title) ?: mTitle
        typedArray.recycle()
    }

    private fun initView() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.layout_toolbar, this).apply {
            toolbar = findViewById(R.id.toolbar)
        }

        set()
    }

    fun set(
        _title: CharSequence = mTitle,
        _subtitle: CharSequence = mSubTitle,
        _isTitleCentered: Boolean = mIsTitleCentered,
        _isSubtitleCentered: Boolean = mIsSubtitleCentered,
        _titleColor: Int = mTitleColor,
        _subTitleColor: Int = mSubTitleColor,
        _titleFontStyle: Int = mTitleFontStyle,
        _subTitleFontStyle: Int = mSubTitleFontStyle,
        _navigationIcon: Int = mNavigationIcon,
        _navBackgroundColor: Int = mNavBackgroundColor,
        _navColor: Int = mNavColor,
    ) = with(toolbar) {
        mTitle = _title
        mSubTitle = _subtitle
        mIsTitleCentered = _isTitleCentered
        mIsSubtitleCentered = _isSubtitleCentered
        mTitleColor = _titleColor
        mSubTitleColor = _subTitleColor
        mTitleFontStyle = _titleFontStyle
        mSubTitleFontStyle = _subTitleFontStyle
        mNavigationIcon = _navigationIcon
        mNavBackgroundColor = _navBackgroundColor
        mNavColor = _navColor

        title = mTitle
        isTitleCentered = mIsTitleCentered
        setTitleTextAppearance(_context, _titleFontStyle)
        setTitleTextColor(ContextCompat.getColor(_context, _titleColor))

        subtitle = _subtitle
        isSubtitleCentered = _isSubtitleCentered
        setSubtitleTextAppearance(_context, _subTitleFontStyle)
        setSubtitleTextColor(ContextCompat.getColor(_context, _subTitleColor))

        navigationIcon = ContextCompat.getDrawable(_context, _navigationIcon)
        setNavigationIconTint(ContextCompat.getColor(_context, _navColor))
        setBackgroundColor(ContextCompat.getColor(_context, _navBackgroundColor))
    }

    fun setSupportActionBar(activity: AppCompatActivity) {
        activity.setSupportActionBar(toolbar)
    }

    fun setNavigationOnClickListener(onClickListener: OnClickListener) = with(toolbar) {
        mNavListener = onClickListener

        setNavigationOnClickListener(mNavListener)
    }
}
