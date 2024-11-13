plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.kotlin.android)
}

android {
  namespace = "com.example.leonapplication.extension"
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
  kotlinOptions {
    jvmTarget = "1.8"
  }
}

dependencies {
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.activity)
  // testImplementation(libs.junit)
  // androidTestImplementation(libs.androidx.test.ext.junit)
  // androidTestImplementation(libs.androidx.espresso.core)
}