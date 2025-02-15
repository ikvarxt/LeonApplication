package com.example.leonapplication.recyclerviewdemo

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView

sealed class ViewHolder(protected val rootView: View) : RecyclerView.ViewHolder(rootView) {

  abstract fun support(item: ListItem): Boolean
  abstract fun realBind(listItem: ListItem)

  fun bind(item: ListItem) {
    if (support(item)) {
      realBind(item)
    } else {
      rootView.isVisible = false
    }
  }

  companion object {
    fun create(type: Constants.ViewType, parent: ViewGroup): ViewHolder {
      val layoutInflater = LayoutInflater.from(parent.context)
      fun viewOf(@LayoutRes layout: Int) = layoutInflater.inflate(layout, parent, false)
      val viewHolder = when (type) {
        Constants.ViewType.Card -> CardViewHolder(viewOf(R.layout.item_card))
        Constants.ViewType.Header -> HeaderViewHolder(viewOf(R.layout.item_header))
        Constants.ViewType.LoadMore -> LoadMoreViewHolder(viewOf(R.layout.item_load_more))
        Constants.ViewType.Footer -> FooterViewHolder(viewOf(R.layout.item_header))
      }
      return viewHolder
    }
  }
}

class CardViewHolder(view: View) : ViewHolder(view) {

  private val image: ShapeableImageView = view.findViewById(R.id.image)
  private val name: TextView = view.findViewById(R.id.name)

  override fun support(item: ListItem): Boolean {
    return item.data is Item
  }

  override fun realBind(listItem: ListItem) {
    val data = listItem.data as Item

    name.text = data.name
    Glide.with(image.context)
      .asDrawable()
      .error(R.drawable.placeholder)
      .placeholder(R.drawable.placeholder)
      .load(data.imgUrl)
      .into(image)
  }

}

class HeaderViewHolder(view: View) : ViewHolder(view) {
  override fun support(item: ListItem): Boolean {
    return item.viewType == Constants.ViewType.Header
  }
  override fun realBind(listItem: ListItem) {

    rootView.setBackgroundColor(Color.RED)

  }
}

class FooterViewHolder(view: View) : ViewHolder(view) {
  override fun support(item: ListItem): Boolean {
    return item.viewType == Constants.ViewType.Footer
  }
  override fun realBind(listItem: ListItem) {}
}

class LoadMoreViewHolder(view: View) : ViewHolder(view) {
  override fun support(item: ListItem): Boolean {
    return item.viewType == Constants.ViewType.LoadMore
  }
  override fun realBind(listItem: ListItem) {
   rootView.setBackgroundColor(Color.BLUE)
  }
}
