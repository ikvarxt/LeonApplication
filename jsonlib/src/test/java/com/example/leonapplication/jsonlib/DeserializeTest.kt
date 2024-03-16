package com.example.leonapplication.jsonlib

import com.example.leonapplication.jsonlib.deserialize.deserialize
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class DeserializeTest : FunSpec({

  test("EntityBean") {
    val json = """{"name": "leon", "age": 24}"""
    val obj = deserialize<PersonBean>(json)
    obj.name shouldBe "leon"
  }

  test("Int") {
    val json = """{"i": 24}"""
    val obj = deserialize<IntBean>(json)
    obj.i shouldBe 24
  }

  test("Double") {
    val json = """{"d": 24.24, "f": 0.24}"""
    val obj = deserialize<DoubleBean>(json)
    obj.d shouldBe 24.24
    obj.f shouldBe 0.24f
  }

  test("String") {
    val json = """{"s": "leon"}"""
    val obj = deserialize<StringBean>(json)
    obj.s shouldBe "leon"
  }

  test("Long") {
    val json = """{"l": 24}"""
    val obj = deserialize<LongBean>(json)
    obj.l shouldBe 24L
  }

  test("Object") {
    val json = """{"o": {"s": "leon"}}"""
    val obj = deserialize<ObjectBean>(json)
    obj.o shouldBe StringBean("leon")
  }

  test("ListBean") {
    val bean = ListBean(listOf("abc", "cde"))

    val json = """{"l":["abc","cde"]}"""
    val obj = deserialize<ListBean>(json)
    obj.l shouldBe bean.l
  }

  test("RawList") {
    val json = """["abc", "bcd"]"""
    val obj = deserialize<List<String>>(json)
    obj shouldBe listOf("abc", "bcd")
  }
}
)