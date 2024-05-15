import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.BuiltArtifactsLoader
import com.android.build.gradle.AppPlugin
import org.gradle.configurationcache.extensions.capitalized

project.plugins.withType<AppPlugin> {
  val androidExtension = project.extensions.getByType<ApplicationAndroidComponentsExtension>()

  androidExtension.onVariants(androidExtension.selector().all()) { variant ->
    project.tasks.register<RenameApkTask>(
      "rename${variant.name.capitalized()}Apk",
    ) {
      val apkFolderProvider = variant.artifacts.get(SingleArtifact.APK)
      apkFolder.set(apkFolderProvider)
      buildArtifactsLoader.set(variant.artifacts.getBuiltArtifactsLoader())
      outApk.set(File(apkFolderProvider.get().asFile, "custom-name-${variant.name}.apk"))
    }
  }
}

abstract class RenameApkTask : DefaultTask() {

  @get:InputFiles
  abstract val apkFolder: DirectoryProperty

  @get:Internal
  abstract val buildArtifactsLoader: Property<BuiltArtifactsLoader>

  @get:OutputFile
  abstract val outApk: RegularFileProperty

  @TaskAction
  fun renameApk() {
    val builtArtifact = buildArtifactsLoader.get()
      .load(apkFolder.get()) ?: throw RuntimeException("Can not load apk")
    File(builtArtifact.elements.single().outputFile)
      .copyTo(outApk.get().asFile)
  }
}