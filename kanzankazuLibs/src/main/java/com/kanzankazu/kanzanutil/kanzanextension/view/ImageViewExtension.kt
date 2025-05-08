@file:Suppress("DEPRECATION")

package com.kanzankazu.kanzanutil.kanzanextension.view

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.transition.AutoTransition
import android.transition.TransitionManager
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
import com.kanzankazu.kanzanutil.kanzanextension.type.debugMessageError
import net.cachapa.expandablelayout.ExpandableLayout

/**
 * A constant representing the resource ID of a placeholder image used in the application.
 * This drawable is typically utilized as a fallback or default image when no other image is available.
 *
 * @property placeholderImage The integer resource ID referencing the default placeholder image.
 * The default value points to `R.drawable.ic_launcher`.
 *
 * Example:
 * ```kotlin
 * // Usage in an ImageView to set a default placeholder
 * imageView.setImageResource(placeholderImage)
 * ```
 */
private val placeholderImage: Int = R.drawable.ic_launcher

/**
 * Creates and returns a standard `RequestOptions` object with a placeholder image set.
 * This method simplifies the creation of a `RequestOptions` instance used for Glide image loading,
 * allowing a custom placeholder resource to be specified.
 *
 * @param placeholder The resource ID of the placeholder image to be used. Defaults to `placeholderImage`.
 * @return A configured `RequestOptions` object with the specified placeholder image set.
 *
 * Example:
 * ```kotlin
 * val requestOptions = requestOptionStandart(R.drawable.default_placeholder)
 * Glide.with(context)
 *     .load(imageUrl)
 *     .apply(requestOptions)
 *     .into(imageView)
 * ```
 */
@SuppressLint("CheckResult")
fun requestOptionStandart(placeholder: Int = placeholderImage): RequestOptions {
    val requestOptions = RequestOptions()
    requestOptions.placeholder(placeholder)
    return requestOptions
}

/**
 * Configures and returns a `RequestOptions` object with specific settings: no disk caching,
 * no memory caching, and a specified placeholder image.
 * This setup is commonly used for loading images with Glide where caching is not desired.
 *
 * @param placeholder The resource ID of the image to display as a placeholder while the actual image is being loaded. Defaults to `placeholderImage
 * `.
 * @return A `RequestOptions` instance configured with no cache and the specified placeholder image.
 *
 * Example:
 * ```kotlin
 * val requestOptions = requestOptionStandartNoSaveCache(R.drawable.default_placeholder)
 * Glide.with(context)
 *     .load(imageUrl)
 *     .apply(requestOptions)
 *     .into(imageView)
 * ```
 */
@SuppressLint("CheckResult")
fun requestOptionStandartNoSaveCache(placeholder: Int = placeholderImage): RequestOptions {
    val requestOptions = RequestOptions()
    requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE)
    requestOptions.skipMemoryCache(true)
    requestOptions.placeholder(placeholder)
    return requestOptions
}

/**
 * Loads an image from a given URL into an ImageView using the Glide library, with options for caching and placeholder.
 *
 * @param url The URL of the image to be loaded.
 * @param isSaveCache Boolean flag indicating whether the image should be cached. Defaults to `true`.
 * @param placeholder The resource ID of the placeholder image to display while the main image is loading. Defaults to `placeholderImage`.
 *
 * Example:
 * ```kotlin
 * imageView.loadImage(
 *     url = "https://example.com/image.jpg",
 *     isSaveCache = true,
 *     placeholder = R.drawable.placeholder
 * )
 * ```
 */
fun ImageView.loadImage(url: String, isSaveCache: Boolean = true, placeholder: Int = placeholderImage) {
    try {
        Glide.with(context)
            .load(url)
            .apply(if (isSaveCache) requestOptionStandart(placeholder) else requestOptionStandartNoSaveCache(placeholder))
            .into(this)
    } catch (e: Exception) {
        e.debugMessageError(" - loadImage")
    }
}

/**
 * Loads an image into an `ImageView` using Glide, applying caching options and a placeholder as specified.
 *
 * @param url The drawable resource ID of the image to load.
 * @param isSaveCache A boolean flag indicating whether to enable caching. Defaults to `true`.
 * @param placeholder The drawable resource ID of the placeholder image displayed while the image loads. Defaults to `placeholderImage`.
 *
 * Example:
 * ```kotlin
 * val imageView: ImageView = findViewById(R.id.myImageView)
 * imageView.loadImage(R.drawable.sample_image, isSaveCache = false, placeholder = R.drawable.loading_placeholder)
 * ```
 */
fun ImageView.loadImage(@DrawableRes url: Int, isSaveCache: Boolean = true, placeholder: Int = placeholderImage) {
    try {
        Glide.with(context)
            .load(url)
            .apply(if (isSaveCache) requestOptionStandart(placeholder) else requestOptionStandartNoSaveCache(placeholder))
            .into(this)
    } catch (e: Exception) {
        e.debugMessageError(" - loadImage1")
    }
}

