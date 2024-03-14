package com.example.leonapplication.webviewdemo

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.text.TextUtils
import android.webkit.MimeTypeMap
import android.webkit.WebView
import androidx.core.os.BuildCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import okhttp3.Cache
import okhttp3.OkHttpClient
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
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

fun provideHttpClient(): OkHttpClient {
  val cache = httpClientCacheFolder?.let { folder ->
    Cache(folder, 100 * 1024 * 1024)
  }
  return OkHttpClient.Builder().apply {
    cache(cache)
  }.build()
}

fun WebView.loadBitmapFor(url: String): Bitmap {
  return Glide.with(this)
    .asBitmap()
    .diskCacheStrategy(DiskCacheStrategy.ALL)
    .load(url)
    .submit()
    .get()
}

fun Bitmap.asInputSteam(format: CompressFormat, compress: Int = 80): InputStream {
  return ByteArrayOutputStream().use {
    compress(format, compress, it)
    ByteArrayInputStream(it.toByteArray())
  }
}

fun getBitmapFormatFromMime(mimeType: String): CompressFormat? = when {
  mimeType.contains("png") -> CompressFormat.PNG
  mimeType.contains("webp") -> if (BuildCompat.isAtLeastR()) CompressFormat.WEBP_LOSSY else CompressFormat.WEBP
  mimeType.contains("jpg") -> CompressFormat.JPEG
  else -> null
}
