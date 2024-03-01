package me.ikvarxt.leonapplication.webviewdemo

import androidx.test.ext.junit.rules.activityScenarioRule
import com.example.leonapplication.webviewdemo.WebViewDemoActivity
import com.example.leonapplication.webviewdemo.WebViewManager
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.measureTime

class WebViewInitializeTest {

  @get:Rule
  var activityScenarioRule = activityScenarioRule<WebViewDemoActivity>()

  @Test
  fun testInitialWebView() {

    val scenario = activityScenarioRule.scenario

    scenario.onActivity { activity ->
      val manager = WebViewManager()
      val time = measureTime {
        manager.getWebView(activity)
      }
      assertTrue(time < 1000.milliseconds)
    }
  }
}