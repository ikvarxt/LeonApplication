package com.example.leonapplication.app

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.PackageManager.NameNotFoundException
import com.example.leonapplication.extension.globalApp
import timber.log.Timber

class App : Application() {

  override fun onCreate() {
    super.onCreate()

    globalApp = this

    Timber.plant(Timber.DebugTree())
  }
}

fun Context.getAllActivities(): List<LaunchEntryActivity>? = try {
  packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
    .activities
    .filter {
      it.name.startsWith(packageName) and it.name.equals(tryGetActivityName()).not()
    }.map {
      LaunchEntryActivity(it.name.substringAfterLast("."), it.name)
    }
} catch (e: NameNotFoundException) {
  Timber.e(e, "getAllActivities: name not found")
  null
}

fun Context.tryGetActivityName(): String? {
  return (this as? Activity)?.componentName?.className
}

data class LaunchEntryActivity(
  val name: String,
  val componentClassName: String,
)