/**
 * Loads an image into the `ImageView` with rounded corners applied to all corners.
 * The method uses Glide to load the image and offers options for caching and placeholders.
 *
 * @param url The resource identifier of the image to be loaded.
 * @param round The radius for rounding the corners of the image.
 * @param isSaveCache A Boolean indicating whether to enable caching of the image. Defaults to `true`.
 * @param placeholder The resource identifier for the placeholder image to be displayed while loading. Defaults to `placeholderImage`.
 *
 * Example:
 * ```kotlin
 * imageView.loadImageRoundedAll(
 *     url = R.drawable.example_image,
 *     round = 16,
 *     isSaveCache = false,
 *     placeholder = R.drawable.placeholder_image
 * )
 * ```
 */
fun ImageView.loadImageRoundedAll(url: Int, round: Int, isSaveCache: Boolean = true, placeholder: Int = placeholderImage) {
    var requestOptions = RequestOptions()
    requestOptions = requestOptions.transforms(CenterCrop(), RoundedCornersTransformation(round, 0, RoundedCornersTransformation.CornerType.ALL))
    Glide.with(context)
        .load(url)
        .apply(if (isSaveCache) requestOptionStandart(placeholder) else requestOptionStandartNoSaveCache(placeholder))
        .apply(requestOptions)
        .into(this)
}

/**
 * Loads an image into an ImageView with rounded corners and optional caching and placeholder support.
 *
 * @param url The URL of the image to load.
 * @param round The corner radius for rounding the image, in pixels.
 * @param isSaveCache Boolean value determining whether to enable caching of the loaded image. Defaults to `true`.
 * @param placeholder The resource ID of the placeholder image to display while the main image is loading. Defaults to `placeholderImage`.
 *
 * Example:
 * ```kotlin
 * imageView.loadImageRoundedAll(
 *     url = "https://example.com/image.jpg",
 *     round = 16,
 *     isSaveCache = false,
 *     placeholder = R.drawable.placeholder_image
 * )
 * ```
 */
fun ImageView.loadImageRoundedAll(url: String, round: Int, isSaveCache: Boolean = true, placeholder: Int = placeholderImage) {
    var requestOptions = RequestOptions()
    requestOptions = requestOptions.transforms(CenterCrop(), RoundedCornersTransformation(round, 0, RoundedCornersTransformation.CornerType.ALL))
    Glide.with(context)
        .load(url)
        .apply(if (isSaveCache) requestOptionStandart(placeholder) else requestOptionStandartNoSaveCache(placeholder))
        .apply(requestOptions)
        .into(this)
}

/**
 * Loads an image from the specified URL into the `ImageView` with the top corners rounded.
 * The image can optionally be cached and a placeholder can be displayed during loading.
 *
 * @param url The URL of the image to load.
 * @param round The radius (in pixels) to round the top corners of the image.
 * @param isSaveCache A flag to determine whether the image should be saved to the cache. Defaults to `true`.
 * @param placeholder The resource ID of the placeholder image to display while the image is being loaded. Defaults to `placeholderImage`.
 *
 * Example:
 * ```kotlin
 * val imageView: ImageView = findViewById(R.id.imageView)
 * imageView.loadImageRoundedTop(
 *     url = "https://example.com/image.jpg",
 *     round = 16,
 *     isSaveCache = false,
 *     placeholder = R.drawable.placeholder_image
 * )
 * ```
 */
fun ImageView.loadImageRoundedTop(url: String, round: Int, isSaveCache: Boolean = true, placeholder: Int = placeholderImage) {
    var requestOptions = RequestOptions()
    requestOptions = requestOptions.transforms(CenterCrop(), RoundedCornersTransformation(round, 0, RoundedCornersTransformation.CornerType.TOP))
    Glide.with(context)
        .load(url)
        .apply(if (isSaveCache) requestOptionStandart(placeholder) else requestOptionStandartNoSaveCache(placeholder))
        .apply(requestOptions)
        .into(this)
}

/**
 * Loads an image from the given URL into an ImageView with a circular crop transformation.
 * Allows caching behavior and placeholder customization.
 *
 * @param url The resource or URL of the image to be loaded.
 * @param isSaveCache A flag to determine whether the image should be cached in memory and disk. Defaults to `true`.
 * @param placeholder The resource ID for the placeholder image to be displayed while the target image loads. Defaults to `placeholderImage`.
 *
 * Example:
 * ```kotlin
 * imageView.loadImageRounded(
 *     url = "https://example.com/image.jpg",
 *     isSaveCache = false,
 *     placeholder = R.drawable.placeholder
 * )
 * ```
 */
fun ImageView.loadImageRounded(url: Int, isSaveCache: Boolean = true, placeholder: Int = placeholderImage) {
    Glide.with(context)
        .load(url)
        .apply(if (isSaveCache) requestOptionStandart(placeholder) else requestOptionStandartNoSaveCache(placeholder))
        .apply(RequestOptions.circleCropTransform())
        .into(this)
}

