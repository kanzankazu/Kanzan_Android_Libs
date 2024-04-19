package com.kanzankazu.kanzanutil.kanzanextension

import android.app.PendingIntent
import android.util.TypedValue.COMPLEX_UNIT_SP
import android.view.View
import android.widget.RemoteViews
import androidx.fragment.app.FragmentActivity

fun RemoteViews.visible(idView: Int) = setViewVisibility(idView, View.VISIBLE)

fun RemoteViews.invisible(idView: Int) = setViewVisibility(idView, View.INVISIBLE)

fun RemoteViews.gone(idView: Int) = setViewVisibility(idView, View.GONE)

fun RemoteViews.visibleView(idView: Int, boolean: Boolean) =
    if (boolean) visible(idView) else gone(idView)

fun RemoteViews.setText(idView: Int, text: CharSequence) = setTextViewText(idView, text)

fun RemoteViews.setTextColors(idView: Int, colors: Int) = setTextColor(idView, colors)

fun RemoteViews.setTextSize(idView: Int, size: Float) =
    setTextViewTextSize(idView, COMPLEX_UNIT_SP, size)

fun RemoteViews.setOnClick(idView: Int, pendIntent: PendingIntent) =
    setOnClickPendingIntent(idView, pendIntent)

fun FragmentActivity.makeRemoteView(layoutId: Int) = RemoteViews(this.packageName, layoutId)
