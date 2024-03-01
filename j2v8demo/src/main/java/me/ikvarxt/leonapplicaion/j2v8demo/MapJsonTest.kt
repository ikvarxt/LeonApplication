@file:Suppress("MemberVisibilityCanBePrivate")

package me.ikvarxt.leonapplicaion.j2v8demo

import com.eclipsesource.v8.V8
import com.eclipsesource.v8.V8Array
import com.eclipsesource.v8.V8Object
import com.eclipsesource.v8.utils.V8ObjectUtils

class MapJsonTest(private val v8: V8) : ScriptTester {

  override val script: String = """
        function main(bool) {
            console.log(bool + "");
            Native.printMap([{"abc": "value"}, {"aaa": "ava"}]);
            
            let map = Native.getMap();
            console.log("map:" + map.a);
            
            let list = Native.getMapList();
            console.log("list:" + list.length);
        }
    """.trimIndent()

  override fun getMainArgs(): Array<String> {
    return arrayOf("false")
  }

  fun printMap(map: V8Array) {
    map.getObject(0).use {
      Console.log(it.toString())
    }
  }

  fun getMap(): V8Object {
    return V8ObjectUtils.toV8Object(v8, hashMapOf("a" to "b"))
  }

  fun getMapList(): V8Object {
    val map = hashMapOf("a" to "b")
    val list = listOf(map, map)
    return V8ObjectUtils.toV8Array(v8, list)
  }

  override fun registerMethods(methodHolder: V8Object) {
    methodHolder.jsMethod(this, ::printMap, V8Array::class.java)
    methodHolder.jsMethod(this, ::getMap)
    methodHolder.jsMethod(this, ::getMapList)
  }
}