import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("soniflac.android.library")
                apply("soniflac.android.library.compose")
                apply("soniflac.android.hilt")
            }

            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
            dependencies {
                "implementation"(project(":core:model"))
                "implementation"(project(":core:common"))

                "implementation"(libs.findLibrary("hilt-navigation-compose").get())
                "implementation"(libs.findLibrary("lifecycle-runtime-compose").get())
                "implementation"(libs.findLibrary("lifecycle-viewmodel-compose").get())
                "implementation"(libs.findLibrary("compose-navigation").get())
            }
        }
    }
}
