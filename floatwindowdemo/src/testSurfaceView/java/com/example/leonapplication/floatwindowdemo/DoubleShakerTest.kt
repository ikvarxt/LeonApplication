package com.example.leonapplication.floatwindowdemo

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class DoubleShakerTest {

  private lateinit var shaker: DoubleShaker

  @Before
  fun setUp() {
    shaker = DoubleShaker(200)
  }

  @Test
  fun whenBeginningThanShakeTrue() {
    assertTrue(shaker.shakeIt())
  }

  @Test
  fun when200msAgoShakeFalse() {
    assertTrue(shaker.shakeIt())
    Thread.sleep(210)
    assertFalse(shaker.shakeIt())
  }

  @Test
  fun when400msAgoThanShakeTrue() {
    assertTrue(shaker.shakeIt())
    Thread.sleep(210)
    assertFalse(shaker.shakeIt())
    Thread.sleep(210)
    assertTrue(shaker.shakeIt())
  }
}