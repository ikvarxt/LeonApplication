package com.example.leonapplication.extension

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast

class CustomToast(context: Context, text: String, withImage: Boolean = false) {

  private val toast = Toast(context)

  init {
    val view = initView(context)
    val textView = view.findViewById<TextView>(R.id.text)

    if (withImage) textView.setTextWithImage(text)
    else textView.text = text

    @Suppress("DEPRECATION")
    toast.view = view
    toast.setGravity(Gravity.TOP, 0, 0)
  }

  @SuppressLint("InflateParams")
  private fun initView(context: Context): View {
    return LayoutInflater.from(context).inflate(R.layout.layout_custom_toast, null, false)
  }

  fun show() = toast.show()
}