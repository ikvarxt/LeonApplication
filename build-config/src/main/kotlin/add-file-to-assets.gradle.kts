import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.gradle.AppPlugin
import org.gradle.configurationcache.extensions.capitalized

project.plugins.withType<AppPlugin> {
  val androidExtension = project.extensions.getByType<ApplicationAndroidComponentsExtension>()

  androidExtension.onVariants(androidExtension.selector().all()) { variant ->
    val addAssetsTask = project.tasks.register<AddAssetsTask>(
      "addAsset${variant.name.capitalized()}",
    ) {
      additionalAsset.set(rootProject.file("README.md"))
      // TODO: just add file to current variant assets folder
      outputDirectory.set(variant.sources.assets!!.all.get()[0].single())
    }
  }
}

abstract class AddAssetsTask : DefaultTask() {

  @get:InputFile
  abstract val additionalAsset: RegularFileProperty

  @get:OutputFiles
  abstract val outputDirectory: DirectoryProperty

  @TaskAction
  fun addAsset() {
    val asset = additionalAsset.get().asFile
    val assetDir = outputDirectory.get().asFile
    assetDir.mkdirs()
    asset.copyTo(File(assetDir, asset.name))
  }

}
