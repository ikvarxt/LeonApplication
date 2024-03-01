package com.example.leonapplication

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import kotlin.coroutines.CoroutineContext

class CancelFlowTest : CoroutineScope {

  private val flow = MutableStateFlow(0)
  private var i = 0

  @Before
  fun setup() {
    launch {
      while (true) {
        flow.emit(++i)
        delay(100)
      }
    }
  }

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

  override val coroutineContext: CoroutineContext
    get() = Dispatchers.Default
}