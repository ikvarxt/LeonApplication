package me.ikvarxt.jsviewdemo

import android.view.View
import android.view.WindowManager

class JsOwnedView(
  val view: View,
  val lp: WindowManager.LayoutParams,
) {

  fun WindowManager.update(x: Int, y: Int) {
    lp.x = x
    lp.y = y
    updateViewLayout(view, lp)
  }
}