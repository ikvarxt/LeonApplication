import Build_gradle.Dep
import groovy.util.Node
import groovy.util.NodeList
import groovy.xml.XmlParser

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
  implementation(project(":mavenpub:proj_transaar"))
}

typealias Dep = Map<String, String>

fun Node.t(name: String): String =
  (get(name) as NodeList).text().toString()

fun getLocalMavenPomDependencies(project: Project, file: File): List<Dep> {
  val list = mutableListOf<Dep>()
  project.logger.warn("getLocalMavenPomDependencies: path $file")

  try {
    val xml = XmlParser().parse(file)
    val dependenciesNode = xml.children()
      .filterIsInstance<Node>()
      .find { it.name().toString().contains("dependencies") }

    if (dependenciesNode == null) {
      project.logger.warn("getLocalMavenPomDependencies no dependencies")
      return list
    }

    val dependencies = dependenciesNode.children()
      .filterIsInstance<Node>()

    val deps = dependencies
      .map { node ->
        mapOf(
          "group" to node.t("groupId"),
          "name" to node.t("artifactId"),
          "version" to node.t("version"),
          "scope" to node.t("scope"),
        )
      }
    list.addAll(deps)
    project.logger.warn("getLocalMavenPomDependencies dependencies: ${dependencies.joinToString("\n")}")
  } catch (e: Exception) {
    project.logger.error("getLocalMavenPomDependencies error:", e)
  }

  return list
}

val collectDependencies by tasks.register("collectDependencies") {
  doLast {
    val allDependencies = mutableListOf<Map<String, String>>()

    // Collect dependencies recursively
    fun collectSubprojectDependencies(project: Project) {
      project.configurations.getByName("implementation").dependencies.forEach { dep ->

        if (dep is FileCollectionDependency) {
          println("collect deps: found file collection: \n${dep.files.joinToString("\n", prefix = "\t")}")
          return@forEach
        }

        if (dep is ExternalModuleDependency && dep.group == "androidx.appcompat") {
          println("collect deps: found external module: $dep, ${dep.javaClass}")
          // if found some special dependency, we put our predefined pom dependencies to it
          val localPomDependencies = getLocalMavenPomDependencies(project, file("react-native-0.66.5.pom"))
          allDependencies.addAll(localPomDependencies)
          return@forEach
        }

        // Ignore project dependencies and duplicates
        if (dep !is ProjectDependency &&
          allDependencies.none { it["group"] == dep.group && it["name"] == dep.name }
        ) {

          println("collect deps: $dep")

          if (dep.name.isBlank() || dep.group.isNullOrBlank() || dep.version.isNullOrBlank()) {
            error("Invalid dependency: $dep")
          }

          allDependencies.add(
            mapOf(
              "group" to dep.group!!,
              "name" to dep.name,
              "version" to dep.version!!,
              "scope" to "compile",
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
          appendNode("scope", dep["scope"])
        }
      }
    }
  }
  repositories {
    maven { url = uri("${project.buildDir}/repo") }
  }
}