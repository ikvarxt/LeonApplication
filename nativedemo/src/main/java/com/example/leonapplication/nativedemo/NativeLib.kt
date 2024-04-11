package com.example.leonapplication.nativedemo

class NativeLib {

  /**
   * A native method that is implemented by the 'nativedemo' native library,
   * which is packaged with this application.
   */
  external fun stringFromJNI(): String

  external fun allocMemory(size: Long)

  companion object {
    // Used to load the 'nativedemo' library on application startup.
    init {
      System.loadLibrary("nativedemo")
    }
  }
}
