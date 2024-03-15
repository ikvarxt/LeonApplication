package com.example.leonapplication.jsonlib

import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.time.Duration
import kotlin.time.measureTime
import com.example.leonapplication.jsonlib.serialize as libSerialize

private val gson = Gson()
private fun Duration.logTime(tag: String) = println("$tag time is $this")

class SerializeTest {

  private val gsonTest = false

  private fun serialize(obj: Any) = if (gsonTest) gson.toJson(obj) else libSerialize(obj)

  @Test fun testEntityBean() {
    val person = PersonBean("Leon", 24)
    assertEquals("""{"name":"Leon","age":24}""", serialize(person))
  }

  @Test fun testString() {
    val sBean = StringBean("leon")
    val json = serialize(sBean)
    assertEquals("""{"s":"leon"}""", json)
  }

  @Test fun testInt() {
    val bean = IntBean(2)
    assertEquals("""{"i":2}""", serialize(bean))
  }

  @Test
  fun testLong() {
    val bean = LongBean(1L)
    assertEquals("""{"l":1}""", serialize(bean))
  }

  @Test
  fun testFloatNumber() {
    val bean = DoubleBean(1.1, 1.2f)
    assertEquals("""{"d":1.1,"f":1.2}""", serialize(bean))
  }

  @Test
  fun testNull() {
    val bean = NullableBean(null)
    val json = if (gsonTest) """{}""" else """{"n":null}"""
    assertEquals(json, serialize(bean))
  }

  @Test
  fun testListBean() {
    val bean = ListBean(listOf("abc", "cde"))

    val json: String
    measureTime {
      json = serialize(bean)
    }.logTime("serialize")

    assertEquals("""{"l":["abc","cde"]}""", json)
  }

  @Test
  fun testRawList() {
    val bean = listOf(true, "abc")
    assertEquals("""[true,"abc"]""", serialize(bean))
  }

  @Test
  fun testObjectBean() {
    val bean = ObjectBean(StringBean("leon"))
    assertEquals("""{"o":{"s":"leon"}}""", serialize(bean))
  }

  @Test
  fun testMultiLineString() {
    val bean = StringBean(
      """
      abcd
      cdd
      """.trimIndent()
    )
    assertEquals("""{"s":"abcd\ncdd"}""", serialize(bean))
  }
}
