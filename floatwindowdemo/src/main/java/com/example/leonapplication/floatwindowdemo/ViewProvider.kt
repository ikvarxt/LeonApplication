package com.example.leonapplication.floatwindowdemo

import android.content.Context
import android.view.View

interface ViewProvider {
  fun getView(context: Context): View
  fun getWidth(): Int
  fun getHeight(): Int

  companion object {
    val width = ViewProviderImpl.getWidth()
    val height = ViewProviderImpl.getHeight()

    fun get(context: Context): View {
      return ViewProviderImpl.getView(context)
    }
  }
}
