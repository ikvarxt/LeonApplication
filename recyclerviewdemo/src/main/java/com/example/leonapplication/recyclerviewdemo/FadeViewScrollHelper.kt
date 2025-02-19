package com.example.leonapplication.recyclerviewdemo

import android.view.View
import android.view.ViewGroup.LayoutParams
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView

class FadeViewScrollHelper(
  private val view: View,
  private val scrollHeight: Int,
  var isEnabled: Boolean = false,
) : RecyclerView.OnScrollListener() {

  private var offset = 0

  override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
    offset += dy

    if (isEnabled.not()) {
      view.alpha = 0f
      return
    }

    view.alpha =
      if (offset > scrollHeight) 1f
      else offset / scrollHeight.toFloat()
  }

  companion object {

    fun initialHeight(view: View) = ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
      val i = insets.getInsets(WindowInsetsCompat.Type.systemBars())
      v.updateLayoutParams<LayoutParams> {
        height = i.top
      }
      insets
    }
  }
}