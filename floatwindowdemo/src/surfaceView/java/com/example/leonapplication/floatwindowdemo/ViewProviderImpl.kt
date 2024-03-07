package com.example.leonapplication.floatwindowdemo

import android.content.Context
import android.view.View

object ViewProviderImpl : ViewProvider {

  override fun getView(context: Context): View {
    return MyGLSurfaceView(context)
  }
}