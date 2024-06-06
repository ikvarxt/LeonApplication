package me.ikvarxt.jsviewdemo

import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.caoccao.javet.annotations.V8Function
import com.caoccao.javet.interop.NodeRuntime
import timber.log.Timber
import java.io.Closeable

class JsViewApi(
  private val context: Context,
  private val viewManager: WindowManager,
  private val node: NodeRuntime,
) : Closeable {

  private val viewMap = hashMapOf<String, JsOwnedView>()
  private val rootViewIds = mutableSetOf<String>()

  private var id: Int = 0
    get() = field++

  @V8Function
  fun createView(type: String, isChild: Boolean, width: Int, height: Int): String {
    val view = createViewByType(context, type)
    val id = "${type.ifBlank { "view" }}-$id"
    val lp = if (isChild) {
      ViewGroup.LayoutParams(width, height)
    } else {
      rootViewIds += id
      createWindowParams(width, height)
    }
    viewMap[id] = JsOwnedView(id, view, lp)
    return id
  }

  /**
   * create a ViewLayout with children ids, currently doesn't support
   * layout nesting
   */
  @V8Function
  fun createViewLayout(width: Int, height: Int, childList: List<String>): String {
    val view = FrameLayout(context)
    val id = "layout-$id"
    val lp = createWindowParams(width, height)
    val children = childList.filter { viewMap.containsKey(it) }.map { viewMap[it]!! }
    val ownedViewGroup = JsOwnedViewGroup(id, view, lp, children)
    viewMap[id] = ownedViewGroup
    rootViewIds += id
    return id
  }

  @V8Function
  fun move(id: String, x: Int, y: Int) {
    viewMap[id]?.run {
      viewManager.update(x, y)
    } ?: Timber.e("no view named $id")
  }

  @V8Function
  fun showView(id: String) {
    viewMap[id]?.run {
      viewManager.show()
    }
  }

  @V8Function
  fun removeView(id: String) {
    val removedView = viewMap[id] ?: run {
      Timber.w("view $id not in map")
      return
    }
    if (rootViewIds.contains(id)) {
      if (removedView is JsOwnedViewGroup) {
        removedView.list
          .map { it.id }
          .forEach { viewMap.remove(it) }
      }
      removedView.run {
        viewManager.removeView(view)
      }
    } else {
      viewMap[id]?.let { jsOwnedView ->
        val view = jsOwnedView.view
        (view.parent as ViewGroup).removeView(view)
      }
    }
  }

  // region view instance api
  @V8Function
  fun setBackgroundImage(id: String, imageSrc: String) {
    viewMap[id]?.let { view ->
      view.view.setBackgroundResource(R.drawable.ic_android_black_24dp)
    }
  }

  @V8Function
  fun setOnClickListener(id: String, callback: String) {
    viewMap[id]?.let { view ->
      view.view.setOnClickListener {
        node.globalObject.invokeVoid(callback, id)
      }
    }
  }
  // endregion

  @V8Function
  fun toast(msg: String) {
    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
  }

  override fun close() {
    rootViewIds.forEach { id ->
      viewMap[id]?.run {
        viewManager.removeView(view)
      }
    }
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

    private fun createViewByType(context: Context, type: String): View {
      return when (type) {
        "ImageView" -> {
          ImageView(context).apply {
            setBackgroundResource(R.drawable.ic_android_black_24dp)
          }
        }

        else -> {
          TextView(context).apply {
            textSize = 20f
            text = "TV-$id"
            setTextColor(Color.BLACK)
            setBackgroundColor(Color.CYAN)
          }
        }
      }
    }
  }
}
