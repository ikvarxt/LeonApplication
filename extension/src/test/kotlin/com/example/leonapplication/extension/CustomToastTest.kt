package com.example.leonapplication.extension

import android.widget.Button
import org.junit.Assert.*
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowToast

@RunWith(RobolectricTestRunner::class)
class CustomToastTest {

  @Ignore("not worked")
  @Test fun testToastShow() {
    Robolectric.buildActivity(ToastTestActivity::class.java).use { controller ->
      controller.setup()

      val activity = controller.get()

      val btn = activity.findViewById<Button>(ToastTestActivity.BTN_ID)
      btn.performClick()

      val toastText = ShadowToast.getTextOfLatestToast()
      assertTrue(toastText.contains("kiss kiss"))
    }
  }

}