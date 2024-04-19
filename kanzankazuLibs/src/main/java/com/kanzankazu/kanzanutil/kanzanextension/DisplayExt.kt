@file:Suppress("DEPRECATION")

package com.kanzankazu.kanzanutil.kanzanextension

import android.R
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.graphics.Point
import android.os.Build
import android.util.TypedValue
import android.view.Display
import android.view.WindowManager
import java.lang.reflect.InvocationTargetException
import kotlin.math.roundToInt

private var widthDisplay = 0
private var heightDisplay = 0

fun dpToPx(dp: Int): Int {
    return (dp * Resources.getSystem().displayMetrics.density).toInt()
}

fun dpToPx(dp: Float): Int {
    val metrics = Resources.getSystem().displayMetrics
    val px = dp * (metrics.densityDpi / 160f)
    return px.roundToInt()
}

fun pxToDp(px: Int): Int {
    return (px / Resources.getSystem().displayMetrics.density).toInt()
}

fun getDisplaySize(activity: Activity) {
    val display = activity.windowManager.defaultDisplay
    val size = Point()
    display.getSize(size)
    widthDisplay = size.x
    heightDisplay = size.y
}

fun getWidthDisplayDevice(): Int {
    return widthDisplay
}

/*REAL FULL DEVICE*/
fun getRealHeightDisplay(): Int {
    println("HEIGHT REAL = $heightDisplay")
    return heightDisplay
}

/*NON REAL - APP ONLY*/
fun getNonRealHeightDisplay(activity: Activity): Int {
    println("HEIGHT NON REAL = " + (heightDisplay - getStatusBarHeight(activity)))
    return heightDisplay - getStatusBarHeight(activity)
}

private fun getToolbarHeight(mActivity: Activity): Int {
    val toolbarHeight = intArrayOf(R.attr.actionBarSize)
    val indexOfAttrTextSize = 0
    val typedValue = TypedValue()
    val typedArray = mActivity.obtainStyledAttributes(typedValue.data, toolbarHeight)
    val height = typedArray.getDimensionPixelSize(indexOfAttrTextSize, -1)
    typedArray.recycle()
    return height
}

fun getDisplayWithoutToolbarHeight(activity: Activity): Int {
    return getNonRealHeightDisplay(activity) - getToolbarHeight(activity)
}

fun getNavBarHeight(activity: Activity): Int {
    val resources: Resources = activity.resources
    val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
    return if (resourceId > 0) {
        resources.getDimensionPixelSize(resourceId)
    } else {
        0
    }
}

fun getStatusBarHeight(activity: Activity): Int {
    val resources: Resources = activity.resources
    val resourceId = activity.resources.getIdentifier("status_bar_height", "dimen", "android")
    return if (resourceId > 0) {
        resources.getDimensionPixelSize(resourceId)
    } else {
        0
    }
}

fun getNavigationBarSize(context: Context): Point? {
    val appUsableSize: Point = getAppUsableScreenSize(context)
    val realScreenSize: Point = getRealScreenSize(context)
    // navigation bar on the side
    if (appUsableSize.x < realScreenSize.x) {
        return Point(realScreenSize.x - appUsableSize.x, appUsableSize.y)
    }
    // navigation bar at the bottom
    return if (appUsableSize.y < realScreenSize.y) {
        Point(appUsableSize.x, realScreenSize.y - appUsableSize.y)
    } else Point()
    // navigation bar is not present
}

fun getAppUsableScreenSize(context: Context): Point {
    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display = windowManager.defaultDisplay
    val size = Point()
    display.getSize(size)
    return size
}

@SuppressLint("ObsoleteSdkInt")
fun getRealScreenSize(context: Context): Point {
    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display = windowManager.defaultDisplay
    val size = Point()
    if (Build.VERSION.SDK_INT >= 17) {
        display.getRealSize(size)
    } else if (Build.VERSION.SDK_INT >= 14) {
        try {
            size.x = Display::class.java.getMethod("getRawWidth").invoke(display) as Int
            size.y = Display::class.java.getMethod("getRawHeight").invoke(display) as Int
        } catch (e: IllegalAccessException) {
        } catch (e: InvocationTargetException) {
        } catch (e: NoSuchMethodException) {
        }
    }
    return size
}