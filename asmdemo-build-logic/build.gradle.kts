plugins {
  `kotlin-dsl`
}

repositories {
  gradlePluginPortal()
  google()
  mavenCentral()
}

dependencies {
  implementation(libs.agp)
  implementation(libs.asm.commons)
}

gradlePlugin {
  plugins {
    create("asmdemo-build-logic") {
      id = "com.example.leonapplication.asmdemo.gradle-plugin"
      implementationClass = "com.example.leonapplicaion.build_logic.AsmDemoPlugin"
    }
  }
}