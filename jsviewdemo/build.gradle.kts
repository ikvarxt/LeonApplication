plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.kotlin.android)
}

android {
  namespace = "me.ikvarxt.jsviewdemo"
  compileSdk = libs.versions.targetSdk.get().toInt()

  defaultConfig {
    minSdk = libs.versions.minSdk.get().toInt()

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
    jvmToolchain(libs.versions.kotlinJvmToolchain.get().toInt())
  }
}

dependencies {
  implementation(project(":javetnodejs"))
  implementation(project(":extension"))

  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.appcompat)
  implementation(libs.google.material)
  implementation(libs.androidx.activity)
  implementation(libs.timber)
  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.test.ext.junit)
  androidTestImplementation(libs.androidx.espresso.core)
}