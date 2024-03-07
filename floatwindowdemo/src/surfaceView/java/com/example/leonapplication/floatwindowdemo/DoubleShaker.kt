package com.example.leonapplication.floatwindowdemo

class DoubleShaker(
  private val gapMs: Int,
) {

  private var start: Long = 0L
  private var current: Boolean = true

  fun shakeIt(): Boolean {
    // start count when first shakeIt been called
    if (start == 0L) start = System.currentTimeMillis()

    val cur = System.currentTimeMillis()

    if (cur - start >= gapMs) {
      start = System.currentTimeMillis()
      current = current.not()
    }
    return current
  }
}