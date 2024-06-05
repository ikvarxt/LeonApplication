package me.ikvarxt.jsviewdemo

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.WindowManager
import android.widget.TextView
import com.caoccao.javet.annotations.V8Function
import timber.log.Timber

class JsViewApi(
  private val context: Context,
  private val viewManager: WindowManager,
) {

  private val viewMap = hashMapOf<String, JsOwnedView>()

  @SuppressLint("SetTextI18n")
  @V8Function
  fun createView(id: String, name: String, width: Int, height: Int) {
    val view = when (name) {
      else -> {
        TextView(context).apply {
          textSize = 20f
          text = "TV-$id"
          setTextColor(Color.BLACK)
          setBackgroundColor(Color.CYAN)
        }
      }
    }
    val lp = createWindowParams(width, height)
    viewManager.addView(view, lp)
    viewMap[id] = JsOwnedView(view, lp)
  }

  @V8Function
  fun move(id: String, x: Int, y: Int) {
    viewMap[id]?.run {
      viewManager.update(x, y)
    } ?: Timber.e("no view named $id")
  }

  companion object {
    private const val TOUCHABLE_WIN_FLAG = (
      WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
        or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
        or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
      )

    private fun createWindowParams(width: Int, height: Int) = WindowManager.LayoutParams().apply {
      type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
      flags = TOUCHABLE_WIN_FLAG
      this.width = width // WindowManager.LayoutParams.WRAP_CONTENT
      this.height = height // WindowManager.LayoutParams.WRAP_CONTENT
      format = PixelFormat.TRANSLUCENT
      // don't know why have to set gravity to top | start to this view to make x and y correct
      gravity = Gravity.TOP or Gravity.START
      x = 0
      y = 0
    }
  }
}
