import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.ApplicationVariant
import com.android.build.gradle.AppPlugin
import org.gradle.configurationcache.extensions.capitalized

operator fun String.invoke() = logger.lifecycle("Variant-aware-task: $this")

project.plugins.withType<AppPlugin> {
  val appPlugin = this
  val android = project.extensions.findByType<ApplicationAndroidComponentsExtension>()!!

//  android.onVariants(android.selector().withFlavor("surfaceView" to "viewType")) {
//    "with variant surfaceView"()
//  }
  android.onVariants(android.selector().all()) { variant: ApplicationVariant ->
    val capitalizeName = variant.name.capitalized()
    "======================== start one variant"()
    "variant name: $variant"()
    "application id: ${variant.applicationId.get()}"()
    "min sdk ver: ${variant.minSdk}"()
    "product flavor ${variant.productFlavors.size}"()

    val beforeAssemble = tasks.register(
      "before${capitalizeName}Assemble"
    ) {
      doFirst {
        "${this.name} is running"()
      }
    }

    // TODO: depend on assembleProvider
  }
}

