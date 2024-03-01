package com.example.leonapplicaion.build_logic

import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class AsmDemoPlugin : Plugin<Project> {

  override fun apply(target: Project) {

    println("apply AsmDemoPlugin")
    target.plugins.withId("com.android.application") {
      target.extensions.configure(ApplicationAndroidComponentsExtension::class.java) {
        this.onVariants(selector().all()) { variant ->
          variant.instrumentation.transformClassesWith(
            AsmDemoClassVisitorFactory::class.java,
            InstrumentationScope.ALL
          ) {}
          variant.instrumentation.setAsmFramesComputationMode(
            FramesComputationMode.COMPUTE_FRAMES_FOR_INSTRUMENTED_METHODS
          )
        }
      }
    }
  }
}