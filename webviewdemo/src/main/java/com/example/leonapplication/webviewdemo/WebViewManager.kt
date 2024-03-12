package com.example.leonapplication.webviewdemo

import android.app.Activity
import android.webkit.WebView
import androidx.tracing.trace
import timber.log.Timber

class WebViewManager {

  private var webView: WebView? = null

  fun getWebView(activity: Activity): WebView {
    if (webView == null) {
      trace("getWebView") {
        webView = WebView(activity)
      }
      Timber.d("webView created")
    }
    return webView!!
  }

  fun loadUrl(url: String) {
    if (url.isEmpty()) {
      Timber.e("loadUrl: webView is null")
      return
    }
    webView?.loadUrl(url)
    Timber.d("loadUrl loading")
  }
}