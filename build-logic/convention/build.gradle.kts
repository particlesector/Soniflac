plugins {
    `kotlin-dsl`
}

group = "com.particlesector.soniflac.buildlogic"

dependencies {
    compileOnly(libs.gradlePlugins.android)
    compileOnly(libs.gradlePlugins.kotlin)
    compileOnly(libs.gradlePlugins.ksp)
    compileOnly(libs.gradlePlugins.hilt)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "soniflac.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "soniflac.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidHilt") {
            id = "soniflac.android.hilt"
            implementationClass = "AndroidHiltConventionPlugin"
        }
        register("androidCompose") {
            id = "soniflac.android.compose"
            implementationClass = "AndroidComposeConventionPlugin"
        }
    }
}
