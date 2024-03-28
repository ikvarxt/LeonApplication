@file:Suppress("RedundantLambdaOrAnonymousFunction")

package com.example.leonapplication.app.partialfunc

import org.junit.Assert.*
import org.junit.Test


class ChainPattern {

  private val student = ApplyFunc(
    { it.cost < 500 },
    { Handler.Student }
  )

  private val teacher = ApplyFunc(
    { it.cost < 10000 },
    { Handler.Teacher }
  )

  private val president = ApplyFunc(
    { it.cost < 100_000 },
    { Handler.President }
  )

  private val reject = ApplyFunc(
    { true },
    { Handler.Reject }
  )

  val applyChain = student orElse teacher orElse president orElse reject

  @Test
  fun testMeeting() {
    val event = ApplyEvent("a meeting", 999)
    assertEquals(applyChain(event), Handler.Teacher)
  }

  @Test fun testWatchMovie() {
    val event = ApplyEvent("watch movie", 200)
    assertEquals(applyChain(event), Handler.Student)
  }

  @Test fun testNewPlayground() {
    val event = ApplyEvent("new playground", 50_000)
    assertEquals(applyChain(event), Handler.President)
  }

  @Test fun testNewBuilding() {
    val event = ApplyEvent("new building", 1_000_000)
    assertEquals(applyChain(event), Handler.Reject)
  }
}

typealias ApplyCheck = (ApplyEvent) -> Boolean
typealias ApplyResult = (ApplyEvent) -> Handler

sealed class Handler {
  data object Student : Handler()
  data object Teacher : Handler()
  data object President : Handler()
  data object Reject : Handler()
}

open class PartialFunction<in P1, out R>(private val definetAt: (P1) -> Boolean, private val f: (P1) -> R) : (P1) -> R {
  override fun invoke(p1: P1): R {
    if (definetAt(p1)) {
      return f(p1)
    } else {
      throw IllegalArgumentException("Value: ($p1) isn't supported by this function")
    }
  }

  fun isDefinedAt(p1: P1) = definetAt(p1)
}

infix fun <P1, R> PartialFunction<P1, R>.orElse(that: PartialFunction<P1, R>): PartialFunction<P1, R> {
  return PartialFunction({ this.isDefinedAt(it) || that.isDefinedAt(it) }) {
    when {
      this.isDefinedAt(it) -> this(it)
      else -> that(it)
    }
  }
}

class ApplyFunc(d: ApplyCheck, h: ApplyResult) : PartialFunction<ApplyEvent, Handler>(d, h)