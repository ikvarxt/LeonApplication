package com.example.leonapplication.floatwindowdemo

import android.content.Context
import android.graphics.Color
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import timber.log.Timber
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
      renderer.reDraw()
    }
  }

}

class MyGLRenderer : GLSurfaceView.Renderer {

  override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
    GLES20.glClearColor(1f, 0f, 0f, 1f)
  }

  override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
    GLES20.glViewport(0, 0, width, height)
  }

  override fun onDrawFrame(gl: GL10?) {
    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
  }

  fun reDraw() {
    GLES20.glClearColor(1f, 0f, 0f, 1f)
    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
    Timber.d("re draw renderer")
  }
}