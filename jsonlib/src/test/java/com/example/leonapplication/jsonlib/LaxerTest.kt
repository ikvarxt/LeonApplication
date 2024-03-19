package com.example.leonapplication.jsonlib

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.io.StringReader

class LaxerTest : StringSpec({

  var lexer = Lexer(StringReader(""))

  fun String.lexer() = Lexer(StringReader(this)).also { lexer = it }

  "no token" {
    lexer.nextToken() shouldBe null
  }

  "brace token" {
    "{}".lexer()
    lexer.nextToken() shouldBe Token.LBRACE
    lexer.nextToken() shouldBe Token.RBRACE
  }

  "bracket token" {
    "[]".lexer()
    lexer.nextToken() shouldBe Token.LBRACKET
    lexer.nextToken() shouldBe Token.RBRACKET
  }

  "null token" {
    "null".lexer()
    lexer.nextToken() shouldBe Token.NullValue
  }

  "string token" {
    """{"a": "bb"}""".lexer()
    with(lexer) {
      nextToken() shouldBe Token.LBRACE
      nextToken() shouldBe Token.StringValue("a")
      nextToken() shouldBe Token.COLON
      nextToken() shouldBe Token.StringValue("bb")
      nextToken() shouldBe Token.RBRACE
    }
    lexer.nextToken() shouldBe null
  }

  "unexpected token" {
    "|".lexer()
    shouldThrow<MalformedJSONException> {
      lexer.nextToken()
    }
  }

  "boolean token" {
    "true, false".lexer()
    lexer.nextToken() shouldBe Token.TRUE
    lexer.nextToken()
    lexer.nextToken() shouldBe Token.FALSE
  }

  "long number token" {
    "100".lexer()
    lexer.nextToken() shouldBe Token.LongValue(100)
  }

  "double number token" {
    "100.1".lexer()
    lexer.nextToken() shouldBe Token.DoubleValue(100.1)
  }

  "negative number token" {
    "-23".lexer()
    lexer.nextToken() shouldBe Token.LongValue(-23)
  }
})