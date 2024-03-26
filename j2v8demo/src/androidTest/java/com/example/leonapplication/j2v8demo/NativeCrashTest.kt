package com.example.leonapplication.j2v8demo

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.eclipsesource.v8.V8Object
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class NativeCrashTest {

  @Test
  fun nativeCrashWithinJ2v8() {
    val tester = ScriptTest()
    tester.start()
  }
}


private class ScriptTest : ScriptTester {

  private val v8 = V8Manager()

  fun start() = attach(v8, "ScriptTest")

  override val script: String
    get() = """
      function main() {
        try {
          const b = ScriptTest.crash()
        } catch (e) {
          console.log("error in js, e: " + e + " stack: " + e.stack)
        }
      }
    """.trimIndent()

  fun crash(): Boolean {
    val aa = 0
    a(aa)
    return true
  }

  fun a(a: Int) {
    val aa = "ab".toInt()
  }

  override fun getMainArgs(): Array<String> = arrayOf()

  override fun registerMethods(methodHolder: V8Object) {
    methodHolder
      .jsMethod(this, ::crash)
  }
}
