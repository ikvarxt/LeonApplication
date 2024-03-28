@file:Suppress("RedundantLambdaOrAnonymousFunction")

package com.example.leonapplication.app.partialfunc

import org.junit.Assert.*
import org.junit.Test

typealias ApplyCheck = (ApplyEvent) -> Boolean
typealias ApplyResult = (ApplyEvent) -> Handler?

sealed class Handler {
  data object Student : Handler()
  data object Teacher : Handler()
  data object President : Handler()
  data object Reject : Handler()
}

class ChainPattern {

  private val student = {
    val definedAt: ApplyCheck = { it.cost < 500 }
    val handler: ApplyResult = { Handler.Student }
    PartialFunction(definedAt, handler)
  }()

  private val teacher = {
    val definedAt: ApplyCheck = { it.cost < 10000 }
    val handler: ApplyResult = { Handler.Teacher }
    PartialFunction(definedAt, handler)
  }()

  private val president = {
    val definedAt: ApplyCheck = { it.cost < 100_000 }
    val handler: ApplyResult = { Handler.President }
    PartialFunction(definedAt, handler)
  }()

  private val reject = {
    val d: ApplyCheck = { it.cost < Int.MAX_VALUE }
    val h: ApplyResult = { Handler.Reject }
    PartialFunction(d, h)
  }()

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

class PartialFunction<in P1, out R>(private val definetAt: (P1) -> Boolean, private val f: (P1) -> R) : (P1) -> R {
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
