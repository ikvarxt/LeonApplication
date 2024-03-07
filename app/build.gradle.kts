plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.android.kotlin)
}

android {
  namespace = "com.example.leonapplication"
  compileSdk = 34

  defaultConfig {
    applicationId = "com.example.leonapplication"
    minSdk = 29
    targetSdk = 34
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    vectorDrawables {
      useSupportLibrary = true
    }
    // here to select which variant will be using when implement :floatwindowdemo module
    // first variant will be selected
    missingDimensionStrategy("viewType", "surfaceView", "normalView")
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
  kotlinOptions {
    jvmTarget = "1.8"
  }
  buildFeatures {
    compose = true
  }
  composeOptions {
    kotlinCompilerExtensionVersion = "1.5.1"
  }
  packaging {
    resources {
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
  }
}

dependencies {

  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.activity.compose)
  implementation(platform(libs.compose.bom))
  implementation(libs.compose.ui)
  implementation(libs.compose.ui.graphics)
  implementation(libs.compose.ui.tooling.preview)
  implementation(libs.compose.material3)
  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.junit.ktx)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(platform(libs.compose.bom))
  androidTestImplementation(libs.compose.ui.test.junit4)
  debugImplementation(libs.compose.ui.test.tooling)
  debugImplementation(libs.compose.ui.test.manifest)

  // auto implement all subproject that already loaded
  rootProject.childProjects.forEach {
    if (it.key == "app") return@forEach
    implementation(it.value)
    kover(it.value)
    println("### app module apply ${it.value}")
  }

  implementation(libs.timber)
}
