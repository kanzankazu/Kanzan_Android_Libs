@file:Suppress("PackageName", "UNCHECKED_CAST", "KDocUnresolvedReference", "unused", "MemberVisibilityCanBePrivate")

package com.kanzankazu.kanzanwidget.recyclerview.base

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Filter
import android.widget.Filterable
import androidx.annotation.LayoutRes
/*import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding*/
import androidx.recyclerview.widget.RecyclerView
import com.kanzankazu.R

abstract class BaseRecyclerViewFilterAdapter<T> : RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {
    var mainDatas = arrayListOf<T>()
    var tempFilterDatas = arrayListOf<T>()

    private var filterModeData: Int = 1
    private var checkPositionData: Int = 0
    private var lastPosition = -1

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
        return if (onMultipleViewType(position, mainDatas[position]) == -1) super.getItemViewType(position)
        else onMultipleViewType(position, mainDatas[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return onCreateView(parent, viewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        return onBindView(mainDatas[position], position, holder)
    }

    override fun getItemCount(): Int = mainDatas.count()

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                mainDatas = if (charString.isEmpty()) {
                    tempFilterDatas
                } else {
                    val filteredList = arrayListOf<T>()
                    filteredList.addAll(onFilterData(tempFilterDatas, charString))
                    filteredList
                }

                val filterResults = FilterResults()
                filterResults.values = mainDatas
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                mainDatas = filterResults.values as ArrayList<T>
                onFilterListener(mainDatas)
                notifyDataSetChanged()
            }
        }
    }

    protected fun convertLayout2View(parent: ViewGroup, layout: Int): View = parent.inflate(layout)

    /*protected fun convertBinding(parent: ViewGroup, layout: Int): ViewDataBinding {
        return DataBindingUtil.inflate(LayoutInflater.from(parent.context), layout, parent, false)
    }*/

    fun setFilterMode(filterMode: Int) {
        this.filterModeData = filterMode
    }

    fun getFilterMode() = filterModeData

    fun setCheckPosition(pos: Int) {
        checkPositionData = pos
    }

    fun getCheckPosition(): Int = checkPositionData

    fun getItemData(position: Int): T = mainDatas[position]

    fun getItemDataS(): ArrayList<T> = mainDatas

    fun setData(dataS: ArrayList<T>) {
        this.mainDatas = dataS
        this.tempFilterDatas = dataS
        notifyDataSetChanged()
    }

    fun addDataS(dataS: ArrayList<T>) {
        val lastSize = this.mainDatas.size
        val arrayListOf = arrayListOf<T>()
        arrayListOf.addAll(dataS)

        this.mainDatas.addAll(dataS)
        this.tempFilterDatas = arrayListOf
        notifyItemRangeInserted(lastSize, dataS.size)
    }

    fun addData(data: T) {
        val arrayListOf = arrayListOf<T>()
        arrayListOf.addAll(mainDatas)
        arrayListOf.add(data)

        val lastSize = mainDatas.size
        this.mainDatas.add(data)
        this.tempFilterDatas = arrayListOf
        notifyItemRangeInserted(lastSize, 1)
    }

    fun addDataFirst(data: T) {
        val position = 0
        this.mainDatas.add(position, data)
        this.tempFilterDatas.add(position, data)
        notifyItemInserted(position)
    }

    fun addDataLast(data: T) {
        mainDatas.add(data)
        tempFilterDatas.add(data)
        notifyItemInserted(mainDatas.lastIndex)
    }

    fun addDataAt(data: T, pos: Int) {
        this.mainDatas.add(pos, data)
        this.tempFilterDatas.add(pos, data)
        notifyItemInserted(pos)
    }

    fun removeDataFirst() {
        val position = 0
        removeAt(position)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun removeDataS() {
        this.mainDatas = arrayListOf()
        this.tempFilterDatas = arrayListOf()
        notifyDataSetChanged()
    }

    fun removeDataLast() {
        mainDatas.removeAt(mainDatas.lastIndex)
        notifyItemRemoved(mainDatas.lastIndex)
    }

    fun removeAt(position: Int) {
        mainDatas[position]
        tempFilterDatas[position]
        mainDatas.removeAt(position)
        tempFilterDatas.removeAt(position)
        notifyItemRemoved(position)
    }

    /**
     * Here is the key method to apply the animation
     */
    fun setAnimation(itemView: View, position: Int, context: Context) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            val animation: Animation = AnimationUtils.loadAnimation(context, R.anim.fade_out)
            itemView.startAnimation(animation)
            lastPosition = position
        }
    }

    open fun addItem(model: T, position: Int) {
        mainDatas.add(position, model)
        notifyItemInserted(position)
    }

    open fun moveItem(fromPosition: Int, toPosition: Int) {
        val model: T = mainDatas.removeAt(fromPosition)
        mainDatas.add(toPosition, model)
        notifyItemMoved(fromPosition, toPosition)
    }

    fun ViewGroup.inflate(@LayoutRes layout: Int): View {
        return LayoutInflater.from(context).inflate(layout, this, false)
    }

    /**
     * @param selected tempDatas[checkPosition].selected
     * @param adapterPosition adapterPosition\currentPosition
     * @param listenerSelectItem
     * @return [ListenerSelectItem.onSelected] always false, [ListenerSelectItem.onSelect] always true
     * */
    protected fun selectItemSingleMultiple(selected: Boolean, adapterPosition: Int, listenerSelectItem: ListenerSelectItem) {
        if (selected) {
            listenerSelectItem.onSelected(false)
        } else {
            checkSingleMode { listenerSelectItem.onCheckSingleMode(it) }
            listenerSelectItem.onSelect(true)
        }
        setChangeModeItem(adapterPosition)
    }

    /**
    (sebelumnya)SINGLE_MODE -> {
    tempDatas[checkPosition].selected = false
    }
     */
    private fun checkSingleMode(listener: (Boolean) -> Unit = {}) {
        when (filterModeData) {
            SINGLE_MODE -> listener(false)
        }
    }

    private fun setChangeModeItem(adapterPosition: Int) {
        when (filterModeData) {
            SINGLE_MODE -> {
                setCheckPosition(adapterPosition)
                notifyDataSetChanged()
            }
        }
    }

    interface ListenerSelectItem {
        /** @return always false, set in function like this => tempDatas[adapterPosition].selected = b */
        fun onSelected(b: Boolean)

        /** @return set in function like this => tempDatas[checkPosition].selected = onSelectItemTrueSingle */
        fun onCheckSingleMode(onSelectItemTrueSingle: Boolean)

        /** @return  always true, set in function like this => tempDatas[adapterPosition].selected = b */
        fun onSelect(b: Boolean)
    }

    class Holder<T>(itemView: View) : BaseRecyclerViewHolder<T>(itemView) {
        override fun setContent(data: T) {
        }

        override fun setListener(data: T) {
        }
    }

    /*abstract class RecyclerViewHolderAbs<T>(binding: ViewDataBinding) : RecyclerView.ViewHolder((binding.root)) {
        abstract fun setContent(data: T)
        open fun setListener(data: T) {}
    }

    class RecyclerViewHolder0(var binding: ViewDataBinding) : RecyclerView.ViewHolder((binding.root)) {
        init {
            binding.root.tag = this
        }
    }

    class RecyclerViewHolder1(var binding: ViewDataBinding) : RecyclerView.ViewHolder((binding.root)) {
        init {
            binding.root.tag = this
        }
    }

    class RecyclerViewHolder2(var binding: ViewDataBinding) : RecyclerView.ViewHolder((binding.root)) {
        init {
            binding.root.tag = this
        }
    }*/
}

