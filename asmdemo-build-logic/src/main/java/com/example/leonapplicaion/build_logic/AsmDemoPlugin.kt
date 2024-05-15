package com.example.leonapplicaion.build_logic

import com.android.build.api.instrumentation.FramesComputationMode
import com.android.build.api.instrumentation.InstrumentationScope
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.ApplicationVariant
import com.android.build.gradle.AppPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.withType

/**
 * only valid for android app module
 */
class AsmDemoPlugin : Plugin<Project> {

  override fun apply(target: Project) {
    target.logger.lifecycle("Asm apply plugin")

    target.plugins.withType<AppPlugin> {
      val appExtension = target.extensions.findByType<ApplicationAndroidComponentsExtension>()!!
      target.logger.lifecycle("Asm within AppPlugin, ext: $appExtension")

      appExtension.onVariants(appExtension.selector().all()) { variant: ApplicationVariant ->
        target.logger.lifecycle("Asm on variant $variant")
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