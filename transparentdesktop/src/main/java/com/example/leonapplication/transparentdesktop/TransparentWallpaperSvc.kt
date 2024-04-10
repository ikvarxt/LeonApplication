package com.example.leonapplication.transparentdesktop

import android.annotation.SuppressLint
import android.content.Context
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import timber.log.Timber

class TransparentWallpaperSvc : WallpaperService() {

  private var engine: Engine? = null

  override fun onCreateEngine(): Engine {
    return WallpaperEngine(applicationContext).also { engine = it }
  }

  inner class WallpaperEngine(private val context: Context) : Engine() {

    private val scope by lazy { MainScope() }

    override fun onCreate(surfaceHolder: SurfaceHolder?) {
      super.onCreate(surfaceHolder)
      Timber.d("created")
    }

    @SuppressLint("MissingPermission")
    override fun onSurfaceCreated(holder: SurfaceHolder?) {
      super.onSurfaceCreated(holder)

      holder ?: throw IllegalStateException("holder is null")

      scope.launch {
        openCamera(context)
        createCaptureSession(holder.surface)
        startPreview(holder.surface)
      }
    }

    override fun onSurfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
      super.onSurfaceChanged(holder, format, width, height)
    }

    override fun onSurfaceDestroyed(holder: SurfaceHolder?) {
      super.onSurfaceDestroyed(holder)
    }

    override fun onDesiredSizeChanged(desiredWidth: Int, desiredHeight: Int) {
      super.onDesiredSizeChanged(desiredWidth, desiredHeight)
    }

    override fun onVisibilityChanged(visible: Boolean) {
      super.onVisibilityChanged(visible)
    }

    override fun onDestroy() {
      super.onDestroy()
    }
  }
}
