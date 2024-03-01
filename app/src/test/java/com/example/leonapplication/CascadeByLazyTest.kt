package com.example.leonapplication

import org.junit.Test
import org.junit.Assert.*
import kotlin.time.Duration.Companion.milliseconds
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
  fun testCascadeByLazyCost() {
    val delegate = ByDelegate()
    val time = measureTime {
      delegate.d
    }
    println("time is $time")
  }

  @Test
  fun testNoLockClassByLazy() {
    val time = measureTime {
      ByDelegate().d
    }
    assertTrue("time cost $time", time < 1.milliseconds)
  }

  @Test
  fun testByLazyLockClassInstance() {
    fun byLazyLockClassItself() {
      val c = ByDelegateLockItself().c
      assertEquals(3, c)
    }

    val timeCost = measureTime {
      repeat(3) {
        byLazyLockClassItself()
      }
    }
    assertTrue("time is $timeCost", timeCost > 2.milliseconds)
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