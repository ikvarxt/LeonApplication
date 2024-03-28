package com.example.leonapplication.appupdatelib

data class AppUpdateInfo(
  val versionCode: Int,
  val versionName: String,
  val downloadUrl: String,
  val md5: String,
  val updateMessage: String?,
  val forceUpdate: Boolean = false,
)
