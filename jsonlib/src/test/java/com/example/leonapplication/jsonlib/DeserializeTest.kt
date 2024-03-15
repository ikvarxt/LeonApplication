package com.example.leonapplication.jsonlib

import org.junit.Assert.assertEquals
import org.junit.Test

class DeserializeTest {

  @Test fun testNull() {
    assertEquals(null, deserialize("{}"))
  }
}
