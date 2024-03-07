package com.example.leonapplication.floatwindowdemo

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.view.WindowManager
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setMargins

class FloatWindowDemoActivity : AppCompatActivity() {

  private var floatingView: View? = null
  private var windowLayoutParams: WindowManager.LayoutParams? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    enableEdgeToEdge()

    loadContent(
      this@FloatWindowDemoActivity,
      onTextClick = {
        requestPermissionOrLunchWindow(
          this@FloatWindowDemoActivity,
          ::launchFloatWindow
        )
      },
      onRemoveClick = ::removeView
    ).also { setContentView(it) }
  }

  override fun onStart() {
    super.onStart()

    if (Settings.canDrawOverlays(this@FloatWindowDemoActivity)) {
      if (floatingView == null)
        launchFloatWindow()
    }
  }

  @SuppressLint("InflateParams", "ClickableViewAccessibility")
  private fun launchFloatWindow() {
    removeView()
    windowLayoutParams = createWindowParams()
    floatingView = createView().apply {
      setOnTouchListener { view, event ->
        return@setOnTouchListener when (event.action) {
          MotionEvent.ACTION_MOVE -> {
            windowLayoutParams?.x = event.rawX.toInt() - view.width / 2
            windowLayoutParams?.y = event.rawY.toInt() - view.height / 2
            windowManager.updateViewLayout(floatingView, windowLayoutParams)
            true
          }

          else -> false
        }
      }
    }
    windowManager.addView(floatingView, windowLayoutParams)
  }

  private fun removeView() {
    floatingView?.let { windowManager.removeView(it) }
    floatingView = null
  }

  private fun createWindowParams() = WindowManager.LayoutParams().apply {
    type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
    flags = TOUCHABLE_WIN_FLAG
    width = ViewProvider.width // WindowManager.LayoutParams.WRAP_CONTENT
    height = ViewProvider.height // WindowManager.LayoutParams.WRAP_CONTENT
    format = PixelFormat.TRANSLUCENT
    // don't know why have to set gravity to top | start to this view to make x and y correct
    gravity = Gravity.TOP or Gravity.START
    x = 0
    y = 28.dp
  }

  private fun createView(): View {
    return ViewProvider.get(this@FloatWindowDemoActivity)
  }

  override fun onDestroy() {
    super.onDestroy()
    removeView()
  }

  companion object {

    @Suppress("unused")
    private const val TAG = "FloatWindowDemoActivity"

    private fun requestPermissionOrLunchWindow(context: Context, launchAction: () -> Unit) {
      if (Settings.canDrawOverlays(context)) {
        launchAction.invoke()
      } else {
        Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).apply {
          data = Uri.parse("package:${context.packageName}")
        }.also {
          context.startActivity(it)
        }
      }
    }

    private fun loadContent(
      context: Context,
      onTextClick: () -> Unit = {},
      onRemoveClick: () -> Unit = {},
    ): View {
      return LinearLayout(context).also { layout ->
        layout.apply {
          layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
          )
          orientation = LinearLayout.VERTICAL
          gravity = Gravity.CENTER
        }
        Button(context).apply {
          text = "点击加载悬浮窗或请求权限"
          textSize = 40f
          layoutParams = MarginLayoutParams(
            MarginLayoutParams.WRAP_CONTENT,
            MarginLayoutParams.WRAP_CONTENT
          ).apply {
            setMargins(48)
            gravity = Gravity.CENTER
          }
          setOnClickListener { onTextClick.invoke() }
        }.also { layout.addView(it) }
        Button(context).apply {
          val buttonText = "remove view"
          text = buttonText.uppercase()
          layoutParams = MarginLayoutParams(
            MarginLayoutParams.WRAP_CONTENT,
            MarginLayoutParams.WRAP_CONTENT
          ).apply {
            setMargins(16)
            gravity = Gravity.CENTER
          }
          setOnClickListener { onRemoveClick.invoke() }
        }.also { layout.addView(it) }
      }
    }

    const val TOUCHABLE_WIN_FLAG = (
      WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
        or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
        or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
      )
  }
}

val Int.dp
  get() = (this * Resources.getSystem().displayMetrics.density).toInt()
