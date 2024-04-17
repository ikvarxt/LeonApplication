package com.example.leonapplication.extension

import android.app.Activity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.util.TypedValueCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding

inline fun Activity.list(content: LinearLayout.() -> Unit) {
  (this as ComponentActivity).enableEdgeToEdge()
  LinearLayout(this).apply {
    orientation = LinearLayout.VERTICAL
    content()
    fitsSystemBar()
  }.also { setContentView(it) }
}

fun ViewGroup.fitsSystemBar() {
  ViewCompat.setOnApplyWindowInsetsListener(this) { v, windowInsets ->
    val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
    v.updatePadding(insets.left, insets.top, insets.right, insets.bottom)
    WindowInsetsCompat.CONSUMED
  }
}

inline fun ViewGroup.button(label: String, crossinline clickAction: () -> Unit) {
  Button(context).apply {
    text = label
    textSize = 50f
    setOnClickListener { clickAction.invoke() }
  }.also { addView(it) }
}

val Int.dip get() = TypedValueCompat.dpToPx(this.toFloat(), globalApp.resources.displayMetrics)
