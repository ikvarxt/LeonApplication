package com.example.leonapplication.floatwindowdemo

import android.content.Context
import android.view.View

interface ViewProvider {
  fun getView(context: Context): View
}
