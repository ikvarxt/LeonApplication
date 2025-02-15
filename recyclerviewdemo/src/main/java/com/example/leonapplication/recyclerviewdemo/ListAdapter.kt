package com.example.leonapplication.recyclerviewdemo

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

class ListAdapter : ListAdapter<ListItem, ViewHolder>(ListItem) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val type = Constants.ViewType.entries.first { it.type == viewType }
    return ViewHolder.create(type, parent)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.bind(getItem(position))
  }

  override fun getItemViewType(position: Int) = getItem(position).viewType.type
}

data class ListItem(
  val viewType: Constants.ViewType,
  val data: Any? = null,
) {
  val isChecked: Boolean = false

  companion object : DiffUtil.ItemCallback<ListItem>() {
    override fun areItemsTheSame(oldItem: ListItem, newItem: ListItem) =
      oldItem.viewType == newItem.viewType && oldItem.data == newItem.data

    override fun areContentsTheSame(oldItem: ListItem, newItem: ListItem): Boolean {
      // TODO:
      return false
    }
  }
}