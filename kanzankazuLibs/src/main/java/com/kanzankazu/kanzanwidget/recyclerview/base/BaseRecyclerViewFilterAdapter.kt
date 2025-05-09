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
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.kanzankazu.R

abstract class BaseRecyclerViewFilterAdapter<T> : RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    var swipeListener: SwipeListener<T>? = null
    var moveListener: MoveListener<T>? = null

    protected var mainDatas = arrayListOf<T>()

    private var originalDatas = arrayListOf<T>()
    private var isLoading = false
    private var filterModeData: Int = SINGLE_MODE
    private var checkPositionData: Int = 0
    private var lastPosition = -1
    private var recentlyDeletedItem: T? = null
    private var recentlyDeletedPosition: Int = -1
    private var undoRemoveData: T? = null

    companion object {
        const val MULTIPLE_MODE = 0
        const val SINGLE_MODE = 1

        private const val TYPE_EMPTY = 0
        private const val TYPE_LOADING = 1
        private const val TYPE_DATA = 2
    }

    protected abstract fun onCreateView(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
    protected abstract fun onBindView(data: T, position: Int, holder: RecyclerView.ViewHolder)

    open fun onMultipleViewType(position: Int, data: T): Int = -1
    open fun onFilterData(row: ArrayList<T>, charString: String): ArrayList<T> = arrayListOf()
    open fun onFilterListener(mainData: ArrayList<T>) {}

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
                mainDatas = (if (charString.isEmpty()) originalDatas else onFilterData(originalDatas, charString))
                return FilterResults().apply { values = mainDatas }
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                mainDatas = filterResults.values as? ArrayList<T> ?: arrayListOf()
                onFilterListener(mainDatas)
                notifyDataSetChanged()
            }
        }
    }

    fun setFilterMode(filterMode: Int) {
        this.filterModeData = filterMode
    }

    fun getFilterMode() = filterModeData

    fun setCheckPosition(pos: Int) {
        if (pos in mainDatas.indices) checkPositionData = pos
    }

    fun getCheckPosition(): Int = checkPositionData

    fun getItemData(position: Int): T? = mainDatas.getOrNull(position)

    fun getItemDataS(): ArrayList<T> = mainDatas

    fun setData(dataS: ArrayList<T>) {
        mainDatas.clear()
        mainDatas.addAll(dataS)

        originalDatas.clear()
        originalDatas.addAll(dataS)

        notifyDataSetChanged()
    }

    fun addDataS(dataS: ArrayList<T>) {
        val lastSize = mainDatas.size
        mainDatas.addAll(dataS)
        originalDatas.addAll(dataS)
        notifyItemRangeInserted(lastSize, dataS.size)
    }

    fun addData(data: T) {
        val lastSize = mainDatas.size
        mainDatas.add(data)
        originalDatas.add(data)
        notifyItemInserted(lastSize)
    }

    fun addDataFirst(data: T) {
        val position = 0
        this.mainDatas.add(position, data)
        this.originalDatas.add(position, data)
        notifyItemInserted(position)
    }

    fun addDataLast(data: T) {
        mainDatas.add(data)
        originalDatas.add(data)
        notifyItemInserted(mainDatas.lastIndex)
    }

    fun addDataAt(data: T, pos: Int) {
        this.mainDatas.add(pos, data)
        this.originalDatas.add(pos, data)
        notifyItemInserted(pos)
    }

    fun removeDataFirst() {
        val position = 0
        removeAt(position)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun removeDataS() {
        this.mainDatas = arrayListOf()
        this.originalDatas = arrayListOf()
        notifyDataSetChanged()
    }

    fun removeDataLast() {
        if (mainDatas.isNotEmpty()) {
            mainDatas.removeAt(mainDatas.lastIndex)
            originalDatas.removeAt(originalDatas.lastIndex)
            notifyItemRemoved(mainDatas.lastIndex)
        }
    }

    fun removeAt(position: Int) {
        if (position in mainDatas.indices && position in originalDatas.indices) {
            mainDatas.removeAt(position)
            originalDatas.removeAt(position)
            notifyItemRemoved(position)
        }
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
            originalDatas.add(recentlyDeletedPosition, recentlyDeletedItem!!)
            notifyItemInserted(recentlyDeletedPosition)
            recentlyDeletedItem = null
            recentlyDeletedPosition = -1
        }
    }

    fun removeDataWithUndo(position: Int) {
        if (position in mainDatas.indices) {
            undoRemoveData = mainDatas[position]
            mainDatas.removeAt(position)

            val indexInOriginal = originalDatas.indexOf(undoRemoveData)
            if (indexInOriginal != -1) {
                originalDatas.removeAt(indexInOriginal)
            }

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

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder,
            ): Boolean {
                val startPosition = viewHolder.absoluteAdapterPosition
                val endPosition = target.absoluteAdapterPosition
                adapter.swapData(startPosition, endPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.absoluteAdapterPosition
                adapter.removeDataWithUndo(position)
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    fun swipeToDelete(position: Int) {
        if (position in mainDatas.indices) {
            val removedItem = mainDatas[position]
            mainDatas.removeAt(position)
            originalDatas.removeAt(position)
            notifyItemRemoved(position)
            swipeListener?.onSwipe(position, removedItem)
        }
    }

    fun swapData(startPosition: Int, endPosition: Int) {
        if (startPosition in mainDatas.indices && endPosition in mainDatas.indices) {
            mainDatas.swap(startPosition, endPosition)
            originalDatas.swap(startPosition, endPosition)
            notifyItemMoved(startPosition, endPosition)
            moveListener?.onMove(startPosition, endPosition, mainDatas[endPosition])
        }
    }

    fun setupLazyLoad(
        recyclerView: RecyclerView,
        nestedScrollView: NestedScrollView? = null,
        thresholdItemCount: Int = 3,
        onLoadMore: () -> Unit,
    ) {
        val nestedScrollViewParent = nestedScrollView ?: findParentNestedScrollView(recyclerView)

        if (nestedScrollViewParent != null) {
            nestedScrollViewParent.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, scrollRange ->
                if (scrollY == scrollRange) {
                    if (!isLoading) {
                        isLoading = true
                        onLoadMore()
                    }
                }
            })
        } else {
            recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val layoutManager = recyclerView.layoutManager ?: return
                    val totalItemCount = layoutManager.itemCount
                    val lastVisibleItemPosition: Int = when (layoutManager) {
                        is androidx.recyclerview.widget.LinearLayoutManager -> layoutManager.findLastVisibleItemPosition()
                        is androidx.recyclerview.widget.GridLayoutManager -> layoutManager.findLastVisibleItemPosition()
                        else -> return
                    }

                    if (!isLoading && totalItemCount <= lastVisibleItemPosition + thresholdItemCount) {
                        isLoading = true
                        onLoadMore()
                    }
                }
            })
        }
    }

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

    protected fun selectItemSingleMultiple(
        selected: Boolean,
        adapterPosition: Int,
        listenerSelectItem: ListenerSelectItem,
    ) {
        if (selected) {
            listenerSelectItem.onUnSelect(Pair(adapterPosition, false))
        } else {
            if (filterModeData == SINGLE_MODE) listenerSelectItem.onCheckSingleMode(Pair(getCheckPosition(), false))
            listenerSelectItem.onSelect(Pair(adapterPosition, true))
        }
        if (filterModeData == SINGLE_MODE) {
            setCheckPosition(adapterPosition)
            notifyItemChanged(adapterPosition)
        }
    }

    private fun <T> MutableList<T>.swap(i: Int, j: Int) {
        val temp = this[i]
        this[i] = this[j]
        this[j] = temp
    }

    private fun ViewGroup.inflate(@LayoutRes layout: Int): View {
        return LayoutInflater.from(context).inflate(layout, this, false)
    }

    private fun findParentNestedScrollView(view: View?): NestedScrollView? {
        var parentView = view?.parent
        while (parentView != null) {
            if (parentView is NestedScrollView) {
                return parentView
            }
            parentView = parentView.parent
        }
        return null
    }
    open class ListItem<out T> {
        data class DataItem<T>(val data: T) : ListItem<T>()
        object Loading : ListItem<Nothing>()
        object Empty : ListItem<Nothing>()
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

    class Holder<T>(itemView: View) : BaseRecyclerViewHolder<T>(itemView) {
        override fun setContent(data: T) {}
        override fun setListener(data: T) {}
    }
}