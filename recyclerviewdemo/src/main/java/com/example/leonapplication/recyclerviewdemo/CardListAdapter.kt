package com.example.leonapplication.recyclerviewdemo

import android.content.Context
import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import androidx.recyclerview.widget.ListAdapter

class CardListAdapter(
  private val listener: ItemListener? = null,
) : ListAdapter<ListItem, ViewHolder>(ListItem.ItemCallback()) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val type = Constants.ViewType.entries.first { it.type == viewType }
    return ViewHolder.create(type, parent, listener)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    val item = getItem(position)
    Log.d(TAG, "onBindViewHolder: item=$item")
    holder.bind(item)
  }

  override fun getItemViewType(position: Int) = getItem(position).viewType.type

  interface ItemListener {
    fun onClick(listItem: ListItem)
    fun onLongClick(listItem: ListItem): Boolean
    fun onChecked(listItem: ListItem, checked: Boolean)
  }

  companion object {
    private const val TAG = "CardListAdapter"

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
  val isChecked: Boolean = false,
  val isEditMode: Boolean = false,
) {

  class ItemCallback : DiffUtil.ItemCallback<ListItem>() {

    override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
      val isData = newItem.data != null
      val dataEquals = oldItem.data?.id == newItem.data?.id
      return oldItem.viewType == newItem.viewType &&
        (if (isData) dataEquals else true)
    }

    override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
      val isData = oldItem.data != null
      val dataEquals = oldItem.data == newItem.data
      val textEquals = (if (newItem.text != null) oldItem.text == newItem.text else true)

      val res = oldItem.isChecked == newItem.isChecked
        && oldItem.isEditMode == newItem.isEditMode
        && if (isData) dataEquals else textEquals
      Log.d(
        TAG, "areContentsTheSame: \n" +
          "res=$res, isData=$isData dataEquals=$dataEquals, textEquals=$textEquals \n" +
          "old=$oldItem, \nnew=$newItem"
      )
      return res
    }
  }

  companion object {
    private const val TAG = "ListItemCallback"
  }
}