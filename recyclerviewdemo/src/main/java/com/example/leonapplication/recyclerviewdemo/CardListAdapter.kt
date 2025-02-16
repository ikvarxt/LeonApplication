package com.example.leonapplication.recyclerviewdemo

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.ListAdapter

class CardListAdapter(
  private val listener: ItemListener? = null,
) : ListAdapter<ListItem, ViewHolder>(ListItem) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val type = Constants.ViewType.entries.first { it.type == viewType }
    return ViewHolder.create(type, parent, listener)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.bind(getItem(position))
  }

  override fun getItemViewType(position: Int) = getItem(position).viewType.type

  interface ItemListener {
    fun onClick(item: ListItem)
    fun onLongClick(item: ListItem): Boolean
  }

  companion object {
    const val SPAN_COUNT = 3
    const val CARD_SPAN_SIZE = 1

    fun layoutManager(context: Context, viewType: (position: Int) -> Int) =
      GridLayoutManager(context, SPAN_COUNT).apply {
        spanSizeLookup = object : SpanSizeLookup() {
          override fun getSpanSize(position: Int): Int {
            val isCard = viewType(position) == Constants.ViewType.Card.type
            return if (isCard) CARD_SPAN_SIZE else SPAN_COUNT
          }
        }
      }
  }
}

data class ListItem(
  val viewType: Constants.ViewType,
  val data: Item? = null,
  val text: String? = null,
) {
  val isChecked: Boolean = false

  companion object : DiffUtil.ItemCallback<ListItem>() {
    override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem) =
      oldItem.viewType == newItem.viewType &&
        (oldItem.text == newItem.text || oldItem.data?.id == newItem.data?.id)

    override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
      return oldItem.isChecked == newItem.isChecked &&
        (oldItem.text == newItem.text || oldItem.data == newItem.data)
    }
  }
}