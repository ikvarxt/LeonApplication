@file:Suppress("unused")

package com.example.leonapplication.appupdatelib

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInstaller
import android.widget.Toast
import timber.log.Timber
import java.io.File
import java.io.IOException

const val APP_UPDATED_ACTION = "com.example.leonapplication.appupdate.ACTION"

/**
 * DOESN't work
 */
internal fun installApkWithSession(context: Context, file: File) {
  var session: PackageInstaller.Session? = null
  try {
    val installer = context.packageManager.packageInstaller
    val sessionParams = PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL)
    val id = installer.createSession(sessionParams)
    session = installer.openSession(id)
    session.addApkFile(file)
    val intent = Intent(context, AppUpdateActivity::class.java)
    intent.setAction(APP_UPDATED_ACTION)
    val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    val statusReceiver = pendingIntent.intentSender

    session.commit(statusReceiver)
  } catch (e: IOException) {
    Timber.e("can not install apk", e)
  } catch (e: Exception) {
    Timber.e("install failed", e)
  } finally {
    session?.abandon()
  }
}

private fun PackageInstaller.Session.addApkFile(file: File) {
  openWrite("package", 0, file.length()).use { output ->
    file.inputStream().use { fileStream ->
      val buffer = ByteArray(16384)
      var n: Int
      while (fileStream.read(buffer).also { n = it } >= 0) {
        output.write(buffer, 0, n)
      }
    }
  }
}

internal fun handleAppUpdateIntents(context: Context, intent: Intent?) {
  intent ?: return

  if (intent.action == APP_UPDATED_ACTION) {
    val extras = intent.extras ?: run {
      Timber.d("extras is null")
      return
    }
    Timber.d("receiver action APP_UPDATED_ACTION")
    val status: Int = extras.getInt(PackageInstaller.EXTRA_STATUS)
    val message: String = extras.getString(PackageInstaller.EXTRA_STATUS_MESSAGE, "no message")
    when (status) {
      PackageInstaller.STATUS_PENDING_USER_ACTION -> {
        // This test app isn't privileged, so the user has to confirm the install.
        @Suppress("DEPRECATION")
        context.startActivity(extras.get(Intent.EXTRA_INTENT) as? Intent)
      }

      PackageInstaller.STATUS_SUCCESS -> {
        Toast.makeText(context, "Install succeeded!", Toast.LENGTH_SHORT).show()
      }

      PackageInstaller.STATUS_FAILURE,
      PackageInstaller.STATUS_FAILURE_ABORTED,
      PackageInstaller.STATUS_FAILURE_BLOCKED,
      PackageInstaller.STATUS_FAILURE_CONFLICT,
      PackageInstaller.STATUS_FAILURE_INCOMPATIBLE,
      PackageInstaller.STATUS_FAILURE_INVALID,
      PackageInstaller.STATUS_FAILURE_STORAGE,
      -> {
        Toast.makeText(context, "Install failed! $status, $message", Toast.LENGTH_SHORT).show()
      }

      else -> {
        Toast.makeText(context, "Unrecognized status received from installer: $status", Toast.LENGTH_SHORT).show()
      }
    }
  }
}