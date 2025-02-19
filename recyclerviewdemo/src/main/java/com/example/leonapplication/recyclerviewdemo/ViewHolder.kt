package com.example.leonapplication.recyclerviewdemo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView

sealed class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

  protected var listener: CardListAdapter.ItemListener? = null

  abstract fun support(item: ListItem): Boolean
  abstract fun realBind(listItem: ListItem)

  fun bind(item: ListItem) {
    if (support(item)) {
      realBind(item)
      setEditMode(item, item.isEditMode)
    } else {
      itemView.isVisible = false
    }
  }

  open fun setEditMode(listItem: ListItem, isEditMode: Boolean) {
    itemView.alpha = if (isEditMode) 0.3f else 1f
  }

  fun setListListener(listener: CardListAdapter.ItemListener?) {
    if (listener == null) {
      this.listener = null
      return
    }
    this.listener = object : CardListAdapter.ItemListener by listener {
      override fun onClick(listItem: ListItem) {
        if (listItem.isEditMode) {
          onChecked(listItem, listItem.isChecked.not())
        } else {
          listener.onClick(listItem)
        }
      }

      override fun onLongClick(listItem: ListItem): Boolean {
        return if (listItem.isEditMode.not()) {
          listener.onLongClick(listItem)
        } else false
      }
    }
  }

  companion object {

    fun create(type: Constants.ViewType, parent: ViewGroup, listener: CardListAdapter.ItemListener?): ViewHolder {
      val viewHolder = when (type) {
        Constants.ViewType.Card -> CardViewHolder(parent)
        Constants.ViewType.Header -> HeaderViewHolder(parent)
        Constants.ViewType.LoadMore -> LoadMoreViewHolder(parent)
        Constants.ViewType.Footer -> FooterViewHolder(parent)
      }
      viewHolder.setListListener(listener)
      return viewHolder
    }

    fun ViewGroup.viewOf(@LayoutRes layout: Int): View =
      LayoutInflater.from(context).inflate(layout, this, false)
  }
}

class CardViewHolder(parent: ViewGroup) : ViewHolder(parent.viewOf(R.layout.item_card)) {

  private val image: ShapeableImageView = itemView.findViewById(R.id.image)
  private val name: TextView = itemView.findViewById(R.id.name)
  private val checkbox: CheckBox = itemView.findViewById(R.id.checkbox)

  override fun support(item: ListItem): Boolean {
    return item.data is Item
  }

  override fun realBind(listItem: ListItem) {
    val data = listItem.data as Item

    name.text = data.name

    if (image.getTag(R.id.tag_card_img_url) != data.imgUrl) {
      image.setTag(R.id.tag_card_img_url, data.imgUrl)
      Glide.with(image.context)
        .asDrawable()
        .error(R.drawable.placeholder)
        .placeholder(R.drawable.placeholder)
        .load(data.imgUrl)
        .into(image)
    }
    image.setOnClickListener {
      listener?.onClick(listItem)
    }
    image.setOnLongClickListener {
      listener?.onLongClick(listItem) ?: false
    }
  }

  override fun setEditMode(listItem: ListItem, isEditMode: Boolean) {
    checkbox.setOnCheckedChangeListener(null)
    checkbox.isVisible = isEditMode
    if (isEditMode) {
      checkbox.isChecked = listItem.isChecked
      checkbox.setOnCheckedChangeListener { _, checked ->
        listener?.onChecked(listItem, checked)
      }
    } else {
      if (checkbox.isChecked) {
        checkbox.isChecked = false
        checkbox.jumpDrawablesToCurrentState()
      }
    }
  }

}

class HeaderViewHolder(parent: ViewGroup) : ViewHolder(parent.viewOf(R.layout.item_header)) {

  private val text: TextView = itemView.findViewById(R.id.text)

  override fun support(item: ListItem): Boolean {
    return item.viewType == Constants.ViewType.Header
  }

  override fun realBind(listItem: ListItem) {
    text.text = listItem.text.toString()
    itemView.setOnClickListener {
      listener?.onClick(listItem)
    }
  }

  override fun setEditMode(listItem: ListItem, isEditMode: Boolean) {
    super.setEditMode(listItem, isEditMode)
    text.isEnabled = isEditMode.not()
  }
}

class FooterViewHolder(parent: ViewGroup) : ViewHolder(parent.viewOf(R.layout.item_footer)) {

  private val button: Button = itemView.findViewById(R.id.button)

  override fun support(item: ListItem): Boolean {
    return item.viewType == Constants.ViewType.Footer
  }

  override fun realBind(listItem: ListItem) {
    button.text = listItem.text.toString()
    button.setOnClickListener {
      listener?.onClick(listItem)
    }
  }

  override fun setEditMode(listItem: ListItem, isEditMode: Boolean) {
    super.setEditMode(listItem, isEditMode)
    button.isEnabled = isEditMode.not()
  }
}

class LoadMoreViewHolder(parent: ViewGroup) : ViewHolder(parent.viewOf(R.layout.item_load_more)) {

  private val button: Button = itemView.findViewById(R.id.loadMore)

  override fun support(item: ListItem): Boolean {
    return item.viewType == Constants.ViewType.LoadMore
  }

  override fun realBind(listItem: ListItem) {
    button.setOnClickListener {
      listener?.onClick(listItem)
    }
  }

  override fun setEditMode(listItem: ListItem, isEditMode: Boolean) {
    button.isEnabled = isEditMode.not()
  }
}
