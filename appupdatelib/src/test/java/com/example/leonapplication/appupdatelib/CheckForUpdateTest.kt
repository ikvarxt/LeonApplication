package com.example.leonapplication.appupdatelib

import com.google.gson.Gson
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.Ignore
import org.junit.Test


class CheckForUpdateTest {

  private val client = OkHttpClient()

  @Ignore("no server on ci")
  @Test fun whenHaveUpdateThanInfo() {
    val info: AppUpdateInfo
    Request.Builder()
      .url("http://localhost:8989/checkUpdate")
      .build()
      .let { req ->
        client.newCall(req).execute().let { res ->
          val body = res.body?.string()
          info = Gson().fromJson(body, AppUpdateInfo::class.java)
        }
      }
    info shouldNotBe null
    info.versionCode shouldBe 2
  }
}