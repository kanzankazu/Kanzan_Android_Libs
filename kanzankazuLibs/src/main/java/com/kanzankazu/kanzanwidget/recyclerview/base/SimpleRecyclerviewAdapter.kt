package com.kanzankazu.kanzanwidget.recyclerview.base

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kanzankazu.kanzanwidget.recyclerview.utils.extension.RecyclerViewLayoutType
import com.kanzankazu.kanzanwidget.recyclerview.utils.extension.setRecyclerView

class SimpleRecyclerviewAdapter<T>(
    private val view: (parent: ViewGroup) -> View,
    private val onBindView: (itemView: View, data: T, position: Int) -> Unit,
) : BaseRecyclerViewFilterAdapter<T>() {

    private lateinit var tHolder: Holder<T>

    override fun onCreateView(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        tHolder = Holder(view(parent))
        return tHolder
    }

    override fun onBindView(data: T, position: Int, holder: RecyclerView.ViewHolder) {
        onBindView((holder as Holder<*>).itemView, data, position)
    }
}

/**
 * Sets up the RecyclerView with a [SimpleRecyclerviewAdapter].
 *
 * This function simplifies the process of configuring a RecyclerView by creating and setting up a [SimpleRecyclerviewAdapter] for you.
 *
 * @param T The type of data that will be displayed in the RecyclerView.
 * @param view A lambda function that creates the view for each item in the RecyclerView. It takes the parent ViewGroup as a parameter and should return the created View.
 * @param onBindView A lambda function that binds the data to the item view. It takes the item view, the data for that position, and the position as parameters.
 * @param layoutManagerIndex The type of LayoutManager to use for the RecyclerView. Defaults to [RecyclerViewLayoutType.RECYCLER_VIEW_LIN_VERTICAL].
 * @param spanCountGrid The number of columns in the grid if the LayoutManager is a GridLayoutManager. Defaults to 2.
 * @param spacingItemDecoration The spacing between items in the RecyclerView. Defaults to 8.
 * @param isNestedScrollingEnabledParam Whether nested scrolling is enabled for the RecyclerView. Defaults to true.
 * @return The created [SimpleRecyclerviewAdapter].
 *
 * ## Usage Example:
 *
 * ```kotlin
 * data class MyItem(val text: String)
 *
 * val recyclerView: RecyclerView = findViewById(R.id.myRecyclerView)
 * val dataList = listOf(MyItem("Item 1"), MyItem("Item 2"), MyItem("Item 3"))
 *
 * recyclerView.setUp<MyItem>(
 *     view = { parent ->
 *         LayoutInflater.from(parent.context).inflate(R.layout.item_my_layout, parent, false)
 *     },
 *     onBindView = { itemView, data, position ->
 *         val textView: TextView = itemView.findViewById(R.id.textViewItem)
 *         textView.text = data.text
 *     }
 * ).apply {
 *     setList(dataList) // Set the data to the adapter
 * }
 * ```
 */
inline fun <reified T> RecyclerView.setSimpleAdapter(
    noinline view: (parent: ViewGroup) -> View,
    noinline onBindView: (itemView: View, data: T, position: Int) -> Unit,
    layoutManagerIndex: RecyclerViewLayoutType = RecyclerViewLayoutType.RECYCLER_VIEW_LIN_VERTICAL,
    spanCountGrid: Int = 2,
    spacingItemDecoration: Int = 8,
    isNestedScrollingEnabledParam: Boolean = true,
    isAll: Boolean = true,
): SimpleRecyclerviewAdapter<T> {
    val genericRecyclerviewAdapter = SimpleRecyclerviewAdapter(view, onBindView)
    this.setRecyclerView(genericRecyclerviewAdapter, layoutManagerIndex, spanCountGrid, spacingItemDecoration, isNestedScrollingEnabledParam, isAll)
    return genericRecyclerviewAdapter
}
