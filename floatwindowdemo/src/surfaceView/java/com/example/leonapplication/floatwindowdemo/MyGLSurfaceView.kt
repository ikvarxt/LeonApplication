package com.example.leonapplication.floatwindowdemo

import android.content.Context
import android.graphics.Color
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import timber.log.Timber
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class MyGLSurfaceView(context: Context) : GLSurfaceView(context) {

  private val renderer: MyGLRenderer

  init {
    setEGLContextClientVersion(2)
    renderer = MyGLRenderer()
    setRenderer(renderer)
    setZOrderOnTop(true)

    renderMode = RENDERMODE_CONTINUOUSLY

    setBackgroundColor(Color.parseColor("#44444488"))

    setOnClickListener {
      queueEvent {
        renderer.extractFrame()
      }
    }
  }
}

class MyGLRenderer : GLSurfaceView.Renderer {

  private val shaker = DoubleShaker(200)
  private var width = 0
  private var height = 0

  override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
    // GLES20.glClearColor(1f, 0f, 0f, 1f)
  }

  override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
    GLES20.glViewport(0, 0, width, height)
    Timber.d("onSurfaceChanged width $width, height $height")
    this.width = width
    this.height = height
  }

  override fun onDrawFrame(gl: GL10?) {
    if (shaker.shakeIt()) {
      GLES20.glClearColor(1f, 0f, 0f, 1f)
    } else {
      GLES20.glClearColor(0f, 1f, 0f, 1f)
    }
    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
  }

  fun extractFrame(): IntArray {
    val byteBuffer = ByteBuffer.allocate(width * height * 4).apply {
      order(ByteOrder.nativeOrder())
      GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, this)
    }.asIntBuffer()
    val pixels = IntArray(width * height)
    byteBuffer.get(pixels)
    // breakpoint here
    // frame value changes may because surface color changed
    Timber.d("byte $pixels")
    return pixels
  }
}