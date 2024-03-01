package com.example.leonapplication

import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DisplayTest {

  @get:Rule
  var activityScenarioRule = activityScenarioRule<MainActivity>()

  @Test
  fun test_getRealScreenSize() {
    val activity = activityScenarioRule.scenario
    activity.onActivity { context ->
      val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
      val metrics = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
        val metrics = wm.currentWindowMetrics
        metrics.bounds.right to metrics.bounds.bottom
      } else {
        val metrics = DisplayMetrics()
        @Suppress("DEPRECATION")
        wm.defaultDisplay.getMetrics(metrics)
        metrics.widthPixels to metrics.heightPixels
      }
      assert(metrics == 1080 to 2160)
    }
  }
}