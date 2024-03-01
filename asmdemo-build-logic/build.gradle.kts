plugins {
  `kotlin-dsl`
}

repositories {
  gradlePluginPortal()
  google()
  mavenCentral()
}

dependencies {
  implementation(libs.gradle.api)
  implementation(libs.asm.commons)
}

gradlePlugin {
  plugins {
    create("asmdemo") {
      id = "com.example.leonapplication.asmdemo.gradle-plugin"
      implementationClass = "com.example.leonapplicaion.build_logic.AsmDemoPlugin"
    }
  }
}