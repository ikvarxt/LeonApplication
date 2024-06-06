package me.ikvarxt.jsviewdemo

import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import timber.log.Timber

open class JsOwnedView(
  open val id: String,
  open val view: View,
  open val lp: ViewGroup.LayoutParams,
) {

  open fun WindowManager.show() {
    addView(view, lp)
  }

  open fun WindowManager.update(x: Int, y: Int) {
    (lp as? WindowManager.LayoutParams)?.let { layoutParams ->
      layoutParams.x = x
      layoutParams.y = y
      updateViewLayout(view, layoutParams)
    } ?: Timber.e("not a window mananger layout params")
  }

}

class JsOwnedViewGroup(
  override val id: String,
  override val view: ViewGroup,
  override val lp: WindowManager.LayoutParams,
  val list: List<JsOwnedView>,
) : JsOwnedView(id, view, lp) {

  override fun WindowManager.show() {
    list.forEach { view.addView(it.view, it.lp) }
    addView(view, lp)
  }
}