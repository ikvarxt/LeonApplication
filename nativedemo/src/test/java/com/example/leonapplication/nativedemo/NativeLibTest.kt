package com.example.leonapplication.nativedemo

import org.junit.Assert.*
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class NativeLibTest {

  @Test fun testNativeLib() {
    assertEquals(NativeLib().stringFromJNI(), "Hello from C++")
  }
}
