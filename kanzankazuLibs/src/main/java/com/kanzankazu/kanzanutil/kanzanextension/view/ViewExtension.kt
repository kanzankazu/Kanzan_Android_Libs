@file:Suppress("DEPRECATION", "unused", "UNUSED_PARAMETER")

package com.kanzankazu.kanzanutil.kanzanextension.view

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.CheckBox
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Switch
import android.widget.TextView
import androidx.annotation.AnimRes
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.annotation.MenuRes
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.core.view.isVisible
import com.kanzankazu.kanzanutil.OnSingleClickListener
import com.kanzankazu.kanzanutil.kanzanextension.type.dpTopx

/**
 * Meng-inflate layout yang diberikan dan mengembalikan view yang telah di-inflate.
 * Layout yang diberikan akan di-inflate dengan menggunakan LayoutInflate yang di dapatkan dari activity.
 * Metode ini berguna jika kita ingin meng-inflate layout di dalam activity dan mengembalikan view yang di-inflate.
 *
 * @param layout resource id dari layout yang ingin di-inflate
 * @return view yang telah di-inflate
 */
fun Activity.inflate(@LayoutRes layout: Int): View {
    return layoutInflater.inflate(layout, null)
}

/**
 * Meng-inflate layout yang diberikan dan mengembalikan view yang telah di-inflate.
 * Layout yang diberikan akan di-inflate dengan menggunakan LayoutInflate yang di dapatkan dari context.
 * Metode ini berguna jika kita ingin meng-inflate layout di dalam view group dan mengembalikan view yang di-inflate.
 *
 * @param layout resource id dari layout yang ingin di-inflate
 * @return view yang telah di-inflate
 */
fun ViewGroup.inflate(@LayoutRes layout: Int): View {
    return LayoutInflater.from(context).inflate(layout, this, false)
}

fun View.visible(anim: Int = 0) {
    if (!isVisible()) {
        if (anim != 0 && isGone()) {
            val loadAnimation = AnimationUtils.loadAnimation(this.context, anim)
            startAnimation(loadAnimation)
        }
        //TransitionManager.beginDelayedTransition(parent as ViewGroup)
        visibility = View.VISIBLE
    }
}

fun View.isVisible(): Boolean = this.visibility == View.VISIBLE

fun View.invisible(anim: Int = 0) {
    if (!isInvisible()) {
        if (anim != 0 && isVisible()) {
            val loadAnimation = AnimationUtils.loadAnimation(this.context, anim)
            startAnimation(loadAnimation)
        }
        //TransitionManager.beginDelayedTransition(parent as ViewGroup)
        visibility = View.INVISIBLE
    }
}

fun View.isInvisible(): Boolean = this.visibility == View.INVISIBLE

fun View.gone(anim: Int = 0) {
    if (!isGone()) {
        if (anim != 0 && isVisible()) {
            val loadAnimation = AnimationUtils.loadAnimation(this.context, anim)
            startAnimation(loadAnimation)
        }
        //TransitionManager.beginDelayedTransition(parent as ViewGroup)
        visibility = View.GONE
    }
}

fun View.isGone(): Boolean = this.visibility == View.GONE

fun View.visibleView(ss: List<String?>, vararg textViews: TextView, isInvisible: Boolean = false): Boolean {
    val stringSize = ss.size
    val tvLength = textViews.size
    return if (stringSize != 0) {
        if (stringSize == tvLength) {
            for (i in ss.indices) {
                val textView = textViews[i]
                textView.text = ss[i]
            }
        }
        visible()
        true
    } else {
        if (isInvisible) invisible() else gone()
        false
    }
}

fun View.visibleView(ss: List<String?>, textView1: TextView, textView2: TextView, isInvisible: Boolean = false): Boolean {
    val stringSize = ss.size
    return if (stringSize in 1..2) {
        textView1.text = ss[0]
        textView2.text = ss[1]
        visible()
        true
    } else {
        if (isInvisible) invisible() else gone()
        false
    }
}

fun View.visibleView(textView: TextView, s: String?, isInvisible: Boolean = false): Boolean {
    return if (!s.isNullOrEmpty()) {
        visible()
        textView.text = s
        true
    } else {
        if (isInvisible) invisible() else gone()
        textView.text = ""
        false
    }
}

fun View.visibleView(b: Boolean, isShowHide: Boolean = true, isInvisible: Boolean = false): Boolean {
    if (b && this is CheckBox) isChecked = b
    else if (b && this is Switch) isChecked = b

    if (isShowHide) visibleView(b)
    return b
}

fun View.visibleView(i: Int, isShowHide: Boolean = true, isInvisible: Boolean = false): Boolean {
    return visibleView(i != 0, isShowHide, isInvisible)
}

fun View.visibleView(s: String?, isShowHide: Boolean = true, isInvisible: Boolean = false): Boolean {
    val b = !s.isNullOrEmpty()
    if (b && this is TextView) {
        text = s
    }

    if (isShowHide) visibleView(b)
    return b
}

fun View.visibleView(sCompare: String?, sForText: String?, isShowHide: Boolean = true, isInvisible: Boolean = false): Boolean {
    val b = !sCompare.isNullOrEmpty()
    if (b && this is TextView) {
        text = sForText
    }

    if (isShowHide) visibleView(b)
    return b
}

