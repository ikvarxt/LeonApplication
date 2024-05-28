package com.example.leonapplication.webviewdemo

import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import timber.log.Timber
import java.io.IOException
import kotlin.time.measureTime

private const val coding = "utf-8"

class MyWebViewClient(
  private val httpClient: OkHttpClient,
) : WebViewClient() {

  override fun shouldInterceptRequest(view: WebView, request: WebResourceRequest): WebResourceResponse? {

    val url = request.url.toString()
    val mimeType = getMimeTypeFromUrl(url) ?: return null
    val builder = Request.Builder()
      .url(url)
    // construct a 403 Not Modified response
    if (url.contains("css")) {
      builder.addHeader("If-Modified-Since", "Tue, 21 Nov 2050 08:00:00 GMT")
    }
    // TODO: bug occurs
    //  1. not all image loaded by Glide
    //  2. may load failed with exception
    // get image by Glide
    // getBitmapFormatFromMime(mimeType)?.let { format ->
    //   Timber.d("glide load $url format ${format.name}")
    //   val inputStream = view.loadBitmapFor(url).asInputSteam(format)
    //   return WebResourceResponse(mimeType, coding, inputStream)
    // }

    val response: Response
    val requestTime = measureTime {
      try {
        response = httpClient.newCall(builder.build()).execute()
      } catch (e: IOException) {
        Timber.e("error when request $url", e)
        return null
      }
    }

    Timber.d(String.format("cached? %5s %s, loadTime: %s", response.cacheResponse != null, response, requestTime))

    val code = response.code
    if (code == 404) return null
    if (code == 504) return null
    // let the WebView to do the redirect request
    if (code == 304) return null

    val webResourceResponse = WebResourceResponse(mimeType, coding, response.body?.byteStream())

    if (response.cacheResponse != null) {
      response.cacheResponse?.body?.byteStream().also { webResourceResponse.data = it }
    }

    val message = response.message.takeIf { it.isNotBlank() } ?: "OK"
    webResourceResponse.setStatusCodeAndReasonPhrase(code, message)
    val stream = response.body?.byteStream() ?: return null
    return WebResourceResponse(mimeType, coding, stream)
  }
}
