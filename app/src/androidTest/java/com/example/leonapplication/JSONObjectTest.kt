package com.example.leonapplication

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class JSONObjectTest {

  @Test
  fun testRawMapToJson() {
    val map = hashMapOf("a" to "b")
    val json = JSONObject().apply {
      put("id", 200)
      put("map", map)
    }
    assertNotEquals("{\"id\":200,\"map\":{\"a\":\"b\"}}", json.toString())
  }

  @Test
  fun testMapWithJsonObject() {
    val map = hashMapOf("a" to "b")
    val json = JSONObject().apply {
      put("id", 200)
      put("map", JSONObject(map as Map<*, *>))
    }
    assertEquals("{\"id\":200,\"map\":{\"a\":\"b\"}}", json.toString())
  }
}