package com.example.leonapplication.jsonlib

import com.example.leonapplication.jsonlib.deserialize.deserialize
import org.junit.Assert.assertEquals
import org.junit.Test

class DeserializeTest {

  @Test fun testEntityBean() {
    val json = """{"name": "leon", "age": 24}"""
    val obj = deserialize<PersonBean>(json)
    assertEquals("leon", obj.name)
  }

  @Test fun testInt() {
    val json = """{"i": 24}"""
    val obj = deserialize<IntBean>(json)
    assertEquals(24, obj.i)
  }

  @Test fun testDouble() {
    val json = """{"d": 24.24, "f": 0.24}"""
    val obj = deserialize<DoubleBean>(json)
    assertEquals(24.24, obj.d, 0.00003)
    assertEquals(0.24f, obj.f, 0.00003f)
  }

  @Test fun testString() {
    val json = """{"s": "leon"}"""
    val obj = deserialize<StringBean>(json)
    assertEquals("leon", obj.s)
  }

  @Test fun testLong() {
    val json = """{"l": 24}"""
    val obj = deserialize<LongBean>(json)
    assertEquals(24L, obj.l)
  }

  @Test fun testObject() {
    val json = """{"o": {"s": "leon"}}"""
    val obj = deserialize<ObjectBean>(json)
    assertEquals(StringBean("leon"), obj.o)
  }

  @Test fun testListBean() {
    val bean = ListBean(listOf("abc", "cde"))

    val json = """{"l":["abc","cde"]}"""
    val obj = deserialize<ListBean>(json)
    assertEquals(bean.l, obj.l)
  }

  @Test fun testRawList() {
    val json = """["abc", "bcd"]"""
    val obj = deserialize<List<String>>(json)
    assertEquals(listOf("abc", "bcd"), obj)
  }
}
