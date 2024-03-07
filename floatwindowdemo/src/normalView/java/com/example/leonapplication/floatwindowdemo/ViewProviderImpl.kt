package com.example.leonapplication.floatwindowdemo

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View

object ViewProviderImpl : ViewProvider {

  @SuppressLint("InflateParams")
  override fun getView(context: Context): View {
    return LayoutInflater.from(context).inflate(R.layout.layout_float_window_demo, null)
  }

  override fun getWidth(): Int = 300
  override fun getHeight(): Int = 300
}