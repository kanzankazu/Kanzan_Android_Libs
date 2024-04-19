package com.kanzankazu.kanzanwidget.recyclerview.base

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kanzankazu.kanzanwidget.recyclerview.utils.extension.RecyclerViewLayoutType
import com.kanzankazu.kanzanwidget.recyclerview.utils.extension.setRecyclerView

class GenericRecyclerviewSingleAdapter<T>(
    private val view: (parent: ViewGroup) -> View,
    private val onBindView: (itemView: View, data: T, position: Int) -> Unit,
) : BaseRecyclerViewFilterAdapter<T>() {

    private lateinit var holder1: Holder<T>

    override fun onCreateView(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        holder1 = Holder(view(parent))
        return holder1
    }

    override fun onBindView(data: T, position: Int, holder: RecyclerView.ViewHolder) {
        onBindView((holder as Holder<*>).itemView, data, position)
    }
}

inline fun <reified T> RecyclerView.setUp(
    noinline view: (parent: ViewGroup) -> View,
    noinline onBindView: (itemView: View, data: T, position: Int) -> Unit,
    layoutManagerIndex: RecyclerViewLayoutType = RecyclerViewLayoutType.RECYCLER_VIEW_LIN_VERTICAL,
    spanCountGrid: Int = 2,
    spacingItemDecoration: Int = 8,
    isNestedScrollingEnabledParam: Boolean = true,
): GenericRecyclerviewSingleAdapter<T> {
    val genericRecyclerviewAdapter = GenericRecyclerviewSingleAdapter(view, onBindView)
    this.setRecyclerView(genericRecyclerviewAdapter, layoutManagerIndex, spanCountGrid, spacingItemDecoration, isNestedScrollingEnabledParam)
    return genericRecyclerviewAdapter
}
