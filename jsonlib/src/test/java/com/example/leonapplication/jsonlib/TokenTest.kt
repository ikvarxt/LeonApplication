package com.example.leonapplication.jsonlib

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class TokenTest : StringSpec({

  "null token" {
    Token.NullValue.value shouldBe null
  }

  "boolean true token has true value" {
    Token.TRUE.value shouldBe true
  }
})