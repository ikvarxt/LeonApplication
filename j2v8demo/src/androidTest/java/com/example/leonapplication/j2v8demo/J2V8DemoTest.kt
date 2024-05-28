package com.example.leonapplication.j2v8demo

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.leonapplication.j2v8demo.normal.ClassJsonTest
import com.example.leonapplication.j2v8demo.normal.DynamicRegisterTest
import com.example.leonapplication.j2v8demo.normal.MapJsonTest
import com.example.leonapplication.j2v8demo.normal.V8Manager
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class J2V8DemoTest {

  private lateinit var manager: V8Manager

  @Before
  fun init() {
    manager = V8Manager()
  }

  @Test
  fun test_JsonClassPassing() {
    ClassJsonTest().attach(manager, "ClassJsonTest")
  }

  @Test
  fun test_mapJsonPassing() {
    MapJsonTest(manager.v8).attach(manager, "Native")
  }

  @Test
  fun test_dynamicRegister() {
    DynamicRegisterTest().attach(manager, "DynamicRegisterTest")
  }

  @After
  fun destroy() = manager.destroy()
}