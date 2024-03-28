package com.example.leonapplication.app.partialfunc

import org.junit.Assert.*
import org.junit.BeforeClass
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ClassicImplTest {

  @Test
  fun testMeeting() {
    val event = ApplyEvent("a meeting", 999)
    assertTrue(student.handle(event)?.handleBy is Teacher)
  }

  @Test fun testWatchMovie() {
    val event = ApplyEvent("watch movie", 200)
    assertTrue(student tryHandle event is Student)
  }

  @Test fun testNewPlayground() {
    val event = ApplyEvent("new playground", 50_000)
    assertTrue(student tryHandle event is President)
  }

  @Test fun testNewBuilding() {
    val event = ApplyEvent("new building", 1_000_000)
    assertTrue(student tryHandle event == null)
  }

  private infix fun ApplyHandler.tryHandle(event: ApplyEvent): ApplyHandler? = handle(event)?.handleBy

  companion object {

    private lateinit var student: Student

    @JvmStatic
    @BeforeClass
    fun setUp() {
      val president = President()
      val teacher = Teacher(president)
      student = Student(teacher)
    }
  }
}

data class ApplyEvent(
  val name: String,
  val cost: Int,
)

data class HeldEvent(
  val applyEvent: ApplyEvent,
  val handleBy: ApplyHandler? = null,
)

interface ApplyHandler {
  val upper: ApplyHandler?

  fun handle(event: ApplyEvent): HeldEvent?
}

class President : ApplyHandler {
  override val upper: ApplyHandler? = null

  override fun handle(event: ApplyEvent): HeldEvent? {
    if (event.cost < 100000) {
      return HeldEvent(event, this)
    }
    return null
  }
}

class Teacher(president: President) : ApplyHandler {
  override val upper: ApplyHandler = president

  override fun handle(event: ApplyEvent): HeldEvent? {
    if (event.cost < 10000) {
      return HeldEvent(event, this)
    }
    return upper.handle(event)
  }
}

class Student(teacher: Teacher) : ApplyHandler {
  override val upper: ApplyHandler = teacher

  override fun handle(event: ApplyEvent): HeldEvent? {
    if (event.cost < 500) {
      return HeldEvent(event, this)
    }
    return upper.handle(event)
  }
}
