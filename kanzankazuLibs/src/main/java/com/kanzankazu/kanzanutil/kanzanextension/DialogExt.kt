package com.kanzankazu.kanzanutil.kanzanextension

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import com.kanzankazu.kanzanutil.kanzanextension.type.debugMessageDebug

fun List<String>.setupOptionItemListDialog(context: Context, onSelectItem: (item: String, position: Int) -> Unit = { _, _ -> }) {
    toTypedArray().setupOptionItemListDialog(context, onSelectItem)
}

fun ArrayList<String>.setupOptionItemListDialog(context: Context, onSelectItem: (item: String, position: Int) -> Unit = { _, _ -> }) {
    toTypedArray().setupOptionItemListDialog(context, onSelectItem)
}

fun Array<String>.setupOptionItemListDialog(context: Context, onSelectItem: (item: String, position: Int) -> Unit = { _, _ -> }) {
    " - setupOptionItemListDialog".debugMessageDebug()
    val chooseImageDialog = AlertDialog.Builder(context)
    chooseImageDialog.setItems(this) { _: DialogInterface?, i: Int -> onSelectItem(this[i].trim(), i) }
    chooseImageDialog.show()
}