fun View.visibleView(
    b: Boolean,
    @AnimRes animShow: Int = 0,
    @AnimRes animHide: Int = 0,
): Boolean {
    return if (b) {
        visible(animShow)
        true
    } else {
        gone(animHide)
        false
    }
}

fun View.visibleView(b: Boolean, isInvisible: Boolean = false): Boolean {
    return if (b) {
        visible()
        true
    } else {
        if (isInvisible) invisible() else gone()
        false
    }
}

fun View.visibleView(vararg views: View) {
    isVisible = views.any { it.isVisible }
}

fun View.visibleView(vararg b: Boolean) {
    isVisible = b.any { it }
}

fun visibleView(vararg views: View): Boolean {
    val mutableListOfVisible = mutableListOf<Boolean>()
    views.forEach { mutableListOfVisible.add(it.isVisible()) }
    return mutableListOfVisible.any { it }
}

fun visibleViews(b: Boolean, vararg views: View, isInvisible: Boolean = false) {
    views.forEach { it.visibleView(b, isInvisible) }
}

fun View.enableView(b: Boolean) {
    when (this) {
        is RadioGroup -> for (i in 0 until childCount) (getChildAt(i) as RadioButton).isEnabled = b
        else -> if (b) {
            isEnabled = true
            isClickable = true
        } else {
            isEnabled = false
            isClickable = false
        }
    }
}

fun enableViews(b: Boolean, vararg views: View) {
    views.forEach { it.enableView(b) }
}

fun View.disable() {
    this.isClickable = false
    this.isEnabled = false
}

fun View.click(listener: () -> Unit) {
    setOnClickListener { listener() }
}

/**0 - 1*/
fun View.fade(value: Float) {
    this.alpha = value
}

fun View.idName(): String? {
    return if (id != -1) resources.getResourceEntryName(id)
    else id.toString()
}

/**
 * Mengatur padding view berdasarkan ukuran dalam satuan dp.
 * Ukuran yang diberikan akan dikonversi menjadi ukuran dalam pixel sebelum digunakan.
 * Nilai default untuk left, top, right, dan bottom adalah 0.
 * @param left padding kiri dalam dp
 * @param top padding atas dalam dp
 * @param right padding kanan dalam dp
 * @param bottom padding bawah dalam dp
 */
fun View.paddingByDp(left: Int = 0, top: Int = 0, right: Int = 0, bottom: Int = 0) {
    setPadding(left.dpTopx(), top.dpTopx(), right.dpTopx(), bottom.dpTopx())
}

/**The sizes passed in onMeasure includes the padding, but not the margin*/
fun View.getHeightView(): Int {
    val layoutParams = layoutParams as ViewGroup.MarginLayoutParams
    return measuredHeight + layoutParams.topMargin + layoutParams.bottomMargin
}

fun View.setOnSingleClickListener(l: View.OnClickListener) {
    setOnClickListener(OnSingleClickListener(l))
}

fun View.setOnSingleClickListener(l: (View) -> Unit) {
    setOnClickListener(OnSingleClickListener(l))
}

fun View.backgroundDrawable(@DrawableRes drawableResource: Int) {
    background = ContextCompat.getDrawable(context, drawableResource)
}

/**
 * Membuat popup menu dan menambahkan listener pada menu item yang di klik.
 * Fungsi ini membuat popup menu dengan resource menu yang diberikan
 * dan menambahkan listener pada menu item yang di klik.
 * Parameter context digunakan untuk membuat popup menu,
 * parameter menuRes digunakan sebagai resource menu yang akan di gunakan,
 * dan parameter listener digunakan sebagai listener yang akan di jalankan
 * ketika menu item di klik.
 * Fungsi ini memanggil setOnSingleClickListener untuk menambahkan listener
 * pada view yang di gunakan sebagai anchor popup menu.
 * Ketika view di klik maka popup menu akan di munculkan dan listener
 * akan di jalankan ketika menu item di klik.
 */
fun View.popUpMenu(context: Context, @MenuRes menuRes: Int, listener: (MenuItem) -> Boolean) {
    setOnSingleClickListener {
        val popupMenu = PopupMenu(context, it)
        popupMenu.setOnMenuItemClickListener { item -> listener(item) }
        popupMenu.menuInflater.inflate(menuRes, popupMenu.menu)
        popupMenu.show()
    }
}
fun View.getViewGroup(): ViewGroup? = this as? ViewGroup

fun ViewGroup.getAllView(): ArrayList<View> {
    val arrayListOf = arrayListOf<View>()
    this.forEach(arrayListOf::add)
    return arrayListOf
}

inline fun <reified T> ViewGroup.getAllViewDetail(): ArrayList<T> {
    val arrayListOf = arrayListOf<T>()
    forEach { if (it is T) arrayListOf.add(it) }
    return arrayListOf
}

inline fun <reified T> ArrayList<View>.getAllViewDetail(): ArrayList<T> {
    val arrayListOf = arrayListOf<T>()
    forEach { if (it is T) arrayListOf.add(it) }
    return arrayListOf
}

fun ArrayList<Triple<View, () -> Unit, ArrayList<(View) -> Boolean>>>.asd(): Boolean {
    val b = arrayListOf<Boolean>()
    forEach { triple ->
        when (triple.first) {
            is EditText -> {
                (triple.first as EditText).also {

                }
            }

            else -> b.add(true)
        }
    }
    return b.all { it }
}
