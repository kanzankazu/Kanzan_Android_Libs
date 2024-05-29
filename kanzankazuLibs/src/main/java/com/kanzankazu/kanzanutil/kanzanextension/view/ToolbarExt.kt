package com.kanzankazu.kanzanutil.kanzanextension.view

import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.android.material.appbar.MaterialToolbar
import com.kanzankazu.R

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
