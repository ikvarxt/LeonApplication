package com.example.leonapplication.extension

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.widget.Button

class ToastTestActivity : Activity() {

  companion object {
    const val BTN_ID = 1427520
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val context = this@ToastTestActivity

    val btn = Button(context).apply {
      id = BTN_ID
      setOnClickListener { showToast(context) }
    }
    setContentView(btn)
  }

  private fun showToast(context: Context) {
    CustomToast(
      context,
      "<img src='emoji_kiss.png'/> kiss kiss <img src='emoji_kiss.png'/>",
      withImage = true
    ).show()
  }

}