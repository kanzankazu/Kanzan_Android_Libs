@file:Suppress("MemberVisibilityCanBePrivate")

package com.kanzankazu.kanzanwidget.recyclerview.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Filter
import android.widget.Filterable
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kanzankazu.R

abstract class BaseListAdapter<T>(
    diffCallback: DiffUtil.ItemCallback<T>,
) : ListAdapter<T, RecyclerView.ViewHolder>(diffCallback), Filterable {

    var swipeListener: SwipeListener<T>? = null
    var moveListener: MoveListener<T>? = null

    private var originalList = arrayListOf<T>()
    private var filterModeData = SINGLE_MODE
    private var checkPositionData = 0
    private var lastPosition = -1
    private var recentlyDeletedItem: T? = null
    private var recentlyDeletedPosition: Int = -1
    private var undoRemoveData: T? = null
    private var isLoading = false

    companion object {
        const val MULTIPLE_MODE = 0
        const val SINGLE_MODE = 1
    }

    protected abstract fun onCreateView(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
    protected abstract fun onBindView(data: T, position: Int, holder: RecyclerView.ViewHolder)

    open fun onMultipleViewType(position: Int, data: T): Int = -1
    open fun onFilterData(row: ArrayList<T>, charString: String): ArrayList<T> = arrayListOf()
    open fun onFilterListener(mainData: ArrayList<T>) {}

    override fun getItemViewType(position: Int): Int {
        val type = onMultipleViewType(position, getItem(position))
        return if (type == -1) super.getItemViewType(position) else type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return onCreateView(parent, viewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        onBindView(getItem(position), position, holder)
        setAnimation(holder.itemView, position)
    }

    override fun getFilter(): Filter = object : Filter() {
        override fun performFiltering(charSequence: CharSequence): FilterResults {
            val charString = charSequence.toString()
            val filteredList = if (charString.isEmpty()) originalList else onFilterData(originalList, charString)
            return FilterResults().apply { values = filteredList }
        }

        override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
            val resultList = filterResults.values as? ArrayList<T> ?: arrayListOf()
            submitList(resultList)
            onFilterListener(resultList)
        }
    }

    fun setFilterMode(filterMode: Int) {
        this.filterModeData = filterMode
    }

    fun getFilterMode() = filterModeData

    fun setCheckPosition(pos: Int) {
        if (pos in currentList.indices) checkPositionData = pos
    }

    fun getCheckPosition(): Int = checkPositionData

    fun getItemData(position: Int): T? = currentList.getOrNull(position)

    fun getItemDataS(): List<T> = currentList

    fun setData(data: List<T>) {
        originalList.clear()
        originalList.addAll(data)
        submitList(ArrayList(data))
    }

    fun addData(data: T) {
        val newList = ArrayList(currentList)
        newList.add(data)
        originalList.add(data)
        submitList(newList)
    }

    fun addDataAt(data: T, pos: Int) {
        val newList = ArrayList(currentList)
        newList.add(pos, data)
        originalList.add(pos, data)
        submitList(newList)
    }

    fun removeAt(position: Int) {
        val newList = ArrayList(currentList)
        val item = newList.removeAt(position)
        originalList.remove(item)
        recentlyDeletedItem = item
        recentlyDeletedPosition = position
        submitList(newList)
    }

    fun undoDelete() {
        if (recentlyDeletedItem != null && recentlyDeletedPosition != -1) {
            val newList = ArrayList(currentList)
            newList.add(recentlyDeletedPosition, recentlyDeletedItem!!)
            originalList.add(recentlyDeletedPosition, recentlyDeletedItem!!)
            submitList(newList)
            recentlyDeletedItem = null
            recentlyDeletedPosition = -1
        }
    }

    fun refreshItemAt(position: Int) {
        notifyItemChanged(position)
    }

    fun findPosition(predicate: (T) -> Boolean): Int? = currentList.indexOfFirst(predicate).takeIf { it >= 0 }

    fun getSelectedItem(): T? =
        if (filterModeData == SINGLE_MODE && checkPositionData in currentList.indices) currentList[checkPositionData] else null

    fun addItemTouchHelperMoveSwipe(recyclerView: RecyclerView) {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder,
            ): Boolean {
                val start = viewHolder.bindingAdapterPosition
                val end = target.bindingAdapterPosition
                val newList = ArrayList(currentList)
                val temp = newList[start]
                newList[start] = newList[end]
                newList[end] = temp
                submitList(newList)
                moveListener?.onMove(start, end, newList[end])
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                swipeListener?.onSwipe(position, getItem(position))
                removeAt(position)
            }
        }
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView)
    }

    fun setCustomAnimation(animationResId: Int, view: View) {
        val animation = AnimationUtils.loadAnimation(view.context, animationResId)
        view.startAnimation(animation)
    }

    fun setupLazyLoad(recyclerView: RecyclerView, nestedScrollView: NestedScrollView? = null, thresholdItemCount: Int = 3, onLoadMore: () -> Unit) {
        val nestedScroll = nestedScrollView ?: findParentNestedScrollView(recyclerView)
        if (nestedScroll != null) {
            nestedScroll.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, scrollRange ->
                if (scrollY == scrollRange && !isLoading) {
                    isLoading = true
                    onLoadMore()
                }
            })
        } else {
            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                    val layoutManager = rv.layoutManager ?: return
                    val totalCount = layoutManager.itemCount
                    val lastVisible = when (layoutManager) {
                        is androidx.recyclerview.widget.LinearLayoutManager -> layoutManager.findLastVisibleItemPosition()
                        is androidx.recyclerview.widget.GridLayoutManager -> layoutManager.findLastVisibleItemPosition()
                        else -> return
                    }
                    if (!isLoading && totalCount <= lastVisible + thresholdItemCount) {
                        isLoading = true
                        onLoadMore()
                    }
                }
            })
        }
    }

    protected fun convertLayout2View(parent: ViewGroup, layout: Int): View =
        LayoutInflater.from(parent.context).inflate(layout, parent, false)

    protected fun selectItemSingleMultiple(selected: Boolean, adapterPosition: Int, listenerSelectItem: ListenerSelectItem) {
        if (selected) {
            listenerSelectItem.onUnSelect(Pair(adapterPosition, false))
        } else {
            if (filterModeData == SINGLE_MODE) {
                listenerSelectItem.onCheckSingleMode(Pair(getCheckPosition(), false))
            }
            listenerSelectItem.onSelect(Pair(adapterPosition, true))
        }
        if (filterModeData == SINGLE_MODE) {
            setCheckPosition(adapterPosition)
            notifyItemChanged(adapterPosition)
        }
    }

    private fun setAnimation(view: View, position: Int) {
        if (position > lastPosition) {
            val animation: Animation = AnimationUtils.loadAnimation(view.context, R.anim.item_animation_fall_down)
            view.startAnimation(animation)
            lastPosition = position
        }
    }

    private fun findParentNestedScrollView(view: View?): NestedScrollView? {
        var parentView = view?.parent
        while (parentView != null) {
            if (parentView is NestedScrollView) return parentView
            parentView = parentView.parent
        }
        return null
    }

    interface ListenerSelectItem {
        fun onUnSelect(intBooleanPair: Pair<Int, Boolean>)
        fun onCheckSingleMode(intBooleanPair: Pair<Int, Boolean>)
        fun onSelect(intBooleanPair: Pair<Int, Boolean>)
    }

    interface SwipeListener<T> {
        fun onSwipe(position: Int, item: T)
    }

    interface MoveListener<T> {
        fun onMove(startPosition: Int, endPosition: Int, data: T)
    }

}
