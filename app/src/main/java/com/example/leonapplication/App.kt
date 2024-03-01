package com.example.leonapplication

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.PackageManager.NameNotFoundException
import timber.log.Timber

class App : Application() {

  override fun onCreate() {
    super.onCreate()

    Timber.plant(Timber.DebugTree())
  }
}

fun Context.getAllActivities(): List<LaunchEntryActivity>? = try {
  packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
    .activities
    .filter {
      it.name.startsWith(packageName) and
        it.name.equals("com.example.leonapplication.MainActivity").not()
    }.map {
      LaunchEntryActivity(
        it.name.substring(it.name.lastIndexOf(".") + 1, it.name.length),
        it.name
      )
    }
} catch (e: NameNotFoundException) {
  Timber.e(e, "getAllActivities: name not found")
  null
}

data class LaunchEntryActivity(
  val name: String,
  val componentClassName: String,
)
