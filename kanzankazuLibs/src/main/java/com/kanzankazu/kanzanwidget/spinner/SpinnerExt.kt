package com.kanzankazu.kanzanwidget.spinner

import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.Spinner
import com.kanzankazu.R

/**
 * Configures a spinner with the provided list of options and sets an optional item selection listener.
 *
 * @param spinner The Spinner widget to be configured.
 * @param listener An optional OnItemSelectedListener to handle item selection events. Can be null if no listener is required.
 */
fun ArrayList<String>.setupOptionItemListSpinner(spinner: Spinner, listener: AdapterView.OnItemSelectedListener?) {
    spinner.setupOptionItemListSpinner(this, listener)
}

/**
 * Configures a Spinner with a list of options and sets an optional item selection listener.
 * The Spinner will display the provided list of items using a predefined layout and allow user selection.
 *
 * @param list The list of items to populate in the Spinner.
 * @param listener The listener to handle item selection events. Can be null if no listener is required.
 */
fun Spinner.setupOptionItemListSpinner(list: ArrayList<String>, listener: AdapterView.OnItemSelectedListener?) {
    val adapter = ArrayAdapter(this.context, R.layout.multiline_spinner_item, list)
    adapter.setDropDownViewResource(R.layout.multiline_spinner_item)
    this.adapter = adapter
    this.onItemSelectedListener = listener
}

/**
 * Configures a Spinner with the provided adapter and item selection listener.
 * The adapter populates the Spinner's items, while the listener handles item selection events.
 *
 * @param adapter The adapter that supplies data to the Spinner. Should be a subclass of BaseAdapter.
 * @param listener The listener for handling item selection events. Can be null if no listener is required.
 *
 * Example:
 * ```kotlin
 * val mySpinner = Spinner(context)
 * val myAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, listOf("Option 1", "Option 2"))
 * mySpinner.setupOptionItemListSpinner(myAdapter, object : AdapterView.OnItemSelectedListener {
 *     override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
 *         // Handle item selection
 *     }
 *     override fun onNothingSelected(parent: AdapterView<*>) {
 *         // Handle no selection
 *     }
 * })
 * ```
 */
fun Spinner.setupOptionItemListSpinner(adapter: BaseAdapter, listener: AdapterView.OnItemSelectedListener?) {
    this.adapter = adapter
    this.onItemSelectedListener = listener
}
