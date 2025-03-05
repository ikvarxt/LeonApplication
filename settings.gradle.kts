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
// demos
//include(":asmdemo")
//includeBuild("asmdemo-build-logic")
//include(":floatwindowdemo")
//include(":j2v8demo")
//include(":webviewdemo")
//include(":tdddemo")
//include(":jsonlib")
//include(":nativedemo")
//include(":appupdatelib")
//include(":downloadlib")
//include(":transparentdesktop")
//include(":javetnodejs")
//include(":jsviewdemo")
//include(":recyclerviewdemo")

include(":mavenpub")
include(":mavenpub:proj_deps")
include(":mavenpub:proj_localaar")

// ext
include(":extension")
// build
//includeBuild("build-config")
