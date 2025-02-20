package com.example.leonapplication.recyclerviewdemo

import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import kotlin.reflect.KClass

class ScrollerAnimationHelper<VH : RecyclerView.ViewHolder>(
  private val kClass: KClass<VH>,
  private val stateChangeListener: VH.(Boolean) -> Unit,
) : RecyclerView.OnScrollListener() {

  @Suppress("UNCHECKED_CAST")
  override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
    when (newState) {
      RecyclerView.SCROLL_STATE_SETTLING,
      RecyclerView.SCROLL_STATE_DRAGGING,
        -> {
        // TODO: 2/20/2025 maybe produce performance issue here
        recyclerView.children
          .map { recyclerView.getChildViewHolder(it) }
          .filter { kClass.isInstance(it) }
          .map { it as VH }
          .forEach { stateChangeListener.invoke(it, false) }
      }

      else -> {
        recyclerView.children
          .map { recyclerView.getChildViewHolder(it) }
          .filter { kClass.isInstance(it) }
          .map { it as VH }
          .forEach { stateChangeListener.invoke(it, true) }
      }
    }
  }
}