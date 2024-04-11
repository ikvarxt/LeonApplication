package com.example.leonapplication.transparentdesktop

import android.Manifest
import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.leonapplication.extension.button
import com.example.leonapplication.extension.list

private var PERMISSIONS_REQUIRED = arrayOf(Manifest.permission.CAMERA)

class TransparentDesktopActivity : AppCompatActivity() {

  private val context: Context get() = this

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    list {
      button("set wallpaper") {
        setWallpaper()
      }
    }

    if (!hasPermissions(context)) {
      // Request camera-related permissions
      activityResultLauncher.launch(PERMISSIONS_REQUIRED)
    }
  }

  private val activityResultLauncher =
    registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions())
    { permissions ->
      // Handle Permission granted/rejected
      var permissionGranted = true
      permissions.entries.forEach {
        if (it.key in PERMISSIONS_REQUIRED && it.value.not())
          permissionGranted = false
      }
      if (!permissionGranted) {
        Toast.makeText(context, "Permission request denied", Toast.LENGTH_LONG).show()
      } else {
        setWallpaper()
      }
    }

  private fun setWallpaper() {
    Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER).apply {
      putExtra(
        WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
        ComponentName(context, TransparentWallpaperSvc::class.java)
      )
      addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
    }.also { startActivity(it) }
  }

  companion object {
    fun hasPermissions(context: Context) = PERMISSIONS_REQUIRED.all {
      ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }
  }
}