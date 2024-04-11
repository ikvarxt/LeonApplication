package com.example.leonapplication.nativedemo

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.leonapplication.extension.button
import com.example.leonapplication.extension.list
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NativeLibActivity : AppCompatActivity() {

  private val nativeLib by lazy { NativeLib() }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    TextView(this).apply {
      text = nativeLib.stringFromJNI()
      textSize = 100f
    }.also {
      setContentView(it)
    }

    list {
      button(nativeLib.stringFromJNI()) {}
      button("alloc memory") {
        lifecycleScope.launch(Dispatchers.IO) {
          nativeLib.allocMemory(1024 * 1024 * 1024L)
        }
      }
    }

  }
}