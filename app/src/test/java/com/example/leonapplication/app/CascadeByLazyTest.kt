package com.example.leonapplication.app

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.measureTime

class CascadeByLazyTest {

  @Test
  fun testNormalByLazyCost() {
    val delegate = ByDelegate()
    val time = measureTime {
      delegate.c
    }
    println("time is $time")
  }

  @Test
  fun testNoLockClassByLazy() {
    val delegate = ByDelegate()
    val time = measureTime {
      delegate.d
    }
    assertTrue("time cost $time", time < 1.milliseconds)
  }

  @Test
  fun testByLazyLockClassInstance() {
    val byD = ByDelegateLockItself()
    val timeCost = measureTime {
      val c = byD.c
      assertEquals(3, c)
    }
    println("time $timeCost")
    assertTrue("time is $timeCost", timeCost < 2.seconds)
  }

  inner class ByDelegate {
    private val a by lazy { 0 }
    private val b by lazy { 1 }
    val c = a + b
    val d by lazy { a + b }
  }

  inner class ByDelegateLockItself {
    private val a by lazy(this) { 1 }
    private val b by lazy(this) { 2 }
    val c by lazy(this) { a + b }
  }
}