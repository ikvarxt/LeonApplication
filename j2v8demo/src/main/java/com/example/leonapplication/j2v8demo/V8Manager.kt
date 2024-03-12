package com.example.leonapplication.j2v8demo

import android.util.Log
import com.eclipsesource.v8.V8
import com.eclipsesource.v8.V8Array
import com.eclipsesource.v8.V8Function
import com.eclipsesource.v8.V8Object
import kotlin.jvm.internal.CallableReference
import kotlin.reflect.KFunction

fun <T : IV8MockObject> register(
  v8: V8,
  mockObject: T,
  jsObjName: String = mockObject.javaClass.simpleName,
) {
  V8Object(v8).use { native ->
    mockObject.registerMethods(native)
    v8.add(jsObjName, native)
  }
}

fun V8Object.jsMethod(
  obj: Any, method: KFunction<*>, vararg params: Class<*> = arrayOf(),
): V8Object {
  val methodName = (method as CallableReference).name
  return registerJavaMethod(obj, methodName, methodName, params)
}

@Suppress("unused")
private fun V8Object.jsMethodOf(
  obj: Any, method: KFunction<*>,
): V8Object {
  val m = method as CallableReference
  return registerJavaMethod(obj, m.name, m.name, m.parameters.map { it::class.java }.toTypedArray())
}

interface IV8MockObject {
  fun registerMethods(methodHolder: V8Object)
}

object Console : IV8MockObject {

  fun log(msg: String) = Log.d("Console", msg)

  override fun registerMethods(methodHolder: V8Object) {
    methodHolder.jsMethod(this, Console::log, String::class.java)
//        methodHolder.jsMethodOf(this, ::log)
  }
}

class V8Manager {

  val v8: V8 = V8.createV8Runtime()

  init {
    register(v8, Console, "console")
  }

  fun registerNativeObject(native: IV8MockObject, name: String) {
    register(v8, native, name)
  }

  fun loadScript(script: String) {
    v8.executeVoidScript(script)
  }

  fun executeScript(functionName: String, vararg param: String) {
    try {
      val v8Function = v8.getObject(functionName) as V8Function

      val v8Param = V8Array(v8)
      param.forEach { v8Param.push(it) }
      v8Function.call(v8, v8Param)

      v8Param.close()
      v8Function.close()
    } catch (e: Exception) {
      throw e
    }
  }

  fun destroy() {
    v8.release(true)
  }

}