package com.kanzankazu.kanzanutil.animation

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.os.Build
import android.view.View
import android.view.ViewAnimationUtils
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import kotlin.math.sqrt


/**
 *Created by ed on 3/28/18.
 */

private const val animationDuration = 1000.toLong()

fun startCircularRevealAnimation(
    view: View,
    revealSettings: RevealAnimationSettings,
    startColor: Int,
    endColor: Int,
    onFinish: Animator.AnimatorListener? = null,
) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        view.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
            override fun onLayoutChange(v: View?, p1: Int, p2: Int, p3: Int, p4: Int, p5: Int, p6: Int, p7: Int, p8: Int) {
                view.removeOnLayoutChangeListener(this)

                val cx = revealSettings.centerX
                val cy = revealSettings.centerY
                val width = revealSettings.width
                val height = revealSettings.height

                val finalRadius = sqrt((width * width + height * height).toDouble()).toFloat()
                val anim = ViewAnimationUtils.createCircularReveal(v, cx, cy, 0f, finalRadius).setDuration(animationDuration)
                anim.interpolator = FastOutSlowInInterpolator()
                if (onFinish != null) anim.addListener(onFinish)
                anim.start()
                startColorAnimation(view, startColor, endColor, animationDuration.toInt())
            }
        })
    }
}

fun exitCircularRevealAnimation(
    view: View,
    revealSettings: RevealAnimationSettings,
    startColor: Int,
    endColor: Int,
    listener: DismissableAnimation.OnDismissedListener,
) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        val cx = revealSettings.centerX
        val cy = revealSettings.centerY
        val width = revealSettings.width
        val height = revealSettings.height


        val initRadius = sqrt((width * width + height * height).toDouble()).toFloat()
        val anim = ViewAnimationUtils.createCircularReveal(view, cx, cy, initRadius, 0f).setDuration(animationDuration)
        anim.duration = animationDuration
        anim.interpolator = FastOutSlowInInterpolator()
        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                view.visibility = View.GONE
                listener.onDismissed()
            }
        })
        anim.start()
        startColorAnimation(view, startColor, endColor, animationDuration.toInt())
    } else {
        listener.onDismissed()
    }
}


fun startColorAnimation(view: View, startColor: Int, endColor: Int, duration: Int) {
    val anim = ValueAnimator()
    anim.setIntValues(startColor, endColor)
    anim.setEvaluator(ArgbEvaluator())
    anim.addUpdateListener { valueAnimator -> view.setBackgroundColor(valueAnimator.animatedValue as Int) }
    anim.duration = duration.toLong()
    anim.start()
}


