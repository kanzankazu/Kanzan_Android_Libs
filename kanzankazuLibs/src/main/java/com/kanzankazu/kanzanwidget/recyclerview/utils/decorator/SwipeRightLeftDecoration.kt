package com.kanzankazu.kanzanwidget.recyclerview.utils.decorator

import android.graphics.Canvas
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

class SwipeRightLeftDecoration(recyclerView: RecyclerView, private val drawObject: DrawObject, private val listener: Listener) {
    init {
        val itemTouchHelperCallback = setItemTouchHelperCallback()
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private fun setItemTouchHelperCallback(): ItemTouchHelper.SimpleCallback {
        return object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                listener.onSwipe(viewHolder, direction)
            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                if (drawObject.a()) {
                    val builder = RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    //builder.addBackgroundColor(0)
                    //builder.addActionIcon(0)
                    //builder.setActionIconTint(0)
                    if (drawObject.rightBackgroundColor != 0) builder.addSwipeRightBackgroundColor(drawObject.rightBackgroundColor)
                    if (drawObject.rightActionIcon != 0) builder.addSwipeRightActionIcon(drawObject.rightActionIcon)
                    if (drawObject.rightActionIconTint != 0) builder.setSwipeRightActionIconTint(drawObject.rightActionIconTint)
                    if (drawObject.rightLabel != "") builder.addSwipeRightLabel(drawObject.rightLabel)
                    if (drawObject.rightLabelColor != 0) builder.setSwipeRightLabelColor(drawObject.rightLabelColor)
                    //builder.setSwipeRightLabelTextSize(0)
                    //builder.setSwipeRightLabelTypeface(0)
                    if (drawObject.leftBackgroundColor != 0) builder.addSwipeLeftBackgroundColor(drawObject.leftBackgroundColor)
                    if (drawObject.leftActionIcon != 0) builder.addSwipeLeftActionIcon(drawObject.leftActionIcon)
                    if (drawObject.leftActionIconTint != 0) builder.setSwipeLeftActionIconTint(drawObject.leftActionIconTint)
                    if (drawObject.leftLabel != "") builder.addSwipeLeftLabel(drawObject.leftLabel)
                    if (drawObject.leftLabelColor != 0) builder.setSwipeLeftLabelColor(drawObject.leftLabelColor)
                    //builder.setSwipeLeftLabelTextSize(0)
                    //builder.setSwipeLeftLabelTypeface(0)
                    //builder.setIconHorizontalMargin(0)
                    //builder.setIconHorizontalMargin(0)
                    val create = builder.create()
                    create.decorate()
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }
    }

    data class DrawObject(
        @DrawableRes val rightActionIcon: Int = 0,
        @ColorRes val rightActionIconTint: Int = 0,
        @ColorRes val rightBackgroundColor: Int = 0,
        @ColorRes val rightLabelColor: Int = 0,
        val rightLabel: String = "",

        @DrawableRes val leftActionIcon: Int = 0,
        @ColorRes val leftActionIconTint: Int = 0,
        @ColorRes val leftBackgroundColor: Int = 0,
        @ColorRes val leftLabelColor: Int = 0,
        val leftLabel: String = "",
    ) {
        fun a(): Boolean {
            return rightBackgroundColor != 0 ||
                    rightActionIcon != 0 ||
                    rightActionIconTint != 0 ||
                    rightLabel != "" ||
                    rightLabelColor != 0 ||
                    leftBackgroundColor != 0 ||
                    leftActionIcon != 0 ||
                    leftActionIconTint != 0 ||
                    leftLabel != "" ||
                    leftLabelColor != 0
        }
    }

    interface Listener {
        /**
         * @see
        try {
        val position = viewHolder.adapterPosition
        val item: String = mAdapter.removeItem(position)

        val snackBar = Snackbar.make(viewHolder.itemView, "Item " + (if (direction == ItemTouchHelper.RIGHT) "deleted" else "archived") + ".", Snackbar.LENGTH_LONG)
        snackBar.setAction(R.string.cancel) {
        try {
        mAdapter.addItem(item, position)
        } catch (e: Exception) {
        Log.e("MainActivity", e.message)
        }}
        snackBar.show()
        } catch (e: Exception) {
        Log.e("MainActivity", e.message)
        }*/
        fun onSwipe(viewHolder: RecyclerView.ViewHolder, direction: Int)
    }
}