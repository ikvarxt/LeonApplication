import groovy.util.Node

plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.kotlin.android)
  `maven-publish`
}

android {
  namespace = "com.example.leonapplication.mavenpub"
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
  publishing {
    singleVariant("debug")
  }
}

dependencies {
  implementation(libs.androidx.core.ktx)
  implementation(project(":mavenpub:proj_deps"))
  implementation(project(":mavenpub:proj_localaar"))
}

val collectDependencies by tasks.register("collectDependencies") {
  doLast {
    try {
      val allDependencies = mutableListOf<Map<String, String>>()

      // Collect dependencies recursively
      fun collectSubprojectDependencies(project: Project) {
        project.configurations.getByName("implementation").dependencies.forEach { dep ->
          // Ignore project dependencies and duplicates
          if (dep !is ProjectDependency &&
            allDependencies.none { it["group"] == dep.group && it["name"] == dep.name }
          ) {
            if (dep is FileCollectionDependency) {
              println("collect deps: found file collection: \n${dep.files.joinToString("\n", prefix = "\t")}")
              return@forEach
            }

            println("collect deps: $dep")

            if (dep.name.isBlank() || dep.group.isNullOrBlank() || dep.version.isNullOrBlank()) {
              error("Invalid dependency: ${dep.name}")
            }

            allDependencies.add(
              mapOf(
                "group" to dep.group!!,
                "name" to dep.name,
                "version" to dep.version!!,
              )
            )
          }
        }
        // Recursively collect from subprojects
        project.subprojects.forEach { subProj ->
          collectSubprojectDependencies(subProj)
        }
      }

      collectSubprojectDependencies(project)

      // Store dependencies for later use
      project.extra["collectedDependencies"] = allDependencies
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }
}

val printPom by tasks.registering {
  doLast {
    // print build dir, publications, pom-default.xml to sout
    val pomFile = file("${buildDir}/publications/debug/pom-default.xml")
    if (pomFile.exists()) {
      println(pomFile.readText())
    } else {
      println("pom-default.xml not found")
    }
  }
}

tasks.withType<PublishToMavenRepository> {
  dependsOn(collectDependencies)
  finalizedBy(printPom)
}

publishing {
  publications.register<MavenPublication>("debug") {
    groupId = "com.leon.app"
    artifactId = "mavenpub"
    version = "0.0.1"
    afterEvaluate {
      from(components["debug"])
    }
    pom.withXml {
      val node: Node = asNode()
      var dependenciesNode = node.children()
        .filterIsInstance<Node>()
        .onEach { n ->
          println("nodeList: name=${n.name()} $n, ${n.javaClass}")
        }
        .firstOrNull { n -> n.name().toString().contains("dependencies") }

      println("deps node: $dependenciesNode")
      @Suppress("UNCHECKED_CAST")
      val allDeps = project.extra["collectedDependencies"] as List<Map<String, String>>?
        ?: error("deps is null")

      if (dependenciesNode == null) {
        dependenciesNode = node.appendNode("dependencies")
      } else {
        val children = dependenciesNode.children()
          .filterIsInstance<Node>()
        children.forEach { child -> dependenciesNode.remove(child) }
      }

      allDeps.forEach { dep ->
        dependenciesNode!!.appendNode("dependency").apply {
          appendNode("groupId", dep["group"])
          appendNode("artifactId", dep["name"])
          appendNode("version", dep["version"])
        }
      }
    }
  }
  repositories {
    maven { url = uri("${project.buildDir}/repo") }
  }
}