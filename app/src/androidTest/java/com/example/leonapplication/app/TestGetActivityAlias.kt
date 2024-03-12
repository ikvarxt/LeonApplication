package com.example.leonapplication.app

import android.content.pm.PackageManager
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Test
import org.junit.runner.RunWith
import timber.log.Timber

@RunWith(AndroidJUnit4::class)
class TestGetActivityAlias {

  private val context = InstrumentationRegistry.getInstrumentation().targetContext

  @Test
  fun test_getActivities() {
    val pm = context.packageManager
    val packageInfo = pm.getPackageInfo(context.packageName, PackageManager.GET_ACTIVITIES)
    val list = packageInfo.activities
    Timber.d("test_getActivities: ${list.joinToString()}")
    assert(list.find { it.name == "com.example.leonapplication.app.MainActivity" } != null)
  }
}