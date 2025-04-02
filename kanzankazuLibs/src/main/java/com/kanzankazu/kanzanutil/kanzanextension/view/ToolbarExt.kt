package com.kanzankazu.kanzanutil.kanzanextension.view

import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.android.material.appbar.MaterialToolbar
import com.kanzankazu.R

/**
 * Configures the MaterialToolbar with the specified parameters for title, subtitle, styles, and navigation.
 *
 * @param _title The main title to be displayed in the toolbar.
 * @param _subtitle The subtitle to be displayed in the toolbar. Defaults to an empty string.
 * @param _isTitleCentered Boolean flag to center the title. Defaults to `false`.
 * @param _isSubtitleCentered Boolean flag to center the subtitle. Defaults to `false`.
 * @param _titleColor The color resource of the title text. Defaults to `R.color.baseBlack`.
 * @param _subTitleColor The color resource of the subtitle text. Defaults to `R.color.baseBlack`.
 * @param _titleFontStyle The style resource ID for the title text appearance. Defaults to `R.style.TvStandart`.
 * @param _SubTitleFontStyle The style resource ID for the subtitle text appearance. Defaults to `R.style.TvStandart`.
 * @param _navigationIcon The drawable resource ID for the navigation icon. Defaults to `R.drawable.ic_arrow_head_left`.
 * @param _navBackgroundColor The color resource ID for the navigation background color.
 * @param _navColor The color resource ID for the navigation icon tint. Defaults to `R.color.baseBlack`.
 * @param _navListener The click listener to be invoked when the navigation icon is clicked.
 *
 * Example:
 * ```kotlin
 * toolbar.setupToolbar(
 *     _title = "Main Title",
 *     _subtitle = "Subtitle",
 *     _isTitleCentered = true,
 *     _isSubtitleCentered = false,
 *     _titleColor = R.color.white,
 *     _subTitleColor = R.color.gray,
 *     _titleFontStyle = R.style.TitleFontStyle,
 *     _SubTitleFontStyle = R.style.SubtitleFontStyle,
 *     _navigationIcon = R.drawable.ic_back,
 *     _navBackgroundColor = R.color.toolbarBackground,
 *     _navColor = R.color.white,
 *     _navListener = View.OnClickListener {
 *         // Handle navigation click
 *     }
 * )
 * ```
 */
fun MaterialToolbar.setupToolbar(
    _title: CharSequence,
    _subtitle: CharSequence = "",
    _isTitleCentered: Boolean = false,
    _isSubtitleCentered: Boolean = false,
    _titleColor: Int = R.color.baseBlack,
    _subTitleColor: Int = R.color.baseBlack,
    _titleFontStyle: Int = R.style.TvStandart,
    _SubTitleFontStyle: Int = R.style.TvStandart,
    _navigationIcon: Int = R.drawable.ic_arrow_head_left,
    _navBackgroundColor: Int,
    _navColor: Int = R.color.baseBlack,
    _navListener: View.OnClickListener,
) {
    title = _title
    isTitleCentered = _isTitleCentered
    setTitleTextAppearance(this.context, _titleFontStyle)
    setTitleTextColor(ContextCompat.getColor(this.context, _titleColor))

    subtitle = _subtitle
    isSubtitleCentered = _isSubtitleCentered
    setSubtitleTextAppearance(this.context, _SubTitleFontStyle)
    setSubtitleTextColor(ContextCompat.getColor(this.context, _subTitleColor))

    navigationIcon = ContextCompat.getDrawable(this.context, _navigationIcon)
    setNavigationIconTint(ContextCompat.getColor(this.context, _navColor))
    setBackgroundColor(ContextCompat.getColor(this.context, _navBackgroundColor))
    setNavigationOnClickListener(_navListener)
}

/**
 * Configures the `Toolbar` with provided parameters such as title, subtitle, text appearance, colors, and navigation listener.
 *
 * @param mTitle The title text to be displayed on the Toolbar.
 * @param mSubtitle The subtitle text to be displayed on the Toolbar. Defaults to an empty string.
 * @param mIsTitleCentered A Boolean flag to indicate whether the title should be centered. Defaults to `false`.
 * @param mIsSubtitleCentered A Boolean flag to indicate whether the subtitle should be centered. Defaults to `false`.
 * @param mTitleColor The color resource ID for the title text. Defaults to `R.color.baseBlack`.
 * @param mSubTitleColor The color resource ID for the subtitle text. Defaults to `R.color.baseBlack`.
 * @param mNavColor The color resource ID for the navigation icon. Defaults to `R.color.baseBlack`.
 * @param mTitleFontStyle The style resource ID for the title text appearance. Defaults to `R.style.TvStandart`.
 * @param mSubTitleFontStyle The style resource ID for the subtitle text appearance. Defaults to `R.style.TvStandart`.
 * @param mListener A `View.OnClickListener` to handle navigation icon clicks.
 *
 * Example:
 * ```kotlin
 * val toolbar: Toolbar = findViewById(R.id.toolbar)
 * toolbar.setupToolbar(
 *     mTitle = "Main Title",
 *     mSubtitle = "Subtitle",
 *     mIsTitleCentered = true,
 *     mTitleColor = R.color.primaryColor,
 *     mSubTitleColor = R.color.secondaryColor,
 *     mListener = View.OnClickListener {
 *         // Handle navigation icon click
 *     }
 * )
 * ```
 */
fun Toolbar.setupToolbar(
    mTitle: CharSequence,
    mSubtitle: CharSequence = "",
    mIsTitleCentered: Boolean = false,
    mIsSubtitleCentered: Boolean = false,
    mTitleColor: Int = R.color.baseBlack,
    mSubTitleColor: Int = R.color.baseBlack,
    mNavColor: Int = R.color.baseBlack,
    mTitleFontStyle: Int = R.style.TvStandart,
    mSubTitleFontStyle: Int = R.style.TvStandart,
    mListener: View.OnClickListener,
) {
    title = mTitle
    setTitleTextAppearance(this.context, mTitleFontStyle)
    setTitleTextColor(ContextCompat.getColor(this.context, mTitleColor))

    subtitle = mSubtitle
    setSubtitleTextAppearance(this.context, mSubTitleFontStyle)
    setSubtitleTextColor(ContextCompat.getColor(this.context, mSubTitleColor))

    setNavigationOnClickListener(mListener)
}
