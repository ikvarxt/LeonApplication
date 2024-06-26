// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.kotlin.android) apply false
  alias(libs.plugins.android.library) apply false
  alias(libs.plugins.spotless) apply false
  alias(libs.plugins.kover) apply false
  alias(libs.plugins.ksp) apply false
  alias(libs.plugins.kotlin.jvm) apply false
  id("dev.iurysouza.modulegraph") version "0.9.0"
}

subprojects {
  apply<kotlinx.kover.gradle.plugin.KoverGradlePlugin>()

  apply<com.diffplug.gradle.spotless.SpotlessPlugin>()
  extensions.configure<com.diffplug.gradle.spotless.SpotlessExtension> {
    // optional: limit format enforcement to just the files changed by this feature branch
    ratchetFrom = "origin/main"

    format("misc") {
      // define the files to apply `misc` to
      target("*.gradle", ".gitattributes", ".gitignore")

      // define the steps to apply to those files
      trimTrailingWhitespace()
      indentWithSpaces(2) // or spaces. Takes an integer argument if you don't like 4
      endWithNewline()
    }

    // java {
    //   // don't need to set target, it is inferred from java

    //   // apply a specific flavor of google-java-format
    //   googleJavaFormat("1.20.0").aosp().reflowLongStrings().skipJavadocFormatting()
    //   // fix formatting of type annotations
    //   formatAnnotations()
    //   // make sure every file has the following copyright header.
    //   // optionally, Spotless can set copyright years by digging
    //   // through git history (see "license" section below)
    //   // licenseHeader( "/* (C)$YEAR */")
    // }

    kotlin {
      // by default the target is every '.kt' and '.kts` file in the java sourcesets
      target("**/*.kt")
      targetExclude("**/build/**/*.kt")
      ktlint(libs.versions.ktlint.get())
        .setEditorConfigPath(rootProject.file(".editorconfig").path)
        .editorConfigOverride(
          mapOf(
            "ktlint_code_style" to "android_studio",
            // "ij_kotlin_allow_trailing_comma" to true,
            // "insert_final_newline" to true,
            "ktlint_function_naming_ignore_when_annotated_with" to "Composable",
          ),
        )
        .customRuleSets(
          listOf(
            "io.nlopez.compose.rules:ktlint:0.3.11",
          ),
        )
      // licenseHeader("/* (C)$year */") // or licenseHeaderFile
    }
    kotlinGradle {
      // target("*.gradle.kts") // default target for kotlinGradle
      ktlint(libs.versions.ktlint.get())
        .editorConfigOverride(
          mapOf(
            // "ij_kotlin_allow_trailing_comma" to true,
          ),
        )
    }
  }
}

moduleGraphConfig {
  readmePath.set("./README.md")
  heading.set("# Module graph")
}