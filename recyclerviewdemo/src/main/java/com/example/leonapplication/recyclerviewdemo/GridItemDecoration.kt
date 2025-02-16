package com.example.leonapplication.recyclerviewdemo

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class GridItemDecoration(private val layoutManager: GridLayoutManager) : RecyclerView.ItemDecoration() {

  override fun getItemOffsets(
    outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State,
  ) {
    val resources = view.resources
    val position = parent.getChildAdapterPosition(view)
    if (position == RecyclerView.NO_POSITION) return

    val margin = resources.getDimensionPixelSize(R.dimen.list_margin)
    val verticalGap = resources.getDimensionPixelSize(R.dimen.list_vertical_gap)

    val spanCount = layoutManager.spanCount
    val spanSize = layoutManager.spanSizeLookup.getSpanSize(position)
    val spanIndex = layoutManager.spanSizeLookup.getSpanIndex(position, spanCount)

    val left: Int
    val right: Int

    if (spanSize == spanCount) {
      left = margin
      right = margin
    } else {
      val itemSpacing = margin * (spanCount - 1) / spanCount
      left = margin - spanIndex * itemSpacing / spanCount
      right = (spanIndex + spanSize) * itemSpacing / spanCount
    }

    outRect.left = left
    outRect.right = right

    if (position == 0) {
      outRect.top = verticalGap
    }
    outRect.bottom = verticalGap
  }
}