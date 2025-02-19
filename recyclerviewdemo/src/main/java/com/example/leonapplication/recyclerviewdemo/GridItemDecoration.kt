package com.example.leonapplication.recyclerviewdemo

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class GridItemDecoration(
  context: Context,
) : RecyclerView.ItemDecoration() {

  private val resources = context.resources
  private val margin = resources.getDimensionPixelSize(R.dimen.list_margin)
  private val verticalGap = resources.getDimensionPixelSize(R.dimen.list_vertical_gap)

  override fun getItemOffsets(
    outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State,
  ) {
    val position = parent.getChildAdapterPosition(view)
    if (position == RecyclerView.NO_POSITION) return

    val layoutManager = parent.layoutManager as? GridLayoutManager ?: return

    val spanCount = layoutManager.spanCount
    val spanSize = layoutManager.spanSizeLookup.getSpanSize(position)
    val spanIndex = layoutManager.spanSizeLookup.getSpanIndex(position, spanCount)

    val left: Int
    val right: Int

    if (spanSize == spanCount) {
      left = margin
      right = margin
    } else {
      left = margin - spanIndex * margin / spanCount
      right = (spanIndex + 1) * margin / spanCount
    }

    outRect.left = left
    outRect.right = right

    if (position == 0) {
      outRect.top = verticalGap
    }
    outRect.bottom = verticalGap
  }
}