plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.kotlin.android)
}

android {
  namespace = "com.leon.app.proj_localaar"
  compileSdk = 34

  defaultConfig {
    minSdk = 29

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    consumerProguardFiles("consumer-rules.pro")
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  kotlin {
    jvmToolchain(11)
  }
}

dependencies {
  implementation(files("libs/extension-debug.aar"))
}