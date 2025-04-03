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
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.kanzankazu.R

abstract class BaseRecyclerViewFilterAdapter<T> : RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    // Variables
    var mainDatas = arrayListOf<T>()
    var swipeListener: SwipeListener<T>? = null
    var moveListener: MoveListener<T>? = null
    var isLoading = false
    private var tempFilterDatas = arrayListOf<T>()
    private var filterModeData: Int = SINGLE_MODE
    private var checkPositionData: Int = 0
    private var lastPosition = -1
    private var recentlyDeletedItem: T? = null
    private var recentlyDeletedPosition: Int = -1

    companion object {
        const val MULTIPLE_MODE = 0
        const val SINGLE_MODE = 1

        private const val TYPE_EMPTY = 0
        private const val TYPE_LOADING = 1
        private const val TYPE_DATA = 2
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
        val diffCallback = object : DiffUtil.Callback() {
            override fun getOldListSize() = mainDatas.size // Saat kosong, ini adalah 0
            override fun getNewListSize() = dataS.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                // Karena dataset lama kosong, ini tidak akan pernah terpanggil
                return mainDatas.getOrNull(oldItemPosition) == dataS.getOrNull(newItemPosition)
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                // Karena dataset lama kosong, ini tidak akan pernah terpanggil
                return mainDatas.getOrNull(oldItemPosition) == dataS.getOrNull(newItemPosition)
            }
        }

        // Kalkulasi perbedaan dengan DiffUtil
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        // Update dataset lama
        mainDatas.clear()
        mainDatas.addAll(dataS)
        tempFilterDatas.clear()
        tempFilterDatas.addAll(dataS)

        // Terapkan perubahan
        diffResult.dispatchUpdatesTo(this)
    }

    fun addAllData(dataList: List<T>) {
        val newData = ArrayList(mainDatas).apply { addAll(dataList) }
        val diffCallback = object : DiffUtil.Callback() {
            override fun getOldListSize() = mainDatas.size
            override fun getNewListSize() = newData.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return mainDatas[oldItemPosition] == newData[newItemPosition] // Sesuaikan logikanya
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return mainDatas[oldItemPosition] == newData[newItemPosition] // Sesuaikan logikanya
            }
        }
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        mainDatas.clear()
        mainDatas.addAll(newData)
        tempFilterDatas.clear()
        tempFilterDatas.addAll(newData)
        diffResult.dispatchUpdatesTo(this)
    }

    fun addDataAt(data: T, position: Int) {
        mainDatas.add(position, data)
        tempFilterDatas.add(position, data)
        notifyItemInserted(position)
    }

    fun updateDataAt(position: Int, newData: T) {
        if (position in mainDatas.indices) {
            val newDataList = ArrayList(mainDatas).apply { this[position] = newData }
            val diffCallback = object : DiffUtil.Callback() {
                override fun getOldListSize() = mainDatas.size
                override fun getNewListSize() = newDataList.size

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return mainDatas[oldItemPosition] == newDataList[newItemPosition] // Sesuaikan logikanya
                }

                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return mainDatas[oldItemPosition] == newDataList[newItemPosition] // Sesuaikan logikanya
                }
            }
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            mainDatas.clear()
            mainDatas.addAll(newDataList)
            tempFilterDatas.clear()
            tempFilterDatas.addAll(newDataList)
            diffResult.dispatchUpdatesTo(this)
        }
    }

    fun removeDataAt(position: Int) {
        if (position in mainDatas.indices) {
            val newDataList = ArrayList(mainDatas)
            newDataList.removeAt(position)
            val diffCallback = object : DiffUtil.Callback() {
                override fun getOldListSize() = mainDatas.size
                override fun getNewListSize() = newDataList.size

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return mainDatas[oldItemPosition] == newDataList[newItemPosition] // Sesuaikan logikanya
                }

                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return mainDatas[oldItemPosition] == newDataList[newItemPosition] // Sesuaikan logikanya
                }
            }
            val diffResult = DiffUtil.calculateDiff(diffCallback)
            mainDatas.clear()
            mainDatas.addAll(newDataList)
            tempFilterDatas.clear()
            tempFilterDatas.addAll(newDataList)
            diffResult.dispatchUpdatesTo(this)
        }
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

    fun setupLazyLoad(
        recyclerView: RecyclerView,
        nestedScrollView: NestedScrollView? = null,
        thresholdItemCount: Int = 3, // Default: Load more saat tersisa 3 item terlihat
        onLoadMore: () -> Unit // Callback untuk memuat data baru
    ) {
        // Cek apakah NestedScrollView diberikan
        val nestedScrollViewParent = nestedScrollView ?: findParentNestedScrollView(recyclerView)

        // Jika RecyclerView berada dalam NestedScrollView
        if (nestedScrollViewParent != null) {
            nestedScrollViewParent.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, scrollRange ->
                if (scrollY == scrollRange) { // Jika scroll mencapai akhir
                    if (!isLoading) {
                        isLoading = true
                        // Callback untuk memuat data baru
                        onLoadMore()
                    }
                }
            })
        } else {
            recyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val layoutManager = recyclerView.layoutManager ?: return
                    val totalItemCount = layoutManager.itemCount
                    val lastVisibleItemPosition: Int = when (layoutManager) {
                        is androidx.recyclerview.widget.LinearLayoutManager -> layoutManager.findLastVisibleItemPosition()
                        is androidx.recyclerview.widget.GridLayoutManager -> layoutManager.findLastVisibleItemPosition()
                        else -> return
                    }

                    // Cek apakah sudah mendekati akhir (thresholdItemCount)
                    if (!isLoading && totalItemCount <= lastVisibleItemPosition + thresholdItemCount) {
                        isLoading = true
                        // Callback untuk memuat data baru
                        onLoadMore()
                    }
                }
            })
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

    /**
     * Fungsi untuk mendeteksi secara otomatis apakah RecyclerView berada di dalam NestedScrollView.
     */
    private fun findParentNestedScrollView(view: View?): NestedScrollView? {
        var parentView = view?.parent
        while (parentView != null) {
            if (parentView is NestedScrollView) {
                return parentView // Return jika menemukan NestedScrollView
            }
            parentView = parentView.parent
        }
        return null // Return null jika tidak ada NestedScrollView
    }

    open class ListItem<out T> {
        data class DataItem<T>(val data: T) : ListItem<T>() // Data reguler
        object Loading : ListItem<Nothing>()
        object Empty : ListItem<Nothing>()
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