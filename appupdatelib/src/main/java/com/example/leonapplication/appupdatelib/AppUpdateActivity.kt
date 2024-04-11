package com.example.leonapplication.appupdatelib

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.webkit.MimeTypeMap
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.example.leonapplication.extension.button
import com.example.leonapplication.extension.list
import timber.log.Timber
import java.io.File


class AppUpdateActivity : AppCompatActivity() {

  private var enqueueId: Long = 0
  private val fileName = "app-debug-1.2.apk"
  private val folderName = "update-apks/"
  private val downloadUrl = "http://localhost:8989/download/$fileName"
  private val externalFolder by lazy {
    File(getExternalFilesDir(null), folderName)
  }
  private val externalApk by lazy {
    File(externalFolder, fileName)
  }
  private val permissionRequester = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
    if (packageManager.canRequestPackageInstalls()) tryInstall()
    else Timber.d("not permit permission")
  }
  private val dm by lazy { getSystemService(DOWNLOAD_SERVICE) as DownloadManager }
  private val context get() = this

  private val receiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
      val id = intent?.getLongExtra(
        DownloadManager.EXTRA_DOWNLOAD_ID, 0L
      ) ?: 0L

      if (enqueueId == id) {
        Timber.d("download complete")

        if (packageManager.canRequestPackageInstalls()) {
          tryInstall()
        } else {
          Timber.d("request permission")
          Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
            setData("package:$packageName".toUri())
          }.also {
            permissionRequester.launch(it)
          }
        }
      } else {
        Timber.d("receiver id $id not mine download")
      }
    }
  }

  override fun onNewIntent(intent: Intent?) {
    super.onNewIntent(intent)

    // handleAppUpdateIntents(this, intent)
  }

  @SuppressLint("SetTextI18n")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    list {
      button("download apk") {
        downloadApk()
      }
      button("install") {
        installApk(context, externalApk)
      }
      button("delete apk") {
        if (externalApk.exists()) externalApk.delete()
      }
      button("v$appVersionName") {}
    }

    ContextCompat.registerReceiver(
      this, receiver,
      IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
      ContextCompat.RECEIVER_EXPORTED
    )
    Handler(Looper.getMainLooper()).postDelayed({
      tryInstall()
    }, 1000)
  }

  private fun tryInstall() {
    if (externalApk.exists()) installApk(context, externalApk)
  }

  private val Context.appVersionName: String
    get() {
      return packageManager.getPackageInfo(packageName, 0).versionName
    }

  private fun downloadApk() {
    if (externalApk.exists()) {
      if (externalApk.delete().not()) Timber.e("delete exists file failed")
    }
    val req = DownloadManager.Request(downloadUrl.toUri()).apply {
      setDestinationInExternalFilesDir(this@AppUpdateActivity, null, "$folderName$fileName")
      setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
      setTitle("Update download for LeonApplication")
      setMimeType(MimeTypeMap.getSingleton().getMimeTypeFromExtension("apk"))
    }
    enqueueId = dm.enqueue(req)
    Timber.d("enqueued download id $enqueueId")
  }

  override fun onDestroy() {
    super.onDestroy()

    unregisterReceiver(receiver)
  }

  private fun installApk(context: Context, file: File?): Boolean {
    if (file?.exists() != true) {
      return false
    }

    // DOESN't work
    // installApkWithSession(context, file)
    // return true

    Timber.d("install apk, file $file")
    val intent = Intent()
    intent.action = Intent.ACTION_VIEW
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    intent.addCategory(Intent.CATEGORY_DEFAULT)

    val uri = FileProvider.getUriForFile(this, "$packageName.fileprovider", file)
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    intent.setDataAndType(uri, MimeTypeMap.getSingleton().getMimeTypeFromExtension("apk"))
    val resInfoList = context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
    for (resolveInfo in resInfoList) {
      context.grantUriPermission(
        resolveInfo.activityInfo.packageName,
        uri,
        Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
      )
    }
    context.startActivity(intent)
    return true
  }
}
