@file:Suppress("MemberVisibilityCanBePrivate")

package com.example.leonapplication.j2v8demo.normal

import com.eclipsesource.v8.V8Object
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class ClassJsonTest : ScriptTester {

  override val script: String = """
        function main(bool) {
            const classJson = ClassJsonTest.getInstance();
            const cl = JSON.parse(classJson);
            console.log("class: " + cl.name);
        }
    """.trimIndent()

  override fun getMainArgs(): Array<String> {
    return arrayOf("false")
  }

  fun getInstance() = toJson(Instance("value"))

  @Suppress("unused")
  class Instance(
    private val name: String,
  )

  override fun registerMethods(methodHolder: V8Object) {
    methodHolder.jsMethod(this, ::getInstance)
  }

  private inline fun <reified T> toJson(obj: T): String {
    val moshi = Moshi.Builder()
      .addLast(KotlinJsonAdapterFactory())
      .build()
    val jsonAdapter = moshi.adapter(T::class.java)
    return jsonAdapter.toJson(obj)
  }
}