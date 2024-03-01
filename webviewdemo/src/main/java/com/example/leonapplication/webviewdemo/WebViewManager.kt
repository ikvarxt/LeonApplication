package com.example.leonapplication.webviewdemo

import android.app.Activity
import android.util.Log
import android.webkit.WebView
import androidx.tracing.trace

class WebViewManager {

  private var webView: WebView? = null

  fun getWebView(activity: Activity): WebView {
    if (webView == null) {
      trace("getWebView") {
        webView = WebView(activity)
      }
    }
    return webView!!
  }

  fun loadUrl(url: String) {
    webView?.loadUrl(url) ?: run {
      Log.e(TAG, "loadUrl: webView is null")
    }
  }

  companion object {
    private const val TAG = "WebViewManager"
  }
}