package me.ikvarxt.leonapp.javetnodejs

object NativeCallback {

  @JvmStatic
  fun nativeCallback(arg: Any? = null): String {
    println("nativeCallback called, $arg")
    return arg.toString()
  }
}