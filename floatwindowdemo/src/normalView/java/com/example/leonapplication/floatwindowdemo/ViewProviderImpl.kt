package com.example.leonapplication.floatwindowdemo

import android.content.Context
import android.view.LayoutInflater
import android.view.View

object ViewProviderImpl : ViewProvider {

  override fun getView(context: Context): View {
    return LayoutInflater.from(context).inflate(R.layout.layout_float_window_demo, null)
  }
}