/**
 * Loads an image into the ImageView with a circular (rounded) transformation using Glide.
 * It allows the configuration of caching strategy and customization of a placeholder image.
 *
 * @param url The `Uri` of the image to load into the `ImageView`.
 * @param isSaveCache A Boolean flag indicating whether the image should be cached. Defaults to `true`.
 * @param placeholder An integer representing the resource ID of the placeholder image displayed while the image loads. Defaults to `placeholderImage
 * `.
 *
 * Example:
 * ```kotlin
 * imageView.loadImageRounded(
 *     url = Uri.parse("https://example.com/image.jpg"),
 *     isSaveCache = true,
 *     placeholder = R.drawable.placeholder_image
 * )
 * ```
 */
fun ImageView.loadImageRounded(url: Uri, isSaveCache: Boolean = true, placeholder: Int = placeholderImage) {
    Glide.with(context)
        .load(url)
        .apply(if (isSaveCache) requestOptionStandart(placeholder) else requestOptionStandartNoSaveCache(placeholder))
        .apply(RequestOptions.circleCropTransform())
        .into(this)
}

/**
 * Loads an image from a specified URL into an ImageView with a rounded/circular transformation.
 * The image can optionally be cached based on the `isSaveCache` parameter, and a placeholder image is displayed while loading.
 *
 * @param url The URL of the image to be loaded.
 * @param isSaveCache Boolean flag indicating whether the image should be cached. Defaults to `true`.
 * @param placeholder The resource ID of the placeholder image to display while the image is loading. Defaults to `placeholderImage`.
 *
 * Example:
 * ```kotlin
 * val imageView: ImageView = findViewById(R.id.imageView)
 * imageView.loadImageRounded(
 *     url = "https://example.com/image.jpg",
 *     isSaveCache = false,
 *     placeholder = R.drawable.placeholder
 * )
 * ```
 */
fun ImageView.loadImageRounded(url: String, isSaveCache: Boolean = true, placeholder: Int = placeholderImage) {
    Glide.with(context)
        .load(url)
        .apply(if (isSaveCache) requestOptionStandart(placeholder) else requestOptionStandartNoSaveCache(placeholder))
        .apply(RequestOptions.circleCropTransform())
        .into(this)
}

/**
 * Toggles the visibility and rotation of an `ImageView` and a corresponding `View` (e.g., a `ViewGroup` or `ExpandableLayout`).
 * If the `targetView` is currently hidden or collapsed, this method makes it visible or expands it, and rotates the `ImageView` by -180 degrees.
 * If the `targetView` is visible or expanded, it hides or collapses it, and resets the `ImageView` rotation to 0 degrees.
 * An optional transition animation is also applied when the `targetView` belongs to a `ViewGroup`.
 *
 * @param targetView The `View` to show/hide or expand/collapse. Can be an `ExpandableLayout` or a standard `View`
 * (e.g., `ViewGroup` or any other UI component).
 *
 * Example:
 * ```kotlin
 * val arrowImageView: ImageView = findViewById(R.id.arrow_up_icon)
 * val expandableLayout: ExpandableLayout = findViewById(R.id.expandable_layout)
 *
 * arrowImageView.loadImageIconArrowUpShowHide(expandableLayout)
 * ```
 */
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

/**
 * Rotates an ImageView to indicate an "arrow up" or "arrow down" state based on a boolean flag.
 * When the state is "show" (`true`), the arrow rotates to its default position (0 degrees).
 * When the state is "hide" (`false`), the arrow rotates 180 degrees.
 *
 * @param isShowHide A boolean flag representing the visibility state.
 *                   If `true`, the arrow shows in its default orientation (0 degrees).
 *                   If `false`, the arrow rotates to indicate a hidden state (-180 degrees).
 *
 * Example:
 * ```kotlin
 * val imageView: ImageView = findViewById(R.id.arrowIcon)
 * imageView.loadImageIconArrowUpShowHide(isShowHide = true)  // Arrow points up
 * imageView.loadImageIconArrowUpShowHide(isShowHide = false) // Arrow points down
 * ```
 */
fun ImageView.loadImageIconArrowUpShowHide(isShowHide: Boolean) {
    if (!isShowHide) {
        animate().rotation(-180F).start()
    } else {
        animate().rotation(-0F).start()
    }
}

/**
 * Updates the visibility and content of an `ImageView` based on the provided image URL or default assets.
 * If an `imageUrl` is provided, it loads the image using Glide. If `imageAsset` is specified and valid, it loads the corresponding asset.
 * If neither is valid, it either hides the `ImageView` or loads a default icon.
 *
 * @param context The context required to load resources using Glide.
 * @param imageUrl The URL of the image to be loaded into the `ImageView`. If null or empty, the `imageAsset` parameter is used instead.
 * @param imageAsset The drawable resource ID of the image asset to be displayed if `imageUrl` is null or empty.
 *                   A value of `-1` hides the `ImageView`, while a value of `0` loads the default icon (`android.R.mipmap.sym_def_app_icon`). Defaults
 *  to `0`.
 *
 * Example:
 * ```kotlin
 * val imageView: ImageView = findViewById(R.id.my_image_view)
 * imageView.visibleView(context, "https://example.com/image.jpg")
 *
 * // Using a local asset ID:
 * imageView.visibleView(context, null, R.drawable.my_local_image)
 *
 * // Hiding the ImageView:
 * imageView.visibleView(context, null, -1)
 * ```
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
