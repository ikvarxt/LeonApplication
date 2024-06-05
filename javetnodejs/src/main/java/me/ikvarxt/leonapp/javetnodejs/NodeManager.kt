package me.ikvarxt.leonapp.javetnodejs

import com.caoccao.javet.interception.logging.JavetStandardConsoleInterceptor
import com.caoccao.javet.interop.NodeRuntime
import com.caoccao.javet.interop.V8Host
import com.caoccao.javet.interop.converters.JavetProxyConverter
import java.io.Closeable

class NodeManager : Closeable {

  lateinit var node: NodeRuntime

  private lateinit var consoleInterceptor: JavetStandardConsoleInterceptor

  fun init() {
    node = V8Host.getNodeInstance().createV8Runtime()
    setupNodeRuntime()
  }

  /**
   * load the [script] context to the node vm
   */
  fun loadContext(script: String) {
    node.getExecutor(script).executeVoid()
  }

  /**
   * register a Java or Kotlin class as JavaScript callback,
   * T can be a static class with JvmStatic annotated or a normal class
   * which need be instanced in the JavaScript side.
   */
  inline fun <reified T> registerCallbackObject() {
    val className = T::class.simpleName
      ?: error("failed to get callback name ${T::class}")
    node.globalObject.set(className, T::class.java)
  }

  private fun setupNodeRuntime() {
    consoleInterceptor = JavetStandardConsoleInterceptor(node)
    consoleInterceptor.register(node.globalObject)
    node.converter = JavetProxyConverter()
  }

  override fun close() {
    consoleInterceptor.unregister(node.globalObject)
    node.close()
  }
}