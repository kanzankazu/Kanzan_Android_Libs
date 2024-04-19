@file:Suppress("DEPRECATION")

package com.kanzankazu.kanzanutil.kanzanextension.view

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.kanzankazu.R
import com.kanzankazu.kanzanutil.RoundedCornersTransformation
import net.cachapa.expandablelayout.ExpandableLayout

private val placeholderImage: Int = R.drawable.ic_launcher

@SuppressLint("CheckResult")
fun requestOptionStandart(placeholder: Int = placeholderImage): RequestOptions {
    val requestOptions = RequestOptions()
    requestOptions.placeholder(placeholder)
    return requestOptions
}

@SuppressLint("CheckResult")
fun requestOptionStandartNoSaveCache(placeholder: Int = placeholderImage): RequestOptions {
    val requestOptions = RequestOptions()
    requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE)
    requestOptions.skipMemoryCache(true)
    requestOptions.placeholder(placeholder)
    return requestOptions
}

fun ImageView.loadImage(url: String, isSaveCache: Boolean = true, placeholder: Int = placeholderImage) {
    try {
        Glide.with(context)
            .load(url)
            .apply(if (isSaveCache) requestOptionStandart(placeholder) else requestOptionStandartNoSaveCache(placeholder))
            .into(this)
    } catch (e: Exception) {
        Log.d("Lihat KanzanKazu", "loadImage ${e.message}")
    }
}

fun ImageView.loadImage(@DrawableRes url: Int, isSaveCache: Boolean = true, placeholder: Int = placeholderImage) {
    try {
        Glide.with(context)
            .load(url)
            .apply(if (isSaveCache) requestOptionStandart(placeholder) else requestOptionStandartNoSaveCache(placeholder))
            .into(this)
    } catch (e: Exception) {
        Log.d("Lihat KanzanKazu", "loadImage ${e.message}")
    }
}

fun ImageView.loadImageRoundedAll(url: Int, round: Int, isSaveCache: Boolean = true, placeholder: Int = placeholderImage) {
    var requestOptions = RequestOptions()
    requestOptions = requestOptions.transforms(CenterCrop(), RoundedCornersTransformation(round, 0, RoundedCornersTransformation.CornerType.ALL))
    Glide.with(context)
        .load(url)
        .apply(if (isSaveCache) requestOptionStandart(placeholder) else requestOptionStandartNoSaveCache(placeholder))
        .apply(requestOptions)
        .into(this)
}

fun ImageView.loadImageRoundedAll(url: String, round: Int, isSaveCache: Boolean = true, placeholder: Int = placeholderImage) {
    var requestOptions = RequestOptions()
    requestOptions = requestOptions.transforms(CenterCrop(), RoundedCornersTransformation(round, 0, RoundedCornersTransformation.CornerType.ALL))
    Glide.with(context)
        .load(url)
        .apply(if (isSaveCache) requestOptionStandart(placeholder) else requestOptionStandartNoSaveCache(placeholder))
        .apply(requestOptions)
        .into(this)
}

fun ImageView.loadImageRoundedTop(url: String, round: Int, isSaveCache: Boolean = true, placeholder: Int = placeholderImage) {
    var requestOptions = RequestOptions()
    requestOptions = requestOptions.transforms(CenterCrop(), RoundedCornersTransformation(round, 0, RoundedCornersTransformation.CornerType.TOP))
    Glide.with(context)
        .load(url)
        .apply(if (isSaveCache) requestOptionStandart(placeholder) else requestOptionStandartNoSaveCache(placeholder))
        .apply(requestOptions)
        .into(this)
}

fun ImageView.loadImageRounded(url: Int, isSaveCache: Boolean = true, placeholder: Int = placeholderImage) {
    Glide.with(context)
        .load(url)
        .apply(if (isSaveCache) requestOptionStandart(placeholder) else requestOptionStandartNoSaveCache(placeholder))
        .apply(RequestOptions.circleCropTransform())
        .into(this)
}

fun ImageView.loadImageRounded(url: Uri, isSaveCache: Boolean = true, placeholder: Int = placeholderImage) {
    Glide.with(context)
        .load(url)
        .apply(if (isSaveCache) requestOptionStandart(placeholder) else requestOptionStandartNoSaveCache(placeholder))
        .apply(RequestOptions.circleCropTransform())
        .into(this)
}

fun ImageView.loadImageRounded(url: String, isSaveCache: Boolean = true, placeholder: Int = placeholderImage) {
    Glide.with(context)
        .load(url)
        .apply(if (isSaveCache) requestOptionStandart(placeholder) else requestOptionStandartNoSaveCache(placeholder))
        .apply(RequestOptions.circleCropTransform())
        .into(this)
}

/** use arrow_up for default image*/
fun ImageView.loadImageIconArrowUpShowHide(targetView: View) {
    if (if (targetView is ExpandableLayout) !targetView.isExpanded else targetView.isGone()) {
        if (targetView is ExpandableLayout) targetView.expand(true) else targetView.visible()
        if (targetView is ViewGroup) TransitionManager.beginDelayedTransition(targetView, AutoTransition())
        animate().rotation(-180F).start()
    } else {
        if (targetView is ExpandableLayout) targetView.collapse(true) else targetView.gone()
        if (targetView is ViewGroup) TransitionManager.beginDelayedTransition(targetView, AutoTransition())
        animate().rotation(-0F).start()
    }
}

fun ImageView.loadImageIconArrowUpShowHide(isShowHide: Boolean) {
    if (!isShowHide) {
        animate().rotation(-180F).start()
    } else {
        animate().rotation(-0F).start()
    }
}

/**
 * @param imageUrl String =  jika tidak kosong atau null akan mengeset ini
 * @param imageAsset Int = jika -1 akan hilang, jika 0 akan mengeset launcher, jika yang lain akan mengeset ini
 */
fun ImageView.visibleView(context: Context, imageUrl: String?, imageAsset: Int = 0) {
    if (!imageUrl.isNullOrEmpty()) {
        Glide.with(context).load(imageUrl).into(this)
    } else {
        /*val layoutParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 500)
        this.layoutParams = layoutParams*/
        when (imageAsset) {
            -1 -> this.gone()
            0 -> Glide.with(context).load(android.R.mipmap.sym_def_app_icon).into(this)
            else -> Glide.with(context).load(imageAsset).into(this)
        }
    }
}
