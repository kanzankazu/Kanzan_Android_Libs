package com.kanzankazu.kanzanutil.kanzanextension

import android.content.Context
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import androidx.annotation.MenuRes

fun MenuItem.visible() {
    isVisible = true
}

fun MenuItem.gone() {
    isVisible = false
}

fun MenuItem.visibleView(b: Boolean) {
    isVisible = b
}

fun Context.setupPopupMenu(targetView: View?, @MenuRes menuRes: Int, listener: (MenuItem) -> Unit) {
    val popup = PopupMenu(this, targetView)
    popup.setOnMenuItemClickListener {
        listener(it)
        false
    }
    popup.inflate(menuRes)
    popup.show()
}

fun Context.setupPopupMenu(targetView: View?, arrayList: ArrayList<String>, listener: (MenuItem) -> Unit) {
    val popup = PopupMenu(this, targetView)
    popup.setOnMenuItemClickListener {
        Log.d("Lihat KanzanKazu", "setupPopupMenu  ${it.groupId}")
        Log.d("Lihat KanzanKazu", "setupPopupMenu  ${it.itemId}")
        listener(it)
        false
    }
    arrayList.forEachIndexed { index, s -> popup.menu.add(Menu.NONE, index, Menu.NONE, s) }
    popup.show()
}
