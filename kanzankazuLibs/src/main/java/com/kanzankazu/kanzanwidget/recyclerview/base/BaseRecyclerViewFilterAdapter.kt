@file:Suppress("PackageName", "UNCHECKED_CAST", "KDocUnresolvedReference", "unused", "MemberVisibilityCanBePrivate")

package com.kanzankazu.kanzanwidget.recyclerview.base

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Filter
import android.widget.Filterable
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.kanzankazu.R

abstract class BaseRecyclerViewFilterAdapter<T> : RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    // Variables
    var mainDatas = arrayListOf<T>()
    var swipeListener: SwipeListener<T>? = null
    var moveListener: MoveListener<T>? = null
    private var tempFilterDatas = arrayListOf<T>()
    private var filterModeData: Int = SINGLE_MODE
    private var checkPositionData: Int = 0
    private var lastPosition = -1
    private var recentlyDeletedItem: T? = null
    private var recentlyDeletedPosition: Int = -1

    companion object {
        const val MULTIPLE_MODE = 0
        const val SINGLE_MODE = 1
    }

    // Abstract Methods
    protected abstract fun onCreateView(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
    protected abstract fun onBindView(data: T, position: Int, holder: RecyclerView.ViewHolder)

    // Open Methods
    open fun onMultipleViewType(position: Int, data: T): Int = -1
    open fun onFilterData(row: ArrayList<T>, charString: String): ArrayList<T> = arrayListOf()
    open fun onFilterListener(mainData: ArrayList<T>) {}

    // Overrides
    override fun getItemViewType(position: Int): Int {
        val type = onMultipleViewType(position, mainDatas[position])
        return if (type == -1) super.getItemViewType(position) else type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return onCreateView(parent, viewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        onBindView(mainDatas[position], position, holder)
        setAnimation(holder.itemView, position)
    }

    override fun getItemCount(): Int = mainDatas.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                mainDatas = if (charString.isEmpty()) {
                    tempFilterDatas
                } else {
                    onFilterData(tempFilterDatas, charString).apply {
                        tempFilterDatas = this
                    }
                }
                return FilterResults().apply { values = mainDatas }
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                mainDatas = filterResults.values as ArrayList<T>
                onFilterListener(mainDatas)
                notifyDataSetChanged()
            }
        }
    }

    // Public Methods
    fun setFilterMode(filterMode: Int) {
        this.filterModeData = filterMode
    }

    fun getFilterMode(): Int = filterModeData

    fun setCheckPosition(pos: Int) {
        checkPositionData = pos
    }

    fun getCheckPosition(): Int = checkPositionData

    fun getItemData(position: Int): T = mainDatas[position]

    fun getItemDataS(): ArrayList<T> = mainDatas

    fun setData(dataS: ArrayList<T>) {
        mainDatas = dataS
        tempFilterDatas = dataS
        notifyDataSetChanged()
    }

    fun addAllData(dataList: List<T>) {
        val startPosition = mainDatas.size
        mainDatas.addAll(dataList)
        tempFilterDatas.addAll(dataList)
        notifyItemRangeInserted(startPosition, dataList.size)
    }

    fun addDataAt(data: T, position: Int) {
        mainDatas.add(position, data)
        tempFilterDatas.add(position, data)
        notifyItemInserted(position)
    }

    fun updateDataAt(position: Int, newData: T) {
        if (position in mainDatas.indices) {
            mainDatas[position] = newData
            tempFilterDatas[position] = newData
            notifyItemChanged(position)
        }
    }

    fun removeDataAt(position: Int) {
        mainDatas.removeAt(position)
        tempFilterDatas.removeAt(position)
        notifyItemRemoved(position)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearData() {
        mainDatas.clear()
        tempFilterDatas.clear()
        notifyDataSetChanged()
    }

    fun refreshItemAt(position: Int) {
        if (position in mainDatas.indices) {
            notifyItemChanged(position)
        }
    }

    fun findPosition(predicate: (T) -> Boolean): Int? {
        return mainDatas.indexOfFirst(predicate).takeIf { it >= 0 }
    }

    fun getSelectedItem(): T? {
        return if (filterModeData == SINGLE_MODE && checkPositionData in mainDatas.indices) {
            mainDatas[checkPositionData]
        } else null
    }

    fun undoDelete() {
        if (recentlyDeletedItem != null && recentlyDeletedPosition != -1) {
            mainDatas.add(recentlyDeletedPosition, recentlyDeletedItem!!)
            tempFilterDatas.add(recentlyDeletedPosition, recentlyDeletedItem!!)
            notifyItemInserted(recentlyDeletedPosition)
            recentlyDeletedItem = null
            recentlyDeletedPosition = -1
        }
    }

    fun removeDataWithUndo(position: Int) {
        if (position in mainDatas.indices) {
            recentlyDeletedItem = mainDatas[position]
            recentlyDeletedPosition = position
            mainDatas.removeAt(position)
            tempFilterDatas.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun setCustomAnimation(animationResId: Int, view: View) {
        val animation = AnimationUtils.loadAnimation(view.context, animationResId)
        view.startAnimation(animation)
    }

    fun addItemTouchHelperMoveSwipe(
        recyclerView: RecyclerView?,
        adapter: BaseRecyclerViewFilterAdapter<T>,
        swipeListener: SwipeListener<T>? = null,
        moveListener: MoveListener<T>? = null,
    ) {
        this.swipeListener = swipeListener
        this.moveListener = moveListener

        // Swipe dan drag handler
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN, // Untuk drag
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT // Untuk swipe
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder,
            ): Boolean {
                // Swap data dalam adapter
                val startPosition = viewHolder.adapterPosition
                val endPosition = target.adapterPosition
                adapter.swapData(startPosition, endPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // Hapus item dalam adapter (dengan undo jika dibutuhkan)
                val position = viewHolder.adapterPosition
                adapter.removeDataWithUndo(position)
            }
        }

        // Tambahkan ItemTouchHelper ke RecyclerView
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    fun swipeToDelete(position: Int) {
        if (position in mainDatas.indices) {
            val removedItem = mainDatas[position]
            mainDatas.removeAt(position)
            tempFilterDatas.removeAt(position)
            notifyItemRemoved(position)
            swipeListener?.onSwipe(position, removedItem)
        }
    }

    fun swapData(startPosition: Int, endPosition: Int) {
        if (startPosition in mainDatas.indices && endPosition in mainDatas.indices) {
            // Swap posisi data utama
            mainDatas[startPosition] = mainDatas[endPosition].also {
                mainDatas[endPosition] = mainDatas[startPosition]
            }

            // Sinkronkan perubahan ke data filter
            tempFilterDatas[startPosition] = tempFilterDatas[endPosition].also {
                tempFilterDatas[endPosition] = tempFilterDatas[startPosition]
            }

            // Notifikasi perubahan posisi
            notifyItemMoved(startPosition, endPosition)

            // Trigger listener
            moveListener?.onMove(startPosition, endPosition, mainDatas[endPosition])
        }
    }

    // Protected Methods
    protected fun convertLayout2View(parent: ViewGroup, layout: Int): View {
        return parent.inflate(layout)
    }

    protected fun setAnimation(view: View, position: Int) {
        if (position > lastPosition) {
            val animation: Animation = AnimationUtils.loadAnimation(view.context, R.anim.item_animation_fall_down)
            view.startAnimation(animation)
            lastPosition = position
        }
    }

    protected fun selectItemSingleMultiple(selected: Boolean, adapterPosition: Int, listenerSelectItem: ListenerSelectItem) {
        if (selected) {
            listenerSelectItem.onSelected(false)
        } else {
            checkSingleMode { listenerSelectItem.onCheckSingleMode(it) }
            listenerSelectItem.onSelect(true)
        }
        setChangeModeItem(adapterPosition)
    }

    // Private Methods
    private fun checkSingleMode(listener: (Boolean) -> Unit = {}) {
        if (filterModeData == SINGLE_MODE) listener(false)
    }

    private fun setChangeModeItem(adapterPosition: Int) {
        if (filterModeData == SINGLE_MODE) {
            setCheckPosition(adapterPosition)
            notifyDataSetChanged()
        }
    }

    private fun ViewGroup.inflate(@LayoutRes layout: Int): View {
        return LayoutInflater.from(context).inflate(layout, this, false)
    }

    // Interfaces
    interface ListenerSelectItem {
        fun onSelected(selected: Boolean)
        fun onCheckSingleMode(isSingleMode: Boolean)
        fun onSelect(selected: Boolean)
    }

    interface SwipeListener<T> {
        fun onSwipe(position: Int, item: T)
    }

    interface MoveListener<T> {
        fun onMove(startPosition: Int, endPosition: Int, data: T)
    }

    class Holder<T>(itemView: View) : BaseRecyclerViewHolder<T>(itemView) {
        override fun setContent(data: T) {}
        override fun setListener(data: T) {}
    }
}