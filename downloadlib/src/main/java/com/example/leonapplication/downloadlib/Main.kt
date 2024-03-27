package com.example.leonapplication.downloadlib

import okhttp3.OkHttpClient
import okhttp3.Request
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import kotlin.concurrent.thread
import kotlin.math.ceil
import kotlin.math.min
import kotlin.time.measureTime

private const val threading = true

fun main() {
  val client = OkHttpClient()

  val base = "http://localhost:8989"
  val file = "test-file-1"

  val url = "$base/download/$file"
  val getSizeUrl = "$base/getFileSize/$file"

  val size = getFileSize(client, getSizeUrl)
  println("get file '$file' size ${size / 1024f / 1024f}MB")

  val chunk = 3
  val chunkSize = ceil(size / chunk.toFloat()).toInt()
  val sb = ByteBuffer.allocate(size + 1024)
  val map = HashMap<Int, ByteArray>()

  val time = measureTime {
    val threads = mutableListOf<Thread>()
    for (i in 0 until chunk) {
      val start = i * chunkSize
      val end = min(start + chunkSize, size)

      if (threading) {
        thread {
          downloadFile(url, client, start, end, map)
        }.also { threads += it }
      } else {
        downloadFile(url, client, start, end, map)
      }
    }
    if (threading) threads.forEach { it.join() }

    map.toSortedMap().forEach { (_, u) ->
      sb.put(u)
    }
    map.clear()
  }

  val res = sb.string()
  // println("raw: abcdefghijklmnopqresuvwxyz")
  println("download done with $time, chunked $chunk")
  sb.clear()
}

fun ByteBuffer.string(charset: Charset = StandardCharsets.UTF_8): String {
  val bytes = if (hasArray()) array()
  else ByteArray(remaining()).also { get(it) }
  return String(bytes, charset)
}

fun getFileSize(client: OkHttpClient, getSizeUrl: String): Int {
  var size = 0
  Request.Builder()
    .url(getSizeUrl)
    .build().let { req ->
      client.newCall(req).execute().use { res ->
        if (res.isSuccessful) {
          // size = res.headersContentLength().toInt()
          size = res.body?.string()?.toInt() ?: 0
        } else {
          println("failed get size, ${res.body?.string()}")
        }
      }
    }
  return size
}

fun downloadFile(url: String, client: OkHttpClient, start: Int, end: Int, map: MutableMap<Int, ByteArray>) {
  val request = Request.Builder()
    .url(url)
    .addHeader("Range", "bytes=$start-$end")
    .build()

  client.newCall(request).execute().use { res ->
    if (res.isSuccessful) {
      res.body?.bytes()?.let { bytes ->
        bytes.joinToString("") { "${it.toChar()}" }.let {
          // println("success with res: $it")
        }
        synchronized(Unit) {
          map.put(start, bytes)
        }
      } ?: {
        println("success with res: null bytes")
      }
    } else {
      println("failed with res, ${res.message}")
    }
  }
}