package com.example.leonapplication.app

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.pm.ActivityInfo
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
  fun matchPackage(list: List<String>): (ActivityInfo) -> Boolean {
    return fun(info: ActivityInfo): Boolean {
      return list.any { info.name.startsWith(it) } and
        (info.name == tryGetActivityName()).not()
    }
  }

  val packageList = listOf(packageName, "me.ikvarxt")
  packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
    .activities
    .filter(matchPackage(packageList))
    .map {
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
