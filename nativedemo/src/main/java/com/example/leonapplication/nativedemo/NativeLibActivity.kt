package com.example.leonapplication.nativedemo

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class NativeLibActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    TextView(this).apply {
      text = NativeLib().stringFromJNI()
      textSize = 100f
    }.also {
      setContentView(it)
    }
  }
}