package com.example.leonapplication.webviewdemo

import android.app.Activity
import android.text.TextUtils
import android.webkit.MimeTypeMap
import java.io.File
import java.lang.ref.WeakReference
import java.util.Locale

lateinit var activityRef: WeakReference<Activity>

fun getFileExtensionFromUrl(url: String): String {
  @Suppress("NAME_SHADOWING") var url = url
  url = url.lowercase(Locale.getDefault())
  if (!TextUtils.isEmpty(url)) {
    val fragment = url.lastIndexOf('#')
    if (fragment > 0) {
      url = url.substring(0, fragment)
    }
    val query = url.lastIndexOf('?')
    if (query > 0) {
      url = url.substring(0, query)
    }
    val filenamePos = url.lastIndexOf('/')
    val filename = if (0 <= filenamePos) url.substring(filenamePos + 1) else url

    // if the filename contains special characters, we don't
    // consider it valid for our matching purposes:
    if (filename.isNotEmpty()) {
      val dotPos = filename.lastIndexOf('.')
      if (0 <= dotPos) {
        return filename.substring(dotPos + 1)
      }
    }
  }
  return ""
}

fun getMimeTypeFromUrl(url: String): String? {
  return MimeTypeMap.getSingleton().getMimeTypeFromExtension(getFileExtensionFromUrl(url))
}

val httpClientCacheFolder
  get() = activityRef.get()?.let { ctx ->
    File(ctx.cacheDir, "HttpClientCache")
  }
