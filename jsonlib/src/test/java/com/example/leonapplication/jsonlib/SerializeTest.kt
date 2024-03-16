package com.example.leonapplication.jsonlib

import com.google.gson.Gson
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import kotlin.time.Duration
import kotlin.time.measureTime
import com.example.leonapplication.jsonlib.serialize as libSerialize

private val gson = Gson()
private fun Duration.logTime(tag: String) = println("$tag time is $this")
private const val gsonTest = false

class SerializeTest : StringSpec({

  fun serialize(obj: Any) = if (gsonTest) gson.toJson(obj) else libSerialize(obj)

  "EntityBean" {
    val person = PersonBean("Leon", 24)
    serialize(person) shouldBe """{"name":"Leon","age":24}"""
  }

  "String" {
    val sBean = StringBean("leon")
    val json = serialize(sBean)
    json shouldBe """{"s":"leon"}"""
  }

  "Int" {
    val bean = IntBean(2)
    serialize(bean) shouldBe """{"i":2}"""
  }

  "Long" {
    val bean = LongBean(1L)
    serialize(bean) shouldBe """{"l":1}"""
  }

  "FloatNumber" {
    val bean = DoubleBean(1.1, 1.2f)
    serialize(bean) shouldBe """{"d":1.1,"f":1.2}"""
  }

  "Null" {
    val bean = NullableBean(null)
    val json = if (gsonTest) """{}""" else """{"n":null}"""
    serialize(bean) shouldBe json
  }

  "ListBean" {
    val bean = ListBean(listOf("abc", "cde"))

    val json: String
    measureTime {
      json = serialize(bean)
    }.logTime("")

    json shouldBe """{"l":["abc","cde"]}"""
  }

  "RawList" {
    val bean = listOf(true, "abc")
    serialize(bean) shouldBe """[true,"abc"]"""
  }

  "ObjectBean" {
    val bean = ObjectBean(StringBean("leon"))
    serialize(bean) shouldBe """{"o":{"s":"leon"}}"""
  }

  "MultiLineString" {
    val bean = StringBean(
      """
      abcd
      cdd
      """.trimIndent()
    )
    serialize(bean) shouldBe """{"s":"abcd\ncdd"}"""
  }

  "exclude json name" {
    val bean = ExcludeBean("eee")
    serialize(bean) shouldBe "{}"
  }
})