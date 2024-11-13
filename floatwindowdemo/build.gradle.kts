plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.kotlin.android)
}

android {
  namespace = "com.example.leonapplication.floatwindowdemo"
  compileSdk = libs.versions.targetSdk.get().toInt()

  defaultConfig {
    minSdk = libs.versions.minSdk.get().toInt()

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    consumerProguardFiles("consumer-rules.pro")
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro"
      )
    }
  }
  flavorDimensions += "viewType"
  productFlavors {
    create("normalView") {
      dimension = "viewType"
    }
    create("surfaceView") {
      dimension = "viewType"
    }
  }
  kotlin {
    jvmToolchain(libs.versions.kotlinJvmToolchain.get().toInt())
  }
}

dependencies {

  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.appcompat)
  implementation(libs.google.material)
  implementation(libs.constraintlayout)
  implementation(libs.timber)
  implementation(project(":extension"))
  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.test.ext.junit)
  androidTestImplementation(libs.androidx.espresso.core)
}