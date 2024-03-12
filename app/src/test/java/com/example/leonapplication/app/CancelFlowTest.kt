package com.example.leonapplication.app

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.BeforeClass
import org.junit.Test

class CancelFlowTest {

  @Test
  fun cancelFlowCollectScopeTest() {
    runBlocking {
      val job = launch {
        flow.collect {
          assertEquals(i, it)
          cancel()
        }
      }
      delay(50)
      assert(job.isCancelled)
    }
  }

  companion object {

    private val flow = MutableStateFlow(0)
    private var i = 0

    @OptIn(DelicateCoroutinesApi::class)
    @JvmStatic
    @BeforeClass
    fun setup() {
      GlobalScope.launch {
        while (true) {
          flow.emit(++i)
          delay(100)
        }
      }
    }
  }
}