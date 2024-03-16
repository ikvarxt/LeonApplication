package com.example.leonapplication.jsonlib

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.io.StringReader

private lateinit var charReader: CharReader

class CharReaderTest : StringSpec({

  beforeTest {
    charReader = CharReader(StringReader("abc"))
  }

  "peek top" {
    charReader.peekNext() shouldBe 'a'
  }

  "peek twice" {
    charReader.peekNext() shouldBe 'a'
    charReader.peekNext() shouldBe 'a'
  }

  "get next char" {
    charReader.readNext() shouldBe 'a'
    charReader.readNext() shouldBe 'b'
  }

  "read next chars" {
    charReader.readNextChars(3) shouldBe "abc"
  }

  "read chars exceed length" {
    shouldThrow<Exception> {
      charReader.readNextChars(4)
    }
  }

  "done with read" {
    charReader.readNextChars(3)
    charReader.readNext() shouldBe null
  }

  "expect chars success" {
    shouldNotThrow<Exception> {
      charReader.expectText("ab", setOf('c'))
    }
  }

  "expect chars no expect" {
    shouldThrow<MalformedJSONException> {
      charReader.expectText("tbc", setOf())
    }
  }

  "expect not followed" {
    shouldThrow<MalformedJSONException> {
      charReader.expectText("ab", setOf(';'))
    }
  }
})