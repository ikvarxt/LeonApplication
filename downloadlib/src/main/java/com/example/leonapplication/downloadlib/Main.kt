package com.example.leonapplication.downloadlib

import okhttp3.OkHttpClient
import okhttp3.Request
import kotlin.math.min


fun main() {
  val client = OkHttpClient()

  val base = "http://localhost:8989"
  val file = "test-file-1"

  val url = "$base/download/$file"
  val getSizeUrl = "$base/getFileSize/$file"

  var size = 0
  Request.Builder()
    .url(getSizeUrl)
    .build().let { req ->
      client.newCall(req).execute().use { res ->
        if (res.isSuccessful) {
          size = res.body?.string()?.toInt() ?: 0
          println("get size success size $size")
        } else {
          println("failed get size, ${res.body?.string()}")
        }
      }
    }

  val chunk = 5

  for (i in 0 until size step chunk) {
    val start = i
    val end = min(start + chunk, size)

    val request = Request.Builder()
      .url(url)
      .addHeader("Range", "bytes=$start-$end")
      .build()

    client.newCall(request).execute().use { res ->
      if (res.isSuccessful) {
        println("success with res: ${res.body?.string()}")
      } else {
        println("failed with res, ${res.message}")
      }
    }
  }
}