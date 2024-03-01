package com.example.leonapplication

import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppTest {

  @get:Rule
  val activityScenario = activityScenarioRule<MainActivity>()

  @Test
  fun testGetAllActivities() {
    activityScenario.scenario.onActivity { activity ->
      val activities = activity.getAllActivities() ?: throw AssertionError("no activities")

      assert(activities.isNotEmpty())
      assert(activities.all { it.componentClassName != activities.javaClass.canonicalName })
    }
  }
}