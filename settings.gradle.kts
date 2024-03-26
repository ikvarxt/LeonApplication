@file:Suppress("UnstableApiUsage")

pluginManagement {
  repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
  }
}
dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    google()
    mavenCentral()
  }
}

rootProject.name = "LeonApplicationPublic"

include(":app")
//include(":asmdemo")
//includeBuild("asmdemo-build-logic")
//include(":floatwindowdemo")
include(":j2v8demo")
//include(":webviewdemo")
//include(":tdddemo")
//include(":jsonlib")
