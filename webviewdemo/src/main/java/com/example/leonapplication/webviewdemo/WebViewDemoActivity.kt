package com.example.leonapplication.webviewdemo

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity

class WebViewDemoActivity : AppCompatActivity() {

  @VisibleForTesting
  lateinit var webViewManager: WebViewManager

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    webViewManager = WebViewManager()

    Looper.getMainLooper().queue.addIdleHandler {
      initWebView()
      false
    }
  }

  @SuppressLint("SetJavaScriptEnabled")
  private fun initWebView() {
    val webView = webViewManager.getWebView(this@WebViewDemoActivity)
    webView.settings.javaScriptEnabled = true
    setContentView(webView)
    webViewManager.loadUrl("https://vuejs.org")
  }
}