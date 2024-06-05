package me.ikvarxt.leonapp.javetnodejs

import com.caoccao.javet.interception.logging.JavetStandardConsoleInterceptor
import com.caoccao.javet.interop.NodeRuntime
import com.caoccao.javet.interop.V8Host
import java.io.Closeable

class NodeManager : Closeable {

  lateinit var node: NodeRuntime

  fun init() {
    node = V8Host.getNodeInstance().createV8Runtime()
    setupNodeRuntime()
  }

  private fun setupNodeRuntime() {
    JavetStandardConsoleInterceptor(node).register(node.globalObject)
  }

  override fun close() {
    node.close()
  }
}