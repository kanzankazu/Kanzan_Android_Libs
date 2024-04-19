package com.kanzankazu.kanzanwidget.toast

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.kanzankazu.R

class MyUtils {
    companion object {
        fun showBottomToTopToast(
            context: Context,
            message: String,
            duration: Int,
        ) {
            // Inflate layout toast
            val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view: View = inflater.inflate(R.layout.toast_layout, null)

            // Atur teks pada TextView di dalam toast
            val toastText: TextView = view.findViewById(R.id.toastText)
            toastText.text = message

            // Membuat objek animasi untuk animasi muncul dari bawah
            val slideUp = TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0f,
                Animation.RELATIVE_TO_PARENT, 0f,
                Animation.RELATIVE_TO_PARENT, 10f,
                Animation.RELATIVE_TO_PARENT, 0f
            )
            slideUp.duration = 500

            // Set animasi pada layout toast
            val toastLayout: LinearLayout = view.findViewById(R.id.toastLayout)
            toastLayout.startAnimation(slideUp)

            // Membuat toast dan mengatur posisi pada bagian bawah
            val toast = Toast(context)
            toast.view = view
            toast.setGravity(Gravity.BOTTOM, 0, 0)
            toast.duration = duration
            toast.show()

            // Menutup toast dengan animasi ke bawah setelah selesai
            slideUp.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}
                override fun onAnimationRepeat(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {
                    val slideDown = TranslateAnimation(
                        Animation.RELATIVE_TO_PARENT, 0f,
                        Animation.RELATIVE_TO_PARENT, 0f,
                        Animation.RELATIVE_TO_PARENT, 0f,
                        Animation.RELATIVE_TO_PARENT, 10f
                    )
                    slideDown.duration = 500
                    toastLayout.startAnimation(slideDown)
                }
            })
        }
    }
}
